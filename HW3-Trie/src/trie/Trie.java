package trie;
import java.util.ArrayList;

/**
 * This class implements a Trie. 
 * @author Prachiti Atigre
 */
public class Trie {
	
	// prevent instantiation
	private Trie() { }
	
	/**
	 * Builds a trie by inserting all words in the input array, one at a time,
	 * in sequence FROM FIRST TO LAST. (The sequence is IMPORTANT!)
	 * The words in the input array are all lower case.
	 * 
	 * @param allWords Input array of words (lowercase) to be inserted.
	 * @return Root of trie with all words inserted from the input array
	 */
	public static TrieNode buildTrie(String[] allWords) 
	{
		/** COMPLETE THIS METHOD **/
		
		// FOLLOWING LINE IS A PLACEHOLDER TO ENSURE COMPILATION
		// MODIFY IT AS NEEDED FOR YOUR IMPLEMENTATION
		
		TrieNode root = new TrieNode(null, null, null); //empty root
		
		if(allWords.length == 0) //no words, empty;
		{
			return root; //returns nothing because there it is empty
		}
		
		else //not empty
		{
			root.firstChild = new TrieNode(new Indexes(0, (short)0, (short)(allWords[0].length() - 1)), null, null); //(integer, short, short) therefore, casting
	
			for (int i = 1; i < allWords.length; i++) //loop through words
			{
				insertNode(root.firstChild, allWords, i, 0, root); //send that word to insert node which will then work on where it goes
			}
			return root;
		}
	}
	
	private static void insertNode(TrieNode ptr, String[] allWords, int addIndex, int charIndex, TrieNode parent) 
	{	
		String addWord = allWords[addIndex];
		String ptrWord = allWords[ptr.substr.wordIndex];
		
		boolean matchingSub = false;
		
		while ((charIndex <= ptr.substr.endIndex) && (ptrWord.charAt(charIndex) == addWord.charAt(charIndex)) && (charIndex < addWord.length())) 
		{
			matchingSub = true; //check if there is are matching characters in two strings ^^ becomes true only when meets above conditions
			charIndex++; //keep looping until they don't match
		}
		
		if (!matchingSub) //if there are no common characters in the two strings
		{ 
			if (ptr.sibling != null) //if no common characters, there move to the next sibling, repeat (if there is a sibling)
			{
				insertNode(ptr.sibling, allWords, addIndex, charIndex, parent);
			}
			else //if there is no sibling, create sibling
			{
				Indexes temp = new Indexes(addIndex, (short)charIndex, (short)(addWord.length() - 1));
				ptr.sibling = new TrieNode(temp, null, null);
				return;
			}
		}
		
		else 
		{ //if there are common characters in the two strings
			if ((ptr.firstChild != null) && (charIndex - 1 < ptr.substr.endIndex)) //check if first child is not null & if previous index is less than the last index
			{
				Indexes temp = new Indexes(ptr.substr.wordIndex, ptr.substr.startIndex, (short)(charIndex - 1));
				TrieNode tempNode = new TrieNode(temp, ptr, ptr.sibling);
				ptr.substr.startIndex = (short)(tempNode.substr.endIndex + 1);
				
				if (parent.firstChild == ptr) 
				{
					parent.firstChild = tempNode;
				}
				else 
				{
					TrieNode tempPtr = parent.firstChild;
					
					while (tempPtr.sibling != ptr && tempPtr.sibling != null) 
					{
						tempPtr = tempPtr.sibling;
					}
					tempPtr.sibling = tempNode;
				}
				
				Indexes newWordIndex = new Indexes(addIndex, (short)charIndex, (short)(addWord.length() - 1));
				ptr.sibling = new TrieNode(newWordIndex, null, null);
				
				return;
			}
			
			if (ptr.firstChild == null) 
			{
				short tempEndIndex = ptr.substr.endIndex;
				ptr.substr.endIndex = (short)(charIndex - 1);
				
				Indexes oldChildIndex = new Indexes(ptr.substr.wordIndex, (short)charIndex, tempEndIndex);
				
				short endIndexWordToAdd = (short)(addWord.length() - 1);
				Indexes newChildIndex = new Indexes(addIndex, (short)charIndex, endIndexWordToAdd);
				
				TrieNode firstChild = new TrieNode(oldChildIndex, null, null);
				TrieNode secondChild = new TrieNode(newChildIndex, null, null);
				
				firstChild.sibling = secondChild;
				ptr.firstChild = firstChild;
				
				return;
			}
			else 
			{
				insertNode(ptr.firstChild, allWords, addIndex, charIndex, ptr);
			}
		}
	}
		
