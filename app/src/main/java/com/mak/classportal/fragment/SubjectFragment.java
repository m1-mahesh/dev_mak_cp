package com.mak.classportal.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.adapter.SubjectListAdapter;
import com.mak.classportal.modales.HomeMenu;

import java.util.ArrayList;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class SubjectFragment extends Fragment {

    static String menuId = "";
    protected ImageView mImageView;
    protected int res;
    ArrayList<HomeMenu> allClassData;
    private View containerView;
    private Bitmap bitmap;

    public static SubjectFragment newInstance(String menuID) {
        SubjectFragment contentFragment = new SubjectFragment();
        menuId = menuID;
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
        allClassData = new ArrayList<>();
        HomeMenu aClass = new HomeMenu();
        aClass.setName("Marathi");

        HomeMenu aClass1 = new HomeMenu();
        aClass1.setName("English");
        HomeMenu aClass2 = new HomeMenu();
        aClass2.setName("Maths");
        HomeMenu aClass3 = new HomeMenu();
        aClass3.setName("History");
        HomeMenu aClass4 = new HomeMenu();
        aClass4.setName("Science");
        HomeMenu aClass5 = new HomeMenu();
        aClass5.setName("Hindi");
        allClassData.add(aClass);
        allClassData.add(aClass1);
        allClassData.add(aClass2);
        allClassData.add(aClass3);
        allClassData.add(aClass4);
        allClassData.add(aClass5);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_subject, container, false);
        RecyclerView subjectList = rootView.findViewById(R.id.subjectList);

        subjectList.setHasFixedSize(true);
        SubjectListAdapter.menuId = menuId;
        SubjectListAdapter adapter1 = new SubjectListAdapter(getContext(), allClassData);
        subjectList.setLayoutManager(new GridLayoutManager(getContext(), 2));


        subjectList.setAdapter(adapter1);

        return rootView;
    }

}

