package connectx.budspencermiopadre;

import connectx.CXPlayer;

import java.util.ArrayList;
import java.util.List;

import connectx.CXBoard;

public class budspencermiopadre implements CXPlayer {



    @Override
    public void initPlayer(int M, int N, int X, boolean first, int timeout_in_secs) {

    }

    @Override
    public int selectColumn(CXBoard B) {
        return 0;
    }

    @Override
    public String playerName() {
        return "budspencermiopadre";
    }
}

class TreeNode {
    int value;
    List<TreeNode> children;

    public TreeNode(int value) {
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(TreeNode child) {
        this.children.add(child);
    }
}