package connectx.abp_budspencer;

import connectx.CXBoard;
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


    private StartEuristicsCreator euristicsCreator;


        @Override
        public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
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
        public String playerName() {
            return "Rododendro";
        }


        @Override
        public int selectColumn(CXBoard b) {
            int move = 0;
            int max = Integer.MIN_VALUE;
            int depth = 8;
            int alpha = Integer.MIN_VALUE;
            int beta = Integer.MAX_VALUE;
            for (int i = 0; i < b.M; i++) {
                if (!(b.fullColumn(i))) {
                    int value = minimax(b, depth, alpha, beta, false);
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
                return evaluate(b, 3);
            }
            if (maximizingPlayer) {
                int max = Integer.MIN_VALUE;
                for (int i = 0; i < b.M; i++) {
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
                for (int i = 0; i < b.M; i++) {
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
        private int evaluate(CXBoard b, int i) {
            int score = 0;
            CXCellState[][] board = b.getBoard();
            int width = b.M;
            int height = b.N;
            int player = b.currentPlayer();
            int opponent = 3 - player;
            int[][][] patterns = {
                    {{0, 0}, {0, 1}, {0, 2}, {0, 3}},
                    {{0, 0}, {1, 0}, {2, 0}, {3, 0}},
                    {{0, 0}, {1, 1}, {2, 2}, {3, 3}},
                    {{0, 0}, {1, -1}, {2, -2}, {3, -3}} // 4    4   
            };
            for (int[][] pattern : patterns) {
                for (int i = 0; i < width - 3; i++) {
                    for (int j = 0; j < height - 3; j++) {
                        int myCount = 0;
                        int yourCount = 0;
                        for (int[] p : pattern) {
                            if (board[i + p[0]][j + p[1]].ordinal() == player - 1) {
                                myCount++;
                            } else if (board[i + p[0]][j + p[1]].ordinal() == opponent - 1) {
                                yourCount++;
                            }
                        }
                        if (myCount > 0 && yourCount == 0) {
                            score += Math.pow(10, myCount);
                        } else if (yourCount > 0 && myCount == 0) {
                            score -= Math.pow(10, yourCount);
                        }
                    }
                }
            }
            return score;
        }
}



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
*/



