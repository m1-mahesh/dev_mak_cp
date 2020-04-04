package com.nikvay.drnitingroup.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.nikvay.drnitingroup.NewPaperActivity;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.modales.NoticeData;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.FileUtils;

import java.util.ArrayList;

public class InstructionsAd extends RecyclerView.Adapter<InstructionsAd.SingleItemRowHolder> {

    private ArrayList<NoticeData> itemsList;
    private Context mContext;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;

    AppSingleTone appSingleTone;
    public InstructionsAd(Context context, ArrayList<NoticeData> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
        appSingleTone = new AppSingleTone(mContext);
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.instruction_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showToast("Downloading...");
//                appSingleTone.createPdf(FileUtils.getAppPath(mContext) + "paper.pdf");
            }
        });
        return mh;
    }
    String className = "";
    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final NoticeData singleItem = itemsList.get(i);
//        holder.tvTitle.setText(singleItem.getTitle());
        holder.checkBox.setText(singleItem.getTitle());
        holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                NewPaperActivity.selectedInstructions.put(singleItem.getId(), isChecked);
            }
        });
    }
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

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected CheckBox checkBox;
        public SingleItemRowHolder(View view) {
            super(view);
            this.tvTitle = (TextView) view.findViewById(R.id.instructionText);
            this.checkBox = view.findViewById(R.id.checkIn);

        }

    }

}