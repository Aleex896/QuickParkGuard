package com.example.alex.quickparkguard;

import android.content.Context;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    public static String userMail;
    public static String password;
    private Context context;
    public static EditText etUsuario;
    public static EditText etPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etUsuario = (EditText) findViewById(R.id.eTUsuario);
        etPass = (EditText) findViewById(R.id.eTPass);
        Button iniciar = (Button) findViewById(R.id.button);

        etUsuario.setTypeface(myFont(this));
        etPass.setTypeface(myFont(this));
        iniciar.setTypeface(myFont(this));

        iniciar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userMail = etUsuario.getText().toString();
                password = etPass.getText().toString();

                if(!userMail.equals("") && !password.equals("")){
                    context = getApplicationContext();
                    //generarXML();
                    new InicioSessionVigilanteHttp(context, MainActivity.this).execute();
                }
                else
                {
                    Toast toast1 = Toast.makeText(getApplicationContext(), "Contraseñas Incorrectas", Toast.LENGTH_LONG);
                    toast1.show();
                }
            }
        });


    }

    // FUNCION PARA ASIGNAR LA FUENTE //////////////////////////////////////////////////////////////
    public static Typeface myFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Walkway SemiBold.ttf");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
}
