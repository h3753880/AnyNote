package anynote.client.android;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import anynote.client.android.Alarm.CallAlarm;
import anynote.client.android.Alarm.GeoDetect;

public class ReSetService extends Service {
    private Calendar c=Calendar.getInstance();//現在時間
    private Calendar dateTime=Calendar.getInstance();//比較時間
    private ToDoDB db=new ToDoDB(this);
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onStart(Intent intent, int startId){
		super.onStart(intent, startId);
		ArrayList<Map<String, Object>> timeNote=db.timeSelect();
		ArrayList<Map<String, Object>> geoNote=db.geoSelect();
		for (Map<String, Object> map : geoNote)
		{
			Calendar startTime=changeToCalendarGeo(map.get("timeStart").toString());
			int result1=startTime.compareTo(Calendar.getInstance());
			Calendar finishTime=changeToCalendarGeo(map.get("timeEnd").toString());
			int result2=finishTime.compareTo(Calendar.getInstance());
			if(result1<=0 && result2>=0)
			{
				Intent i=new Intent();
				Bundle bundle=new Bundle();
				bundle.putInt("status",1);
				bundle.putDouble("latitude",Double.parseDouble(map.get("latitude").toString()));
				bundle.putDouble("longitude",Double.parseDouble(map.get("longitude").toString()));
				bundle.putString("title", map.get("title").toString());
				bundle.putString("content", map.get("content").toString());
				bundle.putDouble("distance", Double.parseDouble(map.get("range").toString()));
				bundle.putString("userId", map.get("userId").toString());
				bundle.putInt("ID", Integer.parseInt(map.get("_id").toString()));
				bundle.putInt("year", startTime.getTime().getYear()+1900);
				bundle.putInt("month", startTime.getTime().getMonth());
				bundle.putInt("day", startTime.getTime().getDate());
				bundle.putInt("hour", startTime.getTime().getHours());
				bundle.putInt("minute", startTime.getTime().getMinutes());
				bundle.putInt("year2", finishTime.getTime().getYear()+1900);
				bundle.putInt("month2", finishTime.getTime().getMonth());
				bundle.putInt("day2", finishTime.getTime().getDate());
				bundle.putInt("hour2", finishTime.getTime().getHours());
				bundle.putInt("minute2", finishTime.getTime().getMinutes());
				if(map.get("getIn").toString().equals("1"))
					bundle.putBoolean("checkboxIn",true);
				else
					bundle.putBoolean("checkboxIn",false);
				if(map.get("getIn").toString().equals("1"))
					bundle.putBoolean("checkboxOut",true);
				else
					bundle.putBoolean("checkboxOut",false);
				i.putExtras(bundle);
				i.setClass(ReSetService.this, GeoDetect.class);
				startService(i);
			}
		}
		for (Map<String, Object> map : timeNote) {
				changeToCalendar(map.get("time").toString());
				if(dateTime.compareTo(c)>=0){
	                /*傳送title與content*/
	                Bundle bundle=new Bundle();
	                int _id=Integer.parseInt(map.get("_id").toString());
	                
	                String name=db.searchFriendName(map.get("userId").toString());
	                if(name.equals("0")){
	                	bundle.putString("name","自己\n");
	                }else{
	                	bundle.putString("name",name);
	                }
	                bundle.putString("title",map.get("title").toString());
	                bundle.putString("content",map.get("content").toString());
	                bundle.putString("img",map.get("img").toString());
					 /* 指定鬧鐘設定時間到時要執行CallAlarm.class */
	                Intent i = new Intent(this, CallAlarm.class);
	                i.putExtras(bundle);

	                /* 建立PendingIntent */ 
	                PendingIntent sender=PendingIntent.getBroadcast(getApplicationContext(),
	                			_id, i,  PendingIntent.FLAG_UPDATE_CURRENT);
	                /* AlarmManager.RTC_WAKEUP設定服務在系統休眠時同樣會執行
	                 * 以set()設定的PendingIntent只會執行一次
	                 * */

	                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);	                
	                
	                int cycle=Integer.parseInt(map.get("cycle").toString());
	                if(cycle==0){

		                am.set(AlarmManager.RTC_WAKEUP, dateTime.getTimeInMillis(), sender);	                	
	                }else{
	                	int times=0;
	                	if(cycle==1)times=360;
	                	else if(cycle==2)times=8640;
	                	else if(cycle==3)times=60480;
	                	else if(cycle==4)times=259200;

		                am.setRepeating(AlarmManager.RTC_WAKEUP,
		                		dateTime.getTimeInMillis(),times*1000,sender);
	                }
				}
					

			}
		
		
	}
	private void changeToCalendar(String time)//��啁�敶Ｘ�頧�calendar
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
	private Calendar changeToCalendarGeo(String time)//��啁�敶Ｘ�頧�calendar
	{
		String[] setTime=time.split("-");
		//System.out.print(time);
		//Calendar date = null ;
		Calendar cal=Calendar.getInstance();
       cal.setTimeInMillis(System.currentTimeMillis());
       cal.set(Calendar.YEAR,Integer.parseInt(setTime[0]));
       cal.set(Calendar.MONTH,Integer.parseInt(setTime[1]));
       cal.set(Calendar.DAY_OF_MONTH,Integer.parseInt(setTime[2]));
       cal.set(Calendar.HOUR_OF_DAY,Integer.parseInt(setTime[3]));
       cal.set(Calendar.MINUTE,Integer.parseInt(setTime[4]));
       cal.set(Calendar.SECOND,0);
       cal.set(Calendar.MILLISECOND,0);
        
       return cal;
		
		
	}
}
