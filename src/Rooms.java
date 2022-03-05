import java.util.ArrayList;
import java.util.Objects;

public class Rooms {

    /**
     * Attributi
     * <h2><strong>nome_stanza</strong></h2> contiene il nome della stanza con la seguente formattazione "email1_email2"
     * <h2><strong>games</strong></h2> Arraylist che contiene tutti i game ovvero tutte le partite. L'indice del game corrisponde proprio al numero del topic
     * ES: il game in indice 10 corrisponde alla partita in topic <i>"email1_email2/10/"</i>
     * **/
    String nome_stanza;
    ArrayList<Game> games;

    /**
     * <h3>Costruttore</h3>
     * Il costruttore si occupa di inizializzare i N game e di capire in quali game parto io e quali non parto io,
     * una volta chiuso il costruttore si farà "l'action immediato" dei game dove parto io
     * **/
    public Rooms(String nome_stanza, int partite_per_stanza) {
        this.nome_stanza = nome_stanza;
        games = new ArrayList<>();

        boolean whoStart = whoStart(nome_stanza);

        //Creo i vari game. Se la stanza ha come nome prima il mio nei game pari parto io e nei game dispari parte lui
        // Se invece la stanza ha come nome prima il suo nei game pari parte lui e nei game dispari parto io

        if(whoStart){
            for(int i = 0; i < partite_per_stanza; i++){
                if(i%2==0){ games.add(new Game(new Tavolo(), true)); }
                else{ games.add(new Game(new Tavolo(), false)); }
            }
        }else{
            for(int i = 0; i < partite_per_stanza; i++){
                if(i%2==0){ games.add(new Game(new Tavolo(), false)); }
                else{ games.add(new Game(new Tavolo(), true)); }
            }
        }

    }

    /**
     * <h2>action</h2>
     * Questo metodo è il metodo principale su cui il nostro algoritmo si interfaccia con il server.
     * Questo metodo si occupa di restituire il punto da inviare direttamente al server.
     * Il metodo ha bisogno dell'indice del game [che verrà preso dal numero della topic] e della mossa avversaria, che verrà letta dalla topic avversaria
     * A questo punto, il metodo estrae il game corretto dall'ArrayList che contiene tutti i game di quella stanza.
     * Una volta che ha il game controlla se sul game siamo partiti noi o è partito l'avversario
     * Estrae e converte la mossa dell'avversario e attraverso il doGame elabora la mossa vincente passandogli il game corrente,
     * l'indice che indica a che punto siamo arrivati della partita [l'indice è contenuto nel game stesso e si incrementa di 1 ogni volta che giochiamo],
     * e ovviamente la mossa avversaria convertita.
     * Infine viene returnata la mossa da spedire già convertita
     * @param id_game il game che si vuole interpellare
     * @param mossa_avversaria la mossa avversaria
     * @return la mossa già convertita da spedire indietro
     * @see Game
     * **/
    public int action(int id_game, int mossa_avversaria){

        Game current_game = games.get(id_game+1);

        int[] ma_local = convertNumberToLocalCoordinate(mossa_avversaria);

        int[] mm_local;

        if(current_game.whoStart) {
            mm_local = doGamePN(current_game, current_game.myMosseCounter + 1, ma_local);
        }else{
            mm_local = doGamePL(current_game, current_game.myMosseCounter + 1, ma_local);
        }

        return convertLocalCoordinateToNumber(mm_local);
    }


    /**
     * whoStart
     * metodo che controllando chi viene prima nel nome della stanza mi dice se parto io o no
     * @return ritorna True se parto io nei game pari e False se non parto io [e quindi partirò nei game dispari]
     * **/
    private boolean whoStart(String nome_stanza){
        String first_gamer = nome_stanza.split("_")[0];
        return Objects.equals(first_gamer, "TRISSER.bot3@gmail.com");
    }


