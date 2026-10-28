package br.com.granjasustentavel.adm;
import java.util.*;
final class MassReplies {
 private static final String[] GROUPS={"tropa","galera","família","pessoal","turma","amigos","meus amigos","parceiros","granjeiros","fazendeiros","jogadores","comunidade","time","equipe","povo","minha gente","todos","ADM","criadores","meus parceiros"};
 private static final String[] ENDS={"!","!!","!!!"," 🐔"," 🌱"," 🐣"," 💚"," 😄"," 🤝"," 🙏"," hoje!"," agora!"," por favor!"," tudo certo?"," beleza?"," firmeza?"," alguém online?"," me contem!"," sucesso!"," tamo junto!"," boa sorte!"," um abraço!"," ótima semana!"," bom fim de semana!"," que Deus abençoe!"," tudo de bom!"," bora pra cima!"," podem ajudar?"," alguém responde?"," preciso saber!"," pessoal!"," galera!"," tropa!"," ADM!"," por gentileza!"," aqui na granja?"," no grupo?"," pelo celular?"," alguém sabe?"," vocês são demais!"," valeu mesmo!"," obrigado pela atenção!"," gratidão a todos!"," não entendi direito!"," me explica?"," quero aprender!"," alguém me orienta?"," hoje de manhã?"," hoje à tarde?"," hoje à noite?"," nessa semana?"," nesse mês?"," neste sábado?"," neste domingo?"," na fazenda?"," no aplicativo?"," no WhatsApp?"," sem pressa!"," com carinho!"," de coração!"," pra todo mundo!"," pra nossa comunidade!"," bora conversar!"," alguém por aí?"," me ajudem nessa!"," estou aprendendo!"," é minha primeira vez!"," estou começando!"," estou animado!"," contem comigo!"," mandem notícias!"," compartilhem dicas!"," quem puder responde!"," abraço a todos!"," que venha coisa boa!"," estamos juntos!"," quero participar!"," aqui é só alegria!"," muita paz pra todos!"," boa produção de ovos!"};
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
 private static final String[][] DIRECT={
  {"oi","oii","oiii","olá","ola","olaa","opa","alô","alo","eai","e aí","fala aí","salve","bom dia","boa tarde","boa noite"},
  {"tem alguém aí","tem alguem ai","alguém aí","alguem ai","alguém online","alguem online","tem alguém aqui","tem alguem aqui","cadê todo mundo","cade todo mundo","onde tá todo mundo","onde ta todo mundo","sumiu todo mundo","grupo tá parado","grupo ta parado"},
  {"cheguei","acabei de chegar","voltei","tô de volta","to de volta","cheguei agora","olha quem chegou","entrei no grupo","sou novo aqui","sou nova aqui","boa cheguei"},
  {"vou sair do grupo","to saindo do grupo","tô saindo do grupo","vou deixar o grupo","vou sair daqui","estou saindo do grupo","adeus pessoal","vou embora do grupo","vou me retirar do grupo","saindo do grupo"},
  {"até mais pessoal","fui galera","até amanhã galera","tchau pessoal","boa noite tropa","até logo família","ate logo familia","falou galera"}
 };
 private static final String[] DIRECT_ANSWERS={
  "🐓🤖 ADM: Opa! Seja bem-vindo à comunidade da Granja Sustentável! 🌱 Digite !menu para ver as opções.",
  "🐣 ADM: Tô por aqui! 😄 A galera pode estar cuidando das galinhas. Precisa de ajuda? Digite !menu.",
  "🐔 ADM: Bem-vindo de volta! 💚 Chega mais, a granja é nossa!",
  "🌱 ADM: Sentiremos sua falta! Obrigado por participar. Se quiser voltar um dia, será bem-vindo! 💚",
  "🌙 ADM: Até a próxima! Boa sorte e cuide bem da sua granja! 🐔"
 };
 private static String simple(String s){
  return java.text.Normalizer.normalize(s.toLowerCase(new Locale("pt","BR")),java.text.Normalizer.Form.NFD).replaceAll("\\p{M}+","").trim().replaceAll("[!?.,;:]+$","").replaceAll("\\s+"," ");
 }
 static String direct(String message){
  String m=simple(message);
  for(int i=0;i<DIRECT.length;i++)for(String trigger:DIRECT[i])if(m.equals(simple(trigger)))return DIRECT_ANSWERS[i];
  return null;
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