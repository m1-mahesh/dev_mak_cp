package com.nikvay.drnitingroup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nikvay.drnitingroup.R;
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

        final StudentData singleItem = itemsList.get(i);

        holder.tvTitle.setText(singleItem.getName());
        holder.marksTxt.setText("Marks: "+singleItem.getMarksGain()+"/"+singleItem.getTotalMarks());

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