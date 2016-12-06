package com.bridge.email;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class EmailNotification {

	public EmailNotification() {
	}
	
	public static boolean send(InternetAddress[] recipients, String header, String messageBody){
		// pw = 5<zpS>ED|fmvX[3S email: cuaidsquaqamole@yahoo.com
		// Sender's email ID needs to be mentioned
	     String from = "cuaidsquaqamole@yahoo.com";
	     String pass ="5<zpS>ED|fmvX[3S";
	    // Recipient's email ID needs to be mentioned.
//	   String to = "rkcb0306@countermail.com";
	   
	   String host = "smtp.mail.yahoo.com";

	   // Get system properties
	   Properties properties = System.getProperties();
	   // Setup mail server
	   properties.put("mail.smtp.starttls.enable", "true");
	   properties.put("mail.smtp.host", host);
	   properties.put("mail.smtp.user", from);
	   properties.put("mail.smtp.password", pass);
	   properties.put("mail.smtp.port", "587");
	   properties.put("mail.smtp.auth", "true");

	   // Get the default Session object.
	   Session session = Session.getDefaultInstance(properties);
	   boolean sendingOk = true;
	   try{
	      // Create a default MimeMessage object.
	      MimeMessage message = new MimeMessage(session);

	      // Set From: header field of the header.
	      message.setFrom(new InternetAddress(from));

	      // Set To: header field of the header.
	      message.addRecipients(Message.RecipientType.TO,
	                               recipients);

	      // Set Subject: header field
	      message.setSubject(header);

	      // Now set the actual message
	      message.setText(messageBody);

	      // Send message
	      Transport transport = session.getTransport("smtp");
	      transport.connect(host, from, pass);
	      transport.sendMessage(message, message.getAllRecipients());
	      transport.close();
	      for (InternetAddress i : recipients) {
	    	  System.out.println("Sent message successfully to "+i.toString());
	      }
	      
	   }catch (MessagingException mex) {
	      mex.printStackTrace();
	      sendingOk = false;
	   }
	   
	   return sendingOk;
	}
	
	
	

}
