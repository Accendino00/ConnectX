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

    private StartEuristicsCreator euristicCreator;

    // Given a board B, a position i, j and a direction from checkDirection(), check if the next cell in that direction is free and playable and return the column
    int checkNextEmpty(CXBoard B, int i, int j, CXCellState player){
        int direction = checkDirection(B, i, j, player);
        switch(direction){
            case -4:
                while(B.cellState(i+1, j-1) != CXCellState.FREE && (0 < i+1 && i+1 < B.N) && (0 < j-1 && j-1 < B.M)){
                    i++;    
                    j--;
                }
                break;
            case -3:
                while(B.cellState(i+1, j+1) != CXCellState.FREE && (0 < i+1 && i+1 < B.N) && (0 < j+1 && j+1 < B.M)){
                    i++;
                    j++;
                }
                break;
            case -2:
                while(B.cellState(i, j-1) != CXCellState.FREE && (0 < i && i < B.N) && (0 < j-1 && j-1 < B.M)){
                    j--;
                }
                break;
            case -1:
                while(B.cellState(i-1, j) != CXCellState.FREE && (0 < i-1 && i-1 < B.N) && (0 < j && j < B.M)){
                    i--;
                }
                break;
            case 1:
                while(B.cellState(i+1, j) != CXCellState.FREE && (0 < i+1 && i+1 < B.N) && (0 < j && j < B.M)){
                    i++;
                }
                break;
            case 2:
                while(B.cellState(i, j+1) != CXCellState.FREE && (0 < i && i < B.N) && (0 < j+1 && j + 1< B.M)){
                    j++;
                }
                break;
            case 3:
                while(B.cellState(i-1, j-1) != CXCellState.FREE && (0 < i-1 && i-1 < B.N) && (0 < j-1 && j-1 < B.M)){
                    i--;
                    j--;
                }
                break;
            case 4:
                while(B.cellState(i-1, j+1) != CXCellState.FREE && (0 < i-1 && i-1 < B.N) && (0 < j+1 && j+1 < B.M)){
                    i--;
                    j++;
                }
                break;
        }
        return i;
    }   
    

    int checkDirection(CXBoard B, int i, int j, CXCellState player){
        // direction = 1 -> vertical, direction = 2 -> horizontal, direction = 3 -> diagonal, direction = 4 -> anti-diagonal, direction = -1 -> vertical opposite, direction = -2 -> horizontal opposite, direction = -3 -> diagonal opposite, direction = -4 -> anti-diagonal opposite
        int direction = 0;
        // Check all the possible directions in the positions i, j on the board B for the player and sum all the cells that are occupied by the player in the same direction and return the score
        int score_vertical = 0;
        // Check the vertical direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i+k, j) == player){
                score_vertical++;
            }
            else if((0 < i+k) && (i+k < B.N)){
                break;
            }
            else if(B.cellState(i+k,j) != CXCellState.FREE){
                break;
            }
        }
        int score_vertical_opposite = 0;
        // Check vertical direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i-k, j) == player){
                score_vertical_opposite++;
            }
            else if((0 < i-k) && (i-k < B.N)){
                break;
            }
            else if(B.cellState(i-k,j) != CXCellState.FREE){
                break;
            }
        }
        int score_horizontal = 0;
        // Check the horizontal direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i, j+k) == player){
                score_horizontal++;
            }
            else if((0 < j+k) && (j+k < B.M)){
                break;
            }
            else if(B.cellState(i,j+k) != CXCellState.FREE){
                break;
            }
        }
        int score_horizontal_opposite = 0;
        // Check the horizontal direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i, j-k) == player){
                score_horizontal_opposite++;
            }
            else if((0 < j-k) && (j-k < B.M)){
                break;
            }
            else if(B.cellState(i,j-k) != CXCellState.FREE){
                break;
            }
        }
        int score_diagonal = 0;
        // Check the diagonal direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i+k, j+k) == player){
                score_diagonal++;
            }
            else if(((0 < i+k) && (i+k < B.N)) || ((0 < j+k) && (j+k < B.M))){
                break;
            }
            else if(B.cellState(i+k,j+k) != CXCellState.FREE){
                break;
            }
        }
        int score_diagonal_opposite = 0;
        // Check the diagonal direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i-k, j-k) == player){
                score_diagonal_opposite++;
            }
            else if(((0 < i-k) && (i-k < B.N)) || ((0 < j-k) && (j-k < B.M))){
                break;
            }
            else if(B.cellState(i-k,j-k) != CXCellState.FREE){
                break;
            }
        }
        int score_antidiagonal = 0;
        // Check the anti-diagonal direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i-k, j+k) == player){
                score_antidiagonal++;
            }
            else if(((0 < i-k) && (i-k < B.N)) || ((0 < j+k) && (j+k < B.M))){
                break;
            }
            else if(B.cellState(i-k,j+k) != CXCellState.FREE){
                break;
            }
        }
        int score_antidiagonal_opposite = 0;
        // Check the anti-diagonal direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i+k, j-k) == player){
                score_antidiagonal_opposite++;
            }
            else if(((0 < i+k) && (i+k < B.N)) || ((0 < j-k) && (j-k < B.M))){
                break;
            }
            else if(B.cellState(i+k,j-k) != CXCellState.FREE){
                break;
            }
        }

        int max = 0;
        if(score_antidiagonal_opposite > max){
            max = score_antidiagonal_opposite;
            direction = -4;
        }
        if(score_diagonal_opposite > max){
            max = score_diagonal_opposite;
            direction = -3;
        }
        if(score_horizontal_opposite > max){
            max = score_horizontal_opposite;
            direction = -2;
        }
        if(score_vertical_opposite > max){
            max = score_vertical_opposite;
            direction = -1;
        }
        if(score_vertical > max){
            max = score_vertical;
            direction = 1;
        }
        if(score_horizontal > max){
            max = score_horizontal;
            direction = 2;
        }
        if(score_diagonal > max){
            max = score_diagonal;
            direction = 3;
        }
        if(score_antidiagonal > max){
            max = score_antidiagonal;
            direction = 4;
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
            else if((0 < i+k) && (i+k < B.N)){
                break;
            }
            else if(B.cellState(i+k,j) != CXCellState.FREE){
                break;
            }
        }
        int score_vertical_opposite = 0;
        // Check vertical direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i-k, j) == player){
                score_vertical_opposite++;
            }
            else if((0 < i-k) && (i-k < B.N)){
                break;
            }
            else if(B.cellState(i-k,j) != CXCellState.FREE){
                break;
            }
        }
        int score_horizontal = 0;
        // Check the horizontal direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i, j+k) == player){
                score_horizontal++;
            }
            else if((0 < j+k) && (j+k < B.M)){
                break;
            }
            else if(B.cellState(i,j+k) != CXCellState.FREE){
                break;
            }
        }
        int score_horizontal_opposite = 0;
        // Check the horizontal direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i, j-k) == player){
                score_horizontal_opposite++;
            }
            else if((0 < j-k) && (j-k < B.M)){
                break;
            }
            else if(B.cellState(i,j-k) != CXCellState.FREE){
                break;
            }
        }
        int score_diagonal = 0;
        // Check the diagonal direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i+k, j+k) == player){
                score_diagonal++;
            }
            else if(((0 < i+k) && (i+k < B.N)) || ((0 < j+k) && (j+k < B.M))){
                break;
            }
            else if(B.cellState(i+k,j+k) != CXCellState.FREE){
                break;
            }
        }
        int score_diagonal_opposite = 0;
        // Check the diagonal direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i-k, j-k) == player){
                score_diagonal_opposite++;
            }
            else if(((0 < i-k) && (i-k < B.N)) || ((0 < j-k) && (j-k < B.M))){
                break;
            }
            else if(B.cellState(i-k,j-k) != CXCellState.FREE){
                break;
            }
        }
        int score_antidiagonal = 0;
        // Check the anti-diagonal direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i-k, j+k) == player){
                score_antidiagonal++;
            }
            else if(((0 < i-k) && (i-k < B.N)) || ((0 < j+k) && (j+k < B.M))){
                break;
            }
            else if(B.cellState(i-k,j+k) != CXCellState.FREE){
                break;
            }
        }
        int score_antidiagonal_opposite = 0;
        // Check the anti-diagonal direction in the opposite direction
        for(int k = 0; k < B.X; k++){
            if(B.cellState(i+k, j-k) == player){
                score_antidiagonal_opposite++;
            }
            else if(((0 < i+k) && (i+k < B.N)) || ((0 < j-k) && (j-k < B.M))){
                break;
            }
            else if(B.cellState(i+k,j-k) != CXCellState.FREE){
                break;
            }
        }
        int max = 0;
        if(score_antidiagonal_opposite > max){
            max = score_antidiagonal_opposite;
            max = -4;
        }
        if(score_diagonal_opposite > max){
            max = score_diagonal_opposite;
            max = -3;
        }
        if(score_horizontal_opposite > max){
            max = score_horizontal_opposite;
            max = -2;
        }
        if(score_vertical_opposite > max){
            max = score_vertical_opposite;
            max = -1;
        }
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
    
    /**
     * Funzione che valuta lo stato della board e ritorna un intero
     * che rappresenta quanto un determinato stato è buono.
     * 
     * @param B La "board" da analizzare
     * @return the column where the player wants to play 
     *         (dovrebbe ritornare un intero che rappresenta quanto un determinato stato è buono)
     *         (quindi dopo verrà scelta la mossa che ritorna l'intero più alto dalla funzione che sceglie la colonna)
     */
    int eval(CXBoard B) {
        // Prendo la board per poter usare le celle e confrontarle
        CXCellState[][] board = B.getBoard();

        // Controllo se sono il giocatore 1 o il giocatore 2
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
                    returnCol = checkNextEmpty(B, best_col, best_row, player);
                    /*if(direction == 0){
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
                    }*/

            } catch (TimeLimitExceededException e) {
                System.out.println("Time limit exceeded");
            } catch (Throwable e) {
                System.out.println("Error: " + e);
            }
            return returnCol;
        }   
    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        /* Initialization of data */
        this.timeout_in_secs = timeout_in_secs;
        this.myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        this.yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        this.rand = new Random(System.currentTimeMillis());

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
