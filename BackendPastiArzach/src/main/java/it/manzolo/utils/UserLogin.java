package it.manzolo.utils;

import java.io.IOException;

public class UserLogin implements java.io.Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    String matricola;
    String password;

    public void setMatricola(String matricola) {
        this.matricola = matricola;
    }

    public String getMatricola() {
        return matricola;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void writeObject(java.io.ObjectOutputStream stream)
            throws IOException {
        stream.writeObject(matricola);
        stream.writeObject(password);
    }

    public void readObject(java.io.ObjectInputStream stream)
            throws IOException, ClassNotFoundException {
        matricola = (String) stream.readObject();
        password = (String) stream.readObject();
    }

    public String toString() {
        return matricola;
    }

}
