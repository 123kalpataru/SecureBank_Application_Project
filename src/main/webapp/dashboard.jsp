<%@ page import="java.util.List,com.kalp.SecureBankApp.Transaction,com.kalp.SecureBankApp.BankUser" %>
<%@ page import="java.text.SimpleDateFormat" %>
<html>
<head>
    <title>SecureBank Dashboard</title>
    <link rel="stylesheet" href="Style.css">
</head>
<body>

<%
    if (session == null || session.getAttribute("user") == null) {
        response.sendRedirect("login.jsp");
        return;
    }

    BankUser user = (BankUser) session.getAttribute("user");
    List<Transaction> txns = (List<Transaction>) request.getAttribute("transactions");
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");

    Integer currentPageAttr = (Integer) request.getAttribute("currentPage");
    Integer totalPagesAttr = (Integer) request.getAttribute("totalPages");
    int currentPage = (currentPageAttr != null) ? currentPageAttr : 1;
    int totalPages = (totalPagesAttr != null) ? totalPagesAttr : 0;
    
    String accountNumber = user.getAccountNumber();
    String maskedAccount = "XXXX-XXXX-"+accountNumber.substring(accountNumber.length()-4);
    
    String errorMsg = (String)request.getAttribute("error");
    String successMsg = (String)request.getAttribute("success");
%>

<header>
<h2>Welcome, <%= user.getFullName() %></h2>
<a   href="logout.jsp"  class="logout">Logout</a>
</header>

<div class="account-info">
 <p>Account Number: <%= maskedAccount %></p> 
 <p>Total Balance: Rs. <%= user.getBalance() %></p> 
 </div>
 
 
<div class="time-display"> 
Current Time: <span id="clock"></span>
 </div>

<div id="transaction-form" class="container">
<h3>Make a Transaction</h3>
<form action="transaction" method="post">
    <select name="type">
        <option value="Deposit">Deposit</option>
        <option value="Withdrawal">Withdrawal</option>
    </select>
    <input type="number" name="amount" min="100"  step="10" required>
    <input type="submit" value="Submit">
</form>
</div>

<!-- Error message -->
<p class="success"><%= successMsg != null ? successMsg : "" %></p>

<h3>Recent Transactions</h3>
<table>
    <tr>
        <th>Date</th><th>Time</th><th>Type</th><th>Amount</th><th>Status</th><th>Action</th>
    </tr>
<%
    if (txns != null && !txns.isEmpty()) {
        for (Transaction t : txns) {
%>
    <tr>
        <td><%= dateFormat.format(t.getTxnDate()) %></td>
        <td><%= timeFormat.format(t.getTxnDate()) %></td>
        <td><%= t.getTxnType() %></td>
        <td>Rs. <%= t.getAmount() %></td>
        <td><%= t.getStatus() %></td>
        <td>
            <form action="deleteTxn" method="post"  onsubmit="return confirmDelete();">
                <input type="hidden" name="txnId" value="<%= t.getTxnId() %>" />
                <input type="submit" value="Delete" />
            </form>
        </td>
    </tr>
<%
        }
    } else {
%>
    <tr><td colspan="6">No transactions found</td></tr>
<%
    }
%>
</table>

<!-- Pagination -->
<div>
<%
    if (totalPages > 1) {
        if (currentPage > 1) {
%>
        <a href="dashboard?page=<%= currentPage - 1 %>">Previous</a>
<%
        }
        for (int i = 1; i <= totalPages; i++) {
            if (i == currentPage) {
%>
        <b><%= i %></b>
<%
            } else {
%>
        <a href="dashboard?page=<%= i %>"><%= i %></a>
<%
            }
        }
        if (currentPage < totalPages) {
%>
        <a href="dashboard?page=<%= currentPage + 1 %>">Next</a>
<%
        }
    }
%>
</div>

<!-- Download button -->
<div>
    <form action="downloadTxn" method="get">
        <input type="submit" value="Download Transactions (CSV)" />
    </form>
</div>

<script>
function updateClock() {
    const now = new Date();
    document.getElementById('clock').textContent =
        now.toLocaleTimeString('en-GB'); // HH:mm:ss format
}
setInterval(updateClock, 1000);
updateClock();
function confirmDelete()
{
return confirm("Are you sure you want to delete this transection");
}

<% if(errorMsg != null && !errorMsg.isEmpty()){%>
alert("<%= errorMsg%>");
<%}%>

<% if(successMsg != null && !successMsg.isEmpty()){%>
alert("<%= successMsg%>");
<%}%>



var  timeoutMinutes = 15;
var warningMinutes = 14;

setTimeout(function()
		{
alert("Your session will expire with in 1 minute, Please save your work or refresh. ");
}, warningMunutes*60*1000);

setTimeout(function(){

	window.location.href='logout';
}, timeoutMinutes *60*1000);
</script>

</body>
</html>
