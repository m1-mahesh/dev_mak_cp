package com.mak.classportal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.TestIntroActivity;
import com.mak.classportal.TestResultActivity;
import com.mak.classportal.modales.TestData;
import com.mak.classportal.utilities.OnClassClick;
import com.mak.classportal.utilities.UserSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduledTestsAdapter extends RecyclerView.Adapter<ScheduledTestsAdapter.SingleItemRowHolder> {

    public static OnClassClick onClassClick;
    public static String menuId = "";
    String className = "";
    private ArrayList<TestData> itemsList;
    private Context mContext;
    private boolean isStep = false;
    private boolean isAttempted = false;
    UserSession userSession;
    public ScheduledTestsAdapter(Context context, ArrayList<TestData> itemsList, boolean isStep, UserSession userSession, boolean isAttempted) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.isStep = isStep;
        this.userSession = userSession;
        this.isAttempted = isAttempted;
    }


    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isStep) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.resulttest_list_item, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);
            return mh;
        }else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_list_item, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);
            return mh;
        }

    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final TestData testData = itemsList.get(i);
        if (isStep){
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TestResultActivity.CLASS_ID = "10th";
                    TestResultActivity.DIVISION = "A";

                    ((Activity)mContext).startActivity(new Intent(mContext, TestResultActivity.class));
                    ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });
        }else {
            holder.testTitle.setText(testData.getTestTitle());

            Date date = null;
            try {
                date = new SimpleDateFormat("yyyy-MM-dd").parse(testData.getTestDate());
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                calendar.add(Calendar.DAY_OF_WEEK, 1);

                SimpleDateFormat formatter = new SimpleDateFormat("MMM");
                String month = formatter.format(date);
                holder.monthText.setText(month.toUpperCase());
                formatter = new SimpleDateFormat("dd");
                holder.dateText.setText(formatter.format(date));
                formatter = new SimpleDateFormat("dd-MMM");
                holder.testExpiryTest.setText("Expire On " + formatter.format(calendar.getTime()));
                holder.testDate.setText(testData.getDuration() + " min *60 Questions");
                holder.timeTxt.setText("Time: " + testData.getTestTime());

            } catch (ParseException e) {
                e.printStackTrace();
            }

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userSession.isStudent() && !isAttempted) {
                        TestIntroActivity.testData = testData;
                        ((Activity) mContext).startActivity(new Intent(mContext, TestIntroActivity.class));
                        ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                    } else {
                        TestResultActivity.CLASS_ID = testData.getTestTitle();
                        TestResultActivity.DIVISION = "A";
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, timeTxt;
        protected TextView monthText, dateText, testTitle, testDate, testExpiryTest;
        protected View hrView;
        protected CardView cardView;


        public SingleItemRowHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.monthText = view.findViewById(R.id.monthText);
            this.dateText = view.findViewById(R.id.dateText);
            this.cardView = view.findViewById(R.id.cardItem);

            this.testTitle = view.findViewById(R.id.testTitle);
            this.testDate = view.findViewById(R.id.testDate);
            this.testExpiryTest = view.findViewById(R.id.testExpiry);
            this.timeTxt = view.findViewById(R.id.timeTxt);

        }

    }

}