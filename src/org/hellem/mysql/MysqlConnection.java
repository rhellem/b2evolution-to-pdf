package org.hellem.mysql;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hellem.b2evo.BloggPost;
import org.hellem.util.Consts;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;

/**
 * See http://www.roseindia.net/jdbc/jdbc-mysql/SelectRecords.shtml for prepared
 * statements
 * 
 * @author Rune Hellem
 * 
 */
public class MysqlConnection {
	Logger logger = Logger.getLogger(Consts.B2TO_PDF_LOG);

	public List<BloggPost> getBlogPosts() {
		List<BloggPost> entries = new ArrayList<BloggPost>();
		Connection con = null;
		try {
			Statement stmt;

			// Register the JDBC driver for MySQL.
			Class.forName(Consts.DBDRIVER);

			// Define URL of database server for
			// database named mysql on the localhost
			// with the default port number 3306.
		
			final String url = Consts.DATABASEURL;

			// Get a connection to the database for a
			// user named 'webuser' with password 'password'
			// Ok when database is local, do NOT use this when online
			
			con = DriverManager.getConnection(url, Consts.DBUSER, Consts.DBPWD);
			
			
			
			// Display URL and connection information
			logger.log(Level.INFO, "URL: " + url);
			logger.log(Level.INFO, "Connection: " + con);
			// Get a Statement object
			stmt = con.createStatement();

			// Use this while testing to avoid creating full pdf
			if (Boolean.valueOf(Consts.SETMAXROWS)) {
					logger.log(Level.INFO,
						"Setting maxrows to " + Consts.MAXROWS + " - only needed when doing testing");
					stmt.setMaxRows(Consts.MAXROWS);
			}

			// Create the select statement, change blog_ID to specify which blog
			StringBuffer select = new StringBuffer(
					"SELECT * FROM " + Consts.SQL_FROM + " ");
			select.append(" WHERE " + Consts.SQL_WHERE + " ");
			select.append(" ORDER BY " + Consts.SQL_ORDER);

			logger.log(Level.FINEST, select.toString());
			stmt.execute(select.toString());

			java.sql.ResultSet r = stmt.getResultSet();
			int i = 0;
			logger.log(Level.FINEST, "Executed SQL");
			while (r.next()) {
				++i;
				BloggPost post = new BloggPost();
				post.setHeader(r.getString("post_title"));
				post.setDato(r.getString("post_datestart"));
				post.setTekst(r.getString("post_content"));

				entries.add(post);
			}

			logger.log(Level.INFO, "Did fetch " + i + " posts");

			con.close();
		} catch (CommunicationsException e) {
			// db-url/uid/pwd is wrong or db is not available
			logger.log(Level.SEVERE, "Not able to connect to db with url: " + Consts.DATABASEURL);
			logger.log(Level.SEVERE, "Message returned: " + e.getMessage());
			
		} catch (SQLException e) {
			// Error in SQL or such
			logger.severe(e.getMessage());
		} catch (ClassNotFoundException e) {
			// Db-driver is wrong, not found etc.
			logger.severe(e.getMessage());
		} finally {
			if (null != con) {
				try {
					con.close();
				} catch (SQLException e) {
					// Do nothing
				}
			}
		}
		return entries;
	}
}
