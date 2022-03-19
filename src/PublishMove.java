import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

/**
 * PublishMove
 * PublishMove extends Thread
 * Questa classe si occupa di creare un thread per pubblicare sulle topic
 * il metodo publish necessita di un Thread separato per problemi alla libreria PAHO
 * che nel caso in cui vengano fatte troppe pubblicazioni contemporaneamente egli si blocca
 * @author Matteo Malvezzi
 * @see Thread
 * @see OnlineGame
 * @see MqttClient
 * **/
public class PublishMove extends Thread{

    /** -------------------- Attributes -------------------- */


    /** <strong>return topic</strong> topic su cui pubblicare **/
    public String rt;
    /** <strong>return message</strong> topic su cui returnare il messaggio **/
    public MqttMessage rm;

    /** <strong>mqtt client</strong> client su cui pubblicare il messsaggio **/
    public MqttClient mc;

    /** -------------------- Constructor -------------------- **/

    /**
     * Costruttore
     * Si occupa di inizializzare i 3 parametri
     * @param return_topic la topic su cui inviare il messaggio
     * @param return_mqtt_message messaggio sottoforma di oggetto Mqtt da inviare
     * @param return_mqtt_client client su cui returnare il messaggio
     * **/
    public PublishMove(String return_topic, MqttMessage return_mqtt_message, MqttClient return_mqtt_client) {
        this.mc = return_mqtt_client;
        this.rt = return_topic;
        this.rm = return_mqtt_message;
    }

    /** Run method
     * questo metodo avviato da start() si occupa di avviare il publish gestendone anche l'eventuale eccezione
     * Usa i due attributi precedentemente definiti nel costruttore
     * @see Thread
     * */
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
