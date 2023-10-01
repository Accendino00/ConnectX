package connectx.negamax_budspencer;

import connectx.CXPlayer;
import connectx.CXBoard;
import connectx.CXGameState;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Random;
import javax.naming.TimeLimitExceededException;

import connectx.euristics.StartEuristicsCreator;

public class BudSpencer implements CXPlayer {

    private int timeout_in_secs;
    private long START_TIME;
    private Random rand; 
    private CXGameState myWin;
    private CXGameState yourWin;
    
    
    // Euristics
    private StartEuristicsCreator euristicsCreator;

    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {
        // Salvo il timeout + il seed casuale e chi vince
        this.timeout_in_secs = timeout_in_secs;
        this.myWin = first ? CXGameState.WINP1 : CXGameState.WINP2;
        this.yourWin = first ? CXGameState.WINP2 : CXGameState.WINP1;
        this.rand = new Random(System.currentTimeMillis());

        // Euristics
        euristicsCreator = new StartEuristicsCreator(N, M, X, first);
    }

    /**
     * Check if the time limit is exceeded.
     * It throws an exception if there is less than 100 ms left on the clock.
     * 
     * @throws TimeLimitExceededException
     */
    private void checkTime() throws TimeLimitExceededException {
        if (System.currentTimeMillis() - START_TIME > timeout_in_secs * 1000 - 100) {
            throw new TimeLimitExceededException();
        }
    }

    @Override
    public int selectColumn(CXBoard B) {
		this.START_TIME = System.currentTimeMillis();
        Integer ava[] = B.getAvailableColumns();

        // We initialize the return col with a random value at the beginning
        int returnCol = ava[rand.nextInt(ava.length)];

        // Get maximum value of ava
        int maxvalue = 0;
        for (int j = 0; j < ava.length; j++) {
            if (ava[j]>=maxvalue) {
                maxvalue = ava[j];
            }
        }
        Boolean [] badMoves = new Boolean [maxvalue+1];
        for (int j = 0; j < badMoves.length; j++) {
            badMoves[j] = false;
        }

        try {
            for(Integer i : ava) {
                // First we see if we can win with 1 move
                checkTime();

                // Now we check if by marking the column we get the win
                CXGameState state = B.markColumn(i);
                B.unmarkColumn();
                if (state == myWin) {
                    
                    /* !!IMPORTANT!! - Remember to remove euristics creator */
                    euristicsCreator.saveToFileEuristics();
                    returnCol = i;
                    
                    // I exit the loop because I found a winning move
                    // I don't return directly since i prefer to have 1 return at the end

                    return returnCol;
                }



                for (Integer j : ava) {
                    checkTime();
                    state = B.markColumn(j);

                    if (state != CXGameState.OPEN) {
                        B.unmarkColumn();
                        continue;
                    }

                    if (!B.fullColumn(i)) {
                        state = B.markColumn(i);
                        if (state == yourWin) {
                            badMoves[j] = true;
                        }
                        B.unmarkColumn();
                    }

                    B.unmarkColumn();
                }
            }
        } catch (TimeLimitExceededException e) {
            System.out.println("Time limit exceeded");
        } catch (Throwable e) {
            System.out.println("Error: " + e);
        }

        // Count the number of not "badMoves" where the column isn't full:
        int count = 0;
        for (int j = 0; j < badMoves.length; j++) {
            if (!badMoves[j] && !B.fullColumn(j)) {
                count++;
            }
        }

        int [] notBadMoves = new int [count];
        for (int j = 0; j < badMoves.length; j++) {
            if(!badMoves[j] && !B.fullColumn(j)) {
                notBadMoves[count-1] = j;
                count--;
            }
        }

        // Select random not bad move if the length is more than 1
        if (notBadMoves.length >= 1) {
            returnCol = notBadMoves[rand.nextInt(notBadMoves.length)];
        }

        /* !!IMPORTANT!! - Remember to remove euristics creator */
        euristicsCreator.interpretMove(B, returnCol);

        return returnCol;
    }

    @Override
    public String playerName() {
        return "Bud Spencer NegaMax Analizzatore";
    }
}
