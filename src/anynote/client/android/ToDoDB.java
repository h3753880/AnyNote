package anynote.client.android;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.maps.GeoPoint;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import anynote.client.classes.*;

public class ToDoDB extends SQLiteOpenHelper {
	private final static String DATABASE_NAME = "AnyNote";
	private final static int DATABASE_VERSION = 35;
	private final static String TABLE_NAME1 = "time_note";
	private final static String TABLE_NAME2 = "geo_note";
	private final static String TABLE_NAME3 = "friend_db";
	private final static String TABLE_NAME4 = "fav_place";
	public final static String _id = "_id";
	public final static String FIELD_id = "noteId";
	public final static String FIELD_TEXT1 = "userId";
	public final static String FIELD_TEXT2 = "title";
	public final static String FIELD_TEXT3 = "content";
	public final static String FIELD_TEXT4 = "time";
	public final static String FIELD_TEXT5 = "friends";
	public final static String FIELD_TEXT6 = "cycle";
	public final static String FIELD_TEXT7 = "img";
	public final static String FIELD_TEXT8 = "sound";
	public final static String GEO_id = "noteId";
	public final static String GEO_TEXT1 = "userId";
	public final static String GEO_TEXT2 = "title";
	public final static String GEO_TEXT3 = "content";
	public final static String GEO_TEXT4 = "longitude";
	public final static String GEO_TEXT5 = "latitude";
	public final static String GEO_TEXT6 = "timeStart";
	public final static String GEO_TEXT7 = "timeEnd";
	public final static String GEO_TEXT8 = "range";
	public final static String GEO_TEXT9 = "friends";
	public final static String GEO_TEXT10 = "getIn";
	public final static String GEO_TEXT11 = "getOut";
	public final static String GEO_TEXT12 = "img";
	public final static String GEO_TEXT13 = "sound";
	public final static String GEO_TEXT14 = "city";
	public final static String Friend_id = "fId";
	public final static String Friend_name = "fname";
	
	public final static String FAV_TEXT1 = "userId";
	public final static String FAV_TEXT2 = "place";
	public final static String FAV_TEXT3 = "longitude";
	public final static String FAV_TEXT4 = "latitude";

	public final static String GEO_TEXT15 = "favPlace";
	
	public ToDoDB(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	@Override
	public void onCreate(SQLiteDatabase db) {
		/* 建立friend_db */
		String sql3 = "CREATE TABLE " + TABLE_NAME3 + " (" + _id
				+ " INTEGER primary key autoincrement, " + Friend_id
				+ " text, " + Friend_name + " text )";
		db.execSQL(sql3);
		/* 建立table time_note */
		String sql1 = "CREATE TABLE " + TABLE_NAME1 + " (" + _id
				+ " INTEGER primary key autoincrement, " + FIELD_id
				+ " INTEGER, " + " " + FIELD_TEXT1 + " text, " + " "
				+ FIELD_TEXT2 + " text, " + " " + FIELD_TEXT3 + " text, " + " "
				+ FIELD_TEXT4 + " text, " + " " + FIELD_TEXT5 + " text, " + " "
				+ FIELD_TEXT6 + " INTEGER, "+ " " + FIELD_TEXT7 + " text, " +  FIELD_TEXT8 + " text )";
		db.execSQL(sql1);
		/* 建立table geo_note */
		String sql2 = "CREATE TABLE " + TABLE_NAME2 + " (" + _id
				+ " INTEGER primary key autoincrement, " + GEO_id
				+ " INTEGER, " + " " + GEO_TEXT1 + " text, " + " " + GEO_TEXT2
				+ " text, " + " " + GEO_TEXT3 + " text, " + " " + GEO_TEXT4
				+ " REAL, " + " " + GEO_TEXT5 + " REAL, " + " " + GEO_TEXT6
				+ " text, " + " " + GEO_TEXT7 + " text, " + " " + GEO_TEXT8
				+ " REAL, " + " " + GEO_TEXT9 + " text, " + "  " + GEO_TEXT10
				+ " INTEGER, " + "  " + GEO_TEXT11 + " INTEGER, "+ " " + GEO_TEXT12 + " text, " + GEO_TEXT13 + " text ,"
				+ GEO_TEXT14 + " text, " + " " + GEO_TEXT15 + " text )";
		db.execSQL(sql2);
		/*建立table fav_place*/
		String sql4 = "CREATE TABLE " + TABLE_NAME4 + " (" + _id
				+ " INTEGER primary key autoincrement, " + " "
				+ FAV_TEXT1 + " text, " + " " + FAV_TEXT2 + " text, " + " " + FAV_TEXT3
				+ " text, " + " " + FAV_TEXT4
				+ " text )";
		db.execSQL(sql4);
	}

	// 新增喜愛點到DB
	public long insertFavPlace(String userId,String place,GeoPoint gp) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* 將新增的值放入ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(FAV_TEXT1, userId);
		cv.put(FAV_TEXT2, place);
		cv.put(FAV_TEXT3, Double.toString(gp.getLongitudeE6()/1E6) );
		cv.put(FAV_TEXT4, Double.toString(gp.getLatitudeE6()/1E6)  );
		
		long row = db.insert(TABLE_NAME4, null, cv);
		return row;
	}
	
