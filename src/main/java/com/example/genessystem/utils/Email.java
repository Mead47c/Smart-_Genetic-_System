package com.example.genessystem.utils;

import javafx.application.Platform;
import javafx.scene.control.Label;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Email {
    private static final String SENDER_EMAIL = "studentsgrading.program@gmail.com";
    private static final String APP_PASSWORD = "jfqt kbox kugh hilf";

    private static ExecutorService executor = Executors.newCachedThreadPool();

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
                message.setFrom(new InternetAddress(SENDER_EMAIL, "Genes System"));
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
                message.setSubject("Your OTP Verification Code");

                String emailContent = String.format(
                        "Dear %s,<br><br>"
                                + "Your OTP verification code is: <b>%06d</b><br><br>"
                                + "This code will expire in 5 minutes.<br><br>"
                                + "This is an automatic email service, don't reply to this email.<br>"
                                + "If you didn't request this code, please ignore this email.<br><br>"
                                + "Best regards,<br>Genes System Team",
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

    public static void shutdownExecutor() {
        executor.shutdown();
    }

    public interface EmailCallback {
        void onSuccess(String message);

        void onFailure(String error);
    }
}
