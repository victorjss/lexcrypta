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
package net.lexcrypta.web.down.servlet;

import java.io.IOException;
import java.util.Base64;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.lexcrypta.core.storage.DecryptedData;
import net.lexcrypta.core.storage.StorageService;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author Víctor Suárez <victorjss@gmail.com>
 */
@WebServlet(urlPatterns = {"/download"})
@MultipartConfig (fileSizeThreshold = 512 * 1024, maxFileSize = 5*1024*1024, maxRequestSize = 6*1024*1024)
public class DownServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req,
            HttpServletResponse resp)
            throws ServletException, IOException {
        String key = req.getParameter("key");
        String seed = req.getParameter("seed");
        
        if (key == null || seed == null
                || "".equals(key.trim()) || "".equals(seed.trim())
                || seed.length() < 6) {
            if (key != null && !"".equals(key.trim())) {
                req.getSession().setAttribute("key", key);
            }
            resp.sendRedirect("index.jsp");
            return;
        }
        
        StorageService service = new StorageService();
        DecryptedData dd = service.decryptContent(seed, Base64.getDecoder().decode(key));
        if (dd == null) {
            resp.sendRedirect("index.jsp");
            return;
        }
        
        resp.setHeader("Content-Disposition", "attachment; filename=" + dd.getFilaName());
        IOUtils.copyLarge(dd.getContent(), resp.getOutputStream());
    }

}
