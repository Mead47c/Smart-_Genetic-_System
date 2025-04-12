package com.example.genessystem.utils;

import javafx.application.Platform;
import javafx.scene.control.Label;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Email {
    private static final String SENDER_EMAIL = "appsmartgeneticsystem@gmail.com";
    private static final String APP_PASSWORD = "nkkh deis ofrr fsvm";
    private static final String SENDER_NAME = "SGS-Smart Genetic System";

    private static ExecutorService executor = Executors.newCachedThreadPool();


    // ==========================================================================================
    // =============================== Sending a text-based Email ===============================
    // ==========================================================================================
    public static int sendOTPEmail(String recipientEmail, String username, Label status, EmailCallback callback) {
        if (executor.isShutdown() || executor.isTerminated()) {
            executor = Executors.newCachedThreadPool();
        }

        int OTP = generateOTP();
        executor.submit(() -> {
            Properties properties = new Properties();
            properties.put("mail.smtp.auth", "true");
            properties.put("mail.smtp.starttls.enable", "true");
            properties.put("mail.smtp.host", "smtp.gmail.com");
            properties.put("mail.smtp.port", "587");
            properties.put("mail.smtp.ssl.protocols", "TLSv1.2");
            properties.put("mail.debug", "true"); // Debugging Enabled

            Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
                }
            });

            // Capture debugging output
            ByteArrayOutputStream debugStream = new ByteArrayOutputStream();
            session.setDebugOut(new PrintStream(debugStream));

            try {
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Your OTP Verification Code");

                String emailContent = String.format(
                        "Dear %s,<br><br>"
                                + "Your OTP verification code is: <b>%06d</b><br><br>"
                                + "This code will expire in 5 minutes.<br><br>"
                                + "This is an automatic email service, don't reply to this email.<br>"
                                + "If you didn't request this code, please ignore this email.<br><br>"
                                + "Best regards,<br>SGS Team",
                        username, OTP
                );

                message.setContent(emailContent, "text/html; charset=utf-8");
                Transport.send(message);

                Platform.runLater(() -> callback.onSuccess("Done!"));
                System.out.println(OTP);
            } catch (MessagingException | UnsupportedEncodingException e) {
                Platform.runLater(() -> callback.onFailure("Failed to send email: " + e.getMessage()));
            } finally {
                shutdownExecutor();
            }
        });

        return OTP;
    }


    public static int generateOTP() {
        return new Random().nextInt(900000) + 100000;
    }


    // ==========================================================================================
    // ============================= Sending Email with Attachments =============================
    // ==========================================================================================
    public static void sendEmailWithAttachment(String recipientEmail, String username, File attachmentFile, EmailCallback callback) {
        if (executor.isShutdown() || executor.isTerminated()) {
            executor = Executors.newCachedThreadPool();
        }

        executor.submit(() -> {
            try {
                Properties properties = new Properties();
                properties.put("mail.smtp.auth", "true");
                properties.put("mail.smtp.starttls.enable", "true");
                properties.put("mail.smtp.host", "smtp.gmail.com");
                properties.put("mail.smtp.port", "587");
                properties.put("mail.smtp.ssl.protocols", "TLSv1.2");

                Session session = Session.getInstance(properties, new Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(SENDER_EMAIL, APP_PASSWORD);
                    }
                });

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(SENDER_EMAIL, SENDER_NAME));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Your Genetic Analysis Report");

                MimeBodyPart messageBodyPart = new MimeBodyPart();
                messageBodyPart.setContent("Dear " + username + ",<br><br>" +
                        "Please find attached your genetic analysis report.<br><br>" +
                        "Regards,<br>SGS Team", "text/html");


                MimeBodyPart attachmentPart = new MimeBodyPart();
                DataSource source = new FileDataSource(attachmentFile);
                attachmentPart.setDataHandler(new DataHandler(source));
                attachmentPart.setFileName(attachmentFile.getName());

                Multipart multipart = new MimeMultipart();
                multipart.addBodyPart(messageBodyPart);
                multipart.addBodyPart(attachmentPart);

                message.setContent(multipart);

                Transport.send(message);

                Platform.runLater(() -> callback.onSuccess("Email sent!"));
            } catch (Exception e) {
                Platform.runLater(() -> callback.onFailure("Failed to send email: " + e.getMessage()));
            }
        });
    }


    // helper methods

    public static void shutdownExecutor() {
        executor.shutdown();
    }

    public interface EmailCallback {
        void onSuccess(String message);

        void onFailure(String error);
    }
}
