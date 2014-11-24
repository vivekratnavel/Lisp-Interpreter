package myint;

public class Global {
	public static BinaryTree dList = new BinaryTree(tokenCategories.ATOM, "NIL");
	//public static BinaryTree dList = null;

	public static BinaryTree getdList() {
		return dList;
	}

	public static void setdList(BinaryTree dList) {
		Global.dList = dList;
	}
}
