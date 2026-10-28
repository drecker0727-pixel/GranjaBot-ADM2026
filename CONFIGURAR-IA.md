# Configuração da IA do GranjaBot ADM

## Arquitetura
WhatsApp Business (notificações Android) → GranjaBot ADM (regras CSV primeiro) → HTTPS Supabase Edge Function granjabot-ia → OpenAI Responses API. A chave OPENAI_API_KEY fica somente nos Secrets do Supabase.

## Ativação pelo celular
1. No Supabase, crie a Edge Function chamada `granjabot-ia` usando `supabase/functions/granjabot-ia/index.ts`.
2. Em Edge Functions → Secrets, configure `OPENAI_API_KEY` com a chave da API OpenAI e `GRANJABOT_SHARED_SECRET` com uma senha longa aleatória de 32+ caracteres. Nunca compartilhe essas chaves no GitHub ou em mensagens.
3. A função usa autenticação por segredo próprio; se o Supabase exigir JWT, a chamada do app sem JWT receberá 401. Configure a verificação de JWT da função de forma compatível com o seu projeto e proteja a função com controles adicionais antes de produção.
4. Implante a função. No GranjaBot ADM, informe a URL HTTPS da função e o segredo compartilhado, salve e ative IA.
5. Ative o robô, autorize notificações do WhatsApp Business e teste com outra pessoa no grupo. Antes de habilitar no grupo real, teste em grupo separado.

## Segurança e limites
- A chave da OpenAI NÃO é distribuída no APK. Porém o segredo compartilhado preenchido no app pode ser extraído de um aparelho comprometido ou de backup local. Para produção, substitua-o por autenticação por dispositivo/usuário, revogação e rotação de credenciais.
- Configure limites de chamadas por usuário, por minuto e orçamento no backend antes de liberar a todos. A versão inicial não possui rate limiting robusto no servidor.
- Notificações Android não garantem identificação confiável de cada participante, nem acesso a todas as mensagens; conversas podem se misturar quando o remetente não é identificado. Não há menções @ reais garantidas, nem remoção/silenciamento de membros.
- Atendimento ativo expira após 20 minutos sem mensagens; o usuário pode encerrar dizendo 'entendi' ou 'resolvido'. O reconhecimento é heurístico.
- O aplicativo usa somente as notificações recebidas; WhatsApp pode mudar o formato ou não oferecer botão Responder.
- Mensagens enviadas à IA podem conter dados pessoais. Informe aos membros, evite informações sensíveis e adote política de privacidade e retenção adequada.
- A IA não realiza saques, compras ou pagamentos. Ela deve encaminhar casos financeiros específicos para um administrador humano.
- IA requer conexão e tem cobrança separada da assinatura ChatGPT. Consulte https://platform.openai.com/pricing e configure limites de gastos.
- A implementação é uma base de testes, não uma integração oficial do WhatsApp e não deve ser tratada como atendimento 24h garantido.
