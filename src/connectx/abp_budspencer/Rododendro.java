package connectx.abp_budspencer;

import connectx.euristics_budspencer.BeatriceDiDante;

import javax.naming.TimeLimitExceededException;

import connectx.CXBoard;
import connectx.CXCell;
import connectx.CXCellState;
import connectx.CXPlayer;
import connectx.CXGameState;

import connectx.euristics.StartEuristicsCreator;

public class Rododendro implements CXPlayer{

    private int M, N, X;
    private CXGameState myWin;
    private CXGameState yourWin;
    private long timeout_in_ms;
    private long START_TIME;
    private boolean maximizingPlayer;
    private CXCellState player;
    private CXCellState enemy;


    private StartEuristicsCreator euristicsCreator;


    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        this.M = M;
        this.N = N;
        this.X = X;
        this.myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        this.yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        this.timeout_in_ms = timeout_in_secs * 1000;
        this.maximizingPlayer = first ? true : false;
        this.player = first ? CXCellState.P1 : CXCellState.P2;
        this.enemy = first ? CXCellState.P2 : CXCellState.P1;
        this.START_TIME = System.currentTimeMillis();

        /* Inizializzo l'euristica */
        euristicsCreator = new StartEuristicsCreator(N, M, X, first);
    }

    @Override
    public String playerName() {
        return "Rododendro";
    }

    // Make a general score function
    private int eval(CXBoard B, CXCellState currentPlayer, Integer[] ava, CXGameState state){
        CXCellState enemyPlayer = currentPlayer == CXCellState.P1 ? CXCellState.P2 : CXCellState.P1;

        currentPlayer = myWin == CXGameState.WINP1 ? CXCellState.P1 : CXCellState.P2;
        enemyPlayer = myWin == CXGameState.WINP1 ? CXCellState.P2 : CXCellState.P1;

        int playerScore = 0;
        int enemyScore = 0;


        // Se il gioco lo vince il player, ritorniamo il massimo valore
        if(state == myWin ){
            return Integer.MAX_VALUE;
        } 
        // Se il gioco lo vince il nemico, ritorniamo il minimo
        else if (state == yourWin ) {
            return Integer.MIN_VALUE;
        } 
        // Se il gioco finisce in pareggio, ritorno 0
        else if (state == CXGameState.DRAW) {
            return 0;
        }

        // Prendiamo la matrice di celle
        CXCellState cells[][] = B.getBoard();

        // Definiamo i contatori
        int j, i, k;

        // Numero di celle consecutive del player trovate per il sottoinsieme di celle considerato
        int playerCellHorizontal = 0;
        int playerCellVertical = 0;
        int playerCellDiagonal = 0;
        int playerCellDiagonalInverse = 0;

        // Numero di celle consecutive del nemico trovate per il sottoinsieme di celle considerato
        int enemyCellHorizontal = 0;
        int enemyCellVertical = 0;
        int enemyCellDiagonal = 0;
        int enemyCellDiagonalInverse = 0;

        // Flag se non è possibile considerare questo sottoinsieme in una certa direzione per problemi di spazio
        boolean impossibleCellHorizontal = false;
        boolean impossibleCellVertical = false;
        boolean impossibleCellDiagonal = false;
        boolean impossibleCellDiagonalInverse = false;


        // Contiamo la quantità di sottoinsiemi di X celle consecutive (del giocatore [almeno 1] o vuote)
        // Per ogni cella noi andiamo a vedere in un ciclo fino a X se ci sono X celle consecutive che sono
        // del giocatore o vuote (con almeno una del player), all'interno della riga, colonna o diagonale
        // Verrà dato un punteggio in base al numero di celle del player trovate in quel sottoinsieme.
        //
        // (Complessità : X*N*M)

        for (j = 0; j < this.M; j++) {      // Per ogni riga
            for (i = 0; i < this.N; i++) {  // Per ogni colonna
          
                // Reinizializziamo i contatori
                playerCellHorizontal = 0;
                playerCellVertical = 0;
                playerCellDiagonal = 0;
                playerCellDiagonalInverse = 0;

                // Reinizializziamo i contatori
                enemyCellHorizontal = 0;
                enemyCellVertical = 0;
                enemyCellDiagonal = 0;
                enemyCellDiagonalInverse = 0;
          
                // Reinizializziamo i flag
                impossibleCellHorizontal = false;
                impossibleCellVertical = false;
                impossibleCellDiagonal = false;
                impossibleCellDiagonalInverse = false;

                // Prima controlliamo se è possibile controllare per un sottoinsieme di X celle 
                // a partire da questa cella (se non è possibile, non controlliamo)
                if (i + this.X > this.N) {
                    impossibleCellHorizontal = true;
                }
                if (j + this.X > this.M) {
                    impossibleCellVertical = true;
                }
                if (j + this.X > this.M || i + this.X > this.N) {
                    impossibleCellDiagonal = true;
                }
                if (j - this.X < 0 || i + this.X > this.N) {
                    impossibleCellDiagonalInverse = true;
                }

                // Adesso andiamo a fare i controlli
                for (k = 0; k < this.X; k++) {
                    // Controllo per le righe
                    if (!impossibleCellHorizontal) {
                        if (cells[j][i + k] == enemyPlayer) {
                            enemyCellHorizontal++;
                        }
                        else if(cells[j][i + k] == currentPlayer){
                            playerCellHorizontal++;
                        }
                    }

                    // Controllo per le colonne
                    if (!impossibleCellVertical) {
                        if (cells[j + k][i] == enemyPlayer) {
                            enemyCellVertical++;;
                        }
                        else if(cells[j + k][i] == currentPlayer){
                            playerCellVertical++;
                        }
                    }

                    // Controllo per le diagonali
                    if (!impossibleCellDiagonal) {
                        if (cells[j + k][i + k] == enemyPlayer) {
                            enemyCellDiagonal++;
                        }
                        else if(cells[j + k][i + k] == currentPlayer){
                            playerCellDiagonal++;
                        }
                    }

                    // Controllo per le diagonali inverse
                    if (!impossibleCellDiagonalInverse) {
                        if (cells[j - k][i + k] == enemyPlayer) {
                            enemyCellDiagonalInverse++;
                        }
                        else if(cells[j - k][i + k] == currentPlayer){
                            playerCellDiagonalInverse++;
                        }
                    }
                }

                // Aggiungiamo allo score il numero di celle del player trovate per ogni sottoinsieme
                // solo se non è un sottoinsieme impossibile
                if (!impossibleCellHorizontal) {
                    if (enemyCellHorizontal == 0)
                        playerScore += playerCellHorizontal;
                    if (playerCellHorizontal == 0)
                        enemyScore += enemyCellHorizontal;
                }
                if (!impossibleCellVertical) {
                    if (enemyCellVertical == 0)
                        playerScore += playerCellVertical;
                    if (playerCellVertical == 0)
                        enemyScore += enemyCellVertical;
                }
                if (!impossibleCellDiagonal) {
                    if (enemyCellDiagonal == 0)
                        playerScore += playerCellDiagonal;
                    if (playerCellDiagonal == 0)
                        enemyScore += enemyCellDiagonal;
                }
                if (!impossibleCellDiagonalInverse) {
                    if (enemyCellDiagonalInverse == 0)
                        playerScore += playerCellDiagonalInverse;
                    if (playerCellDiagonalInverse == 0)
                        enemyScore += enemyCellDiagonalInverse;
                }
            }
        }

        return playerScore - enemyScore;
    }


    @Override
    public int selectColumn(CXBoard b) {
        return iterativeDeepening(b, 20, Integer.MIN_VALUE, Integer.MAX_VALUE, true);
        
    }


    private int alphaBeta(CXBoard b, int depth, int alpha, int beta, boolean maximizingPlayer) {
        int value;
        if (depth == 0 || !(b.gameState() == CXGameState.OPEN)) {
            int score = eval(b, maximizingPlayer ? player : enemy, b.getAvailableColumns(), b.gameState());
            return score;
        }
        if (maximizingPlayer) {
            value = Integer.MIN_VALUE;
            // For each child of node
            for (Integer i : b.getAvailableColumns()) {
                b.markColumn(i);
                value = Math.max(value, alphaBeta(b, depth - 1, alpha, beta, false));
                b.unmarkColumn();
                alpha = Math.max(alpha, value);
                if (value >= beta) {
                    break;
                }
            }
            return value;
        } else {
            value = Integer.MAX_VALUE;
            for (Integer i : b.getAvailableColumns()) {
                b.markColumn(i);
                value = Math.min(value, alphaBeta(b, depth - 1, alpha, beta, true));
                b.unmarkColumn();
                beta = Math.min(beta, value);
                if (value <= alpha) {
                    break;
                }
            }
            return value;
        }
    }

    private Integer iterativeDeepening(CXBoard b, int maxDepth, int alpha, int beta, boolean maximizingPlayer) {
        int maxValue = Integer.MIN_VALUE;
        int returnColumn = b.getAvailableColumns()[0];
        int value = 0;
        int depth = 1;
        while (depth <= maxDepth) {
            for (Integer i : b.getAvailableColumns()) {
                b.markColumn(i);
                if(System.currentTimeMillis() - START_TIME > timeout_in_ms){
                    return value;
                }
                value = alphaBeta(b, depth, alpha, beta, maximizingPlayer);
                depth++;
                b.unmarkColumn();
                if (value > maxValue) {
                    maxValue = value;
                    returnColumn = i;
                }
            }
        }
        return returnColumn;
    }
}



