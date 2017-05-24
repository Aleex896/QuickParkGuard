package com.example.alex.quickparkguard;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.text.TextRecognizer;

import java.util.Calendar;
import java.util.StringTokenizer;

public class ListadoActivity extends AppCompatActivity {

    private TextRecognizer detector;

    public static final int REQUEST_CODE = 1;
    public static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 1;
    Button lector,btact;
    TextView nusuario;
    TextView clock;
    TextView fecha;
    String usuario,textoqr;
    public static String pob,zona;
    Context context;

    public static TableLayout table;
    public static ScrollView llListado;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listado);

        clock = (TextView) findViewById(R.id.tVClock);
        fecha = (TextView) findViewById(R.id.tVDate);

        llListado = (ScrollView)findViewById(R.id.svlistado);
        table = (TableLayout)findViewById(R.id.tablelayout);

        btact = (Button)findViewById(R.id.breset);
        btact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    llListado.setVisibility(View.VISIBLE);
                    table.removeAllViews();
                    TableRow tableRow = new TableRow(context);
                    tableRow.setVerticalGravity(1);
                    table.addView(tableRow);

                    TextView mat = new TextView(context);
                    mat.setGravity(Gravity.CENTER);
                    mat.setText("Matricula");
                    mat.setTextSize(20);
                    mat.setTypeface(myFont(context));

                    TextView tp = new TextView(context);
                    tp.setGravity(Gravity.CENTER);
                    tp.setText("Tiempo Max.");
                    tp.setTextSize(20);
                    tp.setTypeface(myFont(context));

                    tableRow.addView(mat);
                    tableRow.addView(tp);

                    context=getApplicationContext();
                    new ListadoHttp(context,ListadoActivity.this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        if (ContextCompat.checkSelfPermission(ListadoActivity.this,
                Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(ListadoActivity.this,
                    Manifest.permission.CAMERA)) {

            } else {
                ActivityCompat.requestPermissions(ListadoActivity.this,
                        new String[]{Manifest.permission.CAMERA},
                        MY_PERMISSIONS_REQUEST_READ_CONTACTS);
            }
        }

        usuario = getIntent().getStringExtra("user");

        nusuario = (TextView) findViewById(R.id.nombrevigi);
        nusuario.setText(usuario);
        lector = (Button)findViewById(R.id.bIniciarLector);


        lector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                    Thread.sleep(500);
                }catch (Exception ex)
                {
                    ex.printStackTrace();
                }
                Intent intent = new Intent(ListadoActivity.this, ScanActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
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
                llListado.setVisibility(View.VISIBLE);
                textoqr = barcode.displayValue;
                Log.d("qr",textoqr);

                StringTokenizer st = new StringTokenizer(textoqr,";");
                pob = st.nextToken();
                zona = st.nextToken();
                zona = zona.replace(" ","%20");
                pob = pob.replace(" ","%20");
                Log.d("qr",pob);
                Log.d("qr",zona);

                lector.setVisibility(View.INVISIBLE);

                try {
                    context=getApplicationContext();
                    new ListadoHttp(context,this).execute();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        btact.setVisibility(View.VISIBLE);
    }

    private void updateTextView() {
        java.util.Date noteTS = Calendar.getInstance().getTime();

        String time = "HH:mm:ss"; // 12:00
        clock.setText(android.text.format.DateFormat.format(time, noteTS));

        String date = "dd-MM-yyyy"; // 01 January 2013
        fecha.setText(android.text.format.DateFormat.format(date, noteTS));
    }

    public static Typeface myFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Walkway SemiBold.ttf");
    }
}
