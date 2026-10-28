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
  Button openThemes=new Button(this);openThemes.setText("🎨 PERSONALIZAR TEMA");root.addView(openThemes);styleButton(openThemes,0xFF4ADE80);
  int savedTheme=prefs.getInt("panel_theme",0);
  openThemes.setOnClickListener(v->{
   LinearLayout options=new LinearLayout(this);options.setOrientation(LinearLayout.VERTICAL);options.setPadding(20,12,20,8);
   ScrollView optionScroll=new ScrollView(this);optionScroll.addView(options);
  Spinner themePicker=new Spinner(this);
  String[] themeNames={"Preto e verde","Claro moderno (tema 3)","Escuro azul","Roxo premium","Preto e dourado","Personalizado"};
  android.widget.ArrayAdapter<String> themeAdapter=new android.widget.ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,themeNames);
  themeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);themePicker.setAdapter(themeAdapter);options.addView(themePicker);
themePicker.setSelection(Math.max(0,Math.min(5,savedTheme)));
  TextView customLabel=new TextView(this);customLabel.setText("Cores personalizadas (hexadecimal, ex.: #090F0B)");options.addView(customLabel);
  EditText customBg=new EditText(this);customBg.setSingleLine(true);customBg.setHint("Fundo #090F0B");customBg.setText(prefs.getString("theme_bg","#090F0B"));options.addView(customBg);
  EditText customAccent=new EditText(this);customAccent.setSingleLine(true);customAccent.setHint("Destaque #4ADE80");customAccent.setText(prefs.getString("theme_accent","#4ADE80"));options.addView(customAccent);
  EditText customText=new EditText(this);customText.setHint("Cor das letras #FFFFFF");customText.setText(prefs.getString("theme_text","#FFFFFF"));options.addView(customText);
  EditText customPanel=new EditText(this);customPanel.setHint("Cor dos campos #17271C");customPanel.setText(prefs.getString("theme_panel","#17271C"));options.addView(customPanel);
  EditText customButton=new EditText(this);customButton.setSingleLine(true);customButton.setHint("Cor dos botões #4ADE80");customButton.setText(prefs.getString("theme_button","#4ADE80"));options.addView(customButton);
  EditText customTitle=new EditText(this);customTitle.setSingleLine(true);customTitle.setHint("Cor dos títulos #4ADE80");customTitle.setText(prefs.getString("theme_title","#4ADE80"));options.addView(customTitle);
  TextView textColorLabel=new TextView(this);textColorLabel.setText("🔤 Cor de TODAS as letras");options.addView(textColorLabel);
  Spinner textColorPicker=new Spinner(this);String[] colorNames={"Automática (mais legível)","Preta","Amarela","Azul","Vermelha"};
  android.widget.ArrayAdapter<String> colorAdapter=new android.widget.ArrayAdapter<>(this,android.R.layout.simple_spinner_item,colorNames);
  colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);textColorPicker.setAdapter(colorAdapter);options.addView(textColorPicker);
  textColorPicker.setSelection(Math.max(0,Math.min(4,prefs.getInt("text_color_choice",0))));
  android.app.AlertDialog dialog=new android.app.AlertDialog.Builder(this).setTitle("🎨 Escolher tema e cores").setView(optionScroll).setNegativeButton("Fechar",(d,w)->{}).create();
  Button applyTheme=new Button(this);applyTheme.setText("💾 Aplicar e salvar tema");options.addView(applyTheme);
  applyTheme.setOnClickListener(btn->{
   int theme=themePicker.getSelectedItemPosition();
   if(theme==5){try{Color.parseColor(customBg.getText().toString().trim());Color.parseColor(customAccent.getText().toString().trim());Color.parseColor(customText.getText().toString().trim());Color.parseColor(customPanel.getText().toString().trim());Color.parseColor(customButton.getText().toString().trim());Color.parseColor(customTitle.getText().toString().trim());}catch(Exception e){Toast.makeText(this,"Use cores como #090F0B",Toast.LENGTH_LONG).show();return;}}
   prefs.edit().putInt("panel_theme",theme).putInt("text_color_choice",textColorPicker.getSelectedItemPosition()).putString("theme_bg",customBg.getText().toString().trim()).putString("theme_accent",customAccent.getText().toString().trim()).putString("theme_text",customText.getText().toString().trim()).putString("theme_panel",customPanel.getText().toString().trim()).putString("theme_button",customButton.getText().toString().trim()).putString("theme_title",customTitle.getText().toString().trim()).apply();
   applyPanelTheme(root,theme);dialog.dismiss();Toast.makeText(this,"Tema salvo!",Toast.LENGTH_SHORT).show();
  });
  dialog.show();
  });
  TextView title=new TextView(this);title.setText("🐔🤖 GranjaBot ADM");title.setTextSize(26);title.setTextColor(0xFF4ADE80);root.addView(title);
  TextView desc=new TextView(this);desc.setText("WhatsApp Business • Respostas por notificações • CSV separado");desc.setTextColor(0xFFE2E8F0);root.addView(desc);
  EditText group=new EditText(this);group.setTextColor(0xFFF3FFF5);group.setHintTextColor(Color.LTGRAY);group.setHint("Nome exato do grupo");group.setText(prefs.getString("group","🐔🌱 Granja Sustentável | Comunidade Oficial 🐣"));root.addView(group);styleInput(group);
  TextView botLabel=new TextView(this);botLabel.setText("📱 Número do Robô (WhatsApp Business)");botLabel.setTextColor(0xFFF3FFF5);root.addView(botLabel);
  EditText botNumber=new EditText(this);botNumber.setTextColor(0xFFF3FFF5);botNumber.setHintTextColor(Color.LTGRAY);botNumber.setHint("Ex.: +55 62 99999-9999");botNumber.setInputType(android.text.InputType.TYPE_CLASS_PHONE);botNumber.setText(prefs.getString("bot_number",""));root.addView(botNumber);styleInput(botNumber);
  TextView numberStatus=new TextView(this);numberStatus.setTextSize(16);numberStatus.setPadding(12,10,12,10);root.addView(numberStatus);
  Button saveNumber=new Button(this);saveNumber.setText("💾 SALVAR NÚMERO");root.addView(saveNumber);styleButton(saveNumber,0xFF93C5FD);
  Button editNumber=new Button(this);editNumber.setText("✏️ EDITAR NÚMERO");root.addView(editNumber);styleButton(editNumber,0xFFFBBF24);
  Runnable showNumberState=()->{
   String saved=prefs.getString("bot_number","");
   boolean locked=!saved.isEmpty();
   botNumber.setEnabled(!locked);
   botNumber.setFocusable( !locked );
   botNumber.setFocusableInTouchMode(!locked);
   botNumber.setTextColor(locked?0xFF9CA3AF:0xFFF3FFF5);
   numberStatus.setText(locked?"✅ Número salvo: "+saved+" — toque em EDITAR para alterar.":"Informe o número do WhatsApp Business e toque em SALVAR.");
   saveNumber.setVisibility(locked?android.view.View.GONE:android.view.View.VISIBLE);
   editNumber.setVisibility(locked?android.view.View.VISIBLE:android.view.View.GONE);
  };
  saveNumber.setOnClickListener(v->{
   String value=botNumber.getText().toString().trim();
   String digits=value.replaceAll("[^0-9]","");
   if(digits.length()<10||digits.length()>15){Toast.makeText(this,"Informe um número válido com DDD (10 a 15 dígitos).",Toast.LENGTH_LONG).show();return;}
   prefs.edit().putString("bot_number",value).apply();
   showNumberState.run();
   Toast.makeText(this,"✅ Número salvo!",Toast.LENGTH_LONG).show();
  });
  editNumber.setOnClickListener(v->{
   botNumber.setEnabled(true);botNumber.setFocusableInTouchMode(true);botNumber.setTextColor(0xFFF3FFF5);
   saveNumber.setVisibility(android.view.View.VISIBLE);editNumber.setVisibility(android.view.View.GONE);
   numberStatus.setText("✏️ Editando número — toque em SALVAR para confirmar.");
   botNumber.requestFocus();
  });
  showNumberState.run();
  TextView numberNote=new TextView(this);numberNote.setText("Este número é apenas uma identificação salva no painel. O WhatsApp Business instalado e o acesso às notificações determinam as respostas. Ser administrador do grupo não permite ao GranjaBot apagar mensagens automaticamente.");numberNote.setTextColor(0xFFC5E4CD);root.addView(numberNote);
  TextView aiTitle=new TextView(this);aiTitle.setText("🤖 Atendimento com inteligência artificial");aiTitle.setTextSize(23);aiTitle.setTextColor(0xFFF3FFF5);root.addView(aiTitle);sectionHeader(aiTitle);
  TextView aiNote=new TextView(this);aiNote.setText("Requer backend Supabase configurado. Nunca coloque a chave da OpenAI no aplicativo. Mensagens podem ser enviadas ao provedor de IA quando ativado.");aiNote.setTextColor(0xFFC5E4CD);root.addView(aiNote);
  EditText aiUrl=new EditText(this);aiUrl.setHint("URL HTTPS da função granjabot-ia");aiUrl.setTextColor(0xFFF3FFF5);aiUrl.setHintTextColor(Color.LTGRAY);aiUrl.setText(prefs.getString("ai_endpoint",""));root.addView(aiUrl);styleInput(aiUrl);
  EditText aiSecret=new EditText(this);aiSecret.setHint("Segredo compartilhado do backend");aiSecret.setTextColor(0xFFF3FFF5);aiSecret.setHintTextColor(Color.LTGRAY);aiSecret.setInputType(129);aiSecret.setText(prefs.getString("ai_secret",""));root.addView(aiSecret);styleInput(aiSecret);
  Switch aiToggle=new Switch(this);aiToggle.setText("🤖 ATIVAR ATENDIMENTO IA");aiToggle.setTextSize(20);aiToggle.setPadding(12,22,12,22);aiToggle.setTextColor(0xFFF3FFF5);aiToggle.setChecked(prefs.getBoolean("ai_enabled",false));root.addView(aiToggle);
  TextView aiStatus=new TextView(this);aiStatus.setTextSize(18);aiStatus.setPadding(12,16,12,16);aiStatus.setText(prefs.getBoolean("ai_enabled",false)?"🟢 IA ligada":"⚪ IA desligada");root.addView(aiStatus);
  aiToggle.setOnCheckedChangeListener((button,checked)->aiStatus.setText(checked?"🟡 IA selecionada — toque em SALVAR":"⚪ IA desligada — toque em SALVAR"));
  Button aiSave=new Button(this);aiSave.setText("💾 SALVAR E ATIVAR CONFIGURAÇÃO IA");aiSave.setTextSize(19);root.addView(aiSave);styleButton(aiSave,0xFF6EE7B7);
  aiSave.setOnClickListener(v->{
   String url=aiUrl.getText().toString().trim(),secret=aiSecret.getText().toString().trim();
   if(aiToggle.isChecked()&&(!url.startsWith("https://")||secret.length()<16)){Toast.makeText(this,"Informe URL HTTPS e segredo de 16+ caracteres",Toast.LENGTH_LONG).show();return;}
   prefs.edit().putString("ai_endpoint",url).putString("ai_secret",secret).putBoolean("ai_enabled",aiToggle.isChecked()).apply();
   aiStatus.setText(aiToggle.isChecked()?"🟢 IA ligada e salva":"⚪ IA desligada e salva");Toast.makeText(this,"Configuração IA salva",Toast.LENGTH_SHORT).show();
  });
  Button aiDisable=new Button(this);aiDisable.setText("⏹ DESLIGAR IA AGORA");root.addView(aiDisable);styleButton(aiDisable,0xFFDC2626);
  aiDisable.setOnClickListener(v->{aiToggle.setChecked(false);prefs.edit().putBoolean("ai_enabled",false).apply();aiStatus.setText("⚪ IA desligada");Toast.makeText(this,"Atendimento IA desativado",Toast.LENGTH_SHORT).show();});
  TextView aiHelp=new TextView(this);aiHelp.setText("PASSOS DA IA: 1. Informe a URL HTTPS do Supabase. 2. Informe o segredo compartilhado (não é a chave OpenAI). 3. Marque ATIVAR IA. 4. Toque em SALVAR. A IA fica desligada até você ativar.");aiHelp.setTextSize(16);root.addView(aiHelp);
  Button aiDiagnostic=new Button(this);aiDiagnostic.setText("🔎 VERIFICAR ERRO DA IA");root.addView(aiDiagnostic);
  aiDiagnostic.setOnClickListener(v->{String error=prefs.getString("ai_last_error","Nenhum erro de conexão registrado. Faça uma pergunta no grupo e verifique novamente.");new android.app.AlertDialog.Builder(this).setTitle("Diagnóstico da IA").setMessage(error).setPositiveButton("OK",null).show();});
  Button testAi=new Button(this);testAi.setText("🧪 TESTAR CONEXÃO REAL DA IA");root.addView(testAi);styleButton(testAi,0xFF0F766E);
  testAi.setOnClickListener(v->{
   testAi.setEnabled(false);testAi.setText("⏳ Testando Supabase...");
   new Thread(()->{
    String reply=AiClient.ask(getApplicationContext(),"teste_painel","Responda apenas: GranjaBot IA funcionando.");
    String error=prefs.getString("ai_last_error","");
    runOnUiThread(()->{
     testAi.setEnabled(true);testAi.setText("🧪 TESTAR CONEXÃO REAL DA IA");
     new android.app.AlertDialog.Builder(this).setTitle(reply!=null?"✅ IA respondeu":"❌ Falha na IA")
      .setMessage(reply!=null?reply:(error.isEmpty()?"Sem resposta do servidor.":error))
      .setPositiveButton("OK",null).show();
    });
   }).start();
  });
  TextView sendTitle=new TextView(this);sendTitle.setText("📨 Mensagem para o grupo");sendTitle.setTextSize(23);sendTitle.setTextColor(0xFFF3FFF5);root.addView(sendTitle);sectionHeader(sendTitle);
  EditText groupMessage=new EditText(this);groupMessage.setHint("Digite a mensagem que deseja enviar ao grupo");groupMessage.setMinLines(2);groupMessage.setTextColor(0xFFFFFFFF);groupMessage.setHintTextColor(Color.LTGRAY);root.addView(groupMessage);styleInput(groupMessage);
  Button sendGroup=new Button(this);sendGroup.setText("📨 ABRIR WHATSAPP BUSINESS PARA ENVIAR");root.addView(sendGroup);styleButton(sendGroup,0xFF16A34A);
  sendGroup.setOnClickListener(v->{
   String message=groupMessage.getText().toString().trim();
   if(message.isEmpty()){Toast.makeText(this,"Escreva uma mensagem primeiro.",Toast.LENGTH_SHORT).show();return;}
   Intent share=new Intent(Intent.ACTION_SEND);share.setType("text/plain");share.putExtra(Intent.EXTRA_TEXT,message);share.setPackage("com.whatsapp.w4b");
   try{startActivity(share);Toast.makeText(this,"Selecione seu grupo e confirme o envio no WhatsApp Business.",Toast.LENGTH_LONG).show();}
   catch(Exception e){Toast.makeText(this,"Instale ou ative o WhatsApp Business para compartilhar.",Toast.LENGTH_LONG).show();}
  });
  CheckBox welcome=new CheckBox(this);welcome.setText("🐣 Dar boas-vindas quando alguém entrar");welcome.setTextColor(0xFFFFFFFF);welcome.setChecked(prefs.getBoolean("welcome_enabled",true));root.addView(welcome);
  welcome.setOnCheckedChangeListener((v,checked)->prefs.edit().putBoolean("welcome_enabled",checked).apply());
  CheckBox farewell=new CheckBox(this);farewell.setText("👋 Despedir quando alguém sair");farewell.setTextColor(0xFFFFFFFF);farewell.setChecked(prefs.getBoolean("farewell_enabled",true));root.addView(farewell);
  farewell.setOnCheckedChangeListener((v,checked)->prefs.edit().putBoolean("farewell_enabled",checked).apply());
  TextView notice=new TextView(this);notice.setText("Boas-vindas e despedidas dependem de o WhatsApp Business mostrar um aviso de entrada/saída como notificação com opção Responder. O envio manual abre o WhatsApp para você escolher o grupo.");notice.setTextColor(0xFFC5E4CD);root.addView(notice);
  TextView moderationTitle=new TextView(this);moderationTitle.setText("🛡️ Moderação do grupo");moderationTitle.setTextSize(23);moderationTitle.setTextColor(0xFFF3FFF5);root.addView(moderationTitle);sectionHeader(moderationTitle);
  CheckBox detectLinks=new CheckBox(this);detectLinks.setText("Detectar links nas mensagens");detectLinks.setTextColor(0xFFF3FFF5);detectLinks.setChecked(prefs.getBoolean("detect_links",false));root.addView(detectLinks);
  detectLinks.setOnCheckedChangeListener((v,checked)->prefs.edit().putBoolean("detect_links",checked).apply());
  CheckBox detectSpam=new CheckBox(this);detectSpam.setText("Detectar mensagens repetidas (spam)");detectSpam.setTextColor(0xFFF3FFF5);detectSpam.setChecked(prefs.getBoolean("detect_spam",false));root.addView(detectSpam);
  detectSpam.setOnCheckedChangeListener((v,checked)->prefs.edit().putBoolean("detect_spam",checked).apply());
  TextView moderationInfo=new TextView(this);moderationInfo.setText("Avisos automáticos de links e spam dependem das notificações do WhatsApp Business. O app não apaga mensagens nem silencia membros automaticamente.");moderationInfo.setTextColor(0xFFC5E4CD);root.addView(moderationInfo);
  Button moderationPanel=new Button(this);moderationPanel.setText("🚨 MODERAÇÃO ADM — VER ALERTAS");root.addView(moderationPanel);styleButton(moderationPanel,0xFFDC2626);
  moderationPanel.setOnClickListener(v->{
   String summary=ModerationLog.summary(this);
   new android.app.AlertDialog.Builder(this).setTitle("🚨 Central de moderação ADM")
    .setMessage(summary+"\nPara excluir mensagens ou remover participantes, abra o grupo no WhatsApp Business e faça a ação manualmente.")
    .setNegativeButton("Fechar",null)
    .setPositiveButton("📱 ABRIR WHATSAPP BUSINESS",(dialog,which)->{
     android.content.pm.PackageManager pm=getPackageManager();
     Intent launch=pm.getLaunchIntentForPackage("com.whatsapp.w4b");
     if(launch!=null){try{startActivity(launch);}catch(Exception e){Toast.makeText(this,"Não foi possível abrir o WhatsApp Business.",Toast.LENGTH_LONG).show();}}
     else Toast.makeText(this,"WhatsApp Business não encontrado.",Toast.LENGTH_LONG).show();
    }).show();
  });
  TextView alertCount=new TextView(this);alertCount.setTextSize(17);alertCount.setTextColor(0xFFFFD166);
  alertCount.setText("🚨 Alertas registrados: "+prefs.getInt("alerts",0)+" — toque em MODERAÇÃO ADM para atualizar.");root.addView(alertCount);
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
    try{if(RuleStore.count(this)==0&&!prefs.getBoolean("ai_enabled",false)&&!prefs.getBoolean("detect_links",false)&&!prefs.getBoolean("detect_spam",false)){toggle.setChecked(false);Toast.makeText(this,"Ative IA, moderação ou importe regras antes de ligar",Toast.LENGTH_LONG).show();return;}}
    catch(Exception e){if(!prefs.getBoolean("ai_enabled",false)&&!prefs.getBoolean("detect_links",false)&&!prefs.getBoolean("detect_spam",false)){toggle.setChecked(false);Toast.makeText(this,"Falha nas regras; ative IA ou moderação",Toast.LENGTH_LONG).show();return;}}
   }
   prefs.edit().putBoolean("enabled",checked).apply();syncStatusService();updateStatus();
  });
  Button batterySettings=new Button(this);batterySettings.setText("🔋 PERMITIR FUNCIONAMENTO COM TELA BLOQUEADA");root.addView(batterySettings);styleButton(batterySettings,0xFF2563EB);
  batterySettings.setOnClickListener(v->{
   try{
    Intent intent=new Intent(android.provider.Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
    intent.setData(android.net.Uri.parse("package:"+getPackageName()));
    startActivity(intent);
   }catch(Exception e){
    try{startActivity(new Intent(android.provider.Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS));}
    catch(Exception ignored){Toast.makeText(this,"Abra Configurações > Apps > GranjaBot ADM > Bateria e permita segundo plano.",Toast.LENGTH_LONG).show();}
   }
  });
  Button replyDiagnostic=new Button(this);replyDiagnostic.setText("🔎 DIAGNÓSTICO DE RESPOSTAS E LINKS");root.addView(replyDiagnostic);styleButton(replyDiagnostic,0xFF0F766E);
  replyDiagnostic.setOnClickListener(v->{
   long seen=prefs.getLong("last_incoming_at",0L);
   String time=seen==0?"Nenhuma mensagem identificada":new java.text.SimpleDateFormat("dd/MM HH:mm:ss",java.util.Locale.getDefault()).format(new java.util.Date(seen));
   String info="Robô: "+(prefs.getBoolean("enabled",false)?"ATIVO":"DESLIGADO")
    +"\\nIA: "+(prefs.getBoolean("ai_enabled",false)?"ATIVA":"DESLIGADA")
    +"\\nDetectar links: "+(prefs.getBoolean("detect_links",false)?"ATIVO":"DESLIGADO")
    +"\\nÚltima mensagem recebida: "+time
    +"\\nTexto: "+prefs.getString("last_incoming_preview","Nenhum")
    +"\\nÚltima falha de resposta: "+prefs.getString("last_reply_error","Nenhuma registrada")
    +"\\nAlertas registrados: "+prefs.getInt("alerts",0);
   new android.app.AlertDialog.Builder(this).setTitle("Diagnóstico do GranjaBot").setMessage(info).setPositiveButton("Fechar",null).show();
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
  testButton.setOnClickListener(v->{String message;try{String reply=RuleStore.match(this,test.getText().toString());message=reply==null?"Nenhuma regra exata encontrada. Experimente !menu":reply;}catch(Exception e){message="Erro no teste: "+e.getMessage();}result.setText(message);result.setTextSize(18);result.setPadding(14,18,14,18);result.setBackgroundColor(0xFF20392A);result.setTextColor(Color.WHITE);Toast.makeText(this,message.length()>110?message.substring(0,110):message,Toast.LENGTH_LONG).show();});
  status=new TextView(this);status.setTextColor(0xFFF3FFF5);status.setPadding(0,24,0,0);root.addView(status);
  TextView note=new TextView(this);note.setText("Requer acesso às notificações. Respostas dependem do botão Responder oferecido pelo WhatsApp Business. Não é integração oficial.");note.setTextColor(0xFFC5E4CD);root.addView(note);
  status.setBackgroundColor(0xFF17271C);status.setPadding(18,20,18,20);status.setTextSize(17);updateStatus();applyPanelTheme(root,savedTheme);
 }
 private void applyPanelTheme(LinearLayout root,int choice){
  int bg=0xFF090F0B,panel=0xFF17271C,fg=0xFFF3FFF5,accent=0xFF4ADE80,title=0xFF4ADE80,button=0xFF4ADE80;
  if(choice==1){bg=0xFFF1F5F9;panel=0xFFFFFFFF;fg=0xFF0F172A;accent=0xFF2563EB;title=accent;button=accent;}
  else if(choice==2){bg=0xFF0B1220;panel=0xFF1E293B;fg=0xFFF8FAFC;accent=0xFF38BDF8;title=accent;button=accent;}
  else if(choice==3){bg=0xFF130D24;panel=0xFF2B2045;fg=0xFFFAF5FF;accent=0xFFC4B5FD;title=accent;button=accent;}
  else if(choice==4){bg=0xFF101010;panel=0xFF242424;fg=0xFFFFF9E8;accent=0xFFFBBF24;title=accent;button=accent;}
  else if(choice==5){
   try{
    bg=Color.parseColor(prefs.getString("theme_bg","#090F0B"));
    panel=Color.parseColor(prefs.getString("theme_panel","#17271C"));
    fg=Color.parseColor(prefs.getString("theme_text","#FFFFFF"));
    accent=Color.parseColor(prefs.getString("theme_accent","#4ADE80"));
    button=Color.parseColor(prefs.getString("theme_button","#4ADE80"));
    title=Color.parseColor(prefs.getString("theme_title","#4ADE80"));
   }catch(IllegalArgumentException ignored){}
  }
  int selected=prefs.getInt("text_color_choice",0);
  if(selected==1)fg=Color.BLACK;
  else if(selected==2)fg=0xFFFFD600;
  else if(selected==3)fg=0xFF1565C0;
  else if(selected==4)fg=0xFFD32F2F;
  root.setBackgroundColor(bg);
  recolorChildren(root,fg,panel,accent,button,title,bg);
 }
 private static int ensureContrast(int fg,int bg){
  double a=luminance(fg),b=luminance(bg);
  if((Math.max(a,b)+0.05)/(Math.min(a,b)+0.05)>=4.5)return fg;
  return b>0.18?Color.BLACK:Color.WHITE;
 }
 private static double luminance(int c){
  double r=Color.red(c)/255.0,g=Color.green(c)/255.0,b=Color.blue(c)/255.0;
  r=r<=0.04045?r/12.92:Math.pow((r+0.055)/1.055,2.4);
  g=g<=0.04045?g/12.92:Math.pow((g+0.055)/1.055,2.4);
  b=b<=0.04045?b/12.92:Math.pow((b+0.055)/1.055,2.4);
  return 0.2126*r+0.7152*g+0.0722*b;
 }
 private static boolean androidxColorLight(int c){return (Color.red(c)*299+Color.green(c)*587+Color.blue(c)*114)>150000;}
 private void recolorChildren(android.view.ViewGroup parent,int fg,int panel,int accent,int button,int title,int bg){
  for(int i=0;i<parent.getChildCount();i++){
   android.view.View v=parent.getChildAt(i);
   if(v instanceof EditText){
    EditText e=(EditText)v;e.setTextColor(ensureContrast(fg,panel));
    e.setHintTextColor(ensureContrast(0xFF888888,panel));
    android.graphics.drawable.GradientDrawable d=new android.graphics.drawable.GradientDrawable();
    d.setColor(panel);d.setCornerRadius(14);d.setStroke(2,accent);e.setBackground(d);
   }else if(v instanceof android.widget.CompoundButton){
    android.widget.CompoundButton c=(android.widget.CompoundButton)v;
    c.setTextColor(ensureContrast(fg,bg));
    c.setTextSize(18);
    c.setPadding(12,18,12,18);
    c.setBackgroundColor(bg);
    int indicator=ensureContrast(accent,bg);
    c.setButtonTintList(android.content.res.ColorStateList.valueOf(indicator));
    if(c instanceof Switch){
     Switch sw=(Switch)c;
     sw.setThumbTintList(android.content.res.ColorStateList.valueOf(indicator));
     sw.setTrackTintList(android.content.res.ColorStateList.valueOf(ensureContrast(0xFF7C8794,bg)));
    }
   }else if(v instanceof Button){
    Button b=(Button)v;b.setTextColor(ensureContrast(fg,button));
    b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(button));
   }else if(v instanceof TextView){
    TextView t=(TextView)v;
    boolean header="section_header".equals(t.getTag())||t.getText().toString().startsWith("🐔🤖 GranjaBot");
    t.setTextColor(header?ensureContrast(title,"section_header".equals(t.getTag())?panel:bg):ensureContrast(fg,v==status?panel:bg));
    if("section_header".equals(t.getTag()))t.setBackgroundColor(panel);
   }
   if(v instanceof android.view.ViewGroup)recolorChildren((android.view.ViewGroup)v,fg,panel,accent,button,title,bg);
  }
 }
 private void sectionHeader(TextView v){v.setTag("section_header");v.setTextColor(0xFF4ADE80);v.setBackgroundColor(0xFF17271C);v.setPadding(16,22,16,22);android.widget.LinearLayout.LayoutParams p=new android.widget.LinearLayout.LayoutParams(-1,-2);p.setMargins(0,22,0,12);v.setLayoutParams(p);}
 private static String timeText(int n){return String.format(java.util.Locale.ROOT,"%02d:%02d",n/60,n%60);}
 private static int parseTime(String s){try{if(!s.matches("\\d{2}:\\d{2}"))return -1;int h=Integer.parseInt(s.substring(0,2)),m=Integer.parseInt(s.substring(3,5));return h<24&&m<60?h*60+m:-1;}catch(Exception e){return -1;}}
 private void styleInput(EditText e){e.setSingleLine(true);e.setTextSize(18);e.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4ADE80));android.graphics.drawable.GradientDrawable inputBg=new android.graphics.drawable.GradientDrawable();inputBg.setColor(0xFF17271C);inputBg.setCornerRadius(14);inputBg.setStroke(2,0xFF397B4D);e.setBackground(inputBg);e.setTextColor(0xFFFFFFFF);e.setHintTextColor(0xFFB9D6C2);e.setSelectAllOnFocus(false);e.setPadding(18,22,18,22);android.widget.LinearLayout.LayoutParams p=new android.widget.LinearLayout.LayoutParams(-1,-2);p.setMargins(0,8,0,14);e.setLayoutParams(p);}
 private void styleButton(Button b,int color){b.setTextColor(0xFF07170A);b.setTextSize(16);b.setAllCaps(false);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4ADE80));b.setPadding(14,18,14,18);android.widget.LinearLayout.LayoutParams p=new android.widget.LinearLayout.LayoutParams(-1,-2);p.setMargins(0,8,0,14);b.setLayoutParams(p);}
 private void syncStatusService(){
  Intent intent=new Intent(this,AdmKeepAliveService.class);
  if(prefs.getBoolean("enabled",false)){
   if(android.os.Build.VERSION.SDK_INT>=33&&checkSelfPermission("android.permission.POST_NOTIFICATIONS")!=android.content.pm.PackageManager.PERMISSION_GRANTED)
    requestPermissions(new String[]{"android.permission.POST_NOTIFICATIONS"},2026);
   try{if(android.os.Build.VERSION.SDK_INT>=26)startForegroundService(intent);else startService(intent);}
   catch(RuntimeException e){prefs.edit().putBoolean("foreground_running",false).putString("foreground_error",e.getClass().getSimpleName()).apply();Toast.makeText(this,"Não foi possível iniciar o serviço permanente.",Toast.LENGTH_LONG).show();}
  }else stopService(intent);
 }
 @Override protected void onResume(){super.onResume();if(prefs!=null&&prefs.getBoolean("enabled",false))syncStatusService();}
 private void updateStatus(){
  if(status==null)return;
  try{
   status.setText((prefs.getBoolean("enabled",false)?"🟢 Ativado":"⚪ Desativado")
    +"\nRegras disponíveis: "+RuleStore.count(this)
    +"\nRespostas enviadas: "+prefs.getInt("replies",0)
    +"\nAlertas detectados: "+prefs.getInt("alerts",0)
    +"\nHorário: "+(prefs.getBoolean("schedule_enabled",false)?"Programado":"Sempre ativo")
    +"\nServiço permanente: "+(prefs.getBoolean("foreground_running",false)?"🟢 ATIVO":"🔴 PARADO")
    +(prefs.getString("foreground_error","").isEmpty()?"":"\nFalha do serviço: "+prefs.getString("foreground_error","")));
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