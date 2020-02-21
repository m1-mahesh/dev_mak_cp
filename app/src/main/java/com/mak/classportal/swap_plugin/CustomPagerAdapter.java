package com.mak.classportal.swap_plugin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.mak.classportal.NewTestActivity;
import com.mak.classportal.R;
import com.mak.classportal.modales.Question;

import java.util.ArrayList;
import java.util.Map;

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
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.question_card, collection, false);

        TextView textViewCard = view.findViewById(R.id.textViewCard);
        TextView marksTextView = view.findViewById(R.id.marksTxt);
        ImageView imageView = view.findViewById(R.id.qImage);
        Question question = mData.get(position);
        textViewCard.setText(question.getQuestion());
        marksTextView.setText("Marks: "+question.getMarks());
        RadioGroup optionView = view.findViewById(R.id.optionView);

        Glide.with(view)
                .load("https://d1m6qo1ndegqmm.cloudfront.net/uploadimages/sales_offer_mainpic_20110622120613Impart_Banner.jpg")
                .into(imageView);
        for (Map.Entry<String, String> entry : question.getOptions().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            RadioButton button = new RadioButton(mContext);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            button.setTypeface(ResourcesCompat.getFont(mContext, R.font.opensanssemibold));
            params.setMargins(0,15,0,0);
            button.setPadding(5,5,5,5);
            button.setBackgroundResource(R.drawable.layout_border);
            button.setTag(key);
            button.setLayoutParams(params);

            ColorStateList colorStateList = new ColorStateList(
                    new int[][] {
                            new int[] { -android.R.attr.state_checked }, // unchecked
                            new int[] {  android.R.attr.state_checked }  // checked
                    },
                    new int[] {
                            R.color.colorAccent,
                            R.color.colorPrimary
                    }
            );
            button.setButtonTintList(colorStateList);
            button.setText(value);
            if (question.getSelectedAns()!=null&&question.getSelectedAns().equals(key))
                button.setSelected(true);
            optionView.addView(button);
        }

        RadioGroup optionGroup = view.findViewById(R.id.optionView);
        optionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for(int i=0;i<group.getChildCount(); i++){
                    RadioButton button =(RadioButton) group.getChildAt(i);
                    if (button.getId() == checkedId) {
                        button.setBackgroundResource(R.drawable.redio_selected);
                        question.setSelectedAns(button.getTag().toString());
                    }
                    else button.setBackgroundResource(R.drawable.layout_border);
                }
                notifyDataSetChanged();
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
