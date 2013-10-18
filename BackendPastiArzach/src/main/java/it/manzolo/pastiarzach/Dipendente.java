package it.manzolo.pastiarzach;


public class Dipendente {
    public static String MATRICOLA;
    public static String PASSWORD;
    public static String ID;
    public static String DIREZIONE;
    public static String LOCAZIONE;
    public static String NOMINATIVO;
    public static boolean AUTENTICATO;

    public static void cleanAll() {
        AUTENTICATO = false;
        MATRICOLA = "";
        PASSWORD = "";
        ID = "";
        DIREZIONE = "";
        LOCAZIONE = "";
        NOMINATIVO = "";
    }


}
