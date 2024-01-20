package connectx.mcts_budspencer;

// Librerie di ConnectX scritte dal docente
import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;

// Import generici e utili
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

import javax.naming.TimeLimitExceededException;

// Librerie di ConnectX scritte dal nostro gruppo
import connectx.euristics.StartEuristicsCreator;

public class BudSpencer implements CXPlayer {

    // Variabili della partita utili al Player
    private int M, N, X; // M = altezza, N = larghezza, X = numero di pedine da allineare per vincere
    private CXGameState myWin; // Stato di vittoria del giocatore
    private CXGameState yourWin; // Stato di vittoria dell'avversario
    private long timeout_in_ms; // Tempo a disposizione del giocatore per la mossa
    private long START_TIME; // Tempo di inizio della mossa (confrontato per capire quando concluderla)
    private String playerName; // Nome del giocatore

    // Variabili utili al Player
    private int currentPlayer; // Giocatore corrente (0 o 1)
    private int opponentPlayer; // Giocatore avversario (1 o 0)

    private MCTS mcts;

    // Euristic creator, che ci salva alcuni dati utili poi per l'euristica
    private StartEuristicsCreator euristicsCreator;

    /**
     * Inizializza il giocatore "Bud Spencer" con i dati della partita.
     * 
     * @param M               Altezza della griglia
     * @param N               Larghezza della griglia
     * @param X               Numero di pedine da allineare per vincere
     * @param first           Se il giocatore è il primo a muovere
     * @param timeout_in_secs Tempo a disposizione del giocatore per la mossa
     */
    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        /* Inizializzo i dati del mio giocatore */
        this.M = M;
        this.N = N;
        this.X = X;
        this.myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        this.yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        this.timeout_in_ms = timeout_in_secs * 1000;
        
        this.currentPlayer = first ? 0 : 1;
        this.opponentPlayer = first ? 1 : 0;

        /* Imposto il nome */
        setPlayerName("Bud Spencer (MCTS)");

        // Imposto la root a vuota
        mcts = new MCTS(currentPlayer, opponentPlayer, N, M, X, timeout_in_secs);

        /* Inizializzo l'euristica */
        euristicsCreator = new StartEuristicsCreator(N, M, X, first);
    }

    @Override
    public int selectColumn(CXBoard B) {
        /* Imposto il tempo di quando inizio a controllare la mossa */
        START_TIME = System.currentTimeMillis();

        int returnMove = mcts.findNextMove(B, currentPlayer, START_TIME);
    
        // Utile per l'euristic creator
        euristicsCreator.interpretMove(B, returnMove);
        return returnMove;
    }

    @Override
    public String playerName() {
        return getPlayerName();
    }

    /************************************************
     * Getter e setter delle variabili del giocatore
     ************************************************/

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerName() {
        return playerName;
    }
}

class MCTSNode {
    public CXBoard board;
    public MCTSNode parent;
    public List<MCTSNode> children;
    public int visitCount;
    public double winScore;

    public MCTSNode(CXBoard board) {
        this.board = board;
        this.parent = null;
        this.children = new ArrayList<>();
        this.visitCount = 0;
        this.winScore = 0;
    }

    public MCTSNode(MCTSNode parent, CXBoard board) {
        this(board);
        this.parent = parent;
    }

    public boolean isLeaf() {
        return children.isEmpty();
    }
}
