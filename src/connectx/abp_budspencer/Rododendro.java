package connectx.abp_budspencer;

import connectx.euristics_budspencer.GeronimoStilton;
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
    private boolean player;


    private StartEuristicsCreator euristicsCreator;


    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
    this.M = M;
    this.N = N;
    this.X = X;
    this.myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
    this.yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
    this.timeout_in_ms = timeout_in_secs * 1000;
    this.player = first ? true : false;

    /* Inizializzo l'euristica */
    euristicsCreator = new StartEuristicsCreator(N, M, X, first);

    }

    @Override
    public String playerName() {
        return "Rododendro";
    }


    @Override
    public int selectColumn(CXBoard b) {
        int move = 0;
        int max = Integer.MIN_VALUE;
        int depth = 5;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        for (int i = 0; i < b.N; i++) {
            if (!(b.fullColumn(i))) {
                int value = minimax(b, depth, alpha, beta, player);
                if (value > max) {
                    max = value;
                    move = i;
                }
            }
        }
        return move;
    }

    private int minimax(CXBoard b, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || b.gameState() == myWin || b.gameState() == yourWin) {
            return evaluate(b);
        }
        if (maximizingPlayer) {
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < b.N; i++) {
                if (!(b.fullColumn(i))) {
                    b.markColumn(i);
                    int value = minimax(b, depth - 1, alpha, beta, false);
                    b.unmarkColumn();
                    max = Math.max(max, value);
                    alpha = Math.max(alpha, value);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < b.N; i++) {
                if (!(b.fullColumn(i))) {
                    b.markColumn(i);
                    int value = minimax(b, depth - 1, alpha, beta, true);
                    b.unmarkColumn();
                    min = Math.min(min, value);
                    beta = Math.min(beta, value);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return min;
        }
    }

    //controlla dove poter mettere un pezzo

    private int middleColumnScore(CXBoard b){
        int middle = b.N/2;
        if (!(b.fullColumn(middle))){
            return 2;
        }
        else{
            return 0;
        }
    }

    private int numberOfPiecesInAColumn(CXBoard b, int column, CXCellState player){
        int count = 0;
        for (int i = 0; i < b.N; i++){
            if (b.cellState(column, i) != CXCellState.FREE && b.cellState(column, i) == player){
                count++;
            }
        }
        return count;
    }


    //controlla dove poter mettere due pezzi vicini

    private int numberOfPiecesInARow(CXBoard b, int column, int row, CXCellState player){
        int count = 0;
        for (int i = 0; i < b.N; i++){
            if (b.cellState(i, row) != CXCellState.FREE && b.cellState(i, row) == player){
                count++;
            }
        }
        return count;
    }

    //check connected pieces in a diagonal
    private int numberOfPiecesInADiagonal(CXBoard b, int column, int row, CXCellState player){
        int count = 0;
        int i = column;
        int j = row;
        while (i < b.N && j < b.M){
            if (b.cellState(i, j) != CXCellState.FREE && b.cellState(i, j) == player){
                count++;
            }
            i++;
            j++;
        }
        i = column;
        j = row;
        while (i >= 0 && j >= 0){
            if (b.cellState(i, j) != CXCellState.FREE && b.cellState(i, j) == player){
                count++;
            }
            i--;
            j--;
        }
        return count;
    }

    //check connected pieces in the other diagonal
    private int numberOfPiecesInADiagonal2(CXBoard b, int column, int row, CXCellState player){
        int count = 0;
        int i = column;
        int j = row;
        while (i < b.N && j >= 0){
            if (b.cellState(i, j) != CXCellState.FREE && b.cellState(i, j) == player){
                count++;
            }
            i++;
            j--;
        }
        i = column;
        j = row;
        while (i >= 0 && j < b.M){
            if (b.cellState(i, j) != CXCellState.FREE && b.cellState(i, j) == player){
                count++;
            }
            i--;
            j++;
        }
        return count;
    }

    //apply score based of number of pieces in a row
    private int numberOfPiecesInARowScore(CXBoard b, int column, int row, CXCellState player){
        return numberOfPiecesInARow(b, column, row, b.currentPlayer() == 0 ? CXCellState.P1 : CXCellState.P2);
    }

    //apply score based of number of pieces in a diagonal
    private int numberOfPiecesInADiagonalScore(CXBoard b, int column, int row, CXCellState player){
        return numberOfPiecesInADiagonal(b, column, row, b.currentPlayer() == 0 ? CXCellState.P1 : CXCellState.P2);
    }

    //apply score based of number of pieces in the other diagonal
    private int numberOfPiecesInADiagonal2Score(CXBoard b, int column, int row, CXCellState player){
        return numberOfPiecesInADiagonal2(b, column, row, b.currentPlayer() == 0 ? CXCellState.P1 : CXCellState.P2);
    }

    //apply score based of number of pieces in a column
    private int numberOfPiecesInAColumnScore(CXBoard b, int column, CXCellState player){
        return numberOfPiecesInAColumn(b, column, b.currentPlayer() == 0 ? CXCellState.P1 : CXCellState.P2);
    }
    
    //evaluate the board using the scoring functions above
    private int evaluate(CXBoard b){
        int score = 0;
        for (int i = 0; i < b.N; i++){
            for (int j = 0; j < b.M; j++){
                if (b.cellState(i, j) == CXCellState.FREE){
                    if(b.currentPlayer() == 0){
                        score += middleColumnScore(b);
                        score += numberOfPiecesInAColumnScore(b, i, CXCellState.P1);
                        score += numberOfPiecesInARowScore(b, i, j, CXCellState.P1);
                        score += numberOfPiecesInADiagonalScore(b, i, j, CXCellState.P1);
                        score += numberOfPiecesInADiagonal2Score(b, i, j, CXCellState.P1);
                    }
                    else{
                        score -= middleColumnScore(b);
                        score -= numberOfPiecesInAColumnScore(b, i, CXCellState.P2);
                        score -= numberOfPiecesInARowScore(b, i, j, CXCellState.P2);
                        score -= numberOfPiecesInADiagonalScore(b, i, j, CXCellState.P2);
                        score -= numberOfPiecesInADiagonal2Score(b, i, j, CXCellState.P2);
                    }
                }
            }
        }
        return score;
    }

    //controlla dove poter mettere tre pezzi vicini
    //etc etc
    //controlla se l'avversario ha due pezzi vicini
    //controlla se l'avversario ha tre pezzi vicini
    //etc etc
    //vedere se si può fare una funzione che controlla se ci sono pezzi vicini
    //e che ritorna un array di array di int con le coordinate dei pezzi vicini
    //dare un punteggio ad ogni mossa in base a quanti pezzi vicini ci sono
    //dare un punteggio ad ogni mossa in base a quanti pezzi vicini ha l'avversario
    //scegliere la mossa con il punteggio più alto
    //se ci sono più mosse con lo stesso punteggio, scegliere a caso tra quelle
    //se non ci sono mosse con punteggio alto, scegliere a caso tra le mosse possibili


}
    
    /* 
    //iterative deepening
    private int iterativeDeepening(CXBoard b, int depth, int alpha, int beta, boolean maximizingPlayer) {
        int move = 0;
        int max = Integer.MIN_VALUE;
        for (int i = 0; i < b.M; i++) {
            if (!(b.fullColumn(i))) {
                b.markColumn(i);
                int value = minimax(b, depth - 1, alpha, beta, false);
                b.unmarkColumn();
                if (value > max) {
                    max = value;
                    move = i;
                }
            }
        }
        return move;
    }
    */

    //valutazione
    
    





