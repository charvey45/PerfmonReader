package com.rexnord.perfmon;

import java.util.LinkedList;

import javax.swing.tree.TreePath;

public class NodeHistory {
	LinkedList<TreePath> nodeHistory;
	LinkedList<TreePath> nodeFutureHistory;
	
	TreePath CurrentNode;
	TreePath NextFutureNode;
	TreePath NextHistoryNode;
	

	public void setCurrentNode(TreePath current){
		NextHistoryNode = nodeHistory.peekLast();
		NextFutureNode = null;
		nodeHistory.add(current);
		CurrentNode = nodeHistory.peekLast();
	}
	
	public TreePath getMoveBackInHistory(){
		NextFutureNode =CurrentNode;
		
		CurrentNode = nodeHistory.removeLast();
		return null;
		
		
		
	}
	
	public void removeNodeTree(TreePath close){
		
	}
	
	
	
}
