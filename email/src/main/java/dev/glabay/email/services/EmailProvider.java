package dev.glabay.email.services;

public interface EmailProvider {

    /**
     * Sends an email using the configured provider.
     *
     * @param recipient recipient email address
     * @param subject email subject
     * @param htmlBody html body
     * @param replyTo optional reply-to email address (nullable)
     * @return provider message id
     */
    String send(String recipient, String subject, String htmlBody, String replyTo);
}
