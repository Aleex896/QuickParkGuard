package com.example.alex.quickparkguard;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.Calendar;

public class ScreenVigilancia extends AppCompatActivity {

    private String usuario;
    public static final int REQUEST_CODE = 1;

    public static TextView texto;
    public static TextView nusuario;
    public static TextView titulovigi;
    public static TextView matricula;
    public static TextView tVtiempolimite;
    public static RelativeLayout relativeLayout;
    private String textoqr;
    private Context context;
    private TextRecognizer detector;
    private String idPlaza;

    public static TextView tvok;
    public static TextView tvnok;

    public static TextView tvfecha;

    public static Button btreset;

    TextView clock;
    TextView fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_usuario);

        clock = (TextView) findViewById(R.id.tVClock);
        fecha = (TextView) findViewById(R.id.tVDate);

        tvfecha = (TextView) findViewById(R.id.fechausuario);

        tvnok = (TextView) findViewById(R.id.tVNoOK);
        tvok = (TextView) findViewById(R.id.tVOK);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        btreset = (Button) findViewById(R.id.breset);
        btreset.setVisibility(View.INVISIBLE);


        usuario = getIntent().getStringExtra("user");

        nusuario = (TextView) findViewById(R.id.nombrevigi);
        titulovigi = (TextView) findViewById(R.id.tVVigilante);
        matricula = (TextView) findViewById(R.id.matricula);
        tVtiempolimite = (TextView) findViewById(R.id.tVtiempolimite);


        Button inilector = (Button) findViewById(R.id.bIniciarLector);
        texto = (TextView) findViewById(R.id.textoqr);

        detector = new TextRecognizer.Builder(this).build();

        inilector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Thread.sleep(500);
                }catch (Exception ex)
                {

                }
                Intent intent = new Intent(ScreenVigilancia.this, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE);

            }
        });

        btreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relativeLayout.setVisibility(View.INVISIBLE);
                tvnok.setVisibility(View.INVISIBLE);
                tvok.setVisibility(View.INVISIBLE);
                btreset.setVisibility(View.INVISIBLE);
            }
        });

        nusuario.setText(usuario);
        inilector.setTypeface(myFont(this));
        nusuario.setTypeface(myFont(this));
        titulovigi.setTypeface(myFont(this));

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    while (!isInterrupted()) {
                        Thread.sleep(1000);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                updateTextView();
                            }
                        });
                    }
                } catch (InterruptedException e) {
                }
            }
        };

        t.start();



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK)
            if (data != null) {
                final Barcode barcode = data.getParcelableExtra("barcode");
                textoqr = barcode.displayValue;
                idPlaza = textoqr.substring(0,15);
                texto.setText(idPlaza);

                try {
                    new ConsultaVigilanteHttp(context,idPlaza).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            btreset.setVisibility(View.VISIBLE);
    }

    private void updateTextView() {
        java.util.Date noteTS = Calendar.getInstance().getTime();

        String time = "HH:mm:ss"; // 12:00
        clock.setText(android.text.format.DateFormat.format(time, noteTS));

        String date = "dd-MM-yyyy"; // 01 January 2013
        fecha.setText(android.text.format.DateFormat.format(date, noteTS));
    }





    // FUNCION PARA ASIGNAR LA FUENTE //////////////////////////////////////////////////////////////
    public static Typeface myFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Walkway SemiBold.ttf");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onBackPressed() {

        relativeLayout.setVisibility(View.INVISIBLE);
        tvnok.setVisibility(View.INVISIBLE);
        tvok.setVisibility(View.INVISIBLE);

    }
}
