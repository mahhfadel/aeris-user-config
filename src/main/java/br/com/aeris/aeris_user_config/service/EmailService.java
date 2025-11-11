package br.com.aeris.aeris_user_config.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Async
    public void enviarEmailHtml(String destinatario, String assunto,
                                String template, Map<String, Object> variaveis) {
        log.info("[EmailService.enviarEmailHtml] Iniciando envio de email HTML para: {} | Assunto: {} | Template: {}",
                destinatario, assunto, template);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(destinatario);
            helper.setSubject(assunto);

            Context context = new Context();
            context.setVariables(variaveis);

            String conteudoHtml = templateEngine.process(template, context);
            helper.setText(conteudoHtml, true);

            mailSender.send(message);
            log.info("[EmailService.enviarEmailHtml] Email enviado com sucesso para: {}", destinatario);

        } catch (MessagingException e) {
            log.error("[EmailService.enviarEmailHtml] Erro ao enviar email para: {} | Assunto: {}",
                    destinatario, assunto, e);
        } catch (Exception e) {
            log.error("[EmailService.enviarEmailHtml] Erro inesperado ao enviar email", e);
        }
    }

    @Async
    public void enviarEmailBoasVindas(String destinatario, String empresa, String nome) {
        log.info("[EmailService.enviarEmailBoasVindas] Preparando envio de email de boas-vindas para: {} | Empresa: {} | Nome: {}", destinatario, empresa, nome);

        Map<String, Object> variaveis = Map.of(
                "empresa", empresa,
                "nome", nome,
                "anoAtual", java.time.Year.now().getValue()
        );

        log.debug("[EmailService.enviarEmailBoasVindas] Variáveis do template: {}", variaveis);

        enviarEmailHtml(destinatario, "Bem-vindo à Aeris!", "email-boas-vindas", variaveis);

        log.info("[EmailService.enviarEmailBoasVindas] Email de boas-vindas enviado para: {}", destinatario);
    }

}
