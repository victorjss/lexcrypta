/*
 * Copyright (C) 2015 Víctor Suárez<victorjss@gmail.com>
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
package net.lexcrypta.core.conf;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Properties;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 *
 * @author Víctor Suárez<victorjss@gmail.com>
 */
public class CoreHelper {
    static volatile Properties queriesProps = null;
    static volatile Properties coreProps = null;
    Connection testConnection = null;

    public static Properties getQueriesProps() {
        return queriesProps;
    }

    public static void setQueriesProps(Properties queriesProps) {
        CoreHelper.queriesProps = queriesProps;
    }

    public static Properties getCoreProps() {
        return coreProps;
    }

    public static void setCoreProps(Properties coreProps) {
        CoreHelper.coreProps = coreProps;
    }


    /**
     * Get configuration value (stored in core.properties) for the specified key. 
     * This method assures thread-safe cache initialization.
     * @param property key
     * @return value assigned to the key
     */
    public String getConfigurationValue(String property) {
        Properties props = coreProps; //optimized volatile access (only one access in this way if already initilized)
        if (props == null) {
            synchronized (CoreHelper.class) {
                if (coreProps == null) {
                    Properties ps = new Properties();
                    try {
                        ps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("core.properties"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    coreProps = ps;
                }
            }
            props = coreProps;
        }
        return props.getProperty(property);
    }

    public Connection getTestConnection() {
        return testConnection;
    }

    public void setTestConnection(Connection testConnection) {
        this.testConnection = testConnection;
    }
    
    /**
     * Get a new connection from the database pool
     * @return a connection from db pool
     * @throws SQLException 
     */
    public Connection getConnection() throws SQLException {
        if (testConnection != null) {
            return testConnection;
        }
        DataSource ds = getDataSource();
        return ds.getConnection();
    }

    /**
     * Get a DataSource from server configuration, using JNDI calls
     * @return DataSource object from server configuration
     */
    public DataSource getDataSource() {
        try {
            InitialContext cxt = new InitialContext();
            DataSource ds = (DataSource) cxt.lookup(getConfigurationValue("jdbc.jndi"));
            return ds;
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Return a SQL (in String format) configured in sql.properties file. This
     * file contanis keys with <database-vendor>.<sql-id>, where <database-vendor>
     * is the result of java.sql.DatabaseMetaData.getDatabaseProductName() in 
     * lower-case format.
     * This method assures thread-safe cache initialization.
     * @param sqlName
     * @return 
     */
    public String getNamedSql(String sqlName) {
        Properties props = queriesProps; //optimized volatile access (only one access in this way if already initilized)
        if (props == null) {
            synchronized (CoreHelper.class) {
                if (queriesProps == null) {
                    Properties ps = new Properties();
                    try {
                        ps.load(Thread.currentThread().getContextClassLoader().getResourceAsStream("sql.properties"));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    queriesProps = ps;
                }
            }
            props = queriesProps;
        }
        return props.getProperty(sqlName);
    }
    
    /**
     * Return a SQL (in String format) based on the provided sqlId value and 
     * the database vendor (obtained from the DatabaseMetaData parameter). 
     * @param dbmd
     * @param sqlId
     * @see #getNamedSql(java.lang.String) 
     * @return 
     */
    public String getSql(DatabaseMetaData dbmd, String sqlId) {
        if (sqlId == null) {
            throw new NullPointerException("sqlId");
        }
        try {
            String sqlName = dbmd.getDatabaseProductName().toLowerCase() + "." + sqlId;
            return getNamedSql(sqlName);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
