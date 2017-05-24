package com.example.alex.quickparkguard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import org.json.JSONArray;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by JoseAntonio on 23/05/2017.
 */

public class ListadoHttp extends AsyncTask<Void,Void,JSONArray> {
    Context mycontext;
    Activity myactivity;

    public ListadoHttp(Context xcontext, Activity xactivity){
        mycontext = xcontext;
        myactivity = xactivity;

    }

    @Override
    protected JSONArray doInBackground(Void... voids) {
        URL url;
        JSONArray jsonAr=null;

        try{
            url = new URL("http://25.103.185.238/quickpark/php/listVehiculosPlaza.php?zona="+ListadoActivity.zona+"&pob="+ListadoActivity.pob);
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

        Calendar mcurrentTime = Calendar.getInstance();
        final int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        final int minute = mcurrentTime.get(Calendar.MINUTE);
        final int day = mcurrentTime.get(Calendar.DAY_OF_MONTH);

        TableLayout tbLayout = ListadoActivity.table;

        String matricula = "";
        String tiempo = "";
        String fecha = "";

        try{
            for(int i=0;i<jsonArray.length();i++) {
                matricula = jsonArray.getJSONObject(i).getString("ve");
                //fecha = jsonArray.getJSONObject(i).getString("fe");
                tiempo = jsonArray.getJSONObject(i).getString("tp");

                final int selectedhour = Integer.parseInt(tiempo.substring(0,2));
                final int selectedmin = Integer.parseInt(tiempo.substring(3,5));

                TableRow tableRow = new TableRow(mycontext);
                tableRow.setVerticalGravity(1);
                tbLayout.addView(tableRow);

                TextView mat = new TextView(mycontext);
                mat.setGravity(Gravity.CENTER);
                mat.setText(matricula);
                mat.setTextSize(20);
                mat.setTypeface(myFont(mycontext));

                TextView tp = new TextView(mycontext);
                tp.setGravity(Gravity.CENTER);
                tp.setText(tiempo);
                tp.setTextSize(20);
                tp.setTypeface(myFont(mycontext));

                if(selectedhour>hour-2 && selectedhour<hour)
                {
                    tp.setTextColor(Color.RED);
                    mat.setTextColor(Color.RED);
                    tableRow.addView(mat);
                    tableRow.addView(tp);
                }
                else {
                    if (selectedhour == hour && selectedmin < minute) {
                        tp.setTextColor(Color.RED);
                        mat.setTextColor(Color.RED);
                        tableRow.addView(mat);
                        tableRow.addView(tp);
                    }
                }



            }
            ListadoActivity.llListado.addView(tbLayout);

        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }

    public static Typeface myFont(Context context) {
        return Typeface.createFromAsset(context.getAssets(), "fonts/Walkway SemiBold.ttf");
    }

}
