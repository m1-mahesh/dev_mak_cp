package com.mak.classportal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.NetworkImageView;
import com.mak.classportal.R;
import com.mak.classportal.RootActivity;
import com.mak.classportal.TestsList;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.utilities.UserSession;

import java.util.ArrayList;
import java.util.List;

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.ViewHolder> {
    List<HomeMenu> popularCatList = new ArrayList<>();
    Context mContext;
    private int selectedItem = -1;
    UserSession userSession;

    public MenuAdapter(Context mContext, List<HomeMenu> list, UserSession userSession) {
        this.popularCatList = list;
        this.mContext = mContext;
        this.userSession = userSession;

    }

    public void onClick(View view) {

    }

    public void clear() {
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.menu_list, parent, false);
        return new ViewHolder(itemView);
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        HomeMenu homeMenu =  popularCatList.get(position);
        holder.cat_layout.setVisibility(View.VISIBLE);
        holder.List_name.setText(homeMenu.getName());
        holder.cat_image.setImageResource(homeMenu.getResourceId());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (homeMenu.getMenuId() == 5) {
                    TestsList.CLASS_ID = userSession.getAttribute("class_id");
                    TestsList.CLASS_NAME = userSession.getAttribute("class_name");
                    TestsList.DIVISION_ID = userSession.getAttribute("division_id");
                    ((Activity) mContext).startActivity(new Intent(mContext, TestsList.class));
                }else {
                    RootActivity.defaultMenu = homeMenu.getMenuId();
                    ((Activity) mContext).startActivity(new Intent(mContext, RootActivity.class));
                }
                ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);

            }
        });

    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public int getItemCount() {
        return popularCatList.size();
    }

    public HomeMenu getItem(int i) {
        return popularCatList.get(i);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView List_name;

        public ImageView cat_image;
        RelativeLayout cat_layout;

        public ViewHolder(View view) {
            super(view);
            List_name = view.findViewById(R.id.list_name1);
            cat_image = view.findViewById(R.id.cat_image);
            cat_layout = view.findViewById(R.id.cat_layout);
        }
    }
}


