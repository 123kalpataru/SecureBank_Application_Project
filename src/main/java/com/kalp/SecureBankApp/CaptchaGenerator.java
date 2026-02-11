package com.kalp.SecureBankApp;

import java.util.Random;

public class CaptchaGenerator
{
public static String generatorCaptchaText(int length)
{
	String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
	StringBuilder sb = new StringBuilder();
	Random random = new Random();
	
	for(int i=0;i<length;i++)
	{
		sb.append(chars.charAt(random.nextInt(chars.length())));
	}
	
	return sb.toString();
}
}
