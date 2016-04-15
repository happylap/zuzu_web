package com.lap.zuzuweb.util.mail;

import java.util.List;
import java.util.ArrayList;
import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

public class Mail 
{
	public String subject;
	public String body;
	public String contentType; // "text/html", "text/plain"
	public List<Receipient> recipientList = new ArrayList<Receipient>();
		
	public List<Receipient> getRecipientList()
	{
		return recipientList;
	}

	public void addMailTo(String email) throws AddressException
	{
		this.recipientList.add(new Receipient(Message.RecipientType.TO, new InternetAddress(email)));	
	}

	public void addMailCC(String email) throws AddressException
	{
		this.recipientList.add(new Receipient(Message.RecipientType.CC, new InternetAddress(email)));
	}
	
	public void addMailBCC(String email) throws AddressException
	{
		this.recipientList.add(new Receipient(Message.RecipientType.BCC, new InternetAddress(email)));
	}
	
}
