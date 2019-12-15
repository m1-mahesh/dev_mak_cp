package com.mak.classportal.fragment;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.adapter.SectionAdapter;
import com.mak.classportal.adapter.SliderAdapter;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.modales.SectionDataModel;
import com.mak.classportal.utilities.OnMenuClickListener;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;


import com.mak.sidemenu.interfaces.ScreenShotable;

import java.util.ArrayList;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class ContentFragment extends Fragment implements ScreenShotable {
    protected int res;
    private Bitmap bitmap;
    private View containerView;
    protected ImageView mImageView;
    ArrayList<SectionDataModel> allSampleData;
    public static OnMenuClickListener menuClickListener;
    public static ContentFragment newInstance(int resId) {
        ContentFragment contentFragment = new ContentFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(Integer.class.getName(), resId);
        contentFragment.setArguments(bundle);
        return contentFragment;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.containerView = view.findViewById(R.id.container);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        res = getArguments().getInt(Integer.class.getName());
        allSampleData = new ArrayList<SectionDataModel>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        SliderView sliderView = rootView.findViewById(R.id.imageSlider);

        SliderAdapter adapter = new SliderAdapter(getContext());

        sliderView.setSliderAdapter(adapter);

        sliderView.setIndicatorAnimation(IndicatorAnimations.WORM); //set indicator animation by using SliderLayout.IndicatorAnimations. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        sliderView.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderView.setAutoCycleDirection(SliderView.AUTO_CYCLE_DIRECTION_BACK_AND_FORTH);
        sliderView.setIndicatorSelectedColor(Color.WHITE);
        sliderView.setIndicatorUnselectedColor(Color.GRAY);
        sliderView.setScrollTimeInSec(4); //set scroll delay in seconds :
        sliderView.startAutoCycle();
        createDummyData();


        RecyclerView sectionList = rootView.findViewById(R.id.sectionList);

        sectionList.setHasFixedSize(true);

        SectionAdapter adapter1 = new SectionAdapter(getContext(), this,allSampleData);

        sectionList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        sectionList.setAdapter(adapter1);
        return rootView;
    }

    @Override
    public void takeScreenShot() {

    }
    public void createDummyData() {
        for (int i = 1; i <= 1; i++) {

            SectionDataModel dm = new SectionDataModel();

            dm.setHeaderTitle("Daily Activities ");

            ArrayList<HomeMenu> singleItem = new ArrayList<HomeMenu>();
            HomeMenu m=new HomeMenu();
            m.setName("Take Attendance");
            m.setResourceId(R.drawable.checklist);
            singleItem.add(m);
            HomeMenu m1=new HomeMenu();
            m1.setName("Take Test");
            m1.setResourceId(R.drawable.plan);
            singleItem.add(m1);
            HomeMenu m2=new HomeMenu();
            m2.setName("Test Result");
            m2.setResourceId(R.drawable.result);
            singleItem.add(m2);
            SectionDataModel dm1 = new SectionDataModel();
            dm.setAllItemsInSection(singleItem);
            dm1.setAllItemsInSection(singleItem);
            dm1.setHeaderTitle("Other");
            allSampleData.add(dm);
            allSampleData.add(dm1);

        }
    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }
}

