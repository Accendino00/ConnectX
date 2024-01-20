package connectx.mcts_budspencer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.naming.TimeLimitExceededException;

import connectx.CXBoard;
import connectx.CXGameState;

/**
 * Usa questo sito per refference: https://www.baeldung.com/java-monte-carlo-tree-search
 * sembra funzionare (?)
 */

public class MCTS {
    private static final int WIN_SCORE = 10;

    private int currentPlayer;
    private int opponent;
    private int M, N, X;
    private int timeout_in_ms;

    private Random random;

    private Tree tree;
    private Node rootNode;

    private long START_TIME;

    private CXGameState myWin, yourWin;

    public MCTS(int currentPlayer, int opponentPlayer, int M, int N, int X, int timeout_in_secs) {
        // Settings di variabili generiche
        this.currentPlayer = currentPlayer;
        this.opponent = opponentPlayer;
        this.M = M;
        this.N = N;
        this.X = X;
        this.timeout_in_ms = timeout_in_secs * 1000;

        this.myWin = this.currentPlayer == 0 ? CXGameState.WINP1 : CXGameState.WINP2;
        this.yourWin = this.currentPlayer == 0 ? CXGameState.WINP2 : CXGameState.WINP1;

        // Imposto il random
        random = new Random();
    }

    public int findNextMove(CXBoard board, int playerNumber, long START_TIME) {
        // Imposto una mossa casuale tra quelle disponibili
        this.START_TIME = START_TIME;

        Integer[] availableColumns = board.getAvailableColumns();
        int returnValue = availableColumns[random.nextInt(availableColumns.length)];

        opponent = 1 - playerNumber; // Se il giocatore è 0, l'avversario è 1 e viceversa

        this.tree = new Tree();
        this.rootNode = tree.getRoot();

        rootNode.getState().setBoard(board);
        rootNode.getState().setPlayerNumber(opponent);

        try {
            while (true) {
                checkTime();

                // Fino a quando ho tempo, espando l'albero
                Node promisingNode = selectPromisingNode(rootNode);
                if (promisingNode.getState().getBoard().gameState() == CXGameState.OPEN) {
                    expandNode(promisingNode);
                }
                Node nodeToExplore = promisingNode;
                if (promisingNode.getChildArray().size() > 0) {
                    nodeToExplore = promisingNode.getChildArray()
                            .get(random.nextInt(promisingNode.getChildArray().size()));
                }
                CXGameState playoutResult = simulateRandomPlayout(nodeToExplore);
                backPropogation(nodeToExplore, playoutResult);
            }
        } catch (TimeLimitExceededException e) {
            // Se ho finito il tempo, ritorno la mossa migliore
            Node winnerNode = rootNode.getChildArray().stream().max((node1, node2) -> {
                return node1.getState().getVisitCount() - node2.getState().getVisitCount();
            }).get();
            returnValue = winnerNode.getState().getBoard().getLastMove().j;
        }

        return returnValue;
    }

    public Node selectPromisingNode(Node rootNode) {
        Node node = rootNode;
        while (node.getChildArray().size() != 0) {
            node = UCT.findBestNodeWithUCT(node);
        }
        return node;
    }

    /**
     * Controlla se il tempo limite è stato superato,
     * lanciando un'eccezione se mancano meno di 100 ms.
     * 
     * @throws TimeLimitExceededException
     */
    private void checkTime() throws TimeLimitExceededException {
        if (System.currentTimeMillis() - START_TIME > this.timeout_in_ms - 100) {
            throw new TimeLimitExceededException();
        }
    }

    public void expandNode(Node node) {
        List<State> possibleStates = node.getState().getAllPossibleStates();
        possibleStates.forEach(state -> {
            Node newNode = new Node(state, node);
            newNode.getState().setPlayerNumber(1 - node.getState().getPlayerNumber());
            node.getChildArray().add(newNode);
        });
    }

    public void backPropogation(Node nodeToExplore, CXGameState playerResult) {
        Node tempNode = nodeToExplore;
        while (tempNode != null) {
            tempNode.getState().incrementVisit();
            if (tempNode.getState().getPlayerNumber() == (myWin == CXGameState.WINP1 ? 0 : 1) && playerResult == myWin) {
                tempNode.getState().addScore(WIN_SCORE);
            }
            tempNode = tempNode.getParent();
        }
    }

