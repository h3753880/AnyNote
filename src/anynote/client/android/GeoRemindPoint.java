package anynote.client.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.gesture.GestureOverlayView;
import android.gesture.GestureOverlayView.OnGestureListener;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import anynote.client.classes.FavPlace;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class GeoRemindPoint extends MapActivity  {
	private int intZoomLevel = 20;// default縮放大小
	private GeoPoint touchGeoPoint;//點擊地圖位置
	private GeoPoint currentGeoPoint;//現在位置
	private Location mLocation01 = null;
	private String strLocationPrivider = "";
	private LocationManager mLocationManager01;
	private MapView myMapView;
	private Button cancelButton;
	private Button okButton;
	private Button favButton;
	private ToDoDB myToDoDB;
	private String[] place;//喜愛點
	private String recordPlace="";//選擇已記錄的喜愛點
	private String[] longitude;//喜愛點經度
	private String[] latitude;//喜愛點緯度
	private POINewLocation poi;
	private boolean newOrOld=true;//new->true old->false 判斷選擇已記錄的喜愛點或新增
	
	/*取得手機DB中的喜愛點*/
	public void getDBFavData()
	{
		myToDoDB = new ToDoDB(this);
		ArrayList<FavPlace> temp=myToDoDB.selectFavPlace();
		if(temp!=null)
		{
			place=new String[temp.size()];
			longitude=new String[temp.size()];
			latitude=new String[temp.size()];
			for(int i=0;i<temp.size();i++)
			{
				place[i]=temp.get(i).getPlace();
				longitude[i]=temp.get(i).getLong();
				latitude[i]=temp.get(i).getLat();
			}
		}
		else
		{
			place=new String[1];
			longitude=new String[1];
			latitude=new String[1];
			place[0]="尚未設定喜愛點";
		}
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geo_remind_point);
		
		/* 取得各元件的View */
		okButton = (Button) findViewById(R.id.buttonOK);
		cancelButton = (Button) findViewById(R.id.buttonCancel);
		myMapView = (MapView) findViewById(R.id.myMapView);
		favButton = (Button) findViewById(R.id.buttonFav);
		

		/*取得手機DB中的喜愛點*/
		getDBFavData();

		/* 建立LocationManager物件取得系統LOCATION服務 */
		mLocationManager01 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		/* 第一次執行向Location Provider取得Location */
		mLocation01 = getLocationPrivider(mLocationManager01);
		
		if(mLocation01!=null)
	    {
	        processLocationUpdated(mLocation01);//第一次執行更新
	    }
	    else
	    {
	    	Toast.makeText(this, "無法取得當前位置", Toast.LENGTH_SHORT).show();
	    }
	
		/* 建立LocationManager物件，監聽Location變更時事件，更新MapView */// ms m
		mLocationManager01.requestLocationUpdates(strLocationPrivider, 8000,
			50, mLocationListener01);
		
		/*點地圖取得提醒點事件*/
		myMapView.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				if(event.getAction()==MotionEvent.ACTION_UP&&event.getEventTime()-event.getDownTime()>1000)
				{
					touchGeoPoint=myMapView.getProjection().fromPixels((int)event.getX(), (int)event.getY());
					Toast.makeText(GeoRemindPoint.this,
							"設定提醒點:\n"+"經度:"+Double.toString(touchGeoPoint.getLongitudeE6()/1E6)+
							"\n緯度:"+Double.toString(touchGeoPoint.getLatitudeE6()/1E6), 
							Toast.LENGTH_SHORT).show();
				
					Drawable star = getResources().getDrawable(R.drawable.star);
					poi = new POINewLocation(star);
					poi.setPoint(touchGeoPoint);
					poi.setContext(GeoRemindPoint.this);
					if(myMapView.getOverlays().size()==2)
						myMapView.getOverlays().remove(1);// 清除上一步驟的圖層
					myMapView.getOverlays().add(poi);// 地圖中加入景點圖層
					newOrOld=true;//新增喜愛點
				}
				
				return false;
			}
		});
			
		/*確定按鈕按下反應事件*/
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(touchGeoPoint!=null)
				{
					
					Intent intent =  new  Intent();   
					Bundle bundle=new Bundle();
					
					if(newOrOld)//新增喜愛點
						bundle.putString("favPlace", poi.getFavPlace());
					else//選擇舊的喜愛點
						bundle.putString("favPlace", recordPlace);
						
					bundle.putDouble("remindPointLo",(double)touchGeoPoint.getLongitudeE6()/1E6);
					bundle.putDouble("remindPointLa",(double)touchGeoPoint.getLatitudeE6()/1E6);
						
					intent.putExtras(bundle);
					GeoRemindPoint.this.setResult(1,intent);//傳送點擊位置回去
					GeoRemindPoint.this.finish();
				}
				else
					Toast.makeText(GeoRemindPoint.this, "請長按地圖選擇位置" , Toast.LENGTH_SHORT).show();
				
			}
		});
		
		/*取消按鈕按下反應事件 */
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				GeoRemindPoint.this.finish();
			}
		});
		
		/*選擇喜愛點按鈕反應事件*/
		favButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(GeoRemindPoint.this);
				dialog.setItems(place, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(GeoRemindPoint.this, "經度:"+longitude[which]+"緯度:"+latitude[which], Toast.LENGTH_SHORT).show();
						recordPlace=place[which];//記錄count
						newOrOld=false;//選擇舊的喜愛點
						touchGeoPoint=new GeoPoint((int)(Double.parseDouble(latitude[which]) *1E6)
								,(int)( Double.parseDouble(longitude[which]) *1E6));
						Drawable star = getResources().getDrawable(R.drawable.star);
						POILocation poi = new POILocation(star);
						poi.setPoint(touchGeoPoint);
						poi.setContext(GeoRemindPoint.this);
						if(myMapView.getOverlays().size()==2)
							myMapView.getOverlays().remove(1);// 清除上一步驟的圖層
						myMapView.getOverlays().add(poi);// 地圖中加入景點圖層
					}
				});
				dialog.show();
			}
		});
	}

	private final LocationListener mLocationListener01 = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub

			/* 當手機收到位置變更時，將location傳入取得地理座標 */
			processLocationUpdated(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			/* 當Provider已離開服務範圍時 */
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	};

	/* 當手機收到位置變更時，將location傳入更新當下GeoPoint及MapView */// locationChanges
	private void processLocationUpdated(Location location) {
		/* 傳入Location物件，取得GeoPoint地理座標 */
		currentGeoPoint = getGeoByLocation(location);
		// ZoomLevel=20預設.更新MapView顯示GoogleMap
		refreshMapViewByGeoPoint(currentGeoPoint, myMapView, intZoomLevel, true);
		refreshItem(currentGeoPoint);// 更新圖片標示點
	}

	public void refreshItem(GeoPoint gp) {//更新當前位置圖層
		Drawable man = getResources().getDrawable(R.drawable.man);
		POILocation poi = null;
		poi = new POILocation(man, gp);
		poi.setContext(this);
		
		if(myMapView.getOverlays().size()>0)
			myMapView.getOverlays().remove(0);// 清除上一步驟的圖層
		myMapView.getOverlays().add(0,poi);// 地圖中加入景點圖層
	}

	public void refreshMapViewByGeoPoint(GeoPoint gp, MapView mv,
			int zoomLevel, boolean bIfSatellite) {
		try {
			MapController mc = mv.getController();
			mc.animateTo(gp);/* 移至該地理座標位址 */
			mc.setZoom(zoomLevel);/* 放大地圖層級 */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private GeoPoint getGeoByLocation(Location location) {
		GeoPoint gp = null;
		try {
			/* 當Location存在 */
			if (location != null) {
				double geoLatitude = location.getLatitude() * 1E6;
				double geoLongitude = location.getLongitude() * 1E6;
				gp = new GeoPoint((int) geoLatitude, (int) geoLongitude);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gp;
	}

	public Location getLocationPrivider(LocationManager lm)
	  {
	    Location retLocation = null;
	    try
	    {
	      Criteria mCriteria01 = new Criteria();
	      mCriteria01.setAccuracy(Criteria.ACCURACY_FINE);
	      mCriteria01.setAltitudeRequired(false);
	      mCriteria01.setBearingRequired(false);
	      mCriteria01.setCostAllowed(true);
	      mCriteria01.setPowerRequirement(Criteria.POWER_LOW);
	      strLocationPrivider = lm.getBestProvider(mCriteria01, true);
	      retLocation = lm.getLastKnownLocation(strLocationPrivider);
	    }
	    catch(Exception e)
	    {
	      e.printStackTrace();
	    }
	    return retLocation;
	  }

	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}
