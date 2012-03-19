package fatworm.parser;

import org.antlr.runtime.*;
import org.antlr.runtime.tree.*;
public class ParserTest {
    public static void main(String[] args) throws Exception {
        ANTLRInputStream input = new ANTLRInputStream(System.in);
        FatwormLexer lexer = new FatwormLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FatwormParser parser = new FatwormParser(tokens);
        FatwormParser.prog_return r= parser.prog();
        CommonTree t = (CommonTree)r.getTree();
        System.out.println(t.toStringTree());
    }
}