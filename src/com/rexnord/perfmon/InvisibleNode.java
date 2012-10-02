//http://www.java2s.com/Code/Java/Swing-Components/InvisibleNodeTreeExample.htm
package com.rexnord.perfmon;

import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

class InvisibleNode extends DefaultMutableTreeNode {
	private static final long serialVersionUID = -2538031549255357653L;
	protected boolean isVisible;

	  public InvisibleNode() {
	    this(null);
	  }

	  public InvisibleNode(Object userObject) {
	    this(userObject, true, true);
	  }

	  public InvisibleNode(Object userObject, boolean allowsChildren,
	      boolean isVisible) {
	    super(userObject, allowsChildren);
	    this.isVisible = isVisible;
	  }

	  public TreeNode getChildAt(int index, boolean filterIsActive) {
	    if (!filterIsActive) {
	      return super.getChildAt(index);
	    }
	    if (children == null) {
	      throw new ArrayIndexOutOfBoundsException("node has no children");
	    }

	    int realIndex = -1;
	    int visibleIndex = -1;
	    Enumeration<?> e = children.elements();
	    while (e.hasMoreElements()) {
	      InvisibleNode node = (InvisibleNode) e.nextElement();
	      if (node.isVisible()) {
	        visibleIndex++;
	      }
	      realIndex++;
	      if (visibleIndex == index) {
	        return (TreeNode) children.elementAt(realIndex);
	      }
	    }

	    throw new ArrayIndexOutOfBoundsException("index unmatched");
	    //return (TreeNode)children.elementAt(index);
	  }

	  public int getChildCount(boolean filterIsActive) {
	    if (!filterIsActive) {
	      return super.getChildCount();
	    }
	    if (children == null) {
	      return 0;
	    }

	    int count = 0;
	    Enumeration<?> e = children.elements();
	    while (e.hasMoreElements()) {
	      InvisibleNode node = (InvisibleNode) e.nextElement();
	      if (node.isVisible()) {
	        count++;
	      }
	    }

	    return count;
	  }

	  public void setVisible(boolean visible) {
	    this.isVisible = visible;
	  }

	  public boolean isVisible() {
	    return isVisible;
	  }

	  /**
		 * 
		 * @param FullTree
		 * @param searchString
		 * @return
		 */
		public static void setVisbilityOnNodes(InvisibleNode FullTree, String searchString) {

			
			Enumeration<?> a = FullTree.children();
			
			while (a.hasMoreElements()){
				
				InvisibleNode g = (InvisibleNode) a.nextElement();
				System.out.println("New Node - " + g.toString());
				
				
				if (g.isLeaf()){
					//Evaluate Name
					
					if (g.toString().matches(searchString)){
						System.out.println("Leaf Match:"+g.toString() + " == " + searchString);
						g.setVisible(true);
					} else{
						System.out.println("Leaf Match:"+g.toString() + " != " + searchString);
						g.setVisible(false);
					}
					
				}else {
					//A possible Parent...Set the node to visible then evaluate it children.
					g.setVisible(true);
					setVisbilityOnNodes(g,searchString);
				}
			}
			
		}
	}