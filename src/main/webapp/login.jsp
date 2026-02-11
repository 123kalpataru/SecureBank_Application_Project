<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Secure Bank Login</title>
<link rel="stylesheet"  href="Style.css">
</head>
<body>

<!-- 🔔 Announcements Ticker -->
 <div class="ticker">
  <p>🔔 System maintenance scheduled for Sunday, 10 PM – 12 AM</p>
  </div>

<header>
<h1>SecureBank Portal</h1>

<div> <button id="darkModeToggle">🌙 Dark Mode</button> 
<button id="increaseText">🔎 Larger Text</button> </div>
</header>

<div class="container">
<h2>Login</h2>

<form action ="login" method="post">
<input type="text"  name="uname"  placeholder="UserName" required>
<input type="password"  name="pwd"  placeholder="Password" required>

<!-- ✅ Forgot Password Link -->
 <p class="forgot-link"> <a href="forgotPassword.jsp">Forgot your password?</a> </p>
 
<div class = "captcha-box">
<img id = "captchaImg"   src="<%=request.getContextPath() %>/captcha"  alt="CAPTCHA Image"><br>
<input type="text"  name="captcha" placeholder="Enter Captcha" required aria-label="Captcha">

<button type="button"   id="refreshBtn">Refresh CAPTCHA</button>

<script>
document.getElementById("refreshBtn").addEventListener("click", function() 
		{ // Each click forces a new request to CaptchaServlet 
document.getElementById("captchaImg").src = "<%= request.getContextPath() %>/captcha?" + Math.random();
});
</script>

</div>
<input type="submit"  value="Login">
</form>

<p style="text-align: center;">
<a href ="register.jsp">Don't have an account?   Register</a>
</p>

<p class="error">${error }</p>
</div>

<section class="features">
<h2>Feature Highlights</h2>
<div class="feature-container">
<div class="feature-card">
            <div class="icon">🔒</div>
            <h3>Secure Login</h3>
            <p>Your credentials are protected with encryption and session security.</p>
        </div>
        
        <div class="feature-card">
            <div class="icon">💳</div>
            <h3>Easy Transactions</h3>
            <p>Deposit and withdraw funds instantly with a simple interface.</p>
        </div>
        
        <div class="feature-card">
            <div class="icon">📊</div>
            <h3>Transaction History & Analytics</h3>
            <p>View your past transactions and analyze spending trends.</p>
        </div>
        
        <div class="feature-card">
            <div class="icon">📥</div>
            <h3>Download Statements</h3>
            <p>Export your transaction history in CSV or PDF format.</p>
        </div>
        </div>
</section>

<footer>
    <p>&copy; 2026 SecureBank. All rights reserved.</p>
    <p><a href="#">About</a> | <a href="#">Privacy Policy</a> | <a href="#">Contact</a></p>
</footer>

<!-- ✅ Scripts for Dark Mode & Accessibility -->
<script src="https://www.google.com/recaptcha/api.js" async defer></script>
<script>
document.getElementById("darkModeToggle").addEventListener("click", function() {
    document.body.classList.toggle("dark-mode");
});
document.getElementById("increaseText").addEventListener("click", function() {
    document.body.classList.toggle("large-text");
});
</script>


</body>
</html>