package anynote.client.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

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
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import anynote.client.android.Alarm.GeoDetect;
import anynote.client.classes.GeoNote;
import anynote.client.classes.TimeNote;

public class GeoModifyActivity extends Activity {
	private ToDoDB myToDoDB;
	private CheckBox checkboxIn;
	private CheckBox checkboxOut;
	private Button geoSetButton;
	private Button okButton;
	private Button cancelButton;
	private Button startTime;
	private Button friendButton;
	private EditText title;
	private EditText content;
	private TextView showStartTime;
	private TextView showEndTime;
	private Spinner range;
	private double distance;
	private int _id;
	private int noteId1;
	private String userId1;
	private String title1;
	private String content1;
	private double latitude1;
	private double longitude1;
	private String timeStart1;
	private String timeEnd1;
	private double range1;
	private String friends1;
	private int getIn1;
	private int getOut1;
	private String img1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.geonote);

		findView();
		myToDoDB = new ToDoDB(this);
		Bundle bundle = this.getIntent().getExtras();
		noteId1 = bundle.getInt("noteId");
		userId1 = bundle.getString("userId");
		title1 = bundle.getString("title");
		content1 = bundle.getString("content");
		latitude1 = bundle.getDouble("longitude");
		longitude1 = bundle.getDouble("latitude");
		timeStart1 = bundle.getString("timeStart");
		timeEnd1 = bundle.getString("timeEnd");
		range1 = bundle.getDouble("range");
		friends1 = bundle.getString("friends");
		getIn1 = bundle.getInt("getIn");
		getOut1 = bundle.getInt("getOut");
		img1 = bundle.getString("img");
		_id = bundle.getInt("_id");
		showStartTime.setText("開始時間:"+toCalendarString(timeStart1));
		showEndTime.setText("結束時間:"+toCalendarString(timeEnd1));
		title.setText(title1);
		content.setText(content1);
		distance = range1;
		// set checkbox
		checkboxIn = (CheckBox) findViewById(R.id.checkBoxIn);//
		checkboxOut = (CheckBox) findViewById(R.id.checkBoxOut);//
		if (getIn1 == 1)
			checkboxIn.setChecked(true);
		else
			checkboxIn.setChecked(false);
		if (getOut1 == 1)
			checkboxOut.setChecked(true);
		else
			checkboxOut.setChecked(false);

		// set ok button
		okButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GeoNote note = new GeoNote();

				note.noteId = noteId1;
				note.userId = userId1;
				note.title = title.getText().toString();
				note.content = content.getText().toString();
				note.range = distance;
				note.startTime = timeStart1;
				note.finishTime = timeEnd1;
				note.getIn = checkboxIn.isChecked();
				note.getOut = checkboxOut.isChecked();
				note.img = img1;
				int _id;
            	_id=myToDoDB.geoNoteAlarmId(note);
				Intent intent=new Intent();//更新GEODETECT中的arraylist(2 修改)
				Bundle bundle=new Bundle();
				bundle.putInt("status", 2);
				bundle.putInt("noteId", _id);
				bundle.putString("title", note.title);
				bundle.putString("content", note.content);
				bundle.putDouble("distance", note.range);
				bundle.putBoolean("checkboxIn", note.getIn);
				bundle.putBoolean("checkboxOut", note.getOut);
				String[] timeS=note.startTime.split("-");
				String[] timeE=note.finishTime.split("-");
				bundle.putInt("year", Integer.parseInt(timeS[0]));
				bundle.putInt("month", Integer.parseInt(timeS[1]));
				bundle.putInt("day", Integer.parseInt(timeS[2]));
				bundle.putInt("hour", Integer.parseInt(timeS[3]));
				bundle.putInt("minute", Integer.parseInt(timeS[4]));
				bundle.putInt("year2", Integer.parseInt(timeE[0]));
				bundle.putInt("month2", Integer.parseInt(timeE[1]));
				bundle.putInt("day2", Integer.parseInt(timeE[2]));
				bundle.putInt("hour2", Integer.parseInt(timeE[3]));
				bundle.putInt("minute2", Integer.parseInt(timeE[4]));
				intent.putExtras(bundle);
				intent.setClass(GeoModifyActivity.this, GeoDetect.class);
				startService(intent);
				myToDoDB.updateGeoNote(_id, note);// 更新手機DB
				updateServerGeo(note);// 更新serverDB
				GeoModifyActivity.this.finish();
			}
		});

		/* 設定地理按鈕 */
		geoSetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(GeoModifyActivity.this, "無法修改提醒之地理位置",
						Toast.LENGTH_SHORT).show();
			}
		});
		/* 取消按鈕 */
		cancelButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GeoModifyActivity.this.finish();
			}
		});
		/* 設定時間按鈕反應事件 */
		startTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent = new Intent();
				Bundle bundle = new Bundle();
				bundle.putString("timeStart", timeStart1);
				bundle.putString("timeEnd", timeEnd1);
				bundle.putInt("status", 5);
				intent.putExtras(bundle);
				intent.setClass(GeoModifyActivity.this, GeoTimeActivity.class);
				GeoModifyActivity.this.startActivityForResult(intent, 5);// 呼叫設定時間頁面
			}
		});
		// set friend button
		friendButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Toast.makeText(GeoModifyActivity.this, "無法修改提醒之朋友",
						Toast.LENGTH_SHORT).show();
			}
		});
	}

	// 更新serverDB(GEO)
	private void updateServerGeo(GeoNote geoNote) {
		System.out.println("deleteGeoNoteupload");
		HttpPost hp = new HttpPost(
				"http://140.121.197.102:8088/anynote/ModifyGeoNote");
		HttpClient client = new DefaultHttpClient();
		HttpParams httpParams = client.getParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 2000);
		HttpConnectionParams.setSoTimeout(httpParams, 3000);
		JSONObject sender = new JSONObject();
		try {
			sender.put("userId", geoNote.userId);
			sender.put("noteId", geoNote.noteId);
			sender.put("title", geoNote.title);
			sender.put("content", geoNote.content);
			sender.put("range", geoNote.range);
			sender.put("startTime", geoNote.startTime);
			sender.put("endTime", geoNote.finishTime);
			sender.put("getIn", geoNote.getIn);
			sender.put("getOut", geoNote.getOut);
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		System.out.print(sender.toString());

		try {
			hp.setEntity(new StringEntity(sender.toString(), HTTP.UTF_8));
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		try {
			switch (requestCode) {
			case 5:
				int year = data.getExtras().getInt("year");
				int month = data.getExtras().getInt("month");
				int day = data.getExtras().getInt("day");
				int hour = data.getExtras().getInt("hour");
				int minute = data.getExtras().getInt("minute");
				showStartTime.setText("開始時間:"
						+ setTime(year, month, day, hour, minute));

				int year2 = data.getExtras().getInt("year2");
				int month2 = data.getExtras().getInt("month2");
				int day2 = data.getExtras().getInt("day2");
				int hour2 = data.getExtras().getInt("hour2");
				int minute2 = data.getExtras().getInt("minute2");
				showEndTime.setText("結束時間:"
						+ setTime(year2, month2, day2, hour2, minute2));

				timeStart1 = year + "-" + month + "-" + day + "-" + hour + "-"
						+ minute + "-" + "0";
				timeEnd1 = year + "-" + month + "-" + day + "-" + hour + "-"
						+ minute + "-" + "0";
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String setTime(int year, int month, int day, int hour, int minute) {
		String timeString = "";
		timeString = timeString + Integer.toString(year) + "年"
				+ Integer.toString(month + 1) + "月" + Integer.toString(day)
				+ "日    " + Integer.toString(hour) + ":"
				+ Integer.toString(minute);
		return timeString;
	}
	 private String toCalendarString(String time)
		{
			String[] setTime=time.split("-");
	       String setTimeString=setTime[0]+"年"+(Integer.parseInt(setTime[1])+1)+"月"+setTime[2]+"日"+setTime[3]+":"+setTime[4];
	       
	       return setTimeString;
	        //System.out.print(dateTime.toString());
		}

	private void findView() {
		startTime = (Button) findViewById(R.id.button5);//
		geoSetButton = (Button) findViewById(R.id.button4);//
		okButton = (Button) findViewById(R.id.button2);
		cancelButton = (Button) findViewById(R.id.button3);//
		friendButton = (Button) findViewById(R.id.friends);//
		title = (EditText) findViewById(R.id.editTitle);//
		content = (EditText) findViewById(R.id.editContent);//
		showStartTime = (TextView) findViewById(R.id.startTime);//
		showEndTime = (TextView) findViewById(R.id.endTime);//

		/* 提醒範圍設定(spinner) */
		range = (Spinner) findViewById(R.id.chooseRange);
		int[] rangeItem = getResources().getIntArray(R.array.chooseRange);
		ArrayList<Integer> list = new ArrayList<Integer>();
		for (int item : rangeItem)
			list.add(item);
		ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this,
				android.R.layout.simple_spinner_dropdown_item, list);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		range.setAdapter(adapter);
		range.setSelection(0);
		range.setPrompt("提醒範圍(公尺)");
		range.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				int temp = (Integer) adapterView.getSelectedItem();
				distance = (double) temp;
				Toast.makeText(GeoModifyActivity.this, "您選擇" + distance,
						Toast.LENGTH_LONG).show();
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
			}
		});
	}
}
