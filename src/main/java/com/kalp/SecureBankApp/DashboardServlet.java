package com.kalp.SecureBankApp;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    private static final int RECORDS_PER_PAGE = 5;
    private final TransactionDao tDao = new TransactionDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        BankUser user = (session != null) ? (BankUser) session.getAttribute("user") : null;

        if (user == null) {
            resp.sendRedirect("login.jsp");
            return;
        }

        int currentPage = 1;
        try {
            String pageParam = req.getParameter("page");
            if (pageParam != null) {
                currentPage = Integer.parseInt(pageParam);
                if (currentPage < 1) currentPage = 1;
            }
        } catch (NumberFormatException ignored) {
        	currentPage = 1;
        }

        
        List<Transaction> txns = null;
        int totalRecords = 0;
        int totalPages = 0;

        try {
            // ✅ Count total records
        	
        	double latestBalance = tDao.getBalance(user.getAccountNumber()); 
        	user.setBalance(latestBalance);
        	session.setAttribute("user", user);
        	
            totalRecords = tDao.countTransactions(user.getAccountNumber());
            totalPages = (int) Math.ceil(totalRecords * 1.0 / RECORDS_PER_PAGE);

            
            if (currentPage > totalPages && totalPages > 0) 
            { 
            	currentPage = totalPages;
            	}
            // ✅ Fetch only current page
            txns = tDao.getTransactionHistory(user.getAccountNumber(), currentPage, RECORDS_PER_PAGE);
           
        } catch (Exception e) {
            e.printStackTrace();
            req.setAttribute("error", "Unable to load transactions at the moment.");
        }

        // ✅ Pass attributes to JSP
        req.setAttribute("user", user);
        req.setAttribute("transactions", txns);
        req.setAttribute("currentPage", currentPage);
        req.setAttribute("totalPages", totalPages);

        req.getRequestDispatcher("dashboard.jsp").forward(req, resp);
    }
}
