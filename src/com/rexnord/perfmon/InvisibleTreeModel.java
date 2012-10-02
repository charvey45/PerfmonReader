//http://www.java2s.com/Code/Java/Swing-Components/InvisibleNodeTreeExample.htm
package com.rexnord.perfmon;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

class InvisibleTreeModel extends DefaultTreeModel {
	private static final long serialVersionUID = 3148836438751367961L;
	protected boolean filterIsActive;
	protected String filterString;
	

	  /**
	 * @return the filterString
	 */
	public String getFilterString() {
		if( filterString == null){
			filterString="";
		}
		return filterString;
	}

	/**
	 * @param filterString the filterString to set
	 */
	public void setFilterString(String filterString) {
		this.filterString = filterString;
	}

	public InvisibleTreeModel(TreeNode root) {
	    this(root, false);
	  }

	  public InvisibleTreeModel(TreeNode root, boolean asksAllowsChildren) {
	    this(root, false, false);
	  }

	  public InvisibleTreeModel(TreeNode root, boolean asksAllowsChildren,
	      boolean filterIsActive) {
	    super(root, asksAllowsChildren);
	    this.filterIsActive = filterIsActive;
	  }

	  public void activateFilter(boolean newValue) {
	    filterIsActive = newValue;
	  }

	  public boolean isActivatedFilter() {
	    return filterIsActive;
	  }

	  public Object getChild(Object parent, int index) {
	    if (filterIsActive) {
	      if (parent instanceof InvisibleNode) {
	        return ((InvisibleNode) parent).getChildAt(index,
	            filterIsActive);
	      }
	    }
	    return ((TreeNode) parent).getChildAt(index);
	  }

	  public int getChildCount(Object parent) {
	    if (filterIsActive) {
	      if (parent instanceof InvisibleNode) {
	        return ((InvisibleNode) parent).getChildCount(filterIsActive);
	      }
	    }
	    return ((TreeNode) parent).getChildCount();
	  }

	}