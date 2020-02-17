package com.mak.classportal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.SelectStudents;
import com.mak.classportal.modales.StudentData;

import java.util.ArrayList;

public class StudentListAdapter extends RecyclerView.Adapter<StudentListAdapter.SingleItemRowHolder> {

    String className = "";
    private ArrayList<StudentData> itemsList;
    private Context mContext;
    private boolean isAttendance;
    public StudentListAdapter(Context context, ArrayList<StudentData> itemsList, boolean isAttendance) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.isAttendance = isAttendance;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        if (this.isAttendance) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_list_item, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);
            return mh;
        }else {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.student_list_item1, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);
            return mh;
        }
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final StudentData singleItem = itemsList.get(i);
        if (!isAttendance){
            holder.studentName.setText(singleItem.getName() + " (" + singleItem.getRollNumber() + ")");
            holder.studentName.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked)
                        SelectStudents.selectedStudents.add(singleItem.getId());
                    else SelectStudents.selectedStudents.remove(singleItem.getId());

                }
            });
        }else {
            holder.tvTitle.setText(singleItem.getName() + " (" + singleItem.getRollNumber() + ")");
        }

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected CheckBox studentName;

        protected ImageView itemImage;


        public SingleItemRowHolder(View view) {
            super(view);
            if (isAttendance)
                this.tvTitle = view.findViewById(R.id.tvTitle);
            else
                this.studentName = view.findViewById(R.id.tvTitle);

//

        }

    }

}