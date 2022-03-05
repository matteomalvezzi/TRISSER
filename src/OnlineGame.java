import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;

import javax.activation.*;


/**
 * LE FUNZIONALITA DI QUESTA CLASSE
 * Mi connetto al mail server per estrarre la mail
 * Leggo la MAIL, Da qui capisco, username, passw, topic in cui giocherò {riempio la topic list} ecc...
 * Apro il client mqtt e mi connetto
 * Vado online
 * Controllo il topic broadcast per capire quando starta la partita e se c'è gente offline {da togliere dalla topic list}
 * Mi iscrivo alle topic della topic list
 *
 * @author Matteo Malvezzi
 * @see Main
 * @see Game
 * @see Tavolo
 * **/
public class OnlineGame {

    public MqttClient current_client;
    //public MqttCallback current_client_callback;

    public static final String EMAIL = "TRISSER.bot3@gmail.com";
    public static final String PASSWD_EMAIL = "Einaudi123";
    public static final String MAIL_SERVER = "imap.gmail.com";
    public static final String MAIL_PROTOCOL = "imaps";

    public String username_client;
    public String password_client;
    public ArrayList<String> topic_list;
    public String game_date;

    public int n_match_for_room;
    public int n_room;

    public OnlineGame(MqttCallback clb) {

        topic_list = new ArrayList<>();

        try{
            String email = connectToMailServer();

            readMail(email);

            System.out.println(username_client + password_client);

//            this.current_client = connectToServer(username_client, password_client);
//
//            if(goOnlineOnServer(this.current_client)){
//                System.out.println("SONO ANDATO ONLINE");
//                //this.current_client.setCallback(clb);
//                for (String s : topic_list) {
//                    System.out.println(s);
//                }
//            }



        } catch (Exception e) {
            e.printStackTrace();
        }


        //String example_mail = "{\"rooms\":[\"TRISSER.server@gmail.com_giaco.paltri@gmail.com\",\"giaco.paltri@gmail.com_abdullah.ali@einaudicorreggio.it\"],\"rules\" : {\"date\":\"22-08-2002 15:30:20\",\"connection_time\":20,\"time\":20,\"bot_number\":100},\"pwd\":\"FnOnaPe3\",\"user\":\"giaco.paltri@gmail.com\",\"room_instance\" : 50}";
    }

    /**
     * getMailContent
     * getTextFromMimeMultipart
     * Metodi per la lettura del contenuto html di una mail
     * @param msg Messaggio contenuto nella mail [body della mail]
     * @return Returna il contenuto del messaggio in una stringa
     * **/
    public String getMailContent(Message msg) throws Exception {
        String result = "";
        if (msg.isMimeType("text/plain")) {
            result = msg.getContent().toString();
        } else if (msg.isMimeType("multipart/*")) {
            MimeMultipart mimeMultipart = (MimeMultipart) msg.getContent();
            result = getTextFromMimeMultipart(mimeMultipart);
        }
        return result;
    }
    private String getTextFromMimeMultipart( MimeMultipart mimeMultipart)  throws Exception{
        String result = "";
        int count = mimeMultipart.getCount();
        for (int i = 0; i < count; i++) {
            BodyPart bodyPart = mimeMultipart.getBodyPart(i);
            if (bodyPart.isMimeType("text/plain")) {
                result = result + "\n" + bodyPart.getContent();
                break; // without break same text appears twice in my tests
            } else if (bodyPart.isMimeType("text/html")) {
                String html = (String) bodyPart.getContent();
                result = result + "\n" + org.jsoup.Jsoup.parse(html).text();
            } else if (bodyPart.getContent() instanceof MimeMultipart){
                result = result + getTextFromMimeMultipart((MimeMultipart)bodyPart.getContent());
            }
        }
        return result;
    }

