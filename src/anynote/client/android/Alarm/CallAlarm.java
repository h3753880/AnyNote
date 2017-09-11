package anynote.client.android.Alarm;

/* import¬ÛÃöclass */
import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.os.Bundle;

/* ©I¥s¾xÄÁAlertªºReceiver */
public class CallAlarm extends BroadcastReceiver
{
  @Override
  public void onReceive(Context context, Intent intent)
  {    
    /* create Intent¡A©I¥sAlarmAlert.class */
	
	Bundle test=intent.getExtras();
	System.out.println("callAlarm");
	Intent i = new Intent(context, AlarmAlert.class);   
    test.putString("STR_CALLER", "");
    i.putExtras(test);
    i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

    context.startActivity(i);      
  }
}