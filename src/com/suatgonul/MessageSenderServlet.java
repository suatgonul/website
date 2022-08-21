package com.suatgonul;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.util.logging.Logger;

import javax.mail.Message;
import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MessageSenderServlet extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String response;
		resp.setContentType("text/plain");
		try {
			String senderName = req.getParameter("contactName");
			String senderEmail = req.getParameter("contactEmail");
			String subject = req.getParameter("contactSubject");
			String message= req.getParameter("contactMessage");
			response = sendMail(senderName, senderEmail, subject, message);
			resp.setStatus(HttpServletResponse.SC_OK);
		} catch (Exception e) {
			logger.severe("Unexpected errors while handling the post request. Exception message:" + e.getMessage());
			response = FAILED_TO_SEND_MESSAGE;
		}
		resp.getWriter().print(response);
	}
	
	private String sendMail(String senderName, String senderEmail, String subject, String message) {
		InternetAddress senderAddress;
		InternetAddress myAddress;
		try {
			myAddress = new InternetAddress("suatgonul@gmail.com", "Message from suatgonul.com");
		} catch (UnsupportedEncodingException e) {
			logger.severe("Failed to initialize my address. Exception message: " + e.getMessage());
			return FAILED_TO_SEND_MESSAGE;
			
		}
		try {
			senderAddress = new InternetAddress(senderEmail, senderName);
		} catch (UnsupportedEncodingException e) {
			logger.severe("Failed to initialize sender address. Exception message: " + e.getMessage());
			return INVALID_NAME_OR_EMAIL;
		}
		
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		Message msg = new MimeMessage(session);
		InternetAddress[] replyToList = new InternetAddress[]{senderAddress};

		try {
		    msg.setFrom(myAddress);
		    msg.setRecipient(RecipientType.TO, myAddress);
		    msg.setReplyTo(replyToList);
		    msg.setSubject(subject);
		    msg.setText(message);
		    Transport.send(msg);
		    logger.info("Mail sent from: " + senderAddress.getAddress());
		} catch (MessagingException e) {
			logger.severe("Sender address: " + myAddress.getAddress());
		    logger.severe("Failed to send mail. Exception message: " + e.getMessage());
		    return FAILED_TO_SEND_MESSAGE;
		}
		return MESSAGE_SENT_SUCCESSFULLY;
	}
	
	private static final long serialVersionUID = -8352448074619429765L;
	private static final Logger logger = Logger.getLogger(MessageSenderServlet.class.getName());
	private static final String FAILED_TO_SEND_MESSAGE = "Failed to send message. Check <a class=\"smoothscroll\" href=\"#about\">about</a> section above for my email.";
	private static final String MESSAGE_SENT_SUCCESSFULLY = "Message sent successfully.";
	private static final String INVALID_NAME_OR_EMAIL = "Something is wrong with your name or email address.";
}
