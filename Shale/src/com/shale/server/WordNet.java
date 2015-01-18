package com.shale.server;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.ISynset;
import edu.mit.jwi.item.ISynsetID;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.item.Pointer;

public class WordNet {

	private IDictionary dict;
	private String path;
	private URL url;

	/**
	 * The singleton instance of the class.
	 */
	private static WordNet instance;

	public static WordNet get(String path) {
		if (instance == null) {
			instance = new WordNet(path);
		}
		return instance;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public WordNet(String path) {
		this.path = path;
		try {
			this.url = new URL("file", null, path);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dict = new Dictionary(url);
	}

	public void testDictionary(String path) throws IOException {
		// construct the URL to the Wordnet dictionary directory
		String wnhome = System.getenv(" WNHOME ");

		// String path = wnhome + File . separator + " dict " ;
		// construct the dictionary object and open it
		dict.open();
		// look up first sense of the word " dog "
		IIndexWord idxWord = dict.getIndexWord(" dog ", POS.NOUN);
		IWordID wordID = idxWord.getWordIDs().get(0);
		IWord word = dict.getWord(wordID);
		System.out.println("Testing dictionary @WordNet");
		//System.out.println(" Id = " + wordID);
		//System.out.println(" Lemma = " + word.getLemma());
		//System.out.println(" Gloss = " + word.getSynset().getGloss());
	}

	public void getSynonyms(String word) {
		// look up first sense of the word " dog "
		IIndexWord idxWord = dict.getIndexWord(word, POS.NOUN);
		IWordID wordID = idxWord.getWordIDs().get(0); // 1 st meaning
		IWord wordWN = dict.getWord(wordID);
		ISynset synset = wordWN.getSynset();
		// iterate over words associated with the synset
		for (IWord w : synset.getWords())
			System.out.println(w.getLemma());
	}

	public void getHypernyms (String word) {
		// get the synset
		IIndexWord idxWord = dict.getIndexWord(word , POS.NOUN);
		IWordID wordID = idxWord.getWordIDs().get(0); // 1 st meaning
		IWord wordWN = dict.getWord(wordID);
		ISynset synset = wordWN.getSynset();
		// get the hypernyms
		List<ISynsetID> hypernyms =
		synset.getRelatedSynsets(Pointer.HYPERNYM) ;
		// print out each h y p e r n y m s id and synonyms
		List<IWord> words ;
		for (ISynsetID sid : hypernyms) {
			words = dict.getSynset(sid).getWords();
			System.out.print(sid+" { ");
			for (Iterator<IWord> i = words.iterator(); i.hasNext();) {
				System.out.print(i.next().getLemma());
				if (i.hasNext())
					System.out.print(" , ");
			}
			System.out.println("}");
		}
	}
}
