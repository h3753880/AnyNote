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
		poiArray.add(new OverlayItem(cPoint, "�{�b��m", "�o�O�ثe��m"));
		populate();
	}

	public POINewLocation(Drawable defaultMarker) {
		super(defaultMarker);
		marker = defaultMarker;
	}

	public void setPoint(GeoPoint gp) {
		touchGp=gp;
		poiArray.add(new OverlayItem(gp, "������m", "��J�a�I�W��"));
		populate();
	}

	public void setContext(Context context) {
		m_context = context;
	}

	@Override
	protected OverlayItem createItem(int arg0)// populate()�ɩI�s
	{
		return poiArray.get(arg0);
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {// �аO�a��
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
	protected boolean onTap(int index) {// �I�����Უ�͹�ܮ�
		LayoutInflater inflater = LayoutInflater.from(m_context);
		final View view = inflater.inflate(R.layout.geo_pop_up, null);//�M��layout
		/* ���oDataBase�̪���� */ 
		myToDoDB = new ToDoDB(m_context);
		
		OverlayItem item = poiArray.get(index);
		AlertDialog.Builder dialog = new AlertDialog.Builder(m_context);
		dialog.setView(view);
		dialog.setTitle(item.getTitle());
		dialog.setMessage(item.getSnippet());
		//�O�����a�I �s�J���DB
		dialog.setPositiveButton("�O���߷R�I", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			    /*���oFB userId*/
				if(AnyNoteActivity.facebook.isSessionValid())
			    	userId=AnyNoteActivity.fbId;
		        else 
		        	userId="0";
				/*���o�߷R�I�W��*/
				EditText editText = (EditText) (view.findViewById(R.id.edittext));
				place=editText.getText().toString();
				
				/*�߷R�I�s�J���DB*/
				if(place.equals(""))
					Toast.makeText(m_context,"�|����J�߷R�I", Toast.LENGTH_SHORT).show();
				else
				{
					myToDoDB.insertFavPlace(userId, place, touchGp);
				}
			}
		});
		//���O�����a�I
		dialog.setNegativeButton("����", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
			}
		});
		dialog.show();
		return true;
	}
}
