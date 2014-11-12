package com.shale.client.semantics;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;

public class WordNet {

	public void testDictionary() throws IOException {
		// construct the URL to the Wordnet dictionary directory
		String wnhome = "yo"; //System.getenv(" WNHOME ");
		String path = wnhome + File.separator + " dict ";
		System.out.println(path);
		URL url = new URL(" file ", null, path);
		// construct the dictionary object and open it
		IDictionary dict = new Dictionary(url);
		dict.open();
		// look up first sense of the word " dog "
		IIndexWord idxWord = dict.getIndexWord(" dog ", POS.NOUN);
		IWordID wordID = idxWord.getWordIDs().get(0);
		IWord word = dict.getWord(wordID);
		System.out.println(" Id = " + wordID);
		System.out.println(" Lemma = " + word.getLemma());
		System.out.println(" Gloss = " + word.getSynset().getGloss());
	}

}
