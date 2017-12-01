package org.hellem.b2evo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;


import org.hellem.util.Consts;

/**
 * Helper class containing the fields needed for each
 * blog post when creating the pdf-document
 * 
 * @author Rune Hellem
 *
 */
public class BloggPost {
	Logger logger = Logger.getLogger(Consts.B2TO_PDF_LOG);
	public BloggPost() {

	}

	private String header = null;
	private String dato = null;
	private String tekst = null;

	public String getHeader() {
		return header;
	}

	public void setHeader(String header) {
		this.header = header;
	}

	public String getDato() {
		return dato;
	}

	public void setDato(String dato) {
		this.dato = dato;
	}

	public String getTekst() {
		return tekst;
	}

	public void setTekst(String tekst) {
		this.tekst = tekst;
	}

	/**
	 * @return date as DateFormat dfPDF = new SimpleDateFormat("dd MMMM yyyy");
	 */
	public String getFormatedDate() {
		// 2010-04-25 16:01:41.0
		DateFormat dfBlog = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		DateFormat dfPDF = new SimpleDateFormat("dd MMMM yyyy");
		try {
			Date today = dfBlog.parse(dato);
		
			return dfPDF.format(today);
		} catch (ParseException e) {
			logger.severe(e.getMessage());
			
		}
		
		return "";
	}

}
