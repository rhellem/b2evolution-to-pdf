package org.hellem.log;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.hellem.util.Consts;

/**
 * @author Rune Hellem
 * 
 */
public class LogStarter {

	public static void configureLogger() {
		Logger logger = Logger.getLogger(Consts.B2TO_PDF_LOG);
		FileHandler fh;

		try {

			// This block configure the logger with handler and formatter
			File file = new File(Consts.OUTPUTFOLDER);
			// In case the directory does not exits
			file.mkdir();
			fh = new FileHandler(Consts.OUTPUTFOLDER + Consts.FILE_SEPARATOR
					+ Consts.B2TO_PDF_LOG + Consts.LOGEXTENSION, true);
			logger.addHandler(fh);

			logger.setLevel(Level.parse(Consts.LOGLEVEL));
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @param startMilliseconds
	 * @return true if diff between NOW and startMilliseconds is equal to or
	 *         greater than Const.LOG_PROGRESS_SECONDS
	 */
	public static boolean logProgress(long startMilliseconds) {
		Date dateNow = Calendar.getInstance().getTime();
		long nowMilliseconds = dateNow.getTime();
		long difference = nowMilliseconds - startMilliseconds;
		
		if (TimeUnit.MILLISECONDS.toSeconds(difference) >= Consts.LOG_PROGRESS_SECONDS) {
		
			return true;
		}
		return false;
	}

}
