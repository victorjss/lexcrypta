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
package net.lexcrypta.core.storage;

/**
 * Needed data for storing encryption data and generating the download URL.
 * We must avoid that a user with database access can conclud the IV, key or 
 * file associated with a URL.
 * @author Víctor Suárez <victorjss@gmail.com>
 */
public class EncryptedData {
    /**
     * File with encrypted content
     */
    byte[] key;
    byte[] id;
    byte[] encryptedPath;

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getId() {
        return id;
    }

    public void setId(byte[] id) {
        this.id = id;
    }

    public byte[] getEncryptedPath() {
        return encryptedPath;
    }

    public void setEncryptedPath(byte[] encryptedPath) {
        this.encryptedPath = encryptedPath;
    }
    
    
}
