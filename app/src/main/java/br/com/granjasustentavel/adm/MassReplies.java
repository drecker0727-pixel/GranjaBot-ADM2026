package br.com.granjasustentavel.adm;
import java.util.*;
final class MassReplies {
 private static final String[] GROUPS={"tropa","galera","família","pessoal","turma","amigos","meus amigos","parceiros","granjeiros","fazendeiros","jogadores","comunidade","time","equipe","povo","minha gente","todos","ADM","criadores","meus parceiros"};
 private static final String[] ENDS={"!","!!","!!!"," 🐔"," 🌱"," 🐣"," 💚"," 😄"," 🤝"," 🙏"," hoje!"," agora!"," por favor!"," tudo certo?"," beleza?"," firmeza?"," alguém online?"," me contem!"," sucesso!"," tamo junto!"," boa sorte!"," um abraço!"," ótima semana!"," bom fim de semana!"," que Deus abençoe!"," tudo de bom!"," bora pra cima!"," podem ajudar?"," alguém responde?"," preciso saber!"," pessoal!"," galera!"," tropa!"," ADM!"," por gentileza!"," aqui na granja?"," no grupo?"," pelo celular?"," alguém sabe?"," vocês são demais!"," valeu mesmo!"," obrigado pela atenção!"," gratidão a todos!"," não entendi direito!"," me explica?"," quero aprender!"," alguém me orienta?"," hoje de manhã?"," hoje à tarde?"," hoje à noite?"};
 private static final String[][] STARTS={
 {"bom dia","boa tarde","boa noite","boa madrugada","salve","fala","opa","e aí","olá","saudações"},
 {"obrigado","obrigada","valeu","gratidão","agradeço","muito obrigado","muito obrigada","tamo junto","tmj","valeu demais"},
 {"até mais","até amanhã","até logo","tchau","fui","vou descansar","boa noite","falou","até depois","bom descanso"},
 {"como faço para","alguém sabe como","ADM como faço para","me ajuda a","preciso aprender a","como posso","onde consigo","quero saber como","podem me orientar a","alguém explica como"},
 {"quem aí já","alguém aqui já","galera vocês já","tropa vocês já","pessoal vocês já","ADM vocês já","família vocês já","granjeiros vocês já","comunidade vocês já","jogadores vocês já"}};
 private static final String[][] OBJECTS={
 GROUPS,GROUPS,GROUPS,
 {"comprar ração","alimentar minhas galinhas","vender meus ovos","incubar um ovo","ver o saldo","pedir saque","comprar uma galinha","ver minha incubadora","abrir a loja","entrar na granja","acompanhar os ciclos","consultar meus ovos","ver o estoque","usar o pix","acessar minha conta","ver minhas galinhas","consultar os preços","atualizar a fazenda","resolver erro no login","chamar o suporte"},
 {"alimentaram as galinhas","coletaram os ovos","viram a atualização","entraram na granja","abriram a loja","usaram a incubadora","compraram ração","viram os pintinhos","conheceram o jogo","viram o galo","testaram o aplicativo","consultaram o saldo","viram os preços","acompanharam o ciclo","pediram ajuda","jogaram hoje","chamaram o ADM","leram as regras","viram o estoque","aprenderam a jogar"}};
 private static final String[] ANSWERS={
 "🐓🤖 ADM: Salve, comunidade! Um ótimo dia para todos! 🌱",
 "💚 ADM: Tamo junto! Obrigado pela participação de vocês! 🐔",
 "🌙 ADM: Até a próxima! Cuidem-se e bom descanso! 🐣",
 "🌾 ADM: Posso ajudar! Abra a granja e diga em qual etapa precisa de orientação. Para comandos, digite !menu.",
 "🐔 ADM: Boa conversa, pessoal! Compartilhem dicas com respeito e ajudem os novos jogadores. 🌱"};
 static void populate(java.util.function.BiConsumer<String,String> add){
  for(int c=0;c<STARTS.length;c++)for(String a:STARTS[c])for(String b:OBJECTS[c])for(String end:ENDS){
   add.accept(a+" "+b+end,ANSWERS[c]);
  }
 }
 static String answer(String message){
  String m=message.toLowerCase(new Locale("pt","BR")).trim().replaceAll("[!?.,;:]+$","");
  for(int c=0;c<STARTS.length;c++)for(String a:STARTS[c])for(String b:OBJECTS[c]){
   String prefix=a+" "+b;
   if(!m.startsWith(prefix))continue;
   String suffix=m.substring(prefix.length());
   for(String end:ENDS){
    String normalized=end.toLowerCase(new Locale("pt","BR")).replaceAll("[!?.,;:]+$","");
    if(suffix.equals(normalized))return ANSWERS[c];
   }
  }
  return null;
 }
}