package anynote.client.android;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.json.JSONException;
import org.json.JSONObject;

import com.facebook.android.FacebookError;
import com.facebook.android.Util;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import anynote.client.classes.SearchNote;

public class MemoryActivity extends Activity {
	// note class
	private List<Map<String, Object>> timeList;
	private List<Map<String, Object>> geoList;
	private SearchNote note;
	private boolean isAll;
	private ToDoDB myToDoDB;

	private int vWidth;
	private int vHeight;
	
	private int noteH;
	private int noteW;

	public class noteX {
		public float x;
		public float y;

		noteX() {
			x = 0;
			y = 0;
		}

		public float getX() {
			return x;
		}

		public float getY() {
			return y;
		}

		public void set(float newX, float newY) {
			x += newX;
			y += newY;
		}

		public boolean check(float newX, float newY) {
			if (newX - x < noteW && newY - y < noteH && newX - x > 0
					&& newY - y > 0) {
				return true;
			}
			return false;
		}
	}
	private int size=50;
	private Button catchbutton;
	private ImageView img1;
	private Bitmap[] Bmap;
	private Bitmap Allmap;
	private View allPic;
	private SurfaceView SurfaceView01;
	private SurfaceHolder surfaceHolder;
	private ImageView display;
	private RelativeLayout layout;
	private FrameLayout allLayout;
	private TextView txt;
	private DisplayMetrics dm;
	private int n_note;
	private int allLoad = 0;
	private int[] position = new int[size];
	private noteX[] n = new noteX[size];
	//返回鍵
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
        	position=null;
        	n=null;
        	Bmap=null;
        	System.gc();
        	MemoryActivity.this.finish();
        }
		return false;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.memorytest);
		
		Toast.makeText(this, "按MENU鍵分享回憶", Toast.LENGTH_LONG).show();
		
		myToDoDB = new ToDoDB(this);
		Bmap = new Bitmap[size];
		allLayout = (FrameLayout) findViewById(R.id.app01);
		layout = (RelativeLayout) findViewById(R.id.note);
		layout.setDrawingCacheEnabled(true);
		layout.setDrawingCacheQuality(RelativeLayout.DRAWING_CACHE_QUALITY_HIGH);
		txt = (TextView) findViewById(R.id.textView2);
		SurfaceView01 = (SurfaceView) findViewById(R.id.SurfaceView01);
		surfaceHolder = SurfaceView01.getHolder();
		display = (ImageView) findViewById(R.id.imageView2);
		dm = new DisplayMetrics();
		Bundle text = this.getIntent().getExtras();
		isAll = text.getBoolean("isAll");
		n_note = 0;
		if (!isAll) {
			note = new SearchNote(text.getString("title"),
					text.getString("content"), text.getString("friends"),
					text.getString("time"), text.getString("upOrDown"));
			timeList = myToDoDB.timeNoteSearch(note);
			geoList = myToDoDB.geoNoteSearch(note);
		} else {
			timeList = myToDoDB.timeSelect();
			geoList = myToDoDB.geoSelect();
		}

		this.getWindowManager().getDefaultDisplay().getMetrics(dm);
		vWidth = dm.widthPixels;
		vHeight = dm.heightPixels;
		System.out.println(vWidth + ":" + vHeight);
		Allmap = Bitmap.createBitmap(vWidth, vHeight, Config.ARGB_8888);
		// 當所有View都載入後才截圖
		ViewTreeObserver vto = layout.getViewTreeObserver();
		vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
				allLoad = 1;

			}
		});

		// Holder方法
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			public void surfaceDestroyed(SurfaceHolder arg0) {
			}

			// 當畫布被創造的時候先畫一次
			public void surfaceCreated(SurfaceHolder arg0) {

				int count=0;
				for (Map<String, Object> map : timeList) {
					count++;
					int temp[]=randomPicture();
					newNote(map.get("title").toString() + "\n"
							+ map.get("content").toString() + "\n"
							+ toCalendarString(map.get("time").toString())
							+ "\n" + toNameString(map.get("userId").toString()),temp[0],temp[1]);
					if (!map.get("img").toString().equals("0")) {
						newImg(map.get("img").toString(),temp[0]+25,temp[1]+25);
						count++;
					}
					if(count>=(size/2)-1)
						break;
				}
				count=0;
				for (Map<String, Object> map : geoList) {
					count++;
					int temp[]=randomPicture();
					newNote(map.get("title").toString() + "\n"
							+ map.get("content") + "\n"
							+ toCalendarString(map.get("timeStart").toString())
							+ "\n" + toNameString(map.get("userId").toString()),temp[0],temp[1]);

					if (!map.get("img").toString().equals("0")) {
						newImg(map.get("img").toString(),temp[0]+25,temp[1]+25);
						count++;
					}
					if(count>=(size/2)-1)
						break;
				}
				if (timeList.isEmpty() && geoList.isEmpty())
					newNote("裡面沒有東西喔!",20,20);
				// newNote("大頭好帥!?");
				// newNote("終於快要完成了");
				// newNote("好想換手機喔!");
				noteH = Bmap[0].getHeight();
				noteW = Bmap[0].getWidth();
				draw();
			}

			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
					int arg3) {
			}
		});
		SurfaceView01.setOnTouchListener(new TouchListener());
	}

	protected static final int MENU_UPLOAD = Menu.FIRST;
	protected static final int MENU_QUIT = Menu.FIRST + 1;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		menu.add(0, MENU_UPLOAD, 0, "上傳Facebook");
		menu.add(0, MENU_QUIT, 0, "回上頁");
		return super.onCreateOptionsMenu(menu);
	}

	private ProgressDialog myDialog;

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub

		switch (item.getItemId()) {
		case MENU_UPLOAD:
			if (AnyNoteActivity.facebook.isSessionValid()) {
				myDialog = ProgressDialog.show(MemoryActivity.this, "",
						"loading...", true);
				new Thread() {
					public void run() {
						try {
							getBitmapForFB();
							sleep(7000);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							myDialog.dismiss();
						}
					}
				}.start();
			} else
				Toast.makeText(MemoryActivity.this, "請登入以使用上傳功能",
						Toast.LENGTH_SHORT).show();
			break;
		case MENU_QUIT:
			Bmap=null;
			position=null;
			n=null;
			System.gc();
			MemoryActivity.this.finish();
		}
		return super.onOptionsItemSelected(item);
	}

	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h) {
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		float scaleWidht = ((float) w / width);
		float scaleHeight = ((float) h / height);
		matrix.postScale(scaleWidht, scaleHeight);
		Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
				matrix, true);
		return newbmp;
	}

	int[] randomPicture()
	{
		//Log.v("memoryRANGE H", Integer.toString(vHeight));
		//Log.v("memoryRANGE W", Integer.toString(vWidth));
		Random rm=new Random();
		int height=rm.nextInt(vHeight)*4/5;
		int width=rm.nextInt(vWidth)*4/5;
		//Log.v("height", Integer.toString(height));
		//Log.v("width", Integer.toString(width));
		int[] a=new int[2];
		a[1]=height;
		a[0]=width;
		return a;
	}
	
	void newNote(String content,int width,int height) {// 新增note
		n[n_note] = new noteX();
		n[n_note].set(width,height);
		Bmap[n_note] = zoomBitmap(getDrawingBitmap(content),
				Allmap.getWidth() / 3, Allmap.getWidth() / 3);

		position[n_note] = n_note;
		n_note++;
	}

	void newImg(String img,int width,int height) {// 新增note
		n[n_note] = new noteX();
		n[n_note].set(width,  height);

		String strImgPath = "/sdcard/AnyNote/";
		String allPath = strImgPath + img;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inPurgeable = true;

		final Bitmap bm = BitmapFactory.decodeFile(allPath, options);
		Bmap[n_note] = zoomBitmap(bm, Allmap.getWidth() / 3,
				Allmap.getWidth() / 3);
		position[n_note] = n_note;
		n_note++;
	}

	// 更新畫面
	void draw() {
		Canvas canvas = null;
		Paint p = new Paint();
		try {
			// 鎖定
			canvas = surfaceHolder.lockCanvas(null);
			canvas.setBitmap(Allmap);
			synchronized (surfaceHolder) {
				// 依照目前的狀態做不同的繪圖
				clear(canvas);
				for (int a = 0; a < n_note; a++) {
					if (Bmap[position[a]] != null)
						canvas.drawBitmap(Bmap[position[a]],
								n[position[a]].getX(), n[position[a]].getY(), p);
				}
			}

		} finally {
			if (canvas != null) {
				// 解除鎖定
				display.setImageBitmap(Allmap);
				surfaceHolder.unlockCanvasAndPost(canvas);

			}
		}

	}

	void clear(Canvas canvas) {
		Paint p = new Paint();
		p.setARGB(230, 139, 69, 0);
		int vWidth = dm.widthPixels;
		int vHeight = dm.heightPixels;
		Rect rect = new Rect(0, 0, Allmap.getWidth(), Allmap.getHeight());
		canvas.drawRect(rect, p);
	}

	public Bitmap getDrawingBitmap(String content) {
		// while(allLoad==0){}
		System.out.println(content);
		txt.setText(content);
		Bitmap b = Bitmap.createBitmap(layout.getDrawingCache());
		return b;

	}

	public Bitmap getImgBitmap(String img) {
		// while(allLoad==0){}

		Bitmap b = Bitmap.createBitmap(layout.getDrawingCache());
		return b;

	}

	public void getBitmapForFB() {
		SurfaceView allView = (SurfaceView) findViewById(R.id.SurfaceView01);
		allView.setDrawingCacheEnabled(true);
		allView.setDrawingCacheQuality(RelativeLayout.DRAWING_CACHE_QUALITY_HIGH);
		Bitmap b = Bitmap.createBitmap(allView.getDrawingCache());
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		Allmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
		byte[] array = os.toByteArray();
		if (AnyNoteActivity.facebook.isSessionValid()) {
			Bundle params = new Bundle();
			params.putString("message", "一起來AnyNote吧!!!!!!");
			params.putByteArray("picture", array);
			String test = "";
			try {
				test = AnyNoteActivity.facebook.request("me/photos", params,
						"POST");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(test);
		}
	}

	/* 移動圖層順序 */
	void moveTop(int a) {
		int temp = position[a];
		for (int now = a; now < n_note - 1; now++) {
			position[now] = position[now + 1];
		}
		position[n_note - 1] = temp;
	}

	/* 圖片移動method */
	final class TouchListener implements OnTouchListener {
		private float startX;
		private float startY;
		private float nowX;
		private float nowY;
		int nowMove = -1;
		private static final int DRAG = 1;
		private int type = 0;

		TouchListener() {

		}

		public boolean onTouch(View v, MotionEvent event) {
			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_DOWN:// 按下瞬間
				startX = event.getX();
				startY = event.getY();
				for (int a = n_note - 1; a >= 0; a--) {
					if (n[position[a]].check(startX, startY)) {
						nowMove = position[a];
						moveTop(a);
						type = DRAG;
						break;
					}
				}

				break;
			case MotionEvent.ACTION_MOVE:// 移動中
				if (type == DRAG && nowMove != -1) {
					nowX = event.getX();
					nowY = event.getY();

					n[nowMove].set(nowX - startX, nowY - startY);
					draw();
					startX = nowX;
					startY = nowY;
				}
				break;

			case MotionEvent.ACTION_UP:// 放開手指
				nowMove = -1;
				type = 0;
				break;
			case MotionEvent.ACTION_POINTER_UP:
				break;
			}
			return true;

		}

	}

	private String toCalendarString(String time) {
		String[] setTime = time.split("-");
		String setTimeString = setTime[0] + "年"
				+ (Integer.parseInt(setTime[1]) + 1) + "月" + setTime[2] + "日"
				+ setTime[3] + ":" + setTime[4];

		return setTimeString;
		// System.out.print(dateTime.toString());
	}

	private String toNameString(String userId) {

		String setTimeString = "";
		if (myToDoDB.searchFriendName(userId).equals("0"))
			setTimeString += "自己";
		else
			setTimeString = myToDoDB.searchFriendName(userId);

		return setTimeString;
		// System.out.print(dateTime.toString());
	}

}

class SampleUploadListener extends BaseRequestListener {

	public void onComplete(final String response, final Object state) {
		try {
			// process the response here: (executed in background thread)
			Log.d("Facebook-Example", "Response: " + response.toString());
			JSONObject json = Util.parseJson(response);
			final String src = json.getString("src");
			System.out.println(src);

		} catch (JSONException e) {
			Log.w("Facebook-Example", "JSON Error in response");
		} catch (FacebookError e) {
			Log.w("Facebook-Example", "Facebook Error: " + e.getMessage());
		}
	}

}
