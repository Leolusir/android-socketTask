package com.leo.sockettask;

import android.os.AsyncTask;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.Socket;

/**
 * Created by leo on 2015/4/17.
 */
public class SocketTask extends AsyncTask<Void, String, String>{
    private String ip;
    private int port;
    private int count;
    private int retrycount;
    private String params;
    private Callback callback;
    private String result;
    Socket socket;

    public SocketTask(String ip, int port, String params, Callback callback){
        this.ip = ip;
        this.port = port;
        this.params = params;
        count = 0;
        retrycount = 0;
        result = "0";
        this.callback = callback;
    }

    @Override
    protected void onCancelled() {
        super.onCancelled();
        if(socket != null && socket.isConnected()) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected void onProgressUpdate(String... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected String doInBackground(Void... params) {
        return Connection();
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(callback != null)
            callback.Oncallback(s);
    }

    private String Connection(){

        try {
            Thread.sleep(2000);
            socket = new Socket(ip, port);

            InputStream in = socket.getInputStream();
            OutputStream out = socket.getOutputStream();

            BufferedInputStream bin = new BufferedInputStream(in);
            BufferedOutputStream bout = new BufferedOutputStream(out);

            while (count < 60) {
                Thread.sleep(1000);

                bout.write(params.getBytes());
                bout.flush();

                byte[] buffer = new byte[20];
                bin.read(buffer);

                result = result + "+" + new String(buffer);

                count ++;
            }

            bout.close();
            bin.close();
            out.close();
            in.close();
            socket.close();

        } catch (ConnectException e){
            if(retrycount > 5){
                result = "failed";
            }else{
                retrycount ++;
                count = 0;
                Connection();
            }
        } catch (IOException e) {
            e.printStackTrace();
            if(retrycount > 5){
                result = "failed";
            }else{
                retrycount ++;
                count = 0;
                Connection();
            }
        } catch (Exception e) {
            e.printStackTrace();
            e.printStackTrace();
            if(retrycount > 5){
                result = "failed";
            }else{
                retrycount ++;
                count = 0;
                Connection();
            }
        }

        return result;
    }

    public interface Callback {
        void Oncallback(String result);
    }
}
