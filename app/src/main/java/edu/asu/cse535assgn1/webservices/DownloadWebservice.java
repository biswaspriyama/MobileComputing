package edu.asu.cse535assgn1.webservices;

import android.content.Context;
import android.os.AsyncTask;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Jithin Roy on 2/24/16.
 */
public class DownloadWebservice {

    String fileName;
    String absoluteDownloadPath;

    DownloadWebserviceInterface mInterface;

    public DownloadWebservice(String name, String pathName, DownloadWebserviceInterface interfaceClass) {
        this.fileName = name;
        mInterface = interfaceClass;
        this.absoluteDownloadPath = pathName + "_downloaded";
        System.out.println("File path = " + fileName + " " + absoluteDownloadPath);
    }

    public void startDownload() {
        new DownloadTask().execute();

    }

    private class DownloadTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... params) {

            HttpsURLConnection connection = null;
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // Not implemented
                }

                @Override
                public void checkServerTrusted(X509Certificate[] arg0, String arg1) throws CertificateException {
                    // Not implemented
                }
            } };

            try {
                SSLContext sc = SSLContext.getInstance("TLS");

                sc.init(null, trustAllCerts, new java.security.SecureRandom());

                HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            } catch (KeyManagementException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }

            InputStream input = null;
            OutputStream output = null;

            try {
                URL url = new URL("https://impact.asu.edu/Appenstance/" + DownloadWebservice.this.fileName);
                connection = (HttpsURLConnection) url.openConnection();
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    System.out.println("Server returned HTTP " + connection.getResponseCode()
                            + " " + connection.getResponseMessage());
                    return false;
                }

                // this will be useful to display download percentage
                // might be -1: server did not report the length
                int fileLength = connection.getContentLength();

                // download the file
                input = connection.getInputStream();
                output = new FileOutputStream(DownloadWebservice.this.absoluteDownloadPath);

                byte data[] = new byte[4096];
                long total = 0;
                int count;
                while ((count = input.read(data)) != -1) {
                    // allow canceling with back button
                    if (isCancelled()) {
                        input.close();
                        return false;
                    }
                    total += count;
//                    // publishing the progress....
//                    if (fileLength > 0) // only if total length is known
//                        publishProgress((int) (total * 100 / fileLength));
                    output.write(data, 0, count);
                }
                if (total > 0) {
                    return true;
                }
            } catch (Exception e) {
                return false;
            } finally {
                try {
                    if (output != null)
                        output.close();
                    if (input != null)
                        input.close();
                } catch (IOException ex) {
                    return false;
                }

                if (connection != null)
                    connection.disconnect();
            }
            return false;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean result) {
            super.onPostExecute(result);

            if (result) {
                mInterface.didComplete(DownloadWebservice.this.absoluteDownloadPath);
            } else {
                mInterface.didFail(DownloadWebservice.this.fileName);
            }
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }
    }

    public interface  DownloadWebserviceInterface {

        public void didComplete(String filePath);

        public void didFail(String filePath);
    }
}