	// 取出所有喜愛點
	public ArrayList<FavPlace> selectFavPlace()
	{
		String tempPlace="";
		String tempLong="";
		String tempLat="";
		ArrayList<FavPlace> place=new ArrayList<FavPlace>();
		SQLiteDatabase db = this.getReadableDatabase();
		if(AnyNoteActivity.fbId==null || AnyNoteActivity.fbId.equals(""))
		{
			return null;
		}
		String query = "SELECT " + FAV_TEXT2 + " , " + FAV_TEXT3 + " , " + FAV_TEXT4
				+ " FROM fav_place WHERE " + FAV_TEXT1 + " = " + AnyNoteActivity.fbId;
		Cursor cursor = db.rawQuery(query, null);
		
		if (cursor != null) {
			cursor.moveToFirst();// 將指標移到第一筆資料
			if(cursor.getCount()<=0)
				return null;
			do {
				Log.v("ToDoDB", "SelectFavPlace");
				
				tempPlace = cursor.getString(0);  
				tempLong=cursor.getString(1);
				tempLat=cursor.getString(2);
					
				Log.v("ToDoDB", tempPlace);
				Log.v("ToDoDB", tempLong);
				Log.v("ToDoDB", tempLat);
				
				place.add(new FavPlace(tempPlace,tempLong,tempLat));
			} while (cursor.moveToNext());
		}
		return place;
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		String sql1 = "DROP TABLE IF EXISTS " + TABLE_NAME1;
		db.execSQL(sql1);
		String sql2 = "DROP TABLE IF EXISTS " + TABLE_NAME2;
		db.execSQL(sql2);
		String sql3 = "DROP TABLE IF EXISTS " + TABLE_NAME3;
		db.execSQL(sql3);
		onCreate(db);
	}

