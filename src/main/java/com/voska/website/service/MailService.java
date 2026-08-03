package com.voska.website.service;

import com.voska.website.config.MailProperties;
import com.voska.website.dto.request.ContactRequest;
import com.voska.website.exception.MailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class MailService {

    private static final String MAIL_SUBJECT_PREFIX = "[voska.dev] New contact message: ";
    private static final String MAIL_SEND_ERROR = "Contact message could not be sent";

    private final JavaMailSender mailSender;
    private final MailProperties mailProperties;

    public void sendContactMail(ContactRequest request) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    false,
                    StandardCharsets.UTF_8.name()
            );

            helper.setFrom(mailProperties.from());
            helper.setTo(mailProperties.to());
            helper.setReplyTo(request.email());
            helper.setSubject(MAIL_SUBJECT_PREFIX + sanitizeHeader(request.subject()));
            helper.setText(buildHtmlContent(request), true);

            mailSender.send(mimeMessage);
        } catch (MessagingException | MailException exception) {
            throw new MailSendException(MAIL_SEND_ERROR, exception);
        }
    }

    private String buildHtmlContent(ContactRequest request) {
        String name = escape(request.name());
        String email = escape(request.email());
        String subject = escape(request.subject());
        String message = escape(request.message()).replace("\n", "<br>");

        return """
                <!doctype html>
                <html lang="tr">
                <body style="margin:0;padding:0;background-color:#0b0c0b;color:#f2f0eb;font-family:Arial,sans-serif">
                  <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="background-color:#0b0c0b">
                    <tr>
                      <td align="center" style="padding:40px 20px">
                        <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0" style="max-width:640px">
                          <tr>
                            <td style="border-left:4px solid #a9853b;padding:0 0 0 20px">
                              <div style="color:#c7a253;font-size:12px;letter-spacing:2px">VOSKA.DEV</div>
                              <h1 style="margin:8px 0 0;color:#f2f0eb;font-size:28px">Yeni ileti&#351;im formu mesaj&#305;</h1>
                            </td>
                          </tr>
                          <tr><td height="30" style="font-size:0;line-height:0">&nbsp;</td></tr>
                          <tr>
                            <td style="background-color:#171816;border:1px solid #2b2c28;padding:24px">
                              <table role="presentation" width="100%%" cellspacing="0" cellpadding="0" border="0">
                                <tr><td style="padding:0 0 18px;color:#c7a253;font-size:12px;font-weight:bold">&#304;sim</td></tr>
                                <tr><td style="padding:0 0 22px;color:#f2f0eb;font-size:15px">%s</td></tr>
                                <tr><td style="padding:0 0 18px;color:#c7a253;font-size:12px;font-weight:bold">E-posta</td></tr>
                                <tr><td style="padding:0 0 22px"><a href="mailto:%s" style="color:#f2f0eb;font-size:15px">%s</a></td></tr>
                                <tr><td style="padding:0 0 18px;color:#c7a253;font-size:12px;font-weight:bold">Konu</td></tr>
                                <tr><td style="padding:0 0 22px;color:#f2f0eb;font-size:15px">%s</td></tr>
                                <tr><td style="border-top:1px solid #343530;padding:22px 0 12px;color:#c7a253;font-size:12px;font-weight:bold">Mesaj</td></tr>
                                <tr><td style="color:#f2f0eb;font-size:15px;line-height:1.7">%s</td></tr>
                              </table>
                            </td>
                          </tr>
                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(name, email, email, subject, message);
    }

    private String escape(String value) {
        return HtmlUtils.htmlEscape(value.strip());
    }

    private String sanitizeHeader(String value) {
        return value.replace("\r", " ").replace("\n", " ").strip();
    }
}
