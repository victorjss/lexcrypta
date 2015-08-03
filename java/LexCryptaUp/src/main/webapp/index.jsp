<%-- 
    Document   : index
    Created on : 18-jul-2015, 12:13:06
    Author     : Víctor Suárez <victorjss@gmail.com>
--%>

<%@page import="java.util.ResourceBundle"%>
<%@page import="net.lexcrypta.web.up.util.UpUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String automaticSeed = UpUtils.generateSeed();
    
    ResourceBundle rb = ResourceBundle.getBundle("upload_messages", request.getLocale());
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title><%= rb.getString("index.title") %></title>
        <link type="text/css" href="css/lexcrypta.css" rel="stylesheet"/>
    </head>
    <body>
        <script type="text/javascript">
            var seed = "<%=automaticSeed%>";
            function toggleAutomaticSeed() {
                var is = document.getElementById ("seed");
                is.readOnly = !is.readOnly;
                is.value = is.readOnly ? seed : "";
                var bt = document.getElementById ("toggle");
                bt.value = is.readOnly ? "<%= rb.getString("index.button.manual") %>" : "<%= rb.getString("index.button.auto") %>";
                return;
            }
        </script>
        <div id="body">
            <h1><%= rb.getString("index.welcome") %></h1>
            <form action="upload" method="post" enctype="multipart/form-data">
                <fieldset>
                    <legend><%= rb.getString("index.id.legend") %></legend>
                    <div class="center">
                        <label for="seed"><%= rb.getString("index.id.label") %></label>
                        <input id="seed" type="text" name="seed" value="" maxlength="20" title="<%= rb.getString("index.seed.description") %>"/>
                        <input id="toggle" type="button" value="<%= rb.getString("index.button.auto") %>" onclick="toggleAutomaticSeed()" class="button"/><br/>
                    </div>
                    <span id="advice-text"><%= rb.getString("index.id.advice") %></span>
                </fieldset>
                <fieldset>
                    <legend><%= rb.getString("index.file.legend") %></legend>
                    <div class="center">
                        <label for="lexfile"><%= rb.getString("index.file.label") %></label>
                        <input id="lexfile" type="file" name="lexfile"/>
                    </div>
                </fieldset>
                <div class="center">
                    <input type="submit" value="<%= rb.getString("index.button.cipher") %>" class="button"/>
                </div>
            </form>
        </div>
        <div id="footer">GitHub: <a href="https://github.com/victorjss/lexcrypta">https://github.com/victorjss/lexcrypta</a></div>
    </body>
</html>
