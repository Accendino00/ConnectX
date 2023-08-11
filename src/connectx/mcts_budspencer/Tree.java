package connectx.mcts_budspencer;

import connectx.CXBoard;

/**
 * Albero n-ario
 * 
 * Ha un nodo radice e ogni nodo ha un numero massimo di figli.
 * Contiene alcuni metodi per la gestione dell'albero. 
 */
public class Tree {

    public class TreeNode {
        private int value; // Eval value
        private TreeNode children[];

        /**
         * Constructor
         * @param value
         * @param maxChildren
         */
        public TreeNode(int value, int maxChildren) {
            this.value = value;
            this.children = new TreeNode[maxChildren];
        }


        /* SETTER AND GETTER */

        public TreeNode[] getChildren() {
            return this.children;
        }

        public int getValue() {
            return this.value;
        }

        public void setChildren(int index, TreeNode child) {
            this.children[index] = child;
        }

        public void setValue(int value) {
            this.value = value;
        }
    }   
    
    private TreeNode root;

    /**
     * Constructor
     * @param value
     * @param maxChildren
     */
    public Tree(int value, int maxChildren) {
        this.root = new TreeNode(value, maxChildren);

    }
    
}

