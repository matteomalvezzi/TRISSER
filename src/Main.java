import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        //Get param about mqtt Broker
        Scanner in = new Scanner(System.in);

        Log.w("IP DEL BROKER", "Inserisci l'indirizzo ip del broker: ");
        String ip_broker = in.nextLine();

        Log.w("PUBLISHER_ID", "Inserisci il publisher id con cui giocherà il client: ");
        String publisher_id = in.nextLine();

        Log.d(ip_broker, publisher_id);

        //Create Online Game
        OnlineGame onlineGame;

        //Initialize online game
        onlineGame = new OnlineGame(ip_broker, publisher_id);

        //Set inner callback
        onlineGame.current_client.setCallback(onlineGame.current_callback);

    }
}
