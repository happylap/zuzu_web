package com.lap.zuzuweb.util.mail;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;

class Receipient
{
	public Message.RecipientType type;
	public InternetAddress address;
	
	Receipient(Message.RecipientType type, InternetAddress address)
	{
		this.type = type;
		this.address = address;
	}
	
	@Override
	public String toString()
	{
		if (type != null && address != null)
		{
			return type + " " + address.getAddress();
		}
		
		return "";
	}
}