    /**
     * connectToMailServer
     * legge la mail e ne estrapola i dati
     * @return il messaggio in chiaro del contenuto della mail
     * **/
    public String connectToMailServer() throws Exception {
        // create properties
        Properties properties = new Properties();

        properties.put("mail.imap.host", MAIL_SERVER);
        properties.put("mail.imap.port", "993");
        properties.put("mail.imap.starttls.enable", "true");
        properties.put("mail.imap.ssl.trust", MAIL_SERVER);

        Session emailSession = Session.getDefaultInstance(properties);

        // create the imap store object and connect to the imap server
        Store store = emailSession.getStore(MAIL_PROTOCOL);

        store.connect(MAIL_SERVER, EMAIL, PASSWD_EMAIL);

        // create the inbox object and open it
        Folder inbox = store.getFolder("Inbox");
        inbox.open(Folder.READ_WRITE);

        // retrieve the messages from the folder in an array and print it
        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), false));

        Message message = messages[0];
        //message.setFlag(Flags.Flag.SEEN, true);
        String result = getMailContent(message);

        return result;
    }

    /**
     * readMail
     * legge la mail e ne estrapola i dati
     * @param mail_content il contenuto della mail
     * **/
    public void readMail(String mail_content){
        //Root of object
        JSONObject jsonObject = (JSONObject) JSONValue.parse(mail_content);

        //Add rooms
        JSONArray rooms = (JSONArray) jsonObject.get("rooms");
        if(rooms!= null){
            for (Object room : rooms) {
                topic_list.add(room.toString());
            }
        }else{
            System.out.println("NON CI SONO STANZE ASSEGNATE");
        }

        //Get date
        JSONObject rules = (JSONObject) jsonObject.get("rules");
        game_date = (String) rules.get("date");

        //Get username
        username_client = EMAIL;
        password_client = (String) jsonObject.get("pwd");

        //Get number of rooms and match for room
        n_room = topic_list.size();
        //n_match_for_room = (int) jsonObject.get("room_instance");
    }

    /**
     * connectToServer
     * metodo che mi connette con il broker Mqtt dato username e password
     * @param username username per connettersi al broker
     * @param passwd password per connettersi al broker
     * **/
    public MqttClient connectToServer(String username, String passwd) {
        try {
            String broker = "tcp://localhost:1883";
            String PubId = "127.0.0.1";

            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient my_client = new MqttClient(broker, PubId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setConnectionTimeout(60);
            connOpts.setKeepAliveInterval(60);
            connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            connOpts.setUserName(username);
            connOpts.setPassword(passwd.toCharArray());
            System.out.println("Connecting to broker: " + broker);
            return my_client;
        } catch (MqttException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * goOnlineOnServer
     * metodo per "Andare OnLine": tale processo invia in una topic di broadcast un messaggio per segnalare al server la nostra attività
     * @param current_client client su cui andare online
     * **/
    public boolean goOnlineOnServer(MqttClient current_client){
        //go online
        String topic = "online/"+username_client;
        MqttMessage message = new MqttMessage("\"online\":true".getBytes(StandardCharsets.UTF_8));

        try{
            current_client.subscribe(topic);
        }catch (MqttException e) {
            e.printStackTrace();
            System.out.println("TOPIC INESISTENTE");
            return false;
        }
        try{
            current_client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("ERRORE INVIO MESSAGGIO PER ANDARE ONLINE");
            return false;
        }
        return true;
    }

    /**
     * convertLocalCoordinateToNumber
     * converte le variabile int[] (coordinate locali di un punto) in un numero, da 1 a 9, che identifica la singola cella
     * @param point punto da convertire in numero
     * **/
    public static int convertLocalCoordinateToNumber(int[] point){
        int r = point[0];
        int c = point[1];

        int idx = 1;
        for(int i = 0; i < 3; i++ ){

            for(int j = 0; j < 3; j++){

                if(i == r && j == c ){ return idx; }
                idx++;
            }

        }
        return -1;
    }

    /**
     * convertNumberToLocalCoordinate
     * converte un numero che rappresenta una cella in una coordinata locale
     * @param number punto da convertire in coordinate locali int[]
     * **/
    public static int[] convertNumberToLocalCoordinate(int number){
        switch (number){
            case 1:
                return new int[]{0, 0};
            case 2:
                return new int[]{0, 1};
            case 3:
                return new int[]{0, 2};
            case 4:
                return new int[]{1, 0};
            case 5:
                return new int[]{1, 1};
            case 6:
                return new int[]{1, 2};
            case 7:
                return new int[]{2, 0};
            case 8:
                return new int[]{2, 1};
            case 9:
                return new int[]{2, 2};
            default:
                return null;
        }
    }
}
