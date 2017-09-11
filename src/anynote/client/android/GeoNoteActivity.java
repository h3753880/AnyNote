package anynote.client.android;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Calendar;


import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import anynote.client.android.Alarm.CallAlarm;
import anynote.client.android.Alarm.GeoDetect;
import anynote.client.classes.GeoNote;
import anynote.client.classes.TimeNote;

public class GeoNoteActivity extends Activity {
	private CheckBox checkboxIn;
	private CheckBox checkboxOut;
	private Button geoSetButton;
	private Button okButton;
	private Button cancelButton;
	private Button startTime;
	private Button friendButton;
    private Button photoButton;
    private Button soundButton;
	private EditText title;
	private EditText content;
	private TextView showStartTime;
	private TextView showEndTime;
	private Spinner range;
	private double distance=200;//�����d��
	private double tempLongitude=0;// �O�������I
	private double tempLatitude=0;
    private File sOut = null; 
	private ToDoDB myToDoDB;
    private GeoNote note;
    private MediaRecorder mRecorder;
	private int year;
	private int month;
	private int day;
	private int hour;
	private int minute;
	private int year2;
	private int month2;
	private int day2;
	private int hour2;
	private int minute2;
	private String city="";
	private String friends="";
    private int recording=0;
    String strImgPath;
    String filename;
    String sFilename;
    public ProgressDialog myDialog = null;
    private String favPlace="";

