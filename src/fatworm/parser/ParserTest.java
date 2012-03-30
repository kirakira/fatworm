package fatworm.parser;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
public class ParserTest {
    public static void main(String[] args) throws Exception {
    	File file = new File("/home/hxr/fatworm/input.txt");
    	InputStream inp = new FileInputStream(file);
        ANTLRInputStream input = new ANTLRInputStream(inp);
        FatwormLexer lexer = new FatwormLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FatwormParser parser = new FatwormParser(tokens);
        FatwormParser.prog_return r= parser.prog();
        CommonTree t = (CommonTree)r.getTree();
        CommonTreeNodeStream ns = new CommonTreeNodeStream(t);
        System.out.println(t.toStringTree());
    }
}