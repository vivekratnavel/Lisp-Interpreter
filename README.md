Lisp-Interpreter
================

Academic Project - CSE 6341: Foundations of Programming Languages

Interpreter Design:
The project is implemented in Java. The various classes used and their functions are described below-

MyScanner: This class implements the scanner part of the project by reading input string from standard input till end of file and throws error for any inappropriate use of symbols. After scanning a statement, the scanner passes the statement to parser for syntax checking.

MyParser: This class implements LL(1) recursive descent parser for validating the input expression with the grammar of the language. The class contains methods to parse each non-terminal symbol and builds the S-Expression binary tree using bottom up approach. It also contains a method to print the S-Expression tree built while parsing the expression. Parsing will succeed only if the given input expression complies to the grammar of language. If the input does not comply with the grammar, then an error message is displayed to the standard output and the control is returned back to the operating system.

BinaryTree: This class implements a binary tree structure containing the value of token and category of a token. This is used to build the S-Expression tree after successful parsing.

BTreePrinter: This class is currently not used. It is useful for visualizing the constructed binary tree. I am not the author of this class. The source of this class file is - http://stackoverflow.com/questions/4965335/how-to-print-binary-tree-diagram

tokenCategories: This is an enum class containing different types of tokens required for the scanner and parser.

MyInterpreter: This is the class which contains the main function and calls the scanner to read input from stdin.

Interpreter: This class evaluates the parsed S expression and gives the final output. It contains all the functions required to evaluate S expression and is the heart of the project. The class also contains helper functions to evaluate and pretty print the output S expression tree.