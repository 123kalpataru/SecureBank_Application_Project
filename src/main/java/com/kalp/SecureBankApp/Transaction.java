package com.kalp.SecureBankApp;

import java.sql.Time;
import java.util.Date;

public class Transaction {
    private int txnId;
    private String accountNumber, txnType, status;
    private double amount;
    private Date txnDate;
    private Time txnTime;

    public int getTxnId() { return txnId; }
    public void setTxnId(int txnId) { this.txnId = txnId; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getTxnType() { return txnType; }
    public void setTxnType(String txnType) { this.txnType = txnType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public Date getTxnDate() { return txnDate; }
    public void setTxnDate(Date txnDate) { this.txnDate = txnDate; }
    public Time getTxnTime() {
        return txnTime;
    }
    public void setTxnTime(Time txnTime) {
        this.txnTime = txnTime;
    }
	

}
