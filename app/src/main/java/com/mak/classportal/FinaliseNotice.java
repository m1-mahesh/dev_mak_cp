package com.mak.classportal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.fragment.NoticeStepTwoFragment;
import com.mak.classportal.modales.Question;
import com.mak.classportal.modales.StudentClass;
import com.mak.classportal.modales.StudentData;
import com.mak.classportal.swap_plugin.CustomViewPager;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.FileUtils;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class FinaliseNotice extends AppCompatActivity implements View.OnClickListener {

    EditText txtDate;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Calendar c;
    boolean isCamera;
    ImageView imageView;
    String picturePath = "";
    AppSingleTone appSingleTone;
    SharedPreferences sharedPreferences;
    UserSession userSession;
    private int mYear, mMonth, mDay, mHour, mMinute;
    TextView attachmentText;
    EditText titleEditText, descriptionEditText;
    Button saveButton;
    ImageView attachmentIc;
    TextView selectedFileText;
    // Will be one_class divisions, division students, all(divisions, students, all)
    public static String NOTICE_TYPE = "";
    public static String CLASS_ID = "";

    public static ArrayList<String> selectedStudents = new ArrayList<>();
    public static ArrayList<String> selectedDivisions = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_notice);
        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.new_notice);
        attachmentText = findViewById(R.id.attachment);
        imageView = findViewById(R.id.imageView);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        titleEditText = findViewById(R.id.title_edit_text);
        saveButton = findViewById(R.id.saveButton);
        attachmentIc = findViewById(R.id.attachmentIcon);
        selectedFileText = findViewById(R.id.selectedFileText);
        attachmentText.setOnClickListener(this);
        attachmentIc.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        prepareSubmitData();

    }
    JSONArray studentIdArray = new JSONArray();
    JSONArray divisionIdArray = new JSONArray();
    String noticeType = "";
    void prepareSubmitData(){
        if (NOTICE_TYPE.equals(Constant.NOTICE_TYPE_STUDENTS)){
            CLASS_ID="";
            for (int i=0;i<SelectStudents.selectedStudents.size(); i++){
                String q = SelectStudents.selectedStudents.get(i);
                studentIdArray.put(q);
            }
            noticeType = "3";
        }else if (NOTICE_TYPE.equals(Constant.NOTICE_TYPE_DIVISION)){
            noticeType = "1";
            for (Map.Entry<String, String> entry : NoticeStepTwoFragment.selectedDivisions.entrySet()) {
                String key = entry.getKey();
                divisionIdArray.put(key);
            }
        }else {
            noticeType = "2";
            CLASS_ID = "";
        }
    }
    void showToast(String toastText) {
        inflater = getLayoutInflater();
        tostLayout = inflater.inflate(R.layout.toast_layout_file,
                findViewById(R.id.toast_layout_root));
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
        switch (v.getId()) {
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
            case R.id.saveButton:
                if (validateFields())
                    saveNotice("");
                break;
            case R.id.attachment:
                showPicPopup();
                break;
            case R.id.attachmentIcon:
                if (true)
                    showPicPopup();
                break;

        }

    }
    boolean validateFields(){

        boolean isValid = false;
        if (!titleEditText.getText().toString().equals(""))
            isValid =true;
        else {
            showToast("'Title' should not be empty, Please Enter Title");
            isValid = false;
        }
        if (isValid) {
            if (!descriptionEditText.getText().toString().equals("")) {
                isValid = true;
            } else {
                showToast("'Description' should not be empty, Please Enter Description");
                isValid = false;
            }
        }
        return isValid;
    }

    void showPicPopup() {
        if (appSingleTone.checkAndRequestPermissions()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
            builder.setTitle("Select option");
            final CharSequence[] items = {"Browse File", "Camera"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        isCamera = false;
                        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                        i.setType("file/*");
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

            Bitmap bitmap = BitmapFactory.decodeFile(picturePath);
            if (bitmap!=null) {
                imageView.setImageBitmap(bitmap);
                new MakeBitmap().execute(bitmap);
            }

        } else if (requestCode == Constant.CAMERA_REQUEST && resultCode == RESULT_OK) {


            Bitmap photo = (Bitmap) data.getExtras().get("data");

            Uri tempUri = getImageUri(this, photo);

            // CALL THIS METHOD TO GET THE ACTUAL PATH
            File finalFile = new File(getRealPathFromURI(tempUri));
            selectedFileText.setText(finalFile.getName());
            picturePath = finalFile.toString();
            imageView.setImageBitmap(photo);

            if (photo!=null)
                toBase64(photo);
        } else {
            //Toast.makeText(this, "Please Select Image", Toast.LENGTH_LONG)
            //   .show();
        }

    }
    String imgBase64Str = "";
    String toBase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            imgBase64Str = Base64.encodeToString(byteArray, Base64.DEFAULT);
            return imgBase64Str;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
    public void saveNotice(String test_id) {

        try {
            String url = appSingleTone.submitNotice;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("title", titleEditText.getText().toString());
            executeAPI.addPostParam("description", descriptionEditText.getText().toString());
            executeAPI.addPostParam("media_base64", imgBase64Str);
            executeAPI.addPostParam("send_by_user_id", userSession.getAttribute("user_id"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("type", noticeType);
            executeAPI.addPostParam("class_id", CLASS_ID);
            executeAPI.addPostParam("division_id_array", divisionIdArray.toString());
            executeAPI.addPostParam("student_id_array", studentIdArray.toString());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    try {
                        Log.d("Result", result.toString());
                        if (result.getInt("error_code") == 200) {
                            showToast("New Notice Created");
                            setResult(Constant.ACTIVITY_FINISH_REQUEST_CODE);
                            finish();
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
    class MakeBitmap extends AsyncTask<Bitmap, Boolean, String> {

        protected void onPreExecute() {

        }

        protected String doInBackground(Bitmap... params) {
            Bitmap bitmap = params[0];
            return toBase64(bitmap);
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);

        }

    }
}
