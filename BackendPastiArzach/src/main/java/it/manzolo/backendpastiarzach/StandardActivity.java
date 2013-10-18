package it.manzolo.backendpastiarzach;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import it.manzolo.backendpastiarzach.ui.HomeActivity;
import it.manzolo.backendpastiarzach.ui.MenuActivity;
import it.manzolo.pastiarzach.ArzachUrls;
import it.manzolo.pastiarzach.Ordine;
import it.manzolo.utils.Internet;
import it.manzolo.utils.MessageBox;
import it.manzolo.utils.ToolTip;

public class StandardActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_standard);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.openAddMenu:
                openAddMenu();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void apriMenu() {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("Richiesta conferma")
                    .setMessage("Vuoi aprire il menu di oggi?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (Internet
                                            .isNetworkAvailable(StandardActivity.this)) {
                                        try {
                                            final ProgressDialog dialogWait = ProgressDialog
                                                    .show(StandardActivity.this,
                                                            "",
                                                            "Elaborazione dati..",
                                                            true);
                                            dialogWait.show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {
                                                    try {
                                                        String retStatus;
                                                        retStatus = new Internet(
                                                                ArzachUrls.APRI_GIORNO_PAGE)
                                                                .getResponse();
                                                        if (retStatus
                                                                .equals("OK")) {
                                                            new ToolTip(
                                                                    StandardActivity.this,
                                                                    "Menu aperto");
                                                            checkMenuStatus();
                                                        } else {
                                                            new MessageBox(
                                                                    StandardActivity.this,
                                                                    "Apertura menu",
                                                                    "Impossibile aprire il menu al momento");
                                                        }

                                                        dialogWait.dismiss();
                                                    } catch (Exception e) {
                                                        new ToolTip(
                                                                StandardActivity.this,
                                                                "Impossibile contattare il server Arzach, riprova",
                                                                true);
                                                        return;
                                                    }
                                                }
                                            }, 3000); // 3000 milliseconds
                                        } catch (Exception e) {
                                            new MessageBox(
                                                    StandardActivity.this,
                                                    "Apertura menu",
                                                    "Impossibile contattare il server arzach, controllare la connessione internet");
                                        }
                                    } else {
                                        new ToolTip(StandardActivity.this,
                                                "Nessuna connessione internet");
                                    }
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // Non si fa niente
                                }
                            }).show();
        } catch (Exception e) {
            new ToolTip(this, "Impossibile aprire il menu");
        }
    }

    public void sospendiMenu() {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("Richiesta conferma")
                    .setMessage("Vuoi sospendere il menu di oggi?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (Internet
                                            .isNetworkAvailable(StandardActivity.this)) {
                                        try {
                                            final ProgressDialog dialogWait = ProgressDialog
                                                    .show(StandardActivity.this,
                                                            "",
                                                            "Elaborazione dati..",
                                                            true);
                                            dialogWait.show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {
                                                    try {
                                                        String retStatus;
                                                        if (Internet
                                                                .isNetworkAvailable(StandardActivity.this)) {
                                                            try {
                                                                retStatus = new Internet(
                                                                        ArzachUrls.SOSPENDI_GIORNO_PAGE)
                                                                        .getResponse();
                                                                if (retStatus
                                                                        .equals("OK")) {
                                                                    new ToolTip(
                                                                            StandardActivity.this,
                                                                            "Menu sospeso");
                                                                    checkMenuStatus();
                                                                } else {
                                                                    new MessageBox(
                                                                            StandardActivity.this,
                                                                            "Sospensione menu",
                                                                            "Impossibile sospendere il menu al momento");
                                                                }
                                                            } catch (Exception e) {
                                                                new MessageBox(
                                                                        StandardActivity.this,
                                                                        "Sospensione menu",
                                                                        "Impossibile contattare il server arzach, controllare la connessione internet");
                                                            }
                                                        } else {
                                                            new ToolTip(
                                                                    StandardActivity.this,
                                                                    "Nessuna connessione internet");
                                                        }
                                                        dialogWait.dismiss();
                                                    } catch (Exception e) {
                                                        new ToolTip(
                                                                StandardActivity.this,
                                                                "Impossibile contattare il server Arzach, riprova",
                                                                true);
                                                        return;
                                                    }
                                                }
                                            }, 3000); // 3000 milliseconds
                                        } catch (Exception e) {
                                            new MessageBox(
                                                    StandardActivity.this,
                                                    "Sospensione menu",
                                                    "Impossibile contattare il server arzach, controllare la connessione internet");
                                        }
                                    } else {
                                        new ToolTip(StandardActivity.this,
                                                "Nessuna connessione internet");
                                    }
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // Non si fa niente
                                }
                            }).show();
        } catch (Exception e) {
            new ToolTip(this, "Impossibile sospendere il menu");
        }
    }

    public void chiudiMenu() {
        try {
            new AlertDialog.Builder(this)
                    .setTitle("Richiesta conferma")
                    .setMessage("Vuoi chiudere il menu di oggi?")
                    .setPositiveButton("Yes",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    if (Internet
                                            .isNetworkAvailable(StandardActivity.this)) {
                                        try {
                                            final ProgressDialog dialogWait = ProgressDialog
                                                    .show(StandardActivity.this,
                                                            "",
                                                            "Elaborazione dati..",
                                                            true);
                                            dialogWait.show();
                                            Handler handler = new Handler();
                                            handler.postDelayed(new Runnable() {
                                                public void run() {
                                                    try {
                                                        String retStatus;
                                                        if (Internet
                                                                .isNetworkAvailable(StandardActivity.this)) {
                                                            try {
                                                                retStatus = new Internet(
                                                                        ArzachUrls.CHIUDI_GIORNO_PAGE)
                                                                        .getResponse();
                                                                if (retStatus
                                                                        .equals("OK")) {
                                                                    new ToolTip(
                                                                            StandardActivity.this,
                                                                            "Menu chiuso");
                                                                    checkMenuStatus();
                                                                } else {
                                                                    new MessageBox(
                                                                            StandardActivity.this,
                                                                            "Chiusura menu",
                                                                            "Impossibile chiudere il menu al momento");
                                                                }
                                                            } catch (Exception e) {
                                                                new MessageBox(
                                                                        StandardActivity.this,
                                                                        "Chiusura menu",
                                                                        "Impossibile contattare il server arzach, controllare la connessione internet");
                                                            }
                                                        } else {
                                                            new ToolTip(
                                                                    StandardActivity.this,
                                                                    "Nessuna connessione internet");
                                                        }
                                                        dialogWait.dismiss();
                                                    } catch (Exception e) {
                                                        new ToolTip(
                                                                StandardActivity.this,
                                                                "Impossibile contattare il server Arzach, riprova",
                                                                true);
                                                        return;
                                                    }
                                                }
                                            }, 3000); // 3000 milliseconds
                                        } catch (Exception e) {
                                            new MessageBox(
                                                    StandardActivity.this,
                                                    "Chiusura menu",
                                                    "Impossibile contattare il server arzach, controllare la connessione internet");
                                        }
                                    } else {
                                        new ToolTip(StandardActivity.this,
                                                "Nessuna connessione internet");
                                    }
                                }
                            })
                    .setNegativeButton("No",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,
                                                    int which) {
                                    // Non si fa niente
                                }
                            }).show();
        } catch (Exception e) {
            new ToolTip(this, "Impossibile sospendere il menu");
        }
    }

    public void openAddMenu() {
        Intent intent = new Intent(this, MenuActivity.class);
        startActivity(intent);
        finish();
    }

    public void openHome() {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void goHome(View view) {
        openHome();
    }

    public void apriMenu(View view) {
        apriMenu();
    }

    public void chiudiMenu(View view) {
        chiudiMenu();
    }

    public void sospendiMenu(View view) {
        sospendiMenu();
    }

    public void inserisciMenu(View view) {
        openAddMenu();

    }

    public void checkMenuStatus() {
        final ProgressDialog dialog = ProgressDialog.show(
                StandardActivity.this, "", "Elaborazione dati..", true);
        dialog.show();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                Ordine ordine = new Ordine();
                View btnApri = findViewById(R.id.btnApriMenu);
                View btnChiudi = findViewById(R.id.btnChiudiMenu);
                View btnSospendi = findViewById(R.id.btnSospendiMenu);

                if (!ordine.isDisponibile()) {
                    btnApri.setVisibility(View.INVISIBLE);
                    btnChiudi.setVisibility(View.INVISIBLE);
                    btnSospendi.setVisibility(View.INVISIBLE);
                } else {
                    if (ordine.isAperto()) {
                        btnApri.setVisibility(View.INVISIBLE);
                        btnChiudi.setVisibility(View.VISIBLE);
                        btnSospendi.setVisibility(View.VISIBLE);
                    } else if (ordine.isChiuso()) {
                        btnApri.setVisibility(View.VISIBLE);
                        btnChiudi.setVisibility(View.INVISIBLE);
                        btnSospendi.setVisibility(View.VISIBLE);

                    } else if (ordine.isSospeso()) {
                        btnApri.setVisibility(View.VISIBLE);
                        btnChiudi.setVisibility(View.VISIBLE);
                        btnSospendi.setVisibility(View.INVISIBLE);
                    }
                }
                dialog.dismiss();
            }
        }, 3000); // 3000 milliseconds
    }

}
