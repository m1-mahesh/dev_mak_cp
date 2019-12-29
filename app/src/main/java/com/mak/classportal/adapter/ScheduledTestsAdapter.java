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
import com.mak.classportal.TestResultActivity;
import com.mak.classportal.modales.TestData;
import com.mak.classportal.utilities.OnClassClick;

import java.util.ArrayList;

public class ScheduledTestsAdapter extends RecyclerView.Adapter<ScheduledTestsAdapter.SingleItemRowHolder> {

    public static OnClassClick onClassClick;
    public static String menuId = "";
    String className = "";
    private ArrayList<TestData> itemsList;
    private Context mContext;
    private boolean isStep = false;

    public ScheduledTestsAdapter(Context context, ArrayList<TestData> itemsList, boolean isStep) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.isStep = isStep;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.test_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        if (isStep){
            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TestResultActivity.CLASS_ID = "10th";
                    TestResultActivity.DIVISION = "A";

                    ((Activity)mContext).startActivity(new Intent(mContext, TestResultActivity.class));
                    ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });
        }
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final TestData testItem = itemsList.get(i);


    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView devisionText;
        protected View hrView;


        public SingleItemRowHolder(View view) {
            super(view);
            this.devisionText = view.findViewById(R.id.divisionText);
            this.hrView = view.findViewById(R.id.hrView);
            this.tvTitle = view.findViewById(R.id.tvTitle);

        }

    }

}