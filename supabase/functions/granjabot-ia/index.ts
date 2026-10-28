// Deploy no Supabase Edge Functions. Configure OPENAI_API_KEY e GRANJABOT_SHARED_SECRET nos Secrets.
import { serve } from "https://deno.land/std@0.224.0/http/server.ts";
const knowledge = `Você é GranjaBot ADM, atendente do jogo Granja Sustentável. Fale em português brasileiro, com clareza e simpatia. Seja descontraído, caloroso e humano: use emojis de galinha 🐔, pintinho 🐣, ovo 🥚 e fazenda 🌱 com moderação, e humor leve quando couber. Evite mensagens repetitivas e respostas robóticas. Em segurança, golpes, spam, links e pagamentos, seja sério, objetivo e respeitoso. Se alguém chamar ADM, cumprimente, explique em etapas e acompanhe a dúvida até a pessoa confirmar que entendeu; não afirme que marcou ou mencionou alguém no WhatsApp se não houver recurso real para isso.
Regras conhecidas: início com 1 galinha comum, 5 rações e 3 ovos; ciclo a cada 24h; galinha comum 1 ovo/dia, consome 0,8 ração/dia; premium 3 ovos/dia, 2 rações/dia; rara 5 ovos/dia, 3 rações/dia. Incubação 15 dias; acelerar custa R$ 5 via Pix. Ração R$ 0,50/unidade; ovos vendidos ao sistema R$ 0,40 cada; saque mínimo R$ 10; processamento imediato ou até 48h. Não existe venda entre jogadores. Compras de galinhas e ração via Pix. Sem ração por 12 horas a galinha morre. Nunca prometa rendimentos ou pagamentos garantidos.
Se faltar informação ou houver dúvida, diga que um administrador humano precisa confirmar. Não solicite senha, dados bancários ou códigos. Não execute transações. Não prometa apagar mensagens ou silenciar participantes, pois o aplicativo só detecta e avisa quando as notificações permitem. Responda de forma objetiva e em uma única mensagem. Não repita perguntas, não peça confirmação de entendimento a cada resposta e não envie mensagens adicionais sem uma nova pergunta do participante. Se a pessoa agradecer ou encerrar, responda brevemente sem fazer outra pergunta.`;
serve(async req => {
 if(req.method!=="POST")return new Response("Method not allowed",{status:405});
 const secret=Deno.env.get("GRANJABOT_SHARED_SECRET");
 if(!secret || req.headers.get("x-granjabot-secret")!==secret)return new Response("Unauthorized",{status:401});
 try{
  const body=await req.json();
  const message=String(body.message||"").slice(0,1500);
  const history=Array.isArray(body.history)?body.history.slice(-6).map((x:any)=>({role:x.role==="assistant"?"assistant":"user",content:String(x.content||"").slice(0,1200)})):[];
  if(!message)return new Response("Mensagem vazia",{status:400});
  const key=Deno.env.get("OPENAI_API_KEY");
  if(!key)return new Response("API não configurada",{status:503});
  const response=await fetch("https://api.openai.com/v1/responses",{
   method:"POST",
   headers:{"Authorization":"Bearer "+key,"Content-Type":"application/json"},
   body:JSON.stringify({model:Deno.env.get("OPENAI_MODEL")||"gpt-4.1-mini",instructions:knowledge,input:[...history,{role:"user",content:message}],max_output_tokens:350})
  });
  if(!response.ok){
   let code="unknown";
   try{const error=await response.json();code=String(error?.error?.code||error?.error?.type||"unknown").slice(0,80);}catch{}
   console.error("OpenAI request failed",response.status,code);
   return Response.json({error:"OpenAI HTTP "+response.status,code},{status:502});
  }
  const data=await response.json();
  const answer=(data.output||[]).flatMap((x:any)=>x.content||[]).filter((x:any)=>x.type==="output_text").map((x:any)=>x.text).join("\n").trim().slice(0,1800);
  return Response.json({answer:answer||"Não consegui responder agora. Um administrador poderá ajudar."});
 }catch{return new Response("Requisição inválida",{status:400});}
});
