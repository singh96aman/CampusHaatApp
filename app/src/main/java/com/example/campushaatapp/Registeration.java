package com.example.campushaatapp;

import android.app.Activity;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Locale;

public class Registeration extends AppCompatActivity {

    EditText eroom, elocality, ecity, estate, ecountry, ezip;
    Button bregister;
    private Spinner spinner1, spinner2, spinner3;
   com.example.campushaatapp.Address address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registeration);

        eroom = (EditText) findViewById(R.id.input_room);
        elocality = (EditText) findViewById(R.id.input_locality);
        //ecity = (EditText) findViewById(R.id.input_city);
        //estate = (EditText) findViewById(R.id.input_state);
        //ecountry = (EditText) findViewById(R.id.input_country);
        ezip = (EditText) findViewById(R.id.input_zipcode);
        bregister = (Button) findViewById(R.id.btn_register);
        addListenerOnSpinnerItemSelection();
        ezip.setText(getPostalCode(Coordinates.curLatitude,Coordinates.curLongitude));

        bregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!validate())
                    Toast.makeText(getApplicationContext(),"Invalid Credentials",Toast.LENGTH_LONG).show();
                else
                {Toast.makeText(getApplicationContext(),"I like to move it move it !",Toast.LENGTH_LONG).show();
                    new HttpAsyncTask().execute("http://ec2-35-154-15-217.ap-south-1.compute.amazonaws.com:8080/campushaatTestAPI/webapi/users/createAddress ");}
            }
        });

    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {


            return POST(urls[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        spinner1.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinner2 = (Spinner) findViewById(R.id.spinner1);
        spinner2.setOnItemSelectedListener(new CustomOnItemSelectedListener());
        spinner3 = (Spinner) findViewById(R.id.spinner1);
        spinner3.setOnItemSelectedListener(new CustomOnItemSelectedListener());
    }


    private boolean checkString(String pattern){
        boolean valid =true;
        for (char c : pattern.toCharArray()) {
            if (!Character.isLetterOrDigit(c)) {
                valid=false;
                break;
            }
        }
        return valid;
    }

    private boolean checkZip(String pattern){
        boolean valid = true;
        for(char c : pattern.toCharArray()){
            if(!Character.isDigit(c)){
                valid =false;
                break;
            }
        }
        return valid;
    }


    private boolean validate() {
        boolean valid = true;

        String room = eroom.getText().toString();
        String locality = elocality.getText().toString();
        //String city = ecity.getText().toString();
        //String state = estate.getText().toString();
        //String country = ecountry.getText().toString();
        String zip = ezip.getText().toString();



        if (room.isEmpty() || room.length() > 6 || !checkString(room))
        {
            eroom.setError("6 Aplhanumeric Characters permitted");
            valid=false;
        }
        if(locality.isEmpty() || locality.length() > 6|| !checkString((locality)))
        {
            elocality.setError("6 Alphanumeric Characters permitted");
            valid=false;
        }
        /*if(city.isEmpty() || !checkString(city))
        {
            ecity.setError("Enter Valid City");
            valid=false;
        }
        if(state.isEmpty() || !checkString(state))
        {
            estate.setError("Enter Valid State");
            valid=false;
        }
        if(country.isEmpty() || !checkString(country))
        {
            ecountry.setError("Enter Valid Country");
            valid=false;
        }
        */
        if(zip.isEmpty() || !checkZip(zip) || zip.length() > 8)
        {
            ezip.setError("Enter Valid Postal Code");
            valid=false;
        }

        return valid;
    }

    private String getAddress(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                android.location.Address address = addresses.get(0);
                result.append(address.getLocality()).append("\n");
                result.append(address.getAdminArea()).append("\n");
                result.append(address.getPostalCode()).append("\n");
                result.append(address.getSubLocality()).append("\n");
                result.append(address.getCountryName());
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    private String getPostalCode(double latitude, double longitude) {
        StringBuilder result = new StringBuilder();
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses.size() > 0) {
                android.location.Address address = addresses.get(0);
                result.append(address.getPostalCode()).append("\n");
            }
        } catch (IOException e) {
            Log.e("tag", e.getMessage());
        }

        return result.toString();
    }

    public String POST(String url){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.accumulate("room", eroom.getText().toString());
            jsonObject.accumulate("locality", elocality.getText().toString());
            jsonObject.accumulate("city", "1");
            jsonObject.accumulate("state","1");
            jsonObject.accumulate("zipCode",getPostalCode(Coordinates.curLatitude,Coordinates.curLongitude));
            jsonObject.accumulate("country","1");
            jsonObject.accumulate("longitude",""+Coordinates.curLongitude);
            jsonObject.accumulate("lattitude",""+Coordinates.curLatitude);
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

}
