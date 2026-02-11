package com.kalp.SecureBankApp;

import java.io.IOException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/transaction")
public class TransactionServlet extends HttpServlet {

    private static final int PAGE_SIZE = 5; // adjust as needed

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        BankUser user = (session != null) ? (BankUser) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        String type = req.getParameter("type");
        String amountStr = req.getParameter("amount");
        double amount;

        try {
            amount = Double.parseDouble(amountStr);
            if (amount <= 0) {
                req.setAttribute("error", " ");
                forwardWithHistory(req, resp, user);
                return;
            }
        } catch (NumberFormatException e) {
            req.setAttribute("error", "Invalid amount entered.");
            forwardWithHistory(req, resp, user);
            return;
        }

        TransactionDao tDao = new TransactionDao();
        boolean success = false;

        if ("Deposit".equalsIgnoreCase(type)) {
            success = tDao.deposit(user.getAccountNumber(), amount);
        } else if ("Withdrawal".equalsIgnoreCase(type)) {
            if (user.getBalance() >= amount) {
                success = tDao.withdraw(user.getAccountNumber(), amount);
            } else {
                req.setAttribute("error", "Insufficient Balance, Please Check your Account Balance!!");
            }
        } else {
            req.setAttribute("error", "Invalid transaction type.");
        }

        // Refresh balance
        double latestBalance = tDao.getBalance(user.getAccountNumber());
        user.setBalance(latestBalance);
        session.setAttribute("user", user);

        // Success/error messages
        if (success) {
            req.setAttribute("success", "");
        } else if (req.getAttribute("error") == null) {
            req.setAttribute("error", "Transaction failed, please try again.");
        }

        // Forward with updated history
        forwardWithHistory(req, resp, user);
    }

    private void forwardWithHistory(HttpServletRequest req, HttpServletResponse resp, BankUser user)
            throws ServletException, IOException {
        TransactionDao tDao = new TransactionDao();

        // ✅ Automatically detect page parameter (defaults to 1)
        int page = 1;
        String pageParam = req.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException ignored) {}
        }

        // Fetch paginated history
        List<Transaction> history = tDao.getTransactionHistory(user.getAccountNumber(), page, PAGE_SIZE);
        int totalRecords = tDao.countTransactions(user.getAccountNumber());
        int totalPages = (int) Math.ceil((double) totalRecords / PAGE_SIZE);

        req.setAttribute("transactions", history);
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);

        req.getRequestDispatcher("dashboard.jsp").forward(req, resp);
    }
}
