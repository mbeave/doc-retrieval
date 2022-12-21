/******************************************************************
   This program searches a given directory for a single-term/permuterm
   query. It then reports which documents in the directory contain
   said search query. It ranks the documents based on the TF-IDF 
   score of each document.
 ******************************************************************/

import java.io.*;
import java.util.*;

class IRSystem
{
  public ArrayList<String> wordIndex = new ArrayList<String>(1000); 						//variable size array for "seenBefore" function
  
  public Map<String, String> permutermIndex = new HashMap<>(5000); 							//hashmap index to store permutations of processed strings
  
  public TreeMap<String, String> sortedPermutermIndex = new TreeMap<>();
  
  public TreeMap<String, String> sortedWordIndex = new TreeMap<>();
  
  public Map<String, Integer> documentWordCount = new HashMap<>(3000);						//hashmap that stores each document name and maps that to total word count in each document
  
  public Map<String, ArrayList<String>> wordDocumentMap = new HashMap<>(5000); 				//this hashmap stores and maps each word to the documents that it appears in. e.g. word -> documents (arraylist of documents)
  
  public Map<String, Integer> wordCountMap = new HashMap<>(50);								//this hashmap maps document name to the number of times the query word appears in each document
  
  public TreeMap<Double, String> TF_IDF_Score = new TreeMap<>(Collections.reverseOrder());	//this treemap sorts the scores for each document's TF-IDF score; each document score (key) maps to it's respective document's string (value)
  
  public HashSet<String> distinctDocSet = new HashSet<>(); 									//this set determines the distinct documents that the query word appears in; used for computation of IDF score
  
  public File[] collection;

  public IRSystem()
  {
    collection = getFiles();
  }
  
  public File[] getFiles()
  {
    File[] files = null;
    try
    {
      System.out.println();
      System.out.print("Enter name of a directory> ");
      Scanner scan = new Scanner(System.in);
      File dir = new File(scan.nextLine());
      files = dir.listFiles();
      System.out.println();
    }
    catch (Exception e)
    {
      System.out.println("Caught error in getFiles: " + e.toString());
    }
    return files;
  }


  public String process(String w)
  {
	w = w.toLowerCase(); //converts characters in string to lower case
	
	w = w.replaceAll("[\\p{Punct}&&[^'+-]]", ""); //deletes all punctuation besides apostrophes, hyphens, and plus signs
	
	if (wordIndex.contains(w) == false) {
		
		wordDocumentMap.put(w, new ArrayList<String>());			//if new word then add word to wordDocumentMap and then map to document name
		wordDocumentMap.get(w).add(documentName);
	
		String permuterm = w + '$';				//creation of first permuterm
		
		permutermIndex.put(permuterm, w);       //inserts permuterm into hashmap as a "key, value" pair where the permuterm is the "key" and the original term is the "value"
		
		
		for (int i = 0; i < permuterm.length() - 1; i++) {			
			char firstChar = permuterm.charAt(0);
			
			permuterm = permuterm.substring(1) + firstChar;		//string process to create new permuterm; moves first character of string to end of string
			
			permutermIndex.put(permuterm, w);
		}
	}
	else {
		wordDocumentMap.get(w).add(documentName);
	}
			
    return w;
  }

  public boolean seenBefore(String w)
  {
	
	
	if (wordIndex.isEmpty()) {	//checks if arraylist has any words yet: if not, then add first word
		wordIndex.add(w);
		return false;
	}
	
	
	if (wordIndex.contains(w)) { //check if wordIndex contains word yet; if not, add word and return false
		return true;
	}
	else {
		wordIndex.add(w);
		return false;
	}
	
  }
  
