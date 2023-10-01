package connectx.mcts_budspencer;

import connectx.CXBoard;
import connectx.CXGameState;


/**
 * Albero n-ario
 * 
 * Ha un nodo radice e ogni nodo ha un numero massimo di figli.
 * Contiene alcuni metodi per la gestione dell'albero. 
 */
public class MCTSTree {

    private CXGameState myWin;

    public class TreeNode {
        private int value; // Eval value
        private TreeNode children[];
        private int exploredTimes;
        private CXBoard board;
        
        /**
         * Constructor
         * @param value
         * @param maxChildren
         */
        public TreeNode(int value, int maxChildren, CXBoard board) {
            this.value = value;
            this.board = board;
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

        public boolean isOver() {
            CXGameState gameState = board.gameState();
            return gameState == CXGameState.WINP1 || gameState == CXGameState.WINP2 || gameState == CXGameState.DRAW;
        }

        public int playerWin (CXGameState myWin) {
            CXGameState gameState = board.gameState();
            if(gameState == myWin) {
                return 1;
            } else if(gameState == CXGameState.DRAW) {
                return 0;
            } else {
                return -1;
            }
        }

        public TreeNode getNextRandomState() {
            Integer[] availableColumns = board.getAvailableColumns();
            int randomIndex = (int) (Math.random() * availableColumns.length);
            int randomColumn = availableColumns[randomIndex];
            CXBoard newBoard = board.copy();
            newBoard.markColumn(randomColumn);
            return new TreeNode(0, newBoard.getAvailableColumns().length, newBoard);
        }

        public void incrementExploredTimes() {
            this.exploredTimes++;
        }

        public int getExploredTimes() {
            return this.exploredTimes;
        }
    }   
    
    private TreeNode root;

    /**
     * Constructor
     * @param value
     * @param maxChildren
     */
    public MCTSTree(CXBoard board, CXGameState myWin, long timeout_in_ms, int playerNum) {
        this.myWin = myWin;
        this.root = new TreeNode(value, board.getAvailableColumns().length, board.copy());
    }
    
    /**
     * Returns the child node with the highest UCB1 value
     * 
     * @param headNode  the node from which to start the search
     * @return          the child node with the highest UCB1 value
     */
    public static TreeNode getMaxUCB1 (TreeNode headNode) {
        TreeNode maxNode = null;
        float maxUCB1 = 0;
        for (TreeNode node : headNode.getChildren()) {
            float UCB1 = UCB1(node);
            if (UCB1 > maxUCB1) {
                maxUCB1 = UCB1;
                maxNode = node;
            }
        }
        return maxNode;
    }

    /**
     * Returns the UCB1 value of the node
     * 
     * @param node  the node to calculate the UCB1 value
     * @return      the UCB1 value of the node
     */
    public static float UCB1 (TreeNode node) {
        float returnValue = 0;
        final float C = 1; // Exploration parameter

        /*
            Algorithm:
            UCB1 = value + C * sqrt(2 * ln(N) / n)
            N = number of times the parent node has been visited
            n = number of times the node has been visited
        */

        int N = node.getValue();
        int n = node.getExploredTimes();

        returnValue = (float) (node.getValue() + C * Math.sqrt(2 * Math.log(N) / n));

        return returnValue;
    }

    public static int rollout (TreeNode node, CXGameState myWin) {
        int returnValue = 0;

        /*
        
            Algorithm:
            Loop for infinity:
                if node is a terminal state (game is over):
                    Return who won (true if current, false if adversary)
                Ai = random value of the available actions
                node = node with the action Ai applied
        */

        while (true) {
            if(node.isOver()) {
                returnValue = node.playerWin(myWin);
                break;
            } else {
                node = node.getNextRandomState();
            }
        }

        return returnValue;
    }
}

