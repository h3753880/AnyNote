package anynote.client.android;


import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;
import android.app.Activity;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import anynote.client.android.Alarm.CallAlarm;
import anynote.client.classes.*;

public class TimeNoteActivity extends Activity {
	private String friends="";
	private ToDoDB myToDoDB;
	private Cursor myCursor;
    private Button timeSetButton;
    private Button okButton;
    private Button cancelButton;
    private Button friendSetButton;
    private Button photoButton;
    private Button soundButton;
    private TextView timeSet;
    private TextView txt;
    private TextView txt2;
    private Spinner spinner;
    private EditText titleText;
    private EditText contentText;
    private TimeNote note;
    private MediaRecorder mRecorder;
    private Calendar c=Calendar.getInstance();
    private File sOut = null;  
    private int year=c.get(Calendar.YEAR);
    private int month=c.get(Calendar.MONTH);
    private int day=c.get(Calendar.DAY_OF_MONTH);
    private int hour=c.get(Calendar.HOUR_OF_DAY);
    private int minute=c.get(Calendar.MINUTE);
    private String strImgPath="";
    private String filename="";
    private String sFilename="";
    private int recording=0;
    public ProgressDialog myDialog = null;
    /** Called when the activity is first created. */
    @Override
	public void onCreate(Bundle savedInstanceState) {
       super.onCreate(savedInstanceState);

       setContentView(R.layout.timenote);
       
   
   
 

       /* ���o�U����View */ 
       findView();
       setTime();
       /* ���oDataBase�̪���� */ 
        myToDoDB = new ToDoDB(this);
        /*�]�wSpinner��� */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
        (this,android.R.layout.simple_spinner_item,new String[]{"�L","�p��","��","�P","��"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
        	            @Override
						public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
        	                //Toast.makeText(TimeNoteActivity.this, "�z���"+adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
        	            }
        	            @Override
						public void onNothingSelected(AdapterView<?> arg0) {
        	              //Toast.makeText(TimeNoteActivity.this, "�z�S����ܥ��󶵥�", Toast.LENGTH_LONG).show();
        	            }
        });
        /*�]�w�ɶ����s���U�����ƥ�*/
        timeSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
	            	Intent intent= new Intent();
	            	intent.setClass(TimeNoteActivity.this, SetTimeActivity.class);
	            	startActivityForResult(intent,1);
            }
        });
        
        friendSetButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	if(AnyNoteActivity.facebook.isSessionValid()) { 
            		myDialog = ProgressDialog.show(TimeNoteActivity.this,
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
		            	System.out.println("before");
		            	Intent intent= new Intent();
		            	intent.setClass(TimeNoteActivity.this, Friend_list.class);
		            	//startActivity(intent);
		            	startActivityForResult(intent,4);
		            	System.out.println("after");
            	 }
            	else{
            		Toast.makeText(TimeNoteActivity.this, "�Э��s�n�J��A�ϥΦ��\��", Toast.LENGTH_LONG).show();
            	}
            }
        });
        photoButton.setOnClickListener(new View.OnClickListener() {
        	 @Override
 			public void onClick(View v) {
        		int noteId=AnyNoteActivity.mPrefs.getInt("timeNote", 1);//�s�@noteID
             	Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                strImgPath = "/sdcard/AnyNote/";//�s��Ӥ������
                String userId="0";
                if(AnyNoteActivity.facebook.isSessionValid()) {userId=AnyNoteActivity.fbId;}
                
                filename = "time_"+noteId+ ".jpg";//�Ӥ��R�W
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
      		       	Toast.makeText(TimeNoteActivity.this,"�}�l�����A�A���I�����s��������",Toast.LENGTH_SHORT).show();
	       			strImgPath = "/sdcard/AnyNote/";              
	       			sFilename = "time_"+noteId+ ".amr";
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
       		       	Toast.makeText(TimeNoteActivity.this,"��������",Toast.LENGTH_SHORT).show();
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
        
        /*�T�w�s�W���s���U�����ƥ�*/
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	if(titleText.getText().toString().equals(""))
            		Toast.makeText(TimeNoteActivity.this, "�п�J�������D",
						Toast.LENGTH_SHORT).show();
            	
            	else if(contentText.getText().toString().equals(""))
            		Toast.makeText(TimeNoteActivity.this, "�п�J�������e",
    						Toast.LENGTH_SHORT).show();
            	
            	else{
	          	int noteId=AnyNoteActivity.mPrefs.getInt("timeNote", 1);//�s�@noteID
	          	boolean friendAlarm=false;
	            if(friends!=""){
	            	  friendAlarm=true;
	            }
            	/*�s�W�m��Ʈw*/           
            	addTodo(noteId);
            	if(!friendAlarm){
	            	int _id;
	            	_id=myToDoDB.timeNoteAlarmId(note);
	                /*�s�W�@���ʴ���*/
	                if(spinner.getSelectedItem().toString()=="�L"){   
	                	/*�]�w�����ɶ�*/
		                c.setTimeInMillis(System.currentTimeMillis());
		                c.set(Calendar.YEAR,year);
		                c.set(Calendar.MONTH,month);
		                c.set(Calendar.DAY_OF_MONTH,day);
		                c.set(Calendar.HOUR_OF_DAY,hour);
		                c.set(Calendar.MINUTE,minute);
		                c.set(Calendar.SECOND,0);
		                c.set(Calendar.MILLISECOND,0);
		                /*�ǰetitle�Pcontent*/
		                Bundle bundle=new Bundle();
		                bundle.putString("name","�ۤv\n");
		                bundle.putString("title",titleText.getText().toString());
		                bundle.putString("content",contentText.getText().toString());
		                bundle.putString("img",note.img);
		                bundle.putString("sound",note.sound);
		                /* ���w�x���]�w�ɶ���ɭn����CallAlarm.class */
		                Intent intent = new Intent(TimeNoteActivity.this, CallAlarm.class);
		                intent.putExtras(bundle);
		                /* �إ�PendingIntent */ 
		                PendingIntent sender=PendingIntent.getBroadcast(getApplicationContext(),
		                			_id, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
		                /* AlarmManager.RTC_WAKEUP�]�w�A�Ȧb�t�Υ�v�ɦP�˷|����
		                 * �Hset()�]�w��PendingIntent�u�|����@��
		                 * */
		                System.out.println("newTimeAlarm"+_id);
		                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		                am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
		                /* �HToast���ܳ]�w�w���� */
		                Toast.makeText(TimeNoteActivity.this,"�s�W�Ƨѿ����\",Toast.LENGTH_SHORT)
		                  .show();
		      
	                }
	                
	                /*�s�W�g���ʴ���*/
	                else{
	                	/*�]�w�g������*/
	                	long times=0;
	                	if(spinner.getSelectedItem().toString()=="�p��")times=360;
	                	else if(spinner.getSelectedItem().toString()=="��")times=8640;
	                	else if(spinner.getSelectedItem().toString()=="�P")times=60480;
	                	else if(spinner.getSelectedItem().toString()=="��")times=259200;
	                	/*�]�w�����ɶ�*/
		                c.setTimeInMillis(System.currentTimeMillis());
		                c.set(Calendar.YEAR,year);
		                c.set(Calendar.MONTH,month);
		                c.set(Calendar.DAY_OF_MONTH,day);
		                c.set(Calendar.HOUR_OF_DAY,hour);
		                c.set(Calendar.MINUTE,minute);
		                c.set(Calendar.SECOND,0);
		                c.set(Calendar.MILLISECOND,0);
		                Bundle bundle=new Bundle();
		                bundle.putString("name","�ۤv\n");
		                bundle.putString("title",titleText.getText().toString());
		                bundle.putString("content",contentText.getText().toString());
		                bundle.putString("img",note.img);
		                bundle.putString("sound",note.sound);
		                /* ���w�x���]�w�ɶ���ɭn����CallAlarm.class */
		                Intent intent = new Intent(TimeNoteActivity.this, CallAlarm.class);
		                intent.putExtras(bundle);
		                /* �إ�PendingIntent */       
		                PendingIntent sender=PendingIntent.getBroadcast(getApplicationContext(),
		                			_id, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
		               
		                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		                am.setRepeating(AlarmManager.RTC_WAKEUP,
		                         c.getTimeInMillis(),times*1000,sender);
		       	        /* �HToast���ܳ]�w�w���� */
		                Toast.makeText(TimeNoteActivity.this,"�s�W�Ƨѿ����\�A�����P����"+spinner.getSelectedItem().toString(),Toast.LENGTH_SHORT)
		                  .show(); 
		               
	                }   
            	}
            	TimeNoteActivity.this.finish();
            }
           }
        });       
        /*�����s�W���s���U�����ƥ�*/       
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	TimeNoteActivity.this.finish();
            }
        });
        
        
	}
    
    private void addTodo(int id)
    {
    	/*�إ�TimeNote�ñN��Ʀs�J*/
    	note = new TimeNote();
    	if (titleText.getText().toString().equals(""))return;
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
		      note.title=titleText.getText().toString();
		      note.content=contentText.getText().toString();	     
		      note.setTime(year, month, day, hour, minute, 0);
		      if(!filename.equals("")){//�P�_�O�_�����
		    	  note.img=filename;
		      }else{
		    	  note.img="0";
		      }
		      if(!sFilename.equals("")){//�P�_�O�_������
		    	  note.sound=sFilename;
		      }else{
		    	  note.sound="0";
		      }
		      note.cycle=spinner.getSelectedItemPosition();
		      myToDoDB.insertTimeNote(note);
	          PE.putInt("timeNote", id+1);
	          PE.commit();//�x�snoteID  
    	}
      /*�s�JDB*/	
           
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
        /*txt&txt2�����տ�X��*/
        timeSet=(TextView)findViewById(R.id.textView5);      
        spinner = (Spinner) findViewById(R.id.timespinner); 
        titleText=(EditText)findViewById(R.id.editText1);
        contentText=(EditText)findViewById(R.id.editText2);
        timeSetButton = (Button) findViewById(R.id.button4);
        friendSetButton = (Button) findViewById(R.id.button1);
        okButton=(Button)findViewById(R.id.button2);
        cancelButton=(Button)findViewById(R.id.button3); 
        photoButton=(Button)findViewById(R.id.photo); 
        soundButton=(Button)findViewById(R.id.sound); 
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
 
    private void setTime(){
    	String timeString="";
    	timeString=timeString+Integer.toString(year)+"�~"+Integer.toString(month+1)+"��"+Integer.toString(day)+"��    "
               	+Integer.toString(hour)+":"+Integer.toString(minute);   	
               	timeSet.setText(timeString);	
    }
    public String uploadImg(String allPath,Bitmap bm){
	   	try{
       		System.out.println("Upload photo Start!!!");

       		ByteArrayOutputStream out = new ByteArrayOutputStream();
       		bm.compress(Bitmap.CompressFormat.PNG, 90, out);
       		byte[] bb=out.toByteArray();
       		
       		
	       	//FileInputStream fileInputStream = new FileInputStream(new File(allPath));
	       	 
	       	final String BOUNDARY   = "---------------------------265001916915724";
	       	final String HYPHENS    = "--";
	       	final String CRLF       = "\r\n";
	       	URL url                 = new URL("http://140.121.213.214:8084/fileup/uploadservlet");    
	       	HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
	       	conn.setRequestMethod("POST");                      // method�@�w�n�OPOST
	       	conn.setDoOutput(true);
	       	conn.setDoInput(true);
	       	conn.setUseCaches(false);   

	       	// ��Content Type�]��multipart/form-data
	       	// �H�γ]�wBoundary�ABoundary�ܭ��n!
	       	// ��A���u�@�ӰѼƮɡABoundary�O�ΨӰϹj�Ѽƪ�   

	        conn.setRequestProperty("Connection", "Keep-Alive");
	        conn.setRequestProperty("referer", "http://140.121.213.214:8084/fileup/");
	        conn.setRequestProperty("Connection", "Keep-Alive");
	        //conn.setRequestProperty("Charset", "UTF-8");
	       	conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + BOUNDARY);
	       
	
	       	// �U���O�}�l�g�Ѽ�
	       	String strContentDisposition = "Content-Disposition: form-data; name=\"filename\"; filename=\""+filename+"\"";
	       	String strContentType = "Content-Type: image/jpeg";
	       	DataOutputStream dataOS = new DataOutputStream(conn.getOutputStream());
	       	dataOS.writeBytes(HYPHENS+BOUNDARY+CRLF);       // �gBOUNDARY
	       	dataOS.writeBytes(strContentDisposition+CRLF);  // �g(Disposition)
	       	dataOS.writeBytes(strContentType+CRLF);            // �g(Content Type)
	       	dataOS.writeBytes(CRLF);        

	     	//int iBytesAvailable = fileInputStream.available();
	       	int iBytesAvailable = 1024;
	       	byte[] byteData = new byte[iBytesAvailable];
	       	//int iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
	       	int x=0;
	       	while (x < bb.length) {
	       		if(bb.length-x<iBytesAvailable){
	       			dataOS.write(bb, x, bb.length-x-1); // �}�l�g���e
	       			x+=bb.length-x;
	       		}
	       		else{
		       	    dataOS.write(bb, x, iBytesAvailable); // �}�l�g���e
		       	    x+=iBytesAvailable;
	       		}
	       	    //iBytesAvailable = fileInputStream.available();
	       	    //iBytesRead = fileInputStream.read(byteData, 0, iBytesAvailable);
	       	}
	       	dataOS.writeBytes(CRLF);    
	       	dataOS.writeBytes(HYPHENS+BOUNDARY+HYPHENS+CRLF);    // (����)�g--==================================--      
	       	
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
		        	imageUrl="http://140.121.213.214:8084/fileup/uploads/"+filename;
		        	
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
    
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
               super.onActivityResult(requestCode, resultCode, data);
               try{
	               switch(requestCode){

	               case 1:
		               	/*��X�ɶ��쭶���W*/
	            	    Bundle bundle=data.getExtras();
		               	year=bundle.getInt("year");
		               	month=bundle.getInt("month");
		               	day=bundle.getInt("day");
		               	hour=bundle.getInt("hour");
		               	minute=bundle.getInt("minute");
		               	setTime();
		               	break;
	               
               	  case 3:
	  	        	 if (resultCode == RESULT_OK) {
	  	        		 	
	  	        	 }
	  	        	 break;
	  	        	 
	              case 4:
		  	        	 //if (resultCode == RESULT_OK) {
		                    friends=data.getExtras().getString("friends");
		                    String fNames=data.getExtras().getString("friendName");
		            	   	Toast.makeText(this, "�A��ܤF"+fNames, Toast.LENGTH_LONG).show();
		            	   	
		  	        	 //}
		  	        	break;	 
	  	        	 
	  	         }
               }
               catch(Exception e)
               {
            	   e.printStackTrace();
               }
    }
       
}    
    

