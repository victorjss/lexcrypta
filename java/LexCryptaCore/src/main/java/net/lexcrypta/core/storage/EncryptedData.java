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

import java.io.File;

/**
 *
 * @author Víctor Suárez <victorjss@gmail.com>
 */
public class EncryptedData {
    File file;
    byte[] key;
    byte[] iv;
    byte[] id;
    byte[] encryptedPath;

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public byte[] getKey() {
        return key;
    }

    public void setKey(byte[] key) {
        this.key = key;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
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
