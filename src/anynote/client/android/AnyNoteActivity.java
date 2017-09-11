package anynote.client.android;



import java.io.IOException;
import java.net.MalformedURLException;
import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.DialogError;
import com.facebook.android.Facebook;
import com.facebook.android.FacebookError;
import com.facebook.android.Util;
import com.facebook.android.Facebook.DialogListener;



import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

public class AnyNoteActivity extends Activity {
	static Facebook facebook = new Facebook("121730154575156");
    String FILENAME = "AndroidSSO_data";
    static SharedPreferences mPrefs;
    public static int syncSwitch=1;
    public static int vibrateSwitch=1;
    public static boolean syncWait=true;
    public static String fbId;
    public static String fbName;
    public static String voicePath="";
    
    //View
    private ImageView login;
    private ImageView newTime;
    private ImageView newGeo;
    private ImageView broswer;
    private ImageView set;
    private ImageView memory;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //loading page
        setContentView(R.layout.main2);
        findView();
        
        mPrefs = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor editor = mPrefs.edit(); 
		syncSwitch=mPrefs.getInt("syncSwitch", 1);
		vibrateSwitch=mPrefs.getInt("vibrateSwitch", 1);
		fbId=mPrefs.getString("fbId", "");
		fbName=mPrefs.getString("fbName", "");  
		
		
		if(fbId==""&&fbName==""){
	       	Toast.makeText(this,"請登入Facebook以使用更多功能",Toast.LENGTH_SHORT)
	        .show();
		}else{
	       	Toast.makeText(this,"歡迎登入:"+fbName,Toast.LENGTH_SHORT)
	        .show();
		}

       	voicePath=mPrefs.getString("voicePath","");
        String access_token = mPrefs.getString("access_token", null);
        long expires = mPrefs.getLong("access_expires", 0);
        if(access_token != null) {
            facebook.setAccessToken(access_token);
        }
        if(expires != 0) {
            facebook.setAccessExpires(expires);
        }
        
      //set sync
        if(facebook.isSessionValid()) {
        	callSync();
    	}
        else System.out.println("no sync");

	        /*
	         * Only call authorize if the access_token has expired.
	         */
		newTime.setOnClickListener(new Button.OnClickListener(){
	            @Override
			    public void onClick(View v) {
	            	Intent intent= new Intent();
	            	intent.setClass(AnyNoteActivity.this,TimeNoteActivity.class);
	            
	            	startActivity(intent);
			    }
    
		});
		newGeo.setOnClickListener(new Button.OnClickListener(){
            @Override
		    public void onClick(View v) {
            	Intent intent= new Intent();
            	intent.setClass(AnyNoteActivity.this,GeoNoteActivity.class);
            
            	startActivity(intent);
		    }

		});
		
		broswer.setOnClickListener(new Button.OnClickListener(){
            @Override
		    public void onClick(View v) {
            	Intent intent= new Intent();
            	intent.setClass(AnyNoteActivity.this,SearchActivity.class);
            
            	startActivity(intent);
		    }

	});
			
		set.setOnClickListener(new Button.OnClickListener(){
            @Override
		    public void onClick(View v) {
            	Intent intent= new Intent();
            	intent.setClass(AnyNoteActivity.this,SetActivity.class);           
            	startActivity(intent);      	
            	
            
		    }

		});
		memory.setOnClickListener(new Button.OnClickListener(){
            @Override
		    public void onClick(View v) {
            	Intent intent= new Intent();
            	intent.setClass(AnyNoteActivity.this,MemorySearchActivity.class);           
            	startActivity(intent);      	
            	
            
		    }

		});
	    final Bundle params = new Bundle();
		login.setOnClickListener(new Button.OnClickListener(){
            @Override
		    public void onClick(View v) {
            	 if(!facebook.isSessionValid()) {   
            		facebook.authorize(AnyNoteActivity.this, new String[] { "user_about_me","publish_stream", "read_stream","user_photos","rsvp_event"},    //login
                   		new DialogListener() {
                       @Override
                       public void onComplete(Bundle values) { 
                       	    SharedPreferences.Editor editor = mPrefs.edit();
                            editor.putString("	", facebook.getAccessToken());
                            editor.putLong("access_expires", facebook.getAccessExpires());
                            editor.commit();
                    	    JSONObject json=null;
	                       	try {
	           					json = Util.parseJson(facebook.request("me", params));
	           				} catch (MalformedURLException e) {
	           					// TODO Auto-generated catch block
	           					e.printStackTrace();
	           				} catch (JSONException e) {
	           					// TODO Auto-generated catch block
	           					e.printStackTrace();
	           				} catch (IOException e) {
	           					// TODO Auto-generated catch block
	           					e.printStackTrace();
	           				} catch (FacebookError e) {
	           					// TODO Auto-generated catch block
	           					e.printStackTrace();
	           				}

               			    String userId="";//get user id
               			    String userName="";//get user id
               			    
	                       	try {
	                       		userId = json.getString("id");
	                       		userName = json.getString("name");

	                       		ToDoDB myToDoDB = new ToDoDB(AnyNoteActivity.this);
	                       		myToDoDB.loginUpdate(userId);	
	           				} catch (JSONException e) {
	           					// TODO Auto-generated catch block
	           					e.printStackTrace();
	           				}
	                    	fbId=userId;
	                		fbName=userName;  
	                       	editor.putString("fbId", userId);
	                       	editor.putString("fbName", userName);
	                       	
	                       	editor.commit();
	                       	callSync();
                   
                       	}
                       @Override
                       public void onFacebookError(FacebookError error) {}
                       @Override
                       public void onError(DialogError e) {}
                       @Override
                       public void onCancel() {}
                   });
            		
            		
		    }
            	 else{
            		 Toast.makeText(AnyNoteActivity.this, "已登入Facebook", Toast.LENGTH_SHORT).show();
            	 }
            		 
          }
	});
		
		
	}
    private void findView() {
		// TODO Auto-generated method stub
        login=(ImageView)findViewById(R.id.imageView1);
        newTime=(ImageView)findViewById(R.id.newtime);
        newGeo=(ImageView)findViewById(R.id.newgeo);
        broswer=(ImageView)findViewById(R.id.broswer);
        set=(ImageView)findViewById(R.id.set);
        memory=(ImageView)findViewById(R.id.memory);
	}
	public void callSync(){
    	System.out.println("callsync");
		Intent intent = new Intent(AnyNoteActivity.this,BroadCast.class);
        PendingIntent sender=PendingIntent.getBroadcast(getApplicationContext(),
                      999, intent, 0);
        AlarmManager am;
        am = (AlarmManager)getSystemService(ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC_WAKEUP,
                0,30000,sender);   	
        
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        facebook.authorizeCallback(requestCode, resultCode, data);
    }    

}