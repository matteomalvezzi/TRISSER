import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Properties;

import javax.mail.*;
import javax.mail.internet.MimeMultipart;
import javax.mail.search.FlagTerm;


/**
 * LE FUNZIONALITA DI QUESTA CLASSE
 * Mi connetto al mail server per estrarre la mail
 * Leggo la MAIL, Da qui capisco, username, passw, topic in cui giocherò {riempio la topic list} ecc...
 * Apro il client mqtt e mi connetto
 * Vado online
 * Controllo il topic broadcast per capire quando starta la partita e se c'è gente offline {da togliere dalla topic list}
 * Mi iscrivo alle topic della topic list
 *
 * @author Matteo Malvezzi, Alessandro Verlanti
 * @see Main
 * @see Game
 * @see Tavolo
 * **/
public class OnlineGame {

    public MqttClient current_client;

    /** ---- Connect to IMAP Server to read email and spec attributes  ---- **/

    public static final String EMAIL = "TRISSER.bot3@gmail.com";
    public static final String PASSWD_EMAIL = "Einaudi123";
    public static final String MAIL_SERVER = "imap.gmail.com";
    public static final String MAIL_PROTOCOL = "imaps";

    /** ---- Connect to Mqtt Server Broker attributes  ---- **/

    public String username_client;
    public String password_client;
    public ArrayList<String> topic_list;
    public String game_date;

    /** ---- data about rooms of this client  ---- **/

    public int n_match_for_room;
    public int n_room;
    public ArrayList<Room> rooms;

    public OnlineGame() {

        topic_list = new ArrayList<>();

        try{
            //Get email
            String email = connectToMailServer();
            //Extract data from email
            readMail(email);
            //Connect to Server
            this.current_client = connectToServer(username_client, password_client);
            //Go online
            if(goOnlineOnServer(this.current_client)){
                //Show rooms
                for (String s : topic_list) {
                    System.out.println(s);
                }
                //Subscribe topics
                subscribeTopic();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //Create rooms
        for (String room_name : this.topic_list) {
            rooms.add(new Room(room_name, this.n_match_for_room));
        }

        //Create callback
        MqttCallback game_callback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                String[] topic_split = topic.split("/");   //Stringa topic splittata in sub-topic
                String room_name = topic_split[0];            //    Stanza/TOPIC

                if(Objects.equals(room_name, "broadcast")){
                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(mqttMessage.toString());
                    if(Objects.equals(jsonObject.get("game").toString(), "Start")){
                        //MI DEVO ISCRIVERE ALLE TOPIC
                    }


                }else if(topic_list.contains(room_name)){
                    int game_index = Integer.parseInt(topic_split[1]);             // Game/SUB-TOPIC
                    String my_topic = "TRISSER.bot3@gmail.com";
                    String enemy_topic = topic_split[2];                           // Topic nemica/SUB-SUB-TOPIC

                    //Get Room
                    Room current_room = getRoom(room_name);
                    if(current_room!=null){
                        //Get Game
                        Game current_game = getGame(current_room, game_index);
                        //Verify that topic is from enemy
                        if(Objects.equals(current_room.enemy, enemy_topic)){
                            //Get enemy move
                            JSONParser jP = new JSONParser();
                            JSONObject jO = (JSONObject) jP.parse(mqttMessage.toString());

                            int enemy_move = (int) jO.get("move");

                            //Calculate my move
                            int my_move = current_room.action(game_index, enemy_move);

                            //Send my move
                            String return_topic = room_name+"/"+game_index+"/"+my_topic;

                            String return_msg = "{\"move\":\"" + my_move + "\"}";
                            MqttMessage returnMqtt_msg = new MqttMessage(return_msg.getBytes(StandardCharsets.UTF_8));
                            current_client.publish(return_topic, returnMqtt_msg);

                        }
                    }


                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        };

        //Set Callback
        this.current_client.setCallback(game_callback);
    }

    /**
     * getRoom
     * Dato un nome stanza restituisce la stanza associata
     * @param room_name nome stanza
     * @return Stanza
     * **/
    public Room getRoom(String room_name){
        for (Room room : rooms) {
            if(Objects.equals(room_name, room.nome_stanza)){
                return room;
            }
        }return null;
    }
    /**
     * getGame
     * Dato una stanza e un indice di game restituisce un game
     * @param room Stanza
     * @param game_index indice del game
     * @return Game
     * **/
    public Game getGame(Room room, int game_index){
        return room.games.get(game_index+1);
    }


    /**
     * setCallback
     * setto la funzione di callback, ovvero l'oggetto che contiene il metodo che verrà richiamato ogni volta che arriverà un messaggio
     * @param mqttCallback oggetto di callback
     * **/
    public void setCallback(MqttCallback mqttCallback){
        //Set callback
        this.current_client.setCallback(mqttCallback);
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
            my_client.connect(connOpts);
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
     * subscribeTopic
     * metodo per iscriversi alle topic affinché i messaggi arrivino nel metodo di callback massageArrived
     * **/
    public boolean subscribeTopic(){
        try {
            this.current_client.subscribe("broadcast"); //Mi iscrivo alla topic di broadcast

            for (String room : this.topic_list) {   //Mi iscrivo a tutte le subtopic[Game] di tutte le topic[Rooms]

//                String[] gamer = room.split("_");
//                String first_gamer = gamer[0];
//                String second_gamer = gamer[1];

                for (int i = 0; i < this.n_match_for_room; i++) {

//                    this.current_client.subscribe(room+"/"+i+"/"+first_gamer);
//                    this.current_client.subscribe(room+"/"+i+"/"+second_gamer);
                    this.current_client.subscribe(room+"/"+i);

                }
            }
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

}
