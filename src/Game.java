import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

/**
 * Citazione di Verla IMPORTANTE
 * Tutte le strade non portano a roma portano a new int[][]{2, 2}
 **/

/**
 * <strong>LE FUNZIONALITA DI QUESTA CLASSE</strong><hr><br>
 * <i>Gestisco il Game</i>
 * <br>
 * <ul>
 * <li>Gestisco il tavolo</li>
 * <li>Calcolo le mosse vincenti</li>
 * <li>registroPRENDO LA MAIL le mosse nemiche e le mie mosse</li>
 * <li>piloto le mosse</li>
 * <li>Calcolo di possibili mosse bloccanti o vincenti</li>
 * </ul>
 * @author Matteo Malvezzi, Alessandro Verlanti
 * @see Tavolo
 * @see Room
 * **/
public class Game {

    /** ------------------------------------ Attributes ------------------------------------ **/

    /** <strong>table</strong> tavolo di gioco **/
    public Tavolo table;
    /** <strong>enemy_move</strong> Arraylist che si occupa di memorizzare le mosse avversarie **/
    public ArrayList<int[]> enemy_move;
    /** <strong>my_move</strong> Arraylist che si occupa di memorizzare le nostre mosse **/
    public ArrayList<int[]> my_move;
    /** <strong>whoStart</strong> indica chi sarà il primo bot ad eseguire la mossa **/
    public boolean whoStart; //true parto io false parte lui
    /** <strong>myMosseCounter</strong> memorizza la quantità di mosse eseguite **/
    public int myMosseCounter;

    /** <strong>ng</strong> indica la prossima gerarchia da seguire (utile per stabilire la direzione da percorrere per eseguire la prossima mossa) **/
    int ng; // nextGerarchy

    /** ------------------------------------ Constructor ------------------------------------ **/

    /**
     * Costruttore
     * Il costruttore si occupa di inserire il tavolo corrente del Game
     * @param current_table il tavolo precedentemente creato
     * @param wS flag WhoStart, se true partiamo noi se false partono loro
     * @see Tavolo
     * **/
    public Game(Tavolo current_table, boolean wS) {
        my_move = new ArrayList<>();
        enemy_move = new ArrayList<>();
        myMosseCounter = 0;

        int ng = 0;
        this.whoStart = wS;
        this.table = current_table;
    }

    /** ------------------------------------ Gaming Methods ----------------------------------- **/

