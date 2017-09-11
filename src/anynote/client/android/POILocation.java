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
//忠孝敦化 121.551098 25.041478
//忠孝復興121.544833 25.041593
//北車 121.517081 25.047924
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
    poiArray.add(new OverlayItem(cPoint,"現在位置","這是目前位置"));
    populate();
  }
  
  public POILocation(Drawable defaultMarker)
  {
	super(defaultMarker);
	marker=defaultMarker;
  }
  
  public void setPoint(GeoPoint gp)
  {
	poiArray.add(new OverlayItem(gp,"提醒位置","這是提醒位置"));
	populate();
  }
  
  public void setContext(Context context){
    m_context=context;
  }
  
  @Override
  protected OverlayItem createItem(int arg0)//populate()時呼叫
  {
    return poiArray.get(arg0);
  }
  
  @Override
  public void draw(Canvas canvas,MapView mapView,boolean shadow){//標記地圖
    super.draw(canvas, mapView, shadow);
    boundCenterBottom(marker);
  }

  @Override
  public int size()
  {
    return poiArray.size();
  }
  
  @Override
  protected boolean onTap(int index) {//點擊之後產生對話框
    OverlayItem item = poiArray.get(index);
    AlertDialog.Builder dialog = new AlertDialog.Builder(m_context);
    dialog.setTitle(item.getTitle());
    dialog.setMessage(item.getSnippet());
    dialog.setPositiveButton("關閉",new DialogInterface.OnClickListener()
    {
		public void onClick(DialogInterface dialog, int which) {}
	});
    dialog.show();
    return true;
  }
}
