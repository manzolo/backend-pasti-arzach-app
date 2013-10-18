package it.manzolo.backendpastiarzach.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;

import it.manzolo.backendpastiarzach.R;
import it.manzolo.backendpastiarzach.RiepilogoGiornalieroActivity;
import it.manzolo.backendpastiarzach.StandardActivity;
import it.manzolo.pastiarzach.Ordine;
import it.manzolo.utils.ToolTip;

public class HomeActivity extends StandardActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        View statusBar = findViewById(R.id.StatoBar);
        View btnApri = statusBar.findViewById(R.id.btnApriMenu);
        View btnChiudi = statusBar.findViewById(R.id.btnChiudiMenu);
        View btnSospendi = statusBar.findViewById(R.id.btnSospendiMenu);
        btnApri.setVisibility(View.INVISIBLE);
        btnChiudi.setVisibility(View.INVISIBLE);
        btnSospendi.setVisibility(View.INVISIBLE);

        ImageButton btnAddMenu = (ImageButton) findViewById(R.id.btnAddMenu);
        ImageButton btnRiepilogoGiornaliero = (ImageButton) findViewById(R.id.btnRiepilogoGiornaliero);
        ImageButton btnPagamenti = (ImageButton) findViewById(R.id.btnPagamenti);

        //Si aggiunge l'handler onClick al pulsante aggiungi menu
        btnAddMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addMenuIntent = new Intent(HomeActivity.this, MenuActivity.class);
                startActivity(addMenuIntent);
                finish();
            }
        });

        //Si aggiunge l'handler onClick al pulsante pagamenti
        btnPagamenti.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pagamentiIntent = new Intent(HomeActivity.this, PagamentiActivity.class);
                startActivity(pagamentiIntent);
                finish();
            }
        });

        //Si aggiunge l'handler onClick al pulsante genera riepilogo
        btnRiepilogoGiornaliero.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Ordine ordine = new Ordine();
                if (ordine.isDisponibile() && ordine.isChiuso()) {
                    Intent riepilogoGiornalieroIntent = new Intent(HomeActivity.this, RiepilogoGiornalieroActivity.class);
                    startActivity(riepilogoGiornalieroIntent);
                    finish();
                } else {
                    new ToolTip(HomeActivity.this, "Per spedire il riepilogo il menu dev'essere chiuso!", true);
                    return;
                }
            }
        });
        //Si controlla lo stato del menu
        checkMenuStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void goHome(View view) {
        super.goHome(view);
    }

    @Override
    public void apriMenu(View view) {
        super.apriMenu(view);
    }

    @Override
    public void chiudiMenu(View view) {
        super.chiudiMenu(view);
    }

    @Override
    public void sospendiMenu(View view) {
        super.sospendiMenu(view);
    }

    @Override
    public void inserisciMenu(View view) {
        super.inserisciMenu(view);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void checkMenuStatus() {
        super.checkMenuStatus();
    }

}
