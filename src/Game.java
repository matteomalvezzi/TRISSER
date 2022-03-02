import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class Game {

    /** Attributi **/
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
     * getFreePoint
     * Questo metodo restituisce le caselle libere di un tavolo
     * @param t t rappresenta il tavolo di gioco da cui estrarre i punti liberi
     * @return il metodo restituisce una ArrayList di ArrayList gli arrayList interni contengono sempre due valori, ovvero le cordinate xy della casella libera
     */
    public ArrayList<ArrayList<Integer>> getFreePoint(Tavolo t){
        ArrayList<ArrayList<Integer>> free_point = new ArrayList<>();

        for(int i =0; i< 3; i++){
            for(int j =0; j< 3; j++){
                if(t.getPoint(i, j) == 1){
                    ArrayList<Integer> new_free_point = new ArrayList<>(Arrays.asList(i, j));
                    free_point.add(new_free_point);
                }
            }
        }
        return free_point;
    }

    /**
     * generateRandomPoint
     * Questo metodo restituisce una casella casuale dato un array di caselle possibili
     * @param possiblePoint insieme di possibili caselle estraibili
     * @return una casella libera sottoforma di arrayList di due valori
     */
    public ArrayList<Integer> generateRandomPoint(ArrayList<ArrayList<Integer>> possiblePoint){
        Random random = new Random();
        int randomPointIndex = random.nextInt(possiblePoint.size());
        return possiblePoint.get(randomPointIndex);
    }
}
