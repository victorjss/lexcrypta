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
        <link type="text/css" href="css/lexcrypta.css" rel="stylesheet"/>
    </head>
    <body>
        <div id="body">
            <h1>Bienvenido a LEXCrypta</h1>
            <form action="download" method="get">
                <fieldset>
                    <legend>Identificación del receptor</legend>
                    <div class="center">
                        <label for="seed">ID del receptor: </label>
                        <input id="seed" class="text" type="text" name="seed" maxlength="20" title="Identificación del recepetor (NIF, email,...)"/>
                    </div>
                    <span id="advice-text">Este ID deben habérselo proporcionado el remitente del mensaje por un canal diferente al de la clave o URL de descarga</span>
                </fieldset>
                <fieldset>
                    <legend>Clave de cifrado</legend>
                    <div class="center">
                        <label for="key">Clave: </label>
                        <%
                        String key = (String)session.getAttribute("key");
                        %>
                        <input id="key" class="text" type="text" name="key" maxlength="256" size="32" title="Clave de cifrado, normalmente enviada por el usuario que subió el fichero" value="<%=key != null && !"".equals(key.trim()) ? key : ""%>"/>
                    </div>
                </fieldset>
                <div class="center">
                    <input type="submit" value="Descargar" class="button"/>
                </div>
            </form>
        </div>
        <div id="footer">LEXCrypta: <a href="https://github.com/victorjss/lexcrypta">https://github.com/victorjss/lexcrypta</a></div>
    </body>
</html>