    public CXGameState simulateRandomPlayout(Node node) {
        Node tempNode = new Node(node);
        State tempState = tempNode.getState();
        CXGameState boardStatus = tempState.getBoard().gameState();

        if (boardStatus == yourWin) {
            tempNode.getParent().getState().setWinScore(Integer.MIN_VALUE);
            return boardStatus;
        }

        while(boardStatus == CXGameState.OPEN) {
            tempState.randomPlay(random);
            boardStatus = tempState.getBoard().gameState();
        }

        return boardStatus;
    }

}

class Node {
    State state;
    Node parent;
    List<Node> childArray;

    /* Constructor */

    public Node(State state) {
        this.state = state;
        this.parent = null;
        this.childArray = new ArrayList<>();
    }

    public Node(State state, Node parent) {
        this.state = state;
        this.parent = parent;
        this.childArray = new ArrayList<>();
    }

    public Node(State state, Node parent, List<Node> childArray) {
        this.state = state;
        this.parent = parent;
        this.childArray = childArray;
    }

    public Node(Node node) {
        this.state = new State(node.getState());
        this.parent = node.getParent();
        this.childArray = node.getChildArray();
    }

    /* Setters and getters */

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public Node getParent() {
        return parent;
    }

    public void setChildArray(List<Node> childArray) {
        this.childArray = childArray;
    }

    public List<Node> getChildArray() {
        return childArray;
    }
}

class Tree {
    Node root;

    /* Constructor */

    public Tree() {
        this.root = new Node(new State(null, 0));
    }

    public Tree(Node root) {
        this.root = root;
    }

    /* Setters and getters */

    public void setRoot(Node root) {
        this.root = root;
    }

    public Node getRoot() {
        return root;
    }
}

class State {
    CXBoard board;
    int playerNumber; // 0 o 1
    int visitCount;
    double winScore;

    /* Constructor */

    public State(CXBoard board, int playerNumber) {
        this.board = board;
        this.playerNumber = playerNumber;
        this.visitCount = 0;
        this.winScore = 0;
    }

    /* Copy constructor */

    public State(State state) {
        this.board = state.board;
        this.playerNumber = state.playerNumber;
        this.visitCount = state.visitCount;
        this.winScore = state.winScore;
    }

    /* Setters and getters */

    public void setBoard(CXBoard board) {
        this.board = board;
    }

    public CXBoard getBoard() {
        return board;
    }

    public void setPlayerNumber(int playerNumber) {
        this.playerNumber = playerNumber;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }

    public void setVisitCount(int visitCount) {
        this.visitCount = visitCount;
    }

    public int getVisitCount() {
        return visitCount;
    }

    public void incrementVisit() {
        this.visitCount++;
    }

    public void setWinScore(double winScore) {
        this.winScore = winScore;
    }

    public void addScore(double score) {
        this.winScore += score;
    }

    public double getWinScore() {
        return winScore;
    }

    /* Methods */

    public List<State> getAllPossibleStates() {
        List<State> possibleStates = new ArrayList<>();

        // Per ogni colonna disponibile, creo uno stato
        for (int column : board.getAvailableColumns()) {
            CXBoard newBoard = board.copy();
            newBoard.markColumn(column);
            possibleStates.add(new State(newBoard, 1 - playerNumber));
        }

        return possibleStates;
    }

    public void randomPlay() {
        // Scelgo una mossa casuale tra quelle disponibili
        Integer[] availableColumns = board.getAvailableColumns();
        int randomColumn = availableColumns[new Random().nextInt(availableColumns.length)];

        // Eseguo la mossa
        board.markColumn(randomColumn);
    }

    public void randomPlay(Random random) {
        // Scelgo una mossa casuale tra quelle disponibili
        Integer[] availableColumns = board.getAvailableColumns();
        int randomColumn = availableColumns[random.nextInt(availableColumns.length)];

        // Eseguo la mossa
        board.markColumn(randomColumn);
    }
}

class UCT {
    public static double uctValue(
            int totalVisit, double nodeWinScore, int nodeVisit) {
        if (nodeVisit == 0) {
            return Integer.MAX_VALUE;
        }
        return ((double) nodeWinScore / (double) nodeVisit)
                + 1.41 * Math.sqrt(Math.log(totalVisit) / (double) nodeVisit);
    }

    public static Node findBestNodeWithUCT(Node node) {
        int parentVisit = node.getState().getVisitCount();
        return Collections.max(
                node.getChildArray(),
                Comparator.comparing(c -> uctValue(parentVisit,
                        c.getState().getWinScore(), c.getState().getVisitCount())));
    }
}