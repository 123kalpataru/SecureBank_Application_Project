<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Verify OTP - SecureBank</title>
<link rel="stylesheet" href="Style.css">

<script type="text/javascript">
function startCountdown(expiryTime) {
    var countdownElement = document.getElementById("countdown");
    var verifyBtn = document.getElementById("verifyBtn");

    function updateCountdown() {
        var now = new Date().getTime();
        var distance = expiryTime - now;

        if (distance <= 0) {
            countdownElement.innerHTML = "OTP Expired!";
            verifyBtn.disabled = true;
            clearInterval(timer);
            return;
        }

        var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
        var seconds = Math.floor((distance % (1000 * 60)) / 1000);
        countdownElement.innerHTML = minutes + "m " + seconds + "s remaining";
    }

    updateCountdown();
    var timer = setInterval(updateCountdown, 1000);
}
</script>
</head>
<body
 <% if (session.getAttribute("otpExpire") != null) { %> 
 onload="startCountdown(<%= session.getAttribute("otpExpire") %>)"
  <% } %>
  >

<div class="otp-box">
    <h2>Enter OTP</h2>
    <p>OTP has been sent to: <b><%= session.getAttribute("otpEmail") %></b></p>
    <p id="countdown"></p>

    <!-- ✅ Single OTP field -->
    <form action="verifyOtp" method="post">
        <input type="text" name="otp" placeholder="Enter OTP" required /><br>
        <input type="submit" id="verifyBtn" value="Verify">
    </form>

    <!-- ✅ Resend option -->
    <form action="resendOtp" method="post" style="margin-top:10px;">
        <input type="submit" value="Resend OTP">
    </form>

    <!-- ✅ Error message -->
    <div class="error">
        <%= (request.getAttribute("error") != null) ? request.getAttribute("error") : "" %>
    </div>
    
    <div class="message"> 
    <%= (request.getAttribute("message") != null) ? request.getAttribute("message") : "" %> 
    </div>
</div>

</body>
</html>
