package com.example.locationmap;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.MalformedParameterizedTypeException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class UrlReader {
    /*
        rechercher les parkings et retourner le resultat sous forme d'un string
     */

    public String read(String s) throws IOException{

        String data ="";
        InputStream input = null;
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(s);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            input = urlConnection.getInputStream();
            BufferedReader buf = new BufferedReader(new InputStreamReader(input));
            StringBuffer sb = new StringBuffer();
            String line="";
            while ((line = buf.readLine()) != null){
                sb.append(line);

            }
            data = sb.toString();
            buf.close();



        }catch(MalformedURLException e){
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            input.close();
            urlConnection.disconnect();
        }
        return data;
    }

}
