package com.mak.classportal.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.PlayVideoActivity;
import com.mak.classportal.R;
import com.mak.classportal.modales.NoticeData;

import java.util.ArrayList;

public class VideoListAd extends RecyclerView.Adapter<VideoListAd.SingleItemRowHolder> {

    private ArrayList<NoticeData> itemsList;
    private Context mContext;

    private String TEST_URL_MP4 = "https://sample-videos.com/video123/mp4/720/big_buck_bunny_720p_5mb.mp4";

    private String TEST_URL_HLS = "https://content.jwplatform.com/manifests/yp34SRmf.m3u8";

    private String TEST_URL_MP3 = "https://host2.rj-mw1.com/media/podcast/mp3-192/Tehranto-41.mp3";
    public VideoListAd(Context context, ArrayList<NoticeData> itemsList) {
        this.itemsList = itemsList;
        this.mContext = context;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.video_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);

        return mh;
    }
    String className = "";
    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, int i) {

        final NoticeData singleItem = itemsList.get(i);
        holder.tvTitle.setText(singleItem.getTitle());
        holder.playerPlaceHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayVideoActivity.videoData = singleItem;
                mContext.startActivity(new Intent(mContext, PlayVideoActivity.class));
                ((Activity) mContext).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });


    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView devisionText;
        protected View hrView;
        protected ImageView playerPlaceHolder;

        public SingleItemRowHolder(View view) {
            super(view);
            tvTitle = view.findViewById(R.id.titleTxt);
            playerPlaceHolder = view.findViewById(R.id.playerPlaceHolder);
        }

    }

}