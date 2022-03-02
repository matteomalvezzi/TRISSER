import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        Tavolo t = new Tavolo();
        Game current_game = new Game(t);

        current_game.table.setPoint(1, 1, 3);
        current_game.table.setPoint(2, 1, 5);

        ArrayList<ArrayList<Integer>> liberi= current_game.getFreePoint(current_game.table);

        System.out.println(liberi);

        ArrayList<Integer> randomici = current_game.generateRandomPoint(liberi);

        System.out.println(randomici);
    }
}
