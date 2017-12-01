package org.hellem.itext;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.ConnectException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.hellem.b2evo.BloggPost;
import org.hellem.b2evo.PostTextHandler;
import org.hellem.log.LogStarter;
import org.hellem.mysql.MysqlConnection;
import org.hellem.util.Consts;
import org.hellem.util.PropertyLoader;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;

/**
 * This code is based on an example that was written by Bruno Lowagie. It was an
 * extra example for the book 'iText in Action' by Manning Publications. ISBN:
 * 1932394796 http://www.1t3xt.com/docs/book.php http://www.manning.com/lowagie/
 * 
 * Itext is distributed GNU Affero General Public License version 3, http://www.gnu.org/licenses/
 * 
 * The code itself creates a PDF-document with two columns over several pages, adding pictures as well
 * 
 * The code has a few limitations (so to speak)
 * <ul>
 * <li>It fetches data directly from MYSQL, a local instance has been used when developing. Contenct from
 * main blog is copied using MYSQLDUMP. See http://hellem.org/blog/index.php/2010/06/03/b2evolution-on-my-laptop-and-rtfm?blog=6
 * for more.</li>
 * <li>Code is written to fix my and only my problem, therefore you might not find it well documented, it can
 * be as far from best practice as possible is (hardcoded sql and such), but it does the trick. Still, since there
 * might be someone else out there with the same need as me I share it, assuming that people able to find my code
 * also would be able to understand the logic and change it to their own needs :-)</li>
 * </ul>
 */

public class PdfFromB2Generator extends PropertyLoader {

	Logger logger = Logger.getLogger(Consts.B2TO_PDF_LOG);
	Properties props = null;

	private final static int whatIsThis = 36;
	private final static int distanceFromtop = 36;

	// Define common column values
	private final static int columnSpacing = 20;
	private final static int columnWidth = 240;

	// Define LLX, LLY, URX, URY for each column
	private final static int column1LLX = 30;
	private final static int column1LLY = whatIsThis;
	private final static int column2LLX = column1LLX + columnWidth
			+ columnSpacing;
	private final static int column2LLY = whatIsThis;

	// {upperleftcorner, ?, position to end column, startingposition from top

	/** Definition of two vertical columns */
	public static final float[][] COLUMNS = {
			{ column1LLX, column1LLY, 260,
					PageSize.A4.getHeight() - distanceFromtop },
			{ column2LLX, column2LLY, 520,
					PageSize.A4.getHeight() - distanceFromtop } };

	

	public PdfFromB2Generator() {
				
	}

	/**
	 * the main method, creates the pdf
	 */
	public static void main(String[] args) {
		LogStarter.configureLogger();
		
		new PdfFromB2Generator().createPDF();
		

	}

	/**
	 * Add content to the pdf document, content equals one single post
	 * from the blog.
	 * 
	 * @param ct Content so far...
	 * @param post A single post
	 */
	private void addContent(ColumnText ct, BloggPost post) {

		// Add header and date
		Chunk header = new Chunk(post.getHeader(), new Font(
				Font.FontFamily.HELVETICA, 14));
		header.setUnderline(+3f, -2f);

		ct.addElement(header);
		Chunk formatedDate = new Chunk(post.getFormatedDate(), new Font(
				Font.FontFamily.HELVETICA, 8));
		ct.addElement(formatedDate);

		
		PostTextHandler postTextHandler = new PostTextHandler();
		java.util.List<String> text = postTextHandler.splitPostText(post
				.getTekst());
		Iterator<String> textIterator = text.iterator();
		String lineOfText = null;

		while (textIterator.hasNext()) {
			lineOfText = textIterator.next();

			if (lineOfText.startsWith("http://")) {
				try {
					Image img = Image.getInstance(lineOfText);

					if (img.getWidth() < img.getHeight()) {
						// Not sure why images which is wider is placed to much
						// to the left if aligned middle
						img.setAlignment(Image.ALIGN_MIDDLE);
					}
					if (img.getHeight() >= 640 || img.getWidth() >= 640) {
						logger.log(Level.FINEST,
								"Large picture, scale even more: " + lineOfText
										+ " H: " + img.getHeight() + " W: "
										+ img.getWidth());
						img.scaleToFit(new Float(img.getWidth() * 0.33),
								new Float(img.getHeight() * 0.33));
					} else {

						img.scaleToFit(new Float(img.getWidth() * 0.5),
								new Float(img.getHeight() * 0.5));
					}
					img.setSpacingBefore(3);
					img.setSpacingAfter(3);

					ct.addElement(img);

				} catch (ConnectException e) {
					logger.severe(e.getMessage());

				} catch (MalformedURLException e) {
					logger.severe(e.getMessage());
				} catch (IOException e) {
					logger.severe(e.getMessage());
				} catch (BadElementException e) {
					logger.severe(e.getMessage());
				}
			} else {

				ct.addElement(new Paragraph(lineOfText.trim()));

			}

		}

		ct.addElement(Chunk.NEWLINE);

	}
	
