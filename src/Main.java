public class Main {

    public static void main(String[] args) {

        //Create Online Game
        OnlineGame onlineGame;

        //Initialize online game
        onlineGame = new OnlineGame();

        //Set inner callback
        onlineGame.current_client.setCallback(onlineGame.current_callback);

    }

}
