package com.ramsofttech.adpushlibrary.backend.servlet;

import java.io.IOException;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by Ravi on 5/14/2015.
 */
public class SendMail extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String subject = req.getParameter("subject");
        String description = req.getParameter("description");
        String email = req.getParameter("email");
        Properties props = new Properties();
        Session session = Session.getDefaultInstance(props, null);

        String msgBody = "\n" + description;

        try {
            Message msg = new MimeMessage(session);
            msg.setFrom(new InternetAddress("ravindra.dobariya@gmail.com",
                    "Backgrounds World"));
            msg.addRecipient(Message.RecipientType.TO, new InternetAddress(
                    email, "Your name"));
            msg.setSubject(subject);
            msg.setText(msgBody);
            Transport.send(msg);

        } catch (Exception e) {
            resp.setContentType("text/plain");
            resp.getWriter().println("Something went wrong. Please try again.");
            throw new RuntimeException(e);
        }

        resp.setContentType("text/plain");
        resp.getWriter().println(
                "Thank you for installing app. An Email has been send out.");
    }
}
