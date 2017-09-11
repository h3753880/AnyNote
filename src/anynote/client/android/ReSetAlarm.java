package anynote.client.android;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReSetAlarm extends BroadcastReceiver {
    /*要接收的intent源*/  
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";   
    static private int time=0;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
        if (intent.getAction().equals(ACTION)&&time==0)    
        {   
        	time++;
			intent.setClass(context, ReSetService.class);
			context.startService(intent);
			System.out.println("Reset alarm start!!!");
        }   
	}

}
