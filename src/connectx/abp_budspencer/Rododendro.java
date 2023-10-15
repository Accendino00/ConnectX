package connectx.abp_budspencer;

import connectx.euristics_budspencer.BeatriceDiDante;

import java.util.concurrent.TimeoutException;

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
    private CXCellState player;
    private CXCellState enemy;
    private long tempoPerGiro;

    private double columnValueMultiplier [];


    private StartEuristicsCreator euristicsCreator;


    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        /* Inizializzando i dati del gioco */
        this.M = M;  // Numero righe
        this.N = N;  // Numero colonne
        this.X = X;  // Numero di cose da mettere in fila per vincere
        this.timeout_in_ms = timeout_in_secs * 1000;

        /* Altri dati utili per i confronti interni */
        this.myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        this.yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        this.player = first ? CXCellState.P1 : CXCellState.P2;
        this.enemy = first ? CXCellState.P2 : CXCellState.P1;
        this.START_TIME = System.currentTimeMillis();
        
        /* Calcoliamo columnValueMultiplier */
        /*columnValueMultiplier = new double[N];
        for (int i = 0; i < N; i++) {
            columnValueMultiplier[i] = gaussian1(i);
        }

        // Print columns values of the multiplier for debug and then exit the program
        for (int i = 0; i < N; i++) {
            System.out.println("Column " + i + " value: " + columnValueMultiplier[i]);
        }

        // Exit the program
        System.exit(0); */

        /* Calcoliamo un giro di eval */
        this.tempoPerGiro = evalRound();

        System.out.println("tempo per giro: " + tempoPerGiro);

        
        /* Inizializzo l'euristica */
        euristicsCreator = new StartEuristicsCreator(N, M, X, first);
    }

    /** First of the gaussians, given a column returns the vaalue of the gaussian in that column */
    private double gaussian1 (int column) {
        double firstValue = 0;
        double secondValue = 0;
        
        double sigma = 0.5;
        double mu = this.N / 2;
        double multiplier = 1/sigma * Math.sqrt(2 * Math.PI);


        // We calculate the first value it using the column value
        firstValue = 
        // First part (1 over sigma * sqrt of 2pi)
        (1 / (sigma * Math.sqrt(2 * Math.PI))) *
        // Second part (e ^ (-(x - mu)^2 / 2 * sigma^2))
        Math.pow(Math.E, -Math.pow(column - mu, 2) / (2 * Math.pow(sigma, 2)));
        ;

        // We calculate the second value using column + 1
        secondValue =
        // First part (1 over sigma * sqrt of 2pi)
        (1 / (sigma * Math.sqrt(2 * Math.PI))) *
        // Second part (e ^ (-(x - mu)^2 / 2 * sigma^2))
        Math.pow(Math.E, -Math.pow(column + 1 - mu, 2) / (2 * Math.pow(sigma, 2)));


        return (firstValue + secondValue) / 2;
    }

    @Override
    public String playerName() {
        return "Rododendro";
    }

    // Funzione per calcolare la durata di tempo di un giro di valutazione
    // inteso come ...
    private long evalRound(){
        CXBoard copy_b = new CXBoard(this.M, this.N, this.X);

        long averageTime = 0;
        int numOfTimes = 10;

        for(int i = 0; i < numOfTimes + 1; i++) {
            // Creiamo una variabile di tempo da ritornare e una copia della board
            long time = java.lang.System.nanoTime();
    
            // funzione di valutazione 
            eval(copy_b, this.player, copy_b.getAvailableColumns() , CXGameState.OPEN);
            if ( i != 0 )
                averageTime += java.lang.System.nanoTime() - time;
        }


        return averageTime / numOfTimes;
    }

    /**
     * Funzione che valuta la situazione di una board.
     * Data una board e dato  
     * 
     * 
     * @param B
     * @param currentPlayer
     * @param ava
     * @param state
     * @return 
    */
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
        if(b.getLastMove() == null || b.numOfMarkedCells() == 1){
            return b.N/2;
        }
        // Impostiamo una colonna di default e la profondità
        int returnColumn = b.getAvailableColumns()[b.N/2];
        int depth = 0;
        START_TIME = System.currentTimeMillis();
        
        try {
            // Iterative Deepening
            while (depth < 1) {
                // Impostiamo le variabili per l'iterazione
                int currentReturnColumn = returnColumn;
                int currentValue = Integer.MIN_VALUE;
                int currentMaxValue = Integer.MIN_VALUE;
                
                for (Integer i : b.getAvailableColumns()) {
                    if(System.currentTimeMillis() - START_TIME > timeout_in_ms - ( 100 + (tempoPerGiro * 0.000001))){  //tempoPerGiro in base a che livello 
                        throw new TimeoutException("Sta per superare il limite di tempo");
                    } else {
                        b.markColumn(i);
                        currentValue = alphaBeta(b, depth, Integer.MAX_VALUE, Integer.MIN_VALUE, false); 
                        b.unmarkColumn();
                        if (currentValue > currentMaxValue) {
                            currentMaxValue = currentValue;
                            currentReturnColumn = i;
                        }
                    }
                }
    
                // Abbiamo finito di guardare la depth corrente, salviamo i nuovi dati trovati e incrementiamo la depth
                returnColumn = currentReturnColumn;
                // Print della nuova colonna e della depth attuale [depth : valore]
                depth++;
            }
        } catch (TimeoutException e) {
            // We were about to surpass the time limit, so we exit the for.
            // Stampiamo la depth alla quale siamo arrivati:
            System.out.println("Depth: " + depth);
        }
        
        return returnColumn;
    } 
        
    // Alpha Beta Pruning
    private int alphaBeta(CXBoard b, int depth, int alpha, int beta, boolean maximizingPlayer) {
        int value;

        // Se il gioco è finito o siamo arrivati alla profondità massima, ritorniamo lo score
        if (depth == 0 || !(b.gameState() == CXGameState.OPEN)) {
            int score = eval(b, maximizingPlayer ? player : enemy, b.getAvailableColumns(), b.gameState());
            return score;
        }

        // Se è il turno del player, massimizziamo
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

        // Se è il turno del nemico, minimizziamo
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

}



