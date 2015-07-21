<%-- 
    Document   : index
    Created on : 18-jul-2015, 12:13:06
    Author     : Víctor Suárez <victorjss@gmail.com>
--%>

<%@page import="net.lexcrypta.web.up.util.UpUtils"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<%
    String automaticSeed = UpUtils.generateSeed();
%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>LEXCrypta Upload page</title>
    </head>
    <body>
        <script type="text/javascript">
            var seed = "<%=automaticSeed%>";
            function toggleAutomaticSeed() {
                var is = document.getElementById ("input-seed");
                is.readOnly = !is.readOnly;
                is.value = is.readOnly ? seed : "";
                var bt = document.getElementById ("button-toggle");
                bt.value = is.readOnly ? "Manual" : "Automático";
                return;
            }
        </script>
        <div id="body">
            <h1>Bienvenido a LEXCrypta</h1>
            <form action="upload" method="post" enctype="multipart/form-data">
                <fieldset>
                    <legend>Identificación del envío</legend>
                    <label for="seed">ID del envío: </label>
                    <input id="input-seed" type="text" name="seed" value="" maxlength="20" title="Identificación del envío: NIF, expediente, aleatorio,..."/>
                    <input id="button-toggle" type="button" value="Automático" onclick="toggleAutomaticSeed()"/><br/>
                    <span id="advice-text">No envíe por el mismo medio este ID y la clave que se generará en el siguiente paso</span>
                </fieldset>
                <fieldset>
                    <legend>Fichero a compartir</legend>
                    <label for="lexfile">Fichero: </label>
                    <input type="file" name="lexfile"/>
                </fieldset>
                <input type="submit" value="Enviar"/>
            </form>
        </div>
        <div id="footer">LEXCrypta: <a href="https://github.com/victorjss/lexcrypta">https://github.com/victorjss/lexcrypta</a></div>
    </body>
</html>
