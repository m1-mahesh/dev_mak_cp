package com.nikvay.drnitingroup.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nikvay.drnitingroup.QuickTestResult;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.TestIntroActivity;
import com.nikvay.drnitingroup.TestResultActivity;
import com.nikvay.drnitingroup.ViewTestQuestions;
import com.nikvay.drnitingroup.modales.TestData;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.UserSession;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ScheduledTestsAdapter extends RecyclerView.Adapter<ScheduledTestsAdapter.SingleItemRowHolder> {

    UserSession userSession;
    private ArrayList<TestData> itemsList;
    private Context mContext;
    private boolean isStep = false;
    int tabIndex;
    AppSingleTone appSingleTone;

    public ScheduledTestsAdapter(Context context, ArrayList<TestData> itemsList, boolean isStep, UserSession userSession, int tabIndex) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.isStep = isStep;
        this.userSession = userSession;
        this.tabIndex = tabIndex;
        appSingleTone = new AppSingleTone(context);
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
            if(isActiveTest(testData, true)) {
//                holder.cardView.setBackgroundColor(Color.parseColor("#CCCCCC"));
                holder.testExpiryTest.setText("Expired On " + testData.getTestDate() + " " + testData.endTime);
            }else {
                holder.testExpiryTest.setText("Expire On " + testData.getTestDate() + " " + testData.endTime);
            }
            if(!isActiveTest(testData, false)) {
//                holder.cardView.setBackgroundColor(Color.parseColor("#CCCCCC"));
            }

            holder.testDate.setText(testData.getDuration() + " min *" + (testData.totalQuestions < 10 ? "0" + testData.totalQuestions : testData.totalQuestions) + " Questions");

            holder.className.setText(testData.getClassName());
            if (tabIndex==2 && userSession.isStudent()) {
                String totalMarks = ""+testData.getGainMarks(), noOfQ = ""+testData.getTotalMarks();
                int gMarks = 0;
                if (testData.getGainMarks() < 10)
                    totalMarks = "0" + testData.getGainMarks();
                if (testData.getTotalMarks() < 10)
                    noOfQ = "0" + testData.getTotalMarks();
                if(testData.correctAnsCount>0 && testData.correctMarks>0){
                     gMarks = (testData.correctAnsCount * testData.correctMarks);
                }
                if(testData.wrongAnsCount>0){
                    int wCount = testData.wrongAnsCount;
                    while(wCount!=0){
                        gMarks -= testData.wrongMarks;
                        wCount --;
                    }
                }
                totalMarks = ""+ (gMarks<10&&gMarks>0? "0"+gMarks:gMarks);
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
                        QuickTestResult.testData = testData;
                        mContext.startActivity(new Intent(mContext, QuickTestResult.class));
                    } else if(tabIndex == Constant.TAB_INDEX_2){
                        TestResultActivity.TEST_ID = testData.getId();
                        TestResultActivity.testData = testData;
                        mContext.startActivity(new Intent(mContext, TestResultActivity.class));
                    }else if(tabIndex != Constant.TAB_INDEX_2 && userSession.isTeacher()){
                        ViewTestQuestions.TEST_ID = testData.getId();
                        ViewTestQuestions.IS_VIEW_CREATED_TEST = true;
                        mContext.startActivity(new Intent(mContext, ViewTestQuestions.class));
                    }
                    ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });
        } else {
            holder.timeTxt.setText("Time: " + testData.getTestTime()+" - "+ testData.endTime);
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (userSession.isStudent() && tabIndex == Constant.TAB_INDEX_0) {
                        if(isActiveTest(testData, false)) {
                            TestIntroActivity.testData = testData;
                            mContext.startActivity(new Intent(mContext, TestIntroActivity.class));
                        }else if (isActiveTest(testData, true)) showToast("Test is Expired...");
                        else showToast("Test will start on time "+testData.getTestTime());

                    } else if (userSession.isStudent() && tabIndex == Constant.TAB_INDEX_2) {
                        QuickTestResult.TEST_ID = testData.getId();
                        QuickTestResult.isQuick = false;
                        QuickTestResult.testData = testData;
                        mContext.startActivity(new Intent(mContext, QuickTestResult.class));
                    }
                    ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });
        }
    }
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
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
    boolean isActiveTest(TestData testData, boolean isExpire){

        try{
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:a");
            Date date1 = null;
            Date date2 = null;
            try {
                date1 = dateFormat.parse(testData.getTestDate()+" "+testData.getTestTime());
                date2 = dateFormat.parse(testData.getTestDate()+" "+testData.endTime);
                Date todayDate = new Date();
                Log.e("T ", todayDate.toString());
                Log.e("S ", date1.toString());
                Log.e("E ", date2.toString());
                if (isExpire){
                    if (date1.before(todayDate) && todayDate.after(date2)) {
                        return true;
                    }else return false;
                }else {
                    if ((date1.after(todayDate) || todayDate.after(date1)) && todayDate.before(date2)) {
                        long diff = appSingleTone.getDiffInMin(todayDate, date1);
                        if (diff != Constant.UNDEFINED_TIME && diff <= 5) {
                            Log.e(testData.getTestTitle(), " Active");
                            return true;
                        }

                    }
                    Log.e("Diff", " " + appSingleTone.getDiffInMin(todayDate, date1));
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }

        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
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