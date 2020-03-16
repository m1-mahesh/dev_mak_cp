package com.nikvay.drnitingroup.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nikvay.drnitingroup.AppController;
import com.nikvay.drnitingroup.PlayVideoActivity;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.modales.NoticeData;

import java.util.ArrayList;
import java.util.List;

public class SliderAdapter extends PagerAdapter {
    private Context context;
    private ArrayList<NoticeData> urls;
    ImageLoader imageLoader;
    public SliderAdapter(Context context, ArrayList<NoticeData> urls) {
        this.context = context;
        this.urls = urls;
    }

    @Override
    public int getCount() {
        return urls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_slider, null);
        imageLoader = AppController.getInstance().getImageLoader();
        NoticeData noticeData = urls.get(position);
        NetworkImageView networkImageView =  view.findViewById(R.id.sliderImage);
        if(!noticeData.getMediaUrl().equals("") && !noticeData.equals("null")) {
            networkImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    PlayVideoActivity.videoData = noticeData;
                    context.startActivity(new Intent(context, PlayVideoActivity.class));
                    ((Activity) context).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);

                }
            });
        }
        networkImageView.setImageUrl(noticeData.getDescription(), imageLoader);

        ViewPager viewPager = (ViewPager) container;
        viewPager.addView(view, 0);

        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ViewPager viewPager = (ViewPager) container;
        View view = (View) object;
        viewPager.removeView(view);
    }
}

