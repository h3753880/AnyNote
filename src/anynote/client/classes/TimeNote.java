package anynote.client.classes;

import java.util.Calendar;

public class TimeNote extends Note {
	public String time="";
	public int cycle=0;
		public TimeNote()
		{}
	
        public TimeNote(int noteId, String userId,String friends, String title, String content, String time, int cycle,String img,String sound)
        {
            this.userId=userId;
            this.noteId=noteId;
            this.friends=friends;
            this.title=title;
            this.content=content;
            this.time=time;
            this.cycle=cycle;
            this.img=img;
            this.sound=sound;
        }
        
        
	public void setTime(int year,int month,int day,int hourOfDay,int minute,int second){
		time+=Integer.toString(year)+"-"+Integer.toString(month)+"-"+Integer.toString(day)
			+"-"+Integer.toString(hourOfDay)+"-"+Integer.toString(minute)+"-"+Integer.toString(second);
	}
}
