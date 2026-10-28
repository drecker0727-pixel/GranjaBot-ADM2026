package br.com.granjasustentavel.adm;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.*;
import java.net.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
public final class AiClient {
 private AiClient(){}
 public static String ask(Context ctx,String sender,String message){
  SharedPreferences p=ctx.getSharedPreferences("adm",Context.MODE_PRIVATE);
  String endpoint=p.getString("ai_endpoint","").trim();
  String token=p.getString("ai_secret","").trim();
  if(!endpoint.startsWith("https://")||token.isEmpty()){p.edit().putString("ai_last_error","URL ou segredo da IA não configurado").apply();return null;}
  HttpURLConnection conn=null;
  try{
   JSONArray history=new JSONArray(p.getString("chat_"+Integer.toHexString(sender.hashCode()),"[]"));
   JSONObject payload=new JSONObject();payload.put("message",message);payload.put("history",history);
   conn=(HttpURLConnection)new URL(endpoint).openConnection();
   conn.setRequestMethod("POST");conn.setConnectTimeout(7000);conn.setReadTimeout(16000);
   conn.setDoOutput(true);conn.setRequestProperty("Content-Type","application/json");
   conn.setRequestProperty("x-granjabot-secret",token);
   try(OutputStream out=conn.getOutputStream()){out.write(payload.toString().getBytes(StandardCharsets.UTF_8));}
   if(conn.getResponseCode()!=200){p.edit().putString("ai_last_error","Supabase retornou HTTP "+conn.getResponseCode()).apply();return null;}
   ByteArrayOutputStream bytes=new ByteArrayOutputStream();
   try(InputStream in=conn.getInputStream()){byte[] buffer=new byte[2048];int n;while((n=in.read(buffer))!=-1&&bytes.size()<8192)bytes.write(buffer,0,n);}
   String answer=new JSONObject(bytes.toString("UTF-8")).optString("answer","").trim();
   if(answer.isEmpty()){p.edit().putString("ai_last_error","Supabase não enviou resposta").apply();return null;}
   JSONObject user=new JSONObject();user.put("role","user");user.put("content",message);
   JSONObject assistant=new JSONObject();assistant.put("role","assistant");assistant.put("content",answer);
   history.put(user);history.put(assistant);
   JSONArray compact=new JSONArray();for(int i=Math.max(0,history.length()-6);i<history.length();i++)compact.put(history.get(i));
   p.edit().putString("chat_"+Integer.toHexString(sender.hashCode()),compact.toString()).remove("ai_last_error").apply();
   return answer;
  }catch(Exception e){p.edit().putString("ai_last_error","Falha de conexão: "+e.getClass().getSimpleName()).apply();return null;}finally{if(conn!=null)conn.disconnect();}
 }
 public static void clear(Context ctx,String sender){
  ctx.getSharedPreferences("adm",Context.MODE_PRIVATE).edit().remove("chat_"+Integer.toHexString(sender.hashCode())).apply();
 }
}