package com.mak.classportal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.SelectQuestionsActivity;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.utilities.Constant;
import com.mak.sidemenu.interfaces.ScreenShotable;

import java.util.ArrayList;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.SingleItemRowHolder> {

    private ArrayList<HomeMenu> itemsList;
    private Context mContext;
    ScreenShotable screenShotable;
    public static String menuId = "";
    public QuestionListAdapter(Context context, ArrayList<HomeMenu> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }
    public QuestionListAdapter(Context context, ScreenShotable screenShotable, ArrayList<HomeMenu> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.screenShotable = screenShotable;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }
    String className = "";
    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final HomeMenu singleItem = itemsList.get(i);

        holder.tvTitle.setText(singleItem.getName());
        holder.tvTitle.setTextSize(15);
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected CheckBox tvTitle;
        protected TextView devisionText;
        protected View hrView;
        protected LinearLayout divisionsView;


        public SingleItemRowHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tvTitle);

        }

    }

}