    /**
     * playGamePN
     * Gioca una mossa in una partita dove partiamo noi
     * ..Non si necessita di una mossa fatta da noi dato che ogni volta che vengono restituite vengono registrate
     * @param idx_t indice di partita [turno] (determina il punto in cui è ferma la partita, si incrementa ogni volta che noi giochiamo)
     * @param enemy mossa avversario del turno corrente
     * @return mossa da restituire
     * **/
    public int[] playGamePN(int idx_t, int[] enemy){
        myMosseCounter++;
        int[] pwm;
        int[] plm;
        switch (idx_t){
            /** ---- Risposta numero 1---- **/
            case 1:
                return mossa1_PN();

            /** ---- Risposta numero 2---- **/
            case 2:
                return mossa2_PN(enemy_move.get(0), my_move.get(0));

            /** ---- Risposta numero 3---- **/
            case 3:
                //Scanner
                pwm = this.table.winningMove(); //possibile winning move
                if(pwm!=null){return pwm;}
                plm = this.table.losingMove();  //possible losing move
                if(plm!=null){return plm; }

                return mossa3_PN(my_move.get(0));
            /** ---- Risposta numero 4-5 ---- **/
            case 4:

            case 5:
                pwm = this.table.winningMove(); //possibile winning move
                if(pwm!=null){return pwm; }
                plm = this.table.losingMove();  //possible losing move
                if(plm!=null){return plm; }

                int [] finale = generateRandomPoint(getFreePoint());
                return finale;

            default:
                return null;
        }
    }
    /**
     * playGamePL
     * Gioca una mossa in una partita dove partono loro
     * @param idx_t indice di partita [turno] (determina il punto in cui è ferma la partita, si incrementa ogni volta che noi giochiamo)
     * @param enemy mossa avversario del turno corrente
     * @return mossa da restituire
     * **/
    public int[] playGamePL(int idx_t, int[] enemy){
        myMosseCounter++;
        int[] pwm;
        int[] plm;
        int[][] next_move;
        switch (idx_t){
            /** ---- Risposta numero 1---- **/
            case 1:
                next_move = mossa1_PL(enemy);
                this.ng = next_move[0][1];
                return next_move[1];

            /** ---- Risposta numero 2---- **/
            case 2:
                //Scanner
                pwm = this.table.winningMove(); //possibile winning move
                if(pwm!=null){return pwm;}
                plm = this.table.losingMove();  //possible losing move
                if(plm!=null){return plm; }

                //Pilotaggio
                switch (this.ng){
                    case 0:
                        next_move = mossa2_0_PL(this.my_move.get(0), this.enemy_move.get(1));
                        this.ng  = next_move[0][1];
                        return next_move[1];
                    case 1:
                        next_move = mossa2_1_PL(this.enemy_move.get(0), this.enemy_move.get(1));
                        this.ng  = next_move[0][1];
                        return next_move[1];
                    case 2:
                        next_move = mossa2_2_PL(this.enemy_move.get(1));
                        this.ng  = next_move[0][1];
                        return next_move[1];
                    default:
                        break;
                }
            /** ---- Risposta numero 3 ---- **/
            case 3:

                //Scanner
                pwm = this.table.winningMove(); //possibile winning move
                if(pwm!=null){return pwm;}
                plm = this.table.losingMove();  //possible losing move
                if(plm!=null){return plm;}

                next_move = mossa3_PL();
                return next_move[1];


            /** ---- Risposta numero 4 ---- **/
            case 4:

                //Scanner
                pwm = this.table.winningMove(); //possibile winning move
                if(pwm!=null){return pwm;}
                plm = this.table.losingMove();  //possible losing move
                if(plm!=null){return plm;}

                int [] finale = generateRandomPoint(getFreePoint());
                return finale;

            default:
                return null;
        }
    }
    /**
     * isFree
     * Controlla se una casella è libera
     * @param p punto di cui fare la verifica
     * @return true if free, false if occupy
     * **/
    public boolean isFree(int[] p){
        int value = this.table.getPoint(p[0], p[1]);
        if(value == 1){ return true; }
        else{ return false; }
    }

    /**
     * setEnemyPoint
     * Setta il point nel tavolo e registra la mossa come mossa nemica
     * @param p punto da settare
     * **/
    public void setEnemyPoint(int[] p){
        this.enemy_move.add(p);
        this.table.setPoint(p[0], p[1],5);
    }

    /**
     * setMyPoint
     * Setta il point nel tavolo e registra la mossa come mossa amica
     * @param p punto da settare
     * **/
    public void setMyPoint(int[] p){
        this.my_move.add(p);
        this.table.setPoint(p[0], p[1],3);
    }

    /** ------------------------------------ Pilot Methods ------------------------------------ **/

    /**
     * ifEqualPoint
     * Restituisce true se due punti sono uguali
     * @param a1 primo point
     * @param a2 secondo point
     * @return true se i due punti sono uguali
     */
    public boolean ifEqualPoint(int[] a1, int[] a2){
        return (a1[0] == a2[0] && a1[1] == a2[1]);
    }

    /**
     * showSetOfPoint
     * Mostra un insieme di punti
     * @param setPoint insieme di punti
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
     * @param point riga e colonna del punto da controllare
     * @return ritorna true se è un angolo altrimenti false
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
     * @param initialPoint angolo di cui ottenere l'opposto
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
     * @param initialPoint punto di cui ottenere l'adiacente libero
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
     * @param initialPoint punto di cui ottenere il primo angolo adiacente libero
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
     * @param enemy_point_1 prima mossa avversario
     * @param mossa_1 prima mossa che abbiamo fatto noi
     * @return lo sviluppo della gerarchia e il punto da mettere
     */
    public int[] mossa2_PN(int[] enemy_point_1,  int[] mossa_1){

        if(enemy_point_1[0] == 1 && enemy_point_1[1] == 1){
            return getOppositeCorner(mossa_1);
        }else {
            return getAdjacentFreeCorner(mossa_1);
        }
    }

    /**
     * mossa3_PN
     * Pilotaggio mossa 3
     * @param mossa_1 prima mossa che abbiamo fatto noi
     * @return lo sviluppo della gerarchia e il punto da mettere
     */
    public int[] mossa3_PN(int[] mossa_1){

        if( this.table.rowProduct(mossa_1[0]) == 15 || this.table.columnProduct(mossa_1[1]) == 15 ){
            return getOppositeCorner(mossa_1);
        }else{
            return getAdjacentFreeCorner(mossa_1);
        }

    }

