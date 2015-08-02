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
                bt.value = is.readOnly ? "Manual" : "Automático";
                return;
            }
        </script>
        <div id="body">
            <h1>Bienvenido a LEXCrypta</h1>
            <form action="upload" method="post" enctype="multipart/form-data">
                <fieldset>
                    <legend>Identificación del envío</legend>
                    <div class="center">
                        <label for="seed">ID del envío: </label>
                        <input id="seed" type="text" name="seed" value="" maxlength="20" title="Identificación del envío: NIF, expediente, aleatorio,..."/>
                        <input id="toggle" type="button" value="Automático" onclick="toggleAutomaticSeed()" class="button"/><br/>
                    </div>
                    <span id="advice-text">No envíe por el mismo medio este ID y la clave de cifrado que se generará en el siguiente paso</span>
                </fieldset>
                <fieldset>
                    <legend>Fichero a compartir</legend>
                    <div class="center">
                        <label for="lexfile">Fichero (5 MB máximo): </label>
                        <input id="lexfile" type="file" name="lexfile"/>
                    </div>
                </fieldset>
                <div class="center">
                    <input type="submit" value="Cifrar fichero" class="button"/>
                </div>
            </form>
        </div>
        <div id="footer">GitHub: <a href="https://github.com/victorjss/lexcrypta">https://github.com/victorjss/lexcrypta</a></div>
    </body>
</html>
