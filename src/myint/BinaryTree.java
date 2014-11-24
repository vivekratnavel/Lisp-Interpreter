package myint;
/**
 * @author Vivek Ratnavel Subramanian
 *
 */
public class BinaryTree {

	public BinaryTree left;
	public BinaryTree right;
	public boolean isList;
	public tokenCategories tokenCat;
	public String tokenValue;
	
	public BinaryTree() {
		// TODO Auto-generated constructor stub
		this.tokenValue = "";
		this.tokenCat = tokenCategories.NON_TERMINAL;
		this.left = null;
		this.right = null;
		this.isList = false;
	}

	public BinaryTree(tokenCategories tc, String tokenValue) {
		this.tokenCat = tc;
		this.tokenValue = tokenValue;
		this.left = null;
		this.right = null;
		this.isList = false;
	}
}
