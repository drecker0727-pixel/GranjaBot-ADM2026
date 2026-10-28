package br.com.granjasustentavel.adm;
import android.content.Context;
import java.io.*;
import java.util.*;
public final class RuleStore {
 private RuleStore(){}
 private static volatile List<Rule> cache;
 private static final String FILE="rules.csv";
 private static class Rule {String trigger,reply;Rule(String t,String r){trigger=t;reply=r;}}
 private static String norm(String s){
  if(s==null)return "";
  String t=java.text.Normalizer.normalize(s.toLowerCase(new Locale("pt","BR")),java.text.Normalizer.Form.NFD).replaceAll("\\p{M}+","");
  return t.trim().replaceAll("[!?.,;:]+$","").replaceAll("\\s+"," ");
 }
 private static String plain(String s){return norm(s).replaceAll("[^a-z0-9 ]","").replaceAll("\\s+"," ").trim();}
 public static int count(Context c)throws IOException{return load(c).size();}
 public static String match(Context c,String message)throws IOException{
  String m=norm(message);if(m.isEmpty())return null;
  List<Rule> rules=load(c);
  for(Rule r:rules)if(r.trigger.equals(m))return r.reply;
  String basic=plain(m);
  for(Rule r:rules)if(plain(r.trigger).equals(basic))return r.reply;
  // Comandos com prefixo são intencionais; não tentar aproximação em texto livre.
  if(m.startsWith("!")||m.length()<9)return MassReplies.answer(message);
  for(Rule r:rules){
   if(r.trigger.startsWith("!")||r.trigger.length()<9)continue;
   String candidate=plain(r.trigger);
   if(candidate.length()>=9 && (basic.equals(candidate+" por favor")||basic.equals("por favor "+candidate)))return r.reply;
  }
  return MassReplies.answer(message);
 }
 private static List<Rule> load(Context c)throws IOException{
  if(cache!=null)return cache;
  synchronized(RuleStore.class){
   if(cache!=null)return cache;
   File f=new File(c.getFilesDir(),FILE);
   if(!f.exists()){try(InputStream in=c.getAssets().open("regras.csv")){cache=Collections.unmodifiableList(withExtra(c,parse(in)));return cache;}catch(java.io.FileNotFoundException e){return Collections.unmodifiableList(withExtra(c,new ArrayList<Rule>()));}}
   try(InputStream in=new FileInputStream(f)){cache=Collections.unmodifiableList(withExtra(c,parse(in)));return cache;}
  }
 }
 public static synchronized int importCsv(Context c,InputStream in)throws IOException{
  byte[] bytes=read(in);List<Rule> rules=parse(new ByteArrayInputStream(bytes));
  if(rules.isEmpty())throw new IOException("CSV sem regras válidas");
  File temp=new File(c.getFilesDir(),"rules.tmp"),dest=new File(c.getFilesDir(),FILE),bak=new File(c.getFilesDir(),"rules.bak");
  try(FileOutputStream out=new FileOutputStream(temp)){out.write(bytes);out.getFD().sync();}
  if(bak.exists()&&!bak.delete())throw new IOException("Falha ao remover backup antigo");
  if(dest.exists()&&!dest.renameTo(bak))throw new IOException("Falha ao preservar regras");
  if(!temp.renameTo(dest)){if(bak.exists())bak.renameTo(dest);throw new IOException("Falha ao salvar CSV");}
  cache=Collections.unmodifiableList(withExtra(c,rules));if(bak.exists())bak.delete();return rules.size();
 }
 private static List<Rule> withExtra(Context c,List<Rule> base)throws IOException{
  List<Rule> all=new ArrayList<>(base);
  Set<String> seen=new HashSet<>();
  for(Rule r:all)seen.add(r.trigger);
  try(InputStream in=c.getAssets().open("saudacoes_extra.csv")){
   for(Rule r:parse(in))if(seen.add(r.trigger))all.add(r);
  }catch(java.io.FileNotFoundException ignored){}
  for(int i=1;i<=5;i++){
   try(InputStream in=c.getAssets().open("conversas_extra_"+i+".csv")){
    for(Rule r:parse(in))if(seen.add(r.trigger))all.add(r);
   }catch(java.io.FileNotFoundException ignored){}
  }
  MassReplies.populate((trigger,reply)->{
   String key=norm(trigger);
   if(seen.add(key))all.add(new Rule(key,reply));
  });
  return all;
 }
 private static byte[] read(InputStream in)throws IOException{
  ByteArrayOutputStream out=new ByteArrayOutputStream();byte[] b=new byte[8192];int n;
  while((n=in.read(b))!=-1){if(out.size()+n>8*1024*1024)throw new IOException("CSV maior que 8 MB");out.write(b,0,n);}
  return out.toByteArray();
 }
 private static List<Rule> parse(InputStream in)throws IOException{
  String data=new String(read(in),java.nio.charset.StandardCharsets.UTF_8);
  if(data.startsWith("\uFEFF"))data=data.substring(1);
  List<List<String>> rows=rows(data);
  if(rows.isEmpty())throw new IOException("CSV vazio");
  int h=rows.get(0).size()==1&&rows.get(0).get(0).trim().startsWith("sep=")?1:0;
  if(rows.size()<=h)throw new IOException("Cabeçalho ausente");
  List<String> header=rows.get(h);
  for(int i=0;i<header.size();i++)header.set(i,header.get(i).trim().toLowerCase(Locale.ROOT));
  int ti=header.indexOf("received_message"),ri=header.indexOf("reply_message"),di=header.indexOf("disabled"),pi=header.indexOf("pattern_matching");
  if(ti<0||ri<0)throw new IOException("Cabeçalho deve conter received_message e reply_message");
  List<Rule> result=new ArrayList<>();Set<String> seen=new HashSet<>();
  for(int i=h+1;i<rows.size();i++){
   List<String> r=rows.get(i);if(r.size()==1&&r.get(0).trim().isEmpty())continue;
   if(r.size()!=header.size())throw new IOException("Colunas incorretas na linha "+(i+1));
   if(di>=0&&"1".equals(r.get(di).trim()))continue;
   if(pi>=0&&!Arrays.asList("none","exact","").contains(norm(r.get(pi))))continue;
   String trigger=norm(r.get(ti)),reply=r.get(ri).trim();
   if(!trigger.isEmpty()&&!reply.isEmpty()&&seen.add(trigger))result.add(new Rule(trigger,reply));
  }
  return result;
 }
 private static List<List<String>> rows(String s)throws IOException{
  List<List<String>> rows=new ArrayList<>();List<String> row=new ArrayList<>();StringBuilder cell=new StringBuilder();boolean quoted=false;
  for(int i=0;i<s.length();i++){char c=s.charAt(i);
   if(quoted){if(c=='"'){if(i+1<s.length()&&s.charAt(i+1)=='"'){cell.append('"');i++;}else quoted=false;}else cell.append(c);}
   else if(c=='"'&&cell.length()==0)quoted=true;
   else if(c==','){row.add(cell.toString());cell.setLength(0);}
   else if(c=='\n'){row.add(cell.toString());cell.setLength(0);rows.add(row);row=new ArrayList<>();}
   else if(c!='\r')cell.append(c);
  }
  if(quoted)throw new IOException("Aspas não fechadas");
  if(cell.length()>0||!row.isEmpty()){row.add(cell.toString());rows.add(row);}
  return rows;
 }
}