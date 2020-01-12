package com.mak.classportal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.SelectQuestionsActivity;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.utilities.Constant;

import java.util.ArrayList;

public class SubjectListAdapter extends RecyclerView.Adapter<SubjectListAdapter.SingleItemRowHolder> {

    public static String menuId = "";
    String className = "";
    private ArrayList<HomeMenu> itemsList;
    private Context mContext;

    public SubjectListAdapter(Context context, ArrayList<HomeMenu> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.subject_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final HomeMenu singleItem = itemsList.get(i);

        holder.tvTitle.setText(singleItem.getName());
        if (menuId.equals(Constant.BOOK)) {

        } else {
            holder.tvTitle.setTextSize(10);
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SelectQuestionsActivity.CLASS_ID = "";
                    SelectQuestionsActivity.SUBJECT_ID = singleItem.getName();
                    SelectQuestionsActivity.INDEX = 1;
                    mContext.startActivity(new Intent(mContext, SelectQuestionsActivity.class));
                    ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView devisionText;
        protected View hrView;
        protected LinearLayout divisionsView;


        public SingleItemRowHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tvTitle);

        }

    }

}