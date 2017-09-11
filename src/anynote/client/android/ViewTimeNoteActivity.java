package anynote.client.android;

import android.app.Activity;
import android.content.Context;
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
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class ViewTimeNoteActivity extends Activity {
	private TextView content;	
	 private TextView close;
	 private TextView IMG;
	 private TextView SOUND;
	 private ToDoDB myToDoDB;
	 private int _id;
		private int noteId;
		private String userId;
		private String title;
		private String content1;
		private String time;
		private String friends;
		 private PopupWindow pw;
		private String img;
		private String sound;
		private DisplayMetrics dm;
		private int vWidth;
		private int vHeight;
	 protected void onCreate(Bundle savedInstanceState) 
	  {
		  
	    super.onCreate(savedInstanceState);
	    myToDoDB = new ToDoDB(this);
	    setContentView(R.layout.viewtimenote);
	    content=(TextView)findViewById(R.id.contentTime);
	    close=(TextView)findViewById(R.id.close);  
	    IMG=(TextView)findViewById(R.id.showImg);
	    SOUND=(TextView)findViewById(R.id.playSound);  
	    dm = new DisplayMetrics();
		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
	    vWidth = dm.widthPixels;
	    vHeight = dm.heightPixels;
	    Bundle bundle = this.getIntent().getExtras();
	    _id = bundle.getInt("_id");
	    noteId = bundle.getInt("noteId");
		userId = bundle.getString("userId");
		title = bundle.getString("title");
		content1 = bundle.getString("content");
		friends=bundle.getString("friends");
		time = bundle.getString("time");
		img=bundle.getString("img");
		sound=myToDoDB.getTimeSoundById(_id);
		System.out.print("view:"+img);
		 content.setText("提醒人："+toNameString(userId)+"\n" +
		    		"標題："+title+"\n"+
		    		"內容："+content1+"\n"+
		    		"提醒時間：\n"+toCalendarString(time)+"\n"+
		    		"朋友：\n"+toFriendString(friends)
		    		);
	    
	    
	    
	    
		close.setOnClickListener(new Button.OnClickListener(){
	        @Override
		    public void onClick(View v) {

	        	  //跳出提醒鬧鐘頁面
	        	
	        	  ViewTimeNoteActivity.this.finish();
	          }
	    });
		IMG.setOnClickListener(new Button.OnClickListener(){
	        @Override
		    public void onClick(View v) {
	        	
	        	if(!img.equals("0"))
	        	showPopupWindow(ViewTimeNoteActivity.this,v);
	        	else Toast.makeText(ViewTimeNoteActivity.this, "此提醒沒有圖片",
						Toast.LENGTH_SHORT).show();
	          }
	    });
		SOUND.setOnClickListener(new Button.OnClickListener(){
	        @Override
		    public void onClick(View v) {
	        		if(!sound.equals("0")){
	        			 try{
	        			 System.out.println("in:"+sound);
	        	    	 String strImgPath = "/sdcard/AnyNote/";
	        	    	 Uri uri = Uri.parse(strImgPath+sound);
	        	    	 AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
	        		     int current = mAudioManager.getStreamVolume( AudioManager.STREAM_RING ); 	
	        	    	 MediaPlayer mMediaPlayer = MediaPlayer.create(ViewTimeNoteActivity.this,uri); 
	        			 mMediaPlayer.setVolume(current, current);
	        			 mMediaPlayer.start();
	        			 }catch(Exception e){
	        				 System.out.println("錄音播放失敗");
	        				 e.printStackTrace();	        				 
	        			 }
	        		}else{ 
	        			System.out.println("out:"+sound);
	        			Toast.makeText(ViewTimeNoteActivity.this, "此提醒沒有錄音",
							Toast.LENGTH_SHORT).show();
	        		}
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
			

			String[] setfriends=friends.split("_");
		   String setTimeString="";
		 System.out.print("TimeNote friends:"+friends);
		   for(String friend : setfriends){
			   System.out.print("TimeNote friend:"+friend);
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
