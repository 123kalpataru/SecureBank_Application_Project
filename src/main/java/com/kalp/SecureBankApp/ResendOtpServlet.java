package com.kalp.SecureBankApp;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/resendOtp")
public class ResendOtpServlet extends HttpServlet
{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{ 
		HttpSession session = req.getSession(false);
		
		if(session == null || session.getAttribute("user") == null)
		{
			req.setAttribute("error", "Session  Expired, Please login agail. ");
			req.getRequestDispatcher("login.jsp").forward(req, resp);
			return;
		}
		BankUser user = (BankUser) session.getAttribute("user");
		
		String emailOtp = OTPGenerator.generateNumericOTP(6);
		
		session.setAttribute("emailOtp", emailOtp);
		session.setAttribute("otpExpire", System.currentTimeMillis() + 2 * 60 * 1000); // 2 minutes 
		session.setAttribute("otpEmail", user.getEmail());
		
		try { // Send new OTP via email 
			EmailService.sendEmailAsync(
					user.getEmail(), "SecureBank Email OTP (Resent)", 
					"Dear " + user.getFullName() + ",\n\nYour new Email OTP is: " + emailOtp +
					"\n\nThis OTP will expire in 2 minutes.\n\nRegards,\nSecureBank"
					); 
			} 
		catch (Exception e)
		{ 
			req.setAttribute("error", "Failed to resend OTP email. Please try again."); 
			req.getRequestDispatcher("Otp.jsp").forward(req, resp); 
			return; 
			}
		resp.sendRedirect("Otp.jsp");
		}
	}

