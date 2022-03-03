import java.util.ArrayList;

public class Main {

    public static void main(String[] args) {

        Tavolo t = new Tavolo();
        Game current_game = new Game(t);

        current_game.table.setPoint(1, 1, 5);

        current_game.table.showTable();

//        int[][] next_mossa =current_game.mossa3_PN(new int[]{0, 0});
//
//        int[] mossa = next_mossa[1];
//
//        for (int i : mossa) {
//            System.out.print(i);
//        }
        int[][] next_mossa = current_game.mossa1_PL(new int[]{1, 1});

        int[] mossa = next_mossa[1];

        for (int i : mossa) {
            System.out.print(i);
        }
    }
}
