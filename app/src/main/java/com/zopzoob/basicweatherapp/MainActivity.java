package com.zopzoob.basicweatherapp;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    EditText et;

    public void display(View view) {


        InputMethodManager inmmg = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inmmg.hideSoftInputFromWindow(et.getWindowToken(),0);
        DownloadTask dt = new DownloadTask();

        String loc = "";
        try {
            loc = URLEncoder.encode(et.getText().toString(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        if (loc.isEmpty()) {
            Toast.makeText(getApplicationContext(), "City unavailable, try again", Toast.LENGTH_LONG);
            return;
        }
        //    Log.i("url: ", "http://api.openweathermap.org/data/2.5/weather?q=" + et.getText().toString() + "&APPID=da8b6bf394c4127545ffbf42c4f14d8d");
        dt.execute("http://api.openweathermap.org/data/2.5/weather?q=" + loc + "&APPID=da8b6bf394c4127545ffbf42c4f14d8d");

    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            String res = "";
            HttpURLConnection connection = null;
            URL url;
            try {
                url = new URL(urls[0]);
                connection = (HttpURLConnection) url.openConnection();

                InputStream inp = connection.getInputStream();
                InputStreamReader isr = new InputStreamReader(inp);

                int data = isr.read();

                while(data != -1) {

                    char curr = (char) data;
                    res+=curr;
                    data = isr.read();
                }
                return res;

            } catch (Exception e) {
                return "oh boy";
            }
           // return null;

        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if (result.equals("oh boy")) {
                Toast.makeText(getApplicationContext(), "City unavailable, try again", Toast.LENGTH_LONG);
                return;
            }


            try {

                JSONObject jo = new JSONObject(result);
                String info = jo.getString("weather");
           //     Log.i("weather: ", info);

                String main = "";
                String desc  ="";
                String weather = "";

                JSONArray jarr = new JSONArray(info);
                for (int i = 0; i < jarr.length(); i++) {
                    JSONObject job = jarr.getJSONObject(i);
                    main = job.getString("main");
                    desc = job.getString("description");

                    if (!main.isEmpty() && !desc.isEmpty()) {
                        weather = main + ": " + desc;
                        TextView fin = (TextView) findViewById(R.id.weather);
                        fin.setText(weather);
                    }

                }
                if (weather.isEmpty()) Toast.makeText(getApplicationContext(), "City unavailable, try again", Toast.LENGTH_LONG);

            } catch(JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        et = (EditText) findViewById(R.id.editText2);

    }
}
