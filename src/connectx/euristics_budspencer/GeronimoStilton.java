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
    private CXCellState playerTileType;
    private int timeout_in_secs;
    private long START_TIME;
    private Random rand; 
    private CXGameState myWin;
    private CXGameState yourWin;

    private StartEuristicsCreator euristicCreator;

   
    


    private int scoregen(CXBoard B, int i, int j, CXCellState player){
        // Check all the possible directions in the positions i, j on the board B for the player and sum all the cells that are occupied by the player in the same direction and return the score
        int score_vertical = 1;
        // Check the vertical direction
       // System.out.println("Sono entrato in scoregen del player");
        int k = 1;
       // System.out.println(B.M);
        //System.out.println(j);
        //System.out.println(i);
    //   try{
        if(B.M-i < B.X){ // If B.M-i is greater than B.X, then the cell in question is not useful for the score because we cant win in the vertical direction starting from it
            if((0 < i+k) && (i+k < B.M)){
                while(B.cellState(i+k, j) == player){
                //System.out.println("Sono entrato nel primo while di scoregen");
                score_vertical*=2;
                k++;
                if((0 >= i+k) || (i+k >= B.M)){
                    break;
                }
            //   System.out.println(k);
            }
        }
    }
        else{
            score_vertical = 0;
        }
        if((0 >= i+k) || (i+k >= B.M)|| B.cellState(i+k,j) != CXCellState.FREE){
            score_vertical=0;
            //System.out.println("Sono entrato nell'if");
        }
        //System.out.println("Sono uscio dal primo while di scoregen");
        /*else if(B.cellState(i+k,j) != CXCellState.FREE){
            score_vertical=0;
        }*/
        int score_horizontal = 1;
        // Check the horizontal(vertical?) direction
        k = 1;
        if(B.N - j < B.X){
        if((0 < j+k) && (j+k < B.N)){
            //System.out.println("Sono entrato nel secondo if di scoregen");
            while(B.cellState(i, j+k) == player){
                //System.out.println("Sono entrato nel secondo while di scoregen");
                score_horizontal*=2;
                k++;
                if((0 >= j+k) || (j+k >= B.N)){
                    break;
                }
            }
        }
    }
        else{
            score_horizontal = 0;
        }
        if((0 >= j+k) || (j+k >= B.N)|| B.cellState(i,j+k) != CXCellState.FREE){
            score_horizontal=0;
        }
        /*else if(B.cellState(i,j+k) != CXCellState.FREE){
            score_horizontal=0;
        }*/
        k = 1;
        int score_diagonal = 1;
      //  System.out.println(B.N);
       // System.out.println(B.M);
       // System.out.println(j);
      //  System.out.println(i);
        // Check the diagonal direction
        if(B.N - j < B.X && B.M - i < B.X){
        if((0 < i+k) && (i+k < B.M) && ((0 < j+k) && (j+k < B.N))){
            //System.out.println("Sono entrato nel terzo if di scoregen");
            while(B.cellState(i+k, j+k) == player){
                //System.out.println("Sono entrato nel terzo while di scoregen");
                score_diagonal*=2;
                k++;
                if((0 >= i+k) || (i+k >= B.M) || ((0 >= j+k) || (j+k >= B.N))){
                    break;
                }
            }
        }
    }
        else{
            score_diagonal = 0;
        }
        if(((0 >= i+k) || (i+k >= B.M)) || ((0 >= j+k) || (j+k >= B.N)) || B.cellState(i+k,j+k) != CXCellState.FREE){
            score_diagonal=0;
        }
        /*else if(B.cellState(i+k,j+k) != CXCellState.FREE){
            score_diagonal=0;
        }*/
        k = 1;
        int score_antidiagonal = 1;
        // Check the anti-diagonal direction
        if(B.N - j > B.X && B.M - (B.M - i) > B.X){
        if((0 < i+k) && (i+k < B.M) && ((0 <= j-k) && (j-k < B.N))){
            //System.out.println("Sono entrato nel quarto if di scoregen");
            while(B.cellState(i+k, j-k) == player){
                //System.out.println("Sono entrato nel quarto while di scoregen");
                score_antidiagonal*=2;
                k++;
                if((0 > i+k) || (i+k >= B.M) || ((0 > j-k) || (j-k >= B.N))|| B.cellState(i+k,j-k) != CXCellState.FREE){
                    break;
                }
            }
        }
    }
        else{
            score_antidiagonal = 0;
        }
        if(((0 > i+k) || (i+k >= B.M)) || ((0 > j-k) || (j-k >= B.N))){
            score_antidiagonal=0;

        }
        /*else if(B.cellState(i-k,j+k) != CXCellState.FREE){
            score_antidiagonal=0;

        }*/

        //System.out.println("Sono arrivato alla fine di scoregen");
        
        return score_antidiagonal + score_diagonal + score_horizontal + score_vertical;
  //  } catch(IndexOutOfBoundsException e){
  //     return 0;
  //  }
    }

    // Make a general score function
    private int scorefinal(CXBoard B, CXCellState player, Integer[] ava, CXGameState state){
        int score = 0;
        CXCellState enemy_player = player == CXCellState.P1 ? CXCellState.P2 : CXCellState.P1;
        int player_score = 0;
        int enemy_score = 0;


     //   System.out.println("sono entrato in scorefinal");
        //try{
            //CXCell lastmove = B.getLastMove();
            //    if (B.isWinningMove(lastmove.i, lastmove.j)) {
             //       return score = 2147483646;
             //   }
             
             if(state == myWin){
                return score = Integer.MAX_VALUE;
             }
             

            /*
                Questa funzione dovrebbe venire usata al di fuori dell'EVAL,
                in quanto sta valutando due mosse in avanti e non da un resoconto della situazione attuale
                della board, ovvero il ruolo che ha questa funzione (EVAL)

                Da mantenere comunque perché utile, però esterno all'EVAL
            
            int j;
            boolean stop;
            for (j = 0, stop = false; j < ava.length && !stop; j++) {
               
                if (!B.fullColumn(ava[j])) {
                    CXGameState state2 = B.markColumn(ava[j]);
                    if (state2 == yourWin) {
                        stop = true; // We don't need to check more since we found a move we need to block
                        enemy_score = Integer.MAX_VALUE;
                    }
                    B.unmarkColumn();
                }
            } 
            */


            CXCell cells[] = B.getMarkedCells();
            int limit = cells.length - 1;

            // Il loop parte al contrario in quanto le mosse più utili da controllare saranno le ultime
            for(int i = limit; i > 0; i--){
                if(cells[i].state == player){
                    player_score += scoregen(B, cells[i].i, cells[i].j, player);
                }
                else if(cells[i].state == enemy_player){
                    enemy_score += scoregen(B, cells[i].i, cells[i].j, enemy_player);
                }
            }




     //       System.out.println("sono uscito dal forloop e ritorno lo score");
            score = player_score - enemy_score;
      //      System.out.println(score);
            return score;
    //} catch (TimeLimitExceededException e) {
      //  System.out.println("Time limit exceeded in scorefinal");
       // return player_score - enemy_score;
  //  } catch (Throwable e) {
    //    System.out.println("Error: " + e);
   // }
   // return score;
        /* try{
            for(int i = 0; i < B.N; i++){
                for(int j = 0; j < B.M; j++){
                    checkTime();
                    if(B.cellState(i, j) == player){
                        player_score += scoregen(B, i, j, player);
                    }
                    else if(B.cellState(i, j) == enemy_player){
                        enemy_score += scoregen(B, i, j, enemy_player);
                    }
                }
            }
        score = player_score - enemy_score;
        return score;
        } catch (TimeLimitExceededException e) {
            System.out.println("Time limit exceeded in scorefinal");
            return player_score - enemy_score;
        } catch (Throwable e) {
            System.out.println("Error: " + e);
        }
        return score = 0; */
    }

    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        /* Initialization of data */
        this.timeout_in_secs = timeout_in_secs;
        this.myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        this.yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        this.rand = new Random(System.currentTimeMillis());
        this.playerTileType = first ? CXCellState.P1 : CXCellState.P2;
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
        /*if(eval(B) == -1){
            return rand.nextInt(B.N);
        } else {
            return eval(B);
        }*/ 
        if(B.getLastMove() == null || B.numOfMarkedCells() == 1){
            return B.N/2;
        }

        Integer ava[] = B.getAvailableColumns();
        int returnCol = ava[rand.nextInt(ava.length)];
        

      //  try {
          int max_score = 0;
            for(Integer i : ava) {
      //          System.out.println("arrivo fin qua?");
       //         System.out.println("arrivo fin qua parte 2?");
                CXGameState state = B.markColumn(i);
       //         System.out.println("arrivo fin qua parte 3?");
                int scorefinal = scorefinal(B, playerTileType, ava, state);
      //          System.out.println("what about qua");
                if(max_score < scorefinal){
                    max_score = scorefinal;
                    returnCol = i;
                        };
                B.unmarkColumn();
                } 
          //  } //catch (TimeLimitExceededException e) {
                //System.out.println("Time limit exceeded");
           // } //catch (Throwable e) {
                //System.out.println("Error: " + e);
           // }
        return returnCol;
    }

    @Override
    public String playerName() {
        return "GeronimoStilton";
    }
    

}
