package com.example.vmac.WatBot;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


//서버 통신 클래스
public class ServerComms {




    public void requestAllLocations(){
        String postReq = new String();
        POSTEncoder pe = new POSTEncoder();
        pe.addDataSet("Request Type", "Virtual Command");
        pe.addDataSet("Command","ask location all");
        postReq = pe.encode();
        this.sendPOST(postReq, "Virtual Command");
    }

    public void requestSomeoneLocations(String s){
        String postReq = new String();
        POSTEncoder pe = new POSTEncoder();
        pe.addDataSet("Request Type", "Virtual Command");
        pe.addDataSet("Command","ask location "+s);
        postReq = pe.encode();
        this.sendPOST(postReq, "Virtual Command");
    }

    public void requestAllTasks(){
        String postReq = new String();
        POSTEncoder pe = new POSTEncoder();
        pe.addDataSet("Request Type", "Virtual Command");
        pe.addDataSet("Command","ask task all");
        postReq = pe.encode();
        this.sendPOST(postReq, "Virtual Command");
    }


    public void deleteTask(int n){
        String postReq = new String();
        POSTEncoder pe = new POSTEncoder();
        pe.addDataSet("Request Type", "Virtual Command");
        pe.addDataSet("Command","update task delete "+n);
        postReq = pe.encode();
        this.sendPOST(postReq, "Virtual Command");
    }

    public void addTask(String task){


        String postReq = new String();
        POSTEncoder pe = new POSTEncoder();
        pe.addDataSet("Request Type", "Virtual Command");
        pe.addDataSet("Command","update task add "+task);
        postReq = pe.encode();
        this.sendPOST(postReq, "Virtual Command");
    }

    private static final int MAX_RETRIES = 5, RETRY_INTERVAL_MILLISEC=3000;



    public ServerComms(Context c){
        this.c=c;
    }

    Context c;



    private URL getURL(){
        try {
            return new URL("http://52.78.30.220:8080/");
        } catch (MalformedURLException e) {
            Log2.log(e);
            return null;
        }
    }






    public void sendGET(String requestType) {
        Log.d("Familink", "GETting from " + getURL());
        DataRetriever dr = new DataRetriever(this, 1);
        dr.setRequestType(requestType);
        dr.execute(getURL());
    }

