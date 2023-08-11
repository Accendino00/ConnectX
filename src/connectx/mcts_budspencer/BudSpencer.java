package connectx.mcts_budspencer;

import connectx.CXPlayer;

import connectx.CXBoard;

public class BudSpencer implements CXPlayer {

    private Tree movesTree; 


    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {

    }

    @Override
    public int selectColumn(CXBoard B) {
        return 0;
    }

    @Override
    public String playerName() {
        return "Bud Spencer";
    }
}