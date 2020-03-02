package com.mak.classportal;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.adapter.VideoListAd;
import com.mak.classportal.modales.NoticeData;
import com.mak.classportal.modales.SubjectData;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoChapterListActivity extends AppCompatActivity {

    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    Typeface opensanssemibold;
    Calendar c = Calendar.getInstance();
    RecyclerView recyclerView;
    LinearLayout notFoundView;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    public static SubjectData subjectData;
    public static String CHAPTER_ID;
    public static boolean IS_VIDEO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.video_chapter_list);
        appSingleTone = new AppSingleTone(this);
        opensanssemibold = ResourcesCompat.getFont(this, R.font.opensanssemibold);
        sharedPreferences = getSharedPreferences("User", Context.MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        recyclerView = findViewById(R.id.attendanceView);
        notFoundView = findViewById(R.id.notFoundView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Chapters");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if (IS_VIDEO){
            getSupportActionBar().setTitle("Videos");
            getVideoList();
        }else {
            ChapterAd adapter1 = new ChapterAd(this);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(adapter1);
        }

    }

    void showToast(String toastText) {
        inflater = getLayoutInflater();
        tostLayout = inflater.inflate(R.layout.toast_layout_file,
                findViewById(R.id.toast_layout_root));
        customToast = tostLayout.findViewById(R.id.text);
        Toast toast = new Toast(getApplicationContext());
        customToast.setText(toastText);
        customToast.setTypeface(ResourcesCompat.getFont(this, R.font.opensansregular));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(tostLayout);
        toast.show();
    }
    public class ChapterAd extends RecyclerView.Adapter<ChapterAd.SingleItemRowHolder> {

        private Context mContext;
        ArrayList<SubjectData> chapters = new ArrayList<>();
        AppSingleTone appSingleTone;
        public ChapterAd(Context context) {
            this.mContext = context;
            appSingleTone = new AppSingleTone(mContext);
            chapters = subjectData.getChaptersList();
        }

        @Override
        public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subject_chapter_item, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);

            return mh;
        }
        @Override
        public void onBindViewHolder(SingleItemRowHolder holder, int i) {
            SubjectData chapterObj = chapters.get(i);
            holder.tvTitle.setText(chapterObj.getName());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoChapterListActivity.CHAPTER_ID = chapterObj.getId();
                    VideoChapterListActivity.IS_VIDEO = true;
                    mContext.startActivity(new Intent(mContext, VideoChapterListActivity.class));
                    ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });

        }


        @Override
        public int getItemCount() {
            return (null != chapters ? chapters.size() : 0);
        }

        public class SingleItemRowHolder extends RecyclerView.ViewHolder {

            protected TextView tvTitle;
            protected TextView devisionText;
            protected CardView cardView;
            protected ImageView downloadView;


            public SingleItemRowHolder(View view) {
                super(view);
                this.devisionText = view.findViewById(R.id.divisionText);
                this.cardView = view.findViewById(R.id.cardView);
                this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
                this.downloadView = view.findViewById(R.id.download);

            }

        }

    }
    ArrayList<NoticeData>  videosList = new ArrayList<>();
    void prepareSubjectChapter(JSONArray apiResponse){
        try {
            videosList.clear();

            for (int i = 0; i < apiResponse.length(); i++) {
                JSONObject classObj = apiResponse.getJSONObject(i);
                NoticeData aClass = new NoticeData();
                aClass.setId(classObj.getString("video_id"));
                aClass.setTitle(classObj.getString("title"));
                aClass.setType(classObj.getString("video_type"));
                aClass.setMediaUrl(classObj.getString("video_url"));
                videosList.add(aClass);
            }
            VideoListAd adapter1 = new VideoListAd(this, videosList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
            recyclerView.setAdapter(adapter1);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void getVideoList() {

        try {
            String url = appSingleTone.getVideos;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("student_id", userSession.getAttribute("user_id"));
            executeAPI.addPostParam("class_id", userSession.getAttribute("class_id"));
            executeAPI.addPostParam("division_id", userSession.getAttribute("division_id"));
            executeAPI.addPostParam("subject_id", subjectData.getId());
            executeAPI.addPostParam("chapter_id", CHAPTER_ID);
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.has("video_list")) {
                            JSONArray object = result.getJSONArray("video_list");
                            prepareSubjectChapter(object);
                        } else {
                            showToast("Something went wrong, Please try again later");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                    Log.d("Result", errorResponse.toString());
                }
            });
            executeAPI.showProcessBar(true);
            executeAPI.executeStringRequest(Request.Method.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
