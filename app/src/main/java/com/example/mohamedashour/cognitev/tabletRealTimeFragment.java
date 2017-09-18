package com.example.mohamedashour.cognitev;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Mohamed Ashour on 16/09/2017.
 */
public class tabletRealTimeFragment extends android.support.v4.app.Fragment{

    private static final String CLIENT_ID = "T2YDCZEE1CLTLTBUR2KFB2ZSWVMEMCBGHKDZRH2ETYD5PJXY";
    private static final String CLIENT_SECRET = "0BFV55PTO5YXWOSXLKZW3W4W2VNONT4C1R1SS1YILLSBMASC";

    ArrayList<foursquareElements> myList;
    private double latitude, longitude;

    GPSTracker gps;

    ListView lv;

    nearbyPlacesAdapter nearByPlacesAdapter;

    ProgressBar prgressLoading;
    RelativeLayout loadingContainer;

    public static SharedPreferences sharedPreferences;
    public static SharedPreferences.Editor editor;
    public static SharedPreferences offData;
    public static SharedPreferences.Editor offEditor;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tablet_fragment_layout, container, false);

        lv = (ListView) view.findViewById(R.id.listview);
        prgressLoading = (ProgressBar) view.findViewById(R.id.progressLoading);
        loadingContainer = (RelativeLayout) view.findViewById(R.id.loadingContainer);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreferences.edit();
        offData = PreferenceManager.getDefaultSharedPreferences(getActivity());
        offEditor = offData.edit();

        gps = new GPSTracker(getActivity());
        //check if GPS enabled
        if (gps.canGetLocation()) {
            latitude = gps.getLatitude();
            longitude = gps.getLongitude();

        } else {
            gps.showSettingsAlert();
        }
        //Online
        if (isNetworkAvailable()){
            String load = sharedPreferences.getString("load", " ");
            if (!load.equals("first")){
                foursquareAsync ob = new foursquareAsync();
                ob.execute();
            }else {
                loadingContainer.setVisibility(View.GONE);
                foursquareAsync ob = new foursquareAsync();
                ob.execute();
            }
            //Offline
        }else {
            try {
                loadingContainer.setVisibility(View.GONE);
                Gson gson = new Gson();
                String json = offData.getString("offline", " ");
                if (!json.equals(" ")){
                    Type type = new TypeToken<ArrayList<foursquareElements>>() {}.getType();
                    ArrayList<foursquareElements> arrayList = gson.fromJson(json, type);
                    nearByPlacesAdapter = new nearbyPlacesAdapter(getActivity(), 0, arrayList);
                    lv.setAdapter(nearByPlacesAdapter);
                }
            }catch (Exception e){
                Log.e("errrrrrrrrr", e.getMessage());
            }
        }

        return view;
    }
    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    private class foursquareAsync extends AsyncTask<View, Void, String> {

        String myLink;
        ProgressBar progressBar;

        @Override
        protected String doInBackground(View... urls) {
            //make call to url
            myLink = makeCall("https://api.foursquare.com/v2/venues/search?client_id="
                    + CLIENT_ID
                    + "&client_secret="
                    + CLIENT_SECRET
                    + "&v=20130815&ll="
                    + String.valueOf(latitude)
                    + ","
                    + String.valueOf(longitude));
            return "";
        }

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String result) {
            loadingContainer.setVisibility(View.GONE);
            editor.putString("load", "first");
            editor.commit();

            if (myLink == null) {
                Toast.makeText(getActivity(), "Something went wrong !!", Toast.LENGTH_LONG).show();
                // we have an error to the call
            } else {
                // get near places
                myList = (ArrayList<foursquareElements>) getPlaces(myLink);
                if (myList.size() == 0){
                    Toast.makeText(getActivity(), "No data found !!", Toast.LENGTH_LONG).show();
                }
                //Save data for offline mode
                Gson gson = new Gson();
                String json = gson.toJson(myList);
                offEditor.putString("offline", json);
                offEditor.commit();

                nearByPlacesAdapter = new nearbyPlacesAdapter(getActivity(), 0, myList);
                lv.setAdapter(nearByPlacesAdapter);
            }
        }
    }

    public static String makeCall(String url) {

        StringBuffer buffer_string = new StringBuffer(url);
        String replyString = "";

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(buffer_string.toString());

        try {
            //responce of the execution of the url
            HttpResponse response = httpclient.execute(httpget);
            InputStream is = response.getEntity().getContent();

            //get data
            BufferedInputStream bis = new BufferedInputStream(is);
            ByteArrayBuffer baf = new ByteArrayBuffer(20);
            int current = 0;
            while ((current = bis.read()) != -1) {
                baf.append((byte) current);
            }
            //convert data to string
            replyString = new String(baf.toByteArray());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return replyString.trim();
    }

    private static ArrayList<foursquareElements> getPlaces(final String response) {

        ArrayList<foursquareElements> bigContainer = new ArrayList<foursquareElements>();
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("response")) {
                if (jsonObject.getJSONObject("response").has("venues")) {
                    JSONArray jsonArray = jsonObject.getJSONObject("response").getJSONArray("venues");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        foursquareElements poi = new foursquareElements();

                        try {
                            //Name
                            if (jsonArray.getJSONObject(i).has("name")) {
                                poi.setName(jsonArray.getJSONObject(i).getString("name"));
                                // address
                                if (jsonArray.getJSONObject(i).has("location")) {
                                    if (jsonArray.getJSONObject(i).getJSONObject("location").has("address")) {
                                        poi.setAddress(jsonArray.getJSONObject(i).getJSONObject("location").getString("address"));
                                        if (jsonArray.getJSONObject(i).getJSONObject("location").has("lat")) {
                                            poi.setLatitude(jsonArray.getJSONObject(i).getJSONObject("location").getString("lat"));
                                        }
                                        if (jsonArray.getJSONObject(i).getJSONObject("location").has("lng")) {
                                            poi.setLongtitude(jsonArray.getJSONObject(i).getJSONObject("location").getString("lng"));
                                        }
                                        //Photo
                                        if (jsonArray.getJSONObject(i).has("categories")) {
                                            if (jsonArray.getJSONObject(i).getJSONArray("categories").length() > 0) {
                                                if (jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).has("icon")) {
                                                    poi.setCategoryIcon(jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).getJSONObject("icon")
                                                                    .getString("prefix")
                                                                    + "bg_32"+ jsonArray.getJSONObject(i).getJSONArray("categories").getJSONObject(0).getJSONObject("icon")
                                                                    .getString("suffix")
                                                    );
                                                }
                                            }
                                        }
                                        bigContainer.add(poi);
                                    }
                                }
                            }
                        } catch (Exception e) {

                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bigContainer;

    }
}
