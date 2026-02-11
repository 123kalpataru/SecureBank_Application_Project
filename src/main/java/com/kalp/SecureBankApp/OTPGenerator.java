package com.kalp.SecureBankApp;

import java.security.SecureRandom;

public class OTPGenerator {
	
	private static final SecureRandom random = new SecureRandom();
	
	private static final String DIGITS = "0123456789";
	
	private OTPGenerator() {}
	public static String generateNumericOTP(int  length)
	{
		return generateOTP(length, DIGITS);
	}
	private static String generateOTP(int length, String charset)
	{
		if(length<=0)
		{
			throw new IllegalArgumentException("OTP length must be positive");
		}
		StringBuilder sb = new StringBuilder(length);
		for(int i=0;i<length;i++)
		{
			int index = random.nextInt(charset.length());
			sb.append(charset.charAt(index));
		}
		
		return sb.toString();
	}
	
	public static OTP generateOtpWithExpiry(int length)
	{
		String code = generateNumericOTP(length);
		Long expiry = System.currentTimeMillis() +(2*60*1000);
		return new OTP(code, expiry);
	}
	
	public static class OTP
	{
		private final String code; 
		private final long expiry;
		
		public OTP( String code, long expiry)
		{
			this.code = code;
			this.expiry = expiry;
		}
		public String getCode()
		{
			return code;
		}
		public long getExpiry()
		{
			return expiry;
		}
		public boolean isExpired()
		{
			return System.currentTimeMillis()>expiry;
		}
	}

}
