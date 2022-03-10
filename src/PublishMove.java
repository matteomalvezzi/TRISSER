import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * PublishMove extends Thread
 * classe che si occupa di creare un thread per pubblicare sulle topic
 * il metodo publish necessita di un Thread separato per problemi alla libreria PAHO
 * che nel caso in cui vengano fatte troppe pubblicazioni contemporaneamente egli si blocca
 * @author Matteo Malvezzi
 * @see Thread
 * @see OnlineGame
 * @see MqttClient
 * **/
public class PublishMove extends Thread{

    /**
     * Attributi
     * <h2><strong>rt</strong></h2> return topic. topic su cui pubblicare
     * <h2><strong>rm</strong></h2> return message. topic su cui returnare il messaggio
     *
     * **/
    public String rt;
    public MqttMessage rm;

    public MqttClient mc;

    public PublishMove(String return_topic, MqttMessage return_mqtt_message, MqttClient return_mqtt_client) {
        this.mc = return_mqtt_client;
        this.rt = return_topic;
        this.rm = return_mqtt_message;
    }

    @Override
    public void run() {
        super.run();
        try{
            this.mc.publish(this.rt, this.rm);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
