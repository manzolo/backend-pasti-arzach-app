package it.manzolo.backendpastiarzach.ui;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import it.manzolo.backendpastiarzach.R;
import it.manzolo.pastiarzach.ArzachUrls;
import it.manzolo.utils.Parameters;
import it.manzolo.utils.ToolTip;

public class RiepilogoGiornalieroActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_riepilogo_giornaliero);
        ImageButton bntSendMail = (ImageButton) findViewById(R.id.btnSendMail);
        bntSendMail.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sendRiepilogo();
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.riepilogo_giornaliero, menu);
        return true;
    }

    @Override
    public void onBackPressed() {
        Intent goHome = new Intent(this, HomeActivity.class);
        startActivity(goHome);
        finish();
    }

    public void sendRiepilogo() {
        final HttpClient client = new DefaultHttpClient(new BasicHttpParams());
        final HttpGet mHttpGetRequest = new HttpGet(ArzachUrls.RIEPILOGO_WORD_PAGE);
        mHttpGetRequest.setHeader("Content-Type", "application/x-www-form-urlencoded");
        HttpResponse response = null;
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), Parameters.RIEPILOGO_WORD);
        EditText to = (EditText) findViewById(R.id.destinatario);
        EditText oggetto = (EditText) findViewById(R.id.oggetto);
        //String extStorage = Environment.getExternalStorageState();

        try {
            response = client.execute(mHttpGetRequest);
        } catch (ClientProtocolException e1) {
            new ToolTip(this, "Impossibile scaricare il riepilogo (0x10), riprovare");
        } catch (IOException e1) {
            new ToolTip(this, "Impossibile scaricare il riepilogo (0x20), riprovare");
        }
        final StatusLine statusLine = response.getStatusLine();
        int lastHttpErrorCode = statusLine.getStatusCode();
        //String lastHttpErrorMsg = statusLine.getReasonPhrase();
        if (lastHttpErrorCode == 200) {
            HttpEntity entity = response.getEntity();
            InputStream inputStream = null;
            try {
                inputStream = entity.getContent();
            } catch (IllegalStateException e) {
                new ToolTip(this, "Impossibile scaricare il riepilogo (0x30), riprovare");
            } catch (IOException e) {
                new ToolTip(this, "Impossibile scaricare il riepilogo (0x40), riprovare");
            }


            FileOutputStream output = null;
            try {
                output = new FileOutputStream(file);
            } catch (FileNotFoundException e) {
                new ToolTip(this, "Impossibile scaricare il riepilogo (0x50), riprovare");
            }
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];
            int len = 0;
            try {
                while ((len = inputStream.read(buffer)) != -1) {
                    output.write(buffer, 0, len);
                }
                inputStream.close();
                output.flush();
                output.close();
            } catch (IOException e) {
                new ToolTip(this, "Impossibile scaricare il riepilogo (0x60), riprovare");
            }
        }

        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("message/rfc822");
        i.putExtra(Intent.EXTRA_EMAIL, new String[]{to.getText().toString()});
        i.putExtra(Intent.EXTRA_SUBJECT, oggetto.getText().toString());
        i.putExtra(Intent.EXTRA_TEXT, "In allegato l'ordine di oggi");

        if (!file.exists() || !file.canRead()) {
            Toast.makeText(this, "Errore nell'allegare il file", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        Uri uri = Uri.fromFile(file);
        i.putExtra(Intent.EXTRA_STREAM, uri);

        try {
            startActivity(Intent.createChooser(i, "Invio e-mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Non � stato configurato nessun client di posta elettronica su questo dispositivo.", Toast.LENGTH_SHORT).show();
        }

    }

}
