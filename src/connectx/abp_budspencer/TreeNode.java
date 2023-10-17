package connectx.abp_budspencer;

import java.util.LinkedList;
import java.util.List;
 
public class TreeNode {
 
  public int[][] node;
  public List<TreeNode> childNodes;
 
  public TreeNode(int[][] node) {
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
  public int[][] getNode() {
    return node;
  }
 
  public List<TreeNode> getChildNodes() {
    return childNodes;
  }
}



