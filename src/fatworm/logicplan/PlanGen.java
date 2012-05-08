package fatworm.logicplan;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.LinkedList;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import fatworm.absyn.*;
import fatworm.parser.FatwormLexer;
import fatworm.parser.FatwormParser;
import fatworm.plantree.*;

public class PlanGen {
	public static void printTree(CommonTree t){
		if (t.getChildCount() != 0) {
			System.out.print("(");
		}
		System.out.print(t.getText());
		if (t.getChildCount() != 0) {
				
		}
		for (int i = 0; i < t.getChildCount(); i++){
			
			printTree((CommonTree)t.getChild(i));
		}
		if (t.getChildCount() != 0)
			System.out.print(")");
	}
	
	/**
	 * select the first child of the certain root having a given Text 
	 * return null if there isn't any 
	 * @param root
	 * @param pattern
	 * @return
	 */
	public static CommonTree selectChild(CommonTree root, String pattern){
		for (int i = 0; i < root.getChildCount(); i++){
			if (root.getChild(i).getText().startsWith(pattern)){
				return (CommonTree)root.getChild(i);
			}
		}
		return null;
	}
	
	/**
	 * 
	 * join all the tables or queries together and
	 * generate a Join node as the root of all the other nodes
	 * 
	 * @param query		the root of the query tree
	 * @param current	the current root of the generated query plan tree 
	 * @return 
	 */
	public static Node processFrom(CommonTree query, Node current){
		Join join = new Join();
		CommonTree tree = selectChild(query, "From");
		if (tree == null) return current;
		CommonTree refList = (CommonTree)tree.getChild(0); // refList.getText() = "TableRefList"
		for (int j = 0; j < refList.getChildCount(); j++){
			CommonTree childJ = (CommonTree) refList.getChild(j).getChild(0);
			if (childJ.getText().startsWith("SimpleRef")){
				/**
				 * the table is referred by an IDENTIFIER stored in the first child's Text
				 */
				join.childList.add(new Table(childJ.getChild(0).getText()));
			} else 
			if (childJ.getText().startsWith("QueryRef")){
				/**
				 *  queryRef(query,IDENTIFIER)
				 *  first child is the subtree of the query
				 *  second child's Text is the IDENTIFIER(alias)
				 */
				CommonTree temp = (CommonTree)childJ.getChild(0); 
				Rename r = new Rename(childJ.getChild(1).getText());  							
				r.childList.add(planGen(temp));
				join.childList.add(r);
			} else if (childJ.getText().startsWith("RenameRef")){
				/**
				 * the table is referred by the real table name and an alias is given
				 * tableRef(RenameRef(table,alias))
				 */
				Rename r = new Rename(childJ.getChild(1).getText());
				r.childList.add(new Table(childJ.getChild(0).getText()));
				join.childList.add(r);
			}
		}
		if (current == null) 
			current = join;
		else
			current.childList.add(join);
		return current;
	}
	
	/**
	 * generate a GroupBy node as the root of the current node
	 * @param query 	the root of the query tree
	 * @param current 	the current root of the generated query plan tree 
	 * @return
	 */
	public static Node processGroupBy(CommonTree query, Node current){
		CommonTree tree = selectChild(query, "GroupBy");
		if (tree == null) return current;
		ColName colName;
		CommonTree temp = (CommonTree)tree.getChild(0).getChild(0);
		if (temp.getText().startsWith("SimpleColumn")){
			colName = new SimpleCol(temp);
		} else {
			colName = new FieldCol(temp);
		}
		GroupBy gb = new GroupBy(colName);
		gb.childList.add(current);
		current.parent = gb;
		return gb;
	}
	
	/**
	 * generate a OrderBy node as the root of the current node
	 * @param query	  the root of the query tree
	 * @param current the current root of the generated logical query plan tree
	 * @return
	 */
	public static Node processOrderBy(CommonTree query, Node current){
		CommonTree tree = selectChild(query, "OrderBy");
		if (tree == null) {
			return current;
		}
		LinkedList<ColName> cnl = new LinkedList<ColName>();
		CommonTree keyList = (CommonTree)tree.getChild(0);
		for (int j = 0; j < keyList.getChildCount(); j++){
			ColName colName = new ColName((CommonTree)keyList.getChild(j).getChild(0).getChild(0));						
			cnl.add(colName);
		}
		OrderBy orderBy = new OrderBy(cnl);
		orderBy.childList.add(current);
		current.parent = orderBy;
		return orderBy;
	}
	
