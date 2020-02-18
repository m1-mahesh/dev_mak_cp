package com.mak.classportal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.modales.NoticeData;
import com.mak.classportal.utilities.OnClassClick;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

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

        final NoticeData notice = itemsList.get(i);

        holder.tvTitle.setText(notice.getTitle());
        holder.description.setText(notice.getDescription());
        try {
            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(notice.getCreatedOn());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            SimpleDateFormat formatter = new SimpleDateFormat("MMM");
            String month = formatter.format(date);
            formatter = new SimpleDateFormat("dd");
            String dateStr = formatter.format(date);



        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.noticeDate.setText(notice.getCreatedOn());

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle, noticeDate;
        protected TextView description;
        protected View hrView;


        public SingleItemRowHolder(View view) {
            super(view);
            this.description = view.findViewById(R.id.divisionsView);
            this.hrView = view.findViewById(R.id.hrView);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.noticeDate = view.findViewById(R.id.noticeDate);
//            this.itemImage = (ImageView) view.findViewById(R.id.itemImage);

        }

    }

}