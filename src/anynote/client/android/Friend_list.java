package anynote.client.android;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SimpleAdapter.ViewBinder;
import android.widget.TextView;
import android.widget.Toast;

public class Friend_list extends ListActivity {
	private ToDoDB myToDoDB;
	int nowList=0;
	int chosenCount=0;
	List<Map<String, Object>> list;   
	List<Map<String, Object>> searchList;   
	final Bundle params = new Bundle();
	final String [] chosenUserName=new String [50];
	final long []chooseUserId=new long [50];
	ImageView xxx;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
			
	        super.onCreate(savedInstanceState);	
	        setContentView(R.layout.fd_list);
	        myToDoDB=new ToDoDB(this);
       	 	
	        final EditText sname=(EditText)findViewById(R.id.editText1);
	        Button ok=(Button)findViewById(R.id.button1); 
	        final TextView txt=(TextView)findViewById(R.id.textView1);
	        xxx=(ImageView)findViewById(R.id.imageView1);
	        System.out.println("findView2");
	        list = getListForSimpleAdapter();  
	        ListView myListView = getListView();
	        System.out.println("getListView()");
	        try{SimpleAdapter  adapter = new SimpleAdapter(Friend_list.this,list,R.layout.item,  
			        new String[] {"BigText", "LittleText","check"},new int[] { R.id.BigText,R.id.LittleText,R.id.checkBox1});  
	        adapter.setViewBinder(new MyViewBinder());
	        setListAdapter(adapter);  
	        }catch(Exception e){
	        	
	        }
	        sname.addTextChangedListener(new TextWatcher(){ 
	        
	        	@Override   
	        	public void beforeTextChanged(CharSequence s,int start,int count,int after){       
	        	    }   
	        	@Override   
	        	public void onTextChanged(CharSequence s, int start, int before, int count) {   
	        	  
	        	  }
				@Override
				public void afterTextChanged(Editable s) {
				    if(sname.getText().toString()=="")nowList=0;
	                 else nowList=1;
	            	 //Toast.makeText(getBaseContext(), sname.getText().toString(), Toast.LENGTH_LONG).show();
	            	 searchList=searchListForSimpleAdapter(sname.getText().toString());
	            	 
	         
	     	        SimpleAdapter  adapter2 = new SimpleAdapter(Friend_list.this,searchList,R.layout.item,
	     	        		new String[] { "BigText", "LittleText","check"},new int[] { R.id.BigText,R.id.LittleText,R.id.checkBox1});  
	    	        
	    	        setListAdapter(adapter2);  
					
				}   
	        	}); 
	        
