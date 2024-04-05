<%@ page import="org.bson.Document" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>

<!DOCTYPE html>
<html>
<head>
    <title>Dashboard</title>
    <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
</head>
<body>
<div class="container my-5">
    <h1>Web Service Dashboard</h1>

    <h2>Operations Analytics</h2>
    <table class="table table-striped">
        <tbody>
        <tr>
            <%
                Long userCount = (Long) request.getAttribute("allUsers");
                if (userCount != null) {
            %>
            <td>User count</td>
            <td><%= userCount %></td>
            <%
            } else {
            %>
            <td>Error: User count data not available</td>
            <%
                }
            %>
        </tr>
        </tbody>
    </table>
    <h2>Logs</h2>
    <table class="table table-striped">
        <thead>
        <tr>
            <th>Timestamp</th>
            <th>Username</th>
            <th>URL Pattern</th>
            <th>Request Parameters</th>
            <th>API Endpoint</th>
            <th>API Response</th>
            <th>Status Code</th>
        </tr>
        </thead>
        <tbody>
        <%
            List<Document> allLogs = (List<Document>) request.getAttribute("allLogs");
            if (allLogs != null) {
                for (Document log : allLogs) {
        %>
        <tr>
            <td><%= log.getLong("timestamp") %></td>
            <td><%= log.getString("username") %></td>
            <td><%= log.getString("urlPattern") %></td>
            <td><%= log.getString("requestParameters") %></td>
            <td><%= log.getString("apiEndpoint") %></td>
            <td><%= log.getString("apiResponse") %></td>
            <td><%= log.getInteger("statusCode") %></td>
        </tr>
        <%
                }
            }
        %>
        </tbody>
    </table>
</div>
</body>
</html>