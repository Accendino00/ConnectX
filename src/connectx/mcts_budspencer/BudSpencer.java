package connectx.mcts_budspencer;

import connectx.CXPlayer;

import javax.naming.TimeLimitExceededException;

import connectx.CXBoard;
import connectx.CXGameState;

import connectx.euristics.StartEuristicsCreator;

public class BudSpencer implements CXPlayer {

    private int M, N, X;
    private CXGameState myWin;
    private CXGameState yourWin;
    private long timeout_in_ms;
    private long START_TIME;


    private StartEuristicsCreator euristicsCreator;

    private MCTSTree movesTree;

    /**
     * Check if the time limit is exceeded.
     * It throws an exception if there is less than 100 ms left on the clock.
     * 
     * @throws TimeLimitExceededException
     */
    private void checkTime() throws TimeLimitExceededException {
        if (System.currentTimeMillis() - START_TIME > this.timeout_in_ms - 100) {
            throw new TimeLimitExceededException();
        }
    }


    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        /* Inizializzo i dati del mio giocatore */
        this.M = M;
        this.N = N;
        this.X = X;
        this.myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        this.yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        this.timeout_in_ms = timeout_in_secs * 1000;

        /* Inizializzo l'euristica */
        euristicsCreator = new StartEuristicsCreator(N, M, X, first);
    }

    @Override
    public int selectColumn(CXBoard B) {
        /* Imposto il tempo di quando inizio a controllare la mossa */
        START_TIME = System.currentTimeMillis();

        /* Mi imposto la prima mossa disponibile, in caso serva */
        Integer returnMove = B.getAvailableColumns()[0];

        try {
            checkTime();
            movesTree = new MCTSTree(B, myWin, yourWin, timeout_in_ms, B.currentPlayer);
            returnMove = movesTree.getBestMove();
            break;
        } catch (TimeLimitExceededException e) {
            System.out.println("Time limit exceeded");
        }

        euristicsCreator.interpretMove(B, returnMove);
        return returnMove;
    }

    @Override
    public String playerName() {
        return "Bud Spencer MCTS";
    }
}