/*
public class Rododendro implements CXPlayer {

    public Rododendro() {
        super("Rododendro");
    }

    @Override
    public int nextMove(CXBoard b) {
        int move = 0;
        int max = Integer.MIN_VALUE;
        int depth = 5;
        int alpha = Integer.MIN_VALUE;
        int beta = Integer.MAX_VALUE;
        for (int i = 0; i < b.getWidth(); i++) {
            if (b.canPlay(i)) {
                int value = minimax(b, depth, alpha, beta, true);
                if (value > max) {
                    max = value;
                    move = i;
                }
            }
        }
        return move;
    }


            int score = 0;
        int[][] board = b.getBoard();
        int width = b.M;
        int height = b.N;
        int player = b.getCurrentPlayer();
        int opponent = 3 - player;
        int[][][] patterns = {
                {{0, 0}, {0, 1}, {0, 2}, {0, 3}},
                {{0, 0}, {1, 0}, {2, 0}, {3, 0}},
                {{0, 0}, {1, 1}, {2, 2}, {3, 3}},
                {{0, 0}, {1, -1}, {2, -2}, {3, -3}} // 4    4   
        };
        for (int[][] pattern : patterns) {
            int myCount = 0;
            int yourCount = 0;
            for (int[] p : pattern) {
                int x = p[0];
                int y = p[1];
                if (x >= 0 && x < width && y >= 0 && y < height) {
                    if (board[x][y] == player) {
                        myCount++;
                    } else if (board[x][y] == opponent) {
                        yourCount++;
                    }
                }
            }
            if (myCount == 4) {
                return 1000000;
            } else if (yourCount == 4) {
                return -1000000;
            } else if (myCount == 3 && yourCount == 0) {
                score += 100;
            } else if (myCount == 2 && yourCount == 0) {
                score += 10;
            } else if (myCount == 1 && yourCount == 0) {
                score += 1;
            } else if (yourCount == 3 && myCount == 0) {
                score -= 100;
            } else if (yourCount == 2 && myCount == 0) {
                score -= 10;
            } else if (yourCount == 1 && myCount == 0) {
                score -= 1;
            }
        }
        return score;
    

    private int minimax(CXBoard b, int depth, int alpha, int beta, boolean maximizingPlayer) {
        if (depth == 0 || b.isGameOver()) {
            return evaluate(b);
        }
        if (maximizingPlayer) {
            int max = Integer.MIN_VALUE;
            for (int i = 0; i < b.getWidth(); i++) {
                if (b.canPlay(i)) {
                    b.play(i);
                    int value = minimax(b, depth - 1, alpha, beta, false);
                    b.undo();
                    max = Math.max(max, value);
                    alpha = Math.max(alpha, value);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return max;
        } else {
            int min = Integer.MAX_VALUE;
            for (int i = 0; i < b.getWidth(); i++) {
                if (b.canPlay(i)) {
                    b.play(i);
                    int value = minimax(b, depth - 1, alpha, beta, true);
                    b.undo();
                    min = Math.min(min, value);
                    beta = Math.min(beta, value);
                    if (beta <= alpha) {
                        break;
                    }
                }
            }
            return min;
        }
    }

    private int evaluate(CXBoard b) {
        int score = 0;
        int[][] board = b.getBoard();
        int width = b.getWidth();
        int height = b.getHeight();
        int player = b.getCurrentPlayer();
        int opponent = 3 - player;
        int[][][] patterns = {
                {{0, 0}, {0, 1}, {0, 2}, {0, 3}},
                {{0, 0}, {1, 0}, {2, 0}, {3, 0}},
                {{0, 0}, {1, 1}, {2, 2}, {3, 3}},
                {{0, 0}, {1, -1}, {2, -2}, {3, -3}} // 4    4   






    }
*/



