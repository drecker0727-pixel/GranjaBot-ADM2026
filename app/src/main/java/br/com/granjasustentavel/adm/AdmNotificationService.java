package br.com.granjasustentavel.adm;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import java.util.*;
public class AdmNotificationService extends NotificationListenerService {
 private static final Map<String,Long> recent=new LinkedHashMap<>();
 private static final Map<String,Long> spamHistory=new LinkedHashMap<>();
 private static final Map<String,java.util.ArrayDeque<Long>> frequency=new LinkedHashMap<>();
 @Override public void onNotificationPosted(StatusBarNotification sbn){
  if(!"com.whatsapp.w4b".equals(sbn.getPackageName()))return;
  SharedPreferences p=getSharedPreferences("adm",MODE_PRIVATE);
  if(!p.getBoolean("enabled",false))return;
  if(p.getBoolean("schedule_enabled",false)){
   java.util.Calendar clock=java.util.Calendar.getInstance();
   int minutes=clock.get(java.util.Calendar.HOUR_OF_DAY)*60+clock.get(java.util.Calendar.MINUTE);
   int from=p.getInt("schedule_from",480),to=p.getInt("schedule_to",1320);
   boolean allowed=from<=to?minutes>=from&&minutes<to:minutes>=from||minutes<to;
   if(!allowed)return;
  }
  String group=p.getString("group","🐔🌱 Granja Sustentável | Comunidade Oficial 🐣").trim();
  if(group.isEmpty())return;
  Notification n=sbn.getNotification();
  if(n==null||n.extras==null||(n.flags&Notification.FLAG_GROUP_SUMMARY)!=0)return;
  String conversation=String.valueOf(n.extras.getCharSequence(Notification.EXTRA_CONVERSATION_TITLE,""));
  String title=String.valueOf(n.extras.getCharSequence(Notification.EXTRA_TITLE,""));
  if(!conversation.isEmpty()? !group.equals(conversation):!group.equals(title))return;
  if(n.category!=null&&!Notification.CATEGORY_MESSAGE.equals(n.category))return;
  CharSequence content=n.extras.getCharSequence(Notification.EXTRA_TEXT);
  if(content==null)return;
  String msg=content.toString().trim();
  if(msg.isEmpty()||msg.startsWith("Você: ")||msg.startsWith("You: "))return;
  String answer=null;
  boolean link=p.getBoolean("detect_links",false)&&msg.matches("(?is).*?(https?://|www\\.|chat\\.whatsapp\\.com/|wa\\.me/).*");
  String sender=String.valueOf(n.extras.getCharSequence(Notification.EXTRA_TITLE,""));
  if(sender.equals(group))sender="Participante não identificado";
  boolean spam=false;
  boolean flood=false;
  if(p.getBoolean("detect_spam",false)){
   long current=System.currentTimeMillis();
   synchronized(frequency){
    java.util.ArrayDeque<Long> times=frequency.get(sender);
    if(times==null){times=new java.util.ArrayDeque<>();frequency.put(sender,times);}
    while(!times.isEmpty()&&current-times.peekFirst()>30000L)times.removeFirst();
    times.addLast(current);flood=times.size()>=5;
    if(frequency.size()>200)frequency.clear();
   }
  }
  if(p.getBoolean("detect_spam",false)){
   String spamKey=sender+"|"+msg.toLowerCase(Locale.ROOT);
   long now=System.currentTimeMillis();
   synchronized(spamHistory){
    spamHistory.entrySet().removeIf(e->now-e.getValue()>60000L);
    spam=spamHistory.containsKey(spamKey);
    spamHistory.put(spamKey,now);
   }
  }
  boolean emojiFlood=p.getBoolean("detect_spam",false)&&msg.codePointCount(0,msg.length())>100&&msg.replaceAll("[\\p{L}\\p{N}\\s]","").length()>60;
  String violation=link?"Link":spam?"Mensagem repetida":flood?"Mensagens em excesso":emojiFlood?"Excesso de símbolos":null;
  if(violation!=null){
   ModerationLog.record(this,sender,violation);
   answer="🛡️ ADM: Atenção às regras do grupo. Evite links e mensagens repetidas.";
  }
  try{
   if(answer==null)answer=RuleStore.match(this,msg);
   if(answer==null){int i=msg.indexOf(": ");if(i>0)answer=RuleStore.match(this,msg.substring(i+2));}
  }catch(Exception e){return;}
  if(answer==null)return;
  long now=System.currentTimeMillis();
  String key=sbn.getKey()+"|"+msg+"|"+answer;
  synchronized(recent){recent.entrySet().removeIf(e->now-e.getValue()>120000L);if(recent.containsKey(key))return;}
  if(now-p.getLong("last_reply_time",0)<2500L||n.actions==null)return;
  for(Notification.Action a:n.actions){
   RemoteInput[] inputs=a.getRemoteInputs();
   if(inputs==null||inputs.length==0||a.actionIntent==null)continue;
   String label=a.title==null?"":a.title.toString().toLowerCase(Locale.ROOT);
   if(!label.contains("responder")&&!label.contains("reply"))continue;
   try{
    Bundle values=new Bundle();
    for(RemoteInput input:inputs)values.putCharSequence(input.getResultKey(),answer);
    Intent intent=new Intent();RemoteInput.addResultsToIntent(inputs,intent,values);
    a.actionIntent.send(this,0,intent);
    synchronized(recent){recent.put(key,now);}
    p.edit().putLong("last_reply_time",now).putInt("replies",p.getInt("replies",0)+1).apply();
    return;
   }catch(PendingIntent.CanceledException|RuntimeException e){return;}
  }
 }
}