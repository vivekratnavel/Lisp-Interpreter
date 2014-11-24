package myint;
/**
 * @author Vivek Ratnavel Subramanian
 *
 */

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;
import myint.tokenCategories;

class ScannerException extends Exception {
	private String message = null;
	 
    public ScannerException() {
        super();
    }
 
    public ScannerException(String message) {
        super(message);
        this.message = message;
    }
 
    public ScannerException(Throwable cause) {
        super(cause);
    }
 
    @Override
    public String toString() {
        return message;
    }
 
    @Override
    public String getMessage() {
        return message;
    }	
}

public class MyScanner {
	//private String inputFile = null;
	//private String[] tokenCategories = {"ATOM", "OPEN_PARANTHESIS", "CLOSING_PARANTHESIS", "DOT"}; 
	/**
	 * @param inputFile
	 */
	public MyScanner() {
		super();
		//this.inputFile = inputFile;
	}
	
	public void scan() {
		String line = "";
		ArrayList<String> allTokens = new ArrayList<String>();
		ArrayList<tokenCategories> categories = new ArrayList<tokenCategories>();
		Scanner scan = new Scanner(System.in);
		int countParen = 0;
		while (scan.hasNext()) {
			line = scan.nextLine();
			line = line.toUpperCase();
			StringTokenizer st = new StringTokenizer(line, "()", true);
			while(st.hasMoreTokens()) {
				String token = st.nextToken();
				//System.out.println(token);
				if(token.equalsIgnoreCase("(")) {
					countParen++;
					allTokens.add(token);
					categories.add(tokenCategories.OPEN_PARANTHESIS);
				} else if(token.equalsIgnoreCase(")")) {
					countParen--;
					allTokens.add(token);
					categories.add(tokenCategories.CLOSING_PARANTHESIS);
				} else {
					// Every other atom and string. Example: plus 5 6
					StringTokenizer st2 = new StringTokenizer(token); // Delimiter is whitespace by default
					while(st2.hasMoreTokens()) {
						token = st2.nextToken();
						//System.out.println("Token: " + token);
						switch(token.charAt(0)) {
							case '+' :
							case '-' :
								if(token.length() == 1) {
									System.out.println("ERROR: Invalid symbol detected.");
									System.exit(1);
								} else {
									String tempToken = "";
									int t = 0;
									tempToken = (token.charAt(0)=='+')?token.substring(1):token;
									try {
										t = Integer.parseInt(tempToken);
									} catch(NumberFormatException e) {
										System.out.println("ERROR: Invalid atom detected. Numeric atom cannot contain alphabets.");
										System.exit(1);
									}
									// If its not invalid, then add the token to the token list
									//System.out.println("Integer check: " + Integer.toString(t));
									allTokens.add(Integer.toString(t));
									categories.add(tokenCategories.ATOM);
								}
								break;
							case '.' :
								if(token.length() > 1) {
									// If any character is present immediately after ".", then its an error
									System.out.println("ERROR: Invalid expression. A whitespace is expected after \".\" ");
									System.exit(1);
								} else {
									allTokens.add(token);
									categories.add(tokenCategories.DOT);
								}
								break;
							case '0' :
							case '1' :
							case '2' :
							case '3' :
							case '4' :
							case '5' :
							case '6' :
							case '7' :
							case '8' :
							case '9' :
								// Check if the token is numeric atom, since literal atoms cannot start with numbers
								try {
									int t = Integer.parseInt(token);
								} catch(NumberFormatException e) {
									System.out.println("ERROR: Invalid atom detected. Literal atoms cannot start with numbers.");
									System.exit(1);
								}
								// If its not invalid, then add the token to the token list
								allTokens.add(token);
								categories.add(tokenCategories.ATOM);
								break;
							default:
								// Only literal atoms can reach this point
								// Check if the atom begins with an alphabet
								if(Character.isLetter(token.charAt(0))) {
									// Check if symbol is present in literal atom
									if(token.contains("+") || token.contains("-") || token.contains(".")) {
										System.out.println("ERROR: Invalid atom detected. Literal atoms cannot contain symbols.");
										System.exit(1);
									}
									allTokens.add(token);
									categories.add(tokenCategories.ATOM);
								} else {
									System.out.println("ERROR: Invalid atom detected. Literal atoms can only start with a letter.");
									System.exit(1);
								}
								break;
						}
					}
				}
			}
			
			if(countParen == 0) {
				if(allTokens.size() > 0) {
					// A statement is complete and is ready to be parsed.
					//MyParser parser = new MyParser(categories, allTokens);
					MyParser parser = new MyParser(categories, allTokens);
					while(parser.currentIndex < parser.tokenCat.size()) {
						parser.parse();
					}
					categories = new ArrayList<tokenCategories>();
					allTokens = new ArrayList<String>();
				} else {
					continue;
				}
			} else if(countParen > 0) {
				// If number of left parens is greater, then continue scanning the next line looking for close parenthesis.
				continue;
			} else {
				// If number of right parens is greater, then throw an errror.
				System.out.println("ERROR: Unbalanced or unexpected right parenthesis in expression. Please check.");
				System.exit(1);
			}
			
		    //System.out.println ("Stdout: " + line);
		}
		
		if(countParen > 0) {
			// If number of left parens is greater than 0, after scanning all the lines, then throw an error
			System.out.println("ERROR: Invalid or incomplete expression. Expecting right paranthesis in expression.");
			System.exit(1);
		}
		
		/*try {
			//String currentPath = System.getProperty("user.dir");
			//System.out.println("Current Path: " + currentPath);
			//fis = new FileInputStream(this.inputFile);
			//br = new BufferedReader(new InputStreamReader(fis, Charset.forName("UTF-8")));
			int index = 0;
			while ((line = br.readLine()) != null) {
			    // Deal with the line
				ArrayList<String> tokens = null;
				try {
					tokens = tokenize(line);
					//allTokens.addAll(index, tokens);
					merge(allTokens, tokens);
					//index += tokens.size();
				} catch (ScannerException e) {
					System.out.println(e.getMessage());
				}
			}
			for(int i=0; i< allTokens.size(); i++) {
				System.out.println(allTokens.get(i));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			// Done with the file
			try {
				if(br != null)
					br.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			br = null;
		}*/
		scan.close();
	}
	public static void merge(ArrayList<String> l1, ArrayList<String> l2) {
	    for (int index2 = 0; index2 < l2.size(); index2++) {
	        l1.add(l2.get(index2));
	    }
	}
}
