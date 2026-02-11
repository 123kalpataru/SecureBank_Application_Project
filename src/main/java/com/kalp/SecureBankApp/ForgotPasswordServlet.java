package com.kalp.SecureBankApp;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/forgotPassword")
public class ForgotPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String email = req.getParameter("email");

        Dao dao = new Dao();
        BankUser user = dao.findUserByEmail(email); // You need to implement this in Dao

        if (user != null) {
            // ✅ Generate OTP
            String otp = OTPGenerator.generateNumericOTP(6);

            HttpSession session = req.getSession();
            session.setAttribute("resetOtp", otp);
            session.setAttribute("otpExpire", System.currentTimeMillis() + 2 * 60 * 1000); // 2 minutes expiry
            session.setAttribute("otpEmail", email);

            try {
                // ✅ Send OTP email asynchronously
                EmailService.sendEmailAsync(
                    email,
                    "SecureBank Password Reset OTP",
                    "Dear " + user.getFullName() + ",\n\nYour OTP for password reset is: " 
                    + otp + "\n\nRegards,\nSecureBank"
                );

                // ✅ Forward to OTP verification page
                req.setAttribute("message", "OTP sent to your email. Please check your inbox.");
                req.getRequestDispatcher("Otp.jsp").forward(req, resp);

            } catch (Exception e) {
                req.setAttribute("error", "Failed to send OTP. Please try again.");
                req.getRequestDispatcher("forgotPassword.jsp").forward(req, resp);
            }

        } else {
            // ✅ Email not found
            req.setAttribute("error", "Email not found. Please try again.");
            req.getRequestDispatcher("forgotPassword.jsp").forward(req, resp);
        }
    }
}