	// ���o�����I(FB�n�� 4����  �qGeoRemindPoint 1����   GeoTimeActivity 2 ����)
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		try{
			if (resultCode == 4){
				friends=data.getExtras().getString("friends");
                String fNames=data.getExtras().getString("friendName");
        	   	Toast.makeText(this, "�A��ܤF"+fNames, Toast.LENGTH_LONG).show();
			}
			else if (resultCode == 1){
				tempLongitude = data.getExtras().getDouble("remindPointLo");
				tempLatitude = data.getExtras().getDouble("remindPointLa");
				favPlace = data.getExtras().getString("favPlace");
				System.out.println("�߷R�I�^��:"+favPlace);
			}
			else if(resultCode == 2){
				year=data.getExtras().getInt("year");
				month=data.getExtras().getInt("month");
				day=data.getExtras().getInt("day");
				hour=data.getExtras().getInt("hour");
				minute=data.getExtras().getInt("minute");
				showStartTime.setText("�}�l�ɶ�:"+setTime(year,month,day,hour,minute));
				//Log.v("testtime", Integer.toString(year));
				year2=data.getExtras().getInt("year2");
				month2=data.getExtras().getInt("month2");
				day2=data.getExtras().getInt("day2");
				hour2=data.getExtras().getInt("hour2");
				minute2=data.getExtras().getInt("minute2");
				showEndTime.setText("�����ɶ�:"+setTime(year2,month2,day2,hour2,minute2));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geonote);
	    /* ���oDataBase�̪���� */ 
		myToDoDB = new ToDoDB(this);
		
		
		/* ���o�U����View */
		findView();
		/*�]�w�ɶ����s�����ƥ�*/
		startTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(GeoNoteActivity.this, GeoTimeActivity.class);
				GeoNoteActivity.this.startActivityForResult(intent, 2);//�I�s�]�w�ɶ�����
			}
		});
		
		/* �]�w�����I���s���U�����ƥ� */
		geoSetButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(GeoNoteActivity.this, GeoRemindPoint.class);
				GeoNoteActivity.this.startActivityForResult(intent, 1);// �I�s�]�w�����I����
			}
		});
		/* �T�w�s�W���s���U�����ƥ� */
		okButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				
				if(tempLongitude==0&&tempLatitude==0)
					Toast.makeText(GeoNoteActivity.this, "�|����ܴ����I", Toast.LENGTH_SHORT).show();
				else if(year==0)
					Toast.makeText(GeoNoteActivity.this, "�|����ܴ����ɶ��d��", Toast.LENGTH_SHORT).show();
				else if(!checkboxIn.isChecked() && !checkboxOut.isChecked() )
					Toast.makeText(GeoNoteActivity.this, "�i�J�ɴ��������}�ɴ����ܤֻݤĿ�@��", Toast.LENGTH_SHORT).show();
				else
				{
					int noteId=AnyNoteActivity.mPrefs.getInt("geoNote", 1);//�s�@noteID
					/*�s�W������Ʈw*/     
					try {
						city=getCity(tempLatitude,tempLongitude);//�g�n����a�}(city)
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}      
		          	boolean friendAlarm=false;
		            if(friends!=""){
		            	  friendAlarm=true;
		            }
	            	addToDo(noteId);
	            	int _id;
	            	_id=myToDoDB.geoNoteAlarmId(note);
	            	if(!friendAlarm){
						Intent intent = new Intent();
						Bundle bundle = new Bundle();
						//bundle.putInt("noteId",noteId);
						bundle.putString("userId", AnyNoteActivity.fbId);
						bundle.putInt("ID", _id);
						bundle.putString("title", title.getText().toString());
						bundle.putString("content", content.getText().toString());
						bundle.putDouble("longitude", tempLongitude);
						bundle.putDouble("latitude", tempLatitude);
						bundle.putDouble("distance", distance);
						bundle.putString("city", city);
						
						bundle.putInt("year", year);
						bundle.putInt("month", month);
						bundle.putInt("day", day);
						bundle.putInt("hour", hour);
						bundle.putInt("minute",minute);
						bundle.putInt("year2", year2);
						bundle.putInt("month2", month2);
						bundle.putInt("day2", day2);
						bundle.putInt("hour2", hour2);
						bundle.putInt("minute2", minute2);
						
						bundle.putBoolean("checkboxIn", checkboxIn.isChecked());
						bundle.putBoolean("checkboxOut", checkboxOut.isChecked());
						bundle.putInt("status", 1);
						Log.v("geoDetect", "service");
						intent.putExtras(bundle);
						intent.setClass(GeoNoteActivity.this, GeoDetect.class);
						startService(intent);
	            	}
	            	
					GeoNoteActivity.this.finish();
				}
			}
		});
		/* �����s�W���s���U�����ƥ� */
			cancelButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					GeoNoteActivity.this.finish();
				}
		});
	       photoButton.setOnClickListener(new View.OnClickListener() {
	        	 @Override
	 			public void onClick(View v) {
	        		int noteId=AnyNoteActivity.mPrefs.getInt("geoNote", 1);//�s�@noteID
	             	Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
	                strImgPath = "/sdcard/AnyNote/";//�s��Ӥ������
	                String userId="0";
	                if(AnyNoteActivity.facebook.isSessionValid()) {userId=AnyNoteActivity.fbId;}
	                
	                filename = "geo_"+noteId+ ".jpg";//�Ӥ��R�W
	                File out = new File(strImgPath);
	                if (!out.exists()) {
	                        out.mkdirs();
	                }
	                strImgPath = strImgPath + filename;//�ӷӤ���������|
	                out = new File(strImgPath);
	                Uri uri = Uri.fromFile(out);
	                imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
	                startActivityForResult(imageCaptureIntent, 3); 	
	             }
	         });
	        soundButton.setOnClickListener(new View.OnClickListener() {
	          	 @Override
	   			public void onClick(View v) {
	          		 	int noteId=AnyNoteActivity.mPrefs.getInt("timeNote", 1);//�s�@noteID
	          		 	strImgPath = "/sdcard/AnyNote/";//�s����������
	          		 	String userId="0";
	          		 	if(AnyNoteActivity.facebook.isSessionValid()) {userId=AnyNoteActivity.fbId;}
	          		 	
	          		 try  
	                {  
	          			
	          			if(recording==0){
	          		    Toast.makeText(GeoNoteActivity.this,"�}�l�����A�A���I�����s��������",Toast.LENGTH_SHORT).show();
	   	       			strImgPath = "/sdcard/AnyNote/";              
	   	       			sFilename = "geo_"+noteId+ ".amr";
	   	       			sOut = new File(strImgPath,sFilename);  
	   	       			mRecorder = new MediaRecorder();         
	   	       			mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);         
	   	       			mRecorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);         
	   	       			mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);               
	   	       			mRecorder.setOutputFile(sOut.getAbsolutePath());         
	   	       			mRecorder.prepare();  
	   	       			mRecorder.start(); 
	   	       			soundButton.setText("������...");
	   	       			recording=1;
	          			}else if(recording==1){
	          		       	Toast.makeText(GeoNoteActivity.this,"��������",Toast.LENGTH_SHORT).show();
	          				mRecorder.stop();  
	          				mRecorder.release();  	         
	          				mRecorder = null;  	
	          				soundButton.setText("����");
	          				recording=2;
	          			}else{
	          				openFile(sOut); 
	          			}

	                } catch (IOException e){  
	                  // TODO Auto-generated catch block  
	                  e.printStackTrace();  
	                }  
	          		 	
	          		 	
	               }
	           });      
	       
	       
		/* ��ܪB�� */
		friendButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {     
	            	if(AnyNoteActivity.facebook.isSessionValid()) {            
	            		myDialog = ProgressDialog.show(GeoNoteActivity.this,
	        					"", "loading...", true);
	        			new Thread() {
	        				public void run() {
	        					try {
	        						sleep(2000);
	        					} catch (Exception e) {
	        						e.printStackTrace();
	        					} finally {

	        						myDialog.dismiss();
	        					}
	        				}
	        			}.start();
			            	Intent intent= new Intent();
			            	intent.setClass(GeoNoteActivity.this, Friend_list.class);			            	
			            	GeoNoteActivity.this.startActivityForResult(intent,4);
			            
	            	 }else{
	            		Toast.makeText(GeoNoteActivity.this, "�Э��s�n�J��A�ϥΦ��\��", Toast.LENGTH_LONG).show();
	            	}
			}
		});
	}
	private void addToDo(int id)
    {		
    	/*�إ�GeoNote�ñN��Ʀs�J*/
    	note = new GeoNote();
    	if (title.getText().toString().equals(""))return;
    	else{
			  SharedPreferences.Editor PE = AnyNoteActivity.mPrefs.edit();
              note.noteId=id;
    		
              if(AnyNoteActivity.facebook.isSessionValid()) {note.userId=AnyNoteActivity.fbId;}
              else note.userId="0";
              if(friends!=""){
            	  friends=note.userId+"_"+friends;
              }else{
            	  friends=note.userId+"_";
              }
          	  System.out.println("f:"+friends);
              note.friends=friends;
		      note.title=title.getText().toString();
		      note.content=content.getText().toString();	  
		      note.range=distance;
		      note.Latitude=tempLatitude;
		      note.Longitude=tempLongitude;
		      note.setTime(year, month, day, hour, minute, 0);
		      note.setEndTime(year2, month2, day2, hour2, minute2, 0);
		      note.getIn=checkboxIn.isChecked();
		      note.getOut=checkboxOut.isChecked();
		      note.city=city;
		      note.setFavPlace(favPlace);
		      System.out.println("�߷R�I�s�J���:"+favPlace);
		      if(filename!=null){
		    	  note.img=filename;
		      }else{
		    	  note.img="0";
		      }
		      if(sFilename!=null){
		    	  note.sound=sFilename;
		      }else{
		    	  note.sound="0";
		      }
		      myToDoDB.insertGeoNote(note);
	          PE.putInt("geoNote", id+1);
	          PE.commit();//�x�snoteID    
    	}
      /*�s�JDB*/	
           
    }        
	
	  private void openFile(File f)  
	    {  
	      Intent intent = new Intent();  
	      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	      intent.setAction(android.content.Intent.ACTION_VIEW);  
	      String type = getMIMEType(f);  
	      intent.setDataAndType(Uri.fromFile(f), type);  
	      startActivity(intent);  
	    }  
	    private String getMIMEType(File f)  
	    {  
	      String end = f  
	          .getName()  
	          .substring(f.getName().lastIndexOf(".") + 1,  
	              f.getName().length()).toLowerCase();  
	      String type = "";  
	      if (end.equals("mp3") || end.equals("aac") || end.equals("aac")  
	          || end.equals("amr") || end.equals("mpeg")  
	          || end.equals("mp4"))  
	      {  
	        type = "audio";  
	      } else if (end.equals("jpg") || end.equals("gif")  
	          || end.equals("png") || end.equals("jpeg"))  
	      {  
	        type = "image";  
	      } else  
	      {  
	        type = "*";  
	      }  
	      type += "/*";  
	      return type;  
	    }  
	
    private String setTime(int year,int month,int day,int hour,int minute){
    	String timeString="";
    	timeString=timeString+Integer.toString(year)+"�~"+Integer.toString(month+1)+"��"+Integer.toString(day)+"��    "
               	+Integer.toString(hour)+":"+Integer.toString(minute);   	
        return timeString;
    }
	private void findView() {
		checkboxIn = (CheckBox) findViewById(R.id.checkBoxIn);
		checkboxOut = (CheckBox) findViewById(R.id.checkBoxOut);
		checkboxIn.setChecked(true);
		checkboxOut.setChecked(false);
		startTime = (Button) findViewById(R.id.button5);
		geoSetButton = (Button) findViewById(R.id.button4);
		okButton = (Button) findViewById(R.id.button2);
		cancelButton = (Button) findViewById(R.id.button3);
		friendButton = (Button) findViewById(R.id.friends);
		title = (EditText) findViewById(R.id.editTitle);
		content = (EditText) findViewById(R.id.editContent);
		showStartTime= (TextView) findViewById(R.id.startTime);
		showEndTime= (TextView) findViewById(R.id.endTime);
        photoButton=(Button)findViewById(R.id.photo); 
        soundButton=(Button)findViewById(R.id.sound); 
		/*�����d��]�w(spinner)*/
		range = (Spinner) findViewById(R.id.chooseRange);
		int[] rangeItem=getResources().getIntArray(R.array.chooseRange);
		ArrayList<Integer> list=new ArrayList<Integer>();
		for(int item:rangeItem)
			list.add(item);
		ArrayAdapter<Integer> adapter
			=new ArrayAdapter<Integer>(this,android.R.layout.simple_spinner_dropdown_item,list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		range.setAdapter(adapter);
		range.setSelection(0);
		range.setPrompt("�����d��(����)");
		range.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
		
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
				// TODO Auto-generated method stub
				int temp=(Integer) adapterView.getSelectedItem();
				distance=(double)temp;
				//Toast.makeText(GeoNoteActivity.this, "�z���"+distance, Toast.LENGTH_LONG).show();
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
			
		});
	}

	// �^�����}�s�@�g�n���ഫ�a�}
	public static String getCity(double latitude, double longitude)
			throws IOException, MalformedURLException {
		URL google = new URL("http://maps.google.com.tw/maps?q=" + latitude
				+ "," + longitude);
		URLConnection uc = google.openConnection();
		// Establish connection to Google Map
		BufferedReader in = new BufferedReader(new InputStreamReader(
				uc.getInputStream(),"big5"));
		String inputLine;
		// For saving the retrieve HTML
		StringBuffer sb = new StringBuffer();
		String pageContent = "";

		// Collect the HTML content
		while ((inputLine = in.readLine()) != null) {
			sb.append(inputLine);
		}
		in.close();
		pageContent = sb.toString();
		// System.out.println(pageContent);

		String startMarkOne = "pp-headline-item";
		String startMarkTwo = "<span class=\"pp-place-title\">";
		String innerStartMark = "<span>";
		String endMark = "</span>";

		int startIndex = 0;
		if (pageContent.indexOf(startMarkOne) != -1)
			startIndex = pageContent.indexOf(startMarkOne)
					+ startMarkOne.length();
		else
			startIndex = pageContent.indexOf(startMarkTwo)
					+ startMarkTwo.length();

		startIndex = pageContent.indexOf(innerStartMark, startIndex)
				+ innerStartMark.length();
		int endIndex = pageContent.indexOf(endMark, startIndex);

		String address = pageContent.substring(startIndex, endIndex);

		return address.substring(3, 6);
	}
	
	
}