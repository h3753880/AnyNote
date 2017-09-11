package anynote.client.android;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import anynote.client.android.Alarm.CallAlarm;
import anynote.client.android.Alarm.GeoDetect;
import anynote.client.classes.GeoNote;
import anynote.client.classes.TimeNote;

public class Sync extends Service {
	
	private final static String TAG="Sync";
	String serverIp = "http://140.121.198.86:8088";
	String serverUrl = serverIp+"/anynote/";
	private static Calendar dateTime=Calendar.getInstance();
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
		System.out.println("onStart");
		
		if(AnyNoteActivity.facebook.isSessionValid()) {  
	  		 Thread syncThread= new Thread(new Runnable() {
	             public void run() {
	            	synchronized(this) {
		     			AnyNoteActivity.syncWait=false;
	            		System.out.println("ifNewUser"+AnyNoteActivity.fbId+":"+AnyNoteActivity.fbName);
		    			ifNewUser(AnyNoteActivity.fbId,AnyNoteActivity.fbName);
		    			System.out.println("download"+AnyNoteActivity.fbId);
		                timeDownload(AnyNoteActivity.fbId);
		                geoDownload(AnyNoteActivity.fbId);
		                timeUpload();
		                geoUpload();
		                AnyNoteActivity.syncWait=true;
	            	}
	             }
	  		 });
	  		 if(AnyNoteActivity.syncWait){
	  			 syncThread.start();
	  		 }
		}
		
	}
	
	public void ifNewUser(String userId,String userName)
	{
		
			HttpPost hp = new HttpPost(serverUrl+"newUser");
			HttpClient client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
			HttpConnectionParams.setSoTimeout(httpParams, 10000);
			try {
				hp.setEntity(new StringEntity(userId+"_"+userName,HTTP.UTF_8));
				//HttpResponse response = client.execute(hp);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				HttpResponse response = client.execute(hp);
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("ifNewUserfinish");
	}
	
	private void timeDownload(String userId)
	{
				
			try {
				
					String url = serverUrl+"GetTimeNote";
					String body = contentDownload(url,userId);
					JSONArray array = new JSONArray(body);
					for (int i = 0; i < array.length(); i++) {
						JSONObject obj = array.getJSONObject(i);
						Log.v(TAG, obj.toString());
						ToDoDB myToDoDB = new ToDoDB(this);
						String imgUrl="0";
						String soundUrl="0";
						//Image Download
						if(!obj.getString("img").equals("0")){
							imgUrl=obj.getString("userId")+"/"+obj.getString("img");
						}
						//Sound Download
						if(!obj.getString("sound").equals("0")){
							soundUrl=obj.getString("userId")+"/"+obj.getString("sound");
						}
						TimeNote timeNote=new TimeNote(obj.getInt("noteId"),obj.getString("userId"),obj.getString("friendId"),
								obj.getString("title"),obj.getString("content"),obj.getString("time"),obj.getInt("cycle"),imgUrl,soundUrl);

						Log.v(TAG, Integer.toString(obj.getInt("status")));
						switch( obj.getInt("status") )
						{
						case 1:
							System.out.println("GetName:"+obj.getString("userName"));
							myToDoDB.insertNewFriendName(obj.getString("userName"), obj.getString("userId"));
							if(!obj.getString("img").equals("0")){
								getImg(imgUrl,obj.getString("userId"));
							}
							if(!obj.getString("sound").equals("0")){
								getSound(soundUrl,obj.getString("userId"));
							}
							myToDoDB.insertTimeNote(timeNote);
							changeToCalendar(timeNote.time);
							setAlarm(dateTime,timeNote);
							
							break;
						case 2:
							myToDoDB.syncUpdateTimeNote(timeNote.noteId, timeNote.title, timeNote.content, timeNote.userId,timeNote.time);
							changeToCalendar(timeNote.time);
							setAlarm(dateTime,timeNote);
							break;
						case 3:
							myToDoDB.syncdelete(timeNote.noteId,timeNote.userId);
							cancelAlarm(timeNote);
							break;
						
						}
						myToDoDB.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}	
				
			System.out.println("time download finish!!!");
	}
	
	private void geoDownload(String userId)
	{
				
			try {
				
					//String url = "http://140.121.197.102:8088/anynote/";
					String url = serverUrl+"GetGeoNote";
					String body = contentDownload(url,userId);
					JSONArray array = new JSONArray(body);
					for (int i = 0; i < array.length(); i++) 
					{
						JSONObject obj = array.getJSONObject(i);
						Log.v(TAG, obj.toString());
						ToDoDB myToDoDB = new ToDoDB(this);
						String imgUrl="0";
						String soundUrl="0";
						//Image Download
						if(!obj.getString("img").equals("0")){
							imgUrl=obj.getString("userId")+"/"+obj.getString("img");
						}
						//Sound Download
						if(!obj.getString("sound").equals("0")){
							soundUrl=obj.getString("userId")+"/"+obj.getString("sound");
						}
						GeoNote geoNote=new GeoNote(obj.getInt( "noteId"), obj.getString("userId")
								,obj.getString("friends"),obj.getString("title"),obj.getString("content")
								,obj.getDouble("longitude"),obj.getDouble("latitude"),obj.getDouble("range")
								,obj.getString("startTime"),obj.getString("finishTime")
								,obj.getBoolean("getIn"),obj.getBoolean("getOut"),imgUrl,soundUrl);
						
						int _id;
						switch( obj.getInt("status") )
						{
						case 1:
							Log.v("下載新增geo", "start");
							myToDoDB.insertNewFriendName(obj.getString("userName"), obj.getString("userId"));
							if(!obj.getString("img").equals("0")){
								getImg(imgUrl,obj.getString("userId"));
							}
							if(!obj.getString("sound").equals("0")){
								getSound(soundUrl,obj.getString("userId"));
							}
							myToDoDB.insertGeoNote(geoNote);
							_id=myToDoDB.geoNoteAlarmId(geoNote);
							Intent intent = new Intent();
							Bundle bundle = new Bundle();
							//bundle.putString("userId", geoNote.userId);
							//bundle.putInt("noteId",geoNote.noteId);
							bundle.putInt("ID", _id);
							bundle.putString("userId", geoNote.userId);
							bundle.putString("title", geoNote.title);
							bundle.putString("content", geoNote.content);
							bundle.putDouble("longitude", geoNote.Longitude);
							bundle.putDouble("latitude", geoNote.Latitude);
							bundle.putDouble("distance", geoNote.range);
							changeToCalendar(geoNote.startTime);
							bundle.putInt("year", dateTime.getTime().getYear()+1900);
							bundle.putInt("month", dateTime.getTime().getMonth());
							bundle.putInt("day", dateTime.getTime().getDate());
							bundle.putInt("hour", dateTime.getTime().getHours());
							bundle.putInt("minute",dateTime.getTime().getMinutes());
							changeToCalendar(geoNote.finishTime);
							bundle.putInt("year2", dateTime.getTime().getYear()+1900);
							bundle.putInt("month2", dateTime.getTime().getMonth());
							bundle.putInt("day2", dateTime.getTime().getDate());
							bundle.putInt("hour2", dateTime.getTime().getHours());
							bundle.putInt("minute2", dateTime.getTime().getMinutes());
							bundle.putBoolean("checkboxIn", geoNote.getIn);
							bundle.putBoolean("checkboxOut", geoNote.getOut);	
							bundle.putString("img", geoNote.img);	
							bundle.putString("sound", geoNote.sound);	
							bundle.putInt("status", 1);
							intent.putExtras(bundle);
							intent.setClass(Sync.this, GeoDetect.class);
							startService(intent);	
							Log.v("下載新增geo", "end");
							break;
						case 2://修改
							myToDoDB.syncUpdateGeoNote(geoNote.noteId, geoNote.title,
									geoNote.content, geoNote.userId, geoNote.startTime, geoNote.finishTime,geoNote.getIn,geoNote.getOut,geoNote.range);
							_id=myToDoDB.geoNoteAlarmId(geoNote);
							Intent intent2 = new Intent();
							Bundle bundle2 = new Bundle();
							//bundle2.putString("userId", geoNote.userId);
							//bundle2.putInt("noteId",geoNote.noteId);
							bundle2.putInt("ID", _id);
							bundle2.putString("title", geoNote.title);
							bundle2.putString("content", geoNote.content);
							bundle2.putDouble("distance", geoNote.range);
							changeToCalendar(geoNote.startTime);
							bundle2.putInt("year", dateTime.getTime().getYear()+1900);
							bundle2.putInt("month", dateTime.getTime().getMonth());
							bundle2.putInt("day", dateTime.getTime().getDate());
							bundle2.putInt("hour", dateTime.getTime().getHours());
							bundle2.putInt("minute",dateTime.getTime().getMinutes());
							changeToCalendar(geoNote.finishTime);
							bundle2.putInt("year2", dateTime.getTime().getYear()+1900);
							bundle2.putInt("month2", dateTime.getTime().getMonth());
							bundle2.putInt("day2", dateTime.getTime().getDate());
							bundle2.putInt("hour2", dateTime.getTime().getHours());
							bundle2.putInt("minute2", dateTime.getTime().getMinutes());
							bundle2.putBoolean("checkboxIn", geoNote.getIn);
							bundle2.putBoolean("checkboxOut", geoNote.getOut);
							intent2.putExtras(bundle2);
							intent2.setClass(Sync.this, GeoDetect.class);
							startService(intent2);
							break;
						case 3://刪除
							Log.v("下載delete", "delete");
							_id=myToDoDB.geoNoteAlarmId(geoNote);
							myToDoDB.syncGeodelete(geoNote.noteId, geoNote.userId);
							
							Intent intent3 = new Intent();
							Bundle bundle3 = new Bundle();
							Log.v("ID", Integer.toString(_id));
							bundle3.putInt("ID", _id);
							bundle3.putInt("status", 3);
							//bundle3.putString("userId", geoNote.userId);
							//bundle3.putInt("noteId",geoNote.noteId);
							intent3.putExtras(bundle3);
							intent3.setClass(Sync.this, GeoDetect.class);
							startService(intent3);
							Log.v("下載deleteEnd", "delete");
							break;
						}
						myToDoDB.close();
					}
				} catch (Exception e) {
					e.printStackTrace();
					// TODO: handle exception
				}	
				
			System.out.println("Geo download finish!!!");
	}
	
	private String contentDownload(String url,String userId) throws Exception {
		StringBuilder sb = new StringBuilder();
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		
		HttpConnectionParams.setConnectionTimeout(httpParams, 2000);
		HttpConnectionParams.setSoTimeout(httpParams, 3000);
		HttpPost hp=new HttpPost(url);
		hp.setEntity(new StringEntity(userId,HTTP.UTF_8));
		HttpResponse response = client.execute(hp);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					entity.getContent(), "UTF-8"), 8192);
			String line = null;
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
			reader.close();
		}

		return sb.toString();
	}
	
	
    private void timeUpload()
    {
	    	
			//System.out.println("upload");
			//取出已經上傳的max id 
			int timeNoteId=AnyNoteActivity.mPrefs.getInt("upTimeNoteId", 0);
			System.out.println("已經上傳的max id :"+timeNoteId);
			
			SharedPreferences.Editor PE = AnyNoteActivity.mPrefs.edit();
			
        
		JSONArray  jArray=new JSONArray();
		ToDoDB db=new ToDoDB(Sync.this);
	 	
        System.out.println(timeNoteId+":"+(db.maxTimeId()));
        if(timeNoteId < (db.maxTimeId()))
		{
			HttpPost hp = new HttpPost(serverUrl+"Create");
			HttpClient client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 4000);
			ArrayList<TimeNote> timeNote=db.selectTime(timeNoteId);

			try {
					for(TimeNote bean:timeNote)
					{
						Log.v(TAG,bean.title);
						Log.v(TAG,bean.userId);
						JSONObject jo = new JSONObject();
						System.out.println(bean.userId+" "+bean.friends);
						jo.put("userId", bean.userId);
						jo.put("noteId", bean.noteId);
						jo.put("friends", bean.friends);
						jo.put("title",bean.title);
						jo.put("content", bean.content);
						jo.put("time", bean.time);
						jo.put("cycle", bean.cycle);
						jo.put("img", bean.img);
						jo.put("sound", bean.sound);
						jArray.put(jo);
						String strImgPath = "/sdcard/AnyNote/";
						String allPath=strImgPath+bean.img;
						if(!bean.img.equals("0")&&bean.img!=null){
							System.out.println("allPath"+allPath);
							 final String filename=bean.img;
					   		 BitmapFactory.Options options = new BitmapFactory.Options();
					   		 options.inSampleSize = 6;
					   		 final Bitmap bm = BitmapFactory.decodeFile(allPath, options);
					   		 Thread upload= new Thread(new Runnable() {
					                   public void run() {
					                   	uploadImg(filename,bm);
					                   }
					            });
					   		 upload.start(); 
						}
						if(!bean.sound.equals("0")&&bean.sound!=null){
							 System.out.println("allPath"+allPath);
							 final String sFilename=bean.sound;

					   		 Thread uploadS= new Thread(new Runnable() {
					                   public void run() {
					                   	uploadSound(sFilename);
					                   }
					            });
					   		 uploadS.start(); 
						}						
					}
		
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			try {
				if(jArray!=null){
					hp.setEntity(new StringEntity(jArray.toString(),HTTP.UTF_8));
					HttpResponse response = client.execute(hp);
					PE.putInt("upTimeNoteId", db.maxTimeId());
			    	PE.commit();
					Log.v(TAG,jArray.toString());
				}
				
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
		
		   db.close();
	}
	}
	
	private void geoUpload()
	{
			//取出已經上傳的max id 
			int geoNoteId=AnyNoteActivity.mPrefs.getInt("upGeoNoteId", 0);
			System.out.println("geoUpload已經上傳的max id :"+geoNoteId);
			
			SharedPreferences.Editor PE = AnyNoteActivity.mPrefs.edit();
        
		JSONArray  jArray=new JSONArray();
		ToDoDB db=new ToDoDB(Sync.this);
		System.out.println(geoNoteId+":"+(db.maxGeoId()));
		if(geoNoteId < (db.maxGeoId()))
		{
			HttpPost hp = new HttpPost(serverUrl+"CreateGeoNote");
			//HttpPost hp = new HttpPost("http://140.121.197.102:8088/anynote/CreateGeoNote");
			HttpClient client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 3000);
			HttpConnectionParams.setSoTimeout(httpParams, 4000);
			ArrayList<GeoNote> geoNote=db.selectGeo(geoNoteId);
				
			try {
					for(GeoNote bean:geoNote)
					{
						System.out.println(bean.Longitude+" v");
						System.out.println(bean.Latitude+" v");
						System.out.println(bean.city);					
						Log.v(TAG,bean.title);
						Log.v(TAG,bean.userId);
						Log.v("喜愛點:",bean.getFavPlace());
						
						JSONObject jo = new JSONObject();
						System.out.println(bean.userId+" "+bean.friends);
						jo.put("userId", bean.userId);
						jo.put("noteId", bean.noteId);
						jo.put("friends", bean.friends);
						jo.put("title",bean.title);
						jo.put("content", bean.content);
						jo.put("longitude", bean.Longitude);
						jo.put("latitude", bean.Latitude);
						jo.put("timeStart", bean.startTime);
						jo.put("timeEnd", bean.finishTime);
						jo.put("range", bean.range);
						jo.put("friends", bean.friends);
						jo.put("getIn", bean.getIn);
						jo.put("getOut", bean.getOut);
						jo.put("img", bean.img);
						jo.put("sound", bean.sound);
						jo.put("city", bean.city);
						jo.put("favPlace", bean.getFavPlace());
						String strImgPath = "/sdcard/AnyNote/";
						String allPath=strImgPath+bean.img;
						if(!bean.img.equals("0")&&bean.img!=null){
							System.out.println("allPath"+allPath);
							 final String filename=bean.img;
					   		 BitmapFactory.Options options = new BitmapFactory.Options();
					   		 options.inSampleSize = 6;
					   		 final Bitmap bm = BitmapFactory.decodeFile(allPath, options);
					   		 Thread upload= new Thread(new Runnable() {
					                   public void run() {
					                   	uploadImg(filename,bm);
					                   }
					            });
					   		 upload.start(); 
						}
						if(!bean.sound.equals("0")&&bean.sound!=null){
							 System.out.println("allPath"+allPath);
							 final String sFilename=bean.sound;
					   		 Thread uploadS= new Thread(new Runnable() {
					                   public void run() {
					                   	uploadSound(sFilename);
					                   }
					            });
					   		 uploadS.start(); 
						}	
						jArray.put(jo);
					}
		
				} catch (JSONException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			try {
				if(jArray!=null){
					hp.setEntity(new StringEntity(jArray.toString(),HTTP.UTF_8));
					HttpResponse response = client.execute(hp);
					PE.putInt("upGeoNoteId", db.maxGeoId());
			    	PE.commit();
					Log.v(TAG,jArray.toString());
				}
				
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
		
		   db.close();
		}
	}
	
    
	private void setAlarm(Calendar alarmTime,TimeNote timeNote)
	{
		ToDoDB db=new ToDoDB(this);
		String name="0";
			name=db.searchFriendName(timeNote.userId);
		if(name.equals("0")){
			name="自己/n";
		}
			int alarmId=db.timeNoteAlarmId(timeNote);
		System.out.println("set_TimealarmId:"+alarmId);
		if(timeNote.cycle==0)
		{
			Bundle bundle=new Bundle();
            bundle.putString("title",timeNote.title);
            bundle.putString("content",timeNote.content);
            bundle.putString("name",name);
            bundle.putString("img",timeNote.img);
            bundle.putString("sound",timeNote.sound);
            Intent intent = new Intent(Sync.this, CallAlarm.class);
            intent.putExtras(bundle);
            
            PendingIntent sender=PendingIntent.getBroadcast(getApplicationContext(),
            		alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            am.set(AlarmManager.RTC_WAKEUP, alarmTime.getTimeInMillis(), sender);

		}
		else
		{
			long times=0;
        	if(timeNote.cycle==1)times=360;
        	else if(timeNote.cycle==2)times=8640;
        	else if(timeNote.cycle==3)times=60480;
        	else if(timeNote.cycle==4)times=259200;
        	Bundle bundle=new Bundle();
        	bundle.putString("title",timeNote.title);
            bundle.putString("content",timeNote.content);
            bundle.putString("name",name);
            bundle.putString("img",timeNote.img);
            bundle.putString("sound",timeNote.sound);
            /* 織繡穢w疆x�癒�包穢w��色�簞簧���色�n�授亂allAlarm.class */
            Intent intent = new Intent(Sync.this, CallAlarm.class);
            intent.putExtras(bundle);
            /* 織藩�ｇ�PendingIntent */       
            PendingIntent sender=PendingIntent.getBroadcast(getApplicationContext(),
            		alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
           
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            am.setRepeating(AlarmManager.RTC_WAKEUP,
                     alarmTime.getTimeInMillis(),times*1000,sender);
			
		}
		db.close();
	}
	
	private void changeToCalendar(String time)//change calendar type
	{
		String[] setTime=time.split("-");
		//System.out.print(time);
		//Calendar date = null ;
		
        dateTime.setTimeInMillis(System.currentTimeMillis());
       dateTime.set(Calendar.YEAR,Integer.parseInt(setTime[0]));
        dateTime.set(Calendar.MONTH,Integer.parseInt(setTime[1]));
       dateTime.set(Calendar.DAY_OF_MONTH,Integer.parseInt(setTime[2]));
       dateTime.set(Calendar.HOUR_OF_DAY,Integer.parseInt(setTime[3]));
       dateTime.set(Calendar.MINUTE,Integer.parseInt(setTime[4]));
       dateTime.set(Calendar.SECOND,0);
        dateTime.set(Calendar.MILLISECOND,0);
        //System.out.print(dateTime.toString());
		
		
	}
	
	private void cancelAlarm(TimeNote timeNote)
	{	
		ToDoDB db=new ToDoDB(this);
	
		int alarmId=db.timeNoteAlarmId(timeNote);
		Intent intent = new Intent(this, CallAlarm.class);
		 PendingIntent sender=PendingIntent.getBroadcast(getApplicationContext(),
				 alarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		 AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		 am.cancel(sender);
		 db.close();
		 
		
	}
	
	 public void getImg(String partUrl,String folder){
		 System.out.println("Download photo Start!!!");

		 
		 //if(partUrl.charAt(partUrl.length()-1)!='0'){
			 System.out.println(partUrl);
			 try{ 
				 Bitmap bitmap = null;
				 String imgUrl=serverUrl+"uploads/"+partUrl;
				 URL url = new URL(imgUrl);
		 		 URLConnection conn = url.openConnection();
		  		 HttpURLConnection httpConn = (HttpURLConnection)conn;
		  		 httpConn.setDoInput(true);
		  		 httpConn.setRequestProperty("Connection", "Keep-Alive");
		  		 InputStream inputStream = httpConn.getInputStream();
		  	     bitmap=BitmapFactory.decodeStream(new FlushedInputStream(inputStream)); 
		  	     inputStream.close();
				 
			
				File SpicyDirectory = new File("/sdcard/AnyNote/"+folder+"/");
			    if (!SpicyDirectory.exists()) {
			         SpicyDirectory.mkdirs();
		        }
				String filename="/sdcard/AnyNote/" + partUrl;
				FileOutputStream out = null;
				out = new FileOutputStream(filename);
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				out.flush();
				out.close();
			 }catch(Exception e){
		       		e.printStackTrace();
		     } 
	
		 System.out.println("Download photo End!!!");
	 }
	 public String uploadImg(String filename,Bitmap bm){
		   	try{
	       		System.out.println("Upload photo Start!!!");


	       		
	       		ByteArrayOutputStream out = new ByteArrayOutputStream();
	       		bm.compress(Bitmap.CompressFormat.JPEG, 70, out);
	       		byte[] bb=out.toByteArray();
	       		
	       		
		 
		       	 
		       	final String BOUNDARY   = "---------------------------265001916915724";
		       	final String HYPHENS    = "--";
		       	final String CRLF       = "\r\n";
		       	URL url                 = new URL(serverUrl+"uploadservlet"+"?id="+AnyNoteActivity.fbId);    
		       	HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
		       	conn.setRequestMethod("POST");                      // method一定要是POST
		       	conn.setDoOutput(true);
		       	conn.setDoInput(true);
		       	conn.setUseCaches(false);   

		       	// 把Content Type設為multipart/form-data
		       	// 以及設定Boundary，Boundary很重要!
		       	// 當你不只一個參數時，Boundary是用來區隔參數的   

		        conn.setRequestProperty("Connection", "Keep-Alive");
		        conn.setRequestProperty("referer", serverUrl);
		        conn.setRequestProperty("Connection", "Keep-Alive");
		        //conn.setRequestProperty("Charset", "UTF-8");
		       	conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
		
		
		       	// 下面是開始寫參數
		       	String strContentDisposition = "Content-Disposition: form-data; name=\"filename\"; filename=\""+filename+"\"";
		       	String strContentType = "Content-Type: image/jpeg";
		       	DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());
		       	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);       // 寫BOUNDARY
		       	dataOS.writeBytes(strContentDisposition+CRLF);  // 寫(Disposition)
		       	dataOS.writeBytes(strContentType+CRLF);            // 寫(Content Type)
		       	dataOS.writeBytes(CRLF);        

		     	//int iBytesAvailable = fileInputStream.available();
		       	int iBytesAvailable = 1024;
		       	byte[] byteData = new byte[iBytesAvailable];
		       	//int iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
		       	int x=0;
		       	while (x < bb.length) {
		       		if(bb.length-x<iBytesAvailable){
		       			dataOS.write(bb, x, (bb.length-x)); // 開始寫內容
		       			x+=(bb.length-x);
		       		}
		       		else{
			       	    dataOS.write(bb, x, iBytesAvailable); // 開始寫內容
			       	    x+=iBytesAvailable;
		       		}
		       	    //iBytesAvailable = fileInputStream.available();
		       	    //iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
		       	}
		 
		       	dataOS.writeBytes(CRLF);    
		       	dataOS.writeBytes(HYPHENS+BOUNDARY+HYPHENS+CRLF);    // (結束)寫--==================================--      
		       	
		       	dataOS.flush();
		       	dataOS.close();
		       	//fileInputStream.close();
		       	
		        InputStream stream = conn.getInputStream();
		        BufferedInputStream in = new BufferedInputStream(stream,8192);
		        int i = 0; 
		        String imageUrl = "";
		        while ((i = in.read()) != -1) {
		        	System.out.print(i); 
		        	if(i==1){
			        	imageUrl=serverIp+"/fileup/uploads/"+filename;
			        break;
			        }
		        }  
		        in.close();
		        if(imageUrl.length()>0){
		        	System.out.println("Upload photo End!!!");
		        }
		        return imageUrl;
	       	}catch(Exception e){
	       		e.printStackTrace();
	       	}
	       	return ""; 
	    }

		
	 public void getSound(String partUrl,String folder){
		 System.out.println("Download sound Start!!!");
		 //if(partUrl.charAt(partUrl.length()-1)!='0'){
			
			 try{ 

				 String soundUrl=serverUrl+"uploads/"+partUrl;
				 System.out.println("sound URL:"+soundUrl);
				 URL url = new URL(soundUrl);
		 		 URLConnection conn = url.openConnection();
		  		 HttpURLConnection httpConn = (HttpURLConnection)conn;
		  		 httpConn.setDoInput(true);
		  		 httpConn.setRequestProperty("Connection", "Keep-Alive");
		  		 InputStream inputStream = httpConn.getInputStream();
		  		 String filename="/sdcard/AnyNote/" + partUrl;
		  		 File SpicyDirectory = new File("/sdcard/AnyNote/"+folder+"/");
				    if (!SpicyDirectory.exists()) {
				         SpicyDirectory.mkdirs();
			        }
		  		 File f=new File(filename);
		  		 OutputStream out=new FileOutputStream(f);
			  	 byte buf[]=new byte[1024];
			  	 int len;
			  	 while((len=inputStream.read(buf))>0)
			  		 out.write(buf,0,len);
			  	 out.close();
			  	 inputStream.close();
			  	 System.out.println("\nFile is created....");
		  	 

			 }catch(Exception e){
		       		e.printStackTrace();
		     } 
	
		 System.out.println("Download sound End!!!");
	 }	 	
	 
	 public String uploadSound(String sFilename){
		   	try{
	       		System.out.println("Upload sound Start!!!");
	       	    String strImgPath = "/sdcard/AnyNote/";
	       	    File file = new File(strImgPath+sFilename);
	       	    FileInputStream fin = new FileInputStream(file);
	       	    byte fileContent[] = new byte[(int)file.length()];
	       	    fin.read(fileContent);
	       		
	       		
		 
		       	 
		       	final String BOUNDARY   = "---------------------------265001916915724";
		       	final String HYPHENS    = "--";
		       	final String CRLF       = "\r\n";
		       	URL url                 = new URL(serverUrl+"uploadservlet"+"?id="+AnyNoteActivity.fbId);    
		       	HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
		       	conn.setRequestMethod("POST");                      // method一定要是POST
		       	conn.setDoOutput(true);
		       	conn.setDoInput(true);
		       	conn.setUseCaches(false);   

		       	// 把Content Type設為multipart/form-data
		       	// 以及設定Boundary，Boundary很重要!
		       	// 當你不只一個參數時，Boundary是用來區隔參數的   

		        conn.setRequestProperty("Connection", "Keep-Alive");
		        conn.setRequestProperty("referer", serverUrl);
		        conn.setRequestProperty("Connection", "Keep-Alive");
		        //conn.setRequestProperty("Charset", "UTF-8");
		       	conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
		
		
		       	// 下面是開始寫參數
		       	String strContentDisposition = "Content-Disposition: form-data; name=\"filename\"; filename=\""+sFilename+"\"";
		       	String strContentType = "application/octet-stream";
		       	DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());
		       	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);       // 寫BOUNDARY
		       	dataOS.writeBytes(strContentDisposition+CRLF);  // 寫(Disposition)
		       	dataOS.writeBytes(strContentType+CRLF);            // 寫(Content Type)
		       	dataOS.writeBytes(CRLF);        
		       	
		       	int iBytesAvailable = 1024;
		       	byte[] byteData = new byte[iBytesAvailable];
		       	//int iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
		       	int x=0;
		       	while (x < fileContent.length) {
		       		if(fileContent.length-x<iBytesAvailable){
		       			dataOS.write(fileContent, x, (fileContent.length-x)); // 開始寫內容
		       			x+=(fileContent.length-x);
		       		}
		       		else{
			       	    dataOS.write(fileContent, x, iBytesAvailable); // 開始寫內容
			       	    x+=iBytesAvailable;
		       		}
		       	    //iBytesAvailable = fileInputStream.available();
		       	    //iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
		       	}
		 
		 
		       	dataOS.writeBytes(CRLF);    
		       	dataOS.writeBytes(HYPHENS+BOUNDARY+HYPHENS+CRLF);    // (結束)寫--==================================--      
		       	
		       	dataOS.flush();
		       	dataOS.close();
		       	//fileInputStream.close();
		       	
		        InputStream stream = conn.getInputStream();
		        BufferedInputStream in = new BufferedInputStream(stream,8192);
		        int i = 0; 
		        String SoundUrl = "";
		        while ((i = in.read()) != -1) {
		        	System.out.print(i); 
		        	if(i==1){
			        	SoundUrl=serverIp+"/fileup/uploads/"+sFilename;
			        break;
			        }
		        }  
		        in.close();
		        if(SoundUrl.length()>0){
		        	System.out.println("Upload Sound End!!!");
		        }
		        return SoundUrl;
	       	}catch(Exception e){
	       		e.printStackTrace();
	       	}
	       	return ""; 
	    }
}
