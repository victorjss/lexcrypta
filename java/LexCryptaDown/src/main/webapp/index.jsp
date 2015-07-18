<%-- 
    Document   : index
    Created on : 18-jul-2015, 12:13:06
    Author     : Víctor Suárez <victorjss@gmail.com>
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>LEXCrypta Download page</title>
    </head>
    <body>
        <h1>Bienvenido a LEXCrypta</h1>
        <form action="download" method="get" enctype="multipart/form-data">
            <fieldset>
                <legend>Identificación del receptor</legend>
                <label for="seed">ID del receptor: </label>
                <input type="text" name="seed" maxlength="20" title="Identificación del recpetor, NIF por ejemplo"/>
            </fieldset>
            <fieldset>
                <legend>Clave de cifrado</legend>
                <label for="key">Clave: </label>
                <%
String key = (String)session.getAttribute("key");
                %>
                <input type="text" name="key" maxlength="256" title="Clave de cifrado, normalmente enviada por el usuario que subió el fichero" value="<%=key != null && !"".equals(key.trim()) ? key : ""%>"/>
            </fieldset>
            <input type="submit" name="Descargar"/>
        </form>
    </body>
</html>
