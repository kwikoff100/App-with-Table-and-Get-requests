package com.example.stitchinggetandtable;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class getData extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "getData";

    String data;
    //public static ArrayList<expenseDataObject> expenseObjects;
    @Override
    protected Void doInBackground(Void... voids) {

        try {
            //expenseObjects = new ArrayList<>();
            URL url = new URL("https://trackdatcash.herokuapp.com/expenses/");

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            InputStream inputStream = connection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            String line = "";
            while(line != null)
            {

                line = br.readLine();
                data = data + line;
            }

            data = data.replaceAll("null", "");
            //Log.e(TAG, data);
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }


        return null;
    }



    @Override
    protected void onPostExecute(Void aVoid) {
        //super.onPostExecute(aVoid);

        MainActivity.longString = (this.data);


    }


}
