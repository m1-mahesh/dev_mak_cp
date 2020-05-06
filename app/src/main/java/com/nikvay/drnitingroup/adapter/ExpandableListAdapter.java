package com.nikvay.drnitingroup.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.nikvay.drnitingroup.FinalisePaperActivity;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.SelectQuestionsActivity;
import com.nikvay.drnitingroup.ViewPaperQuestions;
import com.nikvay.drnitingroup.modales.Question;
import com.nikvay.drnitingroup.utilities.Constant;

import java.util.ArrayList;

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    ArrayList<Question> questionArrayList = new ArrayList<>();
    boolean isAdmin;
    private Context _context;

    public ExpandableListAdapter(Context context, ArrayList<Question> questionArrayList, boolean isAdmin) {
        this._context = context;
        this.questionArrayList = questionArrayList;
        this.isAdmin = isAdmin;
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

        TextView txtListChild = convertView
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

        TextView lblListHeader = convertView
                .findViewById(R.id.lblListHeader);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(question.question);
        TextView marks = convertView.findViewById(R.id.lblMarks);
        marks.setText(question.getAnswerDescription());
        if (this.isAdmin) {
            ImageView editButton = convertView.findViewById(R.id.editButton);
            editButton.setVisibility(View.VISIBLE);
            editButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openPopupMenu(editButton, question);
                }
            });
        }
        return convertView;
    }
    void openPopupMenu(View view, Question question){
        PopupMenu popupMenu = new  PopupMenu(_context, view , Gravity.END);
        popupMenu.getMenuInflater().inflate(R.menu.paper_edit_menu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.editHeading:
                        Log.e("Hed", "");
                        FinalisePaperActivity.IS_UPDATE = true;
                        FinalisePaperActivity.paperData = question;
                        _context.startActivity(new Intent(_context, FinalisePaperActivity.class));
                        ((Activity) _context).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                        break;
                    case R.id.addQuestion:
                        SelectQuestionsActivity.INDEX = 0;
                        Constant.headQuestion = question;
                        Constant.paperData = ViewPaperQuestions.paperDta;
                        Constant.IS_PAPER = true;
                        Constant.ADD_Q_IN_PAPER = true;
                        SelectQuestionsActivity.apiData = ViewPaperQuestions.apiData;
                        ((Activity) _context).startActivityForResult(new Intent(_context, SelectQuestionsActivity.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
                        ((Activity) _context).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);

                        break;
                    case R.id.deleteQ:
                        Log.e("Del", "");
                        SelectQuestionsActivity.INDEX = 1;
                        Constant.headQuestion = question;
                        Constant.paperData = ViewPaperQuestions.paperDta;
                        Constant.IS_PAPER = true;
                        Constant.ADD_Q_IN_PAPER = false;
                        Constant.DELETE_Q_IN_PAPER = true;
                        SelectQuestionsActivity.apiData = ViewPaperQuestions.apiData;
                        ((Activity) _context).startActivityForResult(new Intent(_context, SelectQuestionsActivity.class), Constant.ACTIVITY_FINISH_REQUEST_CODE);
                        ((Activity) _context).overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                        break;
                }
                return false;
            }
        });
        popupMenu.show();
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
