public class Main {

    public static void main(String[] args) {

        OnlineGame onlineGame;
        onlineGame = new OnlineGame();
        //SET CALLBACK
        onlineGame.current_client.setCallback(onlineGame.current_callback);

    }

}
