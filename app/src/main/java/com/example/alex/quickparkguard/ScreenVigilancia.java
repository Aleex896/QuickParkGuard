package com.example.alex.quickparkguard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.Serializable;
import java.util.Calendar;

public class ScreenVigilancia extends AppCompatActivity implements Serializable{

    private String usuario;
    public static final int REQUEST_CODE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;

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

    public static Button btreset,inilector,blistado;

    private TextView puede,plaza,idpla,matr;
    TextView clock;
    TextView fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_usuario);


        if (ContextCompat.checkSelfPermission(ScreenVigilancia.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {

// Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(ScreenVigilancia.this,
                    Manifest.permission.CAMERA)) {

// Show an explanation to the user *asynchronously* -- don't block
// this thread waiting for the user's response! After the user
// sees the explanation, try again to request the permission.

            } else {

// No explanation needed, we can request the permission.

                ActivityCompat.requestPermissions(ScreenVigilancia.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);

// MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
// app-defined int constant. The callback method gets the
// result of the request.
            }
        }

        usuario = getIntent().getStringExtra("user");

        clock = (TextView) findViewById(R.id.tVClock);
        fecha = (TextView) findViewById(R.id.tVDate);
        tvfecha = (TextView) findViewById(R.id.fechausuario);
        tvnok = (TextView) findViewById(R.id.tVNoOK);
        tvok = (TextView) findViewById(R.id.tVOK);
        nusuario = (TextView) findViewById(R.id.nombrevigi);
        titulovigi = (TextView) findViewById(R.id.tVVigilante);
        matricula = (TextView) findViewById(R.id.matricula);
        tVtiempolimite = (TextView) findViewById(R.id.tVtiempolimite);
        texto = (TextView) findViewById(R.id.textoqr);
        puede = (TextView) findViewById(R.id.textView4);
        matr =(TextView)findViewById(R.id.matricula);
        plaza = (TextView)findViewById(R.id.textView3);
        idpla = (TextView)findViewById(R.id.textView2);

        relativeLayout = (RelativeLayout) findViewById(R.id.relativeLayout);

        btreset = (Button) findViewById(R.id.breset);
        inilector = (Button) findViewById(R.id.bIniciarLector);
        blistado = (Button)findViewById(R.id.bListado);


        btreset.setVisibility(View.INVISIBLE);

        nusuario.setText(usuario);

        inilector.setTypeface(myFont(this));
        nusuario.setTypeface(myFont(this));
        titulovigi.setTypeface(myFont(this));
        btreset.setTypeface(myFont(this));
        blistado.setTypeface(myFont(this));
        tVtiempolimite.setTypeface(myFont(this));
        tvfecha.setTypeface(myFont(this));
        tvok.setTypeface(myFont(this));
        tvnok.setTypeface(myFont(this));
        puede.setTypeface(myFont(this));
        matr.setTypeface(myFont(this));
        texto.setTypeface(myFont(this));
        plaza.setTypeface(myFont(this));
        idpla.setTypeface(myFont(this));

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
                intent.putExtra("user", usuario);
                startActivityForResult(intent, REQUEST_CODE);

            }
        });

        blistado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent golistado = new Intent(ScreenVigilancia.this,ListadoActivity.class);
                golistado.putExtra("user",usuario);
                startActivity(golistado);
            }
        });

        btreset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blistado.setVisibility(View.VISIBLE);
                inilector.setVisibility(View.VISIBLE);
                relativeLayout.setVisibility(View.INVISIBLE);
                tvnok.setVisibility(View.INVISIBLE);
                tvok.setVisibility(View.INVISIBLE);
                btreset.setVisibility(View.INVISIBLE);
            }
        });



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
                blistado.setVisibility(View.INVISIBLE);
                inilector.setVisibility(View.INVISIBLE);
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
