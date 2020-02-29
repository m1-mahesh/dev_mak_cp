package com.mak.classportal;

import android.os.Bundle;
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

public class HomeWorkDetails extends AppCompatActivity {

    public static NoticeData noticeData;
    Toolbar toolbar;
    NetworkImageView homeworkAttachment;
    LinearLayout containerView;
    TextView descriptionText;
    ProgressBar progressBar;
    ImageLoader imageLoader;
    ImageView downloadIcon;
    RelativeLayout forImageViewLayout;
    CardView forDownloadView;

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
        forImageViewLayout = findViewById(R.id.forImageView);
        forDownloadView = findViewById(R.id.forDownloadView);
        progressBar.setVisibility(View.GONE);
        if (noticeData != null && noticeData.getMediaUrl() != null && !noticeData.getMediaUrl().equals("")) {
            imageLoader = AppController.getInstance().getImageLoader();
            homeworkAttachment.setImageUrl(noticeData.getMediaUrl(), imageLoader);
        }
        if (noticeData != null) {
            descriptionText.setText(noticeData.getDescription());
            if (noticeData.getType()!=null){
                if (noticeData.getType().equalsIgnoreCase("pdf")||noticeData.getType().equalsIgnoreCase("doc")){
                    forDownloadView.setVisibility(View.VISIBLE);
                    forImageViewLayout.setVisibility(View.GONE);
                }else if (noticeData.getType().equalsIgnoreCase("image")){
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
}
