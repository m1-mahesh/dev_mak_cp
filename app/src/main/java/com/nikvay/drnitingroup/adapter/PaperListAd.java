package com.nikvay.drnitingroup.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.SelectQuestionsActivity;
import com.nikvay.drnitingroup.ViewPaperQuestions;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONObject;

import java.util.ArrayList;

public class PaperListAd extends RecyclerView.Adapter<PaperListAd.SingleItemRowHolder> {

    private ArrayList<NoticeData> itemsList;
    private Context mContext;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    SharedPreferences sharedPreferences;
    UserSession userSession;
    AppSingleTone appSingleTone;
    public PaperListAd(Context context, ArrayList<NoticeData> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
        appSingleTone = new AppSingleTone(mContext);
        sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.paper_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);

        return mh;
    }
    String className = "";
    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final NoticeData singleItem = itemsList.get(i);
        holder.tvTitle.setText(singleItem.getTitle());
        holder.subjectName.setText("("+singleItem.getSubjectName()+")");
        holder.examDuration.setText(singleItem.examTimeHr+" Hr");
        holder.examDate.setText(singleItem.examDate);
        holder.createdByTxt.setText("Created By: "+singleItem.createdBy);
        holder.downloadView.setVisibility(View.GONE);
        if (userSession.getBoolean("isAdmin"))
            holder.downloadView.setVisibility(View.VISIBLE);

        holder.addQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectQuestionsActivity.selectedQ.clear();
                ViewPaperQuestions.apiData = singleItem;
                Constant.IS_PAPER = true;
                ((Activity)mContext).startActivity(new Intent(mContext, ViewPaperQuestions.class));
                ((Activity)mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
//                ((Activity)mContext).finish();
            }
        });
        holder.downloadView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getPaperData(singleItem.getId(), singleItem.getTitle());
            }
        });
        holder.viewQ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPaperQuestions.paperDta = singleItem;
                SelectQuestionsActivity.selectedQ.clear();
                ViewPaperQuestions.apiData = singleItem;
                Constant.IS_PAPER = true;
                ((Activity)mContext).startActivity(new Intent(mContext, ViewPaperQuestions.class));
                ((Activity)mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });
    }
    void showToast(String toastText){
        inflater = ((Activity)mContext).getLayoutInflater();
        tostLayout = inflater.inflate(R.layout.toast_layout_file,
                (ViewGroup) ((Activity)mContext).findViewById(R.id.toast_layout_root));
        customToast = tostLayout.findViewById(R.id.text);
        Toast toast = new Toast(mContext);
        customToast.setText(toastText);
        customToast.setTypeface(ResourcesCompat.getFont(mContext, R.font.opensansregular));
        toast.setGravity(Gravity.NO_GRAVITY, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(tostLayout);
        toast.show();
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, examDate, examDuration, subjectName;
        protected TextView devisionText, createdByTxt;
        protected View hrView;
        protected ImageView downloadView, addQ, viewQ;


        public SingleItemRowHolder(View view) {
            super(view);
            this.devisionText = view.findViewById(R.id.divisionText);
            this.hrView = view.findViewById(R.id.hrView);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.downloadView = view.findViewById(R.id.download);
            this.examDate = view.findViewById(R.id.examDate);
            this.examDuration = view.findViewById(R.id.examDuration);
            this.subjectName = view.findViewById(R.id.subjectNameTxt);
            this.createdByTxt = view.findViewById(R.id.createdByText);
            this.addQ = view.findViewById(R.id.addQ);
            this.addQ.setVisibility(View.GONE);
            this.viewQ = view.findViewById(R.id.viewQ);
        }
    }
    public void getPaperData(String paperID, String paperTitle) {
        try {

            String url = appSingleTone.paperData;
            ExecuteAPI executeAPI = new ExecuteAPI(mContext, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("paper_id", paperID);
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    AppSingleTone.jsonObject = result;
                    appSingleTone.createPdf(paperTitle.replace(" ", "-")+".pdf");
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