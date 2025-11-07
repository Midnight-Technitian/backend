package dev.glabay.email.services;

import dev.glabay.logging.MidnightLogger;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class SmtpEmailProvider implements EmailProvider {
    private final Logger logger = MidnightLogger.getLogger(SmtpEmailProvider.class);

    private final JavaMailSender mailSender;
    private final String fromAddress;
    private final String fromName;

    public SmtpEmailProvider(
        JavaMailSender mailSender,
        @Value("${email.fromAddress}") String fromAddress,
        @Value("${email.fromName}") String fromName
    ) {
        this.mailSender = mailSender;
        this.fromAddress = fromAddress;
        this.fromName = fromName;
    }

    @Override
    public String send(String recipient, String subject, String htmlBody, String replyTo) {
        try {
            var message = mailSender.createMimeMessage();
            var helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());

            // DEBUG: Log all email addresses
            logger.debug("╔════════════════════════════════════════╗");
            logger.debug("║      EMAIL SEND ATTEMPT DEBUG          ║");
            logger.debug("╠════════════════════════════════════════╝");
            logger.debug("║ From Address: {}", fromAddress);
            logger.debug("║ From Name:    {}", fromName);
            logger.debug("║ To:           {}", recipient);
            logger.debug("║ Reply-To:     {}", replyTo != null ? replyTo : "NULL");
            logger.debug("║ Subject:      {}", subject);
            logger.debug("╚═════════════════════════════════════════");

            // From (with friendly name if provided)
            if (fromName != null && !fromName.isBlank())
                helper.setFrom(new InternetAddress(fromAddress, fromName));
            else
                helper.setFrom(fromAddress);

            helper.setTo(recipient);
            helper.setSubject(subject);
            helper.setText(htmlBody, true); // HTML

            if (replyTo != null && !replyTo.isBlank()) {
                logger.error("⚠️  WARNING: Setting Reply-To to: {}", replyTo);
                logger.error("⚠️  This address MUST be verified in AWS SES!");
                helper.setReplyTo(replyTo);
            }

            // Ensure a Message-ID is present before sending
            message.saveChanges();
            var messageIdBefore = message.getMessageID();

            logger.info("📤 Attempting to send via AWS SES...");
            mailSender.send(message);
            logger.info("✅ Email sent successfully!");

            // Try to return the message id (may be null depending on transport)
            var messageIdAfter = message.getMessageID();
            return messageIdAfter != null ? messageIdAfter : messageIdBefore != null ? messageIdBefore : "smtp";
        }
        catch (Exception ex) {
            logger.error("❌ SMTP SEND FAILED!");
            logger.error("Error: {}", ex.getMessage());
            throw new RuntimeException("SMTP send failed", ex);
        }
    }
}
