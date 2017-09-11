package anynote.client.classes;

import java.util.Calendar;

import com.google.android.maps.GeoPoint;

public class GeoNote extends Note{
	public  GeoPoint destGeoPoint;//提醒目標點
	public double Longitude;
	public double Latitude;
	public double range;//提醒誤差
	public Calendar timeStart=Calendar.getInstance();//開始時間
	public String startTime="";
	public Calendar timeFinish=Calendar.getInstance();//結束時間
	public String finishTime="";
	public boolean oneTime;//判斷只跑一次
	public boolean approachStatus;//接近提醒或離開提醒判斷用
	public boolean getIn;//接近提醒為true
	public boolean getOut;//離開提醒為true
	public String city;//提醒所在城市
	private String favPlace="";//喜愛點
	
	public void setFavPlace(String place)
	{
		if(place.equals("") || place==null)
			favPlace="";
		else
			favPlace=place;
	}
	
	public String getFavPlace() 
	{
		return favPlace;
	}
	
	public GeoNote()
	{
		approachStatus=false;
		oneTime=false;
	}	
	
	public GeoNote(int noteId,String userId,String title,String content,GeoPoint destGeoPoint,
			double range,Calendar timeStart,Calendar timeFinish,boolean getIn,boolean getOut)
	{
		this.noteId=noteId;
		this.userId=userId;
		this.title=title;
		this.content=content;
		this.destGeoPoint=destGeoPoint;
		this.range=range;
		
		this.timeStart.setTime(timeStart.getTime());
        this.timeFinish.setTime(timeFinish.getTime());
        
        this.getIn=getIn;
        this.getOut=getOut;
        
        approachStatus=false;
        oneTime=false;
	}
	
	public GeoNote(int noteId, String userId,String friends,String title,String content,
			double Longitude,double Latitude,double range,String startTime,String finishTime,boolean getIn ,boolean getOut,String img,String sound)
	{
		this.noteId=noteId;
		this.userId=userId;
		this.friends=friends;
		this.Longitude=Longitude;
		this.Latitude=Latitude;
		this.title=title;
		this.content=content;
		this.range=range;
		this.startTime=startTime;
		this.finishTime=finishTime;
		this.getIn=getIn;
		this.getOut=getOut;
		this.img=img;
		this.sound=sound;
	}
	
	public Calendar getStartTime()
	{
		return timeStart;
	}
	
	public Calendar getEndTime()
	{
		return timeFinish;
	}
	
	public String getTitle()
	{
		return title;
	}
	
	public String getContent()
	{
		return content;
	}
	
	public GeoPoint getDestGeoPoint()
	{
		return destGeoPoint;
	}
	
	public double getDistance()
	{
		return range;
	}
	public void setTime(int year,int month,int day,int hourOfDay,int minute,int second){
		startTime+=Integer.toString(year)+"-"+Integer.toString(month)+"-"+Integer.toString(day)
			+"-"+Integer.toString(hourOfDay)+"-"+Integer.toString(minute)+"-"+Integer.toString(second);
	}
	public void setEndTime(int year,int month,int day,int hourOfDay,int minute,int second){
		finishTime+=Integer.toString(year)+"-"+Integer.toString(month)+"-"+Integer.toString(day)
			+"-"+Integer.toString(hourOfDay)+"-"+Integer.toString(minute)+"-"+Integer.toString(second);
	}
}
