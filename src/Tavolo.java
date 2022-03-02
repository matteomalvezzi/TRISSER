import java.security.InvalidParameterException;

public class Tavolo {

    /** attribute **/
    public static final int N_C = 3; // cells number
    private int[][] table;

    /** constructor **/
    public Tavolo() {
        this.table = new int[N_C][N_C];
        for(int i = 0; i < N_C; i++) {
            for(int j = 0; j < N_C; j++){
                this.table[i][j] = 1;
            }
        }
    }

    /*** ---------------  Check winning and losing method  ------------------***/

    /** metodo che esegue tutti i controlli vincenti sulla matrice **/
    public int[] winningMove(int[][] x){

        int[] result;

        result = scanRowColumn(x, true, true);
        if(result[0] != -1){
            return result;
        }

        result = scanRowColumn(x, true, false);
        if(result[0] != -1){
            return result;
        }

        return scanDiagonal(x, true);
    }

    /** metodo che esegue tutti i controlli bloccanti sulla matrice **/
    public int[] losingMove(int [][] x){

        int[] result;

        result = scanRowColumn(x, false, true);
        if(result[0] != -1){
            return result;
        }

        result = scanRowColumn(x, false, false);
        if(result[0] != -1){
            return result;
        }

        return scanDiagonal(x, false);
    }

    /*** ---------------  Scanning Method ------------------***/

    /** scan winning or losing move on row and column **/
    public int[] scanRowColumn(int[][] x, boolean is_X, boolean is_row) { // matrice, mossa vincente o bloccante, controllo riga o colonna
        int product = 1;
        int row_move;
        int column_move;
        int winning_count = 9;
        boolean flag;

        if(!is_X){
            winning_count = 25;
        }

        for (int i = 0; i < N_C; i++) {
            product = 1;
            flag = false;
            for (int j = 0; j < N_C; j++) {
                row_move = i;
                column_move = j;
                if (!is_row){
                    row_move = j;
                    column_move = i;
                }

                if (x[row_move][column_move] == 1) {
                    flag = true;
                }

                product *= x[row_move][column_move];

                if (product == winning_count && flag) {
                    return new int[]{row_move, column_move};
                }
            }
        }
        return new int[] {-1, -1};
    }

    /** scan winning or losing move on diagonal **/
    public int[] scanDiagonal(int[][] x, boolean is_X){
        int product = 1;
        int row_win_move = 0;
        int column_win_move = 0;
        int winning_count = 9;
        boolean flag = false;

        if(!is_X){
            winning_count = 25;
        }

        for (int i = 0; i < N_C ; i++) {

            if (x[i][i] == 1) {
                row_win_move = i;
                column_win_move = i;
                flag = true;
            }

            product *= x[i][i];

            if(product == winning_count && flag){
                return new int[] {row_win_move, column_win_move};
            }
        }

        product = 1;
        flag = false;

        for (int i = 0; i < N_C; i++){

            if (x[N_C-i-1][i] == 1){
                row_win_move = N_C-i-1;
                column_win_move = i;
                flag = true;
            }

            product *= x[N_C-i-1][i];

            if(product == winning_count && flag){
                return new int[] {row_win_move, column_win_move};
            }
        }

        return new int[] {-1, -1};
    }



    /*** ---------------  Set/View Point ------------------***/

    /** set point **/
    public void setPoint(int r, int c, int point){
        try{
            if(point == 3 || point == 5 || point == 1){
                this.table[r][c] = point;
            }else{
                throw new InvalidParameterException();
            }
        }catch(InvalidParameterException e){}
    }

    /** set point **/
    public int getPoint(int r, int c){
        try{
            if(r >= 0 && r <= 2 && c >= 0 && c <= 2){
                return this.table[r][c];
            }else{
                throw new InvalidParameterException();
            }
        }catch(InvalidParameterException e){ return -1;}
    }


    /*** ---------------  Show table ------------------***/

    public void showTable(){
        for(int i = 0; i < N_C; i++) {
            for(int j = 0; j < N_C; j++){
                System.out.printf(String.valueOf(this.table[i][j]) + " | ");
            }
            System.out.println("");
            System.out.println("----------");
        }
    }
}