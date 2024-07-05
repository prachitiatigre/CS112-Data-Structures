package lse;
import java.io.*;
import java.util.*;

/**
 * This class builds an index of keywords. Each keyword maps to a set of pages in
 * which it occurs, with frequency of occurrence in each page.
 *
 */
public class LittleSearchEngine {
	
	/**
	 * This is a hash table of all keywords. The key is the actual keyword, and the associated value is
	 * an array list of all occurrences of the keyword in documents. The array list is maintained in 
	 * DESCENDING order of frequencies.
	 */
	HashMap<String,ArrayList<Occurrence>> keywordsIndex;
	
	/**
	 * The hash set of all noise words.
	 */
	HashSet<String> noiseWords;
	
	/**
	 * Creates the keyWordsIndex and noiseWords hash tables.
	 */
	public LittleSearchEngine() {
		keywordsIndex = new HashMap<String,ArrayList<Occurrence>>(1000,2.0f);
		noiseWords = new HashSet<String>(100,2.0f);
	}
	
	/**
	 * Scans a document, and loads all keywords found into a hash table of keyword occurrences
	 * in the document. Uses the getKeyWord method to separate keywords from other words.
	 * 
	 * @param docFile Name of the document file to be scanned and loaded
	 * @return Hash table of keywords in the given document, each associated with an Occurrence object
	 * @throws FileNotFoundException If the document file is not found on disk
	 */
	public HashMap<String,Occurrence> loadKeywordsFromDocument(String docFile) throws FileNotFoundException {
		
		Scanner sc = new Scanner(new File(docFile));
		HashMap<String, Occurrence> table_of_keywords = new HashMap<>();

		while(sc.hasNext()) {
			
			String word = sc.next();
			word = word.trim(); //removes spaces
				
			word = getKeyword(word);
				
			if(word != null) {
				
				Occurrence check = table_of_keywords.get(word);
					
				if(check != null) {
					
					check.frequency = check.frequency + 1;
				}
				else {
					
					Occurrence info = new Occurrence (docFile, 1);
					table_of_keywords.put(word,info);
				}
			}
		}
		sc.close();
		return table_of_keywords;
	}

	/**
	 * Merges the keywords for a single document into the master keywordsIndex
	 * hash table. For each keyword, its Occurrence in the current document
	 * must be inserted in the correct place (according to descending order of
	 * frequency) in the same keyword's Occurrence list in the master hash table. 
	 * This is done by calling the insertLastOccurrence method.
	 * 
	 * @param kws Keywords hash table for a document
	 */
	public void mergeKeywords(HashMap<String,Occurrence> kws) {
		
		Set<String> words = kws.keySet();

		for (String s : words) {

			if (keywordsIndex.containsKey(s)) {
				
				ArrayList<Occurrence> occurences = keywordsIndex.get(s);
				occurences.add(kws.get(s));
				insertLastOccurrence(occurences);
			} 
			else {
				
				ArrayList<Occurrence> occurences = new ArrayList<>();
				Occurrence next = kws.get(s);
				occurences.add(next);
				keywordsIndex.put(s, occurences);
			}
		}
	}

	/*
	 * Given a word, returns it as a keyword if it passes the keyword test,
	 * otherwise returns null. A keyword is any word that, after being stripped of any
	 * trailing punctuation(s), consists only of alphabetic letters, and is not
	 * a noise word. All words are treated in a case-INsensitive manner.
	 * 
	 * Punctuation characters are the following: '.', ',', '?', ':', ';' and '!'
	 * NO OTHER CHARACTER SHOULD COUNT AS PUNCTUATION
	 * 
	 * If a word has multiple trailing punctuation characters, they must all be stripped
	 * So "word!!" will become "word", and "word?!?!" will also become "word"
	 * 
	 * See assignment description for examples
	 * 
	 * @param word Candidate word
	 * @return Keyword (word without trailing punctuation, LOWER CASE)
	 */
	public String getKeyword(String word) {
		
        word = word.toLowerCase();
		int original_length = word.length();
        char firstCharacter = word.charAt(0);

		if (word == null || original_length <= 0 || Character.isLetter(firstCharacter) == false) 
            return null;
        
        int firstIndex = 0;
        int lastIndex = 0;

        //traverse and find the first letter 
        for (int i = 0; i < original_length; i++) {
        	
            char character = word.charAt(i); //character at index i
             
            if (Character.isLetter(character) == true) {
            	
                firstIndex = word.indexOf(character);
                break;
            }
        }
        
        //reverse the string so that we can find the last letter 
        String reverse = "";
        for (int i = word.length() - 1; i >= 0; i--) {
            reverse = reverse + word.charAt(i);
        }
        
        //now we can find the last letter similar to how we found the first letter
        for (int i = 0; i < original_length; i++) {
            
        	char character = reverse.charAt(i);
            
            if (Character.isLetter(character) == true) {
            	
                lastIndex = reverse.indexOf(character);
                break;
            }
        }
                
        word = word.substring(firstIndex, original_length - lastIndex); //substring(inclusive, exclusive)
		int new_length = word.length(); //substring length

        //check to see if not a letter
        for (int i = 0; i < new_length; i++) {
       
        	char character = word.charAt(i);
            
            if (Character.isLetter(character) == false) {
                return null;
            }
        } 
                
        if (noiseWords.contains(word) == true) {
        	return null;
        }
        return word;
	}

