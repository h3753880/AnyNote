package anynote.client.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;


import android.app.Activity;
import android.app.AlarmManager;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;

import android.view.MotionEvent;
import android.view.View;
import android.view.GestureDetector.OnGestureListener;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.widget.SimpleAdapter;
import android.widget.AbsListView.OnScrollListener;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.PopupWindow;

import android.widget.SimpleCursorAdapter;
import anynote.client.android.Alarm.CallAlarm;
import anynote.client.android.Alarm.GeoDetect;
import anynote.client.android.Friend_list.MyViewBinder;
import anynote.client.classes.SearchNote;




public class BrowseActivity extends ListActivity implements OnTouchListener, OnGestureListener{
	 private ToDoDB myToDoDB;
	  static Cursor myCursor;
	  static List<Map<String, Object>>  list;
	  static ListView myListView;
	  private int _id;
	  protected final static int MENU_EDIT = Menu.FIRST + 1;
	  protected final static int MENU_DELETE = Menu.FIRST + 2;
	  private PopupWindow pw;
	  private int pos;
	  private int viewType=0;
	  private GestureDetector gestureDetector;
	  private int verticalMinDistance = 120;  
	  private int minVelocity         = 0;  
	  private boolean isAll;
	  private SearchNote note;
	  private TextView title;
	 
	  /*bundle.putString("title", note.title);
            	bundle.putString("content", note.content);
            	bundle.putString("friends", note.friends);
            	bundle.putString("time", note.time);
            	bundle.putString("upOrDown", note.upOrDown);
            	intent.putExtras(bundle);
            	*/

	
	  
	 @Override
	  public void onCreate(Bundle savedInstanceState)
	  {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.browse);
	    Toast.makeText(BrowseActivity.this, "左右滑動以切換顯示介面", Toast.LENGTH_LONG).show();
	    myToDoDB = new ToDoDB(this);
	    gestureDetector = new GestureDetector((OnGestureListener) this);  
	    Bundle text=this.getIntent().getExtras();
	    title=(TextView)findViewById(R.id.browsetitle);
	    isAll=text.getBoolean("isAll");
	    title.setText("時間提醒");
	    //public SearchNote(String title,String content,String friends,String time,String upOrDown)
	    if(!isAll)note=new SearchNote(text.getString("title"),
	    		text.getString("content"),
	    		text.getString("friends"),
	    		text.getString("time"),
	    		text.getString("upOrDown"));
	    
        myListView = getListView();
        System.out.println("getListView()");
     
        SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
		        new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
    
        setListAdapter(adapter);
	    
        LinearLayout viewSnsLayout = (LinearLayout)findViewById(R.id.viewSnsLayout);    
        viewSnsLayout.setOnTouchListener(this);    
        viewSnsLayout.setLongClickable(true); 
	    
	    myListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
		        //list.get(arg2).get("_id");
				pos=arg2;
		        _id = (Integer) list.get(arg2).get("_id");
		        
