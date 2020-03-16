package com.nikvay.drnitingroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.adapter.StudentListAdapter;
import com.nikvay.drnitingroup.modales.StudentData;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class TakeAttendance extends AppCompatActivity {

    public static String CLASS_ID = "";
    public static String CLASS_NAME = "";
    public static String DIVISION_ID = "";
    public static String DIVISION_NAME = "";
    public static String ATTENDANCE_DATE = "";



    ArrayList<StudentData> students = new ArrayList<>();
    TextView topTitle;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    public static HashMap<String, String> studentAttendance = new HashMap<>();
    private SearchView searchView;
    StudentListAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_attendance);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Class: "+CLASS_NAME+"("+ DIVISION_NAME+")");
        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());

        mRecyclerView = findViewById(R.id.studentList);
        mRecyclerView.setHasFixedSize(true);
        studentAttendance.clear();
        mAdapter = new StudentListAdapter(this,students, true);
        getStudents();

    }

    void prepareStudents(JSONArray apiResponse){
        try {
            students.clear();
            SelectQuestionsActivity.divisions.clear();

            for (int i = 0; i < apiResponse.length(); i++) {
                JSONObject classObj = apiResponse.getJSONObject(i);
                StudentData studentData = new StudentData();
                studentData.setId(classObj.getString("student_id"));
                studentData.setName(classObj.getString("name"));
                studentAttendance.put(studentData.getId(), "1");
                students.add(studentData);
            }
            mAdapter = new StudentListAdapter(this,students, true);

            mRecyclerView.setLayoutManager(new GridLayoutManager(getApplicationContext(), 2));
            mRecyclerView.setAdapter(mAdapter);

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getStudents() {

        try {
            String url = appSingleTone.studentListByDivision;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", CLASS_ID);
            executeAPI.addPostParam("division_id", DIVISION_ID);
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.has("student_list")) {
                            JSONArray object = result.getJSONArray("student_list");
                            prepareStudents(object);
                        } else {
                            showToast("Something went wrong, Please try again later");
                        }
                    } catch (JSONException e) {
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

    public void submitAttendance(View view){
        if (studentAttendance.size()>0){
            submitAttendance();
        }
    }
    void showToast(String toastText){
        inflater = getLayoutInflater();
        tostLayout = inflater.inflate(R.layout.toast_layout_file,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        customToast = tostLayout.findViewById(R.id.text);
        Toast toast = new Toast(getApplicationContext());
        customToast.setText(toastText);
        customToast.setTypeface(ResourcesCompat.getFont(this, R.font.opensansregular));
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(tostLayout);
        toast.show();
    }

    public void submitAttendance() {

        try {
            String url = appSingleTone.submitClassAttendance;
            JSONObject jsonObject = new JSONObject();
            for (Map.Entry<String, String> entry : studentAttendance.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                jsonObject.put(key, value);
            }

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", CLASS_ID);
            executeAPI.addPostParam("division_id", DIVISION_ID);
            executeAPI.addPostParam("attendances_marked_by_teacher_id", userSession.getAttribute("user_id"));
            executeAPI.addPostParam("attendance_date", ATTENDANCE_DATE);
            executeAPI.addPostParam("attendance_data_array", jsonObject.toString());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    try {
                        Log.d("Result", result.toString());
                        if (result.getInt("error_code") == 200) {
                            showToast("Data Submitted");
                            finish();
                        }
                    }catch (JSONException e){
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.app_bar_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                mAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                mAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }
    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.app_bar_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
