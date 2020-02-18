package com.mak.classportal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.HomeWorkDetails;
import com.mak.classportal.R;
import com.mak.classportal.modales.NoticeData;
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.OnClassClick;

import java.util.ArrayList;

public class HomeworkAd extends RecyclerView.Adapter<HomeworkAd.SingleItemRowHolder> {

    private ArrayList<NoticeData> itemsList;
    private Context mContext;
    public static OnClassClick onClassClick;
    public static String menuId = "";

    public HomeworkAd(Context context, ArrayList<NoticeData> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.homework_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }
    String className = "";
    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final NoticeData singleItem = itemsList.get(i);

        holder.tvTitle.setText("HomeWork For Monday");
        holder.descriptionText.setText(singleItem.getDescription());
        holder.homeworkDate.setText(singleItem.getCreatedOn());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HomeWorkDetails.noticeData = singleItem;
                mContext.startActivity(new Intent(mContext, HomeWorkDetails.class));
                ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, homeworkDate;
        protected TextView descriptionText;
        protected View hrView;
        protected CardView cardView;


        public SingleItemRowHolder(View view) {
            super(view);
            this.descriptionText = view.findViewById(R.id.descriptionText);
            this.hrView = view.findViewById(R.id.hrView);
            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.homeworkDate = (TextView) view.findViewById(R.id.workDate);
            this.cardView = view.findViewById(R.id.cardView);
//            this.itemImage = (ImageView) view.findViewById(R.id.itemImage);

        }

    }

}