	//this checks to find the longest common prefix between two strings with a common prefix
	private static int findCommonPrefix(String word1, String word2) 
	{
		int lengthOfCommonPrefix = 0;
		int minLength = Math.min(word1.length(), word2.length());
		
		for (int i = 0; i < minLength; i++) 
		{	
			if (word1.charAt(0) != word2.charAt(0)) 
			{
				return -1;
			}
			
			if (word1.charAt(i) == word2.charAt(i)) 
			{
				lengthOfCommonPrefix++;
			}
			else 
			{
				return lengthOfCommonPrefix;
			}
		}
		return lengthOfCommonPrefix;
	}
	
	private static ArrayList<TrieNode> searchLeaf(TrieNode pointer, ArrayList<TrieNode> matches, String[]allWords, int minLength, String prefix)
	{
		TrieNode ptr = pointer;
		
		while(ptr != null) 
		{
			String word = allWords[ptr.substr.wordIndex].substring(0, ptr.substr.endIndex + 1);
			int commonPrefix = findCommonPrefix(word, prefix);
		
			if(commonPrefix == -1) 
			{
				ptr = ptr.sibling;
			}
			else
			{
				if(commonPrefix < minLength) 
				{
					if(ptr.firstChild != null) 
					{
						matches = searchLeaf(ptr.firstChild, matches, allWords, minLength, prefix);
						
						if(matches == null) 
						{
							matches = new ArrayList<TrieNode>();
						}
						ptr = ptr.sibling;
					}
					else 
					{
						ptr = ptr.sibling;
					}
				}
				
				else 
				{
					if(ptr.firstChild != null) 
					{
						matches = searchLeaf(ptr.firstChild, matches, allWords, minLength, prefix);
						ptr = ptr.sibling;
						
						if(matches == null) 
						{
							matches = new ArrayList<TrieNode>();
						}
					}
					else 
					{
						matches.add(ptr);
						ptr = ptr.sibling;
					}	
				}	
			}
		}
		if (matches.isEmpty() == true || matches == null) 
		{
			return null;
		}
		return matches;
	}
	
	/**
	 * Given a trie, returns the "completion list" for a prefix, i.e. all the leaf nodes in the 
	 * trie whose words start with this prefix. 
	 * For instance, if the trie had the words "bear", "bull", "stock", and "bell",
	 * the completion list for prefix "b" would be the leaf nodes that hold "bear", "bull", and "bell"; 
	 * for prefix "be", the completion would be the leaf nodes that hold "bear" and "bell", 
	 * and for prefix "bell", completion would be the leaf node that holds "bell". 
	 * (The last example shows that an input prefix can be an entire word.) 
	 * The order of returned leaf nodes DOES NOT MATTER. So, for prefix "be",
	 * the returned list of leaf nodes can be either hold [bear,bell] or [bell,bear].
	 *
	 * @param root Root of Trie that stores all words to search on for completion lists
	 * @param allWords Array of words that have been inserted into the trie
	 * @param prefix Prefix to be completed with words in trie
	 * @return List of all leaf nodes in trie that hold words that start with the prefix, 
	 * 			order of leaf nodes does not matter.
	 *         If there is no word in the tree that has this prefix, null is returned.
	 */
	public static ArrayList<TrieNode> completionList(TrieNode root, String[] allWords, String prefix) 
	{
		/** COMPLETE THIS METHOD **/
		// FOLLOWING LINE IS A PLACEHOLDER TO ENSURE COMPILATION
		// MODIFY IT AS NEEDED FOR YOUR IMPLEMENTATION
		
		ArrayList<TrieNode> matches = new ArrayList<TrieNode>();
		TrieNode ptr = root.firstChild;
		int minLength = prefix.length();

		return searchLeaf(ptr, matches, allWords, minLength, prefix);	
	}
	
	public static void print(TrieNode root, String[] allWords) {
		System.out.println("\nTRIE\n");
		print(root, 1, allWords);
	}
	
	private static void print(TrieNode root, int indent, String[] words) {
		if (root == null) {
			return;
		}
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		
		if (root.substr != null) {
			String pre = words[root.substr.wordIndex]
							.substring(0, root.substr.endIndex+1);
			System.out.println("      " + pre);
		}
		
		for (int i=0; i < indent-1; i++) {
			System.out.print("    ");
		}
		System.out.print(" ---");
		if (root.substr == null) {
			System.out.println("root");
		} else {
			System.out.println(root.substr);
		}
		
		for (TrieNode ptr=root.firstChild; ptr != null; ptr=ptr.sibling) {
			for (int i=0; i < indent-1; i++) {
				System.out.print("    ");
			}
			System.out.println("     |");
			print(ptr, indent+1, words);
		}
	}
 }
