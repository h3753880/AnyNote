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

	private int intZoomLevel = 20;// default�Y��j�p
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
		
		/* ���o�U����View */
		okButton = (Button) findViewById(R.id.buttonOK);
		myMapView = (MapView) findViewById(R.id.myMapView);
		/*�]�w�Y����s*/
		myMapView.setBuiltInZoomControls(true);
		refreshMapViewByGeoPoint(destPoint, myMapView, intZoomLevel, true);
		refreshItem(destPoint);// ��s�Ϥ��Х��I
		
		/*�T�w���s���U�����ƥ�*/
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showRemindPoint.this.finish();
			}
		});
		
	}
	
	public void refreshItem(GeoPoint gp) {//��s��e��m�ϼh
		Drawable star = getResources().getDrawable(R.drawable.star);
		POILocation poi = null;
		poi = new POILocation(star);
		poi.setPoint(gp);
		poi.setContext(this);
		myMapView.getOverlays().add(poi);// �a�Ϥ��[�J���I�ϼh
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
	
	@Override
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}

}