package anynote.client.android;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

class FlushedInputStream extends FilterInputStream {

	public FlushedInputStream(InputStream inputStream) {
		super(inputStream);
	}

	@Override
	public long skip(long n) throws IOException {
		long totalBytesSkipped = 0L;
		while (totalBytesSkipped < n) {
			long bytesSkipped = in.skip(n - totalBytesSkipped);
			if (bytesSkipped == 0L) {
				int b = read();
				if (b < 0) {
					break; // we reached EOF
				} else {
					bytesSkipped = 1; // we read one byte
				}
			}
			totalBytesSkipped += bytesSkipped;
		}
		return totalBytesSkipped;
	}
}

public class SetActivity extends Activity {
	/** Called when the activity is first created. */
	ToggleButton sync;
	ToggleButton Vibrate;
	TextView txt;
	Button voiceSet;
	Button logout;
	Button toMain;
	Button cancelVoice;
	Button upsound;
	String strImgPath;
	String filename;
	ImageView photoView;
	MediaController mediaplayer;

	final SharedPreferences.Editor editor = AnyNoteActivity.mPrefs.edit();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set);
		sync = (ToggleButton) findViewById(R.id.toggleButton1);
		Vibrate = (ToggleButton) findViewById(R.id.toggleButton2);
		txt = (TextView) findViewById(R.id.textView4);
		voiceSet = (Button) findViewById(R.id.button1);
		logout = (Button) findViewById(R.id.logout);
		toMain = (Button) findViewById(R.id.toMain);
		upsound = (Button) findViewById(R.id.upsound);
		cancelVoice = (Button) findViewById(R.id.cancelVoice);
		photoView = (ImageView) findViewById(R.id.imageView1);
		mediaplayer = (MediaController) findViewById(R.id.mediaController1);
		
		txt.setText(AnyNoteActivity.voicePath);
		if (AnyNoteActivity.syncSwitch == 1)
			sync.setChecked(true);
		else
			sync.setChecked(false);
		if (AnyNoteActivity.vibrateSwitch == 1)
			Vibrate.setChecked(true);
		else
			Vibrate.setChecked(false);

		sync.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					AnyNoteActivity.syncSwitch = 1;
					editor.putInt("syncSwitch", 1);
					editor.commit();
				} else {
					AnyNoteActivity.syncSwitch = 0;
					editor.putInt("syncSwitch", 0);
					editor.commit();
					Intent intent = new Intent(SetActivity.this,
							BroadCast.class);
					PendingIntent sender = PendingIntent.getBroadcast(
							getApplicationContext(), 999, intent, 0);
					AlarmManager am;
					am = (AlarmManager) getSystemService(ALARM_SERVICE);
					am.cancel(sender);
				}
			}

		});
		Vibrate.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
				if (isChecked) {
					AnyNoteActivity.vibrateSwitch = 1;
					editor.putInt("vibrateSwitch", 1);
					editor.commit();
				} else {
					AnyNoteActivity.vibrateSwitch = 0;
					editor.putInt("vibrateSwitch", 0);
					editor.commit();

				}
			}

		});
		cancelVoice.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				txt.setText("");

				AnyNoteActivity.voicePath = "";
				editor.putString("voicePath", "");
				editor.commit();
			}
		});
		upsound.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				getSound();
			}
		});
		
		voiceSet.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				// 建立 "選擇檔案 Action" 的 Intent
				Intent intent = new Intent(Intent.ACTION_PICK);

				// 過濾檔案格式
				intent.setType("audio/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				// startActivityForResult(intent, REQUEST_CODE_THREE);
				// 建立 "檔案選擇器" 的 Intent (第二個參數: 選擇器的標題)
				Intent destIntent = Intent.createChooser(intent, "選擇檔案");

				// 切換到檔案選擇器 (它的處理結果, 會觸發 onActivityResult 事件)
				startActivityForResult(destIntent, 0);
			}
		});

		logout.setOnClickListener(new OnClickListener() {

			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if (AnyNoteActivity.facebook.isSessionValid()) {
					try {

						AnyNoteActivity.facebook.logout(SetActivity.this);

						String method = "DELETE";
						Bundle params = new Bundle();
						params.putString("permission", "publish_stream");
						AnyNoteActivity.facebook.request("/me/permissions",
								params, method);
						editor.putString("fbId", "");
						editor.putString("fbName", "");
						editor.putString("access_token", null);
						editor.putLong("access_expires", 0);
						editor.commit();
						Intent intent = new Intent(SetActivity.this,
								BroadCast.class);
						PendingIntent sender = PendingIntent.getBroadcast(
								getApplicationContext(), 999, intent, 0);
						AlarmManager am;
						am = (AlarmManager) getSystemService(ALARM_SERVICE);
						am.cancel(sender);
						Toast.makeText(SetActivity.this, "已登出",
								Toast.LENGTH_LONG).show();
					} catch (MalformedURLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else
					Toast.makeText(SetActivity.this, "您已經登出", Toast.LENGTH_LONG)
							.show();
			}
		});
		toMain.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				SetActivity.this.finish();
			}
		});
	}
	
	 public void getSound(){
		 System.out.println("Download sound Start!!!");

		 String serverUrl = "http://192.168.1.105:8084/anynote/";
		 //if(partUrl.charAt(partUrl.length()-1)!='0'){
			
			 try{ 
				 String soundUrl=serverUrl+"uploads/100000102111415/time_2.amr";
				 URL url = new URL(soundUrl);
		 		 URLConnection conn = url.openConnection();
		  		 HttpURLConnection httpConn = (HttpURLConnection)conn;
		  		 httpConn.setDoInput(true);
		  		 httpConn.setRequestProperty("Connection", "Keep-Alive");
		  		 InputStream inputStream = httpConn.getInputStream();

		  		 File f=new File("/sdcard/AnyNote/test.amr");

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
	       		
	       		
		 
		       	 
		       	final String BOUNDARY   = "-----------------------------41184676334";
		       	final String HYPHENS    = "--";
		       	final String CRLF       = "\r\n";
		       	URL url                 = new URL("http://140.121.198.86:8088/anynote/uploadservlet"+"?id="+AnyNoteActivity.fbId);    
		       	HttpURLConnection conn  = (HttpURLConnection) url.openConnection();
		       	conn.setRequestMethod("POST");                      // method一定要是POST
		       	conn.setDoOutput(true);
		       	conn.setDoInput(true);
		       	conn.setUseCaches(false);   

		       	// 把Content Type設為multipart/form-data
		       	// 以及設定Boundary，Boundary很重要!
		       	// 當你不只一個參數時，Boundary是用來區隔參數的   

		        conn.setRequestProperty("Connection", "Keep-Alive");
		        conn.setRequestProperty("referer", "http://140.121.198.86:8088/anynote/");
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
			        	SoundUrl="http://140.121.198.86:8088/fileup/uploads/"+sFilename;
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



	public String uploadImg(String allPath, Bitmap bm) {
		try {
			System.out.println("Upload photo Start!!!");

			ByteArrayOutputStream out = new ByteArrayOutputStream();
			bm.compress(Bitmap.CompressFormat.PNG, 90, out);
			byte[] bb = out.toByteArray();

			// FileInputStream fileInputStream = new FileInputStream(new
			// File(allPath));

			final String BOUNDARY = "---------------------------265001916915724";
			final String HYPHENS = "--";
			final String CRLF = "\r\n";
			URL url = new URL(
					"http://140.121.213.214:8084/fileup/uploadservlet");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST"); // method一定要是POST
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setUseCaches(false);

			// 把Content Type設為multipart/form-data
			// 以及設定Boundary，Boundary很重要!
			// 當你不只一個參數時，Boundary是用來區隔參數的

			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("referer",
					"http://140.121.213.214:8084/fileup/");
			conn.setRequestProperty("Connection", "Keep-Alive");
			// conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Content-Type",
					"multipart/form-data; boundary=" + BOUNDARY);

			// 下面是開始寫參數
			String strContentDisposition = "Content-Disposition: form-data; name=\"filename\"; filename=\""
					+ filename + "\"";
			String strContentType = "Content-Type: image/jpeg";
			DataOutputStream dataOS = new DataOutputStream(
					conn.getOutputStream());
			dataOS.writeBytes(HYPHENS + BOUNDARY + CRLF); // 寫BOUNDARY
			dataOS.writeBytes(strContentDisposition + CRLF); // 寫(Disposition)
			dataOS.writeBytes(strContentType + CRLF); // 寫(Content Type)
			dataOS.writeBytes(CRLF);

			// int iBytesAvailable = fileInputStream.available();
			int iBytesAvailable = 1024;
			byte[] byteData = new byte[iBytesAvailable];
			// int iBytesRead = fileInputStream.read(byteData, 0,
			// iBytesAvailable);
			int x = 0;
			while (x < bb.length) {
				if (bb.length - x < iBytesAvailable) {
					dataOS.write(bb, x, bb.length - x - 1); // 開始寫內容
					x += bb.length - x;
				} else {
					dataOS.write(bb, x, iBytesAvailable); // 開始寫內容
					x += iBytesAvailable;
				}
				// iBytesAvailable = fileInputStream.available();
				// iBytesRead = fileInputStream.read(byteData, 0,
				// iBytesAvailable);
			}
			dataOS.writeBytes(CRLF);
			dataOS.writeBytes(HYPHENS + BOUNDARY + HYPHENS + CRLF); // (結束)寫--==================================--

			dataOS.flush();
			dataOS.close();
			// fileInputStream.close();

			InputStream stream = conn.getInputStream();
			BufferedInputStream in = new BufferedInputStream(stream, 8192);
			int i = 0;
			String imageUrl = "";
			while ((i = in.read()) != -1) {
				System.out.print(i);
				if (i == 1) {
					imageUrl = "http://140.121.213.214:8084/fileup/uploads/"
							+ filename;

					break;
				}
			}
			in.close();
			if (imageUrl.length() > 0) {
				System.out.println("Upload photo End!!!");
			}
			return imageUrl;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {

		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);

		// 有選擇檔案
		try {

			if (requestCode == 0) {
				if (resultCode == RESULT_OK) {
					// 取得檔案的 Uri
					Uri uri = data.getData();

					if (uri != null) {
						txt.setText(uri.toString());

						AnyNoteActivity.voicePath = uri.toString();
						editor.putString("voicePath", uri.toString());
						editor.commit();

					} else {
						setTitle("無效的檔案路徑 !!");
					}
				}
			}
			/* 抓取照相機圖片 */
			else if (requestCode == 1) {
				if (resultCode == RESULT_OK) {
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 4;
					final Bitmap bm = BitmapFactory.decodeFile(strImgPath,
							options);
					photoView.setImageBitmap(bm);

					Thread upload = new Thread(new Runnable() {
						public void run() {
							uploadImg(strImgPath, bm);
						}
					});
					upload.start();

				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}

}
