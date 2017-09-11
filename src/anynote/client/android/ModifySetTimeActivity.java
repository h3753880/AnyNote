package anynote.client.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

public class ModifySetTimeActivity extends Activity {
	private Button buttonOk;
	private Button buttonCancel;
	private TimePicker time;
    private DatePicker date;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_time);
		findView();
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
				intent.putExtras(bundle);
				ModifySetTimeActivity.this.setResult(2,intent);
				ModifySetTimeActivity.this.finish();
			}
		});
        //按下取消鍵
        buttonCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ModifySetTimeActivity.this.finish();
			}
		});
	}

	private void findView() {
		Bundle text=this.getIntent().getExtras();
		buttonOk=(Button)findViewById(R.id.button1);
		buttonCancel=(Button)findViewById(R.id.button2);
		time=(TimePicker)findViewById(R.id.timePicker1);
		date=(DatePicker)findViewById(R.id.datePicker1);
		changeToCalendar(text.getString("time"));
	}
    private void changeToCalendar(String time)
	{
		String[] setTime=time.split("-");
        date.init(Integer.parseInt(setTime[0]), Integer.parseInt(setTime[1]), Integer.parseInt(setTime[2]), new DatePicker.OnDateChangedListener() {
			
			@Override
			public void onDateChanged(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				
				
			}
		});
        this.time.setCurrentHour(Integer.parseInt(setTime[3]));
        this.time.setCurrentMinute(Integer.parseInt(setTime[4]));	 
        //System.out.print(dateTime.toString());
	}
	
}