	/**
	 * generate a Distinct node as the root of the current node
	 * @param query		the root of the query tree
	 * @param current	the current root of the generated logical query plan tree
	 * @return
	 */
	public static Node processDistinct(CommonTree query, Node current){
		CommonTree tree = selectChild(query, "Distinct");
		if (tree == null) return current;
		Distinct distinct = new Distinct();
		distinct.childList.add(current);
		current.parent = distinct;
		return distinct;
	}

	
	
	/**
	 * generate a projection node as the root of attributes
	 * @param query
	 * @param current
	 * @return
	 */
	public static Node processSelectColumn(CommonTree query, Node current){
		CommonTree tree = selectChild(query, "SelectColumn");
		if (tree == null) return current;
		LinkedList<ProjectionValue> valList = new LinkedList<ProjectionValue>();
		CommonTree exprList = (CommonTree)tree.getChild(0);
		for (int j = 0; j < exprList.getChildCount(); j++){
			/**
			 * SelectColumn(SelectExprList(SelectExpr(SimpleValue/RenameValue/AllColumn)))
			 * 					^							^		
			 * 					|							|
			 * 					|							|
			 * 				 exprList	 	 			  ChildJ
			 */
			CommonTree childJ = (CommonTree)exprList.getChild(j).getChild(0);
			ProjectionValue val = null;
			if (childJ.getText().startsWith("AllColumn")){
				val =  new ProjectionAllColumnValue(new ConstDefault("AllColumn"));
			}
			if (childJ.getText().startsWith("RenameValue")){
				val = new ProjectionRenameValue(getValue((CommonTree)childJ.getChild(0)), childJ.getChild(1).getText());
			}
			if (childJ.getText().startsWith("SimpleValue")){
				val = new ProjectionSimpleValue(getValue((CommonTree)childJ.getChild(0)));
			}
			valList.add(val);
		}
		Projection projection = new Projection(valList);
		projection.childList.add(current);
		current.parent = projection;
		return projection;
	}
	
	/**
	 * 
	 * @param query
	 * @param current
	 * @return
	 */
	public static Node processWhereCondition(CommonTree query, Node current){
		CommonTree tree = selectChild(query, "WhereCondition");
		if (tree == null) return current;
		Select select = new Select(getBoolExpr((CommonTree)tree.getChild(0)));
		current.parent = select;
		select.childList.add(current);
		return select;
	}
	
	/**
	 * 
	 * @param query
	 * @param current
	 * @return
	 */
	public static Node processHavingCondition(CommonTree query, Node current){
		CommonTree tree = selectChild(query, "HavingCondition");
		if (tree == null) return current;
		Select select = new Select(getBoolExpr((CommonTree)tree.getChild(0)));
		if (select.boolValue instanceof InExpr){
			
		}
		current.parent = select;
		return select;
	}
	
	public static void printNode(PrintWriter writer, int level, Node cur){
		if (cur == null) return;
		writer.println(level+"\t"+cur.toString());
		for (Node n : cur.childList){
			printNode(writer, level+1, n);
		}
	}
	
	/**
	 * get the value of the subtree
	 * @param tree
	 * @return
	 */
	public static Value getValue(CommonTree tree){
		if (tree.getText().startsWith("AllColumn")){
			return new ConstDefault("AllColumn");
		}
		if (tree.getText().startsWith("RenameValue")){
			return null;//new ProjectionRenameValue(getValue((CommonTree)tree.getChild(0)), tree.getChild(1).getText());
		}
		if (tree.getText().startsWith("SimpleValue")){
			return getValue((CommonTree)tree.getChild(0));
		}
		String root = tree.getText();
		if (root.indexOf("-+/%*") != -1){
			/**
			 * a operator and two operands
			 */
			Value left = getValue((CommonTree)tree.getChild(1));
			Value right = getValue((CommonTree)tree.getChild(2));
			return new OpValue(root, left, right);
		}
		if (root.startsWith("ColumnName")){
			ColName colName;
			CommonTree temp = (CommonTree)tree.getChild(0);
			if (temp.getText().startsWith("SimpleColumn")){
				colName = new SimpleCol(temp);
			} else {
				colName = new FieldCol(temp);
			}
			return new ColumnValue(colName);
		}
		if (root.startsWith("Func")){
			root = root.substring(4);
			root.trim();
			String func = tree.getChild(0).getText();
			Value val = getValue((CommonTree)tree.getChild(1));
			return new FuncValue(func, val);
		}
		if (root.startsWith("ConstInt")) 
			return new ConstInt(tree.getChild(0).getText());
		if (root.startsWith("ConstFloat")) 
			return new ConstFloat(tree.getChild(0).getText());
		if (root.startsWith("ConstTimeStamp")) 
			return new ConstTimeStamp(tree.getChild(0).getText());
		if (root.startsWith("ConstString")) 
			return new ConstString(tree.getChild(0).getText());
		if (root.startsWith("ConstNull")) 
			return new ConstNull(tree.getChild(0).getText());
		if (root.startsWith("ConstDefault")) 
			return new ConstDefault(tree.getChild(0).getText());
		if (root.startsWith("ConstBoolean"))
			return new ConstBoolean(tree.getChild(0).getText());
		// must not be reached
		return null;
	}
	
