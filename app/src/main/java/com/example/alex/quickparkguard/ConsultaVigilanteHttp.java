package com.example.alex.quickparkguard;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by Alex on 22/05/2017.
 */

public class ConsultaVigilanteHttp extends AsyncTask<Void,Void,JSONArray>{

    Context mycontext;
    String idplaza;

    ScreenVigilancia sv;

    public static String plaza = "";
    public static String vehiculo = "";
    public static String fecha = "";
    public static String tiempo = "";

    public ConsultaVigilanteHttp(Context xcontext, String xidplaza){
        idplaza=xidplaza;
        mycontext = xcontext;
    }

    @Override
    protected JSONArray doInBackground(Void... params) {
        URL url;
        JSONArray jsonAr=null;

        try{
            url = new URL("http://25.103.185.238/quickpark/php/consultaVigilante.php?plaza="+idplaza+"");
            HttpURLConnection urlConnection=(HttpURLConnection)url.openConnection();

            Log.d("consulta plaza URL",url.toString());
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
                Log.d("listCar", sb.toString());
                urlConnection.disconnect();

                jsonAr = new JSONArray(sb.toString());

            }
        }catch (Exception ex)
        {
            Log.d("errorR",ex.toString());
        }
        return jsonAr;
    }

    @Override
    protected void onPostExecute(JSONArray jsonArray){
        super.onPostExecute(jsonArray);

        sv = new ScreenVigilancia();

        sv.relativeLayout.setVisibility(View.VISIBLE);

        plaza = "";
        vehiculo = "";
        fecha = "";
        tiempo = "";

        try{
            for(int i=0;i<jsonArray.length();i++) {

                plaza = jsonArray.getJSONObject(i).getString("idPlaza");
                vehiculo = jsonArray.getJSONObject(i).getString("vehiculo");
                fecha = jsonArray.getJSONObject(i).getString("fecha");
                tiempo = jsonArray.getJSONObject(i).getString("tiempo");
            }


            sv.tvfecha.setText(fecha.substring(0,10).toString());
            sv.texto.setText(plaza.toString());
            sv.matricula.setText(vehiculo.toString());
            sv.tVtiempolimite.setText(tiempo.toString());

            Calendar mcurrentTime = Calendar.getInstance();
            final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            final int minute = mcurrentTime.get(Calendar.MINUTE);
            final int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);

            final int selectedhour = Integer.parseInt(tiempo.substring(0,2));
            final int selectedmin = Integer.parseInt(tiempo.substring(3,5));
            final int selectedday = Integer.parseInt(fecha.substring(8,10));

            Log.d("dia", String.valueOf(selectedday));

            if(selectedday>=day)
            {
                if(selectedhour<hour)
                {
                    sv.tvnok.setVisibility(View.VISIBLE);
                    sv.tvok.setVisibility(View.INVISIBLE);
                }
                else
                {
                    if(selectedhour==hour && selectedmin<minute)
                    {
                        sv.tvnok.setVisibility(View.VISIBLE);
                        sv.tvok.setVisibility(View.INVISIBLE);
                    }
                    else
                    {
                        sv.tvok.setVisibility(View.VISIBLE);
                        sv.tvnok.setVisibility(View.INVISIBLE);
                    }
                }
            }
            else
            {
                sv.tvnok.setVisibility(View.VISIBLE);
                sv.tvok.setVisibility(View.INVISIBLE);
            }

        }
        catch(Exception ex)
        {

        }
    }
}
