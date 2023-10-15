package connectx.euristics_budspencer;

import connectx.CXBoard;
import connectx.CXCell;
import connectx.CXPlayer;
import connectx.CXGameState;
import connectx.CXCellState;

import java.io.IOError;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import javax.naming.TimeLimitExceededException;

import connectx.euristics.StartEuristicsCreator;

public class HogRider implements CXPlayer {
    
    // Number of rows
    private int M;  
    // Number of columns
    private int N;  
    // Number of adjacent tiles to win
    private int X;  
    
    private CXCellState playerTileType;
    private int timeout_in_secs;
    private long START_TIME;
    private Random rand; 
    private CXGameState myWin;
    private CXGameState yourWin;
    private final int BLOCKENEMY = 200000;
    private final int DOUBLEVICTORY = 100000;

    private StartEuristicsCreator euristicCreator;

    public HogRider() {
        // Initialize data
        this.rand = new Random(System.currentTimeMillis());
    }

    // Creo una funzione generale per controllare se ho una possibile doppia vittoria ( B-X-1 celle in fila )
    // con uno switch per capire se è orizzontale, diagonale o diagonale inversa
    // non necessito di controllare la direzione verticale dato che non è possibile avere una doppia vittoria in verticale

    private int isDoubleVictory(CXBoard B, CXCellState cells[][], CXCellState currentPlayer, int j, int i,  int direction){
        // Variabili sulle quali si eseguono i controlli, che ci indicano l'indice
        // della prima e dell'ultima colonna della cella da vedere
        int colonnaPrima, colonnaUltima;
        
        // Prima controlliamo da che lato c'è l'ultimo elemento, controllando se la prima è una cella del player
        // il controllo è uguale per tutti e 3 i casi in quanto lavoriamo con le colonne
        if (cells[j][i] == currentPlayer) {
            colonnaPrima = i - 1;
            colonnaUltima = i + B.X - 1; 
        }    
        else {
            colonnaPrima = i;
            colonnaUltima = i + B.X;
        }
        // Controlliamo se le colonne da controllare superano i limite della board
        if (colonnaPrima >= 0 && colonnaUltima < B.N) {
            switch(direction){
                // Caso orizzontale
                case 1:
                // Controlliamo se le celle delle colonne da controllare sono vuote
                if (cells[j][colonnaPrima] == CXCellState.FREE && cells[j][colonnaUltima] == CXCellState.FREE) {
                    // Controlliamo se siamo a riga 0 oppure se sotto le celle vuote c'è una cella occupata
                    if(j == this.M-1) // Siamo a riga 0, cioe M-1 poiche la tabella è stampata al contrario
                        return DOUBLEVICTORY;
                    if(j-1 >= 0){ // Controllo di non avere un accesso fuori dalla matrice
                        if(((cells[j - 1][colonnaPrima] != CXCellState.FREE) && (cells[j - 1][colonnaUltima] != CXCellState.FREE))){
                            return DOUBLEVICTORY;
                        }
                    } else {
                        return this.N * 5; // Comunque un caso positivo, ma non tanto quanto DOUBLEVICTORY
                    }
                }
                break;

                // Caso diagonale
                case 2:
                // Controlliamo se le celle delle colonne da controllare sono vuote, se sono a riga 0 allora j-1 non
                // è una cella valida quindi aggiungo un controllo per sicurezza
                if(j-1 >= 0 && j+1 < B.M){
                    if (cells[j-1][colonnaPrima] == CXCellState.FREE && cells[j+1][colonnaUltima] == CXCellState.FREE) {
                        // Non ho bisogno di controllare se sono a riga 0, in quanto se sono a riga 0, allora j-1 non è una cella valida
                        if(j-2>=0){ // Controllo di non avere un accesso fuori dalla matrice
                            if(((cells[j - 2][colonnaPrima] != CXCellState.FREE) && (cells[j][colonnaUltima] != CXCellState.FREE))){
                                return DOUBLEVICTORY;
                            }
                        }
                        else {
                            return this.N * 5; // Comunque un caso positivo, ma non tanto quanto DOUBLEVICTORY
                        }
                    }
                }
                break;

                // Caso diagonale inversa
                case 3:
                // Controlliamo se le celle delle colonne da controllare sono vuote, se sono a riga 0 allora j-1 non
                // è una cella valida quindi aggiungo un controllo per sicurezza
                if(j-1 >= 0 && j+1 < B.M){
                    if (cells[j+1][colonnaPrima] == CXCellState.FREE && cells[j-1][colonnaUltima] == CXCellState.FREE) {
                        // Non ho bisogno di controllare se sono a riga 0, in quanto se sono a riga 0, allora j-1 non è una cella valida
                        if(j-2>=0){ // Controllo di non avere un accesso fuori dalla matrice
                            if(((cells[j - 2][colonnaUltima] != CXCellState.FREE) && (cells[j][colonnaPrima] != CXCellState.FREE))){
                                return DOUBLEVICTORY;
                            }
                        }
                        else {
                            return this.N * 5; // Comunque un caso positivo, ma non tanto quanto DOUBLEVICTORY
                        }
                    }
                }
            }
        }
        return 0;
    }