  int wordCount = 0;
  String documentName;
  int documentCount = 0;
  public void start()
  {
    try
    {
      for (File f : collection)
      {
    	wordCount = 0;
    	documentName = f.toString();
    	documentName = documentName.substring(documentName.lastIndexOf("\\"));		//returns document name without path
    	documentName = documentName.substring(1, documentName.indexOf("."));		//cuts off extension from document name; example: "COSC 3315.dat" becomes "COSC 3315"
    	
        Scanner sc = new Scanner(f);
        while (sc.hasNextLine())
        {
          StringTokenizer st = new StringTokenizer(sc.nextLine());
          while (st.hasMoreTokens())
          {
        	wordCount = wordCount + 1;
            String inputWord = st.nextToken();
            //System.out.print(inputWord + "\t");
            String outputWord = process(inputWord);		//processes new word and removes punctuation; also adds permuterms to permuterm index
            //System.out.print(outputWord + "\t");
            
            seenBefore(outputWord);						//adds processed word to wordIndex
            if (st.hasMoreTokens() == false) {
            	documentWordCount.put(documentName, wordCount);		//this if statement checks if there's more tokens in current file; if not, it maps documentName to current wordCount
            }
          }
        }
      }
      //for (Map.Entry<String, Integer> entry: documentWordCount.entrySet()) {				//prints document names and word count in each document
	    //.out.println(entry.getKey() + " -> " + entry.getValue());			
      //}
      //for (Map.Entry<String, ArrayList<String>> entry: wordDocumentMap.entrySet()) {		//prints each word and each document that that word appears in
	    //System.out.println(entry.getKey() + " -> " + entry.getValue());			
      //}
      						
      sortedPermutermIndex.putAll(permutermIndex);	
      																					
      //for (Map.Entry<String, String> entry: sortedPermutermIndex.entrySet()) {			//prints sorted permutation index	
    	  //System.out.println(entry.getKey() + " -> " + entry.getValue());				
      //}
      System.out.println("IR System complete!\n");
      
      Scanner sc = new Scanner(System.in); 
      boolean firstTime = true;
      String keepGoing = "g";
      
      while (!keepGoing.equals("q")) {
    	  firstTime = false;
	  	  System.out.println("Please enter your query:");
	  	  String userWord = sc.next();						
	  	  
	  	  if (userWord.contains("*") == true) {
	  		userWord = userWord.replaceAll("\\*", "\\$");
	  		userWord = userWord + "$";
	  		while (userWord.charAt(0) != '$') {			
				char firstChar = userWord.charAt(0);
				
				userWord = userWord.substring(1) + firstChar;		//string process to create appropriate permuterm; moves first character of string to end of string
			}
			userWord = userWord.substring(1);
	  	  }
	  	  else {
	  		userWord = "$" + userWord;
	  	  }
	  	  
	  	  
	  	  
	  	  String userPermuterm = "";
	  	  char[] userWordCharArray = userWord.toCharArray();
	  	  int userWordLength = userWord.length();
	  	  int min = 2;
	  	  
	  	  for (String key: sortedPermutermIndex.keySet()) {						//this for loop is the main algorithm that determines edit distance between the two given words
	  		  char[] keyCharArray = key.toCharArray();							//it returns closest permuterm to given query term
	  		  int keyLength = key.length();
	  		  
	  		  int dGrid[][] = new int[userWordLength + 1][keyLength + 1];
	  		  
	  		  for (int i = 0; i <= userWordLength; i++) {
	  			  dGrid[i][0] = i;
	  		  }
	  														
	  		  for (int i = 0; i <= keyLength; i++) {
	  			  dGrid[0][i] = i;
	  		  }
	  		  
	  		  for (int i = 1; i <= userWordLength; i++) {							
	  			  for (int j = 1; j <= keyLength; j++) {
	  				  int notEqual = 0;
	  				  if (userWordCharArray[i-1] != keyCharArray[j-1]) {
	  					  notEqual = 1;
	  				  }
	  					
	  				  dGrid[i][j] = Math.min(Math.min(dGrid[i-1][j-1] + notEqual, dGrid[i-1][j] + 1), dGrid[i][j-1] + 1);
	  			  }
	  		  }
	  		  
	  		  if (dGrid[userWordLength][keyLength] < min) {
	  			  userPermuterm = key;
	  			  min = dGrid[userWordLength][keyLength];
	  		  }
	  		  
	  	  }
	  	  
	  	  //System.out.println(userWord);
	  	  
	  	  /*  for (String key: sortedPermutermIndex.keySet()) {
	  		  if (key.contains(userWord)) {							//this is an old algorithm for searching sortedPermutermIndex for the permuterm that is closest match to user query
	  			  if (userWord.length() == key.length()) {
	  				System.out.println(key);
	    			userPermuterm = key;
	    			break;
	  			  }
	  			  else if (userWord.length() == key.length() + 1) {
	  				System.out.println(key);
	  				userPermuterm = key;
	  				break;
	  			  }
	  			System.out.println(key);  
	  		  	userPermuterm = key;  
	  		  }
	  	  }*/
	  	  
	  	  
	  	  String userPermutermValue = permutermIndex.get(userPermuterm);			//this gets the value associated with the permuterm key stored in permutermIndex
	  	  
	  	  ArrayList<String> userDocuments = wordDocumentMap.get(userPermutermValue); //this gets the documents associated with the value that was pulled from the permuterm index
	  	  if (userDocuments == null) {
	  		  System.out.println("\nSorry, no matches.");
	  		  System.out.println("\nPress any key to continue or 'q' to quit");
	  		  keepGoing = sc.next();
	  		  continue;  
	  	  }
	  	  
	  	  //System.out.println(userPermutermValue);
	  	  //System.out.println(userDocuments);
	
	  	  for (String s: userDocuments) {
	  		  if (wordCountMap.containsKey(s)) {
	  			  wordCountMap.put(s, wordCountMap.get(s) + 1);					//this for loop fills wordCountMap with (key, value) pairs; i.e. (document name, # of times query word appears in said document)
	  			  distinctDocSet.add(s);										//in addition it fills distinctDocSet
	  		  }
	  		  else {
	  			  wordCountMap.put(s, 1);
	  			  distinctDocSet.add(s);
	  		  }
	  	  }
	  	  
	  	  double termFreq = 0.0;							//term frequency
	  	  double invDocFreq = 0.0;							//inverse document frequency
	  	  
	  	  for (String s: userDocuments) {											//this for loop computes TFIDF scores for each document puts the scores into TF_IDF_Score treemap (score, document name)
	  		  termFreq = wordCountMap.get(s)/(double) documentWordCount.get(s);
	  		  invDocFreq = 1.0/distinctDocSet.size();
	  		  
	  		  TF_IDF_Score.put(termFreq*invDocFreq, s);
	  		  termFreq = 0.0;
	  		  invDocFreq = 0.0;
	  	  }
	  	 
	  	  System.out.println("Results:");
	  	  for(Map.Entry<Double, String> entry: TF_IDF_Score.entrySet()) {
	  		  System.out.println(entry.getValue());
	  	  }
	  	  
	  	  //System.out.println(TF_IDF_Score);				//uncomment this line to see the TF-IDF scores of each document when this program is run
	  	  //System.out.println(wordCountMap.entrySet());
	      TF_IDF_Score.clear();
  		  System.out.println("\nPress any key to continue or 'q' to quit");
  		  keepGoing = sc.next();
	    }
    }
    catch(Exception e)
    {
      System.out.println("Error in start:  " + e.toString());
    }
  }
}  