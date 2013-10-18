package it.manzolo.pastiarzach;

public class User {
    int id;
    String nominativo;

    public User(int id, String nominativo) {
        this.id = id;
        this.nominativo = nominativo;
    }

    public String getNominativo() {
        return this.nominativo;
    }

    public void setNominativo(String nominativo) {
        this.nominativo = nominativo;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
