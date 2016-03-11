package edu.asu.cse535assgn1.webservices;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import edu.asu.cse535assgn1.database.DatabaseManager;

/**
 * Created by Jithin Roy on 2/24/16.
 */
public class UploadWebservice {

    private static String TAG = "UploadWebservice";
    private String uploadFilePath;

    public UploadWebservice(String filePath) {
        this.uploadFilePath = filePath;
    }

    public void startUpload() {
        Log.i(TAG, "Start db upload");
        UploadTask task = new UploadTask();
        task.execute(DatabaseManager.sharedInstance().databaseAbsolutePath());
    }

    public boolean isRunning()  {
        return false;
    }

    private class UploadTask extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {


            int count = params.length;
            Log.i(TAG, "Length = " + count + "name = " + params[0]);

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


            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024;

            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            InputStream input = null;
            DataOutputStream output = null;

            try {
                URL url = new URL("https://impact.asu.edu/Appenstance/UploadToServerGPS.php");

                String filePathName = params[0];
                String fileUploadPathName = params[0] + "_upload";
                copy(new File(filePathName), new File(fileUploadPathName));

                String fileName = DatabaseManager.sharedInstance().databaseName();
                connection = (HttpsURLConnection) url.openConnection();
                connection.setUseCaches(false);
                connection.setDoOutput(true);
                connection.setDoInput(true);

                connection.setRequestMethod("POST");
                connection.setRequestProperty("Connection", "Keep-Alive");
                connection.setRequestProperty("Cache-Control", "no-cache");
                connection.setRequestProperty("ENCTYPE", "multipart/form-data");
                connection.setRequestProperty("Content-Type", "multipart/form-data;boundary="+boundary);
                connection.setRequestProperty("uploaded_file", fileName);


                output = new DataOutputStream(connection.getOutputStream());

                output.writeBytes(twoHyphens + boundary + lineEnd);
                output.writeBytes("Content-Disposition: form-data; name='uploaded_file';fileName='" +fileName+"'" + lineEnd);
                output.writeBytes(lineEnd);

                Log.i(TAG, "DB file " + params[0]);


                FileInputStream fileInputStream = new FileInputStream(fileUploadPathName);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                System.out.println("File length " + bytesAvailable + "");

                while (bytesRead > 0) {
                    try {
                        output.write(buffer);
                        Log.i(TAG, "Bytes read!!");
                    } catch (OutOfMemoryError e) {
                        e.printStackTrace();
                    }
                    bytesAvailable = fileInputStream.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                }

                output.writeBytes(lineEnd);
                output.writeBytes(twoHyphens + boundary + twoHyphens
                        + lineEnd);
                output.flush();
                output.close();

                fileInputStream.close();

                File fileToDelete = new File(fileUploadPathName);
                fileToDelete.delete();

                // Responses from the server (code and message)
                int serverResponseCode = connection.getResponseCode();
                String serverResponseMessage = connection.getResponseMessage();
                System.out.println("Server Response Code " + " " + serverResponseCode);
                System.out.println("Server Response Message "+ serverResponseMessage);

            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        private void copy(File src, File dst) throws IOException {
            InputStream in = new FileInputStream(src);
            OutputStream out = new FileOutputStream(dst);

            // Transfer bytes from in to out
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }


}
