<%-- 
    Document   : result
    Created on : 18-jul-2015, 12:21:10
    Author     : Víctor Suárez <victorjss@gmail.com>
--%>

<%@page import="java.io.IOException"%>
<%@page import="java.util.List"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>LEXCrypta Upload page</title>
    </head>
    <body>
        <h1>Gracias por usar LEXCrypta</h1>
        <span id="result-page-key">La clave de descarga es <b><%=request.getAttribute("base64Key")%></b></span><br/>
        <span id="result-page-advice">Puede descargar compartir dicha clave o las siguientes URL:</span><br/>
        <span id="result-page-url"><a href="<%=request.getAttribute("url")%>"><%=request.getAttribute("url")%></a></span>
    </body>
</html>
