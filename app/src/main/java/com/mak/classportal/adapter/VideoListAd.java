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
import com.mak.classportal.video_player.AndExoPlayerView;

import java.util.ArrayList;

public class VideoListAd extends RecyclerView.Adapter<VideoListAd.SingleItemRowHolder> {

    private ArrayList<NoticeData> itemsList;
    private Context mContext;
    public static OnClassClick onClassClick;
    public static String menuId = "";

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
        if (i==0)
            holder.andExoPlayerView.setSource(TEST_URL_MP4);

    }

    @Override
    public int getItemCount() {
        return (null != itemsList ? itemsList.size() : 0);
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected TextView devisionText;
        protected View hrView;
        protected AndExoPlayerView andExoPlayerView;

        public SingleItemRowHolder(View view) {
            super(view);

            andExoPlayerView = view.findViewById(R.id.andExoPlayerView);
        }

    }

}