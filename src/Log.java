/**
 * Questa classe si occupa di fornire i metodi di log necessari alla visualizzazione di ogni tipologia di informazione
 * in modo leggibile ed ordinato tramite l'utilizzo di colori che vengono assegnati a seconda del tipo di messaggio ricevuto
 * @author Matteo Malvezzi
 * @see OnlineGame
 * **/
public class Log {

    /** ------------------------------------ Attributes ------------------------------------ **/

    /** <strong>ANSI_RESET</strong> Standard di segnalazione di reset **/
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_BLACK = "\u001B[30m";
    /** <strong>ANSI_RED</strong> Standard di segnalazione per gli errori **/
    public static final String ANSI_RED = "\u001B[31m";
    /** <strong>ANSI_GREEN</strong> Standard di segnalazione per le informazioni **/
    public static final String ANSI_GREEN = "\u001B[32m";
    /** <strong>ANSI_YELLOW</strong> Standard di segnalazione per i warning **/
    public static final String ANSI_YELLOW = "\u001B[33m";
    /** <strong>ANSI_BLUE</strong> Standard di segnalazione per il debug **/
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_CYAN = "\u001B[36m";
    public static final String ANSI_WHITE = "\u001B[37m";

    /** ------------------------------------ Log Methods ------------------------------------ **/

    /** Information Log **/
    public static void i(String method, String message) {
        System.out.println(ANSI_GREEN + method + " : " + message + ANSI_RESET);
    }

    /** Error Log **/
    public static void e(String method, String message) {
        System.out.println(ANSI_RED + method + " : " + message + ANSI_RESET);
    }

    /** Debug Log **/
    public static void d(String method, String message) {
        System.out.println(ANSI_BLUE + method + " : " + message + ANSI_RESET);
    }

    /** Warning Log **/
    public static void w(String method, String message) {
        System.out.println(ANSI_YELLOW + method + " : " + message + ANSI_RESET);
    }
}