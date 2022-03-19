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
 * <strong>LE FUNZIONALITA DI QUESTA CLASSE</strong><hr><br>
 * <i>Gestisco il Game</i>
 * <br>
 * <ul>
 * <li>Mi connetto al mail server per estrarre la mail</li>
 * <li>Leggo la MAIL, Da qui capisco, username, passw, topic in cui giocherò {riempio la topic list}</li>
 * <li>Apro il client mqtt e mi connetto</li>
 * <li>Vado online</li>
 * <li>Controllo il topic broadcast per capire quando starta la partita e se c'è gente offline {da togliere dalla topic list}</li>
 * <li>Mi iscrivo alle topic della topic list</li>
 * </ul>
 *
 * @author Matteo Malvezzi
 * @see Main
 * @see Game
 * @see Tavolo
 * **/
public class OnlineGame {

    /** ---- Mqtt client object  ---- **/

    /** <strong>current_client</strong> contiene l'MqttClient corrente **/
    public MqttClient current_client;
    /** <strong>current_callback</strong> contiene l'MqttCallback corrente **/
    public MqttCallback current_callback;

    /** ---- Connect to IMAP Server to read email and spec attributes  ---- **/

    /** <strong>EMAIL</strong> contiene la mail del nostro bot **/
    public static final String EMAIL = "TRISSER.bot3@gmail.com";
    /** <strong>PASSWD_EMAIL</strong> contiene la password della mail del nostro bot **/
    public static final String PASSWD_EMAIL = "Einaudi123";
    /** <strong>MAIL_SERVER</strong> contiene l'indirizzo del mail server **/
    public static final String MAIL_SERVER = "imap.gmail.com";
    /** <strong>MAIL_PROTOCOL</strong> contiene il protocollo di comunicazione utilizzato dal mail server **/
    public static final String MAIL_PROTOCOL = "imaps";

    /** ---- Connect to Mqtt Server Broker attributes  ---- **/

    /** <strong>BROKER_HOST</strong> contiene l'indirizzo ip del broker **/
    public String BROKER_HOST;
    /** <strong>PUBLISHER_ID</strong> contiene l'indirizzo ip del publisher **/
    public String PUBLISHER_ID;

    /** <strong>username_client</strong> contiene l'username del nostro client **/
    public String username_client;
    /** <strong>password_client</strong> contiene la password del nostro client **/
    public String password_client;
    /** <strong>topic_list</strong> contiene la lista di topic a cui sarà necessario iscriversi **/
    public ArrayList<String> topic_list;
    /** <strong>game_date</strong> contiene la data di gioco della partita, che verrà estrapolata dalla mail ricevuta dal server **/
    public String game_date;

    /** ---- data about rooms of this client  ---- **/

    /** <strong>n_match_for_room</strong> contiene il numero di partite per stanza **/
    public int n_match_for_room;
    /** <strong>n_room</strong> contiene il numero di stanze **/
    public int n_room;
    /** <strong>rooms</strong> contiene l'insieme delle stanze **/
    public ArrayList<Room> rooms;

