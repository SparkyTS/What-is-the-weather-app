package com.sparkyts.whatistheweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    private static String cityName;
    protected class FetchData extends AsyncTask<String, Void, String> {


        @Override
        protected String doInBackground(String... urls) {
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                InputStream is = urlConnection.getInputStream();
                StringBuilder stringBuilder = new StringBuilder();
                int i;
                while((i=is.read())!=-1)
                    stringBuilder.append((char)i);
                return stringBuilder.toString();

            } catch (Exception e){
                e.printStackTrace();
            }
            return "Failed !";
        }
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = new JSONArray(jsonObject.getString("weather"));
                    JSONObject jsonMain = new JSONObject(jsonObject.getString("main"));

                    TextView weather =  findViewById(R.id.weather);
                    weather.setText("City : " +  cityName + "\n" +
                                    "Temp : " + String.format("%.1f°C",Float.parseFloat(jsonMain.getString("temp"))-273.15f) + "\n");

                    for(int i = 0 ; i < jsonArray.length(); i++){
                        JSONObject weatherInfo = new JSONObject(jsonArray.getString(i));
                        weather.append(weatherInfo.getString("main") + " : " + weatherInfo.getString("description"));
                    }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void findWeather(View view){
        TextView cityNameTextView = findViewById(R.id.cityName);
        cityName = cityNameTextView.getText().toString();
        cityNameTextView.setText("");

        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(cityNameTextView.getWindowToken(), 0);

        FetchData fetchData = new FetchData();
        try {
            fetchData.execute("http://api.openweathermap.org/data/2.5/weather?q="+ URLEncoder.encode(cityName,"UTF-8") + "&APPID=89d94c62c8033c6ecb0747a83d76c9d0");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}
