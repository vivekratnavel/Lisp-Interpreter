package myint;
import java.io.*;
import java.util.Scanner;

import myint.MyScanner;

/**
 * 
 */

/**
 * @author Vivek Ratnavel Subramanian
 *
 */
public class MyInterpreter {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//System.out.println("Arg1: " + args[0]);
		//System.out.println("Arg2: " + args[1]);
		/*try {
			System.setIn(new FileInputStream(args[0]));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		MyScanner myScanner = new MyScanner();
		myScanner.scan();
	}

}