    public void onGETReturn(String data, String requestType, int tries) {
        if (data == null) {

            if (tries >= MAX_RETRIES) {
                Log.e("Familink", "GET failed after "+MAX_RETRIES+" tries.");
                Toast.makeText(this.c, "5번의 서버 연결 시도가 모두 실패하였습니다.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Log.d("Familink", "Null returned to GET request. Retrying. (try " + tries + ")");
                Toast.makeText(this.c, "서버 연결에 실패하였습니다. 재시도합니다.", Toast.LENGTH_SHORT).show();
            }

            final String requestTypeF=requestType;
            final int triesF=tries;
            final ServerComms scF=this;

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    DataRetriever dr = new DataRetriever(scF, triesF + 1);
                    dr.setRequestType(requestTypeF);
                    dr.execute(getURL());
                }
            }, RETRY_INTERVAL_MILLISEC);



        } else {
            Log.d("Familink", "GET returned. | Request type:" + requestType+" | Data(newline stripped, full data on FamilinkHTML): "+data.replace("\n",""));

        }
    }


    public void sendPOST(String s, String requestType) {
        Log.d("FamiLink", "Sending POST to " + getURL() + " msg: " + s);
        DataSender ds = new DataSender(this, s, requestType, 1);
        ds.setRequestType(requestType);
        ds.execute(getURL());
    }


    public void onPOSTReturn(String data, String origParams, String requestType, int tries) {
        if (data == null) {
            if (tries >= MAX_RETRIES) {
                Log.e("Familink", "POST failed after "+MAX_RETRIES+" tries.");
                Toast.makeText(this.c, "5번의 서버 연결 시도가 모두 실패하였습니다.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Log.d("Familink", "Null returned to POST request. Retrying. (try "+tries+")");
                Toast.makeText(this.c, "서버 연결에 실패하였습니다. 재시도합니다.", Toast.LENGTH_SHORT).show();
            }

            final String requestTypeF=requestType,origParamsF=origParams;
            final int triesF=tries;
            final ServerComms scF=this;

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    DataSender ds = new DataSender(scF, origParamsF, requestTypeF, triesF + 1);
                    ds.setRequestType(requestTypeF);
                    ds.execute(getURL());
                }
            }, RETRY_INTERVAL_MILLISEC);


        } else {
            Log.d("Familink", "POST returned. | Request type:" + requestType+" | Data(newline stripped, full data on FamilinkHTML): "+data.replace("\n",""));
            Log.v("FamilinkHTML", "Data Returned: " + data);

        }
    }


    private class DataRetriever extends AsyncTask<URL, Void, String> {
        String requestType;
        ServerComms sc;
        int tries = 0;

        public DataRetriever(ServerComms sc, int tries) {

            super();
            this.sc = sc;
            this.tries = tries;
            Log.d("Familink", "DataRetriever initialized. try " + tries);
        }

        public void setRequestType(String s) {
            this.requestType = s;
        }

        protected String doInBackground(URL... urls) {
            try {

                URL url = urls[0];
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestProperty("Connection", "close");
                urlConnection.getInputStream();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                StringBuilder sb = new StringBuilder();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }

                urlConnection.disconnect();

                return sb.toString();
            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.w("Familink", "Error in GET(ServerComms>DataRetriever>doInBackground).\n" + errors.toString().substring(0, 300) + "...(omitted)");
                //Log.w("Familink", "Error in GET(ServerComms>DataRetriever>doInBackground).\n" + errors.toString());
                //Log.w("Familink", "Error in GET(ServerComms>DataRetriever>doInBackground).");
                return null;
            }
        }

        protected void onPostExecute(String result) {
            this.sc.onGETReturn(result, requestType, tries);
        }


    }

    private class DataSender extends AsyncTask<URL, Void, String> {


        ServerComms sc;
        String params;
        String requestType;
        String origParams;
        int tries;

        public DataSender(ServerComms sc, String params, String requestType, int tries) {
            this.params = params;
            this.sc = sc;
            this.origParams = params;
            this.tries = tries;
            Log.d("Familink", "DataSender initialized. try " + tries);
        }

        public void setRequestType(String s) {
            this.requestType = s;
        }

        @Override
        protected String doInBackground(URL... urls) {
            //Code from http://www.xyzws.com/javafaq/how-to-use-httpurlconnection-post-data-to-web-server/139
            URL url = urls[0];
            HttpURLConnection connection = null;

            try {


                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("Connection", "close");
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length", "" +
                        Integer.toString(params.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(params);
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                return response.toString();

            } catch (Exception e) {

                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.w("Familink", "Error in POST(ServerComms>DataSender>doInBackground).\n" + errors.toString().substring(0, 300) + "...(omitted)");
                //Log.w("Familink", "Error in POST(ServerComms>DataSender>doInBackground).\n"+errors.toString());
                //Log.w("Familink", "Error in POST(ServerComms>DataSender>doInBackground).");
                return null;

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }


        @Override
        protected void onPostExecute(String res) {
            this.sc.onPOSTReturn(res, origParams, requestType, tries);
        }
    }

}

class POSTEncoder {
    ArrayList<String> keys, values;
    public POSTEncoder(){
        keys=new ArrayList<String>();
        values=new ArrayList<String>();
    }

    public void addDataSet(String key, String value){
        keys.add(key);
        values.add(value);
    }
    public String encode(){

        String res=new String();
        for (int i=0;i<keys.size();i++){
            if (i!=0) res=res+"&";
            try {
                res = res + URLEncoder.encode(keys.get(i), "UTF-8") + "=" + URLEncoder.encode(values.get(i), "UTF-8");
            }catch(UnsupportedEncodingException e){
                Log.wtf("Familink", "What the fuck");
            }

        }
        return res;
    }
}
/*

//서버 통신 클래스
public class ServerComms {




    public void requestAllLocations(){
        String postReq = new String();
        POSTEncoder pe = new POSTEncoder();
        pe.addDataSet("Request Type", "Request Location");
        postReq = pe.encode();
        this.sendPOST(postReq, "Delete Me");
    }

    public void requestSomeoneLocations(String name){

    }

    public void requestAllTasks(){

    }

    public void requestMyTasks(){

    }

    public void deleteTask(int n){

    }

    public void addTask(String task){

    }

    private static final int MAX_RETRIES = 5, RETRY_INTERVAL_MILLISEC=3000;



    public ServerComms(Context c){
        this.c=c;
    }

    Context c;



    private URL getURL(){
        try {
            return new URL("http://52.78.30.220:8080/");
        } catch (MalformedURLException e) {
            Log2.log(e);
            return null;
        }
    }






    public void sendGET(String requestType) {
        Log.d("Familink", "GETting from " + getURL());
        DataRetriever dr = new DataRetriever(this, 1);
        dr.setRequestType(requestType);
        dr.execute(getURL());
    }

    public void onGETReturn(String data, String requestType, int tries) {
        if (data == null) {

            if (tries >= MAX_RETRIES) {
                Log.e("Familink", "GET failed after "+MAX_RETRIES+" tries.");
                Toast.makeText(this.c, "5번의 서버 연결 시도가 모두 실패하였습니다.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Log.d("Familink", "Null returned to GET request. Retrying. (try " + tries + ")");
                Toast.makeText(this.c, "서버 연결에 실패하였습니다. 재시도합니다.", Toast.LENGTH_SHORT).show();
            }

            final String requestTypeF=requestType;
            final int triesF=tries;
            final ServerComms scF=this;

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    DataRetriever dr = new DataRetriever(scF, triesF + 1);
                    dr.setRequestType(requestTypeF);
                    dr.execute(getURL());
                }
            }, RETRY_INTERVAL_MILLISEC);



        } else {
            Log.d("Familink", "GET returned. | Request type:" + requestType+" | Data(newline stripped, full data on FamilinkHTML): "+data.replace("\n",""));

        }
    }


    public void sendPOST(String s, String requestType) {
        Log.d("FamiLink", "Sending POST to " + getURL() + " msg: " + s);
        DataSender ds = new DataSender(this, s, requestType, 1);
        ds.setRequestType(requestType);
        ds.execute(getURL());
    }


    public void onPOSTReturn(String data, String origParams, String requestType, int tries) {
        if (data == null) {
            if (tries >= MAX_RETRIES) {
                Log.e("Familink", "POST failed after "+MAX_RETRIES+" tries.");
                Toast.makeText(this.c, "5번의 서버 연결 시도가 모두 실패하였습니다.", Toast.LENGTH_SHORT).show();
                return;
            } else {
                Log.d("Familink", "Null returned to POST request. Retrying. (try "+tries+")");
                Toast.makeText(this.c, "서버 연결에 실패하였습니다. 재시도합니다.", Toast.LENGTH_SHORT).show();
            }

            final String requestTypeF=requestType,origParamsF=origParams;
            final int triesF=tries;
            final ServerComms scF=this;

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    DataSender ds = new DataSender(scF, origParamsF, requestTypeF, triesF + 1);
                    ds.setRequestType(requestTypeF);
                    ds.execute(getURL());
                }
            }, RETRY_INTERVAL_MILLISEC);


        } else {
            Log.d("Familink", "POST returned. | Request type:" + requestType+" | Data(newline stripped, full data on FamilinkHTML): "+data.replace("\n",""));
            Log.v("FamilinkHTML", "Data Returned: " + data);

        }
    }


    private class DataRetriever extends AsyncTask<URL, Void, String> {
        String requestType;
        ServerComms sc;
        int tries = 0;

        public DataRetriever(ServerComms sc, int tries) {

            super();
            this.sc = sc;
            this.tries = tries;
            Log.d("Familink", "DataRetriever initialized. try " + tries);
        }

        public void setRequestType(String s) {
            this.requestType = s;
        }

        protected String doInBackground(URL... urls) {
            try {

                URL url = urls[0];
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.setReadTimeout(5000);
                urlConnection.setRequestProperty("Connection", "close");
                urlConnection.getInputStream();
                InputStream in = new BufferedInputStream(urlConnection.getInputStream());
                StringBuilder sb = new StringBuilder();
                String line;
                BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }

                urlConnection.disconnect();

                return sb.toString();
            } catch (Exception e) {
                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.w("Familink", "Error in GET(ServerComms>DataRetriever>doInBackground).\n" + errors.toString().substring(0, 300) + "...(omitted)");
                //Log.w("Familink", "Error in GET(ServerComms>DataRetriever>doInBackground).\n" + errors.toString());
                //Log.w("Familink", "Error in GET(ServerComms>DataRetriever>doInBackground).");
                return null;
            }
        }

        protected void onPostExecute(String result) {
            this.sc.onGETReturn(result, requestType, tries);
        }


    }

    private class DataSender extends AsyncTask<URL, Void, String> {


        ServerComms sc;
        String params;
        String requestType;
        String origParams;
        int tries;

        public DataSender(ServerComms sc, String params, String requestType, int tries) {
            this.params = params;
            this.sc = sc;
            this.origParams = params;
            this.tries = tries;
            Log.d("Familink", "DataSender initialized. try " + tries);
        }

        public void setRequestType(String s) {
            this.requestType = s;
        }

        @Override
        protected String doInBackground(URL... urls) {
            //Code from http://www.xyzws.com/javafaq/how-to-use-httpurlconnection-post-data-to-web-server/139
            URL url = urls[0];
            HttpURLConnection connection = null;

            try {


                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);
                connection.setRequestProperty("Connection", "close");
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type",
                        "application/x-www-form-urlencoded");

                connection.setRequestProperty("Content-Length", "" +
                        Integer.toString(params.getBytes().length));
                connection.setRequestProperty("Content-Language", "en-US");

                connection.setUseCaches(false);
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(
                        connection.getOutputStream());
                wr.writeBytes(params);
                wr.flush();
                wr.close();

                //Get Response
                InputStream is = connection.getInputStream();
                BufferedReader rd = new BufferedReader(new InputStreamReader(is));
                String line;
                StringBuffer response = new StringBuffer();
                while ((line = rd.readLine()) != null) {
                    response.append(line);
                    response.append('\r');
                }
                rd.close();
                return response.toString();

            } catch (Exception e) {

                StringWriter errors = new StringWriter();
                e.printStackTrace(new PrintWriter(errors));
                Log.w("Familink", "Error in POST(ServerComms>DataSender>doInBackground).\n" + errors.toString().substring(0, 300) + "...(omitted)");
                //Log.w("Familink", "Error in POST(ServerComms>DataSender>doInBackground).\n"+errors.toString());
                //Log.w("Familink", "Error in POST(ServerComms>DataSender>doInBackground).");
                return null;

            } finally {

                if (connection != null) {
                    connection.disconnect();
                }
            }
        }


        @Override
        protected void onPostExecute(String res) {
            this.sc.onPOSTReturn(res, origParams, requestType, tries);
        }
    }

}

class POSTEncoder {
    ArrayList<String> keys, values;
    public POSTEncoder(){
        keys=new ArrayList<String>();
        values=new ArrayList<String>();
    }

    public void addDataSet(String key, String value){
        keys.add(key);
        values.add(value);
    }
    public String encode(){

        String res=new String();
        for (int i=0;i<keys.size();i++){
            if (i!=0) res=res+"&";
            try {
                res = res + URLEncoder.encode(keys.get(i), "UTF-8") + "=" + URLEncoder.encode(values.get(i), "UTF-8");
            }catch(UnsupportedEncodingException e){
                Log.wtf("Familink", "What the fuck");
            }

        }
        return res;
    }
}*/

