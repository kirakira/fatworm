package fatworm.plantree;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class Node {
	public LinkedList<Node> childList = new LinkedList<Node>();
	public Node parent;
	public int getChildCount() {
		return childList.size();
	}
	public void switchChild(Node n) {
		for (int i = 0; i < childList.size(); i++){
			if (childList.get(i) == n){
				childList.remove(i);
				childList.add(i, n);
			}
		}
	}
	public String toString(){
		return "mustn't be echoed, something wrong@Node";
	}
	
    public Set<String> dumpUsefulColumns() {
    	Set<String> result = new HashSet<String>();
    	for(Node node: childList) {
    		result.addAll(node.dumpUsefulColumns());
    	}
    	return result;
    }	
	
}
