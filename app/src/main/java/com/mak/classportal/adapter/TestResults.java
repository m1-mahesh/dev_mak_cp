package com.mak.classportal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.TestIntroActivity;
import com.mak.classportal.TestResultActivity;
import com.mak.classportal.modales.Question;
import com.mak.classportal.modales.TestData;
import com.mak.classportal.utilities.OnClassClick;
import com.mak.classportal.utilities.UserSession;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TestResults extends RecyclerView.Adapter<TestResults.SingleItemRowHolder> {

    public static OnClassClick onClassClick;
    public static String menuId = "";
    String className = "";
    private ArrayList<Question> itemsList;
    private Context mContext;
    UserSession userSession;
    public TestResults(Context context, ArrayList<Question> itemsList, UserSession userSession) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.userSession = userSession;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

            Question testData = itemsList.get(i);
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.quick_result_item, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);

            return mh;

    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final Question question = itemsList.get(i);
        char selectedChar = (char)question.getSelectedAns();
        char actualAns = question.getCorrectAns().charAt(0);
        if (selectedChar ==  actualAns){
            holder.correctAnsView.setVisibility(View.GONE);
            holder.wrongAnsView.setVisibility(View.VISIBLE);
            holder.statusImg.setImageResource(R.drawable.ic_correct_ans_24dp);
        }else{
            holder.statusImg.setImageResource(R.drawable.ic_wrong_ans_24dp);
            holder.correctAnsView.setVisibility(View.VISIBLE);
            holder.wrongAnsView.setVisibility(View.VISIBLE);
        }
        holder.tvTitle.setText(question.getQuestion());
        holder.marksTxt.setText(""+question.getMarks());
        holder.correctAnsVTxt.setText(question.getCorrectAns());
        if (question.getSelectedAns() == -1)
            holder.yourAnsVTxt.setText("None");
        else
            holder.yourAnsVTxt.setText(""+selectedChar);



    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, marksTxt;
        protected TextView yourAnsVTxt, correctAnsVTxt;
        protected LinearLayout wrongAnsView, correctAnsView;
        protected ImageView statusImg;


        public SingleItemRowHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.yourAnsVTxt = view.findViewById(R.id.yourAnsV);
            this.correctAnsVTxt = view.findViewById(R.id.correctAns);
            this.wrongAnsView = view.findViewById(R.id.wrongAnsView);
            this.correctAnsView = view.findViewById(R.id.correctAnsView);
            this.marksTxt = view.findViewById(R.id.marksTxt);
            this.statusImg = view.findViewById(R.id.ansStatus);

        }

    }

}