				//deleteTodo();
				return false;
			}
	    });
	    
	    myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() 
	    {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				_id = (Integer) list.get(arg2).get("_id");
				pos=arg2;
				System.out.print(_id);
				 showPopupWindow(BrowseActivity.this,arg1);
				
				
			}
		});

		  
	  }
	
	 public void showPopupWindow(Context context,View parent){
	        LayoutInflater inflater = (LayoutInflater)   
	           context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
	        final View vPopupWindow=inflater.inflate(R.layout.popupwindow, null, false);
	       
	        pw= new PopupWindow(vPopupWindow,300,100,true);
	        pw.setFocusable(true);
	     
	        pw.setBackgroundDrawable(new BitmapDrawable());
	        Button buttonDelete=(Button)vPopupWindow.findViewById(R.id.buttonDelete);
	        buttonDelete.setOnClickListener(new OnClickListener(){
	            @Override
	            public void onClick(View v) {
	               
	            	//设置文本框内容
	            	if(viewType==0)
	            	deleteTodo();
	            	else
	            	{
	            		Intent intent=new Intent();
	            		Bundle bundle=new Bundle();
	    				bundle.putInt("status", 3);//刪除GeoDetect中的arraylist(3刪除)
	    				//bundle.putString("userId", list.get(pos).get("userId").toString());
	    				//bundle.putInt("noteId", Integer.parseInt(list.get(pos).get("noteId").toString()));
	    				bundle.putInt("ID", _id);
	    				intent.putExtras(bundle);
	    				intent.setClass(BrowseActivity.this, GeoDetect.class);
	    				startService(intent);
	    				deleteGeoTodo();//刪除手機和ServerDB地理提醒
	            	}
	            	pw.dismiss();
	            }
	        });
	        Button buttonEdit=(Button)vPopupWindow.findViewById(R.id.buttonEdit);
	        buttonEdit.setOnClickListener(new OnClickListener(){
	            @Override
	            public void onClick(View v) 
	            	{
	                //设置文本框内容
	            	
	            	if(!list.get(pos).get("userId").toString().equals(AnyNoteActivity.fbId)&&!list.get(pos).get("userId").toString().equals("0"))
	            		Toast.makeText(BrowseActivity.this, "無法修改朋友之提醒",
								Toast.LENGTH_SHORT).show();
	            	else{
	            	if(viewType==0)editTodo();
	            	else editGeoTodo();
	            	}
	            	pw.dismiss();
	            }
	        });
	        Button buttonShow=(Button)vPopupWindow.findViewById(R.id.buttonShow);
	        buttonShow.setOnClickListener(new OnClickListener(){
	            @Override
	            public void onClick(View v) 
	            	{
	                //设置文本框内容
	            	if(viewType==1){
	            	Bundle bundle=new Bundle();
	            	bundle.putInt("_id",_id);
	                bundle.putInt("noteId",Integer.parseInt(list.get(pos).get("noteId").toString()));
	                bundle.putString("userId", list.get(pos).get("userId").toString());
	                bundle.putString("title", list.get(pos).get("title").toString());
	                bundle.putString("content", list.get(pos).get("content").toString());
	                bundle.putDouble("longitude", Double.parseDouble(list.get(pos).get("longitude").toString()));
	                bundle.putDouble("latitude", Double.parseDouble(list.get(pos).get("latitude").toString()));
	                bundle.putString("timeStart", list.get(pos).get("timeStart").toString());
	                bundle.putString("timeEnd", list.get(pos).get("timeEnd").toString());
	                bundle.putDouble("range", Double.parseDouble( list.get(pos).get("range").toString()));
	                bundle.putString("friends", list.get(pos).get("friends").toString());
	                System.out.println("browse:"+list.get(pos).get("friends").toString());
	                bundle.putInt("getIn", Integer.parseInt((list.get(pos).get("getIn").toString())));
	                bundle.putInt("getOut", Integer.parseInt((list.get(pos).get("getOut").toString())));
	                bundle.putString("img", list.get(pos).get("img").toString());
	                Log.v("BBBtimeEND", list.get(pos).get("timeEnd").toString());
	            	Intent intent = new Intent(BrowseActivity.this, ViewGeoNoteActivity.class);
	                intent.putExtras(bundle);
	                startActivity(intent);}
	            	else 
	            	{
	            		Bundle bundle=new Bundle();
	                    bundle.putInt("_id",_id);
	                    System.out.println("_id:"+_id);
	                    bundle.putInt("noteId",Integer.parseInt(list.get(pos).get("noteId").toString()));
	                    bundle.putString("userId",list.get(pos).get("userId").toString());
	                    bundle.putString("title",list.get(pos).get("title").toString());
	                    System.out.println(list.get(pos).get("title").toString());//
	                    bundle.putString("content", list.get(pos).get("content").toString());
	                    System.out.println(list.get(pos).get("content").toString());
	                    bundle.putString("time", list.get(pos).get("time").toString());
	                    System.out.println(list.get(pos).get("time").toString());
	                    bundle.putString("friends",list.get(pos).get("friends").toString());
	                    System.out.println(list.get(pos).get("friends").toString());
	                    bundle.putString("cycle", list.get(pos).get("cycle").toString());
	                    bundle.putString("img", list.get(pos).get("img").toString());
	                    System.out.println(list.get(pos).get("cycle").toString());
	                    Intent intent = new Intent(BrowseActivity.this, ViewTimeNoteActivity.class);
	                    intent.putExtras(bundle);
	                    //startActivity(intent);
	                    startActivity(intent);
	            		
	            	}
	            	
	            	pw.dismiss();
	            	}
	        });
	        pw.showAtLocation(parent, Gravity.CENTER, 0, 0);
	        
	        }
	 private void deleteGeoTodo() //把選擇的資料刪掉
	  {

   	String userId=AnyNoteActivity.fbId;
	    //if (_id == 0)
	      //return;
   	//System.out.println("userId:"+userId);
   	//System.out.println("user1:"+myCursor.getString(2));
   	//System.out.println("user2:"+myCursor.getString(6));
   	if((list.get(pos).get("userId").toString()).equals(userId)&&!((list.get(pos).get("friends").toString()+"_")).equals(userId))//若是自己傳給別人的就上船後刪除
	    {
	    	
	    	try {
	    		deleteNoteupload(userId);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	    }
	    myToDoDB.deleteGeo(_id);//若是自己給自己的或者是別人給自己的就直接刪除
	    cancelAlarm();
	    list = myToDoDB.geoSelect(); 
	    SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
		        new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});  
       //adapter.setViewBinder(new MyViewBinder());
      
      setListAdapter(adapter);  
	    
	    _id = 0;
	  }

	 
	 
	    private void deleteTodo() //把選擇的資料刪掉
		  {

	    	String userId=AnyNoteActivity.fbId;
		    //if (_id == 0)
		      //return;
	    	//System.out.println("userId:"+userId);
	    	//System.out.println("user1:"+myCursor.getString(2));
	    	//System.out.println("user2:"+myCursor.getString(6));
	    	if((list.get(pos).get("userId").toString()).equals(userId)&&!((list.get(pos).get("friends").toString())+"_").equals(userId))//若是自己傳給別人的就上船後刪除
		    {
		    	
		    	try {
		    		deleteNoteupload(userId);
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
		    }
		    myToDoDB.delete(_id);//若是自己給自己的或者是別人給自己的就直接刪除
		    cancelAlarm();
		    list = myToDoDB.timeSelect();  
		    SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
			        new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});  
	        //adapter.setViewBinder(new MyViewBinder());
	       
	       setListAdapter(adapter);  
		    
		    _id = 0;
		  }
		private void deleteNoteupload(String userId) throws JSONException
		{
	         
			System.out.println("deleteTimeNoteupload");
			HttpPost hp = new HttpPost("http://140.121.197.102:8088/anynote/Delete");
			HttpClient client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 2000);
			HttpConnectionParams.setSoTimeout(httpParams, 3000);
			JSONObject sender=new JSONObject();
			sender.put("userId", userId);
			sender.put("noteId",list.get(pos).get("noteId").toString());
			sender.put("noteType",viewType);
			//System.out.println("userId:"+userId);
			//System.out.println("noteId:"+myCursor.getInt(1));
			//array.put(jo1);*/
				try {
					hp.setEntity(new StringEntity(sender.toString(),HTTP.UTF_8));
					HttpResponse response = client.execute(hp);
					
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClientProtocolException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			
			
		
		}
		private void editTodo()
		{
			//if(myCursor.getString(2)!=userId);
			 //else
			 //{
			 Bundle bundle=new Bundle();
               bundle.putInt("_id",_id);
               System.out.println("_id:"+_id);
               bundle.putInt("noteId",Integer.parseInt(list.get(pos).get("noteId").toString()));
               bundle.putString("userId",list.get(pos).get("userId").toString());
               bundle.putString("title",list.get(pos).get("title").toString());
               System.out.println(list.get(pos).get("title").toString());//
               bundle.putString("content", list.get(pos).get("content").toString());
               System.out.println(list.get(pos).get("content").toString());
               bundle.putString("time", list.get(pos).get("time").toString());
               System.out.println(list.get(pos).get("time").toString());
               //bundle.putString("friends",list.get(pos).get("friends").toString());
               //System.out.println(list.get(pos).get("friends").toString());
               bundle.putString("cycle", list.get(pos).get("cycle").toString());
               bundle.putString("img", list.get(pos).get("img").toString());
               System.out.println(list.get(pos).get("cycle").toString());
               Intent intent = new Intent(BrowseActivity.this, ModifyActivity.class);
               intent.putExtras(bundle);
               //startActivity(intent);
               startActivityForResult(intent,0);
			 //}
		}
		private void editGeoTodo()//修改地裡提醒
		{
			//if(myCursor.getString(2)!=userId);
			 //else
			 //{
			 Bundle bundle=new Bundle();
               bundle.putInt("_id",_id);
              /*	public final static String GEO_id = "noteId";
	public final static String GEO_TEXT1 = "userId";
	public final static String GEO_TEXT2 = "title";
	public final static String GEO_TEXT3 = "content";
	public final static String GEO_TEXT4 = "longitude";
	public final static String GEO_TEXT5 = "latitude";
	public final static String GEO_TEXT6 = "timeStart";
	public final static String GEO_TEXT7 = "timeEnd";
	public final static String GEO_TEXT8 = "range";
	public final static String GEO_TEXT9 = "friends";
	public final static String GEO_TEXT10 = "getIn";
	public final static String GEO_TEXT11 = "getOut";
	public final static String GEO_TEXT12 = "img";
               * */
               bundle.putInt("noteId",Integer.parseInt(list.get(pos).get("noteId").toString()));
               bundle.putString("userId", list.get(pos).get("userId").toString());
               bundle.putString("title", list.get(pos).get("title").toString());
               bundle.putString("content", list.get(pos).get("content").toString());
               bundle.putDouble("longitude", Double.parseDouble(list.get(pos).get("longitude").toString()));
               bundle.putDouble("latitude", Double.parseDouble(list.get(pos).get("latitude").toString()));
               bundle.putString("timeStart", list.get(pos).get("timeStart").toString());
               bundle.putString("timeEnd", list.get(pos).get("timeEnd").toString());
               bundle.putDouble("range", Double.parseDouble( list.get(pos).get("range").toString()));
               //bundle.putString("friends", list.get(pos).get("friends").toString());
               bundle.putInt("getIn", Integer.parseInt((list.get(pos).get("getIn").toString())));
               bundle.putInt("getOut", Integer.parseInt((list.get(pos).get("getOut").toString())));
               bundle.putString("img", list.get(pos).get("img").toString());
               Log.v("BBBtimeEND", list.get(pos).get("timeEnd").toString());
               Intent intent = new Intent(BrowseActivity.this, GeoModifyActivity.class);
               intent.putExtras(bundle);
               startActivityForResult(intent,1);
		}
		private void cancelAlarm()
		{	
				
			Intent intent = new Intent(this, CallAlarm.class);
			 PendingIntent sender=PendingIntent.getBroadcast(getApplicationContext(),
	         		_id, intent, 0);
			 AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
			 am.cancel(sender);
			 
			
		}
		 @Override
		    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		               super.onActivityResult(requestCode, resultCode, data);
		               try{
			               switch(requestCode){
			               case 0:
			            	   if(isAll){
			            	   list = myToDoDB.timeSelect();
			                   SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
			           		        new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
			                   setListAdapter(adapter);
			            	   }
			            	   else{
			            		   list = myToDoDB.timeNoteSearch(note);
			            		   SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
					           		        new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
					                   setListAdapter(adapter);
			            	   }
			            	   break;
			               case 1:
			            	   if(isAll){
				            	   list = myToDoDB.geoSelect();
				                   SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
				           		        new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
				                   setListAdapter(adapter);
				            	   }
				            	   else{
				            		   list = myToDoDB.geoNoteSearch(note);
				            		   SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
						           		        new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
						                   setListAdapter(adapter);
				            	   }
			            	   
			              }
		               }
		               catch(Exception e)
		               {
		            	   e.printStackTrace();
		               }
		    }

		@Override
		public boolean onDown(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		 public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {  
		   
		     if (e1.getX() - e2.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {  
		   
		         // 切换Activity  
		         if(viewType==0){ //原本是時間提醒
		        	 
		        	  viewType=1;
		        	  if(isAll){
		        	  BrowseActivity.list=myToDoDB.geoSelect();
		        	  SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
		      		        new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
		              //adapter.setViewBinder(new MyViewBinder());
		              setListAdapter(adapter);
		              
		              }
		        	  else
		        	  {
		        		  list = myToDoDB.geoNoteSearch(note);
	            		   SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
	            				   new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
			                   setListAdapter(adapter);
			                  
		        	  }
		        	  title.setText("地理提醒");
		         }
		         else //原本是地理提醒
		         { 
		        	 viewType=0;
		        	 if(isAll){   
		        		 BrowseActivity.list=myToDoDB.timeSelect(); 
		        	 
		        	  SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
		      		        new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
		              //adapter.setViewBinder(new MyViewBinder());
		              setListAdapter(adapter);
		             
		              }
		        	 else
		        	 { 
		        		 list = myToDoDB.timeNoteSearch(note);
	            		   SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
	            				   new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
			                   setListAdapter(adapter);
			                  
			                   
		        		 
		        	 }
		        	 title.setText("時間提醒");
		         }
		        
		         
		         //Toast.makeText(this, "向左手势", Toast.LENGTH_SHORT).show();  
		     } else if (e2.getX() - e1.getX() > verticalMinDistance && Math.abs(velocityX) > minVelocity) {  
		   
		         // 切换Activity  
		    	 //Intent intent = new Intent(BrowseActivity.this, GeoBrowseActivity.class);
		         if(viewType==0){ //原本是時間提醒
		        	 viewType=1;
		        	 if(isAll){//是全選
		        	  BrowseActivity.list=myToDoDB.geoSelect();
		        	  SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
		      		        new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
		              //adapter.setViewBinder(new MyViewBinder());
		              setListAdapter(adapter);
		              
		        	 }
		        	 else{//式搜尋
		        		 list = myToDoDB.geoNoteSearch(note);
	            		   SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
	            				   new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
			                   setListAdapter(adapter);
		        		 
		        	 }
		        	 title.setText("地理提醒");
		         }
		         else  //原本是地理提醒
		         {
		        	  viewType=0;
		        	 if(isAll){//是全選
		        	  BrowseActivity.list=myToDoDB.timeSelect();
		        	  
		        	  SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
		      		        new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
		              //adapter.setViewBinder(new MyViewBinder());
		              setListAdapter(adapter);
		              
		        	 }
		        	 else{//式搜尋
		        		 list = myToDoDB.timeNoteSearch(note);
	            		   SimpleAdapter  adapter = new SimpleAdapter(BrowseActivity.this,list,R.layout.list,  
	            				   new String[] {"title","content"},new int[] { R.id.listTextView,R.id.listTextView1});
			                   setListAdapter(adapter);
			                   
		        	 }
		        	 title.setText("時間提醒");
		         }
		        
		        
		     }  
		   
		     return false;  
		 }  

		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {  
			pw.dismiss();
		    return gestureDetector.onTouchEvent(event);  
		} 
		@Override  
		   public boolean dispatchTouchEvent(MotionEvent ev) {  
		       gestureDetector.onTouchEvent(ev);  
		       // scroll.onTouchEvent(ev);  
		       return super.dispatchTouchEvent(ev);  
		   }  
		
	    
	    
	    
	    
}