	/**
	 * This is where the magic starts
	 */ 
	public void createPDF() {
		logger.entering(PdfFromB2Generator.class.toString(), "createPDF");
		logger.info("Create the PDF");
		
		
		// Create the document
		Document document = new Document(PageSize.A4);
		try {
			// Create the file to output the content to (relative path)
			PdfWriter writer = PdfWriter.getInstance(document,
					new FileOutputStream(Consts.OUTPUTFOLDER + Consts.FILE_SEPARATOR + Consts.OUTPUTFILE));
			// Prepare to write content
			// document.setPageSize(new Rectangle(594,840));

			document.open();
			PdfContentByte cb = drawColumnlines(writer);
			
			// Read from the database
			MysqlConnection mysqlConnection = new MysqlConnection();
			List<BloggPost> blogEntries = mysqlConnection.getBlogPosts();

			ColumnText ct = new ColumnText(cb);
			int column = 0;
			ct.setSimpleColumn(COLUMNS[column][0], COLUMNS[column][1],
					COLUMNS[column][2], COLUMNS[column][3]);

			int status = ColumnText.START_COLUMN;
			float y;

			// Loop all postings (blogpost) in the blog
			java.util.Date startTime = Calendar.getInstance().getTime();
			for (BloggPost blogpost : blogEntries) {
				// Do this to keep track of progress 
				// while creating the pdf (might take some time)
				if (LogStarter.logProgress(startTime.getTime())) {
					logger.log(Level.INFO, "....creating PDF...have patience...");
					startTime = Calendar.getInstance().getTime();
				}
				// Verify if enough space
				y = ct.getYLine();

				addContent(ct, blogpost);
				status = ct.go();

				// Need a new column?
				while (ColumnText.hasMoreText(status)) {
					column = (column + 1) % 2;
					// Need a new page?
					if (column == 0) {
						document.newPage();
						// Draw a line between the columns
						drawColumnlines(writer);
					}
					// Create the column
					ct.setSimpleColumn(COLUMNS[column][0], COLUMNS[column][1],
							COLUMNS[column][2], COLUMNS[column][3]);
					y = COLUMNS[column][3];
					ct.setYLine(y);
					status = ct.go();
				}
			}
			
			// Add a line referring to itext
			ct.addElement(new Paragraph(Consts.ITEXT_SALUTATION));
			ct.go();

		} catch (DocumentException de) {
			logger.severe(de.getMessage());
		} catch (IOException ioe) {
			logger.severe(ioe.getMessage());		
		}

		// Close and wrap up
		document.close();
		logger.info("Done creating the PDF");
		logger.exiting(PdfFromB2Generator.class.getName(), "createPDF");
	}

	private PdfContentByte drawColumnlines(PdfWriter writer) {
		PdfContentByte cb = writer.getDirectContent();
		cb.setRGBColorStroke(0xC0, 0xC0, 0xC0);
		cb.setLineWidth(2);

		// Draw lines

		cb.moveTo(column1LLX + columnWidth + (columnSpacing / 2),
				distanceFromtop);
		cb.lineTo(column1LLX + columnWidth + (columnSpacing / 2), PageSize.A4
				.getHeight()
				- distanceFromtop);

		cb.stroke();
		return cb;
	}
}
