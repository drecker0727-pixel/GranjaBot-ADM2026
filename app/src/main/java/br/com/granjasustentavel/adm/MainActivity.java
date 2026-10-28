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
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(22,28,22,28);root.setBackgroundColor(0xFF090F0B);
  ScrollView scroll=new ScrollView(this);scroll.addView(root);setContentView(scroll);
  TextView themeLabel=new TextView(this);themeLabel.setText("🎨 Escolha o tema do painel");themeLabel.setTextSize(21);root.addView(themeLabel);
  Spinner themePicker=new Spinner(this);
  String[] themeNames={"Preto e verde","Claro moderno (tema 3)","Escuro azul","Roxo premium","Preto e dourado","Personalizado"};
  android.widget.ArrayAdapter<String> themeAdapter=new android.widget.ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,themeNames);
  themePicker.setAdapter(themeAdapter);root.addView(themePicker);
  int savedTheme=prefs.getInt("panel_theme",0);themePicker.setSelection(Math.max(0,Math.min(5,savedTheme)));
  TextView customLabel=new TextView(this);customLabel.setText("Cores personalizadas (hexadecimal, ex.: #090F0B)");root.addView(customLabel);
  EditText customBg=new EditText(this);customBg.setSingleLine(true);customBg.setHint("Fundo #090F0B");customBg.setText(prefs.getString("theme_bg","#090F0B"));root.addView(customBg);
  EditText customAccent=new EditText(this);customAccent.setSingleLine(true);customAccent.setHint("Destaque #4ADE80");customAccent.setText(prefs.getString("theme_accent","#4ADE80"));root.addView(customAccent);
  Button applyTheme=new Button(this);applyTheme.setText("Aplicar e salvar meu tema");root.addView(applyTheme);
  applyTheme.setOnClickListener(v->{
   int theme=themePicker.getSelectedItemPosition();
   if(theme==5){try{Color.parseColor(customBg.getText().toString().trim());Color.parseColor(customAccent.getText().toString().trim());}catch(Exception e){Toast.makeText(this,"Use cores como #090F0B",Toast.LENGTH_LONG).show();return;}}
   prefs.edit().putInt("panel_theme",theme).putString("theme_bg",customBg.getText().toString().trim()).putString("theme_accent",customAccent.getText().toString().trim()).apply();
   applyPanelTheme(root,theme);Toast.makeText(this,"Tema salvo!",Toast.LENGTH_SHORT).show();
  });
  TextView title=new TextView(this);title.setText("🐔🤖 GranjaBot ADM");title.setTextSize(26);title.setTextColor(0xFF4ADE80);root.addView(title);
  TextView desc=new TextView(this);desc.setText("WhatsApp Business • Respostas por notificações • CSV separado");desc.setTextColor(0xFFE2E8F0);root.addView(desc);
  EditText group=new EditText(this);group.setTextColor(0xFFF3FFF5);group.setHintTextColor(Color.LTGRAY);group.setHint("Nome exato do grupo");group.setText(prefs.getString("group","🐔🌱 Granja Sustentável | Comunidade Oficial 🐣"));root.addView(group);styleInput(group);
  TextView botLabel=new TextView(this);botLabel.setText("📱 Número do Robô (WhatsApp Business)");botLabel.setTextColor(0xFFF3FFF5);root.addView(botLabel);
  EditText botNumber=new EditText(this);botNumber.setTextColor(0xFFF3FFF5);botNumber.setHintTextColor(Color.LTGRAY);botNumber.setHint("Ex.: +55 62 99999-9999");botNumber.setInputType(android.text.InputType.TYPE_CLASS_PHONE);botNumber.setText(prefs.getString("bot_number",""));root.addView(botNumber);styleInput(botNumber);
  Button saveNumber=new Button(this);saveNumber.setText("Salvar número do robô");root.addView(saveNumber);styleButton(saveNumber,0xFF93C5FD);
  saveNumber.setOnClickListener(v->{prefs.edit().putString("bot_number",botNumber.getText().toString().trim()).apply();Toast.makeText(this,"Número salvo. Ative esse número no WhatsApp Business.",Toast.LENGTH_LONG).show();});
  TextView aiTitle=new TextView(this);aiTitle.setText("🤖 Atendimento com inteligência artificial");aiTitle.setTextSize(23);aiTitle.setTextColor(0xFFF3FFF5);root.addView(aiTitle);sectionHeader(aiTitle);
  TextView aiNote=new TextView(this);aiNote.setText("Requer backend Supabase configurado. Nunca coloque a chave da OpenAI no aplicativo. Mensagens podem ser enviadas ao provedor de IA quando ativado.");aiNote.setTextColor(0xFFC5E4CD);root.addView(aiNote);
  EditText aiUrl=new EditText(this);aiUrl.setHint("URL HTTPS da função granjabot-ia");aiUrl.setTextColor(0xFFF3FFF5);aiUrl.setHintTextColor(Color.LTGRAY);aiUrl.setText(prefs.getString("ai_endpoint",""));root.addView(aiUrl);styleInput(aiUrl);
  EditText aiSecret=new EditText(this);aiSecret.setHint("Segredo compartilhado do backend");aiSecret.setTextColor(0xFFF3FFF5);aiSecret.setHintTextColor(Color.LTGRAY);aiSecret.setInputType(129);aiSecret.setText(prefs.getString("ai_secret",""));root.addView(aiSecret);styleInput(aiSecret);
  Switch aiToggle=new Switch(this);aiToggle.setText("Ativar atendimento IA");aiToggle.setTextColor(0xFFF3FFF5);aiToggle.setChecked(prefs.getBoolean("ai_enabled",false));root.addView(aiToggle);
  Button aiSave=new Button(this);aiSave.setText("Salvar configuração IA");root.addView(aiSave);styleButton(aiSave,0xFF6EE7B7);
  aiSave.setOnClickListener(v->{
   String url=aiUrl.getText().toString().trim(),secret=aiSecret.getText().toString().trim();
   if(aiToggle.isChecked()&&(!url.startsWith("https://")||secret.length()<16)){Toast.makeText(this,"Informe URL HTTPS e segredo de 16+ caracteres",Toast.LENGTH_LONG).show();return;}
   prefs.edit().putString("ai_endpoint",url).putString("ai_secret",secret).putBoolean("ai_enabled",aiToggle.isChecked()).apply();
   Toast.makeText(this,"Configuração IA salva",Toast.LENGTH_SHORT).show();
  });
  TextView moderationTitle=new TextView(this);moderationTitle.setText("🛡️ Moderação do grupo");moderationTitle.setTextSize(23);moderationTitle.setTextColor(0xFFF3FFF5);root.addView(moderationTitle);sectionHeader(moderationTitle);
  CheckBox detectLinks=new CheckBox(this);detectLinks.setText("Detectar links nas mensagens");detectLinks.setTextColor(0xFFF3FFF5);detectLinks.setChecked(prefs.getBoolean("detect_links",false));root.addView(detectLinks);
  detectLinks.setOnCheckedChangeListener((v,checked)->prefs.edit().putBoolean("detect_links",checked).apply());
  CheckBox detectSpam=new CheckBox(this);detectSpam.setText("Detectar mensagens repetidas (spam)");detectSpam.setTextColor(0xFFF3FFF5);detectSpam.setChecked(prefs.getBoolean("detect_spam",false));root.addView(detectSpam);
  detectSpam.setOnCheckedChangeListener((v,checked)->prefs.edit().putBoolean("detect_spam",checked).apply());
  TextView moderationInfo=new TextView(this);moderationInfo.setText("Avisos automáticos de links e spam dependem das notificações do WhatsApp Business. O app não apaga mensagens nem silencia membros automaticamente.");moderationInfo.setTextColor(0xFFC5E4CD);root.addView(moderationInfo);
  TextView scheduleTitle=new TextView(this);scheduleTitle.setText("🕒 Horário de funcionamento");scheduleTitle.setTextSize(23);scheduleTitle.setTextColor(0xFFF3FFF5);root.addView(scheduleTitle);sectionHeader(scheduleTitle);
  CheckBox schedule=new CheckBox(this);schedule.setText("Ativar horário programado");schedule.setTextColor(0xFFF3FFF5);schedule.setChecked(prefs.getBoolean("schedule_enabled",false));root.addView(schedule);
  EditText start=new EditText(this);start.setHint("Início (HH:mm)");start.setTextColor(0xFFF3FFF5);start.setHintTextColor(Color.LTGRAY);start.setSingleLine(true);start.setText(timeText(prefs.getInt("schedule_from",480)));root.addView(start);styleInput(start);
  EditText end=new EditText(this);end.setHint("Fim (HH:mm)");end.setTextColor(0xFFF3FFF5);end.setHintTextColor(Color.LTGRAY);end.setSingleLine(true);end.setText(timeText(prefs.getInt("schedule_to",1320)));root.addView(end);styleInput(end);
  Button saveSchedule=new Button(this);saveSchedule.setText("Salvar horário");root.addView(saveSchedule);styleButton(saveSchedule,0xFF7C3AED);
  saveSchedule.setOnClickListener(v->{
   int from=parseTime(start.getText().toString()),to=parseTime(end.getText().toString());
   if(from<0||to<0||from==to){Toast.makeText(this,"Use HH:mm, com início e fim diferentes",Toast.LENGTH_LONG).show();return;}
   prefs.edit().putInt("schedule_from",from).putInt("schedule_to",to).putBoolean("schedule_enabled",schedule.isChecked()).apply();
   Toast.makeText(this,"Horário salvo",Toast.LENGTH_SHORT).show();updateStatus();
  });
  TextView statsTitle=new TextView(this);statsTitle.setText("📊 Painel ADM e histórico");statsTitle.setTextSize(23);statsTitle.setTextColor(0xFFF3FFF5);root.addView(statsTitle);sectionHeader(statsTitle);
  Button history=new Button(this);history.setText("Ver histórico de alertas");root.addView(history);styleButton(history,0xFF0F766E);
  history.setOnClickListener(v->new android.app.AlertDialog.Builder(this).setTitle("Histórico de moderação").setMessage(ModerationLog.summary(this)).setPositiveButton("Fechar",null).show());
  Button refresh=new Button(this);refresh.setText("Atualizar estatísticas");root.addView(refresh);styleButton(refresh,0xFF2563EB);
  refresh.setOnClickListener(v->updateStatus());
  Button save=new Button(this);save.setText("Salvar nome do grupo");root.addView(save);styleButton(save,0xFF2563EB);
  save.setOnClickListener(v->{prefs.edit().putString("group",group.getText().toString().trim()).apply();Toast.makeText(this,"Grupo salvo",Toast.LENGTH_SHORT).show();});
  Switch toggle=new Switch(this);toggle.setText("Ativar respostas automáticas");toggle.setTextColor(0xFFF3FFF5);toggle.setChecked(prefs.getBoolean("enabled",false));root.addView(toggle);toggle.setPadding(12,20,12,20);toggle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF1E293B));
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
  TextView backupNote=new TextView(this);backupNote.setText("A exportação salva as regras atuais. Para restaurar, use Importar regras CSV.");backupNote.setTextColor(0xFFC5E4CD);root.addView(backupNote);
  EditText test=new EditText(this);test.setTextColor(0xFFF3FFF5);test.setHintTextColor(Color.LTGRAY);test.setHint("Digite comando para testar: !menu");root.addView(test);styleInput(test);
  Button testButton=new Button(this);testButton.setText("Testar regra");root.addView(testButton);styleButton(testButton,0xFF059669);
  TextView result=new TextView(this);result.setTextColor(0xFFF3FFF5);result.setTextIsSelectable(true);root.addView(result);
  testButton.setOnClickListener(v->{try{String reply=RuleStore.match(this,test.getText().toString());result.setText(reply==null?"Nenhuma regra exata encontrada":reply);}catch(Exception e){result.setText("Erro: "+e.getMessage());}});
  status=new TextView(this);status.setTextColor(0xFFF3FFF5);status.setPadding(0,24,0,0);root.addView(status);
  TextView note=new TextView(this);note.setText("Requer acesso às notificações. Respostas dependem do botão Responder oferecido pelo WhatsApp Business. Não é integração oficial.");note.setTextColor(0xFFC5E4CD);root.addView(note);
  status.setBackgroundColor(0xFF17271C);status.setPadding(18,20,18,20);status.setTextSize(17);updateStatus();
 }
 private void applyPanelTheme(LinearLayout root,int choice){
  int bg=0xFF090F0B,panel=0xFF17271C,fg=0xFFF3FFF5,accent=0xFF4ADE80,muted=0xFFC5E4CD,btnText=0xFF07170A;
  if(choice==1){bg=0xFFF1F5F9;panel=0xFFFFFFFF;fg=0xFF0F172A;accent=0xFF2563EB;muted=0xFF475569;btnText=0xFFFFFFFF;}
  else if(choice==2){bg=0xFF0B1220;panel=0xFF1E293B;fg=0xFFF8FAFC;accent=0xFF38BDF8;muted=0xFFCBD5E1;btnText=0xFF08121F;}
  else if(choice==3){bg=0xFF130D24;panel=0xFF2B2045;fg=0xFFFAF5FF;accent=0xFFC4B5FD;muted=0xFFD8CDEC;btnText=0xFF201336;}
  else if(choice==4){bg=0xFF101010;panel=0xFF242424;fg=0xFFFFF9E8;accent=0xFFFBBF24;muted=0xFFDDD1B3;btnText=0xFF1C1500;}
  else if(choice==5){
   try{bg=Color.parseColor(prefs.getString("theme_bg","#090F0B"));accent=Color.parseColor(prefs.getString("theme_accent","#4ADE80"));}catch(Exception ignored){}
   boolean light=androidxColorLight(bg);panel=light?0xFFFFFFFF:0xFF17271C;fg=light?0xFF101820:0xFFFFFFFF;muted=light?0xFF334155:0xFFE2E8F0;btnText=androidxColorLight(accent)?0xFF07170A:0xFFFFFFFF;
  }
  root.setBackgroundColor(bg);
  recolorChildren(root,fg,muted,panel,accent,btnText);
 }
 private static boolean androidxColorLight(int c){return (Color.red(c)*299+Color.green(c)*587+Color.blue(c)*114)>150000;}
 private void recolorChildren(android.view.ViewGroup parent,int fg,int muted,int panel,int accent,int btnText){
  for(int i=0;i<parent.getChildCount();i++){
   android.view.View v=parent.getChildAt(i);
   if(v instanceof EditText){
    EditText e=(EditText)v;e.setTextColor(fg);e.setHintTextColor(muted);
    android.graphics.drawable.GradientDrawable d=new android.graphics.drawable.GradientDrawable();d.setColor(panel);d.setCornerRadius(14);d.setStroke(2,accent);e.setBackground(d);
   }else if(v instanceof Button){
    Button b=(Button)v;b.setTextColor(btnText);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(accent));
   }else if(v instanceof TextView){
    TextView t=(TextView)v;t.setTextColor("section_header".equals(t.getTag())?accent:fg);
    if("section_header".equals(t.getTag()))t.setBackgroundColor(panel);
   }else if(v instanceof Spinner){
    Spinner spinner=(Spinner)v;spinner.setBackgroundTintList(android.content.res.ColorStateList.valueOf(accent));
   }
   if(v instanceof android.view.ViewGroup)recolorChildren((android.view.ViewGroup)v,fg,muted,panel,accent,btnText);
  }
 }
 private void sectionHeader(TextView v){v.setTag("section_header");v.setTextColor(0xFF4ADE80);v.setBackgroundColor(0xFF17271C);v.setPadding(16,22,16,22);android.widget.LinearLayout.LayoutParams p=new android.widget.LinearLayout.LayoutParams(-1,-2);p.setMargins(0,22,0,12);v.setLayoutParams(p);}
 private static String timeText(int n){return String.format(java.util.Locale.ROOT,"%02d:%02d",n/60,n%60);}
 private static int parseTime(String s){try{if(!s.matches("\\d{2}:\\d{2}"))return -1;int h=Integer.parseInt(s.substring(0,2)),m=Integer.parseInt(s.substring(3,5));return h<24&&m<60?h*60+m:-1;}catch(Exception e){return -1;}}
 private void styleInput(EditText e){e.setSingleLine(true);e.setTextSize(18);e.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4ADE80));android.graphics.drawable.GradientDrawable inputBg=new android.graphics.drawable.GradientDrawable();inputBg.setColor(0xFF17271C);inputBg.setCornerRadius(14);inputBg.setStroke(2,0xFF397B4D);e.setBackground(inputBg);e.setTextColor(0xFFFFFFFF);e.setHintTextColor(0xFFB9D6C2);e.setSelectAllOnFocus(false);e.setPadding(18,22,18,22);android.widget.LinearLayout.LayoutParams p=new android.widget.LinearLayout.LayoutParams(-1,-2);p.setMargins(0,8,0,14);e.setLayoutParams(p);}
 private void styleButton(Button b,int color){b.setTextColor(0xFF07170A);b.setTextSize(16);b.setAllCaps(false);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4ADE80));b.setPadding(14,18,14,18);android.widget.LinearLayout.LayoutParams p=new android.widget.LinearLayout.LayoutParams(-1,-2);p.setMargins(0,8,0,14);b.setLayoutParams(p);}
 private void updateStatus(){
  if(status==null)return;
  try{
   status.setText((prefs.getBoolean("enabled",false)?"🟢 Ativado":"⚪ Desativado")
    +"\nRegras disponíveis: "+RuleStore.count(this)
    +"\nRespostas enviadas: "+prefs.getInt("replies",0)
    +"\nAlertas detectados: "+prefs.getInt("alerts",0)
    +"\nHorário: "+(prefs.getBoolean("schedule_enabled",false)?"Programado":"Sempre ativo"));
  }catch(Exception e){status.setText("Erro ao ler regras: "+e.getMessage());}
 }
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