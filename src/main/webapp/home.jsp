<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html>
<head>
    <title>Select Joke Category</title>
</head>
<body>
<h1>Select a Joke Category</h1>
<br/>
<a href="${pageContext.request.contextPath}/random-joke">Get Random Joke</a>
<% String[] category = {"animal","career","celebrity","dev","explicit","fashion","food","history","money","movie","music","political","religion","science","sport","travel"}; %>
<form action="${pageContext.request.contextPath}/joke-by-category" method="get">
    <select name="category">
        <option value="">-- Select Category --</option>
        <% for (int i=0; i<16; i++){ %>
        <option value="<%= category[i] %>"><%= category[i] %></option>
        <% } %>
    </select>
    <br/>
    <input type="submit" value="Get Joke">
</form>
<br>
<a href="${pageContext.request.contextPath}/user-jokes">Get User Jokes</a>
</body>
</html>
