package anynote.client.android;

import java.util.ArrayList;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
//�������� 121.551098 25.041478
//�����_��121.544833 25.041593
//�_�� 121.517081 25.047924
//hotel 121.516396 25.046177
//http://airuo.no-ip.biz/blog/?p=204
public class POILocation extends ItemizedOverlay<OverlayItem>
{
  
  private ArrayList<OverlayItem> poiArray=new ArrayList<OverlayItem>();
  private Drawable marker;
  private Context m_context;
  public POILocation(Drawable defaultMarker,GeoPoint cPoint)//constructor
  {
    super(defaultMarker);
    marker=defaultMarker;
    poiArray.add(new OverlayItem(cPoint,"�{�b��m","�o�O�ثe��m"));
    populate();
  }
  
  public POILocation(Drawable defaultMarker)
  {
	super(defaultMarker);
	marker=defaultMarker;
  }
  
  public void setPoint(GeoPoint gp)
  {
	poiArray.add(new OverlayItem(gp,"������m","�o�O������m"));
	populate();
  }
  
  public void setContext(Context context){
    m_context=context;
  }
  
  @Override
  protected OverlayItem createItem(int arg0)//populate()�ɩI�s
  {
    return poiArray.get(arg0);
  }
  
  @Override
  public void draw(Canvas canvas,MapView mapView,boolean shadow){//�аO�a��
    super.draw(canvas, mapView, shadow);
    boundCenterBottom(marker);
  }

  @Override
  public int size()
  {
    return poiArray.size();
  }
  
  @Override
  protected boolean onTap(int index) {//�I�����Უ�͹�ܮ�
    OverlayItem item = poiArray.get(index);
    AlertDialog.Builder dialog = new AlertDialog.Builder(m_context);
    dialog.setTitle(item.getTitle());
    dialog.setMessage(item.getSnippet());
    dialog.setPositiveButton("����",new DialogInterface.OnClickListener()
    {
		public void onClick(DialogInterface dialog, int which) {}
	});
    dialog.show();
    return true;
  }
}
