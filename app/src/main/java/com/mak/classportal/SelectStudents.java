package com.mak.classportal;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.mak.classportal.adapter.StudentListAdapter;
import com.mak.classportal.modales.StudentData;

import java.util.ArrayList;
import java.util.HashMap;

public class SelectStudents extends AppCompatActivity {

    ArrayList<StudentData> students = new ArrayList<>();
    TextView topTitle;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    public static ArrayList<String> selectedStudents = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_students);
        topTitle = findViewById(R.id.tvTitle);
        topTitle.setText("Select Students");
        selectedStudents.clear();
        for (int i=101;i<125;i++){
            StudentData studentData = new StudentData();
            studentData.setName("Rakesh Mehata");
            studentData.setRollNumber(""+i);
            students.add(studentData);
        }

        RecyclerView studentList = findViewById(R.id.studentList);

        studentList.setHasFixedSize(true);

        StudentListAdapter adapter1 = new StudentListAdapter(this,students, false);

        studentList.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));

        studentList.setAdapter(adapter1);

    }
    public void saveStudents(View view){
        finish();
    }
}
