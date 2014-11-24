package myint;

import java.util.ArrayList;
import java.util.HashSet;

public class Interpreter {

	//public BinaryTree dList = new BinaryTree(tokenCategories.ATOM, "NIL");
	public BinaryTree tree;
	public Interpreter(BinaryTree tree) {
		// TODO Auto-generated constructor stub
		this.tree = tree;
	}
	
	public Interpreter() {
		
	}
	
	public static BinaryTree car(BinaryTree tree) {
		BinaryTree result = tree;
		if(tree != null) {
			if(atom(tree)) {
				System.out.println("ERROR: Cannot perform CAR on Atom.");
				System.exit(1);
			} else {
				if(tree.left != null) {
					result = tree.left;
				} else {
					System.out.println("ERROR: Cannot perform CAR on the expression.");
					System.exit(1);
				}
			}
		}
		return result;
	}
	
	public static BinaryTree cdr(BinaryTree tree) {
		BinaryTree result = tree;
		if(tree != null) {
			if(atom(tree)) {
				System.out.println("ERROR: Cannot perform CDR on Atom.");
				System.exit(1);
			} else {
				if(tree.right != null) {
					result = tree.right;
				} else {
					System.out.println("ERROR: Cannot perform CDR on the expression.");
					System.exit(1);
				}
			}
		}
		return result;
	}
	
	public static BinaryTree cons(BinaryTree left, BinaryTree right) {
		BinaryTree root = new BinaryTree();
		if(left == null && right == null) {
			System.out.println("ERROR: Invalid inputs for CONS operation.");
			System.exit(1);
		} else if(left == null) {
			//System.out.println("Left is NULL...");
			root = right;
		} else if(right == null) {
			//System.out.println("Right is NULL...");
			root = left;
		} else {
			//System.out.println("Both not NULL...");
			root.left = left;
			root.right = right;
		}
		return root;
	}
	
	public static boolean atom(BinaryTree tree) {
		boolean isAtom = false;
		if(tree != null && tree.tokenCat == tokenCategories.ATOM) {
			isAtom = true;
		}
		return isAtom;
	}
	
	public static boolean isNull(BinaryTree tree) {
		boolean isNull = false;
		if(atom(tree) && tree.tokenValue.equalsIgnoreCase("NIL")) {
			isNull = true;
		}
		return isNull;
	}
	
	public BinaryTree myInt(BinaryTree tree) {
		return eval(tree, new BinaryTree(tokenCategories.ATOM, "NIL"), Global.getdList());
	}
	
	public BinaryTree eval(BinaryTree tree, BinaryTree aList, BinaryTree dList) {
		/*System.out.println("Printing tree inside eval...");
		Interpreter.printTree(tree);
		if(aList != null) {
			System.out.println("Printing aList inside eval...");
			Interpreter.printTree(aList);	
		}*/
		BinaryTree resultantTree = null;
		if(atom(tree)) {
			//System.out.println("Inside Eval... Atom: " + tree.tokenValue);
			if(tree.tokenValue.equalsIgnoreCase("T") || isNull(tree) || isInt(tree)) {
				resultantTree = tree;
			} else if(bound(tree, aList)) {
				//System.out.println("Inside bound...");
				resultantTree = getval(tree, aList);
			} else {
				System.out.println("ERROR: Unbound variable.");
				System.exit(1);
			}
		} else {
			// Exp is list
			int numberOfParams = getNumberOfParams(cdr(tree));
			if(car(tree).tokenValue.equalsIgnoreCase("QUOTE")) {
				if(numberOfParams != 1) {
					System.out.println("ERROR: Only one parameter required for QUOTE operation.");
					System.exit(1);
				}
				resultantTree = car(cdr(tree));
			} else if(car(tree).tokenValue.equalsIgnoreCase("COND")) {
				resultantTree = evcon(cdr(tree), aList, dList);
			} else if(car(tree).tokenValue.equalsIgnoreCase("DEFUN")) {
				// Validate the input
				
				//this.dList = addPairs(cons(car(cdr(tree)), new BinaryTree(tokenCategories.ATOM, "NIL")), cdr(cdr(tree)), this.dList);
				
				/*
				 * dList should be populated in this format
				 * (Func_name . (param_list . body) )
				 */
				if(numberOfParams != 3) {
					System.out.println("ERROR: Three parameters required for DEFUN operation.");
					System.exit(1);
				}
				if(checkUserDefinedFunction(cdr(tree))) {
					Global.setdList(cons( cons( car(cdr(tree)), cons( car(cdr(cdr(tree))), car(cdr(cdr(cdr(tree)))) ) ), Global.getdList()));
					//System.out.println("Printing dList...");
					//Interpreter.computeIsList(Global.getdList());
					//Interpreter.printTree(Global.getdList());
					resultantTree = car(cdr(tree));
				} else {
					System.out.println("ERROR: Cannot define function with function name " + car(cdr(tree)).tokenValue);
					System.exit(1);
				}
			} else {
				resultantTree = apply(car(tree), evlist(cdr(tree), aList, dList), aList, dList);
			}
		}
		return resultantTree;
	}
	
