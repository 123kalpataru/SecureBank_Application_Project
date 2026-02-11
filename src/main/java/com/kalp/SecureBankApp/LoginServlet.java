package com.kalp.SecureBankApp;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        String userName = req.getParameter("uname");
        String password = req.getParameter("pwd");
        String enteredCaptcha = req.getParameter("captcha");

        HttpSession session = req.getSession();
        String sessionCaptcha = (String) session.getAttribute("captcha");

        // ✅ First validate CAPTCHA
        if (sessionCaptcha == null || !sessionCaptcha.equalsIgnoreCase(enteredCaptcha)) {
            req.setAttribute("error", "Invalid CAPTCHA. Please try again.");
            // regenerate captcha
            session.setAttribute("captcha", CaptchaGenerator.generatorCaptchaText(6));
            req.getRequestDispatcher("login.jsp").forward(req, resp);
            return;
        }

        // ✅ Then validate user credentials
        Dao dao = new Dao();
        BankUser user = dao.validateUser(userName, password);

        if (user != null) {
            session.setAttribute("user", user);
            String emailOtp = OTPGenerator.generateNumericOTP(6);

            session.setAttribute("emailOtp", emailOtp);
            session.setAttribute("otpExpire", System.currentTimeMillis() + 2 * 60 * 1000);
            session.setAttribute("otpEmail", user.getEmail());

            try {
                EmailService.sendEmailAsync(
                        user.getEmail(),
                        "SecureBank Email OTP",
                        "Dear " + user.getFullName() + ",\n\nYour Email OTP is: " + emailOtp + "\n\nRegards,\nSecureBank"
                );
            } catch (Exception e) {
                req.setAttribute("error", "Failed to send OTP email, Please try again.");
                // regenerate captcha
                session.setAttribute("captcha", CaptchaGenerator.generatorCaptchaText(6));
                req.getRequestDispatcher("login.jsp").forward(req, resp);
                return;
            }

            resp.sendRedirect("Otp.jsp");
        } else {
            req.setAttribute("error", "Please check your username and password.");
            // regenerate captcha
            session.setAttribute("captcha", CaptchaGenerator.generatorCaptchaText(6));
            req.getRequestDispatcher("login.jsp").forward(req, resp);
        }
    }
}
