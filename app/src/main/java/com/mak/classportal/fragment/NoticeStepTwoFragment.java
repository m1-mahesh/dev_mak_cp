package com.mak.classportal.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.mak.classportal.R;
import com.mak.classportal.SelectStudents;
import com.mak.classportal.modales.StudentClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NoticeStepTwoFragment extends Fragment {

    public Map<String, String> selectedDivisions = new HashMap<>();
    GridLayout divisionsGridLayout;
    Spinner classSpinner, divisionSpinner;
    CheckBox ifConditionCheckBox;
    TextView selectTopText;
    Button selectStudentButton;
    ArrayList<StudentClass> classes = new ArrayList<>();
    String selectedClass = "";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            classes = getArguments().getParcelableArrayList("classData");
        }
        View rootView = inflater.inflate(R.layout.activity_new_notice3, container,
                false);
        divisionsGridLayout = rootView.findViewById(R.id.divisionsLayout);
        classSpinner = rootView.findViewById(R.id.selectClass);
        divisionSpinner = rootView.findViewById(R.id.selectDivision);
        ifConditionCheckBox = rootView.findViewById(R.id.ifOneStudent);
        selectTopText = rootView.findViewById(R.id.selectTxt);
        selectStudentButton = rootView.findViewById(R.id.selectStudents);
        selectStudentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(getContext(), SelectStudents.class));
                getActivity().overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });
        ifConditionCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    selectTopText.setVisibility(View.GONE);
                    divisionsGridLayout.setVisibility(View.GONE);
                    divisionSpinner.setVisibility(View.VISIBLE);
                    selectStudentButton.setVisibility(View.VISIBLE);
                } else {
                    selectTopText.setVisibility(View.VISIBLE);
                    divisionsGridLayout.setVisibility(View.VISIBLE);
                    divisionSpinner.setVisibility(View.GONE);
                    selectStudentButton.setVisibility(View.GONE);
                }
            }
        });
        spinnerImplementation();
        return rootView;
    }

    void spinnerImplementation() {
        ArrayAdapter<StudentClass> adapter = new ArrayAdapter(getContext(), android.R.layout.simple_spinner_dropdown_item, classes);
        classSpinner.setAdapter(adapter);
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StudentClass sClass = (StudentClass) parent.getSelectedItem();
                selectedClass = sClass.getId();
                if (!selectedClass.equals("") && !ifConditionCheckBox.isChecked()) {
                    divisionsGridLayout.setVisibility(View.VISIBLE);
                    selectTopText.setVisibility(View.VISIBLE);
                } else {
                    divisionsGridLayout.setVisibility(View.GONE);
                    selectTopText.setVisibility(View.GONE);
                }
                divisionsGridLayout.removeAllViews();

                for (Map.Entry<String, String> entry : sClass.getDivisions().entrySet()) {
                    String key = entry.getKey();
                    String value = entry.getValue();
                    GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                    CheckBox checkBox = new CheckBox(getContext());
                    checkBox.setText(value);
                    params.setMargins(10, 10, 0, 0);
                    checkBox.setTypeface(ResourcesCompat.getFont(getContext(), R.font.proximanovaregular));
                    checkBox.setLayoutParams(params);
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (isChecked)
                                selectedDivisions.put(key, value);
                            else selectedDivisions.remove(key);

                        }
                    });
                    divisionsGridLayout.addView(checkBox);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

}