    /**
     * Costruttore
     * Si occupa di:
     * connettersi al mail-server ed estrarre il contenuto della mail ricevuta dal server che contiene le relative regole di gioco
     * leggere la mail
     * creare il l'MqttClient e connettersi
     * andare online
     * iscriversi alle topic
     * @param broker_ip ip del broker mqtt
     * @param publisher_id id del client mqtt (id del pubblicatore)
     * **/
    public OnlineGame(String broker_ip, String publisher_id) {

        //Set information about broker
        this.BROKER_HOST = broker_ip;
        this.PUBLISHER_ID = publisher_id;

        //Initialize topic_name_list and list of room
        topic_list = new ArrayList<>();
        rooms = new ArrayList<>();

        try{
            //Get email
            Log.i("connectToMailServer", "Connessione al mail server in corso");
            String email = connectToMailServer();
            Log.i("connectToMailServer", "Mail estratta con successo");

            //Extract data from email
            readMail(email);
            Log.i("readMail", "Regole del gioco estratte dalla mail con successo");

            //Connect to Broker
            Log.i("connectToServer", "Connessione al broker in corso [username = "+username_client+", password = "+password_client+"]");
            this.current_client = connectToServer(username_client, password_client);
            Log.i("connectToServer", "Connessione al broker avvenuta con successo");

            //Subscribe topics
            subscribeTopic();
            Log.i("subscribeTopic", "Iscrizione alle topic avvenuta con successo");

            //Go online
            if(goOnlineOnServer(this.current_client)){
                Log.i("goOnlineOnServer", "Sono online, comunicazione con il server RIUSCITA!");

                //Show rooms
                for (int i = 0; i < topic_list.size(); i++) {
                    Log.i(String.valueOf(i), topic_list.get(i));
                }

            }
        } catch (Exception e) {
            Log.e("Errore", "Connessione al broker non riuscita");
            e.printStackTrace();
        }

        Log.i("new Room", "Creazione delle room e dei game in corso... ");
        //Create rooms and games
        for (String room_name : this.topic_list) {
            rooms.add(new Room(room_name, this.n_match_for_room));
        }

        Log.i("new MqttCallback()", "Setto la callback");
        //Create callback
        this.current_callback = new MqttCallback() {
            @Override
            public void connectionLost(Throwable throwable) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {

                //Get topic
                String[] topic_split = topic.split("/");   //Stringa topic splittata in sub-topic
                String room_name = topic_split[0];            //    Stanza/TOPIC

                //Check if is room or broadcast
                if(Objects.equals(room_name, "broadcast")){

                    Log.i("messageArrived", "NUOVO MESSAGGIO DA BROADCAST --> "+mqttMessage.toString());

                    JSONParser jsonParser = new JSONParser();
                    JSONObject jsonObject = (JSONObject) jsonParser.parse(mqttMessage.toString());

                    //Check if is "start" message or "not_connected" message
                    if(!jsonObject.containsKey("not_connected")){

                        //Check if game contain "start" message
                        if(Objects.equals(jsonObject.get("game").toString(), "start")) {

                            Log.i("messageArrived", "Inizio del gioco, inizializzo le topic e gioco per primo quando inizio io");

                            //Do first move for each game in each room if I start
                            for (Room room : rooms) {

                                for (int i = 0; i < room.games.size(); i++) {
                                    //Get game
                                    Game on_game = room.games.get(i);
                                    //Check if I start
                                    if(on_game.whoStart){
                                        //Do action
                                        int my_move = room.action(i, 0);
                                        //Send move
                                        sendMove(room.nome_stanza, i, "TRISSER.bot3@gmail.com", my_move, current_client);
                                    }
                                }
                            }
                        }
                        Log.i("messageArrived", "Inizializzazione delle partite dove partiamo noi completata");
                    }

                }else if(topic_list.contains(room_name)){

                    //I've received a message from subscribed topic, I'll send answer
                    int game_index = Integer.parseInt(topic_split[1]);             // Game/SUB-TOPIC
                    String enemy_topic = topic_split[2];                           // Topic nemica/SUB-SUB-TOPIC

                    //Get Room
                    Room current_room = getRoom(room_name);

                    if(current_room!=null){ //Check if room isn't null
                        //Get Game
                        Game current_game = getGame(current_room, game_index);

                        //Verify that topic is from enemy
                        if(Objects.equals(current_room.enemy, enemy_topic)){

                            //Get enemy move
                            JSONParser jP = new JSONParser();
                            JSONObject jO = (JSONObject) jP.parse(mqttMessage.toString());

                            String enemy_move = (String) jO.get("move");
                            int enemy_mv = Integer.parseInt(enemy_move);

                            //Calculate my move
                            int my_move = current_room.action(game_index, enemy_mv);

                            if(my_move!=-1){ //Check if move is valid --> if game is finished move is equal to -1 (null for int)
                                //Send my move
                                sendMove(room_name, game_index, "TRISSER.bot3@gmail.com", my_move, current_client);
                            }
                        }
                    }


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
     * @throws Exception eccezione da gestire per il get del contenuto della mail
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

    /**
     * getTextFromMimeMultipart
     * Metodi per la lettura del contenuto html di una mail
     * @param mimeMultipart contenuto MIME della mail
     * @return Contenuto della mail in formato plain text e non in HTML
     * @throws Exception eccezione estrazione contenuto mail
     * **/
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
     * @throws Exception eccezione derivante dalla fallita connessione al mail server
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
                String room_name = room.toString();
                if(room_name.contains(EMAIL)){
                    topic_list.add(room_name);
                }
            }
        }else{
            Log.w("readMail", "Il server non ci ha assegnato nessuna stanza in cui giocare");
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
    //TODO Per ora username e password non sono ancora usati. Da scommentare nel caso in cui se ne faccia uso
    public MqttClient connectToServer(String username, String passwd) {
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            MqttClient my_client = new MqttClient(this.BROKER_HOST, this.PUBLISHER_ID, persistence);
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

            Log.d("connectToServer", "Il broker ( "+ this.BROKER_HOST +" ) ha accettato la nostra connessione ( PubID = " + this.PUBLISHER_ID + " ) con protocollo MQTT_VERSION_3_1");
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
            Log.e("goOnlineOnServer", "ERRORE! Iscrizione alla topic "+topic+" FALLITA!");
            return false;
        }
        try{
            current_client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
            Log.e("goOnlineOnServer", "ERRORE! Il client non riesce ad andare online, pubblicazione online:true FALLITA!");
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

                Log.d("subscribeTopic", "Nuova room trovata "+room);

                for (int i = 0; i < this.n_match_for_room; i++) {

                    String new_topic = room+"/"+i+"/"+getEnemyName(room);
                    this.current_client.subscribe(new_topic);
                }

                Log.d("subscribeTopic", "Iscrizione a tutti i game della room : "+room+" avvenuta con successo");
            }
        } catch (MqttException e) {
            Log.e("subscribeTopic", "Errore. Iscrizione alle topic fallita");
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * sendMove
     * metodo per inviare la mossa
     * @param room_name nome della stanza (TOPIC)
     * @param game_idx nome del game (SUB-TOPIC)
     * @param my_topic nome giocatore su cui pubblicare la topic [ovvero noi] (SUB-SUB-TOPIC)
     * @param current_client client su cui pubblicare la mossa
     * @param my_move la mossa da pubblicare
     * @see PublishMove
     * **/
    public void sendMove(String room_name, int game_idx, String my_topic, int my_move, MqttClient current_client){
        //Creo la topic
        String return_topic = room_name+"/"+game_idx+"/"+my_topic;
        String return_msg = "{\"move\":\"" + my_move + "\"}";
        //Creo il messaggio
        MqttMessage returnMqtt_msg = new MqttMessage(return_msg.getBytes(StandardCharsets.UTF_8));
        returnMqtt_msg.setQos(1);
        //Uso la classe PublishMove che crea un thread che pubblica la mossa
        PublishMove pb = new PublishMove(return_topic, returnMqtt_msg, current_client);
        pb.start();
    }
}
