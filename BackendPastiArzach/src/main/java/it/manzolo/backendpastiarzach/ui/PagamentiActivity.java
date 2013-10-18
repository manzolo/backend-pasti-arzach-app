package it.manzolo.backendpastiarzach.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import it.manzolo.backendpastiarzach.R;
import it.manzolo.pastiarzach.ArzachUrls;
import it.manzolo.pastiarzach.BuoniPasto;
import it.manzolo.pastiarzach.SpinBuonipastoAdapter;
import it.manzolo.pastiarzach.SpinUserAdapter;
import it.manzolo.pastiarzach.User;
import it.manzolo.utils.Internet;
import it.manzolo.utils.MessageBox;
import it.manzolo.utils.ToolTip;

public class PagamentiActivity extends Activity {
    static SpinUserAdapter usersAdapter;
    static SpinBuonipastoAdapter buoniAdapter;
    static Spinner ddUser;
    static Spinner ddBuono;
    static EditText numerobuoni;
    static EditText euro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagamenti);
        Button btnSalva = (Button) findViewById(R.id.btnSalva);
        ddUser = (Spinner) findViewById(R.id.ddDipendenti);
        ddBuono = (Spinner) findViewById(R.id.ddBuoni);
        numerobuoni = (EditText) findViewById(R.id.numerobuoni);
        euro = (EditText) findViewById(R.id.euro);
        numerobuoni.setText("0");

        ArrayList<User> users = new ArrayList<User>();
        User empty = new User(0, "");
        users.add(empty);
        try {
            JSONArray dipendentiLista = new Internet(
                    ArzachUrls.PAGAMENTI_LISTA_DIPENDENTI_PAGE).getJSONArray();
            for (int i = 0; i < dipendentiLista.length(); i++) {
                JSONObject dipendente = dipendentiLista.getJSONObject(i);
                User myUser = new User(Integer.parseInt(dipendente.get("id").toString()), dipendente.get("nominativo").toString());
                users.add(myUser);

            }

            usersAdapter = new SpinUserAdapter(PagamentiActivity.this, android.R.layout.simple_list_item_single_choice, users);
            ddUser.setAdapter(usersAdapter);


        } catch (final Exception e) {
            new ToolTip(PagamentiActivity.this, e.getMessage(), true);
        }

        ArrayList<BuoniPasto> buoni = new ArrayList<BuoniPasto>();
        try {
            JSONArray buoniLista = new Internet(
                    ArzachUrls.PAGAMENTI_LISTA_BUONIPASTO_PAGE).getJSONArray();
            for (int i = 0; i < buoniLista.length(); i++) {
                JSONObject buono = buoniLista.getJSONObject(i);
                BuoniPasto myBuono = new BuoniPasto(Integer.parseInt(buono.get("id").toString()), buono.get("valore").toString());
                buoni.add(myBuono);

            }

            buoniAdapter = new SpinBuonipastoAdapter(PagamentiActivity.this, android.R.layout.simple_spinner_item, buoni);
            ddBuono.setAdapter(buoniAdapter);
            ddBuono.setSelection(2);

        } catch (final Exception e) {
            new ToolTip(PagamentiActivity.this, e.getMessage(), true);
            Log.e("errore", e.getMessage());
        }

        btnSalva.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(PagamentiActivity.this)
                        .setTitle("inserimento pagamento")
                        .setMessage("Sei sicuro di voler inserire il pagamento?")
                        .setPositiveButton("Yes",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        HttpClient httpclient = new DefaultHttpClient();
                                        HttpPost httppost = new HttpPost(ArzachUrls.PAGAMENTI_ADD_PAGE);
                                        List<NameValuePair> pagamento = new ArrayList<NameValuePair>();
                                        String dipendente_id = String.valueOf(usersAdapter.getUserId((int) ddUser.getSelectedItemId()));
                                        if (Integer.parseInt(dipendente_id) <= 0) {
                                            new ToolTip(PagamentiActivity.this, "Specificare correttamente a chi accreditare il pagamento", true);
                                            return;
                                        }
                                        pagamento.add(new BasicNameValuePair("dipendente_id", dipendente_id));
                                        if (Integer.parseInt(numerobuoni.getText().toString()) <= 0 && Integer.parseInt(euro.getText().toString()) <= 0) {
                                            new ToolTip(PagamentiActivity.this, "Specificare correttamente il pagamento", true);
                                            return;
                                        }
                                        if (Integer.parseInt(numerobuoni.getText().toString()) > 0) {
                                            pagamento.add(new BasicNameValuePair("buonopasto_id", String.valueOf(buoniAdapter.getBuonoId((int) ddBuono.getSelectedItemId()))));
                                            pagamento.add(new BasicNameValuePair("buonopasto_quantita", numerobuoni.getText().toString()));
                                        }

                                        if (Float.parseFloat(euro.getText().toString()) > 0) {
                                            pagamento.add(new BasicNameValuePair("euro", euro.getText().toString()));
                                        }

                                        try {
                                            httppost.setEntity(new UrlEncodedFormEntity(pagamento));
                                        } catch (UnsupportedEncodingException e) {
                                            new ToolTip(PagamentiActivity.this, "Errore nell'invio del pagamento (0x10)", true);
                                            return;
                                        }
                                        // Si esegue la POST
                                        try {
                                            HttpResponse response = httpclient.execute(httppost);
                                            HttpEntity entity = response.getEntity();
                                            String responseText = EntityUtils.toString(entity);
                                            if (responseText.equals("OK")) {
                                                new ToolTip(PagamentiActivity.this, "Pagamento inserito!", true);
                                            } else {
                                                new MessageBox(PagamentiActivity.this, "Errore", "Errore nell'inserimento del pagamento!");
                                                return;
                                            }


                                        } catch (ClientProtocolException e) {
                                            new ToolTip(PagamentiActivity.this, "Errore nell'invio del pagamento (0x20)", true);
                                            return;
                                        } catch (IOException e) {
                                            new ToolTip(PagamentiActivity.this, "Errore nell'invio del pagamento (0x30)", true);
                                            return;
                                        }

                                        ddBuono.setSelection(2);
                                        ddUser.setSelection(0);
                                        euro.setText("0");
                                        numerobuoni.setText("0");
                                    }
                                })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                //new ToolTip(PagamentiActivity.this,String.valueOf(usersAdapter.getUserId((int) ddUser.getSelectedItemId())) + " " + buoniAdapter.getBuonoId((int) ddBuono.getSelectedItemId()) + " " + numerobuoni.getText().toString() + " " + euro.getText().toString(),true);
                //new ToolTip(PagamentiActivity.this,String.valueOf(usersAdapter.getUserId((int) ddUser.getSelectedItemId())) + " " + usersAdapter.getUserNominativo((int) ddUser.getSelectedItemId()));
                //new ToolTip(PagamentiActivity.this,String.valueOf(buoniAdapter.getBuonoId((int) ddBuono.getSelectedItemId())) + " " + buoniAdapter.getValore((int) ddBuono.getSelectedItemId()));
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.pagamenti, menu);
        return true;
    }

}
