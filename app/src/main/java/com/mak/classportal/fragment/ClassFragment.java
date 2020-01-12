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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mak.classportal.R;
import com.mak.classportal.adapter.ClassListAdapter;
import com.mak.classportal.modales.StudentClass;
import com.mak.classportal.utilities.Constant;

import java.util.ArrayList;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class ClassFragment extends Fragment  {

    private View containerView;
    protected ImageView mImageView;
    protected int res;
    private Bitmap bitmap;
    public static String menuId = "";
    ArrayList<StudentClass> allClassData;

    public static ClassFragment newInstance(String menuID) {
        ClassFragment contentFragment = new ClassFragment();
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
        StudentClass aClass=new StudentClass();
        aClass.setName("1st");

        StudentClass aClass1=new StudentClass();
        aClass1.setName("2nd");
        StudentClass aClass2=new StudentClass();
        aClass2.setName("3rd");
        StudentClass aClass3=new StudentClass();
        aClass3.setName("4th");
        StudentClass aClass4=new StudentClass();
        aClass4.setName("5th");
        StudentClass aClass5=new StudentClass();
        aClass5.setName("6th");
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
        View rootView = inflater.inflate(R.layout.fragment_classes, container, false);
        RecyclerView classList = rootView.findViewById(R.id.classList);

        classList.setHasFixedSize(true);
        ClassListAdapter adapter1 = new ClassListAdapter(getContext(),allClassData);
        if (this.menuId.equals(Constant.TAKE_TEST)||this.menuId.equals(Constant.CASE)){
            ClassListAdapter.menuId = this.menuId;
            classList.setLayoutManager(new GridLayoutManager(getContext(), 2));

        }else{
            ClassListAdapter.menuId = this.menuId;
            classList.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        }


        classList.setAdapter(adapter1);

        return rootView;
    }

}