	/**
	 * Inserts the last occurrence in the parameter list in the correct position in the
	 * list, based on ordering occurrences on descending frequencies. The elements
	 * 0..n-2 in the list are already in the correct order. Insertion is done by
	 * first finding the correct spot using binary search, then inserting at that spot.
	 * 
	 * @param occs List of Occurrences
	 * @return Sequence of mid point indexes in the input list checked by the binary search process,
	 *         null if the size of the input list is 1. This returned array list is only used to test
	 *         your code - it is not used elsewhere in the program.
	 */
	public ArrayList<Integer> insertLastOccurrence(ArrayList<Occurrence> occs) {
		
		ArrayList<Integer> indexes = new ArrayList<Integer>(); 
		
		if(occs.size() <= 1)
			return null;
		
		Occurrence temp = occs.get(occs.size() - 1);
		occs.remove(occs.size() - 1);
		
		int min = occs.size() - 1;
		int max = 0; 
		int mid = 0;
		int midFrequency = 0;
		
		while(min >= max) {
			
			mid = (max+min) / 2;
			midFrequency=occs.get(mid).frequency;
			
			if(midFrequency < temp.frequency) {
				
				min = mid - 1;
				indexes.add(mid);
			}
			if(midFrequency>temp.frequency) {
				
				max = mid + 1;
				indexes.add(mid);
				mid++;
			}
			if(midFrequency == temp.frequency){
				
				indexes.add(mid);
				break;
			}
		}
		occs.add(mid,temp);
		return indexes;
	}

	/**
	 * This method indexes all keywords found in all the input documents. When this
	 * method is done, the keywordsIndex hash table will be filled with all keywords,
	 * each of which is associated with an array list of Occurrence objects, arranged
	 * in decreasing frequencies of occurrence.
	 * 
	 * @param docsFile Name of file that has a list of all the document file names, one name per line
	 * @param noiseWordsFile Name of file that has a list of noise words, one noise word per line
	 * @throws FileNotFoundException If there is a problem locating any of the input files on disk
	 */
	public void makeIndex(String docsFile, String noiseWordsFile) 
	throws FileNotFoundException {
		// load noise words to hash table
		Scanner sc = new Scanner(new File(noiseWordsFile));
		while (sc.hasNext()) {
			String word = sc.next();
			noiseWords.add(word);
		}
		
		// index all keywords
		sc = new Scanner(new File(docsFile));
		while (sc.hasNext()) {
			String docFile = sc.next();
			HashMap<String,Occurrence> kws = loadKeywordsFromDocument(docFile);
			mergeKeywords(kws);
		}
		sc.close();
	}

	/**
	 * Search result for "kw1 or kw2". A document is in the result set if kw1 or kw2 occurs in that
	 * document. Result set is arranged in descending order of document frequencies. 
	 * 
	 * Note that a matching document will only appear once in the result. 
	 * 
	 * Ties in frequency values are broken in favor of the first keyword. 
	 * That is, if kw1 is in doc1 with frequency f1, and kw2 is in doc2 also with the same 
	 * frequency f1, then doc1 will take precedence over doc2 in the result. 
	 * 
	 * The result set is limited to 5 entries. If there are no matches at all, result is null.
	 * 
	 * See assignment description for examples
	 * 
	 * @param kw1 First keyword
	 * @param kw1 Second keyword
	 * @return List of documents in which either kw1 or kw2 occurs, arranged in descending order of
	 *         frequencies. The result size is limited to 5 documents. If there are no matches, 
	 *         returns null or empty array list.
	 */
	public ArrayList<String> top5search(String kw1, String kw2) {
		
		ArrayList<String> topResults = new ArrayList<String>();
		ArrayList<Occurrence> list1 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> list2 = new ArrayList<Occurrence>();
		ArrayList<Occurrence> joint = new ArrayList<Occurrence>();
		
		if (keywordsIndex.containsKey(kw1)) {
			list1 = keywordsIndex.get(kw1);
		}
		
		if (keywordsIndex.containsKey(kw2)) {
			list2 = keywordsIndex.get(kw2);
		}
		
		joint.addAll(list1);
		joint.addAll(list2);
		
		if (list1 != null && list2 != null) {
		
			for (int i = 0; i < joint.size()-1; i++) {
				
				for (int j = 1; j < joint.size()-i; j++) {
					
					if (joint.get(j-1).frequency < joint.get(j).frequency) {
						
						Occurrence temp = joint.get(j-1);
						joint.set(j-1, joint.get(j));
						joint.set(j,  temp);
					}
				}
			}

			for (int i = 0; i < joint.size()-1; i++) {
				
				for (int j = i + 1; j < joint.size(); j++) {
					
					if (joint.get(i).document == joint.get(j).document)
						joint.remove(j);
				}
			}
		}
		while (joint.size() > 5)
			joint.remove(joint.size()-1);
				
		for (Occurrence oc : joint)
			topResults.add(oc.document);

		return topResults;
	}
}