	public BinaryTree apply(BinaryTree tree, BinaryTree params, BinaryTree aList, BinaryTree dList) {
		//System.out.println("Printing tree inside apply...");
		//Interpreter.printTree(tree);
		/*if(params != null) {
			System.out.println("Printing params inside apply...");
			Interpreter.printTree(params);
		}*/
		if(atom(tree)) {
			int numberOfParams = getNumberOfParams(params);
			//System.out.println("Number of params: " + numberOfParams);
			if(tree.tokenValue.equalsIgnoreCase("CAR")) {
				if(numberOfParams != 1) {
					System.out.println("ERROR: Only one parameter is expected for CAR operation.");
					System.exit(1);
				}
				return car(car(params));
			} else if(tree.tokenValue.equalsIgnoreCase("CDR")) {
				if(numberOfParams != 1) {
					System.out.println("ERROR: Only one parameter is expected for CDR operation.");
					System.exit(1);
				}
				return cdr(car(params));
			} else if(tree.tokenValue.equalsIgnoreCase("CONS")) {
				if(numberOfParams != 2) {
					System.out.println("ERROR: Two parameters required for CONS operation.");
					System.exit(1);
				}
				return cons(car(params), car(cdr(params)));
			} else if(tree.tokenValue.equalsIgnoreCase("ATOM")) {
				if(numberOfParams != 1) {
					System.out.println("ERROR: Only one parameter is expected for ATOM operation.");
					System.exit(1);
				}
				if(atom(car(params))) {
					return new BinaryTree(tokenCategories.ATOM,"T");
				} else {
					return new BinaryTree(tokenCategories.ATOM, "NIL");
				}
			} else if(tree.tokenValue.equalsIgnoreCase("EQ")) {
				if(numberOfParams != 2) {
					System.out.println("ERROR: Two parameters required for EQ operation.");
					System.exit(1);
				}
				if(eq(car(params), car(cdr(params)))) {
					return new BinaryTree(tokenCategories.ATOM,"T");
				} else {
					return new BinaryTree(tokenCategories.ATOM, "NIL");
				}
			} else if(tree.tokenValue.equalsIgnoreCase("PLUS")) {
				if(numberOfParams != 2) {
					System.out.println("ERROR: Two parameters required for PLUS operation.");
					System.exit(1);
				}
				return plus(car(params), car(cdr(params)));
			} else if(tree.tokenValue.equalsIgnoreCase("MINUS")) {
				if(numberOfParams != 2) {
					System.out.println("ERROR: Two parameters required for MINUS operation.");
					System.exit(1);
				}
				return minus(car(params), car(cdr(params)));
			} else if(tree.tokenValue.equalsIgnoreCase("NULL")) {
				if(numberOfParams != 1) {
					System.out.println("ERROR: Only one parameter is required for NULL operation.");
					System.exit(1);
				}
				if(isNull(car(params))) {
					return new BinaryTree(tokenCategories.ATOM,"T");
				} else {
					return new BinaryTree(tokenCategories.ATOM, "NIL");
				}
			} else if(tree.tokenValue.equalsIgnoreCase("INT")) {
				if(numberOfParams != 1) {
					System.out.println("ERROR: Only one parameter is required for INT operation.");
					System.exit(1);
				}
				if(isInt(car(params))) {
					return new BinaryTree(tokenCategories.ATOM,"T");
				} else {
					return new BinaryTree(tokenCategories.ATOM, "NIL");
				}
			} else if(tree.tokenValue.equalsIgnoreCase("TIMES")) {
				if(numberOfParams != 2) {
					System.out.println("ERROR: Two parameters are required for TIMES operation.");
					System.exit(1);
				}
				return times(car(params), car(cdr(params)));
			} else if(tree.tokenValue.equalsIgnoreCase("QUOTIENT")) {
				if(numberOfParams != 2) {
					System.out.println("ERROR: Two parameters are required for QUOTIENT operation.");
					System.exit(1);
				}
				return quotient(car(params), car(cdr(params)));
			} else if(tree.tokenValue.equalsIgnoreCase("REMAINDER")) {
				if(numberOfParams != 2) {
					System.out.println("ERROR: Two parameters are required for REMAINDER operation.");
					System.exit(1);
				}
				return remainder(car(params), car(cdr(params)));
			} else if(tree.tokenValue.equalsIgnoreCase("LESS")) {
				if(numberOfParams != 2) {
					System.out.println("ERROR: Two parameters are required for LESS operation.");
					System.exit(1);
				}
				return less(car(params), car(cdr(params)));
			} else if(tree.tokenValue.equalsIgnoreCase("GREATER")) {
				if(numberOfParams != 2) {
					System.out.println("ERROR: Two parameters are required for GREATER operation.");
					System.exit(1);
				}
				return greater(car(params), car(cdr(params)));
			} 
			else {
				// User defined functions
				int numParamsUserDef = getNumberOfParams(car(getval(tree, dList)));
				if(numberOfParams != numParamsUserDef) {
					System.out.println("ERROR: Number of parameters do no match.");
					System.exit(1);
				}
				//System.out.println("Applying user defined Functions...");
				return eval(cdr(getval(tree,dList)), addPairs(car(getval(tree, dList)), params, aList), dList);
			}
		} else {
			System.out.println("ERROR: Error in apply function.");
			System.exit(1);
		}
		return null;
	}
	
