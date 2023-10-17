package connectx.abp_budspencer;

import java.util.LinkedList;
import java.util.List;

import connectx.CXBoard;
 
public class TreeNode {
 
  public CXBoard node;
  public List<TreeNode> childNodes;
 
  public TreeNode(CXBoard node) {
    this.node = node;
    this.childNodes = new LinkedList<>();
  }
 
  public void addChild(TreeNode childNode) {
    this.childNodes.add(childNode);
  }
  
  /* 
  public void showTreeNodes() {
    BreathFirstSearchPrintTreeNodes.printNodes(this);
  }
  */
  public CXBoard getNode() {
    return node;
  }
 
  public List<TreeNode> getChildNodes() {
    return childNodes;
  }
}



