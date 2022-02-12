import java.security.InvalidParameterException;
import java.util.ArrayList;

public class Tavolo {

    /** attribute **/
    public static final int N_R = 3;
    public static final int N_C = 3;
    private int[][] table;

    /** constructor **/
    public Tavolo() {
        this.table = new int[N_R][N_C];
        for(int i = 0; i < N_R; i++) {
            for(int j = 0; j < N_C; j++){
                this.table[i][j] = 1;
            }
        }
    }

    /** scanning winning mosses on row **/
    public int[] winningMoveScanRow(int[][] x) {
        int product = 1;
        int row_win_move = 0;
        int column_win_move = 0;

        for (int i = 0; i < N_R; i++) {
            for (int j = 0; j < N_C; j++) {

                if (x[i][j] == 1) {
                    row_win_move = i;
                    column_win_move = j;
                }

                product *= x[i][j];

                if (product == 9) {
                    return new int[] {row_win_move, column_win_move};
                }
            }
        }
        return new int[] {-1, -1};
    }

    /** scanning winning mosses on column **/
    public int[] winningMoveScanColumn(int[][] x){
        int product = 1;
        int column_win_move = 0;
        int row_win_move = 0;

        for (int j = 0; j < N_C; j++){
            for (int i = 0; i < N_R; i++){

                if (x[j][i] == 1){
                    row_win_move = j;
                    column_win_move = i;
                }

                product *= x[j][i];

                if (product == 9){
                    return new int[] {row_win_move, column_win_move};
                }
            }
        }
        return new int[] {-1, -1};
    }

    /** scanning winning mosses on diagonal **/
    public int[] winningMoveScanDiagonal(int[][] x){
        int product = 1;
        int row_win_move = 0;
        int column_win_move = 0;

        for (int i = 0; i < N_R; i++){
            for (int j = 0; j < N_C; j++){
                if (i == j){

                    if (x[i][j] == 1){
                        row_win_move = i;
                        column_win_move = j;
                    }

                    product *= x[i][j];

                    if(product == 9){
                        return new int[] {-1, -1};
                    }
                }
            }
        }
        return new int[] {-1, -1};
    }


    //Partiamo noi funzioni
    public void tryL(int[][] x, int mossa_1_i){
        int[][] mossa_1 = { {0, 0}, {0, 2}, {2, 2}, {2, 0} };

    }

    /**
     * Set point method
     * **/
    public void setPoint(int r, int c, int point){
        try{
            if(point == 3 || point == 5 || point == 1){
                this.table[r][c] = point;
            }else{
                throw new InvalidParameterException();
            }
        }catch(InvalidParameterException e){}
    }
}