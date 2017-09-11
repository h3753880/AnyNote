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
       
   
   
 

       /* 取得各元件的View */ 
       findView();
       setTime();
       /* 取得DataBase裡的資料 */ 
        myToDoDB = new ToDoDB(this);
        /*設定Spinner選單 */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
        (this,android.R.layout.simple_spinner_item,new String[]{"無","小時","天","周","月"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
        	            @Override
						public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
        	                //Toast.makeText(TimeNoteActivity.this, "您選擇"+adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
        	            }
        	            @Override
						public void onNothingSelected(AdapterView<?> arg0) {
        	              //Toast.makeText(TimeNoteActivity.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
        	            }
        });
        /*設定時間按鈕按下反應事件*/
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
            		Toast.makeText(TimeNoteActivity.this, "請重新登入後再使用此功能", Toast.LENGTH_LONG).show();
            	}
            }
        });
        photoButton.setOnClickListener(new View.OnClickListener() {
        	 @Override
 			public void onClick(View v) {
        		int noteId=AnyNoteActivity.mPrefs.getInt("timeNote", 1);//製作noteID
             	Intent imageCaptureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                strImgPath = "/sdcard/AnyNote/";//存放照片的文件夾
                String userId="0";
                if(AnyNoteActivity.facebook.isSessionValid()) {userId=AnyNoteActivity.fbId;}
                
                filename = "time_"+noteId+ ".jpg";//照片命名
                File out = new File(strImgPath);
                if (!out.exists()) {
                        out.mkdirs();
                }
                strImgPath = strImgPath + filename;//該照片的絕對路徑
                out = new File(strImgPath);
                Uri uri = Uri.fromFile(out);
                imageCaptureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
                startActivityForResult(imageCaptureIntent, 3); 	
             }
         });
        soundButton.setOnClickListener(new View.OnClickListener() {
       	 @Override
			public void onClick(View v) {
       		 	int noteId=AnyNoteActivity.mPrefs.getInt("timeNote", 1);//製作noteID
       		 	strImgPath = "/sdcard/AnyNote/";//存放錄音的文件夾
       		 	String userId="0";
       		 	if(AnyNoteActivity.facebook.isSessionValid()) {userId=AnyNoteActivity.fbId;}
       		 	
       		 try  
             {  
       			
       			if(recording==0){
      		       	Toast.makeText(TimeNoteActivity.this,"開始錄音，再次點擊按鈕結束錄音",Toast.LENGTH_SHORT).show();
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
	       			soundButton.setText("錄音中...");
	       			recording=1;
       			}else if(recording==1){
       		       	Toast.makeText(TimeNoteActivity.this,"錄音完成",Toast.LENGTH_SHORT).show();
       				mRecorder.stop();  
       				mRecorder.release();  	         
       				mRecorder = null;  	
       				soundButton.setText("播放");
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
        
        /*確定新增按鈕按下反應事件*/
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	if(titleText.getText().toString().equals(""))
            		Toast.makeText(TimeNoteActivity.this, "請輸入提醒標題",
						Toast.LENGTH_SHORT).show();
            	
            	else if(contentText.getText().toString().equals(""))
            		Toast.makeText(TimeNoteActivity.this, "請輸入提醒內容",
    						Toast.LENGTH_SHORT).show();
            	
            	else{
	          	int noteId=AnyNoteActivity.mPrefs.getInt("timeNote", 1);//製作noteID
	          	boolean friendAlarm=false;
	            if(friends!=""){
	            	  friendAlarm=true;
	            }
            	/*新增置資料庫*/           
            	addTodo(noteId);
            	if(!friendAlarm){
	            	int _id;
	            	_id=myToDoDB.timeNoteAlarmId(note);
	                /*新增一次性提醒*/
	                if(spinner.getSelectedItem().toString()=="無"){   
	                	/*設定提醒時間*/
		                c.setTimeInMillis(System.currentTimeMillis());
		                c.set(Calendar.YEAR,year);
		                c.set(Calendar.MONTH,month);
		                c.set(Calendar.DAY_OF_MONTH,day);
		                c.set(Calendar.HOUR_OF_DAY,hour);
		                c.set(Calendar.MINUTE,minute);
		                c.set(Calendar.SECOND,0);
		                c.set(Calendar.MILLISECOND,0);
		                /*傳送title與content*/
		                Bundle bundle=new Bundle();
		                bundle.putString("name","自己\n");
		                bundle.putString("title",titleText.getText().toString());
		                bundle.putString("content",contentText.getText().toString());
		                bundle.putString("img",note.img);
		                bundle.putString("sound",note.sound);
		                /* 指定鬧鐘設定時間到時要執行CallAlarm.class */
		                Intent intent = new Intent(TimeNoteActivity.this, CallAlarm.class);
		                intent.putExtras(bundle);
		                /* 建立PendingIntent */ 
		                PendingIntent sender=PendingIntent.getBroadcast(getApplicationContext(),
		                			_id, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
		                /* AlarmManager.RTC_WAKEUP設定服務在系統休眠時同樣會執行
		                 * 以set()設定的PendingIntent只會執行一次
		                 * */
		                System.out.println("newTimeAlarm"+_id);
		                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		                am.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
		                /* 以Toast提示設定已完成 */
		                Toast.makeText(TimeNoteActivity.this,"新增備忘錄成功",Toast.LENGTH_SHORT)
		                  .show();
		      
	                }
	                
	                /*新增週期性提醒*/
	                else{
	                	/*設定週期長度*/
	                	long times=0;
	                	if(spinner.getSelectedItem().toString()=="小時")times=360;
	                	else if(spinner.getSelectedItem().toString()=="天")times=8640;
	                	else if(spinner.getSelectedItem().toString()=="周")times=60480;
	                	else if(spinner.getSelectedItem().toString()=="月")times=259200;
	                	/*設定提醒時間*/
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
		                bundle.putString("img",note.img);
		                bundle.putString("sound",note.sound);
		                /* 指定鬧鐘設定時間到時要執行CallAlarm.class */
		                Intent intent = new Intent(TimeNoteActivity.this, CallAlarm.class);
		                intent.putExtras(bundle);
		                /* 建立PendingIntent */       
		                PendingIntent sender=PendingIntent.getBroadcast(getApplicationContext(),
		                			_id, intent,  PendingIntent.FLAG_UPDATE_CURRENT);
		               
		                AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
		                am.setRepeating(AlarmManager.RTC_WAKEUP,
		                         c.getTimeInMillis(),times*1000,sender);
		       	        /* 以Toast提示設定已完成 */
		                Toast.makeText(TimeNoteActivity.this,"新增備忘錄成功，提醒周期為"+spinner.getSelectedItem().toString(),Toast.LENGTH_SHORT)
		                  .show(); 
		               
	                }   
            	}
            	TimeNoteActivity.this.finish();
            }
           }
        });       
        /*取消新增按鈕按下反應事件*/       
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
			public void onClick(View v) {
            	TimeNoteActivity.this.finish();
            }
        });
        
        
	}
    
    private void addTodo(int id)
    {
    	/*建立TimeNote並將資料存入*/
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
		      if(!filename.equals("")){//判斷是否有拍照
		    	  note.img=filename;
		      }else{
		    	  note.img="0";
		      }
		      if(!sFilename.equals("")){//判斷是否有錄音
		    	  note.sound=sFilename;
		      }else{
		    	  note.sound="0";
		      }
		      note.cycle=spinner.getSelectedItemPosition();
		      myToDoDB.insertTimeNote(note);
	          PE.putInt("timeNote", id+1);
	          PE.commit();//儲存noteID  
    	}
      /*存入DB*/	
           
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
        /*txt&txt2為測試輸出用*/
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
    	timeString=timeString+Integer.toString(year)+"年"+Integer.toString(month+1)+"月"+Integer.toString(day)+"日    "
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
	       	conn.setRequestMethod("POST");                      // method一定要是POST
	       	conn.setDoOutput(true);
	       	conn.setDoInput(true);
	       	conn.setUseCaches(false);   

	       	// 把Content Type設為multipart/form-data
	       	// 以及設定Boundary，Boundary很重要!
	       	// 當你不只一個參數時，Boundary是用來區隔參數的   

	        conn.setRequestProperty("Connection", "Keep-Alive");
	        conn.setRequestProperty("referer", "http://140.121.213.214:8084/fileup/");
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
	       			dataOS.write(bb, x, bb.length-x-1); // 開始寫內容
	       			x+=bb.length-x;
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
		               	/*輸出時間到頁面上*/
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
		            	   	Toast.makeText(this, "你選擇了"+fNames, Toast.LENGTH_LONG).show();
		            	   	
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
    

