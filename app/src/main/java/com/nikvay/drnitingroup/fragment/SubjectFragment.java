package com.nikvay.drnitingroup.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.VideoChapterListActivity;
import com.nikvay.drnitingroup.modales.SubjectData;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class SubjectFragment extends Fragment {

    static String menuId = "";
    protected ImageView mImageView;
    protected int res;
    ArrayList<SubjectData> subjectData;
    private View containerView;
    private Bitmap bitmap;
    AppSingleTone appSingleTone;
    SharedPreferences sharedPreferences;
    UserSession userSession;
    RecyclerView subjectList;
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.container);
        appSingleTone = new AppSingleTone(getContext());
        sharedPreferences = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        subjectList = view.findViewById(R.id.subjectList);
        subjectList.setHasFixedSize(true);
        getSubjectChapters();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        subjectData = new ArrayList<>();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subject, container, false);

        return rootView;
    }
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    void showToast(String toastText){
        inflater = getActivity().getLayoutInflater();
        tostLayout = inflater.inflate(R.layout.toast_layout_file,
                (ViewGroup) getActivity().findViewById(R.id.toast_layout_root));
        customToast = tostLayout.findViewById(R.id.text);
        Toast toast = new Toast(getContext());
        customToast.setText(toastText);
        customToast.setTypeface(ResourcesCompat.getFont(getContext(), R.font.opensansregular));
        toast.setGravity(Gravity.NO_GRAVITY, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(tostLayout);
        toast.show();
    }

    public class SubjectAd extends RecyclerView.Adapter<SubjectAd.SingleItemRowHolder> {

        private ArrayList<SubjectData> itemsList;
        private Context mContext;

        AppSingleTone appSingleTone;
        public SubjectAd(Context context, ArrayList<SubjectData> itemsList) {
            this.itemsList = itemsList;
            this.mContext = context;
            appSingleTone = new AppSingleTone(mContext);
        }

        @Override
        public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subject_chapter_item, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);

            return mh;
        }
        @Override
        public void onBindViewHolder(SingleItemRowHolder holder, int i) {

            final SubjectData singleItem = itemsList.get(i);
            holder.tvTitle.setText(singleItem.getName());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    VideoChapterListActivity.subjectData = singleItem;
                    VideoChapterListActivity.IS_VIDEO = false;
                    mContext.startActivity(new Intent(mContext, VideoChapterListActivity.class));
                    ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });

        }


        @Override
        public int getItemCount() {
            return (null != itemsList ? itemsList.size() : 0);
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
    void prepareSubjectChapter(JSONArray apiResponse){
        try {
            subjectData.clear();

            for (int i = 0; i < apiResponse.length(); i++) {
                JSONObject classObj = apiResponse.getJSONObject(i);
                SubjectData aClass = new SubjectData();
                aClass.setId(classObj.getString("id"));
                aClass.setName(classObj.getString("subject_name"));
                ArrayList<SubjectData> chapterList = new ArrayList<>();
                for (int j =0; j< classObj.getJSONArray("chapter_list").length(); j++) {
                    JSONObject obj = classObj.getJSONArray("chapter_list").getJSONObject(j);
                    SubjectData chapter = new SubjectData();
                    chapter.setId(obj.getString("id"));
                    chapter.setName(obj.getString("chapter_name"));
                    chapterList.add(chapter);
                }
                aClass.setChaptersList(chapterList);
                Log.e("",aClass.id);
                subjectData.add(aClass);
            }
            SubjectAd adapter1 = new SubjectAd(getContext(), subjectData);
            subjectList.setLayoutManager(new LinearLayoutManager(getContext()));
            subjectList.setAdapter(adapter1);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void getSubjectChapters() {

        try {
            String url = appSingleTone.subjectChapterList;

            ExecuteAPI executeAPI = new ExecuteAPI(getContext(), url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", userSession.getAttribute("class_id"));
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.has("subject_list")) {
                            JSONArray object = result.getJSONArray("subject_list");
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

