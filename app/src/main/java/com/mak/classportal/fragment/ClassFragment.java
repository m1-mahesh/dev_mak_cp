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
import com.mak.classportal.adapter.ClassListAdapter;
import com.mak.classportal.adapter.SectionAdapter;
import com.mak.classportal.adapter.SliderAdapter;
import com.mak.classportal.modales.HomeMenu;
import com.mak.classportal.modales.SectionDataModel;
import com.mak.classportal.modales.StudentClass;
import com.mak.classportal.utilities.OnMenuClickListener;
import com.mak.sidemenu.interfaces.ScreenShotable;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class ClassFragment extends Fragment implements ScreenShotable {

    private View containerView;
    protected ImageView mImageView;
    protected int res;
    private Bitmap bitmap;
    public static OnMenuClickListener menuClickListener;
    ArrayList<StudentClass> allClassData;

    public static ClassFragment newInstance(int resId) {
        ClassFragment contentFragment = new ClassFragment();
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
        allClassData = new ArrayList<>();
        StudentClass aClass=new StudentClass();
        aClass.setName("1st");
        allClassData.add(aClass);
        allClassData.add(aClass);
        allClassData.add(aClass);
        allClassData.add(aClass);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_classes, container, false);
        RecyclerView classList = rootView.findViewById(R.id.classList);

        classList.setHasFixedSize(true);

        ClassListAdapter adapter1 = new ClassListAdapter(getContext(), this,allClassData);

        classList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));

        classList.setAdapter(adapter1);

        return rootView;
    }

    @Override
    public void takeScreenShot() {

    }

    @Override
    public Bitmap getBitmap() {
        return bitmap;
    }
}

