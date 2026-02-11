package com.kalp.SecureBankApp;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
@WebServlet("/register")
public class RegisterServlet extends HttpServlet 
{
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		
		String password = req.getParameter("pwd");
		String confirmPassword = req.getParameter("cpwd");
		
		if(password == null || confirmPassword == null || !password.equals(confirmPassword))
		{
			req.setAttribute("error", "Password do not match");
			req.getRequestDispatcher("register.jsp").forward(req, resp);
			return ;
		}
		
		String email  = req.getParameter("email");
		String phone  = req.getParameter("phone");
		
		if (email == null || email.isEmpty()) 
		{
			req.setAttribute("error", "Email address is missing. Please try again.");
			req.getRequestDispatcher("register.jsp").forward(req, resp);
			return;
			}
		
		if (phone == null || phone.isEmpty()) 
		{ 
			req.setAttribute("error", "Phone number is missing. Please try again."); 
			req.getRequestDispatcher("register.jsp").forward(req, resp); 
			return;
			}
		BankUser user = new BankUser();
		 user.setFullName(req.getParameter("fname"));
		 user.setEmail (req.getParameter("email"));
		 user.setPhoneNumber(req.getParameter("phone"));
		 user.setUsername(req.getParameter("uname"));
		 user.setPassword(password);
		 user.setAccountNumber("ACCT" + System.currentTimeMillis()+(int)(Math.random()*1000));
		 user.setBalance(0.0);
		 
		
					
					 
					 
					 HttpSession session = req.getSession();
					 session.setAttribute("pendingUser", user);
					 session.setAttribute("otpEmail", user.getEmail());
					 String emailOtp = OTPGenerator.generateNumericOTP(6);
					 
					 session.setAttribute("emailOtp", emailOtp);
					 session.setAttribute("otpExpire", System.currentTimeMillis() + 2 * 60 * 1000);
					 
					 EmailService.sendEmailAsync( 
							 user.getEmail(),
							 "Your SecureBank Email OTP is",
							 "Dear  " + user.getFullName() + ",\n\nYour Email OTP is: " + emailOtp + "\n\nRegards,\nSecureBank"
							 );
					 
					 req.setAttribute("message", "Please verify the OTP sent to your email.");
					 req.getRequestDispatcher("Otp.jsp").forward(req, resp);

					 
	}

	}


