package com.lap.zuzuweb.util.mail;

import java.util.List;
import java.util.Properties;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lap.zuzuweb.Secrets;
import com.lap.zuzuweb.util.CommonUtils;

public class MailSender 
{
	private static final Logger logger = LoggerFactory.getLogger(MailSender.class);
		
	private static Properties mailServerProperties;
	private static Session getMailSession;
	private static MimeMessage generateMailMessage;
 
	public static void main(String args[]) throws AddressException, MessagingException 
	{
		
		String subjectTemplate = "豬豬快租-發送驗證碼 %s";
		String bodyTemplate = "*** 此為系統自動發送，請勿直接回覆 ***<br/><br/><h2>驗證碼: %s</h2><br/>提醒您，此驗證碼將於30分鐘後失效。";
		Mail mail = new Mail();
		mail.subject = String.format(subjectTemplate, CommonUtils.getUTCStringFromDate(CommonUtils.getUTCNow()));
		mail.body = String.format(bodyTemplate, "1234");
		mail.contentType = "text/html;charset=UTF-8";
		
		mail.addMailTo("test@gmail.com");
		sendEmail(mail);
		
		System.out.println("\n\n ===> Your Java Program has just sent an Email successfully. Check your email..");
	}
 	
	public static void sendEmail(Mail mail) throws MessagingException {
		
		// Step1
		mailServerProperties = System.getProperties();
		mailServerProperties.put("mail.smtp.port", "587");
		mailServerProperties.put("mail.smtp.auth", "true");
		mailServerProperties.put("mail.smtp.starttls.enable", "true");
 
		// Step2
		getMailSession = Session.getDefaultInstance(mailServerProperties, null);
		generateMailMessage = new MimeMessage(getMailSession);
		
		generateMailMessage.setSubject(mail.subject);
		logger.info("mail subject: " + mail.subject);
		//generateMailMessage.setContent(mail.body, mail.contentType);
		generateMailMessage.setContent(mail.body, mail.contentType);
		logger.info("mail body: " + mail.body);
		logger.info("mail contentType: " + mail.contentType);
		List<Receipient>receipients = mail.getRecipientList();
		
		for(Receipient r: receipients)
		{
			if (r.type != null && r.address != null)
			{
				logger.info("receipientr: " + r);
				generateMailMessage.addRecipient(r.type, r.address);
			}	
		}

		// Enter your correct gmail UserID and Password
		// if you have 2FA enabled then provide App Specific Password
		Transport transport = getMailSession.getTransport("smtp");
		transport.connect("smtp.gmail.com", Secrets.ZUZU_EMAIL, Secrets.ZUZU_PWD);
		logger.info("ZUZU_EMAIL: " + Secrets.ZUZU_EMAIL);
		transport.sendMessage(generateMailMessage, generateMailMessage.getAllRecipients());
		transport.close();
	}
}
