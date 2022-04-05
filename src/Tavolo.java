import java.security.InvalidParameterException;

/**
 * Questa classe tavolo si occupa principalmente della gestione del tavolo di gioco,
 * inoltre, contiene i metodi relativi agli scanner delle mosse vincenti e bloccanti
 * applicati su righe, colonne, e le due diagonali
 * @author Matteo Malvezzi, Alessandro Verlanti
 * @see Game
 * **/
public class Tavolo {

    /** ------------------------------------ Attributes ------------------------------------ **/


    /** <strong>N_C</strong> contiene il numero di celle della matrice table **/
    public static final int N_C = 3;
    /** <strong>table</strong> matrice che rappresenta il tavolo di gioco **/
    private int[][] table;

    /** ------------------------------------ Constructor ------------------------------------ **/

    /**
     * Costruttore
     * Il costruttore si occupa di inizializzare ad 1 tutte le celle del tavolo di gioco
     * **/
    public Tavolo() {
        this.table = new int[N_C][N_C];
        for(int i = 0; i < N_C; i++) {
            for(int j = 0; j < N_C; j++){
                this.table[i][j] = 1;
            }
        }
    }

    /** --------------------------  Check winning and losing method  ------------------------- **/

    /**
     * winningMove
     * Metodo che si occupa di eseguire tutti i metodi di scansione di eventuali mosse vincenti su righe, colonne e le due diagonali
     * @return ritorna, se trova una mossa vincente, la posizione di riga e colonna per vincere, altrimenti null
     * **/
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

    /**
     * losingMove
     * Metodo che si occupa di eseguire tutti i metodi di scansione di eventuali mosse bloccanti su righe, colonne e le due diagonali
     * @return ritorna, se trova una mossa bloccante, la posizione di riga e colonna per bloccare una possibile mossa vincente avversaria, altrimenti null
     * **/
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

    /** ---------------------------------  Scanning Method ---------------------------------- **/

    /**
     * scanRowColumn
     * Scansiona tutte le righe e tutte le colonne e indica la posizione (riga e colonna) di un eventuale mossa vincente o bloccante
     * @param x tavolo di gioco
     * @param is_X parametro che indica se si sta controllando una mossa vincente (true) o una mossa bloccante (false)
     * @param is_row parametro che indica se si sta controllando una riga (true) o una colonna (false)
     * @return ritorna, se viene trovata una mossa vincente o bloccante, la riga e colonna in cui posizionare il punto, altrimenti null
     * **/
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

    /**
     * scanDiagonal
     * Scansiona le due diagonali e indica la posizione (riga e colonna) di un eventuale mossa vincente o bloccante
     * @param x tavolo di gioco
     * @param is_X parametro che indica se si sta controllando una mossa vincente (true) o una mossa bloccante (false)
     * @return ritorna, se viene trovata una mossa vincente o bloccante, la riga e colonna in cui posizionare il punto, altrimenti null
     * **/
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

    /** ---------------  Set/View Point ------------------ **/

    /**
     * setPoint
     * Permette di settare il punto all'interno del tavolo di gioco
     * @param r riga in cui settare il punto
     * @param c colonna in cui settare il punto
     * @param point valore del punto (3=nostro / 5=avversario)
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

    /**
     * getPoint
     * Permette di ottenere il punto di riga e colonna specificata
     * @param r riga in cui si trova il punto
     * @param c colonna in cui si trova il punto
     * @return ritorna il valore del punto selezionato
     * **/
    public int getPoint(int r, int c){
        try{
            if(r >= 0 && r <= 2 && c >= 0 && c <= 2){
                return this.table[r][c];
            }else{
                throw new InvalidParameterException();
            }
        }catch(InvalidParameterException e){ return -1;}
    }

    /** ---------------  Show table ------------------ **/

    public void showTable(){
        for(int i = 0; i < N_C; i++) {
            for(int j = 0; j < N_C; j++){
                System.out.printf(String.valueOf(this.table[i][j]) + " | ");
            }
            System.out.println("");
            System.out.println("----------");
        }
    }

    /**
     * showRealTable
     * Permette di visualizzare il tavolo di gioco
     * converte tutti i nostri punti (identificati dal valore 3) con una X e i punti dell'avversario (identificati dal valore 5) con un O
     * **/
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

    /**
     * rowProduct
     * Calcola il prodotto della riga specificata
     * @param r riga in cui si trova il punto
     * @return ritorna il prodotto della riga selezionata
     * **/
    public int rowProduct(int r){
        int product = 1;
        for(int i = 0; i < N_C; i++){
            int value_of_point = table[r][i];
            product*= value_of_point;
        }
        return product;
    }

    /**
     * columnProduct
     * Calcola il prodotto della colonna specificata
     * @param c colonna in cui si trova il punto
     * @return ritorna il prodotto della colonna selezionata
     * **/
    public int columnProduct(int c){
        int product = 1;
        for(int i = 0; i < N_C; i++){
            int value_of_point = table[i][c];
            product*= value_of_point;
        }
        return product;
    }

    /**
     * primaryDiagonalProduct
     * Calcola il prodotto della prima diagonale quella che va da 0-0 a 2-2
     * @return ritorna il prodotto della prima diagonale
     * **/
    public int primaryDiagonalProduct(){
        int product = 1;
        for(int i = 0; i < N_C; i++){
            product*= table[i][i];
        }
        return product;
    }

    /**
     * secondaryDiagonalProduct
     * Calcola il prodotto della prima diagonale quella che va da 0-2 a 2-0
     * @return ritorna il prodotto della seconda diagonale
     * **/
    public int secondaryDiagonalProduct(){
        int product = 1;
        for(int i = 0; i < N_C; i++){
            product *= table[N_C-i-1][i];
        }
        return product;
    }
}