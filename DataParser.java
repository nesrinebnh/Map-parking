package com.example.locationmap;

import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DataParser {

    private HashMap<String, String> getParking(JSONObject googlePlaceJson){
        /*  recuperer les cooredonnés d'un parking a partir du JSONObject
         et les mettre dans un hashMap */
        HashMap<String,String> googleParkingMap = new HashMap<>();
        String parkingName = "-NA-";
        String vicinity = "-NA-";
        String lat = "";
        String log ="";
        String ref ="";

        try {
            if((!googlePlaceJson.isNull("name"))&&(!googlePlaceJson.isNull("vicinity")) ) {

                parkingName = googlePlaceJson.getString("name");
                vicinity =  googlePlaceJson.getString("vicinity");
            }
            lat = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lat");
            log = googlePlaceJson.getJSONObject("geometry").getJSONObject("location").getString("lng");

            ref = googlePlaceJson.getString("reference");

            /* remplir le HqshMqp avec les coordonnés */
            googleParkingMap.put("parking_Name", parkingName);
            googleParkingMap.put("vicinity",vicinity);
            googleParkingMap.put("lat",lat);
            googleParkingMap.put("lng",log);
            googleParkingMap.put("reference",ref);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  googleParkingMap;

    }

    /* retourner la liste des parkings trouvés */
    public List<HashMap<String,String>> getListParking(String jsonData){

        JSONArray array  = null;
        String status = "";
        try {
            JSONObject obj = new JSONObject(jsonData);
            /* l'url contient les resultats sous forme d'un array son key c'est results */
            array = obj.getJSONArray("results");
            status = obj.getString("status");

            if(!status.equals("OK")){
                Object errorMsg ;
                errorMsg = obj.get("error_message");
                Log.i("error message",errorMsg.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();

        }
        Log.i("ParkingList",status);
        if( status.equals("OK") ) {
            int size = array.length();


            List<HashMap<String, String>> parkingList = new ArrayList<>();
            HashMap<String, String> parkingMap = null;
            for (int i = 0; i < size; i++) {
                try {
                    parkingMap = getParking((JSONObject) array.get(i));
                    parkingList.add(parkingMap);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
            return parkingList;
        } else {

            return null;
        }
    }

}
