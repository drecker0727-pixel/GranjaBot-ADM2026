package br.com.granjasustentavel.adm;
import android.app.*;
import android.content.*;
import android.os.*;
public class AdmKeepAliveService extends Service {
 private static final String CHANNEL="granjabot_status";
 @Override public void onCreate(){
  super.onCreate();
  NotificationManager manager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
  if(Build.VERSION.SDK_INT>=26)manager.createNotificationChannel(new NotificationChannel(CHANNEL,"GranjaBot ADM ativo",NotificationManager.IMPORTANCE_LOW));
 }
 @Override public int onStartCommand(Intent intent,int flags,int startId){
  if(!getSharedPreferences("adm",MODE_PRIVATE).getBoolean("enabled",false)){stopSelf();return START_NOT_STICKY;}
  Intent open=new Intent(this,MainActivity.class);
  PendingIntent pending=PendingIntent.getActivity(this,0,open,PendingIntent.FLAG_UPDATE_CURRENT|(Build.VERSION.SDK_INT>=23?PendingIntent.FLAG_IMMUTABLE:0));
  Notification.Builder builder=Build.VERSION.SDK_INT>=26?new Notification.Builder(this,CHANNEL):new Notification.Builder(this);
  builder.setSmallIcon(R.drawable.granjabot_rooster).setContentTitle("🐓 GranjaBot ADM ativo").setContentText("Monitorando notificações do WhatsApp Business").setContentIntent(pending).setOngoing(true).setShowWhen(false);
  startForeground(2026,builder.build());
  return START_STICKY;
 }
 @Override public android.os.IBinder onBind(Intent intent){return null;}
}
