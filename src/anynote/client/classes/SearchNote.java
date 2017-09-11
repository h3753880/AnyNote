package anynote.client.classes;

public class SearchNote extends Note{
	public String time;
	public String upOrDown;
	public SearchNote(String title,String content,String friends,String time,String upOrDown)
	{
		
        this.friends=friends;
        this.title=title;
        this.content=content;
        this.time=time;
        this.upOrDown=upOrDown;
	}

}
