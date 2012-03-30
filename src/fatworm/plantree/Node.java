package fatworm.plantree;

import java.util.LinkedList;

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
	public void Display() {
		this.print();
		for (Node n : childList){
			n.Display();
		}
	}
	public void print(){
		System.out.print(this.getClass().toString());
	}
}