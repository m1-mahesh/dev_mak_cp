package com.mak.classportal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mak.classportal.modales.NoticeData;

public class HomeWorkDetails extends AppCompatActivity {

    Toolbar toolbar;
    public static NoticeData noticeData;
    ImageView homeworkAttachment;
    LinearLayout containerView;
    TextView descriptionText;
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
        if (noticeData!=null && noticeData.getMediaUrl()!=null&&!noticeData.getMediaUrl().equals("")) {
            Glide.with(containerView)
                    .load(noticeData.getMediaUrl())
                    .into(homeworkAttachment);
        }
        if (noticeData!=null)
            descriptionText.setText(noticeData.getDescription());
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