	public BinaryTree evcon(BinaryTree tree, BinaryTree aList, BinaryTree dList) {
		if(isNull(tree)) {
			System.out.println("ERROR: Expression is null.");
			System.exit(1);
		} else if(eval(car(car(tree)), aList, dList).tokenValue.equalsIgnoreCase("T")) {
			return eval(car(cdr(car(tree))), aList, dList);
		} else {
			return evcon(cdr(tree), aList, dList);
		}
		return null;
	}
	
	/**
	 * evlist[x, a, d] = [
	 * 		null[x] ->  NIL |
	 * 		T -> cons[eval[car[x], a, d], evlist[cdr[x], a, d] ]
	 * 	]
	 * @param tree
	 * @param aList
	 * @param dList
	 * @return
	 */
	public BinaryTree evlist(BinaryTree tree, BinaryTree aList, BinaryTree dList) {
		if(isNull(tree)) {
			return new BinaryTree(tokenCategories.ATOM, "NIL");
		} else {
			return cons(eval(car(tree), aList, dList), evlist(cdr(tree), aList, dList));
		}
	}
	
	public static boolean eq(BinaryTree left, BinaryTree right) {
		boolean isEq = false;
		if(atom(left) && atom(right)) {
			//System.out.println("Left: " + left.tokenValue);
			//System.out.println("Right: " + right.tokenValue);
			if(isInt(left) && isInt(right)) {
				if(Integer.parseInt(left.tokenValue) == Integer.parseInt(right.tokenValue)) {
					isEq = true;
				}
			} else if(left.tokenValue.equalsIgnoreCase(right.tokenValue)) {
				isEq = true;
			}
		} else {
			System.out.println("ERROR: EQ cannot be applied on lists or empty atoms.");
			System.exit(1);
		}
		return isEq;
	}
	
