package it.manzolo.backendpastiarzach.ui;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import it.manzolo.backendpastiarzach.R;
import it.manzolo.pastiarzach.ArzachUrls;
import it.manzolo.pastiarzach.Dipendente;
import it.manzolo.utils.Internet;
import it.manzolo.utils.Parameters;
import it.manzolo.utils.ToolTip;
import it.manzolo.utils.UpdateNotification;
import it.manzolo.utils.UserLogin;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Just for testing, allow network access in the main thread, NEVER use
        // this is productive code
        // StrictMode.ThreadPolicy policy = new
        // StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitNetwork().build();
        StrictMode.setThreadPolicy(policy);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        EditText txtMatricola = (EditText) findViewById(R.id.matricola);
        EditText txtPassword = (EditText) findViewById(R.id.password);

        UserLogin user = new UserLogin();
        //Si leggono se sono presenti credenziali salvate
        try {
            File file = new File(this.getCacheDir(), Parameters.FILENAME);
            FileInputStream fileInput = new FileInputStream(file);
            ObjectInputStream objectInput = new ObjectInputStream(fileInput);
            user.readObject(objectInput);
        } catch (Exception e) {
            //Se non si legge il file con le credenziali non importa
        }

        txtMatricola.setText(user.getMatricola());
        txtPassword.setText(user.getPassword());

        //Si controlla se ci sono aggiornamenti della App
        new UpdateNotification(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    /**
     * Viene chiamato appena si clicca su accedi
     */
    public void login(View view) {
        try {

            EditText txtMatricola = (EditText) findViewById(R.id.matricola);
            String matricola = txtMatricola.getText().toString();
            EditText txtPassword = (EditText) findViewById(R.id.password);
            String password = txtPassword.getText().toString();

            if (matricola.length() == 0) {
                new ToolTip(this, "Inserisci prima la matricola");
                return;
            }
            if (password.length() == 0) {
                new ToolTip(this, "Inserisci la password");
                return;
            }
            Dipendente.AUTENTICATO = isAutenticato(matricola, password);
            if (Dipendente.AUTENTICATO) {
                //Si salva un oggetto con le credenziali
                saveLoginInformation(matricola, password);
                Intent intent = new Intent(this, HomeActivity.class);
                intent.putExtra("matricola", matricola);
                startActivity(intent);
            } else {
                Dipendente.cleanAll();
                // Se non si e' autorizzati si da l'avviso
                new ToolTip(this, "Autenticazione non riuscita", true);
                return;
            }

        } catch (Exception e) {
            new ToolTip(this,
                    "Impossibile stabilire una connessione con il server");
            return;
        }

    }

    private void saveLoginInformation(String matricola, String password) {
        try {
            File file = new File(this.getCacheDir(), Parameters.FILENAME);
            FileOutputStream fileOutput = new FileOutputStream(file);
            ObjectOutputStream objectOutput = new ObjectOutputStream(fileOutput);
            UserLogin user = new UserLogin();
            user.setMatricola(matricola);
            user.setPassword(password);
            user.writeObject(objectOutput);
            objectOutput.flush();
            objectOutput.close();
            Dipendente.MATRICOLA = user.getMatricola();
            Dipendente.PASSWORD = user.getPassword();
        } catch (Exception e) {
            //Se non riesco a salvare le informzioni utente si avverte e basta
            Dipendente.cleanAll();
            new ToolTip(this, "Impossibile salvare l'utente e la password");
        }
    }

    private boolean isAutenticato(String matricola, String password) {
        boolean state;
        JSONObject retval = null;

        try {
            retval = new Internet(ArzachUrls.LOGIN_ADMIN_PAGE + matricola
                    + "/password/" + password).getJSONObject();
        } catch (Exception e) {
            new ToolTip(this, "Impossibile contattare il server Arzach", true);
            return false;
        }
        try {
            if (Integer.parseInt(retval.getString("retcode")) == 0) {
                state = true;
            } else {
                state = false;
            }
        } catch (NumberFormatException e) {
            return false;
        } catch (JSONException e) {
            return false;
        }
        return state;

    }
}
