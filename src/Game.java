import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Citazione di Verla
 * Tutte le strade non portano a roma portano a new int[][]{2, 2}
 * */

public class Game {

    /** ------------------------------------ Attributi ------------------------------------ **/
    public Tavolo table;

    /** ------------------------------------ Constructor ------------------------------------ **/

    /**
     * Game
     * Il costruttore si occupa di inserire il tavolo corrente del Game
     * @param current_table il tavolo precedentemente creato
     * @see Tavolo
     * **/
    public Game(Tavolo current_table) {
        this.table = current_table;
    }

    /** ------------------------------------ Methods ------------------------------------ **/

    /**
     * ifEqualPoint
     * Restituisce true se due punti sono uguali
     * @param a1 primo point
     * @param a2 secondo point
     * @return true if points are equal
     */
    public boolean ifEqualPoint(int[] a1, int[] a2){
        return (a1[0] == a2[0] && a1[1] == a2[1]);
    }

    /**
     * showSetOfPoint
     * Mostra un insieme di punti
     */
    public static void showSetOfPoint(ArrayList<int[]> setPoint){
        for (int[] ints : setPoint) {
            for (int anInt : ints) {
                System.out.print(anInt);
            }
            System.out.println("");
        }
    }

    /**
     * ifCorner
     * restituisce se è un corner o no
     */
    public static boolean ifCorner(int[] point){
        int[][] corner_int = { {0, 0}, {0, 2}, {2, 0}, {2, 2} };

        ArrayList<int[]> corner = new ArrayList<>(Arrays.asList(corner_int));

        for (int[] aCorner : corner) {
            if(point[0] == aCorner[0] && point[1] == aCorner[1]){ return true; }
        }return false;
    }

    /**
     * getFreePoint
     * Questo metodo restituisce le caselle libere di un tavolo
     * @return il metodo restituisce una ArrayList di ArrayList gli arrayList interni contengono sempre due valori, ovvero le cordinate xy della casella libera
     */
    public ArrayList<int[]> getFreePoint(){
        ArrayList<int[]> free_point = new ArrayList<>();

        for(int i =0; i< 3; i++){
            for(int j =0; j< 3; j++){
                if(this.table.getPoint(i, j) == 1){
                    free_point.add( new int[]{i, j} );
                }
            }
        }
        return free_point;
    }

    /**
     * getFreeCorner
     * Questo metodo restituisce i corner liberi
     * @return l'insieme dei corner liberi
     */
    public ArrayList<int[]> getFreeCorner(){
        ArrayList<int[]> fp = this.getFreePoint();

        int[][] corner_int = { {0, 0}, {0, 2}, {2, 0}, {2, 2} };

        ArrayList<int[]> corner = new ArrayList<>(Arrays.asList(corner_int));

        ArrayList<int[]> free_corner = new ArrayList<>();

        for (int[] ints : fp) {
            for (int[] ints1 : corner) {
                if(ints1[0] == ints[0] && ints1[1] == ints[1]){ free_corner.add(ints); }
            }
        }
        return free_corner;
    }
    /**
     * getFreeCentralZone
     * Questo metodo restituisce i punti non angoli non centro
     * @return l'insieme dei punti che non sono ne il centro ne gli angoli liberi
     */
    public ArrayList<int[]> getFreeCentralZone(){
        ArrayList<int[]> fp = this.getFreePoint();

        int[][] central_int = { {0, 1}, {1, 0}, {1, 2}, {2, 1} };

        ArrayList<int[]> central = new ArrayList<>(Arrays.asList(central_int));

        ArrayList<int[]> free_central = new ArrayList<>();

        for (int[] aFree : fp) {
            for (int[] aCentral : central) {
                if(ifEqualPoint(aFree, aCentral)){ free_central.add(aCentral); }
            }
        }
        return free_central;
    }

    /**
     * generateRandomPoint
     * Questo metodo restituisce una casella casuale dato un array di caselle possibili
     * @param possiblePoint insieme di possibili caselle estraibili
     * @return una casella libera, sempre espressa via array[tupla] di int
     */
    public int [] generateRandomPoint(ArrayList<int []> possiblePoint){
        Random random = new Random();
        int randomPointIndex = random.nextInt(possiblePoint.size());
        return possiblePoint.get(randomPointIndex);
    }

    /**
     * getOppositeCorner
     * Questo metodo restituisce il corner opposto a un corner dato
     * @return il corner opposto
     */
    public int[] getOppositeCorner(int[] initialPoint){
        if(initialPoint[0] == 0 && initialPoint[1] == 0){
            return new int[]{ 2, 2 };
        }else if(initialPoint[0] == 2 && initialPoint[1] == 2){
            return new int[]{ 0, 0 };
        }else if(initialPoint[0] == 2 && initialPoint[1] == 0){
            return new int[]{ 0, 2 };
        }else if(initialPoint[0] == 0 && initialPoint[1] == 2){
            return new int[]{ 2, 0 };
        }
        return null;
    }

    /**
     * getAdjacentFreePoint
     * Questo metodo restituisce i punti adiacenti a un punto liberi
     * @return l'insieme dei free point
     */
    public ArrayList<int[]> getAdjacentFreePoint(int[] initialPoint){
        int r = initialPoint[0];
        int c = initialPoint[1];
        ArrayList<int[]> afp = new ArrayList<>();
        ArrayList<int[]> fp = getFreePoint();

        for (int[] point : fp) {
            if(point[0] == r || point[1] == c ){ afp.add(point); }
        }
        return afp;
    }