	/**
	 * 
	 * 
		bound [var, list] = [
			null [list] → NIL |
			eq [var, caar[list]] → T |
			T → bound [var, cdr [list]]
		]
	 * @param tree
	 * @param aList
	 * @return
	 */
	public static boolean bound(BinaryTree tree, BinaryTree aList) {
		//System.out.println("Printing tree inside bound...");
		//System.out.println("Value: " + tree.tokenValue);
		//Interpreter.printTree(aList);
		boolean isBound = false;
		if(atom(tree)) {
			//System.out.println("Checking for var: " + tree.tokenValue);
			if(isNull(aList)) {
				return false;
			} else if(eq(tree, car(car(aList)))) {
				return true;
			} else {
				return bound(tree, cdr(aList));
			}
		} else {
			System.out.println("ERROR: Cannot perform BOUND for list.");
			System.exit(1);
		}
		return isBound;
	}
	
	/**
	 * 
		getval [var, list] = [
			eq [var, caar [list]] → cdar [list] |
			T → getval [var, cdr[list]]
		]
	 * @param tree
	 * @param aList
	 * @return
	 */
	public static BinaryTree getval(BinaryTree tree, BinaryTree aList) {
		if(atom(tree)) {
			//System.out.println("Printing tree inside getval...");
			//System.out.println("Value: " + tree.tokenValue);
			//Interpreter.printTree(aList);
			//if(bound(tree, aList)) {
				if(eq(tree, car(car(aList)))) {
					//System.out.println("Returning getVal..");
					//Interpreter.printTree(cdr(car(aList)));
					return cdr(car(aList));
				} else {
					return getval(tree, cdr(aList));
				}
			/*} else {
				System.out.println("ERROR: Variable is not bound. Cannot get value.");
			}*/
		} else {
			System.out.println("ERROR: Cannot perform GETVAL operation for list.");
			System.exit(1);
		}
		return null;
	}
	
	/**
	 * 
		addpairs [varlist, valuelist, oldlist] = [
			null [varlist] → oldlist |
			T → cons[ cons[car[varlist], car[valuelist], addpairs[cdr[varlist], cdr [valuelist], oldlist]]
		] 
	 * @param varList
	 * @param valueList
	 * @param oldList
	 * @return
	 */
	public static BinaryTree addPairs(BinaryTree varList, BinaryTree valueList, BinaryTree oldList) {
		//System.out.println("Printing varList inside Addpairs...");
		//Interpreter.computeIsList(varList);
		//Interpreter.printTree(varList);
		if(isNull(varList)) {
			//System.out.println("Returning oldlist...");
			return oldList;
		} else {
			return (cons(cons(car(varList), car(valueList)), addPairs(cdr(varList), cdr(valueList), oldList)));
		}
	}
	
	public static boolean isInt(BinaryTree tree) {
		boolean isInt = false;
		try {
			if(atom(tree)) {
				int t = Integer.parseInt(tree.tokenValue);
				isInt = true;
			}
		} catch(java.lang.NumberFormatException e) {
			isInt = false;
		}
		return isInt;
	}
	
	public static boolean isRightMostLeafNil(BinaryTree tree) {
		if(tree.right == null && tree.left == null) {
			if(tree.tokenValue.equalsIgnoreCase("NIL")) {
				return true;
			} else {
				return false;
			}
		} else if(tree.right != null){
			return isRightMostLeafNil(tree.right);
		}
		return false;
	}
	
