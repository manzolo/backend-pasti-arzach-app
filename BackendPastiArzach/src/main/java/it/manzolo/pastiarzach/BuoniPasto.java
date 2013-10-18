package it.manzolo.pastiarzach;

public class BuoniPasto {
    int id;
    String valore;

    public BuoniPasto(int id, String valore) {
        this.id = id;
        this.valore = valore;
    }

    public String getValore() {
        return this.valore;
    }

    public void setValore(String valore) {
        this.valore = valore;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
