package com.mak.classportal.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.fragment.ContentFragment;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.modales.StudentClass;
import com.mak.sidemenu.interfaces.ScreenShotable;

import java.util.ArrayList;

public class ClassListAdapter extends RecyclerView.Adapter<ClassListAdapter.SingleItemRowHolder> {

    private ArrayList<StudentClass> itemsList;
    private Context mContext;
    ScreenShotable screenShotable;

    public ClassListAdapter(Context context, ScreenShotable screenShotable, ArrayList<StudentClass> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
        this.screenShotable = screenShotable;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.class_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final StudentClass singleItem = itemsList.get(i);

        holder.tvTitle.setText(singleItem.getName());
        //holder.itemImage.setImageResource(singleItem.getResourceId());

        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentFragment.menuClickListener.onMenuClick(screenShotable, singleItem.getName());
            }
        });
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

        protected ImageView itemImage;


        public SingleItemRowHolder(View view) {
            super(view);

            this.tvTitle = (TextView) view.findViewById(R.id.tvTitle);
            this.itemImage = (ImageView) view.findViewById(R.id.itemImage);

        }

    }

}