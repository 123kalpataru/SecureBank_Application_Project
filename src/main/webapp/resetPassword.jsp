<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Reset Password - SecureBank</title>
<link rel="stylesheet" href="Style.css">
</head>
<body>

<div class="reset-box">
    <h2>Reset Your Password</h2>
    <p>Please enter your new password below.</p>

    <!-- ✅ Reset password form -->
    <form action="resetPassword" method="post">
        <input type="password" name="newPassword" placeholder="New Password" required /><br>
        <input type="password" name="confirmPassword" placeholder="Confirm Password" required /><br>
        <input type="submit" value="Update Password">
    </form>

    <!-- ✅ Error / Success message -->
    <div class="error">
        <%= (request.getAttribute("error") != null) ? request.getAttribute("error") : "" %>
    </div>
    <div class="message">
        <%= (request.getAttribute("message") != null) ? request.getAttribute("message") : "" %>
    </div>
</div>

</body>
</html>
