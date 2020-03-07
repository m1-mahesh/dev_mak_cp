package com.mak.classportal.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.HomeWorkDetails;
import com.mak.classportal.R;
import com.mak.classportal.modales.NoticeData;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class NoticeListAd extends RecyclerView.Adapter<NoticeListAd.SingleItemRowHolder> {

    private ArrayList<NoticeData> itemsList;
    private Context mContext;
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    public NoticeListAd(Context context, ArrayList<NoticeData> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
        sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
        appSingleTone = new AppSingleTone(mContext);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notice_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final NoticeData notice = itemsList.get(i);

        holder.tvTitle.setText(notice.getTitle());
        holder.description.setText(notice.getDescription());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(notice.getCreatedOn());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            SimpleDateFormat formatter = new SimpleDateFormat("MMM");
            String month = formatter.format(date);
            formatter = new SimpleDateFormat("dd");
            String dateStr = formatter.format(date);

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    HomeWorkDetails.noticeData = notice;

                    mContext.startActivity(new Intent(mContext, HomeWorkDetails.class));
                    ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });
            if (userSession.isTeacher()) {
                holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showPopupMenu(notice.getId(), i);
                        return false;
                    }
                });
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.createdOnDateTxt.setText(notice.getCreatedOn());
        holder.noticeDate.setText("Created by");
        holder.createdByText.setText(notice.getCreatedBy());

    }
    void showPopupMenu(String id, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.AlertDialogStyle));
        builder.setTitle("Select option");
        final CharSequence[] items = {"Delete"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0) {
                    deleteItem(id, pos);
                }
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, noticeDate, createdOnDateTxt, createdByText;
        protected TextView description;
        protected View hrView;
        protected CardView cardView;


        public SingleItemRowHolder(View view) {
            super(view);
            this.description = view.findViewById(R.id.descriptionText);
            this.hrView = view.findViewById(R.id.hrView);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.noticeDate = view.findViewById(R.id.noticeDate);
            this.cardView = view.findViewById(R.id.cardView);
            this.createdOnDateTxt = view.findViewById(R.id.createdOnDateTxt);
            this.createdByText = view.findViewById(R.id.createdByText);
//            this.itemImage = (ImageView) view.findViewById(R.id.itemImage);

        }

    }
    public void deleteItem(String id, int pos) {

        try {
            String url = appSingleTone.deleteNotice;

            ExecuteAPI executeAPI = new ExecuteAPI(mContext, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("teacher_id", userSession.getAttribute("user_id"));
            executeAPI.addPostParam("notice_id", id);
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    try {
                        Log.d("Result", result.toString());
                        if (result.getInt("error_code") == 200) {
                            itemsList.remove(pos);
                            notifyItemRemoved(pos);
                        }
                    }catch (Exception e){
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