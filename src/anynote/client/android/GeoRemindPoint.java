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
	private int intZoomLevel = 20;// default�Y��j�p
	private GeoPoint touchGeoPoint;//�I���a�Ϧ�m
	private GeoPoint currentGeoPoint;//�{�b��m
	private Location mLocation01 = null;
	private String strLocationPrivider = "";
	private LocationManager mLocationManager01;
	private MapView myMapView;
	private Button cancelButton;
	private Button okButton;
	private Button favButton;
	private ToDoDB myToDoDB;
	private String[] place;//�߷R�I
	private String recordPlace="";//��ܤw�O�����߷R�I
	private String[] longitude;//�߷R�I�g��
	private String[] latitude;//�߷R�I�n��
	private POINewLocation poi;
	private boolean newOrOld=true;//new->true old->false �P�_��ܤw�O�����߷R�I�ηs�W
	
	/*���o���DB�����߷R�I*/
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
			place[0]="�|���]�w�߷R�I";
		}
	}
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geo_remind_point);
		
		/* ���o�U����View */
		okButton = (Button) findViewById(R.id.buttonOK);
		cancelButton = (Button) findViewById(R.id.buttonCancel);
		myMapView = (MapView) findViewById(R.id.myMapView);
		favButton = (Button) findViewById(R.id.buttonFav);
		

		/*���o���DB�����߷R�I*/
		getDBFavData();

		/* �إ�LocationManager������o�t��LOCATION�A�� */
		mLocationManager01 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		/* �Ĥ@������VLocation Provider���oLocation */
		mLocation01 = getLocationPrivider(mLocationManager01);
		
		if(mLocation01!=null)
	    {
	        processLocationUpdated(mLocation01);//�Ĥ@�������s
	    }
	    else
	    {
	    	Toast.makeText(this, "�L�k���o��e��m", Toast.LENGTH_SHORT).show();
	    }
	
		/* �إ�LocationManager����A��ťLocation�ܧ�ɨƥ�A��sMapView */// ms m
		mLocationManager01.requestLocationUpdates(strLocationPrivider, 8000,
			50, mLocationListener01);
		
		/*�I�a�Ϩ��o�����I�ƥ�*/
		myMapView.setOnTouchListener(new View.OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				
				if(event.getAction()==MotionEvent.ACTION_UP&&event.getEventTime()-event.getDownTime()>1000)
				{
					touchGeoPoint=myMapView.getProjection().fromPixels((int)event.getX(), (int)event.getY());
					Toast.makeText(GeoRemindPoint.this,
							"�]�w�����I:\n"+"�g��:"+Double.toString(touchGeoPoint.getLongitudeE6()/1E6)+
							"\n�n��:"+Double.toString(touchGeoPoint.getLatitudeE6()/1E6), 
							Toast.LENGTH_SHORT).show();
				
					Drawable star = getResources().getDrawable(R.drawable.star);
					poi = new POINewLocation(star);
					poi.setPoint(touchGeoPoint);
					poi.setContext(GeoRemindPoint.this);
					if(myMapView.getOverlays().size()==2)
						myMapView.getOverlays().remove(1);// �M���W�@�B�J���ϼh
					myMapView.getOverlays().add(poi);// �a�Ϥ��[�J���I�ϼh
					newOrOld=true;//�s�W�߷R�I
				}
				
				return false;
			}
		});
			
		/*�T�w���s���U�����ƥ�*/
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if(touchGeoPoint!=null)
				{
					
					Intent intent =  new  Intent();   
					Bundle bundle=new Bundle();
					
					if(newOrOld)//�s�W�߷R�I
						bundle.putString("favPlace", poi.getFavPlace());
					else//����ª��߷R�I
						bundle.putString("favPlace", recordPlace);
						
					bundle.putDouble("remindPointLo",(double)touchGeoPoint.getLongitudeE6()/1E6);
					bundle.putDouble("remindPointLa",(double)touchGeoPoint.getLatitudeE6()/1E6);
						
					intent.putExtras(bundle);
					GeoRemindPoint.this.setResult(1,intent);//�ǰe�I����m�^�h
					GeoRemindPoint.this.finish();
				}
				else
					Toast.makeText(GeoRemindPoint.this, "�Ъ����a�Ͽ�ܦ�m" , Toast.LENGTH_SHORT).show();
				
			}
		});
		
		/*�������s���U�����ƥ� */
		cancelButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				GeoRemindPoint.this.finish();
			}
		});
		
		/*��ܳ߷R�I���s�����ƥ�*/
		favButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
				AlertDialog.Builder dialog = new AlertDialog.Builder(GeoRemindPoint.this);
				dialog.setItems(place, new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Toast.makeText(GeoRemindPoint.this, "�g��:"+longitude[which]+"�n��:"+latitude[which], Toast.LENGTH_SHORT).show();
						recordPlace=place[which];//�O��count
						newOrOld=false;//����ª��߷R�I
						touchGeoPoint=new GeoPoint((int)(Double.parseDouble(latitude[which]) *1E6)
								,(int)( Double.parseDouble(longitude[which]) *1E6));
						Drawable star = getResources().getDrawable(R.drawable.star);
						POILocation poi = new POILocation(star);
						poi.setPoint(touchGeoPoint);
						poi.setContext(GeoRemindPoint.this);
						if(myMapView.getOverlays().size()==2)
							myMapView.getOverlays().remove(1);// �M���W�@�B�J���ϼh
						myMapView.getOverlays().add(poi);// �a�Ϥ��[�J���I�ϼh
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

			/* ���������m�ܧ�ɡA�Nlocation�ǤJ���o�a�z�y�� */
			processLocationUpdated(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			/* ��Provider�w���}�A�Ƚd��� */
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

	/* ���������m�ܧ�ɡA�Nlocation�ǤJ��s��UGeoPoint��MapView */// locationChanges
	private void processLocationUpdated(Location location) {
		/* �ǤJLocation����A���oGeoPoint�a�z�y�� */
		currentGeoPoint = getGeoByLocation(location);
		// ZoomLevel=20�w�].��sMapView���GoogleMap
		refreshMapViewByGeoPoint(currentGeoPoint, myMapView, intZoomLevel, true);
		refreshItem(currentGeoPoint);// ��s�Ϥ��Х��I
	}

	public void refreshItem(GeoPoint gp) {//��s��e��m�ϼh
		Drawable man = getResources().getDrawable(R.drawable.man);
		POILocation poi = null;
		poi = new POILocation(man, gp);
		poi.setContext(this);
		
		if(myMapView.getOverlays().size()>0)
			myMapView.getOverlays().remove(0);// �M���W�@�B�J���ϼh
		myMapView.getOverlays().add(0,poi);// �a�Ϥ��[�J���I�ϼh
	}

	public void refreshMapViewByGeoPoint(GeoPoint gp, MapView mv,
			int zoomLevel, boolean bIfSatellite) {
		try {
			MapController mc = mv.getController();
			mc.animateTo(gp);/* ���ܸӦa�z�y�Ц�} */
			mc.setZoom(zoomLevel);/* ��j�a�ϼh�� */
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private GeoPoint getGeoByLocation(Location location) {
		GeoPoint gp = null;
		try {
			/* ��Location�s�b */
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