    /** ------------------------------------ Funzioni pilota mosse se parte l'avversario ------------------------------------ **/
    /**
     * mossa1_PL
     * Pilotaggio mossa 1 nel caso in cui partono loro
     * @param enemy_point_1 prima mossa avversario
     * @return il punto da mettere
     */
    public int[][] mossa1_PL(int[] enemy_point_1){

        if(enemy_point_1[0] == 1 && enemy_point_1[1] == 1){
            return new int[][]{ { 1, 0 }, generateRandomPoint(getFreeCorner())};
        }else if(ifCorner(enemy_point_1)){
            return new int[][]{ { 1, 1 }, new int[]{ 1, 1,} };
        }else{
            return new int[][]{ { 1, 2 }, new int[]{ 1, 1,} };
        }
    }

    /**
     * mossa2_0_PL
     * Pilotaggio mossa 2 nel caso in cui partono loro (Tale metodo parte se la prima mossa è al centro)
     * @param mossa_1 mia prima mossa
     * @param enemy_point_2 sua seconda mossa [perché la prima è ovviamente il centro]
     * @return il punto da mettere
     */
    public int[][] mossa2_0_PL(int[] mossa_1, int[] enemy_point_2){
        int[] opposite_of_mine = getOppositeCorner(mossa_1);

        if(ifEqualPoint(enemy_point_2, opposite_of_mine)){
            return new int[][]{ { 0, 1 }, generateRandomPoint(getFreeCorner()) };
        }else{
            return new int[][]{ { 0, 0 }, generateRandomPoint(getFreePoint()) };
        }
    }

    /**
     * mossa2_1_PL
     * Pilotaggio mossa 2 nel caso in cui partono loro (Tale metodo parte se la prima mossa è in un angolo)
     * @param enemy_point_1 prima mossa dell'avversario,
     * @param enemey_point_2 seconda mossa dell'avversario
     * @return il punto da mettere
     */
    public int[][] mossa2_1_PL(int[] enemy_point_1, int[] enemey_point_2){
        int[] opposite_of_your = getOppositeCorner(enemy_point_1);

        if(ifEqualPoint(enemey_point_2, opposite_of_your)){
            return new int[][]{ {1, 0}, generateRandomPoint(getFreeCentralZone()) };
        }else{
            return new int[][]{ {1, 1}, generateRandomPoint(getFreeCorner()) };
        }
    }

    /**
     * mossa2_2_PL
     * Pilotaggio mossa 2 nel caso in cui partono loro (Tale metodo parte se la prima mossa non è ne al centro ne in un angolo)
     * @param enemy_point_2 seconda mossa dell'avversario
     * @return il punto da mettere
     */
    public int[][] mossa2_2_PL(int[] enemy_point_2){

        if(ifCorner(enemy_point_2)){

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
                if(table.rowProduct(aCorner[0]) != 1 || table.columnProduct(aCorner[1]) != 1 ){
                    good_corner.add(aCorner);
                }
            }

            return new int[][]{ {2, 1}, generateRandomPoint(good_corner) };
        }
    }
    /**
     * mossa3_PL
     * Pilotaggio mossa 3 nel caso in cui partono loro (Tale metodo parte se la prima mossa non è ne al centro ne in un angolo dato che siamo sotto alla terza gerarchia)
     * @return il punto da mettere
     */
    public int[][] mossa3_PL(){

        int prodotto_diagonale_1 = this.table.primaryDiagonalProduct();
        int prodotto_diagonale_2 = this.table.secondaryDiagonalProduct();

        if(prodotto_diagonale_1 ==15){
            if( isFree(new int[]{0, 0}) ){
                return new int[][]{ {3, 1}, new int[]{0, 0} };
            }else if( isFree(new int[]{2, 2}) ){
                return new int[][]{ {3, 1}, new int[]{2, 2} };
            }
        }
        else if(prodotto_diagonale_2 == 15) {
            if (isFree(new int[]{0, 2})) {
                return new int[][]{{3, 1}, new int[]{0, 2}};
            } else if (isFree(new int[]{2, 0})) {
                return new int[][]{{3, 1}, new int[]{2, 0}};
            }
        }
        return null;
    }
}
