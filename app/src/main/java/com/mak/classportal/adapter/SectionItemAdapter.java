package com.mak.classportal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.modales.HomeMenu;

import java.util.ArrayList;

public class SectionItemAdapter extends RecyclerView.Adapter<SectionItemAdapter.SingleItemRowHolder> {

    private ArrayList<HomeMenu> itemsList;
    private Context mContext;

    public SectionItemAdapter(Context context, ArrayList<HomeMenu> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.hom_menu_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final HomeMenu singleItem = itemsList.get(i);

        holder.tvTitle.setText(singleItem.getName());
        holder.itemImage.setImageResource(singleItem.getResourceId());

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;

        protected ImageView itemImage;


        public SingleItemRowHolder(View view) {
            super(view);

            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.itemImage = view.findViewById(R.id.itemImage);

        }

    }

}