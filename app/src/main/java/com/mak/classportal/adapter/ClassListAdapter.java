package com.mak.classportal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.TakeAttendance;
import com.mak.classportal.TestsList;
import com.mak.classportal.modales.StudentClass;
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.OnClassClick;

import java.util.ArrayList;
import java.util.Map;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.SingleItemRowHolder> {

    public static OnClassClick onClassClick;
    public static String menuId = "";
    String className = "";
    private ArrayList<StudentClass> itemsList;
    private Context mContext;

    public ClassListAdapter(Context context, ArrayList<StudentClass> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.class_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(final SingleItemRowHolder holder, int i) {

        final StudentClass singleItem = itemsList.get(i);

        holder.tvTitle.setText("Class: " + singleItem.getName());
        holder.divisionsView.removeAllViews();
        if (menuId.equals(Constant.TAKE_TEST) || menuId.equals(Constant.CASE)) {
            holder.divisionsView.setVisibility(View.GONE);
            holder.devisionText.setVisibility(View.GONE);
            holder.hrView.setVisibility(View.GONE);
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TestsList.CLASS_ID = singleItem.getId();
                    TestsList.CLASS_NAME = singleItem.getName();
                    mContext.startActivity(new Intent(mContext, TestsList.class));
                    ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
            });
        } else {
            for (Map.Entry<String, String> entry : singleItem.getDivisions().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                TextView textView = new TextView(mContext);
                textView.setPadding(5, 5, 5, 5);
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                textView.setTypeface(Typeface.create("serif-monospace", Typeface.NORMAL));
                params.setMarginStart(30);
                textView.setBackground(mContext.getResources().getDrawable(R.drawable.layout_border));
                textView.setText(""+value.charAt((value.length()-1)));
                textView.setTextSize(25);
                textView.setLayoutParams(params);
                className = value;
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        TakeAttendance.CLASS_ID = singleItem.getName();
                        TakeAttendance.DIVISION = className;
                        mContext.startActivity(new Intent(mContext, TakeAttendance.class));
                        ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);

                    }
                });
                holder.divisionsView.addView(textView);
            }
            for (int j = 0; j < singleItem.getDivisions().size(); j++) {

            }
        }

        //holder.itemImage.setImageResource(singleItem.getResourceId());

//        holder.itemImage.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                ContentFragment.menuClickListener.onMenuClick(screenShotable, singleItem.getName());
//            }
//        });
       /* Glide.with(mContext)
                .load(feedItem.getImageURL())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .error(R.drawable.bg)
                .into(feedListRowHolder.thumbView);*/
    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView devisionText;
        protected View hrView;
        protected LinearLayout divisionsView;


        public SingleItemRowHolder(View view) {
            super(view);
            this.devisionText = view.findViewById(R.id.divisionText);
            this.hrView = view.findViewById(R.id.hrView);
            this.tvTitle = view.findViewById(R.id.tvTitle);
//            this.itemImage = (ImageView) view.findViewById(R.id.itemImage);
            this.divisionsView = view.findViewById(R.id.divisionsView);

        }

    }

}