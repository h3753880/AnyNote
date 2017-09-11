package anynote.client.android;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;

public class showRemindPoint extends MapActivity {

	private int intZoomLevel = 20;// default縮放大小
	private GeoPoint destPoint;
	private MapView myMapView;
	private Button okButton;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_point);
		
		Bundle bundle = this.getIntent().getExtras();
		double lo=bundle.getDouble("longitude");
		double la=bundle.getDouble("latitude");
		destPoint=new GeoPoint( (int) (la*1E6), (int) (lo*1E6) );
		
		/* 取得各元件的View */
		okButton = (Button) findViewById(R.id.buttonOK);
		myMapView = (MapView) findViewById(R.id.myMapView);
		/*設定縮放按鈕*/
		myMapView.setBuiltInZoomControls(true);
		refreshMapViewByGeoPoint(destPoint, myMapView, intZoomLevel, true);
		refreshItem(destPoint);// 更新圖片標示點
		
		/*確定按鈕按下反應事件*/
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showRemindPoint.this.finish();
			}
		});
		
	}
	
	public void refreshItem(GeoPoint gp) {//更新當前位置圖層
		Drawable star = getResources().getDrawable(R.drawable.star);
		POILocation poi = null;
		poi = new POILocation(star);
		poi.setPoint(gp);
		poi.setContext(this);
		myMapView.getOverlays().add(poi);// 地圖中加入景點圖層
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
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}