	/**
	 * 
	 * @param tree
	 * @return
	 */
	public static BoolExpr getBoolExpr(CommonTree tree){
		if (tree.getText().startsWith("AndList")){
			LinkedList<BoolExpr> andList = new LinkedList<BoolExpr>();
			for (int j = 0; j < tree.getChildCount(); j++){
				CommonTree childJ = (CommonTree)tree.getChild(j);
				andList.add(getBoolExpr(childJ));
			}
			return new AndList(andList);
		}
		if (tree.getText().startsWith("OrList")){
			LinkedList<BoolExpr> orList = new LinkedList<BoolExpr>();
			for (int j = 0; j < tree.getChildCount(); j++){
				CommonTree childJ = (CommonTree)tree.getChild(j);
				orList.add(getBoolExpr(childJ));
			}
			return new OrList(orList);
		}
		if (tree.getText().startsWith("In")){
			Value val = getValue((CommonTree)tree.getChild(0));
			Node query = planGen((CommonTree)tree.getChild(1));
			printNode(writer,0,query);
			return new InExpr(val, query);
		}
		if (tree.getText()=="Compare"){
			Value left = getValue((CommonTree)tree.getChild(0));
			Value right = getValue((CommonTree)tree.getChild(1));
			String cop = tree.getChild(2).getText();
			return new CompareExpr(left, right, cop);
		}
		if (tree.getText()=="CompareAny"){
			Value val = getValue((CommonTree)tree.getChild(0));
			Node query = planGen((CommonTree)tree.getChild(1));
			String cop = tree.getChild(2).getText();
			return new CompareAnyExpr(val, query, cop);
		}
		if (tree.getText()=="CompareAll"){
			Value val = getValue((CommonTree)tree.getChild(0));
			Node query = planGen((CommonTree)tree.getChild(1));
			String cop = tree.getChild(2).getText();
			return new CompareAllExpr(val, query, cop);
		}
		return null;
	}
	
	/**
	 * 
	 * @param t   the parse result 
	 * @return	the logical query plan tree
	 */
	public static Node planGen(CommonTree t) {
		Node current = null;
		if (t.getText().startsWith("Query")) {
			current = processFrom(t, current);
			current = processGroupBy(t, current);
			current = processOrderBy(t, current);
			current = processDistinct(t, current);
			current = processWhereCondition(t, current);
			current = processHavingCondition(t, current);
			current = processSelectColumn(t, current);
		}
		return current;
	}
	static PrintWriter writer = null;
	public static void main(String[] args) throws Exception {

        //initial
		File file = new File("/home/hxr/fatworm/input.txt");
    	InputStream inp = new FileInputStream(file);
        ANTLRInputStream input = new ANTLRInputStream(inp);
        writer = new PrintWriter(new FileOutputStream("/home/hxr/fatworm/output.txt"));
        FatwormLexer lexer = new FatwormLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        //parser 
        FatwormParser parser = new FatwormParser(tokens);
        FatwormParser.prog_return r= parser.prog();
        CommonTree t = (CommonTree)r.getTree();
        CommonTreeNodeStream ns = new CommonTreeNodeStream(t);
        writer.println(t.toStringTree());
        
        //logical query plan/ relation algebra tree
        Node tree = planGen(t);
        
        //output
        printNode(writer, 0, tree);
        writer.flush();
    }
}
