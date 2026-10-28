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
 @Override public void onNotificationPosted(StatusBarNotification sbn){
  if(!"com.whatsapp.w4b".equals(sbn.getPackageName()))return;
  SharedPreferences p=getSharedPreferences("adm",MODE_PRIVATE);
  if(!p.getBoolean("enabled",false))return;
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
  boolean spam=false;
  if(p.getBoolean("detect_spam",false)){
   String sender=String.valueOf(n.extras.getCharSequence(Notification.EXTRA_TITLE,""));
   String spamKey=sender+"|"+msg.toLowerCase(Locale.ROOT);
   long now=System.currentTimeMillis();
   synchronized(spamHistory){
    spamHistory.entrySet().removeIf(e->now-e.getValue()>60000L);
    spam=spamHistory.containsKey(spamKey);
    spamHistory.put(spamKey,now);
   }
  }
  if(link)answer="🛡️ ADM: Evite enviar links sem autorização da administração.";
  else if(spam)answer="🛡️ ADM: Evite repetir mensagens no grupo.";
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
    p.edit().putLong("last_reply_time",now).apply();
    return;
   }catch(PendingIntent.CanceledException|RuntimeException e){return;}
  }
 }
}