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
<div class="container">
<h2>Create Account</h2>

<form action="register"  method="post">
<input type="text"  name="fname"  placeholder="Full Name" required>
<input type="email"  name="email"  placeholder="Email Address" required>
<input type="text"  name="phone"  placeholder="Phone Number"   pattern="[0-9]{10}"     title ="Enter 10 digit mobile number"  required>
<input type="text"  name="uname"  placeholder="User Name" required>
<input type="password"  name="pwd"  placeholder="Password" required>
<input type="password"  name="cpwd"  placeholder="Confirm Password"  required>

<input type="submit"    value="Register">
</form>

<p class="error"> 
<%= request.getAttribute("error") == null ? "" : request.getAttribute("error") %> 
</p>
</div>

</body>
</html>