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
    public int[] winningMove(){
        int[][] x = this.table;

        int[] result;

        result = scanRowColumn(x, true, true);
        if(result != null){
            return result;
        }

        result = scanRowColumn(x, true, false);
        if(result != null){
            return result;
        }

        return scanDiagonal(x, true);
    }

    /** metodo che esegue tutti i controlli bloccanti sulla matrice **/
    public int[] losingMove(){
        int[][] x = this.table;

        int[] result;

        result = scanRowColumn(x, false, true);
        if(result != null){
            return result;
        }

        result = scanRowColumn(x, false, false);
        if(result != null){
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
        int good_row = 0;
        int good_column = 0;
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
                    good_row = row_move;
                    good_column = column_move;
                }

                product *= x[row_move][column_move];

                if (product == winning_count && flag) {
                    return new int[]{good_row, good_column};
                }
            }
        }
        return null;
    }

    /** scan winning or losing move on diagonal **/
    public int[] scanDiagonal(int[][] x, boolean is_X){
        int product = 1;
        int row_move = 0;
        int column_move = 0;
        int winning_count = 9;
        boolean flag = false;

        if(!is_X){
            winning_count = 25;
        }

        for (int i = 0; i < N_C ; i++) {

            if (x[i][i] == 1) {
                row_move = i;
                column_move = i;
                flag = true;
            }

            product *= x[i][i];

            if(product == winning_count && flag){
                return new int[] {row_move, column_move};
            }
        }

        product = 1;
        flag = false;

        for (int i = 0; i < N_C; i++){

            if (x[N_C-i-1][i] == 1){
                row_move = N_C-i-1;
                column_move = i;
                flag = true;
            }

            product *= x[N_C-i-1][i];

            if(product == winning_count && flag){
                return new int[] {row_move, column_move};
            }
        }

        return null;
    }



    /*** ---------------  Set/View Point ------------------***/

    /** set point
     * TODO mettere controllo per evitare che vengano settati punti gia occupati
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

    /** get point **/
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

    public void showRealTable(){
        for(int i = 0; i < N_C; i++) {
            for(int j = 0; j < N_C; j++){
                if(this.table[i][j] == 3){
                    System.out.printf("X | ");
                }else if(this.table[i][j] == 5){
                    System.out.printf("O | ");
                }else{
                    System.out.printf("  | ");
                }
            }
            System.out.println("");
            System.out.println("----------");
        }
    }

    /*** ---------------  get Row Product and Column Product ------------------***/

    public int rowProduct(int r ){
        int product = 1;
        for(int i = 0; i < 3; i++){
            int value_of_point = table[r][i];
            product*= value_of_point;
        }
        return product;
    }

    public int columnProduct(int c ){
        int product = 1;
        for(int i = 0; i < 3; i++){
            int value_of_point = table[i][c];
            product*= value_of_point;
        }
        return product;
    }


}