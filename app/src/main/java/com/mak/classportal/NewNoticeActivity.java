package com.mak.classportal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.fragment.NoticeStepOneFragment;
import com.mak.classportal.fragment.NoticeStepTwoFragment;
import com.mak.classportal.modales.StudentClass;
import com.mak.classportal.modales.SubjectData;
import com.mak.classportal.swap_plugin.CustomViewPager;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class NewNoticeActivity extends AppCompatActivity implements View.OnClickListener {

    EditText txtDate;
    private int mYear, mMonth, mDay, mHour, mMinute;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Calendar c;
    boolean isCamera;
    ImageView imageView;
    String picturePath = "";
    AppSingleTone appSingleTone;
    CustomViewPager mViewPager;
    ArrayList<StudentClass> classes = new ArrayList<>();
    SharedPreferences sharedPreferences;
    UserSession userSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notice1);
        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.new_notice);
        mViewPager =  findViewById(R.id.pager);
        mViewPager.setAdapter(new SamplePagerAdapter(
                getSupportFragmentManager()));

        mViewPager.setPagingEnabled(false);
        ((Button) findViewById(R.id.saveButton)).setOnClickListener(this);
        ((Button) findViewById(R.id.prevButton)).setOnClickListener(this);

        getClassDivision();
        /*txtDate = findViewById(R.id.date_edit_text);

        txtDate.setOnClickListener(this);
        txtDate.setTag(txtDate.getKeyListener());
        txtDate.setKeyListener(null);

        c = Calendar.getInstance();*/

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
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.date_edit_text:
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);


                DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                txtDate.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.time_edit_text:
                break;
            case R.id.saveButton:
                mViewPager.setCurrentItem(1,true);
                break;
            case R.id.prevButton:
                mViewPager.setCurrentItem(0,true);
                break;

        }

    }
    public class SamplePagerAdapter extends FragmentPagerAdapter {

        public SamplePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            /** Show a Fragment based on the position of the current screen */
            if (position == 0) {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("classData", classes);
                NoticeStepOneFragment oneStep = new NoticeStepOneFragment();
                oneStep.setArguments(bundle);
                return oneStep;
            } else {
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("classData", classes);
                NoticeStepTwoFragment oneStep = new NoticeStepTwoFragment();
                oneStep.setArguments(bundle);
                return oneStep;
            }
        }

        @Override
        public int getCount() {
            // Show 2 total pages.
            return 2;
        }
    }
    void prepareClassData(JSONArray apiResponse){
        try {
            classes.clear();
            SelectQuestionsActivity.divisions.clear();
            StudentClass aClass1 = new StudentClass();
            aClass1.setName("Select Class");
            aClass1.setId("");
            classes.add(aClass1);
            for (int i = 0; i < apiResponse.length(); i++) {
                JSONObject classObj = apiResponse.getJSONObject(i);
                StudentClass aClass = new StudentClass();
                aClass.setId(classObj.getString("class_id"));
                aClass.setName(classObj.getString("class_name"));
                for (int j =0; j< classObj.getJSONArray("division_list").length(); j++) {
                    JSONObject obj = classObj.getJSONArray("division_list").getJSONObject(j);
                    aClass.addDivision(obj.getString("division_id"), obj.getString("division_name"));
                    SelectQuestionsActivity.divisions.put(obj.getString("division_id"), obj.getString("division_name"));
                }
                classes.add(aClass);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public void getClassDivision() {

        try {
            String url = appSingleTone.classDivisionList;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("teacher_id", userSession.getAttribute("user_id"));
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.has("class_list")) {
                            JSONArray object = result.getJSONArray("class_list");
                            prepareClassData(object);
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

    void showPicPopup(){
        if (appSingleTone.checkAndRequestPermissions()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
            builder.setTitle("Select option");
            final CharSequence[] items = {"Gallery", "Camera"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        isCamera = false;
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, Constant.RESULT_LOAD_IMAGE);
                    } else if (item == 1) {
                        isCamera = true;
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, Constant.CAMERA_REQUEST);
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = this.getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                picturePath = selectedImage.getPath();

            } else {
                int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                picturePath = cursor.getString(idx);
                cursor.close();
            }
            imageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            //new UpdateProfile().execute();
//            uploadBitmap(BitmapFactory.decodeFile(picturePath));

        } else if (requestCode == Constant.CAMERA_REQUEST && resultCode == RESULT_OK) {


            Bitmap photo = (Bitmap) data.getExtras().get("data");

            Uri tempUri = getImageUri(this, photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));

            picturePath = finalFile.toString();
            imageView.setImageBitmap(photo);
            //new UpdateProfile().execute();
//            uploadBitmap(photo);
        } else {
            //Toast.makeText(this, "Please Select Image", Toast.LENGTH_LONG)
            //   .show();
        }

    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = this.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
}
