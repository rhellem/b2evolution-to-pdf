package org.hellem.util;

import java.util.Properties;

/**
Collected constants of general utility.

<P>All members of this class are immutable. 

<P>(This is an example of 
<a href='http://www.javapractices.com/Topic2.cjp'>class for constants</a>.)
*/
public final class Consts extends PropertyLoader {

 /** Opposite of {@link #FAILS}.  */
 public static final boolean PASSES = true;
 /** Opposite of {@link #PASSES}.  */
 public static final boolean FAILS = false;
 
 /** Opposite of {@link #FAILURE}.  */
 public static final boolean SUCCESS = true;
 /** Opposite of {@link #SUCCESS}.  */
 public static final boolean FAILURE = false;

 /** 
  Useful for {@link String} operations, which return an index of <tt>-1</tt> when 
  an item is not found. 
 */
 public static final int NOT_FOUND = -1;
 
 /** System property - <tt>line.separator</tt>*/
 public static final String NEW_LINE = System.getProperty("line.separator");
 /** System property - <tt>file.separator</tt>*/
 public static final String FILE_SEPARATOR = System.getProperty("file.separator");
 /** System property - <tt>path.separator</tt>*/
 public static final String PATH_SEPARATOR = System.getProperty("path.separator");
 
 public static final String EMPTY_STRING = "";
 public static final String SPACE = " ";
 public static final String TAB = "\t";
 public static final String SINGLE_QUOTE = "'";
 public static final String PERIOD = ".";
 public static final String DOUBLE_QUOTE = "\"";
 
 // Constants used in more than one class of the org.hellem-package
 public static final String OUTPUTFOLDER;
 public static final String LOGEXTENSION;
 public static final String OUTPUTFILE;
 public final static String B2TO_PDF_LOG;
 public final static String SEARCH_FOR_DOMAIN;
 public final static String REPLACE_WITH_DOMAIN;
 public final static String DATABASEURL;
 public final static String DBUSER;
 public final static String DBPWD;
 public final static String ITEXT_SALUTATION;
 public final static String SETMAXROWS;
 public final static int MAXROWS;
 public final static String DBDRIVER;
 public final static String LOGLEVEL;
 public final static String SQL_FROM;
 public final static String SQL_WHERE;
 public final static String SQL_ORDER;
 public final static long LOG_PROGRESS_SECONDS;
 
 
 static {
	 Properties props = loadProperties(Consts.class.getName());
	 
	 OUTPUTFOLDER = props.getProperty("outputfolder");
	 LOGEXTENSION = props.getProperty("logextension");
	 OUTPUTFILE = props.getProperty("outputfile");
	 B2TO_PDF_LOG = props.getProperty("b2topdf");
	 SEARCH_FOR_DOMAIN = props.getProperty("search_for_domain");
	 REPLACE_WITH_DOMAIN = props.getProperty("replace_with_domain");
	 DATABASEURL = props.getProperty("databaseurl");
	 DBUSER = props.getProperty("dbuser");
	 DBPWD = props.getProperty("dbpwd");
	 ITEXT_SALUTATION = props.getProperty("itext_salutation");
	 SETMAXROWS = props.getProperty("setmaxrows");
	 MAXROWS = Integer.parseInt(props.getProperty("maxrows"));
	 DBDRIVER = props.getProperty("dbdriver");
	 LOGLEVEL = props.getProperty("loglevel");
	 SQL_FROM = props.getProperty("sql_from");
	 SQL_WHERE = props.getProperty("sql_where");
	 SQL_ORDER = props.getProperty("sql_order");
	 LOG_PROGRESS_SECONDS = Long.parseLong(props.getProperty("log_progress_seconds"));
 }
 
 

 // PRIVATE //

 /**
  The caller references the constants using <tt>Consts.EMPTY_STRING</tt>, 
  and so on. Thus, the caller should be prevented from constructing objects of 
  this class, by declaring this private constructor. 
 */
 private Consts(){
   //this prevents even the native class from 
   //calling this ctor as well :
   throw new AssertionError();
 }
}
