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
        <h1>Bienvenido a LEXCrypta</h1>
        <form action="/upload" method="post" enctype="multipart/form-data">
            <fieldset>
                <legend>Identificación del receptor (NIF por ejemplo)</legend>
                <label for="seed">ID del receptor: </label>
                <input type="text" name="seed" maxlength="20" title="Identificación del recpetor, NIF por ejemplo"/>
            </fieldset>
            <fieldset>
                <legend>Fichero a compartir</legend>
                <label for="lexfile">Fichero: </label>
                <input type="file" name="lexfile"/>
            </fieldset>
            <input type="submit" name="Enviar"/>
        </form>
    </body>
</html>