    // Make a general score function
    private int eval(CXBoard B, CXCellState currentPlayer, Integer[] ava, CXGameState state){
        CXCellState enemyPlayer = currentPlayer == CXCellState.P1 ? CXCellState.P2 : CXCellState.P1;

        currentPlayer = myWin == CXGameState.WINP1 ? CXCellState.P1 : CXCellState.P2;
        enemyPlayer = myWin == CXGameState.WINP1 ? CXCellState.P2 : CXCellState.P1;

        int playerScore = 0;
        int enemyScore = 0;


        // Se il gioco lo vince il player, ritorniamo il massimo valore
        if(state == myWin){
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


        // Contiamo la quantita di sottoinsiemi di X celle consecutive (del giocatore [almeno 1] o vuote)
        // Per ogni cella noi andiamo a vedere in un ciclo fino a X se ci sono X celle consecutive che sono
        // del giocatore o vuote (con almeno una del player), all'interno della riga, colonna o diagonale
        // Verra dato un punteggio in base al numero di celle del player trovate in quel sottoinsieme.
        //
        // (Complessita : X*N*M)

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

                        // Se abbiamo contato X-1 diverse celle del player 
                        //      (non vediamo se ne contiamo X in quanto questo controllo viene fatto precedentemente dalla 
                        //       condizione del gioco, la quale sara player win nel caso ce ne fossero 4 in fila)
                        // E queste celle sono messe di fila (pertanto non vi sono spazi vuoti -> viene controllato vedendo se la prima o l'ultima cella sono vuote)
                        // Se queste celle sono in fila, allora dobbiamo controllare se prima E dopo queste celle c'è uno spazio vuoto
                        //    - Impostiamo il ritorno a 50000
                        // Se inoltre lo spazio sotto agli spazi vuoti è occupato oppure se siamo alla prima riga
                        //    - Impostiamo il ritorno a 100000

                        // Se abbiamo trovato X-1 celle del nemico e non ci sono celle del player
                        if (enemyCellHorizontal == (B.X - 1) && playerCellHorizontal == 0) {
                            // chiamo la funzione isDoubleVictory per controllare se c'è una possibile doppia vittoria
                            // con direzione 1 cioe orizzontale, ci sono anche direzione 2 e 3 per le diagonali
                            enemyCellHorizontal += (isDoubleVictory(B, cells, enemyPlayer, j, i, 1))*3; // dato che è una mossa difensiva, la moltiplico per 3 per darle più importanza
                        }    
                        // Se abbiamo trovato X-1 celle del player e non ci sono celle del nemico
                        if (playerCellHorizontal == (B.X - 1) && enemyCellHorizontal == 0) {
                            // chiamo la funzione isDoubleVictory per controllare se c'è una possibile doppia vittoria
                            // con direzione 1 cioe orizzontale
                            playerCellHorizontal += isDoubleVictory(B, cells, currentPlayer, j, i, 1);
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
                        if(enemyCellDiagonal == (B.X - 1) && playerCellDiagonal == 0){
                            enemyCellDiagonal += (isDoubleVictory(B, cells, enemyPlayer, j, i, 2))*3;
                        }
                        if (playerCellDiagonal == (B.X - 1) && enemyCellDiagonal == 0) {
                            playerCellDiagonal += isDoubleVictory(B, cells, currentPlayer, j, i, 2);
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
                        if(enemyCellDiagonalInverse == (B.X - 1) && playerCellDiagonalInverse == 0){
                            enemyCellDiagonalInverse += (isDoubleVictory(B, cells, enemyPlayer, j, i, 3))*3;
                        }
                        if (playerCellDiagonalInverse == (B.X - 1) && enemyCellDiagonalInverse == 0) {
                            playerCellDiagonalInverse += isDoubleVictory(B, cells, currentPlayer, j, i, 3);
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
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        /* Initialization of data */
        this.timeout_in_secs = timeout_in_secs;
        this.myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        this.yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        this.rand = new Random(System.currentTimeMillis());

        // M, N, X values
        this.M = M;
        this.N = N;
        this.X = X;

        /* Initilization of euristics */
        this.euristicCreator = new StartEuristicsCreator(M, N, X, first);
    }


    private void checkTime() throws TimeLimitExceededException {
        if (System.currentTimeMillis() - START_TIME > timeout_in_secs * 1000 - 100) {
            throw new TimeLimitExceededException();
        }
    }


    @Override
    public int selectColumn(CXBoard B) {
        // Impostiamo il tempo iniziale
        START_TIME = System.currentTimeMillis();
        
        // Se è il primo turno, giochiamo al centro
        if(B.getLastMove() == null || B.numOfMarkedCells() == 1){
            return B.N/2;
        }

        // Selezioniamo per prima una colonna a caso
        Integer ava[] = B.getAvailableColumns();
        int returnCol = ava[rand.nextInt(ava.length)];
        
        // We print the gameboard in ascii mode
        // We get the cells, for each row we print colored "O" which are LIGHTBLUE if they are the player and RED if they are the enemy
        // The colors are made using \033[96m and \033[91m for LIGHTBLUE and RED respectively. We also set the background to
        // the same colors, using \033[106m and \033[101m for LIGHTBLUE and RED respectively.
        
       CXCellState cells [][] = B.getBoard();
        String output = "Gameboard:\n";
        for(int i = 0; i < this.M; i++){
            for(int j = 0; j < this.N; j++){
                if(cells[i][j] == CXCellState.FREE){
                    output += "\033[30m\033[40m  \033[0m";
                }
                else if(cells[i][j] == CXCellState.P1){
                    output += "\033[106m\033[96m  \033[0m";
                }
                else{
                    output += "\033[101m\033[91m  \033[0m";
                }
            }
            output += "\n";
        }

        System.out.println(output);

        try {
            int maxScore = 0;
            for(Integer i : ava) {
                // Controlliamo il tempo
                checkTime();

                // Impostiamo le variabili per l'eval
                CXGameState gameStateAfterMove = B.markColumn(i);
                playerTileType = B.getLastMove().state;

                int currentMoveScore = eval(B, playerTileType, ava, gameStateAfterMove);
                
                // Se la nuova mossa è migliore della migliore trovata fin'ora, viene segnata come migliore
                if (maxScore < currentMoveScore) {
                    maxScore = currentMoveScore;
                    returnCol = i;

                    // Print the updated move:
                    System.out.println("New best move: " + returnCol + " with score: " + maxScore);
                }
                B.unmarkColumn();
            } 
        } catch (TimeLimitExceededException e) {
            // Abbiamo superato il limite di tempo, quindi ritorniamo la returnCol;
            return returnCol;
        }

        return B.getMarkedCells().length < 1 ? this.N/2 : B.getMarkedCells()[B.getMarkedCells().length-2].j + 1;

        //return returnCol;
    }

    @Override
    public String playerName() {
        return "HogRider";
    }
    

}
