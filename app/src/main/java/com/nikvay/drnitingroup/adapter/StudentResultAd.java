package com.nikvay.drnitingroup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.TestResultActivity;
import com.nikvay.drnitingroup.modales.StudentData;

import java.util.ArrayList;

public class StudentResultAd extends RecyclerView.Adapter<StudentResultAd.SingleItemRowHolder> {

    private ArrayList<StudentData> itemsList;
    private Context mContext;

    public StudentResultAd(Context context, ArrayList<StudentData> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_result_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }
    String className = "";
    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final StudentData testData = itemsList.get(i);

        holder.tvTitle.setText(testData.getName());
        int gMarks = 0;
        if(testData.correctAnsCount>0 && TestResultActivity.testData.correctMarks>0){
            gMarks = (testData.correctAnsCount * TestResultActivity.testData.correctMarks);
        }
        if(testData.wrongAnsCount>0){
            int wCount = testData.wrongAnsCount;
            while(wCount!=0){
                gMarks -= TestResultActivity.testData.wrongMarks;
                wCount --;
            }
        }
        String totalMarks = ""+ (gMarks<10&&gMarks>0? "0"+gMarks:gMarks);
        holder.marksTxt.setText("Marks: "+totalMarks+"/"+testData.getTotalMarks());

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, marksTxt;

        protected ImageView itemImage;


        public SingleItemRowHolder(View view) {
            super(view);

            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.marksTxt = view.findViewById(R.id.resultText);
//

        }

    }

}