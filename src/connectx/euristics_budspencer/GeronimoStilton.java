package connectx.euristics_budspencer;

import connectx.CXBoard;
import connectx.CXCell;
import connectx.CXPlayer;
import connectx.CXGameState;
import connectx.CXCellState;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;
import javax.naming.TimeLimitExceededException;

import connectx.euristics.StartEuristicsCreator;

public class GeronimoStilton implements CXPlayer {
    private CXCellState player;
    private int timeout_in_secs;
    private long START_TIME;
    private Random rand; 
    private CXGameState myWin;
    private CXGameState yourWin;

    int checkDirection(CXBoard B, int i, int j, CXCellState player){
        // direction = 0 -> vertical, direction = 1 -> horizontal, direction = 2 -> diagonal, direction = 3 -> anti-diagonal
        int direction = 0;
        int score_vertical = 0;
        // Check the vertical direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i+k, j) == player){
                score_vertical++;
            }
        }
        // Check vertical direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i-k, j) == player){
                score_vertical++;
            }
        }
        int score_horizontal = 0;
        // Check the horizontal direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i, j+k) == player){
                score_horizontal++;
            }
        }
        // Check the horizontal direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i, j-k) == player){
                score_horizontal++;
            }
        }
        int score_diagonal = 0;
        // Check the diagonal direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i+k, j+k) == player){
                score_diagonal++;
            }
        }
        // Check the diagonal direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i-k, j-k) == player){
                score_diagonal++;
            }
        }
        int score_antidiagonal = 0;
        // Check the anti-diagonal direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i-k, j+k) == player){
                score_antidiagonal++;
            }
        }
        // Check the anti-diagonal direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i+k, j-k) == player){
                score_antidiagonal++;
            }
        }

        int max = 0;
        if(score_vertical > max){
            max = score_vertical;
            direction = 0;
        }
        if(score_horizontal > max){
            max = score_horizontal;
            direction = 1;
        }
        if(score_diagonal > max){
            max = score_diagonal;
            direction = 2;
        }
        if(score_antidiagonal > max){
            max = score_antidiagonal;
            direction = 3;
        }

        return direction;
    }

    int score(CXBoard B, int i, int j, CXCellState player){
        // Check all the possible directions in the positions i, j on the board B for the player and sum all the cells that are occupied by the player in the same direction and return the score
        int score_vertical = 0;
        // Check the vertical direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i+k, j) == player){
                score_vertical++;
            }
        }
        // Check vertical direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i-k, j) == player){
                score_vertical++;
            }
        }
        int score_horizontal = 0;
        // Check the horizontal direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i, j+k) == player){
                score_horizontal++;
            }
        }
        // Check the horizontal direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i, j-k) == player){
                score_horizontal++;
            }
        }
        int score_diagonal = 0;
        // Check the diagonal direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i+k, j+k) == player){
                score_diagonal++;
            }
        }
        // Check the diagonal direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i-k, j-k) == player){
                score_diagonal++;
            }
        }
        int score_antidiagonal = 0;
        // Check the anti-diagonal direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i-k, j+k) == player){
                score_antidiagonal++;
            }
        }
        // Check the anti-diagonal direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i+k, j-k) == player){
                score_antidiagonal++;
            }
        }
        int max = 0;

        if(score_vertical > max){
            max = score_vertical;
        }
        if(score_horizontal > max){
            max = score_horizontal;
        }
        if(score_diagonal > max){
            max = score_diagonal;
        }
        if(score_antidiagonal > max){
            max = score_antidiagonal;
        }
        return max;
    
    }
    
    int eval(CXBoard B) {
        
        CXCellState[][] board = B.getBoard();
        // Check if we are player 1 or player 2
        if(B.getLastMove() == null){
                player = CXCellState.P1;
            } else {
                player = CXCellState.P2;
            }
            if(B.getLastMove() == null){
                return B.N/2;
            } 
            
            this.START_TIME = System.currentTimeMillis();
            Integer ava[] = B.getAvailableColumns();

            // We initialize the return col with a random value at the beginning
            int returnCol = ava[rand.nextInt(ava.length)];

            try {
                for(Integer i : ava) {
                    // First we see if we can win with 1 move
                    checkTime();

                    // Now we check if by marking the column we get the win
                    CXGameState state = B.markColumn(i);
                    B.unmarkColumn();
                    if (state == myWin) {
                        returnCol = i;
                        
                        // I exit the loop because I found a winning move
                        // I don't return directly since i prefer to have 1 return at the end

                        break; 
                    }


                    // Now we check if we can block the adversary from winning
                    // We mark a random column and see if he can has a winning move
                    if (ava[0] != i)
                        state = B.markColumn(ava[0]);
                    else if (ava.length > 1)
                        state = B.markColumn(ava[1]);
                    else // If we can't play more than 1 move we skip this column
                        continue; 

                    // If there is a winning move that we haven't checked yet or a draw we skip this column
                    if (state != CXGameState.OPEN){
                        B.unmarkColumn();
                        continue;
                    }
                        
                    // We mark the column and check if they win, if they do we save the column
                    state = B.markColumn(i);
                    if (state == yourWin) {
                        returnCol = i;
                    }

                    B.unmarkColumn();
                    B.unmarkColumn();  
                }
                    int max_score = 0;
                    int best_col = 0;
                    int best_row = 0;
                    for(int i = 0; i < B.N; i++){
                        for(int j=0; j < B.M; j++){  
                            if(board[i][j] == player){
                                if(score (B, i, j, player) > max_score){
                                    max_score = score(B, i, j, player);
                                    best_col = i;
                                    best_row = j;
                                }     
                            }
                            // Check if the cell is occupied by the player and then check if there is a nearby free cell to the left or right and place the piece there
                            /* if(board[i][j] == player){
                                if(board[i-1][j] == CXCellState.FREE){
                                    returnCol = i-1;
                                } else if(board[i+1][j] == CXCellState.FREE){
                                    returnCol = i+1;
                                }
                                // Check also if the diagonal cells are free and playable
                                else if(board[i-1][j-1] == CXCellState.FREE && board[i-1][j-2] != CXCellState.FREE){
                                    returnCol = i-1;
                                } else if(board[i-1][j+1] == CXCellState.FREE && board[i-1][j] != CXCellState.FREE){
                                    returnCol = i-1;
                                } else if(board[i+1][j-1] == CXCellState.FREE && board[i+1][j-2] != CXCellState.FREE){
                                    returnCol = i+1;
                                } else if(board[i+1][j+1] == CXCellState.FREE && board[i+1][j] != CXCellState.FREE){
                                    returnCol = i+1;
                                }

                            }*/
                        }
                    }
                    int direction = checkDirection(B, best_col, best_row, player);
                    if(direction == 0){
                        if(board[best_col][best_row-1] == CXCellState.FREE){
                            returnCol = best_col;
                        } else if(board[best_col][best_row+1] == CXCellState.FREE){
                            returnCol = best_col;
                        }
                    } else if(direction == 1){
                        if(board[best_col-1][best_row] == CXCellState.FREE){
                            returnCol = best_col-1;
                        } else if(board[best_col+1][best_row] == CXCellState.FREE){
                            returnCol = best_col+1;
                        }
                    } else if(direction == 2){
                        if(board[best_col-1][best_row-1] == CXCellState.FREE){
                            returnCol = best_col-1;
                        } else if(board[best_col+1][best_row+1] == CXCellState.FREE){
                            returnCol = best_col+1;
                        }
                    } else if(direction == 3){
                        if(board[best_col-1][best_row+1] == CXCellState.FREE){
                            returnCol = best_col-1;
                        } else if(board[best_col+1][best_row-1] == CXCellState.FREE){
                            returnCol = best_col+1;
                        }
                    }

            } catch (TimeLimitExceededException e) {
                System.out.println("Time limit exceeded");
            } catch (Throwable e) {
                System.out.println("Error: " + e);
            }
            return returnCol;
        }   
    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        this.timeout_in_secs = timeout_in_secs;
        this.myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        this.yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        this.rand = new Random(System.currentTimeMillis());
    }


    private void checkTime() throws TimeLimitExceededException {
        if (System.currentTimeMillis() - START_TIME > timeout_in_secs * 1000 - 100) {
            throw new TimeLimitExceededException();
        }
    }


    @Override
    public int selectColumn(CXBoard B) {
        if(eval(B) == -1){
            return rand.nextInt(B.N);
        } else {
            return eval(B);
        }
    }

    @Override
    public String playerName() {
        return "GeronimoStilton";
    }
    

}
