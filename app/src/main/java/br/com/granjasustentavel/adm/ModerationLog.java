package br.com.granjasustentavel.adm;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;
public final class ModerationLog {
 private ModerationLog(){}
 public static synchronized void record(Context c,String sender,String type){
  SharedPreferences p=c.getSharedPreferences("adm",Context.MODE_PRIVATE);
  try{
   JSONArray old=new JSONArray(p.getString("moderation_history","[]"));
   JSONArray entries=new JSONArray();
   JSONObject item=new JSONObject();
   item.put("time",System.currentTimeMillis());item.put("sender",sender);item.put("type",type);
   entries.put(item);
   for(int i=0;i<old.length()&&i<99;i++)entries.put(old.getJSONObject(i));
   p.edit().putString("moderation_history",entries.toString()).putInt("alerts",p.getInt("alerts",0)+1).apply();
  }catch(Exception ignored){}
 }
 public static String summary(Context c){
  SharedPreferences p=c.getSharedPreferences("adm",Context.MODE_PRIVATE);
  try{
   JSONArray a=new JSONArray(p.getString("moderation_history","[]"));
   StringBuilder out=new StringBuilder("Ocorrências recentes: "+a.length()+"\n");
   SimpleDateFormat df=new SimpleDateFormat("dd/MM HH:mm",new Locale("pt","BR"));
   for(int i=0;i<Math.min(a.length(),30);i++){
    JSONObject item=a.getJSONObject(i);
    out.append(df.format(new Date(item.getLong("time")))).append(" • ")
      .append(item.optString("sender","Desconhecido")).append(" • ")
      .append(item.optString("type","Alerta")).append("\n");
   }
   return out.toString();
  }catch(Exception e){return "Sem histórico disponível";}
 }
}