package com.nikvay.drnitingroup.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.nikvay.drnitingroup.NewTestActivity;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.modales.BoardData;

import java.util.ArrayList;

public class BoardAd extends RecyclerView.Adapter<BoardAd.SingleItemRowHolder> {

    private ArrayList<BoardData> itemsList;
    private Context mContext;
    public static String menuId = "";

    public BoardAd(Context context, ArrayList<BoardData> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.board_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewTestActivity.isTest = false;
                mContext.startActivity(new Intent(mContext, NewTestActivity.class));
                ((Activity)mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                ((Activity) mContext).finish();
            }
        });
        return mh;
    }
    String className = "";
    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final BoardData singleItem = itemsList.get(i);

        holder.tvTitle.setText(singleItem.getName());


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
            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);

        }

    }

}