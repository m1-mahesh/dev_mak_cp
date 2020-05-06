package com.nikvay.drnitingroup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.modales.Question;
import com.nikvay.drnitingroup.modales.TestData;
import com.nikvay.drnitingroup.utilities.UserSession;

import java.util.ArrayList;

public class TestResults extends RecyclerView.Adapter<TestResults.SingleItemRowHolder> {

    boolean isAttemptedTest = false;
    String className = "";
    private ArrayList<Question> itemsList;
    private Context mContext;
    UserSession userSession;
    public static TestData testData;

    public TestResults(Context context, ArrayList<Question> itemsList, UserSession userSession, boolean isAttemptedTest) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.userSession = userSession;
        this.isAttemptedTest = isAttemptedTest;
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
        holder.tvTitle.setText(question.getQuestion());
        holder.marksTxt.setText("");
        holder.textDescription.setText(question.getAnswerDescription());
        if (question.getSelectedAns() != null && question.getSelectedAns().equals(question.getCorrectAns())) {
            holder.correctAnsView.setVisibility(View.GONE);
            holder.wrongAnsView.setVisibility(View.VISIBLE);
            holder.statusImg.setImageResource(R.drawable.ic_correct_ans_24dp);
            if (testData!=null){
                holder.marksTxt.setText("" + testData.correctMarks);
            }
        } else if (question.getSelectedAns() != null && !question.getSelectedAns().equals("null")) {
            holder.statusImg.setImageResource(R.drawable.ic_wrong_ans_24dp);
            holder.correctAnsView.setVisibility(View.VISIBLE);
            holder.wrongAnsView.setVisibility(View.VISIBLE);
            if (testData!=null){
                holder.marksTxt.setText("-" + testData.wrongMarks);
            }
        }else {
            holder.statusImg.setImageResource(R.drawable.ic_not_24dp);
            holder.correctAnsView.setVisibility(View.VISIBLE);
            holder.wrongAnsView.setVisibility(View.GONE);
            if (testData!=null){
                holder.marksTxt.setText("0");
            }
        }
        if (!isAttemptedTest) {

            if (question.icContainKey(question.getCorrectAns()))
                holder.correctAnsVTxt.setText(question.getOptionOfId(question.getCorrectAns()));
            else
                holder.correctAnsVTxt.setText("Unknown");
            if (question.getSelectedAns() == null)
                holder.yourAnsVTxt.setText("None");
            else
                holder.yourAnsVTxt.setText(question.getOptionOfId(question.getSelectedAns()));
        }else {
            if (question.getCorrectAns()!=null && !question.getCorrectAns().equals("null"))
                holder.correctAnsVTxt.setText(question.getCorrectAns());
            else
                holder.correctAnsVTxt.setText("Unknown");
            if (question.getSelectedAns() != null && question.getSelectedAns().equals("null"))
                holder.yourAnsVTxt.setText("None");
            else
                holder.yourAnsVTxt.setText(question.getSelectedAns());
        }


    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, marksTxt, textDescription;
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
            this.textDescription = view.findViewById(R.id.descriptionText);

        }

    }

}