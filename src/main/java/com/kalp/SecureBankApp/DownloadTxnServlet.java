package com.kalp.SecureBankApp;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

@WebServlet("/downloadTxn")
public class DownloadTxnServlet extends HttpServlet {
    private final TransactionDao txnDao = new TransactionDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        BankUser user = (session != null) ? (BankUser) session.getAttribute("user") : null;

        if (user == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String accountNumber = user.getAccountNumber();
        List<Transaction> txnList = txnDao.getTransactionHistory(accountNumber);

        response.setContentType("text/csv");
        response.setHeader("Content-Disposition", "attachment; filename=transactions.csv");

        try (PrintWriter out = response.getWriter()) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            // ✅ CSV header
            out.println("TxnId,Date,Type,Amount,Status");

            // ✅ Write rows
            for (Transaction txn : txnList) {
                String dateStr = (txn.getTxnDate() != null) ? sdf.format(txn.getTxnDate()) : "";
                out.printf("%d,%s,%s,%.2f,%s%n",
                        txn.getTxnId(),
                        dateStr,
                        txn.getTxnType(),
                        txn.getAmount(),
                        txn.getStatus());
            }
        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Unable to generate transaction download.");
        }
    }
}
