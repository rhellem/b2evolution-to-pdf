package org.hellem.b2evo;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.hellem.util.Consts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * This class cleans up each blog post, removing any links and/or text related to links
 * The handling of images is assuming that all images are reffered to using links, not the 
 * B2Evolution internal way of adding pictures to a post.
 * 
 * Class is using functinality provided by jSoup http://jsoup.org/ for easy html-handling.
 * 
 * @author Rune Hellem
 *
 */
public class PostTextHandler {

	public static final String divStart = "<div class=\"image_block\">";
	public static final String divStopp = "</div>";
	private static Logger logger = Logger.getLogger(Consts.B2TO_PDF_LOG);
	

	/**
	 * The handler expects text with images in a div-tag of class image_block, with just
	 * one picture pr. div tags splitted pr. image, in the same order as it  
	 * It returns a list of Strings 
	 * @param postText The whole text of the post including url to images
	 * @return List of Strings
	 */
	public List<String> splitPostText(String postText) {

		List<String> tekst = new ArrayList<String>();

		boolean endOfText = false;
		boolean firstCheck = true;
		int startPos = 0;
		int endPos = 0;

		while (!endOfText) {
			// If this is the first run, substring is done from char 0
			// else substring done from the calculated starting position
			if (firstCheck) {
				// Starting position of the first <div
				startPos = postText.indexOf(divStart, (endPos - startPos));
				endPos = postText.indexOf(divStopp, startPos)
						+ divStopp.length();
				// If startPos = 0, then the text starts with an image
				if (startPos > 0) {
					tekst.add(returnPlainPostText(postText.substring(0, startPos)));

				}

				// If any images
				if (startPos != -1) {
					tekst.add(returnUrlToImage(postText.substring(startPos,
							endPos)));
				} else {
					// No Images, just add and break
					tekst.add(returnPlainPostText(postText));
					// Break
					endOfText = true;
					continue;
				}

				firstCheck = false;
			} else {
				// Start searching from the end of </div>
				startPos = endPos;
				endPos = postText.indexOf(divStart, startPos);

				// At the end of all text (-1) or just no more pictures?
				if (endPos != -1) {
					tekst.add(returnPlainPostText(postText.substring(startPos, endPos)));
				} else {
					// Cut all text to the end and break
					tekst.add(returnPlainPostText(postText.substring(startPos)));
					endOfText = true;
					continue;
				}

				// Cut the next div-tag
				startPos = endPos;
				endPos = postText.indexOf(divStopp, startPos)
						+ divStopp.length();
				tekst
						.add(returnUrlToImage(postText.substring(startPos,
								endPos)));
			}
		}

		return tekst;

	}

	/**
	 * Uses http://jsoup.org/ for easy extraction of the content of the <img
	 * src= attribute
	 * 
	 * @param divTagWithUrl
	 * @return
	 */
	private String returnUrlToImage(String divTagWithUrl) {
		Document doc = Jsoup.parse(divTagWithUrl);
		
		logger.log(Level.FINE, "Replacing " + Consts.SEARCH_FOR_DOMAIN + " with " + Consts.REPLACE_WITH_DOMAIN + " in string " + doc.select("img").first().attr("src"));
		return doc.select("img").first().attr("src").replace(Consts.SEARCH_FOR_DOMAIN, Consts.REPLACE_WITH_DOMAIN);
		
	}
	
	private String returnPlainPostText(String rawPostText) {
		// Remove all html in posts
		rawPostText = rawPostText.replaceAll("\\<.*?\\>", "");
		// Remove all youtube-tags in posts
		rawPostText = rawPostText.replaceAll(".youtube].*?tube]", "");
		// Now changed to remove the official B2-Youtube code snippet 
		rawPostText = rawPostText.replaceAll(".video:youtube.*?]", "");

		
		// Other text to be removed
		rawPostText = rawPostText.replaceAll("\\(Større bilde\\)", "");
		rawPostText = rawPostText.replaceAll("(?i)(klikk på bildet, så blir det større)", "");
		rawPostText = rawPostText.replaceAll("(?i)(\\(Klikk på bildet så blir det større\\))", "");
		rawPostText = rawPostText.replaceAll("(?i)(Klikk på bildet så blir det større)", "");
		rawPostText = rawPostText.replaceAll("(?i)(Tips: Klikk på bildet så får du se det i litt større versjon.)", "");
		rawPostText = rawPostText.replaceAll("(?i)(Som før, klikk på bildet for å se større versjon av det)", "");
		rawPostText = rawPostText.replaceAll("(?i)(Klikk på bildet så får du se det i stort format)", "");
		rawPostText = rawPostText.replaceAll("(?i)(Klikk på bildet så får du se større versjon.)", "");
		rawPostText = rawPostText.replaceAll("(?i)(Bildet blir mye større hvis du klikker på det...)", "");
		return rawPostText;
	}

}
