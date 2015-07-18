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
        <title>LEXCrypta Upload page</title>
    </head>
    <body>
        <div id="body">
            <h1>Bienvenido a LEXCrypta</h1>
            <form action="upload" method="post" enctype="multipart/form-data">
                <fieldset>
                    <legend>Identificación del receptor</legend>
                    <label for="seed">ID del receptor: </label>
                    <input type="text" name="seed" maxlength="20" title="Identificación del recepetor, NIF por ejemplo"/>
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
