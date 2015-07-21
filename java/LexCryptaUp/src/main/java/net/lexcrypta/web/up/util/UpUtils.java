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
package net.lexcrypta.web.up.util;

import java.util.Random;

/**
 *
 * @author Víctor Suárez <victorjss@gmail.com>
 */
public class UpUtils {
    static Random random = null;
    
    static {
        try {
            random = new Random();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static String generateSeed() {
        long l = Math.abs(random.nextLong());
        return String.format("%019d", l);
    }
}
