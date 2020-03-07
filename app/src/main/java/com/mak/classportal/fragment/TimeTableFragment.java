package com.mak.classportal.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mak.classportal.AppController;
import com.mak.classportal.NewTimetableActivity;
import com.mak.classportal.R;
import com.mak.classportal.RootActivity;
import com.mak.classportal.modales.NoticeData;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Konstantin on 22.12.2014.
 */
public class TimeTableFragment extends Fragment {


    ArrayList<NoticeData> timeTableData = new ArrayList<>();
    public static boolean IS_ADD = false;
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    RecyclerView mRecyclerView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public void onResume() {
        super.onResume();
        if (IS_ADD) {
            IS_ADD = false;
            getTimeTableList();
        }
    }
    @SuppressLint("RestrictedApi")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_timetable, container, false);


        FloatingActionButton fab = rootView.findViewById(R.id.fab);
        if (!RootActivity.hasPermissionToCreate)
            fab.setVisibility(View.GONE);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), NewTimetableActivity.class));
                getActivity().overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });

        mRecyclerView = rootView.findViewById(R.id.timeTableList);
        appSingleTone = new AppSingleTone(getContext());
        sharedPreferences =  getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        getTimeTableList();
        return rootView;
    }

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    void parseTimetableList(JSONObject jsonObject){
        try {
            timeTableData.clear();
            Log.e("", jsonObject.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("timetable_list");
            for(int i = 0;i<jsonArray.length();i++){
                JSONObject object = jsonArray.getJSONObject(i);
                NoticeData notice = new NoticeData();
                notice.setId(object.getString("id"));
                notice.setTitle(object.getString("title"));
                notice.setMediaUrl(object.getString("media_name"));
                notice.setCreatedBy(object.getString("send_by"));
                notice.setType(object.getString("media_type"));
                notice.setClassName(object.getString("class_name"));
                notice.setDivisionName(object.getString("division_name"));
                timeTableData.add(notice);
            }
            /*Collections.sort(timeTableData, new Comparator<NoticeData>() {
                @Override
                public int compare(NoticeData o1, NoticeData o2) {
                    try {
                        return dateFormat.parse(o2.getCreatedOn()).compareTo(dateFormat.parse(o1.getCreatedOn()));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    return 0;
                }
            });*/
            TimetableAd adapter1 = new TimetableAd(getContext(), timeTableData);
            mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
            mRecyclerView.setAdapter(adapter1);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }
    public void getTimeTableList() {

        try {
            String url = null;
            if (userSession.isStudent())
                url = appSingleTone.getTimetablesForStudent;
            else
                url = appSingleTone.getTimetablesForTeacher;
            ExecuteAPI executeAPI = new ExecuteAPI(getContext(), url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            if (userSession.isTeacher())
                executeAPI.addPostParam("teacher_id", userSession.getAttribute("user_id"));
            else {
                executeAPI.addPostParam("student_id", userSession.getAttribute("user_id"));
                executeAPI.addPostParam("class_id", userSession.getAttribute("class_id"));
                executeAPI.addPostParam("division_id", userSession.getAttribute("division_id"));
            }
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    parseTimetableList(result);
                }

                @Override
                public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                    Log.d("Result", errorResponse.toString());
                }
            });
            executeAPI.showProcessBar(true);
            executeAPI.executeStringRequest(Request.Method.POST);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class TimetableAd extends RecyclerView.Adapter<TimetableAd.SingleItemRowHolder> {

        private ArrayList<NoticeData> itemsList;
        private Context mContext;
        AppSingleTone appSingleTone;
        UserSession userSession;
        SharedPreferences sharedPreferences;
        public TimetableAd(Context context, ArrayList<NoticeData> itemsList) {
            this.itemsList = itemsList;
            this.mContext = context;
            sharedPreferences = context.getSharedPreferences("User", Context.MODE_PRIVATE);
            appSingleTone = new AppSingleTone(mContext);
            userSession = new UserSession(sharedPreferences, sharedPreferences.edit());

        }

        @Override
        public SingleItemRowHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
            View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.time_tible_item, null);
            SingleItemRowHolder mh = new SingleItemRowHolder(v);
            return mh;
        }

        @Override
        public void onBindViewHolder(TimetableAd.SingleItemRowHolder holder, int i) {

            final NoticeData singleItem = itemsList.get(i);

            ImageLoader imageLoader = AppController.getInstance().getImageLoader();
            holder.imageView.setImageUrl(singleItem.getMediaUrl(), imageLoader);
            holder.createdOnDateTxt.setText(singleItem.getClassName()+"("+singleItem.getDivisionName()+")");
            holder.createdByText.setText(singleItem.getCreatedBy());
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            if (userSession.isTeacher()) {
                holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        showPopupMenu(singleItem.getId(), i);
                        return false;
                    }
                });
            }
        }

        void showPopupMenu(String id, int pos) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.AlertDialogStyle));
            builder.setTitle("Select option");
            final CharSequence[] items = {"Delete"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        deleteItem(id, pos);
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }

        @Override
        public int getItemCount() {
            return (null != itemsList ? itemsList.size() : 0);
        }

        public class SingleItemRowHolder extends RecyclerView.ViewHolder {

            protected TextView createdOnDateTxt, createdByText;
            protected NetworkImageView imageView;
            protected CardView cardView;


            public SingleItemRowHolder(View view) {
                super(view);
                this.createdOnDateTxt = view.findViewById(R.id.createdOnDateTxt);
                this.createdByText = view.findViewById(R.id.createdByText);
                this.imageView = view.findViewById(R.id.topImage);
                this.cardView = view.findViewById(R.id.cardView);
            }

        }

        public void deleteItem(String id, int pos) {

            try {
                String url = appSingleTone.deleteTimetable;

                ExecuteAPI executeAPI = new ExecuteAPI(mContext, url, null);
                executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
                executeAPI.addPostParam("teacher_id", userSession.getAttribute("user_id"));
                executeAPI.addPostParam("timetable_id", id);
                executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                    @Override
                    public void onResponse(JSONObject result) {
                        try {
                            Log.d("Result", result.toString());
                            if (result.getInt("error_code") == 200) {
                                itemsList.remove(pos);
                                notifyItemRemoved(pos);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                        Log.d("Result", errorResponse.toString());
                    }
                });
                executeAPI.showProcessBar(true);
                executeAPI.executeStringRequest(Request.Method.POST);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

