package anynote.client.android.Alarm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.Locale;

import com.google.android.maps.GeoPoint;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import anynote.client.android.AnyNoteActivity;
import anynote.client.android.GeoModifyActivity;
import anynote.client.android.GeoRemindPoint;
import anynote.client.android.Sync;
import anynote.client.android.ToDoDB;
import anynote.client.classes.GeoNote;

public class GeoDetect extends Service {
	// private GeoPoint destGeoPoint=new GeoPoint((int) (25.047924 * 1E6),(int)
	// (121.517081 * 1E6));// ���եΥؼЦ�m
	private GeoPoint currentGeoPoint;// �{�b��m
	private Location mLocation01 = null;
	private String strLocationPrivider = "";
	private LocationManager mLocationManager01;
	int _id;
	// private GeoPoint destGeoPoint;// �ؼЦ�m
	// private double distance;// �����~�t�d��
	// private String remindTitle;// �������D
	// private String remindContent;// �������e
	private LinkedList<GeoNote> geoNote = new LinkedList<GeoNote>();// �O���Ҧ�geo����

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		Bundle bundle = intent.getExtras();

		// 1 �s�W 2 �ק� 3 �R��
		if (bundle.getInt("status") == 1) {
			Log.v("Before new geoNote", Integer.toString(geoNote.size()));
			GeoPoint destGeoPoint = new GeoPoint(
					(int) (bundle.getDouble("latitude") * 1E6),
					(int) (bundle.getDouble("longitude") * 1E6));
			String remindTitle = bundle.getString("title");
			String remindContent = bundle.getString("content");
			double distance = bundle.getDouble("distance");
			String userId = bundle.getString("userId");
			// int noteId=bundle.getInt("noteId");
			_id = bundle.getInt("ID");

			Calendar c1 = Calendar.getInstance();
			Calendar c2 = Calendar.getInstance();
			c1.set(Calendar.YEAR, bundle.getInt("year"));
			c1.set(Calendar.MONTH, bundle.getInt("month"));
			c1.set(Calendar.DAY_OF_MONTH, bundle.getInt("day"));
			c1.set(Calendar.HOUR_OF_DAY, bundle.getInt("hour"));
			c1.set(Calendar.MINUTE, bundle.getInt("minute"));
			c1.set(Calendar.SECOND, 0);
			c1.set(Calendar.MILLISECOND, 0);

			c2.set(Calendar.YEAR, bundle.getInt("year2"));
			c2.set(Calendar.MONTH, bundle.getInt("month2"));
			c2.set(Calendar.DAY_OF_MONTH, bundle.getInt("day2"));
			c2.set(Calendar.HOUR_OF_DAY, bundle.getInt("hour2"));
			c2.set(Calendar.MINUTE, bundle.getInt("minute2"));
			c2.set(Calendar.SECOND, 0);
			c2.set(Calendar.MILLISECOND, 0);

			/* �إ�geonote��Jarraylist */
			geoNote.add(new GeoNote(_id, userId, remindTitle, remindContent,
					destGeoPoint, distance, c1, c2, bundle
							.getBoolean("checkboxIn"), bundle
							.getBoolean("checkboxOut")));

			Log.v("After new geoNote", Integer.toString(geoNote.size()));

			/* �إ�LocationManager������o�t��LOCATION�A�� */
			mLocationManager01 = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			/* �Ĥ@������VLocation Provider���oLocation */
			mLocation01 = getLocationPrivider(mLocationManager01);

			if (mLocation01 != null) {
				processLocationUpdated(mLocation01);// �Ĥ@�������s
			} else {
				Toast.makeText(this, "�L�k���o��e��m", Toast.LENGTH_LONG);
			}
			/* �إ�LocationManager����A��ťLocation�ܧ�ɨƥ�A��sMapView */// ms m
			mLocationManager01.requestLocationUpdates(strLocationPrivider,
					10000, 50, mLocationListener01);
		} else if (bundle.getInt("status") == 2) {
			String title = bundle.getString("title");
			String content = bundle.getString("content");
			double distance = bundle.getDouble("distance");
			int year = bundle.getInt("year");
			int month = bundle.getInt("month");
			int day = bundle.getInt("day");
			int hour = bundle.getInt("hour");
			int minute = bundle.getInt("minute");
			int year2 = bundle.getInt("year2");
			int month2 = bundle.getInt("month2");
			int day2 = bundle.getInt("day2");
			int hour2 = bundle.getInt("hour2");
			int minute2 = bundle.getInt("minute2");
			boolean getIn = bundle.getBoolean("checkboxIn");
			boolean getOut = bundle.getBoolean("checkboxOut");
			int _id = bundle.getInt("ID");
			modifyGeo(_id, title, content, distance, year, month, day, hour,
					minute, year2, month2, day2, hour2, minute2, getIn, getOut);
		} else if (bundle.getInt("status") == 3) {
			int _id = bundle.getInt("ID");
			deleteGeo(_id);
		}
	}

	// �R���a�z����
	public void deleteGeo(int noteId) {
		Log.v("GeoBeforeDelete", Integer.toString(geoNote.size()));
		for (int i = 0; i < geoNote.size(); i++) {
			if (geoNote.get(i).noteId == noteId) {
				geoNote.remove(i);
				break;
			}
		}
		Log.v("GeoAfterDelete", Integer.toString(geoNote.size()));
	}

	// �ק�a�z����
	public void modifyGeo(int noteId, String title, String content,
			double distance, int year, int month, int day, int hour,
			int minute, int year2, int month2, int day2, int hour2,
			int minute2, boolean getIn, boolean getOut) {
		Log.v("GeoBeforeModify", Integer.toString(geoNote.size()));
		for (int i = 0; i < geoNote.size(); i++) {
			if (geoNote.get(i).noteId == noteId) {
				geoNote.get(i).title = title;
				geoNote.get(i).content = content;
				geoNote.get(i).range = distance;
				geoNote.get(i).setTime(year, month, day, hour, minute, 0);
				geoNote.get(i).setEndTime(year2, month2, day2, hour2, minute2,
						0);
				geoNote.get(i).timeStart.set(year, month, day, hour, minute, 0);
				geoNote.get(i).timeFinish.set(year2, month2, day2, hour2,
						minute2, 0);
				geoNote.get(i).getIn = getIn;
				geoNote.get(i).getOut = getOut;
			}
		}
		Log.v("GeoAfterModify", Integer.toString(geoNote.size()));
	}

	// �p����I�g�n�׶Z��
	private double EARTH_RADIUS = 6378137; // �a�y�b�|

	double rad(double d) {
		return d * 3.1415926535898 / 180.0;
	}

	public boolean getDistance(GeoPoint cgp, GeoPoint dgp) {
		double lat1, lng1, lat2, lng2;
		lat1 = (int) cgp.getLatitudeE6() / 1E6;
		lng1 = (int) cgp.getLongitudeE6() / 1E6;
		lat2 = (int) dgp.getLatitudeE6() / 1E6;
		lng2 = (int) dgp.getLongitudeE6() / 1E6;
		double radLat1 = rad(lat1);
		double radLat2 = rad(lat2);
		double a = radLat1 - radLat2;
		double b = rad(lng1) - rad(lng2);
		double s = 2 * Math.asin(Math.sqrt(Math.pow(Math.sin(a / 2), 2)
				+ Math.cos(radLat1) * Math.cos(radLat2)
				* Math.pow(Math.sin(b / 2), 2)));
		s = s * EARTH_RADIUS;
		s = Math.round(s * 10000) / 10000; // ��줽��

		// Log.v("testc","�n��:"+Double.toString(cgp.getLongitudeE6()/1E6)+
		// "\n�g��:"+Double.toString(cgp.getLatitudeE6()/1E6));
		// Log.v("testd","�n��:"+Double.toString(dgp.getLongitudeE6()/1E6)+
		// "\n�g��:"+Double.toString(dgp.getLatitudeE6()/1E6));
		// Log.v("test1", Double.toString(s));
		if (s <= geoNote.getLast().getDistance())
			return true;
		else
			return false;
	}

	private final LocationListener mLocationListener01 = new LocationListener() {
		@Override
		public void onLocationChanged(Location location) {
			// TODO Auto-generated method stub

			/* ���������m�ܧ�ɡA�Nlocation�ǤJ���o�a�z�y�� */
			processLocationUpdated(location);
		}

		@Override
		public void onProviderDisabled(String provider) {
			// TODO Auto-generated method stub
			/* ��Provider�w���}�A�Ƚd��� */
		}

		@Override
		public void onProviderEnabled(String provider) {
			// TODO Auto-generated method stub
		}

		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
			// TODO Auto-generated method stub

		}
	};

	/* ���������m�ܧ�ɡA�Nlocation�ǤJ��s��UGeoPoint��MapView */// locationChanges
	private void processLocationUpdated(Location location) {
		/* �ǤJLocation����A���oGeoPoint�a�z�y�� */
		currentGeoPoint = getGeoByLocation(location);

		Log.v("GEODETECTsize", Integer.toString(geoNote.size()));

		for (GeoNote item : geoNote) {
			Log.v("startTime", item.getStartTime().getTime().toString());
			Log.v("finishTime", item.getEndTime().getTime().toString());
			// Log.v("toieem2", Calendar.getInstance().getTime().toString());
			int result1 = item.getStartTime().compareTo(Calendar.getInstance());
			int result2 = item.getEndTime().compareTo(Calendar.getInstance());

			// Log.v("Time",Integer.toString(result1));
			// Log.v("time2", Integer.toString(result2));

			// �p����I�Z���O�_�W�L�w�]�� �M �O�_�b�����ɶ��d��
			Log.v("geo", "detectlocation");
			Log.v("geoResult1", Integer.toString(result1));
			Log.v("geoResult2", Integer.toString(result2));
			Log.v("destLong",
					Integer.toString(item.getDestGeoPoint().getLongitudeE6()));
			Log.v("destLati",
					Integer.toString(item.getDestGeoPoint().getLatitudeE6()));
			Log.v("getdistance",
					Boolean.toString(getDistance(currentGeoPoint,
							item.getDestGeoPoint())));
			if (result1 <= 0 && result2 >= 0
					&& getDistance(currentGeoPoint, item.getDestGeoPoint())) {
				Log.v("geo", "arrive");
				if (!item.approachStatus && !item.oneTime)// �P�_�O�_���񴣿��M�O�_�]�L�@��
				{
					Log.v("geo", "runAlarmClose");
					Log.v("geoGetIN", Boolean.toString(item.getIn));
					item.oneTime = true;
					item.approachStatus = true;
					if (item.getIn) {// �P�_�O�_�]�w���񴣿�
						Toast.makeText(this, "���񴣿��I", Toast.LENGTH_LONG).show();
						callGeoWindow(item);
					}
				}
			} else if (result1 <= 0 && result2 >= 0
					&& !getDistance(currentGeoPoint, item.getDestGeoPoint())) {
				Log.v("geo", "leave");
				if (item.approachStatus && item.oneTime)// �P�_�O�_���񴣿��M�O�_�]�L�@��
				{
					item.oneTime = false;
					item.approachStatus = false;
					if (item.getOut) {// �P�_�O�_�]�w���񴣿�
						Toast.makeText(this, "���}�����I", Toast.LENGTH_LONG).show();
						callGeoWindow(item);
					}
				}
			}
		}
	}

	// �o�X�a�z����
	public void callGeoWindow(GeoNote item) {
		Intent intent = new Intent();
		Bundle bundle = new Bundle();
		ToDoDB db = new ToDoDB(this);
		String name = "0";
		try {
			name = db.searchFriendName(item.userId);
			if (name.equals("0")) {
				name = "�ۤv\n";
			}
				
		} catch (Exception e) {

		}

		bundle.putString("title", item.getTitle());
		bundle.putString("content", item.getContent());
		bundle.putString("name", name);
		bundle.putString("img", db.getGeoImgById(_id));
		bundle.putString("sound", db.getGeoSoundById(_id));
		intent.putExtras(bundle);
		db.close();
		intent.setClass(GeoDetect.this, AlarmAlert.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
	}

	private GeoPoint getGeoByLocation(Location location) {
		GeoPoint gp = null;
		try {
			/* ��Location�s�b */
			if (location != null) {
				double geoLatitude = location.getLatitude() * 1E6;
				double geoLongitude = location.getLongitude() * 1E6;
				gp = new GeoPoint((int) geoLatitude, (int) geoLongitude);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return gp;
	}

	public Location getLocationPrivider(LocationManager lm) {
		Location retLocation = null;
		try {
			Criteria mCriteria01 = new Criteria();
			mCriteria01.setAccuracy(Criteria.ACCURACY_FINE);
			mCriteria01.setAltitudeRequired(false);
			mCriteria01.setBearingRequired(false);
			mCriteria01.setCostAllowed(true);
			mCriteria01.setPowerRequirement(Criteria.POWER_LOW);
			strLocationPrivider = lm.getBestProvider(mCriteria01, true);
			retLocation = lm.getLastKnownLocation(strLocationPrivider);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return retLocation;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}
}
