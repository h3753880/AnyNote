package anynote.client.android;

import java.io.File;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;
import anynote.client.classes.TimeNote;

public class GeoTimeActivity extends Activity {
	private Button buttonOk;
	private Button buttonCancel;
	private TimePicker time;
    private DatePicker date;
    private TimePicker time2;
    private DatePicker date2;
    private int status;
    private String timeStart;
    private String timeEnd;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_geo_time);
		findView();
		try{
			Bundle bundleReceive=this.getIntent().getExtras();
			status=bundleReceive.getInt("status");
			timeStart=bundleReceive.getString("timeStart");
			timeEnd=bundleReceive.getString("timeEnd");
			setClock(timeStart,timeEnd);
		}catch(Exception e)
		{e.printStackTrace();}
		
		/* 取得各元件的View */
		
		
        //按下確定鍵
        buttonOk.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
		        
		        Intent intent=new Intent();   
				Bundle bundle=new Bundle();
				bundle.putInt("year", date.getYear());
				bundle.putInt("month", date.getMonth());
				bundle.putInt("day", date.getDayOfMonth());
				bundle.putInt("hour", time.getCurrentHour());
				bundle.putInt("minute",time.getCurrentMinute());
				bundle.putInt("year2", date2.getYear());
				bundle.putInt("month2", date2.getMonth());
				bundle.putInt("day2", date2.getDayOfMonth());
				bundle.putInt("hour2", time2.getCurrentHour());
				bundle.putInt("minute2",time2.getCurrentMinute());
				
				intent.putExtras(bundle);
				
				if(status==5)//修改
					GeoTimeActivity.this.setResult(5,intent);
				else if(status==0)//新增
					GeoTimeActivity.this.setResult(2,intent);
				
				GeoTimeActivity.this.finish();
			}
		});
        //按下取消鍵
        buttonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {	
				GeoTimeActivity.this.finish();
			}
		});
	}
	
	
	private void setClock(String timeStart,String timeEnd)
	{
		Log.v("setTime", timeStart);
		Log.v("setTimeE", timeEnd);
		String[] setTimeE=timeEnd.split("-");
		String[] setTime=timeStart.split("-");
		Log.v("String11",setTimeE[0]+","+setTime[0]);
		date2.init(Integer.parseInt(setTimeE[0]), 
	        		Integer.parseInt(setTimeE[1]), Integer.parseInt(setTimeE[2]), 
	        		new DatePicker.OnDateChangedListener() {
				
				@Override
				public void onDateChanged(DatePicker view, int year, int monthOfYear,
						int dayOfMonth) {
						
				}
	    });
		
        date.init(Integer.parseInt(setTime[0]), 
        		Integer.parseInt(setTime[1]), Integer.parseInt(setTime[2]), 
        		new DatePicker.OnDateChangedListener() {
			
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
					
			}
		});
        this.time.setCurrentHour(Integer.parseInt(setTime[3]));
        this.time.setCurrentMinute(Integer.parseInt(setTime[4]));	 
        this.time2.setCurrentHour(Integer.parseInt(setTimeE[3]));
        this.time2.setCurrentMinute(Integer.parseInt(setTimeE[4]));
        System.out.print("setTime");
	}
	

	
	public void findView()
	{
		buttonOk=(Button)findViewById(R.id.buttonOK);
		buttonCancel=(Button)findViewById(R.id.buttonCANCEL);
		time=(TimePicker)findViewById(R.id.timePicker1);
        date=(DatePicker)findViewById(R.id.datePicker1);
        time2=(TimePicker)findViewById(R.id.timePicker2);
        date2=(DatePicker)findViewById(R.id.datePicker2);
       
	}
}