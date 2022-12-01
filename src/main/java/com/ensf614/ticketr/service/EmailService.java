package com.ensf614.ticketr.service;

import java.util.ArrayList;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import com.ensf614.ticketr.model.Receipt;
import com.ensf614.ticketr.model.Ticket;

@Component
public class EmailService {

    @Autowired
    private Environment env;

    public void sendEmail(String to, String subject, String body) {
        String username = env.getProperty("spring.mail.username");
        String password = env.getProperty("spring.mail.password");
        String host = env.getProperty("spring.mail.host");
        int port = Integer.parseInt(env.getProperty("spring.mail.port"));

        Properties properties = new Properties();
        properties.setProperty("mail.smtp.host", host);
        properties.setProperty("mail.smtp.port", String.valueOf(port));
        properties.setProperty("mail.smtp.auth", "true"); // enable auth
        properties.setProperty("mail.smtp.starttls.enable", "true"); // enable TLS

        // get session instace baed on the settings defined above
        Session session = Session.getInstance(properties);

        // `prepareMessage` implementation is omitted, construct your own message here
        MimeMessage mimeMessage = prepareMessage(session, username, to, subject, body);

        // your credentials

        // get the transport instance from the freshly created session
        // pass the valid protocol name, here the SMTP is used
        Transport transport;
        try {
            transport = session.getTransport("smtp");
            transport.connect(host, port, username, password);

            // send the message
            transport.sendMessage(mimeMessage, mimeMessage.getAllRecipients());
        } catch (NoSuchProviderException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // connect to the transport instance with your credentials

    }

    private MimeMessage prepareMessage(Session session, String username, String to, String subject, String body) {
        MimeMessage mimeMessage = new MimeMessage(session);

        try {
            mimeMessage.setFrom(username);
            mimeMessage.setRecipient(javax.mail.Message.RecipientType.TO, new javax.mail.internet.InternetAddress(to));
            mimeMessage.setSubject(subject);
            mimeMessage.setContent(body, "text/html");

        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();

        }

        return mimeMessage;

    }

    public void sendHtmlReceipt(Receipt data) {
        String to = data.getUserEmail();
        String subject = "Ticketr Receipt";
        String body = "<h1>Thank you for using Ticketr!</h1><br><br><h2>Receipt</h2><br><br><h3>Movie: "
                + data.getMovieName() + "</h3><br><h3>Theatre: " + data.getTheatreName() + "</h3><br><h3>Date: "
                + data.getDate() + "</h3><br><h3>Time: " + data.getTime() + "</h3>";
        body += "<br><br><h3>Tickets</h3><br><br>";
        for (Ticket t : data.getTickets()) {
            body += "<h3>Seat: " + t.getSeat().getSeat_number() + "</h3><br><h3>Price: $" + t.getPrice()
                    + "</h3><br><h3>Ticket NO: " + t.getId() + "</h3><br><br>";
        }
        body += "<br><br><h3>Total: $" + data.getPrice() + "</h3>";
        body += "<br><br><h3>Receipt NO: " + data.getId() + "</h3>";
        body += "<br><br><h3>Enjoy the movie!</h3>";
        sendEmail(to, subject, body);

    }
}