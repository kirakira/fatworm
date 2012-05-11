package fatworm.database;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;
import org.antlr.runtime.tree.CommonTreeNodeStream;


import fatworm.logicplan.PlanGen;
import fatworm.parser.FatwormLexer;
import fatworm.parser.FatwormParser;
import fatworm.planner.BasicExecutor;
import fatworm.planner.BasicQueryPlanner;
import fatworm.planner.Executor;
import fatworm.planner.QueryPlanner;
import fatworm.plantree.Command;
import fatworm.plantree.Node;
import fatworm.plantree.SqlStatement;
import fatworm.query.Scan;
import fatworm.storage.Storage;
import fatworm.storagemanager.MemoryStorageManager;
import fatworm.storagemanager.StorageManagerInterface;

public class Database {
    QueryPlanner queryPlanner;
    Executor executor;
    static Database instance;
    StorageManagerInterface storageManager;
    
    public Database() {
    	queryPlanner = new BasicQueryPlanner();
    	executor = new BasicExecutor();
    	//storageManager = new MemoryStorageManager();
    	storageManager = new Storage();
    	instance = this;
    	
    }
    public  QueryPlanner getQueryPlanner() {
        return queryPlanner;
    }
    
    public static Database getInstance() {
    	return instance;
    }
    
    public StorageManagerInterface getStorageManager() { 
    	return storageManager;
    }
    
    
    public static void main(String[] args) throws Exception {
    	Database database = new Database();
    	BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
    	String sql = reader.readLine();
    	while(sql != null) {
    		Scan result = database.execute(sql);
    		if(result != null) {
    			int width = result.getNumberOfColumns();
    			result.beforeFirst();
    			while(result.next()) {
	    			for (int i = 0; i < width; i++)
	    				System.out.print(result.getColumnByIndex(i).toString() + " | ");
	    			System.out.println();
    			}
    			System.out.println();
    		}
    		sql = reader.readLine();
    		
    	}
    }
    
    public Scan execute(String sql) throws RecognitionException, IOException {
    	
    	InputStream inp =  new ByteArrayInputStream(sql.getBytes());

        ANTLRInputStream input = new ANTLRInputStream(inp);
        
//        writer = new PrintWriter(new FileOutputStream("/home/hxr/fatworm/output.txt"));
        FatwormLexer lexer = new FatwormLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        //parser 
        FatwormParser parser = new FatwormParser(tokens);
        FatwormParser.prog_return r= parser.prog();
        CommonTree t = (CommonTree)r.getTree();
//        CommonTreeNodeStream ns = new CommonTreeNodeStream(t);
//        writer.println(t.toStringTree());
        
        //logical query plan/ relation algebra tree
        SqlStatement statement = PlanGen.planGen(t);
  
        if(statement instanceof Command)
        	executor.execute((Command)statement);
        else if (statement instanceof Node)
        	return queryPlanner.createQueryPlan((Node)statement).open();
        return null;
        //output
//        printNode(writer, 0, tree);
//        writer.flush();
    	
    }
    
}