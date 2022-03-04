import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {
        int[] ma;
        Tavolo t = new Tavolo();

        Game cg = new Game(t, false);

        ma = cg.generateRandomPoint(cg.getFreePoint());
        System.out.println("--------MOSSA AVVERSARIO: "+ ma[0] +" - " + ma[1] +"-------");
        doGamePL(cg, 1, ma);

        cg.table.showRealTable();

        ma = cg.generateRandomPoint(cg.getFreePoint());
        System.out.println("--------MOSSA AVVERSARIO: "+ ma[0] +" - " + ma[1] +"-------");
        doGamePL(cg, 2, ma);

        cg.table.showRealTable();

        ma = cg.generateRandomPoint(cg.getFreePoint());
        System.out.println("--------MOSSA AVVERSARIO: "+ ma[0] +" - " + ma[1] +"-------");
        doGamePL(cg, 3, ma);

        cg.table.showRealTable();

        ma = cg.generateRandomPoint(cg.getFreePoint());
        System.out.println("--------MOSSA AVVERSARIO: "+ ma[0] +" - " + ma[1] +"-------");
        doGamePL(cg, 4, ma);

        cg.table.showRealTable();
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
    public static void doGamePN(Game my_game, int idx_game, int[] enemy_move){

        if(enemy_move!=null){ my_game.setEnemyPoint(enemy_move); }
        int[] next_my_move = my_game.playGamePN(idx_game, enemy_move);
        my_game.setMyPoint(next_my_move);
        System.out.println("Gioco in : " + next_my_move[0] + "-" + next_my_move[1] );

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
    public static void doGamePL(Game my_game, int idx_game, int[] enemy_move){

        my_game.setEnemyPoint(enemy_move);
        int[] next_my_move = my_game.playGamePL(idx_game, enemy_move);
        my_game.setMyPoint(next_my_move);

    }

    public static void testOfGame(){
        int[] ma;
        Tavolo t = new Tavolo();

        Game current_game = new Game(t, true);

        doGamePN(current_game, 1, null);

        current_game.table.showRealTable();

        ma = current_game.generateRandomPoint(current_game.getFreePoint());
        System.out.println("--------- L'AVVERSARIO RISPONDE IN :" + ma[0] + "-" + ma[1] + "------");

        doGamePN(current_game, 2, ma);

        current_game.table.showRealTable();

        ma = current_game.generateRandomPoint(current_game.getFreePoint());
        System.out.println("--------- L'AVVERSARIO RISPONDE IN :" + ma[0] + "-" + ma[1] + "------");

        doGamePN(current_game, 3, ma);

        current_game.table.showRealTable();

        ma = current_game.generateRandomPoint(current_game.getFreePoint());
        System.out.println("--------- L'AVVERSARIO RISPONDE IN :" + ma[0] + "-" + ma[1] + "------");

        doGamePN(current_game, 4, ma);

        current_game.table.showRealTable();

        ma = current_game.generateRandomPoint(current_game.getFreePoint());
        System.out.println("--------- L'AVVERSARIO RISPONDE IN :" + ma[0] + "-" + ma[1] + "------");

        doGamePN(current_game, 5, null);

        current_game.table.showRealTable();
        System.out.println("--------- PROSSIMA MOSSA ------");
    }
}
