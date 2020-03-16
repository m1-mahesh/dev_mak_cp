package com.nikvay.drnitingroup.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.modales.StudentData;

import java.util.ArrayList;

public class StudentAttendanceTabAd extends RecyclerView.Adapter<StudentAttendanceTabAd.SingleItemRowHolder> {

    String className = "";
    private ArrayList<StudentData> itemsList;
    private Context mContext;

    public StudentAttendanceTabAd(Context context, ArrayList<StudentData> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;

    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final StudentData singleItem = itemsList.get(i);
        holder.tvTitle.setText(singleItem.getName() + " (" + singleItem.getId() + ")");
        holder.absentCheckBox.setClickable(false);
        holder.presentCheckBox.setClickable(false);
        if (singleItem.getAttendanceStatus() == 1){
            holder.presentCheckBox.setChecked(true);
        }else holder.absentCheckBox.setChecked(true);

        holder.checkLayout.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                Log.e("Checked ", "" + checkedId);


            }
        });

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected RadioGroup checkLayout;
        protected CheckBox studentName;
        protected RadioButton presentCheckBox, absentCheckBox;

        protected ImageView itemImage;


        public SingleItemRowHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.presentCheckBox = view.findViewById(R.id.presentButton);
            this.absentCheckBox = view.findViewById(R.id.absentButton);
            this.checkLayout = view.findViewById(R.id.checkLayout);

        }

    }

}