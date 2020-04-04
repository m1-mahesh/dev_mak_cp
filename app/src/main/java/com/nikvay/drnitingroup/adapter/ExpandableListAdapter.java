package com.nikvay.drnitingroup.adapter;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.modales.Question;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context _context;
    ArrayList<Question>  questionArrayList = new ArrayList<>();

    public ExpandableListAdapter(Context context, ArrayList<Question> questionArrayList) {
        this._context = context;
        this.questionArrayList = questionArrayList;
    }

    @Override
    public Object getChild(int groupPosition, int childPosititon) {
        return this.questionArrayList.get(groupPosition).sectionData.get(childPosititon);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        final Question question = (Question) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_item, null);
        }

        TextView txtListChild = (TextView) convertView
                .findViewById(R.id.lblListItem);

        Log.e("Que", question.question);
        txtListChild.setText(question.question);
        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.questionArrayList.get(groupPosition).sectionData.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.questionArrayList.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this.questionArrayList.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        Question question = (Question) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this._context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.expandable_list_header, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(question.question);
        TextView marks = convertView.findViewById(R.id.lblMarks);
        marks.setText(question.getAnswerDescription());
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
