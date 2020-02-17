package com.mak.classportal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.QuickTestResult;
import com.mak.classportal.R;
import com.mak.classportal.TestIntroActivity;
import com.mak.classportal.TestResultActivity;
import com.mak.classportal.modales.TestData;
import com.mak.classportal.utilities.Constant;
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
    UserSession userSession;
    private ArrayList<TestData> itemsList;
    private Context mContext;
    private boolean isStep = false;
    int tabIndex;

    public ScheduledTestsAdapter(Context context, ArrayList<TestData> itemsList, boolean isStep, UserSession userSession, int tabIndex) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.isStep = isStep;
        this.userSession = userSession;
        this.tabIndex = tabIndex;
    }


    @NonNull
    @Override
    public SingleItemRowHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (isStep) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.resulttest_list_item, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);
            return mh;
        } else {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.test_list_item, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);
            return mh;
        }

    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final TestData testData = itemsList.get(i);
        holder.testTitle.setText(testData.getTestTitle());
        if (tabIndex == Constant.TAB_INDEX_2)holder.tvTitle.setText("Attempted");
        else if(tabIndex == Constant.TAB_INDEX_1) holder.tvTitle.setText("Ongoing");
        else if(tabIndex == Constant.TAB_INDEX_0) holder.tvTitle.setText("Active");

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
            holder.testDate.setText(testData.getDuration() + " min *" + (testData.totalQuestions < 10 ? "0" + testData.totalQuestions : testData.totalQuestions) + " Questions");

            holder.className.setText(testData.getClassName());
            if (tabIndex==2 && userSession.isStudent()) {
                String totalMarks = ""+testData.getGainMarks(), noOfQ = ""+testData.getTotalMarks();
                if (testData.getGainMarks() < 10)
                    totalMarks = "0" + testData.getGainMarks();
                if (testData.getTotalMarks() < 10)
                    noOfQ = "0" + testData.getTotalMarks();

                holder.tvTitle.setText("Result: " + totalMarks + "/" + noOfQ);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (isStep) {

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userSession.isStudent()) {
                        QuickTestResult.TEST_ID = testData.getId();
                        QuickTestResult.isQuick = false;
                        mContext.startActivity(new Intent(mContext, QuickTestResult.class));
                    } else if(tabIndex == Constant.TAB_INDEX_2){
                        TestResultActivity.TEST_ID = testData.getId();
                        mContext.startActivity(new Intent(mContext, TestResultActivity.class));
                    }
                    ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });
        } else {

            holder.timeTxt.setText("Time: " + testData.getTestTime());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userSession.isStudent() && tabIndex == Constant.TAB_INDEX_0) {
                        TestIntroActivity.testData = testData;
                        mContext.startActivity(new Intent(mContext, TestIntroActivity.class));
                    } else if (userSession.isStudent() && tabIndex == Constant.TAB_INDEX_2) {
                        QuickTestResult.TEST_ID = testData.getId();
                        QuickTestResult.isQuick = false;
                        mContext.startActivity(new Intent(mContext, QuickTestResult.class));
                    }
                    ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return itemsList.size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, timeTxt, className;
        protected TextView monthText, dateText, testTitle, testDate, testExpiryTest;
        protected View hrView;
        protected CardView cardView;


        public SingleItemRowHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.monthText = view.findViewById(R.id.monthText);
            this.dateText = view.findViewById(R.id.dateText);
            this.cardView = view.findViewById(R.id.cardItem);
            this.className = view.findViewById(R.id.className);
            this.testTitle = view.findViewById(R.id.testTitle);
            this.testDate = view.findViewById(R.id.testDate);
            this.testExpiryTest = view.findViewById(R.id.testExpiry);
            this.timeTxt = view.findViewById(R.id.timeTxt);

        }

    }

}