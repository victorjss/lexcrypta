<%-- 
    Document   : index
    Created on : 18-jul-2015, 12:13:06
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
        <title><%= rb.getString("index.title") %></title>
        <link type="text/css" href="css/lexcrypta.css" rel="stylesheet"/>
    </head>
    <body>
        <div id="body">
            <h1><%= rb.getString("index.welcome") %></h1>
            <form action="download" method="get">
                <fieldset>
                    <legend><%= rb.getString("index.id.legend") %></legend>
                    <div class="center">
                        <label for="seed"><%= rb.getString("index.id.label") %></label>
                        <input id="seed" class="text" type="text" name="seed" maxlength="20" title="<%= rb.getString("index.id.description") %>"/>
                    </div>
                    <span id="advice-text"><%= rb.getString("index.id.advice") %></span>
                </fieldset>
                <fieldset>
                    <legend><%= rb.getString("index.id.legend") %></legend>
                    <div class="center">
                        <label for="key"></label>
                        <%
                        String key = (String)session.getAttribute("key");
                        %>
                        <input id="key" class="text" type="text" name="key" maxlength="256" size="32" title="<%= rb.getString("index.key.description") %>" value="<%=key != null && !"".equals(key.trim()) ? key : ""%>"/>
                    </div>
                </fieldset>
                <div class="center">
                    <input type="submit" value="<%= rb.getString("index.button.download") %>" class="button"/>
                </div>
            </form>
        </div>
        <div id="footer">LEXCrypta: <a href="https://github.com/victorjss/lexcrypta">https://github.com/victorjss/lexcrypta</a></div>
    </body>
</html>