	// 新增朋友名字到DB
	public long insertNewFriendName(String name,String id) {
		System.out.println("name:"+name+"id:"+id);
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT " + Friend_name + " FROM " + TABLE_NAME3 + " WHERE "
				+ Friend_id + "= '" + id+"'" ;
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null&& cursor.getCount() == 0) {
			/* 將新增的值放入ContentValues */
			System.out.println("isNull");
			ContentValues cv = new ContentValues();
			cv.put(Friend_id, id);
			cv.put(Friend_name, name);

			long row = db.insert(TABLE_NAME3, null, cv);
			return row;
		}
		return 0;
	}
	// 查詢朋友名字
		public String searchFriendName(String id) {
			SQLiteDatabase db = this.getWritableDatabase();
			String query = "SELECT " + Friend_name + " FROM " + TABLE_NAME3 + " WHERE "
					+ Friend_id + "= '" + id+"'" ;
			Cursor cursor = db.rawQuery(query, null);
			if (cursor != null&& cursor.getCount() > 0) {
				cursor.moveToFirst(); // 將指標移到第一筆資料
				String name = cursor.getString(0);
				System.out.println("Name::"+name);
				cursor.close();
				db.close();
				return name;
			} else {
				return "0";
			}

		}	
		// 查詢時間備忘img
		public String getTimeImgById(int id) {
			SQLiteDatabase db = this.getWritableDatabase();
			String query = "SELECT " + FIELD_TEXT7 + " FROM " + TABLE_NAME1 + " WHERE "
					+ _id + "= " + id ;
			Cursor cursor = db.rawQuery(query, null);
			if (cursor != null&& cursor.getCount() > 0) {
				cursor.moveToFirst(); // 將指標移到第一筆資料
				String img = cursor.getString(0);
				System.out.println("img::"+img);
				cursor.close();
				db.close();
				return img;
			} else {
				return "0";
			}
		}	
		// 查詢地理備忘img
		public String getGeoImgById(int id) {
			SQLiteDatabase db = this.getWritableDatabase();
			String query = "SELECT " + GEO_TEXT12 + " FROM " + TABLE_NAME2 + " WHERE "
					+ _id + "= " + id ;
			Cursor cursor = db.rawQuery(query, null);
			if (cursor != null&& cursor.getCount() > 0) {
				cursor.moveToFirst(); // 將指標移到第一筆資料
				String img = cursor.getString(0);
				System.out.println("img::"+img);
				cursor.close();
				db.close();
				return img;
			} else {
				return "0";
			}
		}	
		// 查詢時間備忘sound
		public String getTimeSoundById(int id) {
			SQLiteDatabase db = this.getWritableDatabase();
			String query = "SELECT " + FIELD_TEXT8 + " FROM " + TABLE_NAME1 + " WHERE "
					+ _id + "= " + id ;
			Cursor cursor = db.rawQuery(query, null);
			if (cursor != null&& cursor.getCount() > 0) {
				cursor.moveToFirst(); // 將指標移到第一筆資料
				String sound = cursor.getString(0);
				System.out.println("sound::"+sound);
				cursor.close();
				db.close();
				return sound;
			} else {
				return "0";
			}
		}			
		// 查詢地理備忘sound
		public String getGeoSoundById(int id) {
			SQLiteDatabase db = this.getWritableDatabase();
			String query = "SELECT " + GEO_TEXT13 + " FROM " + TABLE_NAME2 + " WHERE "
					+ _id + "= " + id ;
			Cursor cursor = db.rawQuery(query, null);
			if (cursor != null&& cursor.getCount() > 0) {
				cursor.moveToFirst(); // 將指標移到第一筆資料
				String sound = cursor.getString(0);
				System.out.println("sound::"+sound);
				cursor.close();
				db.close();
				return sound;
			} else {
				return "0";
			}
		}	
	private Cursor select() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_NAME1, null, null, null, null, null,
				null);
		if (cursor != null && cursor.getCount() > 0)
			cursor.moveToFirst();
		return cursor;
	}

	public ArrayList<Map<String, Object>> timeSelect() {

		Map<String, Object> map;
		Cursor cursor;
		cursor = select();
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();// 將指標移到第一筆資料
			do {
				Log.v("test", "TodoDBadd");
				map = new HashMap<String, Object>();
				map.put("_id", cursor.getInt(0));
				map.put("noteId", cursor.getInt(1));
				map.put("userId", cursor.getString(2));
				map.put("title", cursor.getString(3));
				map.put("content", cursor.getString(4));
				map.put("time", cursor.getString(5));
				map.put("friends", cursor.getString(6));
				map.put("cycle", cursor.getInt(7));
				map.put("img", cursor.getString(8));
				map.put("sound", cursor.getString(9));
				list.add(map);

			} while (cursor.moveToNext());
		}
		// else return null;

		return list;

	}

	public ArrayList<Map<String, Object>> geoSelect() {

		Map<String, Object> map;
		Cursor cursor;
		cursor = Gselect();
		ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();// 將指標移到第一筆資料
			do {
				/*
				 * public final static String GEO_id = "noteId"; public final
				 * static String GEO_TEXT1 = "userId"; public final static
				 * String GEO_TEXT2 = "title"; public final static String
				 * GEO_TEXT3 = "content"; public final static String GEO_TEXT4 =
				 * "longitude"; public final static String GEO_TEXT5 =
				 * "latitude"; public final static String GEO_TEXT6 =
				 * "timeStart"; public final static String GEO_TEXT7 =
				 * "timeEnd"; public final static String GEO_TEXT8 = "range";
				 * public final static String GEO_TEXT9 = "friends"; public
				 * final static String GEO_TEXT10 = "getIn"; public final static
				 * String GEO_TEXT11 = "getOut";
				 */
				Log.v("test", "TodoDBadd");
				map = new HashMap<String, Object>();
				map.put("_id", cursor.getInt(0));
				map.put("noteId", cursor.getInt(1));
				map.put("userId", cursor.getString(2));
				map.put("title", cursor.getString(3));
				map.put("content", cursor.getString(4));
				map.put("longitude", cursor.getString(5));
				map.put("latitude", cursor.getString(6));
				map.put("timeStart", cursor.getString(7));
				map.put("timeEnd", cursor.getString(8));
				map.put("range", cursor.getInt(9));
				map.put("friends", cursor.getString(10));
				map.put("getIn", cursor.getInt(11));
				map.put("getOut", cursor.getInt(12));
				map.put("img", cursor.getString(13));
				map.put("sound", cursor.getString(14));
				list.add(map);

			} while (cursor.moveToNext());
		}
		// else return null;

		return list;

	}

	public Cursor Gselect() {
		SQLiteDatabase db = this.getWritableDatabase();
		Cursor cursor = db.query(TABLE_NAME2, null, null, null, null, null,
				null);
		if (cursor != null)
			cursor.moveToFirst();
		return cursor;
	}

	public ArrayList<TimeNote> selectTime(int oldMaxId)// 選擇欲上傳的timenote
	{
		TimeNote timeNote = new TimeNote();
		ArrayList<TimeNote> result = new ArrayList<TimeNote>();
		SQLiteDatabase db = this.getReadableDatabase();
		// String query= "SELECT * FROM time_note";
		 String query= "SELECT " +FIELD_id+", "
		   		 + FIELD_TEXT1+", "
		   		 + FIELD_TEXT2+", "
		   		 + FIELD_TEXT3+", "
		   		 + FIELD_TEXT4+", "
		   		 + FIELD_TEXT5+", "
		   		 + FIELD_TEXT6+", "
		   		 + FIELD_TEXT7+", "
		   		 + FIELD_TEXT8
		   		+" FROM time_note WHERE "+FIELD_id+ " > "+oldMaxId+" AND "+FIELD_TEXT1+" = "+AnyNoteActivity.fbId;
		Cursor cursor = db.rawQuery(query, null);
		System.out.println("oldMaxId:" + Integer.toString(oldMaxId));
		// Log.v("test",cursor.toString());
		if (cursor.moveToFirst()) {
			// cursor.moveToFirst();
			// 將指標移到第一筆資料

			do {
				Log.v("test", "TodoDBadd");
				timeNote.noteId = cursor.getInt(0);
				timeNote.userId = cursor.getString(1);
				timeNote.title = cursor.getString(2);
				timeNote.content = cursor.getString(3);
				timeNote.time = cursor.getString(4);
				System.out.println("time" + cursor.getString(4));
				timeNote.friends = cursor.getString(5);
				timeNote.cycle = cursor.getInt(6);
		        timeNote.img=cursor.getString(7);
		        timeNote.sound=cursor.getString(8);
				result.add(timeNote);
			} while (cursor.moveToNext());

		}

		return result;
	}

	public ArrayList<GeoNote> selectGeo(int oldMaxId)// 選擇欲上傳的geonote(同步上傳時)
	{
		GeoNote geoNote = new GeoNote();
		ArrayList<GeoNote> result = new ArrayList<GeoNote>();
		SQLiteDatabase db = this.getReadableDatabase();

		String query = "SELECT " + GEO_id + ", " + GEO_TEXT1 + ", " + GEO_TEXT2
				+ ", " + GEO_TEXT3 + ", " + GEO_TEXT4 + ", " + GEO_TEXT5 + ", "
				+ GEO_TEXT6 + ", " + GEO_TEXT7 + ", " + GEO_TEXT8 + ", "
				+ GEO_TEXT9 + ", " + GEO_TEXT10 + ", " + GEO_TEXT11+ ", " + GEO_TEXT12+ ", " 
				+ GEO_TEXT13 + ", " 
				+ GEO_TEXT14 + ", "
				+ GEO_TEXT15 + " "+ //喜愛點
				" FROM geo_note WHERE " + GEO_id + " > " + oldMaxId + " AND "
				+ GEO_TEXT1 + " = " + AnyNoteActivity.fbId;
		Cursor cursor = db.rawQuery(query, null);
		System.out.println("oldMaxId:" + Integer.toString(oldMaxId));

		if (cursor != null) {
			cursor.moveToFirst();// 將指標移到第一筆資料
			do {
				Log.v("test", "TodoDBadd");

				geoNote.noteId = cursor.getInt(0);
				geoNote.userId = cursor.getString(1);
				geoNote.title = cursor.getString(2);
				geoNote.content = cursor.getString(3);
				geoNote.Longitude = cursor.getDouble(4);
				geoNote.Latitude = cursor.getDouble(5);
				geoNote.startTime = cursor.getString(6);
				geoNote.finishTime = cursor.getString(7);
				geoNote.range = cursor.getDouble(8);
				geoNote.friends = cursor.getString(9);
				if (cursor.getInt(10) == 1)
					geoNote.getIn = true;
				else
					geoNote.getIn = false;
				if (cursor.getInt(11) == 1)
					geoNote.getOut = true;
				else
					geoNote.getOut = false;
				geoNote.img=cursor.getString(12);
				geoNote.sound=cursor.getString(13);
				geoNote.city=cursor.getString(14);
				geoNote.setFavPlace(cursor.getString(15));//喜愛點存入
				result.add(geoNote);
			} while (cursor.moveToNext());

		}
		return result;
	}

	public void dropTable() {
		SQLiteDatabase db = this.getReadableDatabase();
		String sql = "DROP TABLE IF EXISTS " + TABLE_NAME1;
		String sql2 = "DROP TABLE IF EXISTS " + TABLE_NAME2;
		db.execSQL(sql);
		db.execSQL(sql2);
	}

	// 新增時間提醒到DB
	public long insertTimeNote(TimeNote note) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* 將新增的值放入ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(FIELD_id, note.noteId);
		cv.put(FIELD_TEXT1, note.userId);
		cv.put(FIELD_TEXT2, note.title);
		cv.put(FIELD_TEXT3, note.content);
		cv.put(FIELD_TEXT4, note.time);
		cv.put(FIELD_TEXT5, note.friends);
		cv.put(FIELD_TEXT6, note.cycle);
		cv.put(FIELD_TEXT7, note.img);
		cv.put(FIELD_TEXT8, note.sound);
		long row = db.insert(TABLE_NAME1, null, cv);
		return row;
	}

	// 新增地理提醒到DB
	public long insertGeoNote(GeoNote note) {
		SQLiteDatabase db = this.getWritableDatabase();
		/* 將新增的值放入ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(GEO_id, note.noteId);
		cv.put(GEO_TEXT1, note.userId);
		cv.put(GEO_TEXT2, note.title);
		cv.put(GEO_TEXT3, note.content);
		cv.put(GEO_TEXT4, note.Longitude);
		cv.put(GEO_TEXT5, note.Latitude);
		cv.put(GEO_TEXT6, note.startTime);
		cv.put(GEO_TEXT7, note.finishTime);
		cv.put(GEO_TEXT8, note.range);
		cv.put(GEO_TEXT9, note.friends);
		cv.put(GEO_TEXT10, note.getIn);
		cv.put(GEO_TEXT11, note.getOut);
		cv.put(GEO_TEXT12, note.img);
		cv.put(GEO_TEXT13, note.sound);
		cv.put(GEO_TEXT14, note.city);
		cv.put(GEO_TEXT15, note.getFavPlace());
		long row = db.insert(TABLE_NAME2, null, cv);
		return row;
	}

	public int maxTimeId() {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT MAX(" + FIELD_id + ") AS max_id FROM "
				+ TABLE_NAME1 + " WHERE " + FIELD_TEXT1 + " = "
				+ AnyNoteActivity.fbId;
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null) {
			cursor.moveToFirst(); // 將指標移到第一筆資料
			int output = cursor.getInt(0);
			cursor.close();
			db.close();
			return output;
		}

		else {
			return 0;
		}

	}

	public int maxGeoId() {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT MAX(" + GEO_id + ") AS max_id FROM "
				+ TABLE_NAME2 + " WHERE " + GEO_TEXT1 + " = "
				+ AnyNoteActivity.fbId;
		Cursor cursor = db.rawQuery(query, null);
		if (cursor != null) {
			cursor.moveToFirst(); // 將指標移到第一筆資料
			int output = cursor.getInt(0);
			cursor.close();
			db.close();
			return output;
		}

		else {
			return 0;
		}

	}

	public void loginUpdate(String id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_TEXT1 + " = ? ";
		String[] whereValue = { "0" };
		/* 將修改的值放入ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(FIELD_TEXT1, id);
		cv.put(FIELD_TEXT5, id);
		db.update(TABLE_NAME1, cv, where, whereValue);
		
		where = GEO_TEXT1 + " = ? ";
		/* 將修改的值放入ContentValues */
		ContentValues cv2 = new ContentValues();
		cv2.put(GEO_TEXT1, id);
		cv2.put(GEO_TEXT9, id);
		db.update(TABLE_NAME2, cv2, where, whereValue);		
		
		db.close();

		System.out.println("loginUpdate");

	}

	public void syncdelete(int noteId, String userId) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_id + " = ? " + " AND " + FIELD_TEXT1 + " = ? ";
		String[] whereValue = { Integer.toString(noteId), userId };
		db.delete(TABLE_NAME1, where, whereValue);
		db.close();
	}

	public void syncGeodelete(int noteId, String userId) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = GEO_id + " = ? " + " AND " + GEO_TEXT1 + " = ? ";
		String[] whereValue = { Integer.toString(noteId), userId };
		db.delete(TABLE_NAME2, where, whereValue);
		db.close();
	}

	public void delete(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = _id + " = ?";
		String[] whereValue = { Integer.toString(id) };
		db.delete(TABLE_NAME1, where, whereValue);
		db.close();
	}

	public void deleteGeo(int id) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = _id + " = ?";
		String[] whereValue = { Integer.toString(id) };
		db.delete(TABLE_NAME2, where, whereValue);
		db.close();
	}

	public void updateTimeNote(int id, TimeNote note) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = _id + " = ? ";
		String[] whereValue = { Integer.toString(id) };
		/* 將修改的值放入ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(FIELD_TEXT2, note.title);
		cv.put(FIELD_TEXT3, note.content);
		cv.put(FIELD_TEXT4, note.time);
		cv.put(FIELD_TEXT6, note.cycle);
		db.update(TABLE_NAME1, cv, where, whereValue);
	}
	public void updateGeoNote(int id, GeoNote note) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = _id + " = ? ";
		String[] whereValue = { Integer.toString(id) };
		/* 將修改的值放入ContentValues *//*
		 * 
		 * 	public final static String GEO_TEXT2 = "title";
	public final static String GEO_TEXT3 = "content";
	public final static String GEO_TEXT6 = "timeStart";
	public final static String GEO_TEXT7 = "timeEnd";
	public final static String GEO_TEXT8 = "range";
		 */
		ContentValues cv = new ContentValues();
		cv.put(GEO_TEXT2, note.title);
		cv.put(GEO_TEXT3, note.content);
		cv.put(GEO_TEXT6, note.startTime);
		cv.put(GEO_TEXT7, note.finishTime);
		cv.put(GEO_TEXT8, note.range);
		db.update(TABLE_NAME2, cv, where, whereValue);
	}

	public void syncUpdateTimeNote(int id, String title, String content,
			String userId, String time) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = FIELD_id + " = ? AND " + FIELD_TEXT1 + " = ? ";
		String[] whereValue = { Integer.toString(id), userId };
		/* 將修改的值放入ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(FIELD_TEXT2, title);
		cv.put(FIELD_TEXT3, content);
		cv.put(FIELD_TEXT4, time);
		db.update(TABLE_NAME1, cv, where, whereValue);
		db.close();
	}

	public void syncUpdateGeoNote(int id, String title, String content,
			String userId, String startTime, String finishTime, boolean getIn,
			boolean getOut, double range) {
		SQLiteDatabase db = this.getWritableDatabase();
		String where = GEO_id + " = ? AND " + GEO_TEXT1 + " = ? ";
		String[] whereValue = { Integer.toString(id), userId };
		/* 將修改的值放入ContentValues */
		ContentValues cv = new ContentValues();
		cv.put(GEO_TEXT2, title);
		cv.put(GEO_TEXT3, content);
		cv.put(GEO_TEXT6, startTime);
		cv.put(GEO_TEXT7, finishTime);
		cv.put(GEO_TEXT8, range);
		if (getIn)
			cv.put(GEO_TEXT10, 1);
		else
			cv.put(GEO_TEXT10, 0);

		if (getOut)
			cv.put(GEO_TEXT11, 1);
		else
			cv.put(GEO_TEXT11, 0);
		db.update(TABLE_NAME2, cv, where, whereValue);
		db.close();
	}

	public int timeNoteAlarmId(TimeNote timeNote) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT " + _id + " FROM " + TABLE_NAME1 + " WHERE "
				+ FIELD_TEXT1 + "=" + timeNote.userId + " AND " + FIELD_id
				+ "=" + timeNote.noteId;
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null ) {
			cursor.moveToFirst(); // 將指標移到第一筆資料
			int output = cursor.getInt(0);
			cursor.close();
			db.close();
			return output;
		} else {
			cursor.close();
			db.close();
			return 0;
		}

	}

	public int geoNoteAlarmId(GeoNote geoNote) {
		SQLiteDatabase db = this.getWritableDatabase();
		String query = "SELECT " + _id + " FROM " + TABLE_NAME2 + " WHERE "
				+ GEO_TEXT1 + "=" + geoNote.userId + " AND " + GEO_id + "="
				+ geoNote.noteId;
		Cursor cursor = db.rawQuery(query, null);

		if (cursor != null) {
			cursor.moveToFirst(); // 將指標移到第一筆資料
			int output = cursor.getInt(0);
			cursor.close();
			db.close();
			return output;
		} else {
			cursor.close();
			db.close();
			
			return 0;
		}

	}

	
	public ArrayList<Map<String, Object>> timeNoteSearch(SearchNote note) {
		Map<String, Object> map;
		Cursor cursor;
		cursor = select();
		Calendar dateTime = Calendar.getInstance();
		dateTime = changeToCalendar(note.time);
		Calendar compareDateTime = Calendar.getInstance();
		dateTime = changeToCalendar(note.time);
		System.out.println("search" + note.title);
		System.out.println("searchContent" + note.content);
		System.out.println("friends" + note.friends);
		System.out.println("time" + note.time);
		System.out.println(note.upOrDown);
		HashSet<Map<String, Object>> list = new HashSet<Map<String, Object>>();
		ArrayList<Map<String, Object>> listReturn = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> listTitle = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> listContent = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> listFriends = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> listTime = new ArrayList<Map<String, Object>>();
		boolean isInFriend = false;
		String[] friend = note.friends.split("_");

		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();// 將指標移到第一筆資料
			do {
				//System.out.println("title:");
				if (cursor.getString(3).indexOf(note.title) != -1
						&& !note.title.equals("")) {

					map = new HashMap<String, Object>();
					map.put("_id", cursor.getInt(0));
					map.put("noteId", cursor.getInt(1));
					map.put("userId", cursor.getString(2));
					map.put("title", cursor.getString(3));
					map.put("content", cursor.getString(4));
					map.put("time", cursor.getString(5));
					map.put("friends", cursor.getString(6));
					map.put("cycle", cursor.getInt(7));
					map.put("img", cursor.getString(8));
					listTitle.add(map);
					// System.out.println("title:" + title);

				}
				if (cursor.getString(4).indexOf(note.content) != -1
						&& !note.content.equals("")) {
					map = new HashMap<String, Object>();
					map.put("_id", cursor.getInt(0));
					map.put("noteId", cursor.getInt(1));
					map.put("userId", cursor.getString(2));
					map.put("title", cursor.getString(3));
					map.put("content", cursor.getString(4));
					map.put("time", cursor.getString(5));
					map.put("friends", cursor.getString(6));
					map.put("cycle", cursor.getInt(7));
					map.put("img", cursor.getString(8));
					listContent.add(map);

				}
				for (String bean : friend) {
					if ((cursor.getString(2).indexOf(bean) != -1||cursor.getString(6).indexOf(bean) != -1)
							&& !note.friends.equals("")) {
						map = new HashMap<String, Object>();
						map.put("_id", cursor.getInt(0));
						map.put("noteId", cursor.getInt(1));
						map.put("userId", cursor.getString(2));
						map.put("title", cursor.getString(3));
						map.put("content", cursor.getString(4));
						map.put("time", cursor.getString(5));
						map.put("friends", cursor.getString(6));
						map.put("cycle", cursor.getInt(7));
						map.put("img", cursor.getString(8));
						listFriends.add(map);
						break;

					}
				}
				if (!note.upOrDown.equals("無")) {
					if (note.upOrDown.equals("以後")) {

						compareDateTime = changeToCalendar(cursor.getString(5));
						if (compareDateTime.compareTo(dateTime) > 0) {
							System.out.println("compare:"
									+ compareDateTime.getTime().toString());
							System.out.println("no:"
									+ dateTime.getTime().toString());

							map = new HashMap<String, Object>();
							map.put("_id", cursor.getInt(0));
							map.put("noteId", cursor.getInt(1));
							map.put("userId", cursor.getString(2));
							map.put("title", cursor.getString(3));
							map.put("content", cursor.getString(4));
							map.put("time", cursor.getString(5));
							map.put("friends", cursor.getString(6));
							map.put("cycle", cursor.getInt(7));
							map.put("img", cursor.getString(8));
							listTime.add(map);

						}
					} else if (note.upOrDown.equals("以前")) {

						compareDateTime = changeToCalendar(cursor.getString(5));
						if (compareDateTime.compareTo(dateTime) < 0) {
							map = new HashMap<String, Object>();
							map.put("_id", cursor.getInt(0));
							map.put("noteId", cursor.getInt(1));
							map.put("userId", cursor.getString(2));
							map.put("title", cursor.getString(3));
							map.put("content", cursor.getString(4));
							map.put("time", cursor.getString(5));
							map.put("friends", cursor.getString(6));
							map.put("cycle", cursor.getInt(7));
							map.put("img", cursor.getString(8));
							listTime.add(map);

						}

					}
				}

			} while (cursor.moveToNext());
		}
		System.out.print(listTitle);
		if (!listTitle.isEmpty()) {
			System.out.print("listTitle");
			for (int i = 0; i < listTitle.size(); i++) {
				System.out.println(listTitle.size());
				if (!note.content.equals("")
						&& !(listTitle.get(i).get("content").toString()
								.indexOf(note.content) != -1)) {
					listTitle.remove(i);
					System.out.println("content");
					i = 0;
				} else if (!note.upOrDown.equals("無")) {
					if (note.upOrDown.equals("以後")) {

						compareDateTime = changeToCalendar(listTitle.get(i)
								.get("time").toString());
						if (compareDateTime.compareTo(dateTime) < 0) {
							System.out.print("time1");
							listTitle.remove(i);
							i--;

						}
					} else if (note.upOrDown.equals("以前")) {

						compareDateTime = changeToCalendar(listTitle.get(i)
								.get("time").toString());
						if (compareDateTime.compareTo(dateTime) > 0) {
							System.out.println("time2");
							listTitle.remove(i);
							i--;

						}

					}
				} else {
					for (String friendbean : friend) {
						if (listTitle.get(i).get("userId").toString()
								.contains(friendbean)||listTitle.get(i).get("friends").toString()
								.contains(friendbean)
								|| !friendbean.equals("")) {
							isInFriend = true;

						}
					}
					if (!isInFriend) {
						System.out.println("friend");
						listTitle.remove(i);
						i--;
					}

				}
			}

			return listTitle;
		}
		else if (!listContent.isEmpty()) {
			System.out.print("listContent");
			for (int i = 0; i < listContent.size(); i++) {
				if (!note.title.equals("")
						&& !(listContent.get(i).get("title").toString()
								.indexOf(note.title) != -1)) {
					listContent.remove(i);
					i --;
				} else if (!note.upOrDown.equals("無")) {
					if (note.upOrDown.equals("以後")) {

						compareDateTime = changeToCalendar(listContent.get(i)
								.get("time").toString());
						if (compareDateTime.compareTo(dateTime) < 0) {

							listContent.remove(i);
							i --;

						}
					} else if (note.upOrDown.equals("以前")) {

						compareDateTime = changeToCalendar(listContent.get(i)
								.get("time").toString());
						if (compareDateTime.compareTo(dateTime) > 0) {

							listContent.remove(i);
							i --;

						}

					}
				} else {
					for (String friendbean : friend) {
						if (listContent.get(i).get("userId").toString()
								.contains(friendbean)||listContent.get(i).get("friends").toString()
								.contains(friendbean)
								|| friendbean.equals("")) {
							isInFriend = true;

						}

					}
					if (!isInFriend) {
						listContent.remove(i);
						i --;
					}

				}

			}
			return listContent;

		} else if (!listFriends.isEmpty()) {
			System.out.print("listFriends");
			for (int i = 0; i < listFriends.size(); i++) {
				if (!note.title.equals("")
						&& !(listFriends.get(i).get("title").toString()
								.indexOf(note.title) != -1)) {
					listFriends.remove(i);
					i --;
				} else if (!note.content.equals("")
						&& !(listFriends.get(i).get("content").toString()
								.indexOf(note.content) != -1)) {
					listFriends.remove(i);
					i --;
				} else if (!note.upOrDown.equals("無")) {
					if (note.upOrDown.equals("以後")) {

						compareDateTime = changeToCalendar(listFriends.get(i)
								.get("time").toString());
						if (compareDateTime.compareTo(dateTime) < 0) {

							listFriends.remove(i);
							i --;

						}
					} else if (note.upOrDown.equals("以前")) {

						compareDateTime = changeToCalendar(listContent.get(i)
								.get("time").toString());
						if (compareDateTime.compareTo(dateTime) > 0) {

							listFriends.remove(i);
							i --;

						}

					}
				}

			}
			return listFriends;

		} else if (!listTime.isEmpty()) {
			System.out.print("listTime");
			for (int i = 0; i < listTime.size(); i++) {
				if (!note.title.equals("")
						&& !(listTime.get(i).get("title").toString()
								.indexOf(note.title) != -1)) {
					listTime.remove(i);
					i --;
				} else if (!note.content.equals("")
						&& !(listTime.get(i).get("content").toString()
								.indexOf(note.content) != -1)) {
					listTime.remove(i);
					i --;
				} else {
					for (String friendbean : friend) {
						if (listTime.get(i).get("userId").toString()
								.contains(friendbean)||listTime.get(i).get("friends").toString()
								.contains(friendbean)
								|| friendbean.equals("")) {
							isInFriend = true;

						}
					}
					if (!isInFriend) {
						listTime.remove(i);
						i --;
					}

				}
			}

			return listTime;
		}

		return listReturn;

	}
