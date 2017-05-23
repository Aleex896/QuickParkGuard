package com.example.alex.quickparkguard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Alex on 21/05/2017.
 */

public class InicioSessionVigilanteHttp extends AsyncTask<Void,Void, JSONArray>{

    private Activity myActivity;
    private Context mycontext;
    private String passU;
    private byte[] passBy;
    private String mail;

    //private CifrarDescifrar descifrar = new CifrarDescifrar();

    public InicioSessionVigilanteHttp(Context context, Activity activity){
        mycontext = context;
        myActivity = activity;
    }


    @Override
    protected JSONArray doInBackground(Void... params) {
        URL url;

        JSONArray jsonAr=null;


        try{
            mail = MainActivity.userMail;
            passU = MainActivity.password;


            url = new URL("http://25.103.185.238/quickpark/php/inicioSessionVigilante.php?user="+mail);
            HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();

            Log.d("inicio",url.toString());
            int status = urlConnection.getResponseCode();

            Log.d("estatUrl","estat:"+status);

            if (status == 200) // if response code = 200 ok
            {
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());

                BufferedReader br = new BufferedReader(new InputStreamReader(in));

                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                Log.d("inicio", sb.toString());
                urlConnection.disconnect();

                jsonAr =new JSONArray( sb.toString());

            }
        }catch (Exception ex){
            Log.d("errorI",ex.toString());
        }
        return jsonAr;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray){
        try {
            String passw = jsonArray.getJSONObject(0).getString("passbd").toString();
            /*String passw;
            passBy = jsonArray.getJSONObject(0).getString("passbd").getBytes("UTF-8");
            Log.d("inicioEstado",passBy.toString());
            passw = descifrar.descifra(passBy);
            Log.d("inicioEstado",passw);*/

            if(passw.equals(passU)){
                Log.d("inicioEstado","Contraseñas correctas");
                Toast toast = Toast.makeText(mycontext.getApplicationContext(),"Session Iniciada",Toast.LENGTH_LONG);
                toast.show();
                MainActivity.etPass.setText(null);
                MainActivity.etUsuario.setText(null);

                Intent gomap = new Intent(myActivity, ScreenVigilancia.class);
                gomap.putExtra("user", mail);
                myActivity.startActivity(gomap);

            }else{
                Log.d("inicioEstado","Datos incorrectos");
                Toast toast1 = Toast.makeText(mycontext.getApplicationContext(),"Correo o Contraseña Incorrectos",Toast.LENGTH_LONG);
                toast1.show();
                MainActivity.etPass.setText(null);
                MainActivity.etUsuario.setText(null);
            }
        } catch (Exception ex){
            Log.d("errorI",ex.toString());
        }

    }

}
