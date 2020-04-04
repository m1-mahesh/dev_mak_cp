package com.nikvay.drnitingroup;

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

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.FileUtils;
import com.nikvay.drnitingroup.utilities.InputStreamVolleyRequest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Map;

public class HomeWorkDetails extends AppCompatActivity {

    public static NoticeData noticeData;
    Toolbar toolbar;
    NetworkImageView homeworkAttachment;
    LinearLayout containerView;
    TextView descriptionText, titleTxt, fileTypeText;
    ProgressBar progressBar;
    ImageLoader imageLoader;
    ImageView downloadIcon, downloadPdfDoc;
    RelativeLayout forImageViewLayout;
    CardView forDownloadView;
    String fileExt = "";
    ProgressDialog pDialog;
    AppSingleTone appSingleTone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_work_details);
        toolbar = findViewById(R.id.toolbar_id);
        toolbar.setTitle("Attachments");
        setSupportActionBar(toolbar);
        appSingleTone = new AppSingleTone(this);
        containerView = findViewById(R.id.containerView);
        descriptionText = findViewById(R.id.descriptionText);
        titleTxt = findViewById(R.id.tvTitle);
        fileTypeText = findViewById(R.id.fileType);
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
//                    new DownloadFileFromURL().execute(noticeData.getMediaUrl(), fileExt);
                    appSingleTone.downloadFile(HomeWorkDetails.this, noticeData.getMediaUrl(), noticeData.getId()+"."+fileExt);
                }
            });
            downloadPdfDoc.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    appSingleTone.downloadFile(HomeWorkDetails.this, noticeData.getMediaUrl(), noticeData.getId()+"."+fileExt);
//                    new DownloadFileFromURL().execute(noticeData.getMediaUrl(), fileExt);
                }
            });
            descriptionText.setText(noticeData.getDescription());
            if (noticeData.getType() != null) {
                if (noticeData.getType().equalsIgnoreCase("2") || noticeData.getType().equalsIgnoreCase("3")) {
                    if (noticeData.getType().equalsIgnoreCase("2"))
                        fileExt = "pdf";
                    else fileExt = "doc";
                    forDownloadView.setVisibility(View.VISIBLE);
                    fileTypeText.setText("(" + fileExt + ")");
                    titleTxt.setText("Attachment");
                    forImageViewLayout.setVisibility(View.GONE);
                } else if (noticeData.getType().equalsIgnoreCase("1")) {
                    fileExt = "png";
                    forDownloadView.setVisibility(View.GONE);
                    forImageViewLayout.setVisibility(View.VISIBLE);
                } else {
                    forDownloadView.setVisibility(View.GONE);
                    forImageViewLayout.setVisibility(View.GONE);
                }
            } else {
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


}
