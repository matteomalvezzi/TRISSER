import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.Objects;

public class Main {

    ArrayList<Rooms> rooms;

    public static void main(String[] args) {

        OnlineGame onlineGame = new OnlineGame();

        int n_partite = onlineGame.n_room;

        int n_partite_per_stanza = onlineGame.n_match_for_room;

        MqttCallback game_callback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                switch (topic){
                    case "broadcast":
                        JSONParser jsonParser = new JSONParser();
                        JSONObject jsonObject = (JSONObject) jsonParser.parse(mqttMessage.toString());
                        if(Objects.equals(jsonObject.get("game").toString(), "Start")){

                        }
                    break;

                    default:

                    break;


                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        };
    }

}
