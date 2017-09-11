package anynote.client.android.Alarm;

/* import相關class */


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import anynote.client.android.AnyNoteActivity;
import anynote.client.android.R;
import android.media.MediaPlayer;
import android.net.Uri;

/* 實際跳出鬧鈴Dialog的Activity */
public class AlarmAlert extends Activity
{
  private Vibrator myVibrator=null;
  private MediaPlayer  mMediaPlayer= null;	
  TextView content;	
  TextView close;	
  ImageView photo;
  DisplayMetrics dm;
  Bitmap bm;
  int vWidth=0;
  int vHeight=0;
  boolean photoView=true;
  @Override
  protected void onCreate(Bundle savedInstanceState) 
  {
	  
    super.onCreate(savedInstanceState);
    setContentView(R.layout.alarm);
    content=(TextView)findViewById(R.id.content);
    close=(TextView)findViewById(R.id.close);  
    photo=(ImageView)findViewById(R.id.img);  
	dm = new DisplayMetrics();
	this.getWindowManager().getDefaultDisplay().getMetrics(dm);
    vWidth = dm.widthPixels;
    vHeight = dm.heightPixels;
    /* 跳出的鬧鈴警示  */
    System.out.println("alarm00");
    Bundle test=this.getIntent().getExtras();
    System.out.println("alarm01");
    myVibrator = (Vibrator) getApplication().getSystemService(Service.VIBRATOR_SERVICE);
    System.out.println(test.getString("sound"));
    if(!test.getString("sound").equals("0")){
    	 String strImgPath = "/sdcard/AnyNote/";
    	 System.out.println("Sound Url:"+strImgPath+test.getString("sound"));
    	 Uri uri = Uri.parse(strImgPath+test.getString("sound"));
    	 AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
	     int current = mAudioManager.getStreamVolume( AudioManager.STREAM_RING ); 	
    	 mMediaPlayer=MediaPlayer.create(this,uri); 
		 mMediaPlayer.setVolume(current, current);
		 mMediaPlayer.start();
    }
    else if(!AnyNoteActivity.voicePath.equals("")){
	     Uri uri = Uri.parse(AnyNoteActivity.voicePath);
	     AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE); 
	     int current = mAudioManager.getStreamVolume( AudioManager.STREAM_RING ); 	
	     mMediaPlayer=MediaPlayer.create(this,uri); 
		 mMediaPlayer.setVolume(current, current);
		 mMediaPlayer.start();
    
    }
    System.out.println("alarm02");
    if(AnyNoteActivity.vibrateSwitch==1){
    	long[] pattern = {800, 50, 400, 30};
	    myVibrator.vibrate(pattern,1);
	    System.out.println("vibrate");
    }
	new Thread() {
		public void run() {
			try {
				sleep(30000);
				myVibrator.cancel();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}.start();

    StringBuffer sb = new StringBuffer(test.getString("content"));    
    /*將content換行*/
    if(sb.length()>15){
    	for(int a=14;a<sb.length();a+=15){
    		sb.insert(a, '\n');
    	}
    }
    System.out.println("sb");
    String output=test.getString("name").substring(0,test.getString("name").length()-1 )+":\n"+test.getString("title")+"\n"+sb.toString();
    System.out.println("ouput:");
    content.setText(output);
   
    
		 String strImgPath = "/sdcard/AnyNote/";
		 System.out.println("img::"+test.getString("img").toString());
		try{
			 if(!test.getString("img").toString().equals("0")){
				 photo.setPadding(0, vHeight/9, vWidth/12, 0);
				 String allPath=strImgPath+test.getString("img").toString();
		   		 BitmapFactory.Options options = new BitmapFactory.Options();
		   		 options.inSampleSize = 6;
		   		 bm = BitmapFactory.decodeFile(allPath, options);
		   		 photo.setImageBitmap(zoomBitmap(bm,vWidth/3,vWidth/3));
			 }
		}catch(Exception e){
			e.printStackTrace();
		}
   	photo.setOnClickListener(new Button.OnClickListener(){
        @Override
	    public void onClick(View v) {
        	if(photoView){
        	    photo.setPadding(0, 0, 0, 0);
        		photo.setImageBitmap(bm);
        		photoView=false;
        	}
        	else{
        	    photo.setPadding(0, vHeight/9, vWidth/12, 0);
       		 	photo.setImageBitmap(zoomBitmap(bm,vWidth/3,vWidth/3));
       		 	photoView=true;
        	}

          }
    });
   		 
   	close.setOnClickListener(new Button.OnClickListener(){
        @Override
	    public void onClick(View v) {
        		 
        	  //跳出提醒鬧鐘頁面
        	try {
        	  if(AnyNoteActivity.vibrateSwitch==1)myVibrator.cancel();
        	  if(AnyNoteActivity.voicePath!="") mMediaPlayer.stop();
			} catch (Exception e) {
				e.printStackTrace();
			}
        	  AlarmAlert.this.finish();
        	  /*Intent i = new Intent(Intent.ACTION_MAIN);
        	  i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	  i.addCategory(Intent.CATEGORY_HOME);
        	  startActivity(i); */
          }
    });


  } 
	public static Bitmap zoomBitmap(Bitmap bitmap,int w,int h) { 
        int width = bitmap.getWidth(); 
        int height = bitmap.getHeight(); 
        Matrix matrix = new Matrix(); 
        float scaleWidht = ((float) w / width); 
        float scaleHeight = ((float) h / height); 
        matrix.postScale(scaleWidht, scaleHeight); 
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height, 
                matrix, true); 
        return newbmp; 
    }
}