package com.mak.classportal;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.mak.classportal.modales.NoticeData;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.FileUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

public class HomeWorkDetails extends AppCompatActivity {

    public static NoticeData noticeData;
    Toolbar toolbar;
    NetworkImageView homeworkAttachment;
    LinearLayout containerView;
    TextView descriptionText;
    ProgressBar progressBar;
    ImageLoader imageLoader;
    ImageView downloadIcon, downloadPdfDoc;
    RelativeLayout forImageViewLayout;
    CardView forDownloadView;
    String fileExt = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work_details);
        toolbar = findViewById(R.id.toolbar_id);
        toolbar.setTitle("Attachments");
        setSupportActionBar(toolbar);
        containerView = findViewById(R.id.containerView);
        descriptionText = findViewById(R.id.descriptionText);
        homeworkAttachment = findViewById(R.id.homeworkAttachment);
        progressBar = findViewById(R.id.progressBar);
        downloadIcon = findViewById(R.id.downloadIcon);
        downloadPdfDoc = findViewById(R.id.download);
        forImageViewLayout = findViewById(R.id.forImageView);
        forDownloadView = findViewById(R.id.forDownloadView);
        progressBar.setVisibility(View.GONE);
        if (noticeData != null && noticeData.getMediaUrl() != null && !noticeData.getMediaUrl().equals("")) {
            imageLoader = AppController.getInstance().getImageLoader();
            homeworkAttachment.setImageUrl(noticeData.getMediaUrl(), imageLoader);
        }
        if (noticeData != null) {
            downloadIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DownloadFileFromURL().execute(noticeData.getMediaUrl(), fileExt);
                }
            });
            downloadPdfDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new DownloadFileFromURL().execute(noticeData.getMediaUrl(), fileExt);
                }
            });
            descriptionText.setText(noticeData.getDescription());
            if (noticeData.getType()!=null){
                if (noticeData.getType().equalsIgnoreCase("2")||noticeData.getType().equalsIgnoreCase("3")){
                    if (noticeData.getType().equalsIgnoreCase("2"))
                        fileExt = "pdf";
                    else fileExt = "doc";
                    forDownloadView.setVisibility(View.VISIBLE);
                    forImageViewLayout.setVisibility(View.GONE);
                }else if (noticeData.getType().equalsIgnoreCase("1")){
                    fileExt = "png";
                    forDownloadView.setVisibility(View.GONE);
                    forImageViewLayout.setVisibility(View.VISIBLE);
                }else {
                    forDownloadView.setVisibility(View.GONE);
                    forImageViewLayout.setVisibility(View.GONE);
                }
            }else {
                forImageViewLayout.setVisibility(View.GONE);
                forDownloadView.setVisibility(View.GONE);
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);


    }

    ProgressDialog pDialog;
    class DownloadFileFromURL extends AsyncTask<String, String, String> {
        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(HomeWorkDetails.this);
            pDialog.setMax(100);
            pDialog.setMessage("Its loading....");
            pDialog.setTitle("Downloading");
            pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            pDialog.show();
            pDialog.setCancelable(false);
        }

        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... f_url) {
            int count;
            try {
                URL url = new URL(f_url[0]);
                String ext = f_url[1];
                URLConnection conection = url.openConnection();
                conection.connect();
                // this will be useful so that you can show a tipical 0-100%           progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                // Output stream
                OutputStream output = new FileOutputStream(path.getAbsolutePath()+"/downloadedfile."+ext);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }

        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            // setting progress percentage
            if (pDialog!=null)
                pDialog.setProgress(Integer.parseInt(progress[0]));
        }

        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after the file was downloaded
            if (pDialog!=null)
                pDialog.dismiss();

            // Displaying downloaded image into image view
            // Reading image path from sdcard

            try{
                String imagePath = Environment.getExternalStorageDirectory().toString() + "/downloadedfile."+fileExt;
                File file = new File(imagePath);
                FileUtils.openFile(HomeWorkDetails.this, file);
            }catch (Exception e){
                e.printStackTrace();
            }
            // setting downloaded into image view

        }

    }

}
