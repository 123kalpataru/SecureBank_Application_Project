package com.kalp.SecureBankApp;

import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;

public class EmailService {
	
  
	
	
    private static final String FROM_EMAIL = "kalpatarumallick226@gmail.com";  // replace with your Gmail
    private static final String APP_PASSWORD = "iivo jgei hpns vbgo";  // replace with 16-digit App Password

    private static final ExecutorService executor = Executors.newFixedThreadPool(5); 
    private static Session createSession()
    {
    	Properties props = new Properties(); 
    	props.put("mail.smtp.host", "smtp.gmail.com"); 
    	props.put("mail.smtp.port", "587"); 
    	props.put("mail.smtp.auth", "true"); 
    	props.put("mail.smtp.starttls.enable", "true"); 
    	return Session.getInstance(props, new Authenticator() 
    	{ 
    		protected PasswordAuthentication getPasswordAuthentication() 
    		{ 
    			return new PasswordAuthentication(FROM_EMAIL, APP_PASSWORD);
    			} }); } // ✅ Async email sending for faster registration 
    
    public static void sendEmailAsync(String to, String subject, String body) 
    { 
    	executor.submit(() ->
    	{ 
    		boolean result = sendEmail(to, subject, body); 
    		if (!result)
    		{ 
    			System.err.println("Failed to send email to " + to); } }); } // ✅ Core synchronous method (used internally) 
    private static boolean sendEmail(String to, String subject, String body)
    {
    	try
    	{ 
    		if (to == null || to.isEmpty())
    		{ 
    			System.err.println("ERROR: Recipient email is null or empty"); 
    			return false;
    			} 
    		Session session = createSession();
    		Message message = new MimeMessage(session); 
    		message.setFrom(new InternetAddress(FROM_EMAIL)); 
    		message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to)); 
    		message.setSubject(subject); message.setText(body); 
    		Transport.send(message); 
    		return true; 
    		} catch (MessagingException e) 
    	{ System.err.println("Email sending failed: " + e.getMessage()); return false; } } // ✅ Shutdown thread pool gracefully (call on app shutdown) 
    public static void shutdown() 
    { 
    	executor.shutdown();
    	}
    }
    
