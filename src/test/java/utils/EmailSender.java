package utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.DataHandler;
import jakarta.activation.FileDataSource;

import java.io.File;
import java.util.Properties;

public class EmailSender {

    public static void sendReport(File attachment) {

        String username = ConfigReader.get("report.email.username");
        String password = ConfigReader.get("report.email.password");
        String toEmail  = ConfigReader.get("report.email.to");

        if (username == null || password == null || toEmail == null) {
            throw new RuntimeException("❌ Missing email configuration in config.properties");
        }

        Properties props = new Properties();

        // ✅ Standard TLS SMTP Configuration
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");

        props.put("mail.smtp.host", ConfigReader.get("report.email.smtp.host"));
        props.put("mail.smtp.port", ConfigReader.get("report.email.smtp.port"));

        // ✅ Create Session with Auth
        Session session = Session.getInstance(
                props,
                new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(username, password);
                    }
                }
        );

        try {

            // ✅ MUST use MimeMessage (fixes your IDE error)
            MimeMessage message = new MimeMessage(session);

            message.setFrom(new InternetAddress(username));

            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(toEmail)
            );

            message.setSubject("Daily Project Execution Report");

            // ✅ Email Body
            MimeBodyPart textPart = new MimeBodyPart();
            textPart.setText("Attached is today's project execution report.");

            Multipart multipart = new MimeMultipart();
            multipart.addBodyPart(textPart);

            // ✅ Optional Attachment
            if (attachment != null && attachment.exists()) {

                MimeBodyPart filePart = new MimeBodyPart();
                FileDataSource source = new FileDataSource(attachment);

                filePart.setDataHandler(new DataHandler(source));
                filePart.setFileName(attachment.getName());

                multipart.addBodyPart(filePart);

                System.out.println("📎 Attaching file → " + attachment.getAbsolutePath());
            }

            message.setContent(multipart);

            // ✅ Send Email
            Transport.send(message);

            System.out.println("✅ Email sent successfully → " + toEmail);

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "❌ Email sending failed → " + e.getMessage(),
                    e
            );
        }
    }
}
