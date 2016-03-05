package edu.asu.cse535assgn1.webservices;

import android.os.AsyncTask;

/**
 * Created by Jithin Roy on 2/24/16.
 */
public class DownloadWebservice {

    String downloadFilePath;

    public DownloadWebservice(String filePath) {
        this.downloadFilePath = filePath;
    }

    public void startDownload() {

    }

    public boolean isRunning()  {
        return false;
    }

    private class DownloadTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled(Void aVoid) {
            super.onCancelled(aVoid);
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }
    }

}
