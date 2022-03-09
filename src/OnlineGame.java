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
    public MqttCallback current_callback;

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

        rooms = new ArrayList<>();

        try{
            //Get email
            System.out.println("PRENDO LA MAIL");
            String email = connectToMailServer();
            //Extract data from email
            System.out.println("LEGGO LA MAIL");
            readMail(email);
            //Connect to Server
            System.out.println("MI CONNETTO AL SERVER");
            this.current_client = connectToServer(username_client, password_client);
            //Subscribe topics
            System.out.println("MI ISCRIVO ALLE TOPIC PER RICEVERE I MESSAGGI DELL'AVVERSARIO");
            subscribeTopic();
            //Go online
            System.out.println("VADO ONLINE");
            if(goOnlineOnServer(this.current_client)){
                //Show rooms
                System.out.println("SONO ONLINE ECCO LE ROOM A CUI SONO ISCRITTO");
                for (String s : topic_list) {
                    System.out.println(s);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("CREO LE ROOM ATTRAVERSO LA LISTA DI ROOM NAME PRESE DALLA MAIL E CREO I GAME");
        //Create rooms and games
        for (String room_name : this.topic_list) {
            rooms.add(new Room(room_name, this.n_match_for_room));
        }
        System.out.println("SETTO LA CALLBACK");
        //Create callback
        this.current_callback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

                System.out.println("MESSAGGIO ARRIVATO DA TOPIC : " + topic);
                String[] topic_split = topic.split("/");   //Stringa topic splittata in sub-topic
                String room_name = topic_split[0];            //    Stanza/TOPIC

                if(Objects.equals(room_name, "broadcast")){
                    System.out.println("INIZIALIZZO LE PARTITE DOVE PARTO IO");

                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(mqttMessage.toString());
                    if(Objects.equals(jsonObject.get("game").toString(), "start")) {
                        //Sono iscritto gioco per primo
                        for (Room room : rooms) {

                            for (int i = 0; i < room.games.size(); i++) {
                                System.out.println("MY ROOM INDICE ROOM : " + i);
                                Game on_game = room.games.get(i);
                                System.out.println("ECCOMI : " + on_game.whoStart);

                                if(on_game.whoStart){

                                    System.out.println("PARTITA DOVE INIZIO IO, PARTO!");
                                    int my_move = room.action(i, 0);
                                    System.out.println("MOSSA ELABORATA: " + my_move);


//                                    String mossa_mia = String.valueOf(my_move);
//                                    String payload = "{\"move\":\""+mossa_mia+"\"}";
//                                    String ciao= "trisser.bot2@gmail.com_TRISSER.bot3@gmail.com/"+i+"/TRISSER.bot3@gmail.com";
//                                    System.out.println(ciao);
//                                    current_client.publish(ciao, new MqttMessage(payload.getBytes(StandardCharsets.UTF_8)));

                                    sendMove(room.nome_stanza, i, "TRISSER.bot3@gmail.com", my_move, current_client);
                                }
                                System.out.println("E QUA?");
                            }
                        }
                    }
                }else if(topic_list.contains(room_name)){

                    System.out.println("ARRIVATA RISPOSTA");

//                    int game_index = Integer.parseInt(topic_split[1]);             // Game/SUB-TOPIC
//                    String my_topic = "TRISSER.bot3@gmail.com";
//                    String enemy_topic = topic_split[2];                           // Topic nemica/SUB-SUB-TOPIC
//
//                    //Get Room
//                    Room current_room = getRoom(room_name);
//
//                    if(current_room!=null){
//                        //Get Game
//                        Game current_game = getGame(current_room, game_index);
//                        //Verify that topic is from enemy
//                        if(Objects.equals(current_room.enemy, enemy_topic)){
//                            System.out.println("RISPONDO");
//                            //Get enemy move
//                            JSONParser jP = new JSONParser();
//                            JSONObject jO = (JSONObject) jP.parse(mqttMessage.toString());
//
//                            String enemy_move = (String) jO.get("move");
//                            int enemy_mv = Integer.parseInt(enemy_move);
//                            System.out.println("MOSSA NEMICA: " + enemy_move);
//
//                            //Calculate my move
//                            int my_move = current_room.action(game_index, enemy_mv);
//
//                            //Send my move
//                            sendMove(room_name, game_index, "TRISSER.bot3@gmail.com", my_move);
//
//                        }
//                    }


                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        };

    }

    /**
     * getEnemyName
     * Dato un nome stanza restituisce l'avversario
     * @param room_name nome stanza
     * @return nome avversario
     * **/
    public String getEnemyName(String room_name){
        String[] gamer = room_name.split("_");
        String first_gamer = gamer[0];
        String second_gamer = gamer[1];
        if(!Objects.equals(first_gamer, EMAIL)){ return first_gamer; }
        else{ return second_gamer; }
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
        return room.games.get(game_index);
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
        Message[] messages = inbox.search(new FlagTerm(new Flags(Flags.Flag.SEEN), true));

        Message message = messages[0];
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
        Long n_game_for_room = (Long) jsonObject.get("room_instance");
        n_match_for_room = n_game_for_room.intValue();
    }

    /**
     * connectToServer
     * metodo che mi connette con il broker Mqtt dato username e password
     * @param username username per connettersi al broker
     * @param passwd password per connettersi al broker
     * **/
    public MqttClient connectToServer(String username, String passwd) {
        try {
            String broker = "tcp://192.168.67.64:1883";
            String PubId = "127.0.128.1";

            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient my_client = new MqttClient(broker, PubId, persistence);
            MqttConnectOptions connOpts = new MqttConnectOptions();
            connOpts.setCleanSession(true);
            connOpts.setConnectionTimeout(60);
            connOpts.setKeepAliveInterval(60);
            connOpts.setMaxInflight(5000);

            connOpts.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);
            //connOpts.setUserName(username);
            //connOpts.setPassword(passwd.toCharArray());
            my_client.connect(connOpts);
            my_client.setTimeToWait(-1);
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
        MqttMessage message = new MqttMessage("{\"online\":true}".getBytes(StandardCharsets.UTF_8));

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


                System.out.println("NUOVA ROOM: " + room + " , mi iscrivo a tutti i game");

                for (int i = 0; i < this.n_match_for_room; i++) {

                    String new_topic = room+"/"+i+"/"+getEnemyName(room);
                    this.current_client.subscribe(new_topic);
                    System.out.println("ISCRITTO ALLA TOPIC = " + new_topic);
                }
            }
        } catch (MqttException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * sendMove
     * metodo per inviare la mossa
     * **/
    public void
    sendMove(String room_name, int game_idx, String my_topic, int my_move, MqttClient current_client){
        String return_topic = room_name+"/"+game_idx+"/"+my_topic;
        String return_msg = "{\"move\":\"" + my_move + "\"}";

        MqttMessage returnMqtt_msg = new MqttMessage(return_msg.getBytes(StandardCharsets.UTF_8));
        returnMqtt_msg.setQos(0);
        System.out.println("RETURN TOPIC: " + return_topic);
        System.out.println("RETURN MSG: " + return_msg);
        System.out.println("RIMANDO INDIETRO QUESTA MOSSA: " + my_move);
        try {
            current_client.publish(return_topic, returnMqtt_msg);
        } catch (MqttException e) {
            e.printStackTrace();
        }

        System.out.println("OI");
    }

}
