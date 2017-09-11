package anynote.client.classes;

public class FavPlace {
	private String placeName;
	private String longitude;
	private String latitude;
	
	public FavPlace(String placeName,String longitude,String latitude)
	{
		this.placeName=placeName;
		this.longitude=longitude;
		this.latitude=latitude;
	}
	
	public String getPlace()
	{
		return placeName;
	}
	public String getLong()
	{
		return longitude;
	}
	public String getLat()
	{
		return latitude;
	}
}
