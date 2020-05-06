package com.nikvay.drnitingroup.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.nikvay.drnitingroup.AppController;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.SelectQuestionsActivity;
import com.nikvay.drnitingroup.modales.Question;
import com.nikvay.drnitingroup.utilities.Constant;

import java.util.ArrayList;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.SingleItemRowHolder> {

    public static String CHAPTER_ID = "";
    boolean isView = false;
    String className = "";
    private Context mContext;
    ImageLoader imageLoader;
    public QuestionListAdapter(Context context, ArrayList<Question> itemsList, boolean isView) {
        this.mContext = context;
        this.isView = isView;
    }

    @Override
    public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.question_list_item, null);
        SingleItemRowHolder mh = new SingleItemRowHolder(v);

        return mh;
    }

    @Override
    public void onBindViewHolder(SingleItemRowHolder holder, final int i) {
        Question singleItem;
        if (imageLoader==null)
            imageLoader = AppController.getInstance().getImageLoader();
        if (Constant.DELETE_Q_IN_PAPER)
            singleItem = Constant.headQuestion.sectionData.get(i);
        else
            singleItem = SelectQuestionsActivity.chapterQuestions.get(CHAPTER_ID).get(i);


        if (!this.isView) {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.tvTitle.setVisibility(View.GONE);
            holder.checkBox.setText(singleItem.getQuestion());
            holder.checkBox.setOnCheckedChangeListener(null);

            //if true, your checkbox will be selected, else unselected
            holder.checkBox.setChecked(singleItem.isChecked);
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    singleItem.setChecked(isChecked);
                }
            });
        } else {
            holder.checkBox.setVisibility(View.GONE);
            holder.tvTitle.setVisibility(View.VISIBLE);
            holder.tvTitle.setText(singleItem.getQuestion());
            holder.tvTitle.setTextSize(15);
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }
        if (singleItem.getImageUrl()!=null&&!singleItem.getImageUrl().equals("")&&!singleItem.getImageUrl().equals("null")) {
            holder.questionImage.setVisibility(View.VISIBLE);
            holder.questionImage.setImageUrl(singleItem.getImageUrl(), imageLoader);
        }else holder.questionImage.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        if (Constant.DELETE_Q_IN_PAPER)
            return Constant.headQuestion.sectionData.size();
        return SelectQuestionsActivity.chapterQuestions.get(CHAPTER_ID).size();
    }

    public class SingleItemRowHolder extends RecyclerView.ViewHolder {

        protected TextView tvTitle;
        protected CheckBox checkBox;
        protected TextView devisionText;
        protected View hrView;
        protected LinearLayout divisionsView;
        protected NetworkImageView questionImage;

        public SingleItemRowHolder(View view) {
            super(view);
            this.tvTitle = view.findViewById(R.id.tvTitle);
            this.checkBox = view.findViewById(R.id.questionCheck);
            this.questionImage = view.findViewById(R.id.questionImage);

        }

    }

}