<%-- 
    Document   : 500
    Created on : 19-jul-2015, 20:36:41
    Author     : Víctor Suárez <victorjss@gmail.com>
--%>

<%@page import="java.util.ResourceBundle"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    ResourceBundle rb = ResourceBundle.getBundle("download_messages", request.getLocale());
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><%= rb.getString("error400.title") %></title>
        <link type="text/css" href="css/lexcrypta.css" rel="stylesheet"/>
    </head>
    <body>
        <div id="body">
            <h1><%= rb.getString("error400.h1") %></h1>
            <span id="error400">
            <%= rb.getString("error400.text") %>
            </span>
        </div>
    </body>
</html>
