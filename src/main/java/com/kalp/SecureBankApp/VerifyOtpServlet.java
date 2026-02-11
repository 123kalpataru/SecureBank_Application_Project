package com.kalp.SecureBankApp;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
@WebServlet("/verifyOtp")
public class VerifyOtpServlet extends HttpServlet 
{
	@Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
	{
		HttpSession session = req.getSession(false);
		if (session == null) { 
			req.setAttribute("error", "Session expired. Please try again."); 
			req.getRequestDispatcher("login.jsp").forward(req, resp); 
			return;
			}
		Long expiry = (Long)session.getAttribute("otpExpire");
		
		if(expiry ==null ||  System.currentTimeMillis()>expiry)
		{
			req.setAttribute("error", "OTP expired. Please try again.");
			req.getRequestDispatcher("Otp.jsp").forward(req, resp);
			return;
		}
		
		String enteredOtp = req.getParameter("otp"); // ✅ single field in Otp.jsp
		String emailOtp = (String) session.getAttribute("emailOtp");
		
		String resetOtp = (String)session.getAttribute("resetOtp");
		
		boolean validEmailOtp = (emailOtp!=null && enteredOtp!= null && enteredOtp.trim().equals(emailOtp.trim()));
		boolean validResetOtp = (resetOtp!=null && enteredOtp!= null && enteredOtp.trim().equals(resetOtp.trim()));
		
		if (validEmailOtp)
		{
			BankUser pendingUser = (BankUser)session.getAttribute("pendingUser");
		 if(pendingUser!=null)
		{ 
			 Dao dao = new Dao();
			 boolean success = dao.registerUser(pendingUser);
		
		 if(success)
		 {
			 session.removeAttribute("pendingUser");
			 resp.sendRedirect("login.jsp?msg=Registration successful, please login.");
		 }
		else
		{
			req.setAttribute("error", "Registration failed, please try again.");
			req.getRequestDispatcher("register.jsp").forward(req, resp);
		}
		} else
		 {
			resp.sendRedirect("dashboard.jsp"); // fallback for login flow
		 }
		}else if (validResetOtp)
		{
			req.getRequestDispatcher("resetPassword.jsp").forward(req, resp);
		}
		else
		{
			req.setAttribute("error", "Invalid OTP, please try again."); 
			req.getRequestDispatcher("Otp.jsp").forward(req, resp);
		}
	}
}
