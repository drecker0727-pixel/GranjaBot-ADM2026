package br.com.granjasustentavel.adm;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.widget.*;
import android.content.SharedPreferences;
public class MainActivity extends Activity {
 private static final int PICK_CSV=42,EXPORT_CSV=43;
 private SharedPreferences prefs;
 private TextView status;
 @Override public void onCreate(Bundle b){
  super.onCreate(b);prefs=getSharedPreferences("adm",MODE_PRIVATE);
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(28,36,28,20);root.setBackgroundColor(Color.rgb(10,17,29));
  ScrollView scroll=new ScrollView(this);scroll.addView(root);setContentView(scroll);
  TextView title=new TextView(this);title.setText("🐔🤖 GranjaBot ADM");title.setTextSize(26);title.setTextColor(Color.WHITE);root.addView(title);
  TextView desc=new TextView(this);desc.setText("WhatsApp Business • Respostas por notificações • CSV separado");desc.setTextColor(Color.LTGRAY);root.addView(desc);
  EditText group=new EditText(this);group.setTextColor(Color.WHITE);group.setHintTextColor(Color.LTGRAY);group.setHint("Nome exato do grupo");group.setText(prefs.getString("group","🐔🌱 Granja Sustentável | Comunidade Oficial 🐣"));root.addView(group);styleInput(group);
  TextView botLabel=new TextView(this);botLabel.setText("📱 Número do Robô (WhatsApp Business)");botLabel.setTextColor(Color.WHITE);root.addView(botLabel);
  EditText botNumber=new EditText(this);botNumber.setTextColor(Color.WHITE);botNumber.setHintTextColor(Color.LTGRAY);botNumber.setHint("Ex.: +55 62 99999-9999");botNumber.setInputType(android.text.InputType.TYPE_CLASS_PHONE);botNumber.setText(prefs.getString("bot_number",""));root.addView(botNumber);styleInput(botNumber);
  Button saveNumber=new Button(this);saveNumber.setText("Salvar número do robô");root.addView(saveNumber);styleButton(saveNumber,0xFF2563EB);
  saveNumber.setOnClickListener(v->{prefs.edit().putString("bot_number",botNumber.getText().toString().trim()).apply();Toast.makeText(this,"Número salvo. Ative esse número no WhatsApp Business.",Toast.LENGTH_LONG).show();});
  TextView moderationTitle=new TextView(this);moderationTitle.setText("🛡️ Moderação do grupo");moderationTitle.setTextSize(21);moderationTitle.setTextColor(Color.WHITE);root.addView(moderationTitle);
  CheckBox detectLinks=new CheckBox(this);detectLinks.setText("Detectar links nas mensagens");detectLinks.setTextColor(Color.WHITE);detectLinks.setChecked(prefs.getBoolean("detect_links",false));root.addView(detectLinks);
  detectLinks.setOnCheckedChangeListener((v,checked)->prefs.edit().putBoolean("detect_links",checked).apply());
  CheckBox detectSpam=new CheckBox(this);detectSpam.setText("Detectar mensagens repetidas (spam)");detectSpam.setTextColor(Color.WHITE);detectSpam.setChecked(prefs.getBoolean("detect_spam",false));root.addView(detectSpam);
  detectSpam.setOnCheckedChangeListener((v,checked)->prefs.edit().putBoolean("detect_spam",checked).apply());
  TextView moderationInfo=new TextView(this);moderationInfo.setText("Avisos automáticos de links e spam dependem das notificações do WhatsApp Business. O app não apaga mensagens nem silencia membros automaticamente.");moderationInfo.setTextColor(Color.LTGRAY);root.addView(moderationInfo);
  TextView scheduleTitle=new TextView(this);scheduleTitle.setText("🕒 Horário de funcionamento");scheduleTitle.setTextSize(21);scheduleTitle.setTextColor(Color.WHITE);root.addView(scheduleTitle);
  CheckBox schedule=new CheckBox(this);schedule.setText("Ativar horário programado");schedule.setTextColor(Color.WHITE);schedule.setChecked(prefs.getBoolean("schedule_enabled",false));root.addView(schedule);
  EditText start=new EditText(this);start.setHint("Início (HH:mm)");start.setTextColor(Color.WHITE);start.setHintTextColor(Color.LTGRAY);start.setSingleLine(true);start.setText(timeText(prefs.getInt("schedule_from",480)));root.addView(start);styleInput(start);
  EditText end=new EditText(this);end.setHint("Fim (HH:mm)");end.setTextColor(Color.WHITE);end.setHintTextColor(Color.LTGRAY);end.setSingleLine(true);end.setText(timeText(prefs.getInt("schedule_to",1320)));root.addView(end);styleInput(end);
  Button saveSchedule=new Button(this);saveSchedule.setText("Salvar horário");root.addView(saveSchedule);styleButton(saveSchedule,0xFF7C3AED);
  saveSchedule.setOnClickListener(v->{
   int from=parseTime(start.getText().toString()),to=parseTime(end.getText().toString());
   if(from<0||to<0||from==to){Toast.makeText(this,"Use HH:mm, com início e fim diferentes",Toast.LENGTH_LONG).show();return;}
   prefs.edit().putInt("schedule_from",from).putInt("schedule_to",to).putBoolean("schedule_enabled",schedule.isChecked()).apply();
   Toast.makeText(this,"Horário salvo",Toast.LENGTH_SHORT).show();updateStatus();
  });
  TextView statsTitle=new TextView(this);statsTitle.setText("📊 Painel ADM e histórico");statsTitle.setTextSize(21);statsTitle.setTextColor(Color.WHITE);root.addView(statsTitle);
  Button history=new Button(this);history.setText("Ver histórico de alertas");root.addView(history);styleButton(history,0xFF0F766E);
  history.setOnClickListener(v->new android.app.AlertDialog.Builder(this).setTitle("Histórico de moderação").setMessage(ModerationLog.summary(this)).setPositiveButton("Fechar",null).show());
  Button refresh=new Button(this);refresh.setText("Atualizar estatísticas");root.addView(refresh);styleButton(refresh,0xFF2563EB);
  refresh.setOnClickListener(v->updateStatus());
  Button save=new Button(this);save.setText("Salvar nome do grupo");root.addView(save);styleButton(save,0xFF2563EB);
  save.setOnClickListener(v->{prefs.edit().putString("group",group.getText().toString().trim()).apply();Toast.makeText(this,"Grupo salvo",Toast.LENGTH_SHORT).show();});
  Switch toggle=new Switch(this);toggle.setText("Ativar respostas automáticas");toggle.setTextColor(Color.WHITE);toggle.setChecked(prefs.getBoolean("enabled",false));root.addView(toggle);toggle.setPadding(12,20,12,20);toggle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1E293B));
  toggle.setOnCheckedChangeListener((v,checked)->{
   if(checked){
    String enabled=Settings.Secure.getString(getContentResolver(),"enabled_notification_listeners");
    if(enabled==null||!enabled.contains(getPackageName())){toggle.setChecked(false);Toast.makeText(this,"Autorize acesso às notificações",Toast.LENGTH_LONG).show();return;}
    try{if(RuleStore.count(this)==0){toggle.setChecked(false);Toast.makeText(this,"Importe regras antes de ativar",Toast.LENGTH_LONG).show();return;}}
    catch(Exception e){toggle.setChecked(false);return;}
   }
   prefs.edit().putBoolean("enabled",checked).apply();updateStatus();
  });
  Button permissions=new Button(this);permissions.setText("Autorizar acesso às notificações");root.addView(permissions);styleButton(permissions,0xFF7C3AED);
  permissions.setOnClickListener(v->startActivity(new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
  Button importButton=new Button(this);importButton.setText("📂 Importar regras CSV");root.addView(importButton);styleButton(importButton,0xFFF59E0B);
  importButton.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_OPEN_DOCUMENT);i.setType("*/*");i.addCategory(Intent.CATEGORY_OPENABLE);i.putExtra(Intent.EXTRA_MIME_TYPES,new String[]{"text/csv","text/comma-separated-values","application/vnd.ms-excel","application/octet-stream"});startActivityForResult(i,PICK_CSV);});
  Button backup=new Button(this);backup.setText("💾 Exportar backup das regras CSV");root.addView(backup);styleButton(backup,0xFF0F766E);
  backup.setOnClickListener(v->{Intent i=new Intent(Intent.ACTION_CREATE_DOCUMENT);i.setType("text/csv");i.addCategory(Intent.CATEGORY_OPENABLE);i.putExtra(Intent.EXTRA_TITLE,"GranjaBot_regras_backup.csv");startActivityForResult(i,EXPORT_CSV);});
  TextView backupNote=new TextView(this);backupNote.setText("A exportação salva as regras atuais. Para restaurar, use Importar regras CSV.");backupNote.setTextColor(Color.LTGRAY);root.addView(backupNote);
  EditText test=new EditText(this);test.setTextColor(Color.WHITE);test.setHintTextColor(Color.LTGRAY);test.setHint("Digite comando para testar: !menu");root.addView(test);styleInput(test);
  Button testButton=new Button(this);testButton.setText("Testar regra");root.addView(testButton);styleButton(testButton,0xFF059669);
  TextView result=new TextView(this);result.setTextColor(Color.WHITE);result.setTextIsSelectable(true);root.addView(result);
  testButton.setOnClickListener(v->{try{String reply=RuleStore.match(this,test.getText().toString());result.setText(reply==null?"Nenhuma regra exata encontrada":reply);}catch(Exception e){result.setText("Erro: "+e.getMessage());}});
  status=new TextView(this);status.setTextColor(Color.WHITE);status.setPadding(0,24,0,0);root.addView(status);
  TextView note=new TextView(this);note.setText("Requer acesso às notificações. Respostas dependem do botão Responder oferecido pelo WhatsApp Business. Não é integração oficial.");note.setTextColor(Color.LTGRAY);root.addView(note);
  updateStatus();
 }
 private static String timeText(int n){return String.format(java.util.Locale.ROOT,"%02d:%02d",n/60,n%60);}
 private static int parseTime(String s){try{if(!s.matches("\\d{2}:\\d{2}"))return -1;int h=Integer.parseInt(s.substring(0,2)),m=Integer.parseInt(s.substring(3,5));return h<24&&m<60?h*60+m:-1;}catch(Exception e){return -1;}}
 private void styleInput(EditText e){e.setSingleLine(true);e.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF38BDF8));e.setPadding(12,16,12,16);}
 private void styleButton(Button b,int color){b.setTextColor(Color.WHITE);b.setAllCaps(false);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));b.setPadding(8,12,8,12);}
 private void updateStatus(){if(status==null)return;try{status.setText((prefs.getBoolean("enabled",false)?"🟢 Ativado":"⚪ Desativado")+"\nRegras importadas: "+RuleStore.count(this));}catch(Exception e){status.setText("Erro ao ler regras: "+e.getMessage());}}
 @Override protected void onActivityResult(int req,int result,Intent data){
  super.onActivityResult(req,result,data);
  if(req==EXPORT_CSV&&result==RESULT_OK&&data!=null&&data.getData()!=null){
   try(java.io.InputStream in=new java.io.File(getFilesDir(),"rules.csv").exists()?new java.io.FileInputStream(new java.io.File(getFilesDir(),"rules.csv")):getAssets().open("regras.csv");
       java.io.OutputStream out=getContentResolver().openOutputStream(data.getData())){
    byte[] buffer=new byte[8192];int n;while((n=in.read(buffer))!=-1)out.write(buffer,0,n);
    Toast.makeText(this,"Backup CSV salvo",Toast.LENGTH_LONG).show();
   }catch(Exception e){Toast.makeText(this,"Erro ao exportar: "+e.getMessage(),Toast.LENGTH_LONG).show();}
  }
  if(req==PICK_CSV&&result==RESULT_OK&&data!=null&&data.getData()!=null){
   try(java.io.InputStream in=getContentResolver().openInputStream(data.getData())){
    int n=RuleStore.importCsv(this,in);Toast.makeText(this,n+" regras importadas",Toast.LENGTH_LONG).show();updateStatus();
   }catch(Exception e){Toast.makeText(this,"Falha na importação: "+e.getMessage(),Toast.LENGTH_LONG).show();}
  }
 }
}