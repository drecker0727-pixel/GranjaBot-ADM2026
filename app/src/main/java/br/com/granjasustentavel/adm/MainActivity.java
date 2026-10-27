package br.com.granjasustentavel.adm;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.graphics.Color;
import android.widget.*;
import android.content.SharedPreferences;
public class MainActivity extends Activity {
 private static final int PICK_CSV=42;
 private SharedPreferences prefs;
 private TextView status;
 @Override public void onCreate(Bundle b){
  super.onCreate(b);prefs=getSharedPreferences("adm",MODE_PRIVATE);
  LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(28,36,28,20);root.setBackgroundColor(Color.rgb(10,17,29));
  ScrollView scroll=new ScrollView(this);scroll.addView(root);setContentView(scroll);
  TextView title=new TextView(this);title.setText("🐔🤖 GranjaBot ADM");title.setTextSize(26);title.setTextColor(Color.WHITE);root.addView(title);
  TextView desc=new TextView(this);desc.setText("WhatsApp Business • Respostas por notificações • CSV separado");desc.setTextColor(Color.LTGRAY);root.addView(desc);
  EditText group=new EditText(this);group.setTextColor(Color.WHITE);group.setHintTextColor(Color.LTGRAY);group.setHint("Nome exato do grupo");group.setText(prefs.getString("group","🐔🌱 Granja Sustentável | Comunidade Oficial 🐣"));root.addView(group);styleInput(group);
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
  EditText test=new EditText(this);test.setTextColor(Color.WHITE);test.setHintTextColor(Color.LTGRAY);test.setHint("Digite comando para testar: !menu");root.addView(test);styleInput(test);
  Button testButton=new Button(this);testButton.setText("Testar regra");root.addView(testButton);styleButton(testButton,0xFF059669);
  TextView result=new TextView(this);result.setTextColor(Color.WHITE);result.setTextIsSelectable(true);root.addView(result);
  testButton.setOnClickListener(v->{try{String reply=RuleStore.match(this,test.getText().toString());result.setText(reply==null?"Nenhuma regra exata encontrada":reply);}catch(Exception e){result.setText("Erro: "+e.getMessage());}});
  status=new TextView(this);status.setTextColor(Color.WHITE);status.setPadding(0,24,0,0);root.addView(status);
  TextView note=new TextView(this);note.setText("Requer acesso às notificações. Respostas dependem do botão Responder oferecido pelo WhatsApp Business. Não é integração oficial.");note.setTextColor(Color.LTGRAY);root.addView(note);
  updateStatus();
 }
 private void styleInput(EditText e){e.setSingleLine(true);e.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF38BDF8));e.setPadding(12,16,12,16);}
 private void styleButton(Button b,int color){b.setTextColor(Color.WHITE);b.setAllCaps(false);b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(color));b.setPadding(8,12,8,12);}
 private void updateStatus(){if(status==null)return;try{status.setText((prefs.getBoolean("enabled",false)?"🟢 Ativado":"⚪ Desativado")+"\nRegras importadas: "+RuleStore.count(this));}catch(Exception e){status.setText("Erro ao ler regras: "+e.getMessage());}}
 @Override protected void onActivityResult(int req,int result,Intent data){
  super.onActivityResult(req,result,data);
  if(req==PICK_CSV&&result==RESULT_OK&&data!=null&&data.getData()!=null){
   try(java.io.InputStream in=getContentResolver().openInputStream(data.getData())){
    int n=RuleStore.importCsv(this,in);Toast.makeText(this,n+" regras importadas",Toast.LENGTH_LONG).show();updateStatus();
   }catch(Exception e){Toast.makeText(this,"Falha na importação: "+e.getMessage(),Toast.LENGTH_LONG).show();}
  }
 }
}