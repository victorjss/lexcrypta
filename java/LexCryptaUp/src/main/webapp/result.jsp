<%-- 
    Document   : result
    Created on : 18-jul-2015, 12:21:10
    Author     : Víctor Suárez <victorjss@gmail.com>
--%>

<%@page import="java.util.ResourceBundle"%>
<%@page import="java.io.IOException"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    ResourceBundle rb = ResourceBundle.getBundle("upload_messages", request.getLocale());
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><%= rb.getString("result.title") %></title>
        <link type="text/css" href="css/lexcrypta.css" rel="stylesheet"/>
    </head>
    <body>
        <div id="body">
            <h1><%= rb.getString("result.thanks") %></h1>
            <p><%= rb.getString("result.key.note.prefix") %> <b><%=session.getAttribute("base64Key")%></b></p><br/>
            <p><%= rb.getString("result.url.note") %></p><br/>
            <p><a href="<%=session.getAttribute("url")%>"><%=session.getAttribute("url")%></a></p>
        </div>
        <div id="footer">LEXCrypta: <a href="https://github.com/victorjss/lexcrypta">https://github.com/victorjss/lexcrypta</a></div>
    </body>
</html>
