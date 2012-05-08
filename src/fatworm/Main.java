package fatworm;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.LinkedList;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;

import fatworm.absyn.*;
import fatworm.parser.FatwormLexer;
import fatworm.parser.FatwormParser;
import fatworm.plantree.*;
import fatworm.logicplan.PlanGen;

public class Main {
    public static final void main(String[] args) throws Exception {
        File file = new File("test/input.txt");
        InputStream inp = new FileInputStream(file);
        ANTLRInputStream input = new ANTLRInputStream(inp);
        FatwormLexer lexer = new FatwormLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        FatwormParser parser = new FatwormParser(tokens);
        FatwormParser.prog_return r= parser.prog();
        CommonTree t = (CommonTree)r.getTree();
        CommonTreeNodeStream ns = new CommonTreeNodeStream(t);
        //System.out.println(t.toStringTree());
        PlanGen.planGen(t);
    }
}
