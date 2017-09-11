package anynote.client.android;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import anynote.client.classes.SearchNote;


public class SearchActivity extends Activity{
	private ToDoDB myToDoDB;
	private Button friendButton;
	private Button searchButton;
	private Button cancelButton;
	private EditText searchContent;
	private Button allButton;
	private TextView txt;
	private EditText search;
	private String friends="";
   private DatePicker date;
   public ProgressDialog myDialog = null;
	private Spinner upOrDown;
	@Override
	public void onCreate(Bundle savedInstanceState)
	  {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.search);
		 findView();
	     friendButton.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View v) {
	               
	            	if(AnyNoteActivity.facebook.isSessionValid()) { 
	            		myDialog = ProgressDialog.show(SearchActivity.this,
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
			            	intent.setClass(SearchActivity.this, Friend_list.class);
			            	//startActivity(intent);
			            	startActivityForResult(intent,4);
			            	System.out.println("after");
	            	 } 
	            	else{
	            		Toast.makeText(SearchActivity.this, "請重新登入後再使用此功能", Toast.LENGTH_LONG).show();
	            	}
	            }
	        });
	     cancelButton.setOnClickListener(new View.OnClickListener() {
	            @Override
				public void onClick(View v) {
	            	SearchActivity.this.finish();
	            	
	            }
	        });	     
	     
	     searchButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String time;
				time=Integer.toString(date.getYear());
				time+="-"+Integer.toString(date.getMonth());
				time+="-"+Integer.toString(date.getDayOfMonth());
				if(friends.equals(""))friends="";
				System.out.println("search"+search.getText().toString());
				System.out.println("searchContent"+searchContent.getText().toString());
				System.out.println("friends"+friends);
				System.out.println("time"+time);
				System.out.println(upOrDown.getSelectedItem().toString());
				
				SearchNote note=new SearchNote(search.getText().toString(), searchContent.getText().toString(), friends, time, upOrDown.getSelectedItem().toString());
				//BrowseActivity.list=myToDoDB.timeSelect();
				BrowseActivity.list= myToDoDB.timeNoteSearch(note);
				// TODO Auto-generated method stub
				Intent intent= new Intent();
				Bundle bundle=new Bundle();
                bundle.putBoolean("isAll",false);
            	intent.setClass(SearchActivity.this,BrowseActivity.class);
            	bundle.putString("title", note.title);
            	bundle.putString("content", note.content);
            	bundle.putString("friends", note.friends);
            	bundle.putString("time", note.time);
            	bundle.putString("upOrDown", note.upOrDown);
            	intent.putExtras(bundle);
            	startActivity(intent);
				
			}
		});
	     allButton.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					BrowseActivity.list=myToDoDB.timeSelect();
					Intent intent= new Intent();
	            	intent.setClass(SearchActivity.this,BrowseActivity.class);
	            	Bundle bundle=new Bundle();
	                bundle.putBoolean("isAll",true);
	                intent.putExtras(bundle);
	            	startActivity(intent);
					
				}
			});
	    
	     
		  
	  }
	private void findView()
	{
		myToDoDB = new ToDoDB(this);
		allButton=
		(Button)findViewById(R.id.allButton);
		cancelButton=(Button)findViewById(R.id.button1);
		 txt=(TextView)findViewById(R.id.searchTextView1);
		 search=(EditText)findViewById(R.id.searchEditText);
		 searchContent=(EditText)findViewById(R.id.searchContentText);
		 date=(DatePicker)findViewById(R.id.searchDatePicker);
		 friendButton=(Button)findViewById(R.id.searchFriendButton);
		// geoButton=(Button)findViewById(R.id.geoButton);
		 upOrDown=(Spinner) findViewById(R.id.spinnerSearch);
		 searchButton=(Button)findViewById(R.id.searchButton);
		 ArrayAdapter<String> adapter = new ArrayAdapter<String>
	        (this,android.R.layout.simple_spinner_item,new String[]{"無","以前","以後"});
	        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	        upOrDown.setAdapter(adapter);
	        upOrDown.setOnItemSelectedListener(new Spinner.OnItemSelectedListener(){
	        	            @Override
							public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id){
	        	                //Toast.makeText(SearchActivity.this, "您選擇"+adapterView.getSelectedItem().toString(), Toast.LENGTH_LONG).show();
	        	            }
	        	            @Override
							public void onNothingSelected(AdapterView<?> arg0) {
	        	              Toast.makeText(SearchActivity.this, "您沒有選擇任何項目", Toast.LENGTH_LONG).show();
	        	            }
	        });
		
	}
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try{
            if (resultCode == 4){
				friends=data.getExtras().getString("friends");
                String fNames=data.getExtras().getString("friendName");
        	   	Toast.makeText(this, "你選擇了"+fNames, 0).show();
            }
        }
        catch(Exception e)
        {
     	   e.printStackTrace();
        }

    }
}