    /**
     * getAdjacentFreeCorner
     * Questo metodo restituisce il primo corner adiacente libero
     * @return il primo corner adiacente libero
     */
    public int[] getAdjacentFreeCorner(int[] initialPoint){
        ArrayList<int[]> free_corner = getFreeCorner();

        ArrayList<int[]> adjacent_free_point = getAdjacentFreePoint(initialPoint);

        for (int[] afp : adjacent_free_point) {
            for (int[] fc : free_corner) {
                if (fc[0] == afp[0] && fc[1] == afp[1]) { return fc; }
            }
        }
        return null;
    }


    /** ------------------------------------ Funzioni pilota mosse se parto io ------------------------------------ **/

    /**
     * mossa1_PN
     * Pilotaggio mossa 1
     * @return il punto da mettere
     */
    public int[] mossa1_PN(){

        int[][] angoli_int = { {0, 0}, {0, 2}, {2, 0}, {2, 2} };

        ArrayList<int[]> angoli = new ArrayList<>(Arrays.asList(angoli_int));

        return generateRandomPoint(angoli);
    }

    /**
     * mossa2_PN
     * Pilotaggio mossa 2
     * @return lo sviluppo della gerarchia e il punto da mettere
     */
    public int[][] mossa2_PN(int[] enemy_point,  int[] mossa_1){

        if(enemy_point[0] == 1 && enemy_point[1] == 1){
            return new int[][]{ {0, 0}, getOppositeCorner(mossa_1) };
        }else {
            return new int[][]{ {3, 0}, getAdjacentFreeCorner(mossa_1) };
        }
    }

    /**
     * mossa3_PN
     * Pilotaggio mossa 3
     * @return lo sviluppo della gerarchia e il punto da mettere
     */
    public int[][] mossa3_PN(int[] mossa_2){

        if( this.table.rowProduct(mossa_2[0]) == 15 || this.table.columnProduct(mossa_2[1]) == 15 ){
            System.out.println("ME LO HA MESSO NELL'ADIACENTE al secondo");
            return new int[][]{ {0, 0}, getOppositeCorner(mossa_2) };
        }else{
            return new int[][]{ {0, 0}, getAdjacentFreeCorner(mossa_2) };
        }

    }

    /** ------------------------------------ Funzioni pilota mosse se parte l'avversario ------------------------------------ **/
    /**
     * mossa1_PL
     * Pilotaggio mossa 1 nel caso in cui partono loro
     * @return il punto da mettere
     */
    public int[][] mossa1_PL(int[] enemy_point){

        if(enemy_point[0] == 1 && enemy_point[1] == 1){
            return new int[][]{ { 1, 0 }, generateRandomPoint(getFreeCorner())};
        }else if(ifCorner(enemy_point)){
            System.out.println("CORNER");
            return new int[][]{ { 1, 1 }, new int[]{ 1, 1,} };
        }else{
            System.out.println("NOT CENTER NOT CORNER");
            return new int[][]{ { 1, 2 }, new int[]{ 1, 1,} };
        }
    }


    /**
     * mossa2_0_PL
     * Pilotaggio mossa 2 nel caso in cui partono loro (Tale metodo parte se la prima mossa è al centro)
     * @return il punto da mettere
     */
    public int[][] mossa2_0_PL(int[] mossa_1, int[] enemy_point){
        int[] opposite_of_mine = getOppositeCorner(mossa_1);

        if(ifEqualPoint(enemy_point, opposite_of_mine)){
            return new int[][]{ { 0, 1 }, generateRandomPoint(getFreeCorner()) };
        }else{
            return new int[][]{ { 0, 0 }, generateRandomPoint(getFreePoint()) };
        }
    }

    /**
     * mossa2_1_PL
     * Pilotaggio mossa 2 nel caso in cui partono loro (Tale metodo parte se la prima mossa è in un angolo)
     * @return il punto da mettere
     */
    public int[][] mossa2_1_PL(int[] enemey_point){
        int[] opposite_of_your = getOppositeCorner(enemey_point);

        if(ifEqualPoint(enemey_point, opposite_of_your)){
            return new int[][]{ {1, 0}, generateRandomPoint(getFreeCentralZone()) };
        }else{
            return new int[][]{ {1, 1}, generateRandomPoint(getFreeCorner()) };
        }
    }

    /**
     * mossa2_2_PL
     * Pilotaggio mossa 2 nel caso in cui partono loro (Tale metodo parte se la prima mossa non è ne al centro ne in un angolo)
     * @return il punto da mettere
     */
    public int[][] mossa2_2_PL(int[] enemy_point){

        if(ifCorner(enemy_point)){

            int prodotto_riga_1 = this.table.rowProduct(1);
            int prodotto_colonna_1 = this.table.columnProduct(1);

            if(prodotto_riga_1 != 15 && prodotto_colonna_1 == 15 ){
                //Libera la riga
                ArrayList<int[]> central_of_row = new ArrayList<>();
                central_of_row.add( new int[]{1, 0});
                central_of_row.add( new int[]{1, 2});

                return new int[][]{ {2, 0}, generateRandomPoint(central_of_row) };
            }else{
                //Libera la colonna
                ArrayList<int[]> central_of_column = new ArrayList<>();
                central_of_column.add( new int[]{0, 1});
                central_of_column.add( new int[]{2, 1});

                return new int[][]{ {2, 0}, generateRandomPoint(central_of_column) };
            }

        }else{
            //is lato

            ArrayList<int[]> free_corner = getFreeCorner();

            ArrayList<int[]> good_corner = new ArrayList<>();

            for (int[] aCorner : free_corner) {
                if(table.rowProduct(aCorner[0]) != 1 && table.columnProduct(aCorner[1]) != 1 ){
                    good_corner.add(aCorner);
                }
            }

            return new int[][]{ {2, 1}, generateRandomPoint(good_corner) };
        }
    }

}
