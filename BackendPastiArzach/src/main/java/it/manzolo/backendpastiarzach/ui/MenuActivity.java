package it.manzolo.backendpastiarzach.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import it.manzolo.backendpastiarzach.R;
import it.manzolo.backendpastiarzach.StandardActivity;
import it.manzolo.pastiarzach.ArzachUrls;
import it.manzolo.pastiarzach.Ordine;
import it.manzolo.utils.MessageBox;
import it.manzolo.utils.ToolTip;

public class MenuActivity extends StandardActivity {
    List<NameValuePair> pietanze = new ArrayList<NameValuePair>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    @Override
    public void onBackPressed() {
        Intent goHome = new Intent(this, HomeActivity.class);
        startActivity(goHome);
        finish();
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            final ProgressDialog dialog = ProgressDialog.show(MenuActivity.this, "", "Elaborazione dati..", true);
            dialog.show();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    try {
                        generateMenu();
                        dialog.dismiss();
                    } catch (Exception e) {
                        new ToolTip(MenuActivity.this, "Impossibile contattare il server Arzach, riprova", true);
                        return;
                    }
                }
            }, 3000);  // 3000 milliseconds
        } catch (Exception e) {
            new ToolTip(this, "Impossibile comunicare con il server", true);
        }
    }

    private void generateMenu() {
        boolean almenoUno = false;
        LinearLayout rl = (LinearLayout) findViewById(R.id.scrollLinearLayout);
        rl.removeAllViews();
        pietanze.clear();
        try {
            Ordine ordine = new Ordine();
            if (ordine.isDisponibile() && !ordine.isAperto()) {
                JSONArray jsonArray = ordine.getListaPiatti();
                for (int i = 0; i < jsonArray.length(); i++) {
                    almenoUno = true;
                    //Si prede ogni pietanza
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    //SI creano le singole pietanze con il proprio checkbox (se il menu è aperto, altrimenti Textview)
                    View tv;
                    if (!ordine.isAperto()) {
                        tv = new CheckBox(this);
                        ((CheckBox) tv).setTextSize(15);
                    } else {
                        tv = new TextView(this);
                        ((TextView) tv).setTextSize(15);
                    }

                    Integer pietanzaID = Integer.parseInt(jsonObject.getString("pasto_id"));
                    pietanze.add(new BasicNameValuePair("id", jsonObject.getString("pasto_id")));
                    tv.setId(pietanzaID);

                    Boolean primo = jsonObject.getString("primo").equals("1");
                    Boolean secondo = jsonObject.getString("secondo").equals("1");
                    Boolean contorno = jsonObject.getString("contorno").equals("1");
                    Boolean dolce = jsonObject.getString("dolce").equals("1");

                    //Si colorano diversamente i tipi di piatto
                    if (primo) {
                        ((TextView) tv).setTextColor(Color.BLUE);
                    } else if (secondo) {
                        ((TextView) tv).setTextColor(Color.rgb(0, 150, 0));
                    } else if (contorno) {
                        ((TextView) tv).setTextColor(Color.parseColor("#DD6F00"));
                    } else if (dolce) {
                        ((TextView) tv).setTextColor(Color.parseColor("#663300"));
                    }
                    ((TextView) tv).setGravity(Gravity.LEFT);
                    String descPortata = "";
                    if (primo) {
                        descPortata = " (Primo)";
                    }
                    if (secondo) {
                        descPortata = " (Secondo)";
                    }
                    if (contorno) {
                        descPortata = " (Contorno)";
                    }
                    if (dolce) {
                        descPortata = " (Dolce)";
                    }
                    ((TextView) tv).setText(jsonObject.getString("descrizione") + descPortata + "\n");
                    rl.addView(tv);
                }
            }

            //Si genera il tasto che permette la cancellazione
            if (almenoUno && !ordine.isAperto()) {
                Button btnCancella = new Button(this);
                btnCancella.setText("Cancella selezionati");
                btnCancella.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        cancella(v);
                    }
                });

                rl.addView(btnCancella);
            }
        } catch (Exception e) {
            //Se non si e' autorizzati si da l'avviso
            new ToolTip(this, "Impossibile controllare i piatti inseriti, riprova", true);
            return;
        }

    }


    public void cancella(View view) {
        List<NameValuePair> selezionati = new ArrayList<NameValuePair>();

        // Si crea una connessione sulla pagina che permette di cancellare i pasti
        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(ArzachUrls.ADMIN_CANCELLA_PIATTI_GIORNO_PAGE);

        for (NameValuePair pietanza : pietanze) {
            CheckBox elemento = (CheckBox) findViewById(Integer.parseInt(pietanza.getValue()));
            if (elemento.isChecked()) {
                selezionati.add(new org.apache.http.message.BasicNameValuePair("options[]", pietanza.getValue()));
            }
        }
        if (selezionati.size() <= 0) {
            new ToolTip(this, "Seleziona almeno un piatto!");
            return;
        }

        try {
            httppost.setEntity(new UrlEncodedFormEntity(selezionati));
            //Si esegue la POST alla pagina che raccoglie gli ordini
            try {
                HttpResponse response = httpclient.execute(httppost);
                JSONArray jsonArray;
                try {
                    String messaggi = "";

                    BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent(), "UTF-8"));
                    StringBuilder builder = new StringBuilder();
                    for (String line = null; (line = reader.readLine()) != null; ) {
                        builder.append(line).append("\n");
                        Log.i("json", builder.toString());
                    }
                    JSONTokener tokener = new JSONTokener(builder.toString());
                    jsonArray = new JSONArray(tokener);

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        messaggi = messaggi + jsonObject.getString("messaggio") + "\n";
                    }
                    new ToolTip(this, messaggi, true);
                } catch (ParseException e) {
                    new ToolTip(this, "Impossibile cancellare alcuni piatti del menu (0x1)", true);
                } catch (JSONException e) {
                    new ToolTip(this, "Impossibile cancellare alcuni piatti del menu (0x2)", true);
                }

            } catch (ClientProtocolException e) {
                new ToolTip(this, "Impossibile cancellare alcuni piatti del menu (0x3)", true);
            } catch (IOException e) {
                new ToolTip(this, "Impossibile cancellare alcuni piatti del menu (0x4)", true);
            }
        } catch (UnsupportedEncodingException e) {
            new ToolTip(this, "Impossibile cancellare alcuni piatti del menu (0x5)", true);
        }

        new ToolTip(this, "Piatto/i cancellato/i, ricarico il menu!!");
        generateMenu();

    }

    public void aggiungi(View view) {
        EditText txtDescrizione = (EditText) findViewById(R.id.descrizione);

        if (txtDescrizione.length() <= 0) {
            new MessageBox(MenuActivity.this,
                    "Attenzione", "Dare una descrizione a questo piatto!");
            return;
        }
        RadioButton rbPrimo = (RadioButton) findViewById(R.id.primo);
        RadioButton rbSecondo = (RadioButton) findViewById(R.id.secondo);
        RadioButton rbContorno = (RadioButton) findViewById(R.id.contorno);
        RadioButton rbDolce = (RadioButton) findViewById(R.id.dolce);
        String piatto = txtDescrizione.getText().toString();

        String tipo = "";
        if (rbPrimo.isChecked()) {
            tipo = "1";
        }
        if (rbSecondo.isChecked()) {
            tipo = "2";
        }
        if (rbContorno.isChecked()) {
            tipo = "3";
        }
        if (rbDolce.isChecked()) {
            tipo = "4";
        }

        HttpClient httpclient = new DefaultHttpClient();
        HttpPost httppost = new HttpPost(ArzachUrls.ADD_MENU_PAGE);

        try {

            List<NameValuePair> selezionati = new ArrayList<NameValuePair>();
            selezionati.add(new BasicNameValuePair("descrizione", piatto));
            selezionati.add(new BasicNameValuePair("tipo", tipo));
            httppost.setEntity(new UrlEncodedFormEntity(selezionati));

            httpclient.execute(httppost);

        } catch (ClientProtocolException e) {
            new MessageBox(
                    MenuActivity.this,
                    "Attenzione",
                    "Non e' stato possibile inviare il pasto, controllare di essere connessi a internet");

        } catch (IOException e) {
            new MessageBox(MenuActivity.this, "Attenzione",
                    "Non e' stato possibile inviare il pasto, errore nell'invio, riprovare!");

        }

        rbPrimo.setChecked(true);
        txtDescrizione.setText("");
        new ToolTip(this, "Piatto aggiunto, ricarico il menu!!", true);
        final ProgressDialog dialog = ProgressDialog.show(MenuActivity.this, "", "Elaborazione dati..", true);
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                try {
                    generateMenu();
                    dialog.dismiss();
                } catch (Exception e) {
                    new ToolTip(MenuActivity.this, "Impossibile contattare il server Arzach, riprova", true);
                    return;
                }
            }
        }, 3000);  // 3000 milliseconds

    }

}
