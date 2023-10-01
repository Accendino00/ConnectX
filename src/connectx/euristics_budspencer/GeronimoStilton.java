package connectx.euristics_budspencer;

import connectx.CXBoard;
import connectx.CXPlayer;
import connectx.CXGameState;
import connectx.CXCellState;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;
import javax.naming.TimeLimitExceededException;

import connectx.euristics.StartEuristicsCreator;

public class GeronimoStilton implements CXPlayer {
    private int timeout_in_secs;
    private long START_TIME;

    int eval(CXBoard B) {
        CXCellState[][] board = B.getBoard();
        if(B.getLastMove() == null){
            return B.N/2;
            };
        else{
            B.getLastMove()
        }
        }


        
        return 0;
    }

    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        
    }


    private void checkTime() throws TimeLimitExceededException {
        if (System.currentTimeMillis() - START_TIME > timeout_in_secs * 1000 - 100) {
            throw new TimeLimitExceededException();
        }
    }


    @Override
    public int selectColumn(CXBoard B) {
        checkTime();
        try{
            int col = eval(B);
            return col;
        } catch(TimeLimitExceededException e){
            System.out.println("Time limit exceeded")
        } 

    }

    @Override
    public String playerName() {
        
    }
    

}
