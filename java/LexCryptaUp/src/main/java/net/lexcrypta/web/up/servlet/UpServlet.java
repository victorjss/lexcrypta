/*
 * Copyright (C) 2015 Víctor Suárez <victorjss@gmail.com>
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.lexcrypta.web.up.servlet;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import net.lexcrypta.core.conf.CoreHelper;
import net.lexcrypta.core.storage.StorageService;

/**
 *
 * @author Víctor Suárez <victorjss@gmail.com>
 */
@WebServlet(urlPatterns = {"/upload"})
@MultipartConfig (fileSizeThreshold = 512 * 1024, maxFileSize = 5*1024*1024, maxRequestSize = 6*1024*1024)
public class UpServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
        Part part = req.getPart("lexfile");
        InputStream content = part.getInputStream();
        StorageService service = new StorageService();
        byte[] key = service.encryptContent(content, getSeed(req));
        String base64Key = Base64.getEncoder().encodeToString(key);
        req.setAttribute("base64Key", base64Key);
        String url = getDownloadUrl(base64Key);
        req.setAttribute("url", url);
    }

    protected String getDownloadUrl(String base64Key) {
        CoreHelper coreHelper = new CoreHelper();
        String downloadBaseUrl = coreHelper.getConfigurationValue("web.download.base.url");
        String url = downloadBaseUrl + "/download?key=" + base64Key;
        return url;
    }

    /**
     * Get seed from input form, by default is the "seed" HTTP parameter.
     * Override this for different seed acquisition methods.
     * @param req Request object
     * @return String with the initial value for encryption process
     */
    protected String getSeed(HttpServletRequest req) {
        return req.getParameter("seed");
    }

}
