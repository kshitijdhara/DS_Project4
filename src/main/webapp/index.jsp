<%--// Andrew ID - kdhara--%>
<%--// Name - Kshtij Dhara--%>
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
<h1>Login</h1>
<form action="${pageContext.request.contextPath}/login" method="get">
    <label for="login-username">Username:</label>
    <input type="text" id="login-username" name="username" required>
    <br>
    <label for="login-password">Password:</label>
    <input type="password" id="login-password" name="password" required>
    <br>
    <input type="submit" value="Login">
</form>
<h1>Sign Up</h1>
<form action="${pageContext.request.contextPath}/create-user" method="get">
    <label for="signup-username">Username:</label>
    <input type="text" id="signup-username" name="username" required>
    <br>
    <label for="signup-password">Password:</label>
    <input type="password" id="signup-password" name="password" required>
    <br>
    <input type="submit" value="Sign Up">
</form>
</body>
</html>