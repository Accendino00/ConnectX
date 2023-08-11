package connectx.euristics;

import connectx.CXBoard;

public class StartEuristicsCreator {
    // Size of the game
    private int N, M, X;

    // First player
    private boolean first;

    // First column chosen
    private int firstColumn;

    /**
     * Constructor
     * 
     * @param N Rows
     * @param M Columns
     * @param X Number of consecutive cells to win
     * @param first First player
     */
    public StartEuristicsCreator(int N, int M, int X, boolean first) {
        this.N = N;
        this.M = M;
        this.X = X;
        this.first = first;

        this.firstColumn = -1;
    }

    /**
     * To be called every move to interpret the move.
     * For now, it only interprets the first move.
     * 
     * @param B Board
     */
    public void interpretMove(CXBoard B, int colChosen) {
        int numOfMoves = B.getMarkedCells().length;
        if (numOfMoves == 0 || numOfMoves == 1) {
            // First column chosen:
            this.firstColumn = colChosen;
        }
    }

    public void saveToFileEuristics() {
        // Read the csv file that is saved like:
        // <col>;<value>\n
        // and increase the value of the "firstColumn" <col>
        // The file is called "winFirstColumn.csv"
        
        // Read the file
        //System.out.println("Lettura file");
        java.io.BufferedReader br = null;
        java.io.FileReader fr = null;
        java.io.FileWriter fw = null;

        int [][] values = new int[this.N][2];
        for (int i = 0; i < this.N; i++) {
            values[i][0] = i;
            values[i][1] = 0;
        }

        String fileName = N+"_"+M+"_"+X+"_winFirstColumn"+(this.first?"1":"2")+".csv";

        try {
            fr = new java.io.FileReader(fileName);
            br = new java.io.BufferedReader(fr);

            String line = br.readLine();
            while (line != null) {
                //System.out.println("Line: " + line);
                String[] splitLine = line.split(";");
                int col = Integer.parseInt(splitLine[0]);
                int value = Integer.parseInt(splitLine[1]);
                values[col][1] = value;
                line = br.readLine();
            }
            br.close();
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File non trovato: " + e);
        } catch (Exception e) {
            System.out.println("Errore nella lettura del file: " + e);
        }

        // Increase the value of the firstColumn
        values[firstColumn][1]++;

        // Write the file
        
        try {
            fw = new java.io.FileWriter(fileName);
            for (int i = 0; i < this.N; i++) {
                fw.write(values[i][0] + ";" + values[i][1] + "\n");
            }
            fw.close();
        } catch (java.io.FileNotFoundException e) {
            System.out.println("File non trovato: " + e);
        } catch (Exception e) {
            System.out.println("Errore nella scrittura del file: " + e);
        }
    }

}
