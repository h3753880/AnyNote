package anynote.client.android;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;

public class POINewLocation extends ItemizedOverlay<OverlayItem> {
	private ArrayList<OverlayItem> poiArray = new ArrayList<OverlayItem>();
	private Drawable marker;
	private Context m_context;
	private ToDoDB myToDoDB;
	private String userId="";
	private String place="";
	private GeoPoint touchGp;

	public POINewLocation(Drawable defaultMarker, GeoPoint cPoint)// constructor
	{
		super(defaultMarker);
		marker = defaultMarker;
		poiArray.add(new OverlayItem(cPoint, "現在位置", "這是目前位置"));
		populate();
	}

	public POINewLocation(Drawable defaultMarker) {
		super(defaultMarker);
		marker = defaultMarker;
	}

	public void setPoint(GeoPoint gp) {
		touchGp=gp;
		poiArray.add(new OverlayItem(gp, "提醒位置", "輸入地點名稱"));
		populate();
	}

	public void setContext(Context context) {
		m_context = context;
	}

	@Override
	protected OverlayItem createItem(int arg0)// populate()時呼叫
	{
		return poiArray.get(arg0);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {// 標記地圖
		super.draw(canvas, mapView, shadow);
		boundCenterBottom(marker);
	}

	@Override
	public int size() {
		return poiArray.size();
	}

	public String getFavPlace()
	{
		return place;
	}
	
	@Override
	protected boolean onTap(int index) {// 點擊之後產生對話框
		LayoutInflater inflater = LayoutInflater.from(m_context);
		final View view = inflater.inflate(R.layout.geo_pop_up, null);//套用layout
		/* 取得DataBase裡的資料 */ 
		myToDoDB = new ToDoDB(m_context);
		
		OverlayItem item = poiArray.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(m_context);
		dialog.setView(view);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		//記錄此地點 存入手機DB
		dialog.setPositiveButton("記錄喜愛點", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			    /*取得FB userId*/
				if(AnyNoteActivity.facebook.isSessionValid())
			    	userId=AnyNoteActivity.fbId;
		        else 
		        	userId="0";
				/*取得喜愛點名稱*/
				EditText editText = (EditText) (view.findViewById(R.id.edittext));
				place=editText.getText().toString();
				
				/*喜愛點存入手機DB*/
				if(place.equals(""))
					Toast.makeText(m_context,"尚未輸入喜愛點", Toast.LENGTH_SHORT).show();
				else
				{
					myToDoDB.insertFavPlace(userId, place, touchGp);
				}
			}
		});
		//不記錄此地點
		dialog.setNegativeButton("關閉", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		dialog.show();
		return true;
	}
}
