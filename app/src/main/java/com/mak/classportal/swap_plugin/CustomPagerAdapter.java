package com.mak.classportal.swap_plugin;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;

import com.mak.classportal.R;
import com.mak.classportal.modales.Question;

import java.util.ArrayList;

/**
 * Created by anupamchugh on 26/12/15.
 */
public class CustomPagerAdapter extends PagerAdapter {

    private Context mContext;
    public static ArrayList<Question> mData;
    public CustomPagerAdapter(Context context, ArrayList<Question> mData) {
        mContext = context;
        this.mData = mData;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        Question modelObject = mData.get(position);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.question_card, collection, false);

        TextView textViewCard = view.findViewById(R.id.textViewCard);
        Question question = mData.get(position);
        textViewCard.setText(question.getQuestion());
        final RadioButton one = view.findViewById(R.id.one);
        final RadioButton two = view.findViewById(R.id.two);
        final RadioButton three = view.findViewById(R.id.three);
        final RadioButton four = view.findViewById(R.id.four);
        if (question.getSelectedAns() == 0)
            one.setSelected(true);
        else if (question.getSelectedAns() == 1)
            two.setSelected(true);
        else if (question.getSelectedAns() == 2)
            three.setSelected(true);
        else if (question.getSelectedAns() == 3)
            four.setSelected(true);
        one.setText(question.getOptions().get(0));
        two.setText(question.getOptions().get(1));
        three.setText(question.getOptions().get(2));
        four.setText(question.getOptions().get(3));
        RadioGroup optionGroup = view.findViewById(R.id.optionView);
        optionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.one:
                        one.setBackgroundResource(R.drawable.redio_selected);
                        two.setBackgroundResource(R.drawable.layout_border);
                        three.setBackgroundResource(R.drawable.layout_border);
                        four.setBackgroundResource(R.drawable.layout_border);
                        question.setSelectedAns(65);
                        notifyDataSetChanged();
                        break;
                    case R.id.two:
                        one.setBackgroundResource(R.drawable.layout_border);
                        two.setBackgroundResource(R.drawable.redio_selected);
                        three.setBackgroundResource(R.drawable.layout_border);
                        four.setBackgroundResource(R.drawable.layout_border);
                        question.setSelectedAns(66);
                        notifyDataSetChanged();
                        break;
                    case R.id.three:
                        one.setBackgroundResource(R.drawable.layout_border);
                        two.setBackgroundResource(R.drawable.layout_border);
                        three.setBackgroundResource(R.drawable.redio_selected);
                        four.setBackgroundResource(R.drawable.layout_border);
                        question.setSelectedAns(67);
                        notifyDataSetChanged();
                        break;
                    case R.id.four:
                        one.setBackgroundResource(R.drawable.layout_border);
                        two.setBackgroundResource(R.drawable.layout_border);
                        three.setBackgroundResource(R.drawable.layout_border);
                        four.setBackgroundResource(R.drawable.redio_selected);
                        question.setSelectedAns(68);
                        notifyDataSetChanged();
                        break;
                }
            }
        });
        collection.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return mData.get(position).getQuestion();
    }

}