    /**
     * doGamePM
     * Funzione che mi svolge un game nella quale partiamo noi:
     * Un game è un processo composto da 3 fasi:
     *      -Setto all'interno del game corrente la mossa avversaria
     *      -Elaboro la prossima mossa da eseguire
     *      -Setto all'interno del game la nuova mossa elaborata
     * @param my_game il game su cui fare la mossa
     * @param idx_game l'indice a cui è il game. Ogni passaggio di indice corrisponde ad un invio di una mossa
     * @param enemy_move la mossa avversaria (Nel caso in cui partiamo noi l'indice è 1 e la mossa avversaria è null)
     * @see Game
     * **/
    public int[] doGamePN(Game my_game, int idx_game, int[] enemy_move){

        if(enemy_move!=null){ my_game.setEnemyPoint(enemy_move); }
        int[] next_my_move = my_game.playGamePN(idx_game, enemy_move);
        my_game.setMyPoint(next_my_move);
        return next_my_move;

    }

    /**
     * doGamePL
     * Funzione che mi svolge un game nella quale partiamo loro:
     * Un game è un processo composto da 3 fasi:
     *      -Setto all'interno del game corrente la mossa avversaria
     *      -Elaboro la prossima mossa da eseguire
     *      -Setto all'interno del game la nuova mossa elaborata
     * @param my_game il game su cui fare la mossa
     * @param idx_game l'indice a cui è il game. Ogni passaggio di indice corrisponde ad un invio di una mossa
     * @param enemy_move la mossa avversaria (Nel caso in cui partiamo noi l'indice è 1 e la mossa avversaria è null)
     * @see Game
     * **/
    public int[] doGamePL(Game my_game, int idx_game, int[] enemy_move){

        my_game.setEnemyPoint(enemy_move);
        int[] next_my_move = my_game.playGamePL(idx_game, enemy_move);
        my_game.setMyPoint(next_my_move);
        return next_my_move;

    }

    /**
     * convertLocalCoordinateToNumber
     * converte le variabile int[] (coordinate locali di un punto) in un numero, da 1 a 9, che identifica la singola cella
     * @param point punto da convertire in numero
     * **/
    public static int convertLocalCoordinateToNumber(int[] point){
        int r = point[0];
        int c = point[1];

        int idx = 1;
        for(int i = 0; i < 3; i++ ){

            for(int j = 0; j < 3; j++){

                if(i == r && j == c ){ return idx; }
                idx++;
            }

        }
        return -1;
    }

    /**
     * convertNumberToLocalCoordinate
     * converte un numero che rappresenta una cella in una coordinata locale
     * @param number punto da convertire in coordinate locali int[]
     * **/
    public static int[] convertNumberToLocalCoordinate(int number){
        switch (number){
            case 1:
                return new int[]{0, 0};
            case 2:
                return new int[]{0, 1};
            case 3:
                return new int[]{0, 2};
            case 4:
                return new int[]{1, 0};
            case 5:
                return new int[]{1, 1};
            case 6:
                return new int[]{1, 2};
            case 7:
                return new int[]{2, 0};
            case 8:
                return new int[]{2, 1};
            case 9:
                return new int[]{2, 2};
            default:
                return null;
        }
    }

    /** Metodi per testare in local il bot, creando delle Game dove il bot intelligente gioca con un bot che gioca con mosse random
     * @deprecated alla fine del progetto verranno rimossi
     * **/
    public void testOfGamePN(){

        int[] ma;
        Tavolo t = new Tavolo();

        Game cg = new Game(t, true);

        for(int i = 1; i < 6; i++){
            ma = cg.generateRandomPoint(cg.getFreePoint());
            System.out.println("--------MOSSA AVVERSARIO: "+ ma[0] +" - " + ma[1] +"-------");
            doGamePN(cg, i, ma);

            cg.table.showRealTable();
        }
    }
    public void testOfGamePL(){
        int[] ma;
        Tavolo t = new Tavolo();

        Game cg = new Game(t, false);

        for(int i = 1; i < 5; i++){
            ma = cg.generateRandomPoint(cg.getFreePoint());
            System.out.println("--------MOSSA AVVERSARIO: "+ ma[0] +" - " + ma[1] +"-------");
            doGamePL(cg, i, ma);

            cg.table.showRealTable();
        }
    }
}
