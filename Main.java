/******************************************************************
   This program searches a given directory for a single-term/permuterm
   query. It then reports which documents in the directory contain
   said search query. It ranks the documents based on the TF-IDF 
   score of each document.
 ******************************************************************/

public class Main
{
  public static void main (String args[]) throws Exception
  {
	  System.out.println();
    IRSystem ir = new IRSystem();
    ir.start();
  }
}
