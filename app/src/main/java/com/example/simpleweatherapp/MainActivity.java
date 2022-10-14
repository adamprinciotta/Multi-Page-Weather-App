package com.example.simpleweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getName();
    private Button reqButton;

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String url = "https://api.darksky.net/forecast/7711c2819f294564cb912e166a5bb983/";
    private String ZipURL = "https://www.zipcodeapi.com/rest/SniZuzQL5GZFXcykC97NaUkv30rxEu6Ko48lLquDpUMvcHIYhcLcNVRS5BXagbWU/info.json/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        reqButton = (Button) findViewById(R.id.sendRequest);

        reqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                getLocation(v);
            }
        });

    }

    private void getLocation(View v) {
        EditText editText = (EditText) findViewById(R.id.editText);
        String zip = editText.getText().toString();
        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);
        //String Request initialized
        JsonObjectRequest mStringRequest = new JsonObjectRequest(Request.Method.GET,
                ZipURL+zip+"/degrees", null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        String result = "";
                        JSONObject Location = null;
                        try {
                            result += response.getString("lat");
                            result += ",";
                            result += response.getString("lng");
                            String city = response.getString("city");
                            String state = response.getString("state");
                            getWeather(result, city, state);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG,"Error : " + error.toString());
                    }
                });


        mRequestQueue.add(mStringRequest);
    }

    private void getWeather(String LatLon, final String city, final String state) {

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);
        //String Request initialized
        JsonObjectRequest mStringRequest = new JsonObjectRequest(Request.Method.GET,
                url+LatLon, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        TextView cityState = (TextView) findViewById(R.id.cityState);
                        cityState.setText(city + ", " + state);
                        TextView textView = (TextView) findViewById(R.id.Weather);
                        textView.setText(temperature(response));
                    }
                },
                new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i(TAG,"Error : " + error.toString());
                        }
                });

        mRequestQueue.add(mStringRequest);
    }

    private String temperature(JSONObject response) {
        String result = "";
        JSONObject WeatherRes = null;
        try {
            WeatherRes = response.getJSONObject("currently");
            result += "Summary: " + response.getJSONObject("hourly").getString("summary") + "\n\n";
            result += "Temperature: " + WeatherRes.getString("temperature") + "°F";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
