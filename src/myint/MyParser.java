/**
 * @author Vivek Ratnavel Subramanian
 *
 */
package myint;

import java.util.ArrayList;

public class MyParser {
	public ArrayList<tokenCategories> tokenCat;
	ArrayList<String> tokens;
	public int currentIndex = 0;
	public int parenRemoveCount = 0;
	public MyParser(ArrayList<tokenCategories> tc, ArrayList<String> tokens) {
		super();
		this.tokenCat = tc;
		this.tokens = tokens;
	}
	
	public void parse() {
		BinaryTree tree = parseStart();
		if(tree != null) {
			Interpreter interpreter = new Interpreter();
			BinaryTree result = interpreter.myInt(tree);
			//Interpreter.printTree(result);
			Interpreter.computeIsList(result);
			Interpreter.printTree(result);
		} else {
			System.out.println("ERROR: Parser error.");
			System.exit(1);
		}
	}
	
	/*
	 *  Implementing the following grammar
	 *  <S>::=<E>      <E>::= atom    <E>::=(<X>
	 *  <X>::=<E><Y>   <X>::=) 	      <Y>::=.<Z>
	 *  <Y>::=<R>)     <R>::=Empty    <R>::=<E><R> 
	 *  <Z>::=<E>)
	 */

	// Production <S>::=<E>
	private BinaryTree parseStart() {  
		BinaryTree tree = new BinaryTree();
		BinaryTree eNode = parseExpression();
		if(eNode != null) {
			//tree.left = eNode;
			tree = eNode;
			return tree;
		} else { 
			return null;
		}
	}

	private BinaryTree parseExpression() {
		tokenCategories currentTokenCat = getCurrentTokenCategory() ; 
		if(currentTokenCat == tokenCategories.ATOM) {
			// Production <E>::=atom
			BinaryTree atomNode = parseAtom();
			if(atomNode != null) {
				//eNode.left = atomNode;
				//eNode.tokenValue = " ";
				return atomNode;
			} else { 
				return null;
			}
		} else if(currentTokenCat == tokenCategories.OPEN_PARANTHESIS) {
			// Production <E>::=(<X>
			BinaryTree openParen = parseOpenParen();
			BinaryTree ntX = parseX();
			if(ntX != null && openParen != null) {
				return ntX;
			} else {
				return null;
			}
		}		
		System.out.println("ERROR: Atom or open paranthesis expected.");
		System.exit(1);
		return null;
	}
	
	private BinaryTree parseX() {
		BinaryTree xNode = new BinaryTree();
		tokenCategories currentTokenCat = getCurrentTokenCategory();
		if(currentTokenCat == tokenCategories.CLOSING_PARANTHESIS) {
			// Production <X>::=)
			BinaryTree closeParenNode = parseCloseParen();
			if(closeParenNode != null) {
				//xNode.left = closeParenNode;
				xNode.tokenValue = "NIL"; 
				xNode.tokenCat = tokenCategories.ATOM;
				return xNode;
			}
		} else {
			// Production <X>::=<E><Y>
			BinaryTree eNode = parseExpression();
			BinaryTree yNode = parseY();
			xNode.left = eNode;
			xNode.right = yNode;
			return xNode;
		}
		return null;
	}
	
	private BinaryTree parseY() {
		tokenCategories currentTokenCat = getCurrentTokenCategory();
		if(currentTokenCat == tokenCategories.DOT) {
			// Production <Y>::=.<Z>
			BinaryTree dotNode = parseDot();
			BinaryTree zNode = parseZ();
			if(dotNode != null && zNode != null) {
				//yNode.left = dotNode;
				//yNode.right = zNode;
				return zNode;
			}
		} else {
			// Production <Y>::=<R>)	
			BinaryTree rNode = parseR();
			BinaryTree closeParenNode = parseCloseParen();
			return rNode;
		}
		return null;
	}
	
	private BinaryTree parseZ() {
		// Production <Z>::=<E>)
		BinaryTree eNode = parseExpression();
		BinaryTree closeParenNode = parseCloseParen();
		return eNode;
	}
	
	private BinaryTree parseR() {
		BinaryTree rNode = new BinaryTree();
		tokenCategories currentTokenCat = getCurrentTokenCategory();
		// When there are no more tokens, R will go to empty
		if(currentTokenCat == tokenCategories.ATOM || currentTokenCat == tokenCategories.OPEN_PARANTHESIS) {
			// Production <R>::=<E><R>
			BinaryTree eNode = parseExpression();
			BinaryTree rNode2 = parseR();
			rNode.left = eNode;
			rNode.right = rNode2;
			return rNode;
		} else {
			// Production  <R>::=Empty
			BinaryTree emptyNode = getNil();
			rNode = emptyNode;
			return rNode;
		}
	}
	
	private BinaryTree parseAtom() {
		BinaryTree atomNode = null;
		if(getCurrentTokenCategory() == tokenCategories.ATOM) {
			atomNode = new BinaryTree(tokenCategories.ATOM, getCurrentTokenValue());
			this.currentIndex++;
		}
		return atomNode;
	}
	
	private BinaryTree parseDot() {
		BinaryTree dotNode = null;
		if(getCurrentTokenCategory() == tokenCategories.DOT) {
			dotNode = new BinaryTree(tokenCategories.DOT, ".");
			this.currentIndex++;
		}
		return dotNode;
	}
	
	private BinaryTree parseCloseParen() {
		BinaryTree closeParen = null;
		if(getCurrentTokenCategory() == tokenCategories.CLOSING_PARANTHESIS) {
			closeParen = new BinaryTree(tokenCategories.CLOSING_PARANTHESIS, ")");
			this.currentIndex++;
		} else {
			System.out.println("ERROR: Close Paranthesis is expected.");
			System.exit(1);
		}
		return closeParen;
	}
	
	private BinaryTree parseOpenParen() {
		BinaryTree openParen = null;
		if(getCurrentTokenCategory() == tokenCategories.OPEN_PARANTHESIS) {
			openParen = new BinaryTree(tokenCategories.OPEN_PARANTHESIS, "(");
			this.currentIndex++;
		}
		return openParen;
	}
	
	private String getCurrentTokenValue() {
		String value = null;
		if(this.currentIndex < this.tokens.size()) {
			value = this.tokens.get(this.currentIndex);
		}
		return value;
	}
	
	private tokenCategories getCurrentTokenCategory() {
		tokenCategories tc = tokenCategories.ERROR;
		if(this.currentIndex < this.tokenCat.size()) {
			tc = this.tokenCat.get(this.currentIndex);
		}
		return tc;
	}
	
	private tokenCategories getLastTokenCategory() {
		if(this.tokenCat.size() > 0) {
			return this.tokenCat.get(this.tokenCat.size()-1);
		}
		return tokenCategories.ERROR;
	}
	
	private void deleteLastTokenCategory() {
		if(this.tokenCat.size() > 0) {
			this.tokenCat.remove(this.tokenCat.size()-1);
		}
	}
	
	private int getNextCloseParen() {
		int index = -1;
		for(int i = this.currentIndex; i < this.tokenCat.size(); i++) {
			if(this.tokenCat.get(i) == tokenCategories.CLOSING_PARANTHESIS) {
				index = i;
				break;
			}
		}
		return index;
	}
	
	private void removeTokenAt(int index) {
		if(index < this.tokenCat.size()) {
			this.tokenCat.remove(index);
			this.tokens.remove(index);
			this.parenRemoveCount++;
		}
	}
	
	private BinaryTree getNil() {
		BinaryTree nNode = new BinaryTree(tokenCategories.ATOM, "NIL");
		return nNode;
	}	
}
