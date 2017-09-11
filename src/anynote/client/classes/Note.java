package anynote.client.classes;

import com.google.android.maps.GeoPoint;

public class Note {
	public String userId;
	public int noteId;
	public String friends="";
	public String title="";
	public String content="";
	public String img="";
	public String sound="";
	public Note(){
		noteId=0;
		friends=null;
		title="";
		content="";
	}
	public void addFriend(long id){
		if(friends==null)
			friends=Long.toString(id);
		else
			friends+="_"+Long.toString(id);
	}
	public String getImg()
	{
		return img;
	}
	public String getSound()
	{
		return sound;
	}
}
