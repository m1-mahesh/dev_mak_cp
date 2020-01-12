package com.mak.classportal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.modales.NoticeData;
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.OnClassClick;

import java.util.ArrayList;

public class NoticeListAd extends RecyclerView.Adapter<NoticeListAd.SingleItemRowHolder> {

    public static OnClassClick onClassClick;
    public static String menuId = "";
    String className = "";
    private ArrayList<NoticeData> itemsList;
    private Context mContext;

    public NoticeListAd(Context context, ArrayList<NoticeData> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.notice_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final NoticeData singleItem = itemsList.get(i);

        holder.tvTitle.setText("Notice For Holiday");
        if (menuId.equals(Constant.TAKE_TEST)) {
            holder.devisionText.setVisibility(View.GONE);
            holder.hrView.setVisibility(View.GONE);
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (onClassClick != null)
                        onClassClick.onClassClick(Constant.SELECT_SUBJECT);
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView devisionText;
        protected View hrView;


        public SingleItemRowHolder(View view) {
            super(view);
            this.devisionText = view.findViewById(R.id.divisionText);
            this.hrView = view.findViewById(R.id.hrView);
            this.tvTitle = view.findViewById(R.id.tvTitle);
//            this.itemImage = (ImageView) view.findViewById(R.id.itemImage);

        }

    }

}