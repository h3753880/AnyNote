package anynote.client.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;

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
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import anynote.client.android.Alarm.CallAlarm;
import anynote.client.classes.TimeNote;

public class ModifyActivity extends Activity {
	private ToDoDB myToDoDB;
	private String friends="";
    private Button timeSetButton;
    private Button okButton;
    private Button cancelButton;
    private Button friendSetButton;
    private int _id;
    private TextView timeSet;
    private String userId;
    private int noteId;
    private TextView txt;
    private TextView txt2;
    private String time;
    private Spinner spinner;
    //private static Calendar dateTime=Calendar.getInstance();
    private EditText titleText;
    private EditText contentText;
    private Calendar c=Calendar.getInstance();
    private int year=c.get(Calendar.YEAR);
    private int month=c.get(Calendar.MONTH);
    private int day=c.get(Calendar.DAY_OF_MONTH);
    private int hour=c.get(Calendar.HOUR_OF_DAY);
    private int minute=c.get(Calendar.MINUTE);
    private String img;
	public void onCreate(Bundle savedInstanceState) {
	       super.onCreate(savedInstanceState);
	       setContentView(R.layout.timenote);
	       
	      
	       findView();
	    		   
	       Bundle text=this.getIntent().getExtras();
	        _id=text.getInt("_id");
	        System.out.println("_id:"+_id);
	        noteId=text.getInt("noteId");
	     
	        userId=text.getString("userId");
	        img=text.getString("img");
	        titleText.setText(text.getString("title"));
	        contentText.setText(text.getString("content"));
	       time=text.getString("time");
	       changeToCalendar(time);
	       setTime();
	        ArrayAdapter<String> adapter = new ArrayAdapter<String>
	        (this,android.R.layout.simple_spinner_item,new String[]{"無","小時","天","周","月"});
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        spinner.setAdapter(adapter);
	        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
	        	            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
	        	                Toast.makeText(ModifyActivity.this, adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
	        	            }
	        	            public void onNothingSelected(AdapterView<?> arg0) {
	        	              //Toast.makeText(ModifyActivity.this, , Toast.LENGTH_LONG).show();
	        	            }
	        });
	       /*Intent intent = new Intent(TimeNoteActivity.this, Sync.class);
	       startService(intent);*/
	       
	     
	        myToDoDB = new ToDoDB(this);  
	     
	        
	        timeSetButton.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View v) {
	            	Bundle bundle=new Bundle();
	            	bundle.putString("time",time);
		            	Intent intent= new Intent();
		            	intent.setClass(ModifyActivity.this, ModifySetTimeActivity.class);
		            	intent.putExtras(bundle);
		            	startActivityForResult(intent,1);
	            }
	        });
	        friendSetButton.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View v) {
	               
	            	Toast.makeText(ModifyActivity.this, "無法修改提醒之朋友",
							Toast.LENGTH_SHORT).show();
	            }
	        });

	        okButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	            	  
	          
	            	//int maxId=myToDoDB.maxId();           	
	             
	            	updateTodo();
	            	//cancelAlarm();
	            	//System.out.println("canel");
	    			System.out.println("\nCancel_id:"+_id);
	    			Intent intent = new Intent(ModifyActivity.this, CallAlarm.class);
	    			 PendingIntent sender=PendingIntent.getBroadcast(getApplicationContext(),
	    					 _id, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
	    			 AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
	    	
	                if(spinner.getSelectedItem().toString()=="無"){   
	               
		                c.setTimeInMillis(System.currentTimeMillis());
		                c.set(Calendar.YEAR,year);
		                c.set(Calendar.MONTH,month);
		                c.set(Calendar.DAY_OF_MONTH,day);
		                c.set(Calendar.HOUR_OF_DAY,hour);
		                c.set(Calendar.MINUTE,minute);
		                c.set(Calendar.SECOND,0);
		                c.set(Calendar.MILLISECOND,0);
		              
		                Bundle bundle=new Bundle();
		                bundle.putString("name","自己\n");
		                bundle.putString("title",titleText.getText().toString());
		                bundle.putString("content",contentText.getText().toString());
		                bundle.putString("img",img);
		               System.out.println("title:"+titleText.getText().toString());
		               System.out.println("content:"+contentText.getText().toString());
		                intent = new Intent(ModifyActivity.this, CallAlarm.class);
		                intent.putExtras(bundle);
		                System.out.println("setAlarm_id"+_id);
		                sender=PendingIntent.getBroadcast(getApplicationContext(),
		                		_id, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		              
		                am = (AlarmManager)getSystemService(ALARM_SERVICE);
		                am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
		               
		                
		      
		               
		                ModifyActivity.this.finish();
	                }
	                
	               
	                else{
	                	
	                	long times=0;
	                	if(spinner.getSelectedItem().toString()=="小時")times=360;
	                	else if(spinner.getSelectedItem().toString()=="天")times=8640;
	                	else if(spinner.getSelectedItem().toString()=="周")times=60480;
	                	else if(spinner.getSelectedItem().toString()=="月")times=259200;
	        
	 	               
		                c.setTimeInMillis(System.currentTimeMillis());
		                c.set(Calendar.YEAR,year);
		                c.set(Calendar.MONTH,month);
		                c.set(Calendar.DAY_OF_MONTH,day);
		                c.set(Calendar.HOUR_OF_DAY,hour);
		                c.set(Calendar.MINUTE,minute);
		                c.set(Calendar.SECOND,0);
		                c.set(Calendar.MILLISECOND,0);
		                Bundle bundle=new Bundle();
		                bundle.putString("name","自己\n");
		                bundle.putString("img",img);
		                bundle.putString("title",titleText.getText().toString());
		                bundle.putString("content",contentText.getText().toString());
		
		                intent = new Intent(ModifyActivity.this, CallAlarm.class);
		                intent.putExtras(bundle);
		  
		                sender=PendingIntent.getBroadcast(getApplicationContext(),
		                		_id, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
		               
		                am = (AlarmManager)getSystemService(ALARM_SERVICE);
		                am.setRepeating(AlarmManager.RTC_WAKEUP,
		                         c.getTimeInMillis(),times*1000,sender);
		           
		       
		               // String tmpS=format(time.getCurrentHour())+"bb"+format(time.getCurrentMinute()); 
		                //BrowseActivity.list = myToDoDB.timeSelect();  

		                //Toast.makeText(ModifyActivity.this,times+"ya",Toast.LENGTH_SHORT)
		                  //.show(); 
		                Intent i=new Intent();
		                ModifyActivity.this.setResult(0, i);
		                ModifyActivity.this.finish();
	                }  
	               
	            }
	            
	        });       
	      
	        cancelButton.setOnClickListener(new View.OnClickListener() {
	            public void onClick(View v) {
	       
	            	ModifyActivity.this.finish();
	            }
	        });
	        
	        
		}
	
	    public void cancelAlarm()
		{	
			System.out.println("Cancel_id:"+_id);
			Intent intent = new Intent(ModifyActivity.this, CallAlarm.class);
			 PendingIntent sender=PendingIntent.getBroadcast(getApplicationContext(),
					 _id, intent, 0);
			 AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
			 am.cancel(sender);
	
		}
	    
	    private void updateTodo()
	    {
	    
	    	TimeNote note = new TimeNote();
	    	if (titleText.getText().toString().equals(""))return;
	    	else{
	    		
	    		  note.title=titleText.getText().toString();
			      note.content=contentText.getText().toString();	     
			      note.setTime(year, month, day, hour, minute, 0);
			      note.cycle=spinner.getSelectedItemPosition();
	    	}	
	    	myToDoDB.updateTimeNote(_id, note);
	    	modifyUpdate(note);
	    	
	    }
	  private void modifyUpdate(TimeNote note)
	  {
		  System.out.println("deleteTimeNoteupload");
			HttpPost hp = new HttpPost("http://140.121.197.102:8088/anynote/ModifyTimeNote");
			HttpClient client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 2000);
			HttpConnectionParams.setSoTimeout(httpParams, 3000);
			JSONObject sender=new JSONObject();
			try {
				sender.put("userId", userId);
				sender.put("noteId",noteId);
				sender.put("title", note.title);
				sender.put("content", note.content);
				sender.put("time", note.time);
				sender.put("cycle", note.cycle);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			System.out.print(sender.toString());
			//sender.put("noteType",viewType);
			//System.out.println("userId:"+userId);
			//System.out.println("noteId:"+myCursor.getInt(1));
			//array.put(jo1);
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

	    private String format(int x)
	    {
	      String s=""+x;
	      if(s.length()==1) s="0"+s;
	      return s;
	    }    
	    
	    private void findView()
	    {
	        txt=(TextView)findViewById(R.id.textView1);
	        txt2=(TextView)findViewById(R.id.textView2);
	        friendSetButton = (Button) findViewById(R.id.button1);
	        timeSet=(TextView)findViewById(R.id.textView5);
	        //time=(TimePicker)findViewById(R.id.timePicker1);
	        //date=(DatePicker)findViewById(R.id.datePicker1);      
	        spinner = (Spinner) findViewById(R.id.timespinner);
	        titleText=(EditText)findViewById(R.id.editText1);
	        contentText=(EditText)findViewById(R.id.editText2);
	        timeSetButton = (Button) findViewById(R.id.button4);
	        okButton=(Button)findViewById(R.id.button2);
	        cancelButton=(Button)findViewById(R.id.button3); 
	        
	       
	    }
	    private void setTime(){
	    	String timeString="";
	    	timeString=timeString+Integer.toString(year)+"年"+Integer.toString(month+1)+"月"+Integer.toString(day)+"日    "
	               	+Integer.toString(hour)+":"+Integer.toString(minute);   	
	               	timeSet.setText(timeString);	
	    }
	    @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	               super.onActivityResult(requestCode, resultCode, data);
	               try{
		               switch(requestCode){
		              
		               case 1:
			               	/*輸出時間到頁面上*/
		            	    Bundle bundle=data.getExtras();
			               	year=bundle.getInt("year");
			               	month=bundle.getInt("month");
			               	day=bundle.getInt("day");
			               	
			               	hour=bundle.getInt("hour");
			               	minute=bundle.getInt("minute");
			               	setTime();
			               	break;
		               }
	               }
	               catch(Exception e)
	               {
	            	   e.printStackTrace();
	               }
	    }

	    private void changeToCalendar(String time)
		{
			String[] setTime=time.split("-");

			year=Integer.parseInt(setTime[0]);
			month=Integer.parseInt(setTime[1]);
			day=Integer.parseInt(setTime[2]);
			hour=Integer.parseInt(setTime[3]);
			minute=Integer.parseInt(setTime[4]);

			
			
		}
	       
}

