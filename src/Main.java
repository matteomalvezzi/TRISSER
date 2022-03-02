import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        Tavolo t = new Tavolo();
        Game current_game = new Game(t);

        current_game.table.setPoint(0, 0, 3);
        current_game.table.setPoint(1, 0, 5);
        current_game.table.setPoint(0, 2, 3);
        current_game.table.setPoint(0, 1, 5);

        ArrayList<int[]> liberi= current_game.getFreePoint();

        for (int[] ints : liberi) {
            for (int anInt : ints) {
                System.out.print(anInt);
            }
            System.out.println("");
        }

        current_game.table.showTable();

        System.out.println(current_game.table.rowProduct(0));
        System.out.println(current_game.table.columnProduct(0));

    }
}
