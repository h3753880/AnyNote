package anynote.client.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;

import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ViewGeoNoteActivity extends Activity {
	private TextView content;	
	 private TextView close;
	 private TextView IMG;
	 private TextView SOUND;
	 private TextView showRemind;
	 private ToDoDB myToDoDB;
	 private int _id;
	 private PopupWindow pw;
		private int noteId;
		private String userId;
		private String title;
		private String content1;
		private double latitude;
		private double longitude;
		private String timeStart;
		private String timeEnd;
		private double range;
		private String friends;
		private int getIn;
		private int getOut;
		private String img;
		private String sound;
			private DisplayMetrics dm;
			private int vWidth;
			private int vHeight;
	 protected void onCreate(Bundle savedInstanceState) 
	  {
		  
	    super.onCreate(savedInstanceState);
	    myToDoDB = new ToDoDB(this);
	    setContentView(R.layout.viewgeonote);
	    content=(TextView)findViewById(R.id.content);
	    close=(TextView)findViewById(R.id.close);
	    showRemind=(TextView)findViewById(R.id.showRemindPoint);
	    IMG=(TextView)findViewById(R.id.showImg);  
	    SOUND=(TextView)findViewById(R.id.playSound);  
	    dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
	    vWidth = dm.widthPixels;
	    vHeight = dm.heightPixels;
	    Bundle bundle = this.getIntent().getExtras();
	    _id=bundle.getInt("_id");
		noteId = bundle.getInt("noteId");
		userId = bundle.getString("userId");
		title = bundle.getString("title");
		content1 = bundle.getString("content");
		latitude = bundle.getDouble("latitude");
		longitude = bundle.getDouble("longitude");
		timeStart = bundle.getString("timeStart");
		timeEnd = bundle.getString("timeEnd");
		range = bundle.getDouble("range");
		friends = bundle.getString("friends");
		getIn = bundle.getInt("getIn");
		getOut = bundle.getInt("getOut");
		img = bundle.getString("img");
		sound=myToDoDB.getGeoSoundById(_id);
	    
	    
	    
	    content.setText("提醒人："+toNameString(userId)+"\n" +
	    		"標題："+title+"\n"+
	    		"內容："+content1+"\n"+
	    		"開始時間：\n"+toCalendarString(timeStart)+"\n"+
	    		"結束時間：\n"+toCalendarString(timeEnd)+"\n"+
	    		"朋友：\n"+toFriendString(friends)
	    		);
		close.setOnClickListener(new Button.OnClickListener(){
	        @Override
		    public void onClick(View v) {

	        	  
	        	
	        	  ViewGeoNoteActivity.this.finish();
	          }
	    });
		showRemind.setOnClickListener(new Button.OnClickListener(){
	        @Override
		    public void onClick(View v) {
	        	Bundle bundle = new Bundle();
	        	 bundle.putDouble("longitude", longitude);
	                bundle.putDouble("latitude", latitude);
	                Intent intent = new Intent(ViewGeoNoteActivity.this, showRemindPoint.class);
                    intent.putExtras(bundle);
                    //startActivity(intent);
                    startActivity(intent);
	        	 
	          }
	    });
		IMG.setOnClickListener(new Button.OnClickListener(){
	        @Override
		    public void onClick(View v) {
	        	if(!img.equals("0"))
		        	showPopupWindow(ViewGeoNoteActivity.this,v);
		        	else Toast.makeText(ViewGeoNoteActivity.this, "此提醒沒有圖片",
							Toast.LENGTH_SHORT).show();
	        	 
	          }
	    });
		SOUND.setOnClickListener(new Button.OnClickListener(){
	        @Override
		    public void onClick(View v) {
	        		if(!sound.equals("0")){
	        	    	 try{
		        			 String strImgPath = "/sdcard/AnyNote/";
		        	    	 Uri uri = Uri.parse(strImgPath+sound);
		        	    	 AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
		        		     int current = mAudioManager.getStreamVolume( AudioManager.STREAM_RING ); 	
		        	    	 MediaPlayer mMediaPlayer = MediaPlayer.create(ViewGeoNoteActivity.this,uri); 
		        			 mMediaPlayer.setVolume(current, current);
		        			 mMediaPlayer.start();	        			 
	        			 }catch(Exception e){
	        				 System.out.println("錄音播放失敗");
	        				 e.printStackTrace();	        				 
	        			 }
	        		}else Toast.makeText(ViewGeoNoteActivity.this, "此提醒沒有錄音",
							Toast.LENGTH_SHORT).show();
	        	 
	          }
	    });

	  }
	 private String toNameString(String userId)
		{
			
			
		   String setTimeString="";
			if(myToDoDB.searchFriendName(userId).equals("0"))setTimeString+="自己";
			else setTimeString=myToDoDB.searchFriendName(userId);
		
	       return setTimeString;
	        //System.out.print(dateTime.toString());
		}
	 private String toFriendString(String friends)
		{
			System.out.print("friends:"+friends);
			String[] setfriends=friends.split("_");
		   String setTimeString="";
		   System.out.print("GeoNote friends:"+friends);
		   for(String friend : setfriends){
			   System.out.print("GeoNote friend:"+friend);
			if(myToDoDB.searchFriendName(friend).equals("0"));
			else setTimeString+=myToDoDB.searchFriendName(friend)+" ";
		   }
		
	       return setTimeString;
	        //System.out.print(dateTime.toString());
		}
	 private String toCalendarString(String time)
		{
			String[] setTime=time.split("-");
	       String setTimeString=setTime[0]+"年"+(Integer.parseInt(setTime[1])+1)+"月"+setTime[2]+"日"+setTime[3]+":"+setTime[4];
	       
	       return setTimeString;
	        //System.out.print(dateTime.toString());
		}
	 public void showPopupWindow(Context context,View parent){
	        LayoutInflater inflater = (LayoutInflater)   
	        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);   
	        final View vPopupWindow=inflater.inflate(R.layout.imgviewer, null, false);
	       
	        pw= new PopupWindow(vPopupWindow,vWidth-50,vHeight-50,true);
	        pw.setFocusable(true);
	     
	        pw.setBackgroundDrawable(new BitmapDrawable());
	        ImageView photo=(ImageView)vPopupWindow.findViewById(R.id.imgView);
			  String strImgPath = "/sdcard/AnyNote/";
	  		  String allPath=strImgPath+img;
			   		 BitmapFactory.Options options = new BitmapFactory.Options();
			   		 options.inSampleSize = 6;
			   		 final Bitmap bm = BitmapFactory.decodeFile(allPath, options);
	                 photo.setImageBitmap(bm);
	                 photo.setOnClickListener(new Button.OnClickListener(){
	         	        @Override
	         		    public void onClick(View v) {
	         	        	
	         	        	pw.dismiss();
	         	        	 
	         	          }
	         	    });
	        pw.showAtLocation(parent, Gravity.CENTER, 0, 0);       
	 }

}