	public static BinaryTree plus(BinaryTree left, BinaryTree right) {
		BinaryTree result = new BinaryTree();
		if(atom(left) && atom(right)) {
			if(isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.tokenValue);
				int rightVal = Integer.parseInt(right.tokenValue);
				int res = leftVal + rightVal;
				result.tokenCat = tokenCategories.ATOM;
				result.tokenValue = Integer.toString(res);
			} else {
				System.out.println("ERROR: Cannot perform PLUS operation on literal atoms.");
				System.exit(1);
			}
		} else {
			System.out.println("ERROR: Cannot perform PLUS operation on Non atoms.");
			System.exit(1);
		}
		return result;
	}
	
	public static BinaryTree minus(BinaryTree left, BinaryTree right) {
		BinaryTree result = new BinaryTree();
		if(atom(left) && atom(right)) {
			if(isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.tokenValue);
				int rightVal = Integer.parseInt(right.tokenValue);
				int res = leftVal - rightVal;
				result.tokenCat = tokenCategories.ATOM;
				result.tokenValue = Integer.toString(res);
			} else {
				System.out.println("ERROR: Cannot perform MINUS operation on literal atoms.");
				System.exit(1);
			}
		} else {
			System.out.println("ERROR: Cannot perform MINUS operation on Non atoms.");
			System.exit(1);
		}
		return result;
	}
	
	public static BinaryTree times(BinaryTree left, BinaryTree right) {
		BinaryTree result = new BinaryTree();
		if(atom(left) && atom(right)) {
			if(isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.tokenValue);
				int rightVal = Integer.parseInt(right.tokenValue);
				int res = leftVal * rightVal;
				result.tokenCat = tokenCategories.ATOM;
				result.tokenValue = Integer.toString(res);
			} else {
				System.out.println("ERROR: Cannot perform TIMES operation on literal atoms.");
				System.exit(1);
			}
		} else {
			System.out.println("ERROR: Cannot perform TIMES operation on Non atoms.");
			System.exit(1);
		}
		return result;
	}
	
	public static BinaryTree quotient(BinaryTree left, BinaryTree right) {
		BinaryTree result = new BinaryTree();
		if(atom(left) && atom(right)) {
			if(isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.tokenValue);
				int rightVal = Integer.parseInt(right.tokenValue);
				int res;
				if(rightVal == 0) {
					res = 0;
				} else {
					res = leftVal/rightVal;
				}
				result.tokenCat = tokenCategories.ATOM;
				result.tokenValue = Integer.toString(res);
			} else {
				System.out.println("ERROR: Cannot perform QUOTIENT operation on literal atoms.");
				System.exit(1);
			}
		} else {
			System.out.println("ERROR: Cannot perform QUOTIENT operation on Non atoms.");
			System.exit(1);
		}
		return result;
	}
	
	public static BinaryTree remainder(BinaryTree left, BinaryTree right) {
		BinaryTree result = new BinaryTree();
		if(atom(left) && atom(right)) {
			if(isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.tokenValue);
				int rightVal = Integer.parseInt(right.tokenValue);
				int res = leftVal % rightVal;
				result.tokenCat = tokenCategories.ATOM;
				result.tokenValue = Integer.toString(res);
			} else {
				System.out.println("ERROR: Cannot perform REMAINDER operation on literal atoms.");
				System.exit(1);
			}
		} else {
			System.out.println("ERROR: Cannot perform REMAINDER operation on Non atoms.");
			System.exit(1);
		}
		return result;
	}
	
	public static BinaryTree less(BinaryTree left, BinaryTree right) {
		BinaryTree result = new BinaryTree();
		if(atom(left) && atom(right)) {
			if(isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.tokenValue);
				int rightVal = Integer.parseInt(right.tokenValue);
				result.tokenCat = tokenCategories.ATOM;
				if(leftVal < rightVal) {
					result.tokenValue = "T";
				} else {
					result.tokenValue = "NIL";
				}
			} else {
				System.out.println("ERROR: Cannot perform TIMES operation on literal atoms.");
				System.exit(1);
			}
		} else {
			System.out.println("ERROR: Cannot perform TIMES operation on Non atoms.");
			System.exit(1);
		}
		return result;
	}
	
	public static BinaryTree greater(BinaryTree left, BinaryTree right) {
		BinaryTree result = new BinaryTree();
		if(atom(left) && atom(right)) {
			if(isInt(left) && isInt(right)) {
				int leftVal = Integer.parseInt(left.tokenValue);
				int rightVal = Integer.parseInt(right.tokenValue);
				result.tokenCat = tokenCategories.ATOM;
				if(leftVal > rightVal) {
					result.tokenValue = "T";
				} else {
					result.tokenValue = "NIL";
				}
			} else {
				System.out.println("ERROR: Cannot perform TIMES operation on literal atoms.");
				System.exit(1);
			}
		} else {
			System.out.println("ERROR: Cannot perform TIMES operation on Non atoms.");
			System.exit(1);
		}
		return result;
	}
	
	public static void printTree(BinaryTree node) {
		if(node != null) {
			computeIsList(node);
			// Uncomment the following two lines to print the tree as a binary tree
			//BTreePrinter bTreePrinter = new BTreePrinter();
			//bTreePrinter.printTree(node);
			
			prettyPrint(node);
			System.out.println("");
		}
	}
	
	private static void prettyPrint(BinaryTree node) {
		if(node.tokenCat == tokenCategories.ATOM) {
			System.out.print(node.tokenValue);
			return;
		}
		if(node.isList) {
			System.out.print("(");
			if(node.left != null) {
				prettyPrint(node.left);
			}
			while(node.right != null && node.right.tokenCat == tokenCategories.NON_TERMINAL) {
				node = node.right;
				System.out.print(" ");
				prettyPrint(node.left);
			}
			System.out.print(")");
			return;
		}
		System.out.print("(");
		//print(node);
		if(node.left != null) {
			prettyPrint(node.left);
		}
		System.out.print(" . ");
		if(node.right != null) {
			//System.out.print(" ");
			prettyPrint(node.right);
		}
		System.out.print(")");
		return;
	}
	
	public static void computeIsList(BinaryTree node) {
		if(node != null && node.tokenCat == tokenCategories.NON_TERMINAL) {
			if(Interpreter.isRightMostLeafNil(node)) {
				node.isList = true;
			}
		}
		if(node.left != null && node.left.tokenCat == tokenCategories.NON_TERMINAL) {
			computeIsList(node.left);
		} 
		while(node.right != null && node.right.tokenCat == tokenCategories.NON_TERMINAL) {
			node = node.right;
			if(node.left != null && node.left.tokenCat == tokenCategories.NON_TERMINAL ) {
				computeIsList(node.left);
			}
		}
		return;
	}

	public static boolean checkUserDefinedFunction(BinaryTree tree) {
		boolean isOk = true;
		if(atom(car(tree))) {
			if(isInt(car(tree))) {
				System.out.println("ERROR: Function name cannot be a numeral atom.");
				System.exit(1);
			}
			String functionName = car(tree).tokenValue;
			String[] builtInFunctions = new String[] {
					"null",
					"plus",
					"minus",
					"car",
					"cdr",
					"atom",
					"cons",
					"int",
					"times",
					"quote",
					"defun",
					"quotient",
					"remainder",
					"less",
					"greater",
					"cond"
					};
			for(String builtInFunction : builtInFunctions) {
				if(functionName.equalsIgnoreCase(builtInFunction)) {
					System.out.println("ERROR: Function name cannot be " + functionName);
					System.exit(1);
				}
			}
			BinaryTree params = car(cdr(tree));
			BinaryTree tempParams = params;
			//ArrayList<String> paramList = new ArrayList<String>();
			HashSet<String> paramList = new HashSet<String>();
			int count = 0;
			while(!isNull(tempParams)) {
				BinaryTree tmp = car(tempParams);
				if(atom(tmp)) {
					if(isInt(tmp)) {
						System.out.println("ERROR: Parameter name cannot be a numeral atom.");
						System.exit(1);
					}
					String paramName = tmp.tokenValue;
					if(paramName.equalsIgnoreCase("T") || paramName.equalsIgnoreCase("NIL")) {
						System.out.println("ERROR: Parameter name cannot be T or NIL.");
						System.exit(1);
					}
					paramList.add(paramName);
					count++;
				} else {
					System.out.println("ERROR: Formal parameter cannot be list.");
					System.exit(1);
				}
				tempParams = cdr(tempParams);
			}
			if(count != paramList.size()) {
				System.out.println("ERROR: Parameter list has duplicate values.");
				System.exit(1);
			}
			BinaryTree body = car(cdr(cdr(tree)));
			
		} else {
			System.out.println("ERROR: Function name cannot be a list.");
			System.exit(1);
		}
		return isOk;
	}
	
	public static int getNumberOfParams(BinaryTree tree) {
		int count = 0;
		if(atom(tree)) {
			return 0;
		}
		while(tree != null) {
			if(tree.left != null) {
				count++;
			}
			tree = tree.right;
		}
		return count;
	}
}
