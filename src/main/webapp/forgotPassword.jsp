<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Forgot Password - SecureBank</title>
<link rel="stylesheet"   href = "Style.css">
</head>
<body>


<div class="container">

<h2>Forgot Password</h2>

<p style="text-align:center; color:#ddd;">
Enter your registered email address. We’ll send you an OTP to reset your password.
</p>

<form action="forgotPassword"  method="post">

<input type="email" name="email" placeholder="Enter your email"  required aria-label="Email Address">
<input type="submit"  value="Send OTP">

</form>

<p style="text-align:center;">

<a href="login.jsp">Back to Login</a>
</p>

<!-- Feedback messages -->
 <p class="error">${error}</p>
  <p class="success-box">${message}</p>
</div>

</body>
</html>