//地裡提醒搜尋
	public ArrayList<Map<String, Object>> geoNoteSearch(SearchNote note) {
		Map<String, Object> map;
		Cursor cursor;
		cursor = Gselect();
		Calendar dateTime = Calendar.getInstance();
		dateTime = changeToCalendar(note.time);
		Calendar compareDateTime = Calendar.getInstance();
		dateTime = changeToCalendar(note.time);
		System.out.println("search" + note.title);
		System.out.println("searchContent" + note.content);
		System.out.println("friends" + note.friends);
		System.out.println("time" + note.time);
		System.out.println(note.upOrDown);
		ArrayList<Map<String, Object>> listReturn = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> listTitle = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> listContent = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> listFriends = new ArrayList<Map<String, Object>>();
		ArrayList<Map<String, Object>> listTime = new ArrayList<Map<String, Object>>();
		boolean isInFriend = false;
		String[] friend = note.friends.split("_");
/*
 * public final static String GEO_id = "noteId";
	public final static String GEO_TEXT1 = "userId";
	public final static String GEO_TEXT2 = "title";
	public final static String GEO_TEXT3 = "content";
	public final static String GEO_TEXT4 = "longitude";
	public final static String GEO_TEXT5 = "latitude";
	public final static String GEO_TEXT6 = "timeStart";
	public final static String GEO_TEXT7 = "timeEnd";
	public final static String GEO_TEXT8 = "range";
	public final static String GEO_TEXT9 = "friends";
	public final static String GEO_TEXT10 = "getIn";
	public final static String GEO_TEXT11 = "getOut";
	public final static String GEO_TEXT12 = "img";
 */
		if (cursor != null && cursor.getCount() > 0) {
			cursor.moveToFirst();// 將指標移到第一筆資料
			do {
				// System.out.println("title:"+title);
				if (cursor.getString(3).indexOf(note.title) != -1
						&& !note.title.equals("")) {

					map = new HashMap<String, Object>();
					map.put("_id", cursor.getInt(0));
					map.put("noteId", cursor.getInt(1));
					map.put("userId", cursor.getString(2));
					map.put("title", cursor.getString(3));
					map.put("content", cursor.getString(4));
					map.put("longitude", cursor.getString(5));
					map.put("latitude", cursor.getString(6));
					map.put("timeStart", cursor.getString(7));
					map.put("timeEnd", cursor.getString(8));
					map.put("range", cursor.getInt(9));
					map.put("friends", cursor.getInt(10));
					map.put("getIn", cursor.getInt(11));
					map.put("getOut", cursor.getInt(12));
					map.put("img", cursor.getString(13));
					listTitle.add(map);
					// System.out.println("title:" + title);

				}
				if (cursor.getString(4).indexOf(note.content) != -1
						&& !note.content.equals("")) {
					map = new HashMap<String, Object>();
					map.put("_id", cursor.getInt(0));
					map.put("noteId", cursor.getInt(1));
					map.put("userId", cursor.getString(2));
					map.put("title", cursor.getString(3));
					map.put("content", cursor.getString(4));
					map.put("longitude", cursor.getString(5));
					map.put("latitude", cursor.getString(6));
					map.put("timeStart", cursor.getString(7));
					map.put("timeEnd", cursor.getString(8));
					map.put("range", cursor.getInt(9));
					map.put("friends", cursor.getInt(10));
					map.put("getIn", cursor.getInt(11));
					map.put("getOut", cursor.getInt(12));
					map.put("img", cursor.getString(13));
					listContent.add(map);

				}
				for (String bean : friend) {
					if ((cursor.getString(2).indexOf(bean) != -1||cursor.getString(10).indexOf(bean) != -1)
							&& !note.friends.equals("")) {
						map = new HashMap<String, Object>();
						map.put("_id", cursor.getInt(0));
						map.put("noteId", cursor.getInt(1));
						map.put("userId", cursor.getString(2));
						map.put("title", cursor.getString(3));
						map.put("content", cursor.getString(4));
						map.put("longitude", cursor.getString(5));
						map.put("latitude", cursor.getString(6));
						map.put("timeStart", cursor.getString(7));
						map.put("timeEnd", cursor.getString(8));
						map.put("range", cursor.getInt(9));
						map.put("friends", cursor.getInt(10));
						map.put("getIn", cursor.getInt(11));
						map.put("getOut", cursor.getInt(12));
						map.put("img", cursor.getString(13));
						listFriends.add(map);
						break;

					}
				}
				if (!note.upOrDown.equals("無")) {
					if (note.upOrDown.equals("以後")) {

						compareDateTime = changeToCalendar(cursor.getString(7));
						if (compareDateTime.compareTo(dateTime) > 0) {
							System.out.println("compare:"
									+ compareDateTime.getTime().toString());
							System.out.println("no:"
									+ dateTime.getTime().toString());

							map = new HashMap<String, Object>();
							map.put("_id", cursor.getInt(0));
							map.put("noteId", cursor.getInt(1));
							map.put("userId", cursor.getString(2));
							map.put("title", cursor.getString(3));
							map.put("content", cursor.getString(4));
							map.put("longitude", cursor.getString(5));
							map.put("latitude", cursor.getString(6));
							map.put("timeStart", cursor.getString(7));
							map.put("timeEnd", cursor.getString(8));
							map.put("range", cursor.getInt(9));
							map.put("friends", cursor.getInt(10));
							map.put("getIn", cursor.getInt(11));
							map.put("getOut", cursor.getInt(12));
							map.put("img", cursor.getString(13));
							listTime.add(map);

						}
					} else if (note.upOrDown.equals("以前")) {

						compareDateTime = changeToCalendar(cursor.getString(7));
						if (compareDateTime.compareTo(dateTime) < 0) {
							
							map = new HashMap<String, Object>();
							map.put("_id", cursor.getInt(0));
							map.put("noteId", cursor.getInt(1));
							map.put("userId", cursor.getString(2));
							map.put("title", cursor.getString(3));
							map.put("content", cursor.getString(4));
							map.put("longitude", cursor.getString(5));
							map.put("latitude", cursor.getString(6));
							map.put("timeStart", cursor.getString(7));
							map.put("timeEnd", cursor.getString(8));
							map.put("range", cursor.getInt(9));
							map.put("friends", cursor.getInt(10));
							map.put("getIn", cursor.getInt(11));
							map.put("getOut", cursor.getInt(12));
							map.put("img", cursor.getString(13));
							listTime.add(map);

						}

					}
				}

			} while (cursor.moveToNext());
		}
		// System.out.print(listTitle);
		if (!listTitle.isEmpty()) {
			System.out.print("listTitle");
			for (int i = 0; i < listTitle.size(); i++) {
				System.out.println(listTitle.size());
				if (!note.content.equals("")
						&& !(listTitle.get(i).get("content").toString()
								.indexOf(note.content) != -1)) {
					listTitle.remove(i);
					System.out.println("content");
					i = 0;
				} else if (!note.upOrDown.equals("無")) {
					if (note.upOrDown.equals("以後")) {

						compareDateTime = changeToCalendar(listTitle.get(i)
								.get("time").toString());
						if (compareDateTime.compareTo(dateTime) < 0) {
							System.out.print("time1");
							listTitle.remove(i);
							i--;

						}
					} else if (note.upOrDown.equals("以前")) {

						compareDateTime = changeToCalendar(listTitle.get(i)
								.get("time").toString());
						if (compareDateTime.compareTo(dateTime) > 0) {
							System.out.println("time2");
							listTitle.remove(i);
							i--;

						}

					}
				} else {
					for (String friendbean : friend) {
						if (listTitle.get(i).get("userId").toString()
								.contains(friendbean)||listTitle.get(i).get("friends").toString()
								.contains(friendbean)
								|| !friendbean.equals("")) {
							isInFriend = true;

						}
					}
					if (!isInFriend) {
						System.out.println("friend");
						listTitle.remove(i);
						i--;
					}

				}
			}

			return listTitle;
		}

		else if (!listContent.isEmpty()) {
			System.out.print("listContent");
			for (int i = 0; i < listContent.size(); i++) {
				if (!note.title.equals("")
						&& !(listContent.get(i).get("title").toString()
								.indexOf(note.title) != -1)) {
					listContent.remove(i);
					i --;
				} else if (!note.upOrDown.equals("無")) {
					if (note.upOrDown.equals("以後")) {

						compareDateTime = changeToCalendar(listContent.get(i)
								.get("time").toString());
						if (compareDateTime.compareTo(dateTime) < 0) {

							listContent.remove(i);
							i --;

						}
					} else if (note.upOrDown.equals("以前")) {

						compareDateTime = changeToCalendar(listContent.get(i)
								.get("time").toString());
						if (compareDateTime.compareTo(dateTime) > 0) {

							listContent.remove(i);
							i --;

						}

					}
				} else {
					for (String friendbean : friend) {
						if (listContent.get(i).get("userId").toString()
								.contains(friendbean)||listContent.get(i).get("friends").toString()
								.contains(friendbean)
								|| friendbean.equals("")) {
							isInFriend = true;

						}

					}
					if (!isInFriend) {
						listContent.remove(i);
						i --;
					}

				}

			}
			return listContent;

		} else if (!listFriends.isEmpty()) {
			System.out.print("listFriends");
			for (int i = 0; i < listFriends.size(); i++) {
				if (!note.title.equals("")
						&& !(listFriends.get(i).get("title").toString()
								.indexOf(note.title) != -1)) {
					listFriends.remove(i);
					i --;
				} else if (!note.content.equals("")
						&& !(listFriends.get(i).get("content").toString()
								.indexOf(note.content) != -1)) {
					listFriends.remove(i);
					i --;
				} else if (!note.upOrDown.equals("無")) {
					if (note.upOrDown.equals("以後")) {

						compareDateTime = changeToCalendar(listFriends.get(i)
								.get("time").toString());
						if (compareDateTime.compareTo(dateTime) < 0) {

							listFriends.remove(i);
							i --;

						}
					} else if (note.upOrDown.equals("以前")) {

						compareDateTime = changeToCalendar(listContent.get(i)
								.get("time").toString());
						if (compareDateTime.compareTo(dateTime) > 0) {

							listFriends.remove(i);
							i --;

						}

					}
				}

			}
			return listFriends;

		} else if (!listTime.isEmpty()) {
			System.out.print("listTime");
			for (int i = 0; i < listTime.size(); i++) {
				if (!note.title.equals("")
						&& !(listTime.get(i).get("title").toString()
								.indexOf(note.title) != -1)) {
					listTime.remove(i);
					i --;
				} else if (!note.content.equals("")
						&& !(listTime.get(i).get("content").toString()
								.indexOf(note.content) != -1)) {
					listTime.remove(i);
					i --;
				} else {
					for (String friendbean : friend) {
						if (listTime.get(i).get("userId").toString()
								.contains(friendbean)||listTime.get(i).get("friends").toString()
								.contains(friendbean)
								|| friendbean.equals("")) {
							isInFriend = true;

						}
					}
					if (!isInFriend) {
						listTime.remove(i);
						i --;
					}

				}
			}

			return listTime;
		}

		return listReturn;

	}
	private Calendar changeToCalendar(String time) {
		String[] setTime = time.split("-");
		// System.out.print(time);
		Calendar dateTime = Calendar.getInstance();

		dateTime.setTimeInMillis(System.currentTimeMillis());
		dateTime.set(Calendar.YEAR, Integer.parseInt(setTime[0]));
		dateTime.set(Calendar.MONTH, Integer.parseInt(setTime[1]));
		dateTime.set(Calendar.DAY_OF_MONTH, Integer.parseInt(setTime[2]));
		/*
		 * dateTime.set(Calendar.HOUR_OF_DAY,Integer.parseInt(setTime[3]));
		 * dateTime.set(Calendar.MINUTE,Integer.parseInt(setTime[4]));
		 * dateTime.set(Calendar.SECOND,0);
		 * dateTime.set(Calendar.MILLISECOND,0);
		 */
		return dateTime;
		// System.out.print(dateTime.toString());
	}

}
