package com.nikvay.drnitingroup.swap_plugin;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.viewpager.widget.PagerAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nikvay.drnitingroup.AppController;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.RunTest;
import com.nikvay.drnitingroup.modales.Question;

import java.util.ArrayList;
import java.util.Map;

/**
 * Created by anupamchugh on 26/12/15.
 */
public class CustomPagerAdapter extends PagerAdapter {

    ImageLoader imageLoader;
    private Context mContext;

    public CustomPagerAdapter(Context context) {
        mContext = context;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        ViewGroup view = (ViewGroup) inflater.inflate(R.layout.question_card, collection, false);

        TextView textViewCard = view.findViewById(R.id.textViewCard);
        TextView marksTextView = view.findViewById(R.id.marksTxt);
        NetworkImageView imageView = view.findViewById(R.id.qImage);
        Question question = RunTest.mData.get(position);
        textViewCard.setText(question.getQuestion());
        marksTextView.setText("Marks: " + RunTest.testData.correctMarks);
        RadioGroup optionView = view.findViewById(R.id.optionView);
        if (question.getImageUrl()!=null && !question.getImageUrl().equals("null") && !question.getImageUrl().equals("")){
            imageLoader = AppController.getInstance().getImageLoader();
            imageView.setImageUrl(question.getImageUrl(), imageLoader);
        }else imageView.setVisibility(View.GONE);
        for (Map.Entry<String, String> entry : question.getOptions().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            RadioButton button = new RadioButton(mContext);
            RadioGroup.LayoutParams params = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.MATCH_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
            button.setTypeface(ResourcesCompat.getFont(mContext, R.font.opensanssemibold));
            params.setMargins(0, 15, 0, 0);
            button.setPadding(5, 5, 5, 5);
            button.setBackgroundResource(R.drawable.layout_border);
            button.setTag(key);
            button.setLayoutParams(params);
            ColorStateList colorStateList = new ColorStateList(
                    new int[][]{
                            new int[]{-android.R.attr.state_checked}, // unchecked
                            new int[]{android.R.attr.state_checked}  // checked
                    },
                    new int[]{
                            R.color.colorAccent,
                            R.color.colorPrimary
                    }
            );
            button.setButtonTintList(colorStateList);
            button.setText(value);
            if (RunTest.mData.get(position).getSelectedAns() != null && RunTest.mData.get(position).getSelectedAns().equals(key)) {
                button.setSelected(true);
                button.setBackgroundResource(R.drawable.redio_selected);
            }
            optionView.addView(button);
        }

        RadioGroup optionGroup = view.findViewById(R.id.optionView);
        optionGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                for (int i = 0; i < group.getChildCount(); i++) {
                    RadioButton button = (RadioButton) group.getChildAt(i);
                    if (button.getId() == checkedId) {
                        button.setBackgroundResource(R.drawable.redio_selected);
                        RunTest.mData.get(position).setSelectedAns(button.getTag().toString());
                        RunTest.mData.get(position).isChecked = true;
                    } else button.setBackgroundResource(R.drawable.layout_border);
                }
//                notifyDataSetChanged();
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
        return RunTest.mData.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return RunTest.mData.get(position).getQuestion();
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return POSITION_NONE;
    }
}