	        myListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View arg1,
						int position, long id) {
						TextView big=(TextView)arg1.findViewById(R.id.BigText);
						TextView small=(TextView)arg1.findViewById(R.id.LittleText);
						
						CheckBox box=(CheckBox)arg1.findViewById(R.id.checkBox1);
						
						if(box.isChecked()){
							box.setChecked(false);
							String name=big.getText().toString();
						    for(int a=0;a<chosenCount;a++) {
					        	if(chosenUserName[a]==name){
					        		while(a<chosenCount-1){
					        			chosenUserName[a]=chosenUserName[a+1];
					        		}
					        		chosenCount--;
					        		break;
					        	}
						    } 
							if(nowList==0)
								list.get(position).put("check", false);
							else 
								searchList.get(position).put("check", false);
						}
						
						else{ box.setChecked(true);
							chosenUserName[chosenCount]=big.getText().toString();
							chooseUserId[chosenCount]= Long.parseLong(small.getText().toString());
							chosenCount++;
							if(nowList==0)
								list.get(position).put("check", true);
							else 
								searchList.get(position).put("check", true);
						}
						String output="你選了"+String.valueOf(chosenCount)+"個好友:";
					    for(int a=0;a<chosenCount;a++) {
					        	output=output+chosenUserName[a]+",";
					    }
					    txt.setText(output);
			}});
	 
	        
	        ok.setOnClickListener(new View.OnClickListener() {
	             @Override
				public void onClick(View v) {
	            	 String outPut="";
	            	 for(int a=0;a<list.size();a++){
	            		 if((Boolean)list.get(a).get("check")==true)
	            		 outPut=outPut+list.get(a).get("LittleText")+"_";
	            	 }
	            	 
	            	 String outPutName="";
	            	 for(int a=0;a<list.size();a++){
	            		 if((Boolean)list.get(a).get("check")==true)
	            			 outPutName=outPutName+list.get(a).get("BigText")+"_";
	            	 }
	            	String newName=" 無";
	            	if(!outPutName.equals(""))
	            		newName=outPutName.substring(0,outPutName.length()-1);
	     	        Intent i=new Intent();
	           	 	Bundle b=new Bundle();      
	            	b.putString("friends", outPut);
	            	b.putString("friendName", newName);
					 i.putExtras(b);
	            	 Friend_list.this.setResult(4, i);
	            	 Friend_list.this.finish();
	             }
	         });

	 }
	
	public class MyViewBinder implements ViewBinder {
		@Override
		public boolean setViewValue(View view, Object data,
		String textRepresentation) {
			if( (view instanceof ImageView) & (data instanceof Bitmap) ) {
				ImageView iv = (ImageView) view;
				Bitmap bm = (Bitmap) data;
				iv.setImageBitmap(bm);
				return true;
			}
			return false;
		}
	}
	  
	private List<Map<String, Object>> searchListForSimpleAdapter(String name) { 
		 searchList=new ArrayList<Map<String,Object>>(list.size());  
         int count=0;
         while(count<list.size()) {
		   if(list.get(count).get("BigText").toString().toLowerCase().indexOf(name.toLowerCase())!=-1){
			   searchList.add(list.get(count));              
		   }
		   count++;
         }
         return searchList;
 
	}  
	   
	   
	   private List<Map<String, Object>>   getListForSimpleAdapter() {  
		    
		   JSONObject json_data = null;
		   
		   try {
	            String query = "select pic_small, name, uid from user where is_app_user = '121730154575156' AND	uid in (select uid2 from friend where uid1=me()) order by name";
	            Bundle params = new Bundle();
	            params.putString("method", "fql.query");
	            params.putString("query", query); 	        
	        	String response = AnyNoteActivity.facebook.request(null,params); // Get a friend information from facebook
	            JSONArray jArray = new JSONArray(response);
	            list=new ArrayList<Map<String,Object>>(jArray.length()); 
	            System.out.println(jArray.length());
	            Map<String, Object> map;
	           
	           int count=0;
	           while(count<jArray.length()) {
	        	   json_data= jArray.getJSONObject(count);
	        	   Bitmap bitmap = null;
	        	   File myDrawFile=null;
	        		/*try {
	            		   URL url = new URL(json_data.getString("pic_small"));
	            		   URLConnection conn = url.openConnection();
	            		   HttpURLConnection httpConn = (HttpURLConnection)conn;
	            		   httpConn.setRequestMethod("GET");
	            		   httpConn.connect();
	            		 
	            		   if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
	            		    InputStream inputStream = httpConn.getInputStream();
	            		     
	            		    bitmap = BitmapFactory.decodeStream(inputStream);
	            		    inputStream.close();
	            		    
	            		   }
	            		  } catch (MalformedURLException e1) {
	            		   // TODO Auto-generated catch block
	            		   e1.printStackTrace();
	            		  } catch (IOException e) {
	            		   // TODO Auto-generated catch block
	            		   e.printStackTrace();
	            		  }
	            	*/
	               map= new HashMap<String, Object>();  
	               //map.put("userImg", bitmap);  
		           map.put("BigText", json_data.getString("name"));  
		           map.put("LittleText", json_data.getLong("uid"));  
		           myToDoDB.insertNewFriendName((json_data.getString("name"))+"\n", json_data.getString("uid"));
		           map.put("check", false);  
		           list.add(map);              
		           count++;
	           }
	           myToDoDB.close();
	           return list;
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
  
		       return list;  
		    }  
	   

	  
}
