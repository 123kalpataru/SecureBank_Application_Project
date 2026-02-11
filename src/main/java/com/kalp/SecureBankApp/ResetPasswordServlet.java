package com.kalp.SecureBankApp;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/resetPassword")
public class ResetPasswordServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        if (session == null) {
            req.setAttribute("error", "Session expired. Please try again.");
            req.getRequestDispatcher("forgotPassword.jsp").forward(req, resp);
            return;
        }

        String email = (String) session.getAttribute("otpEmail");
        String newPassword = req.getParameter("newPassword");
        String confirmPassword = req.getParameter("confirmPassword");
   
        if (newPassword == null || confirmPassword == null || !newPassword.equals(confirmPassword)) {
            req.setAttribute("error", "Passwords do not match. Please try again.");
            req.getRequestDispatcher("resetPassword.jsp").forward(req, resp);
            return;
        }

        try {
            Dao dao = new Dao();
            boolean updated = dao.updateUserPassword(email, newPassword); // implement in Dao

            if (updated) {
                // ✅ Clear OTP-related session attributes
                session.removeAttribute("emailOtp");
                session.removeAttribute("otpExpire");
                session.removeAttribute("otpEmail");

                req.setAttribute("message", "Password updated successfully. Please login with your new password.");
                req.getRequestDispatcher("login.jsp").forward(req, resp);
            } else {
                req.setAttribute("error", "Failed to update password. Please try again.");
                req.getRequestDispatcher("resetPassword.jsp").forward(req, resp);
            }

        } catch (Exception e) {
            req.setAttribute("error", "An error occurred. Please try again.");
            req.getRequestDispatcher("resetPassword.jsp").forward(req, resp);
        }
    }
}
