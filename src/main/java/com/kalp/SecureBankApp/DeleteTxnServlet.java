package com.kalp.SecureBankApp;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/deleteTxn")
public class DeleteTxnServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) 
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        BankUser user = (session != null) ? (BankUser) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String txnIdStr = req.getParameter("txnId");
        int txnId = 0;
        try {
            txnId = Integer.parseInt(txnIdStr);
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid transaction ID");
            resp.sendRedirect("dashboard");
            return;
        }

        TransactionDao tDao = new TransactionDao();
        boolean success = tDao.deleteTransaction(txnId);

        if (!success) {
            req.setAttribute("error", "Failed to delete transaction");
        }

        // ✅ Always redirect back to dashboard so updated list shows
        resp.sendRedirect("dashboard");
    }
}
