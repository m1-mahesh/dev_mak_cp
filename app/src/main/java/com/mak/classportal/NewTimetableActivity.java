package com.mak.classportal;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.fragment.HomeworkFragment;
import com.mak.classportal.fragment.TimeTableFragment;
import com.mak.classportal.modales.StudentClass;
import com.mak.classportal.modales.SubjectData;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class NewTimetableActivity extends AppCompatActivity implements View.OnClickListener {

    EditText titleEditText;
    TextView customToast, attachmentText;
    LayoutInflater inflater;
    View tostLayout;
    Calendar c;
    Spinner classSpinner, divisionSpinner;
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    ArrayList<StudentClass> classes = new ArrayList<>();
    ArrayList<SubjectData> subjects = new ArrayList<>();
    String selectedClass = "", selectedDivision = "";
    boolean isCamera;
    ImageView imageView;
    String picturePath = "";
    String fileBase64Str = "";
    String extension;
    ImageView attachmentIc;
    TextView selectedFileText;
    Dialog dialog1;
    boolean isValidFile = false;
    String mediaType = "";
    Dialog alertDialog;
    String fileName = "";
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_time_table);
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.new_timetable);
        titleEditText = findViewById(R.id.title_edit_text);
        attachmentText = findViewById(R.id.attachment);
        classSpinner = findViewById(R.id.selectClass);
        divisionSpinner = findViewById(R.id.selectDivision);
        imageView = findViewById(R.id.imageView);
        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        attachmentIc = findViewById(R.id.attachmentIcon);
        selectedFileText = findViewById(R.id.selectedFileText);
        attachmentText.setOnClickListener(this);
        attachmentIc.setOnClickListener(this);
        attachmentText.setOnClickListener(this);
        c = Calendar.getInstance();
        findViewById(R.id.saveButton).setOnClickListener(this);
        getClassDivision();
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

            case R.id.saveButton:
                if (selectedClass.equals(""))
                    showToast("Please Select Class");
                else if (selectedDivision.equals(""))
                    showToast("Please Select Division");
                else if (titleEditText.getText().toString().equals(""))
                    showToast("Please Enter Timetabale Title");
                else submitHomework();
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

    void spinnerImplementation(boolean isClass) {
        if (isClass) {
            ArrayAdapter<StudentClass> adapter = new ArrayAdapter<StudentClass>(this, android.R.layout.simple_spinner_dropdown_item, classes);
            classSpinner.setAdapter(adapter);
            classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    StudentClass sClass = (StudentClass) parent.getSelectedItem();
                    selectedClass = sClass.getId();
                    String[] divisionArrays = new String[sClass.getDivisions().size()+1];
                    divisionSpinner.setVisibility(View.VISIBLE);
                    int i = 0;
                    divisionArrays[i++] = "Select Division";
                    for (Map.Entry<String, String> entry : sClass.getDivisions().entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        divisionArrays[i++] = value;
                    }
                    ArrayAdapter<String> adapter1 = new ArrayAdapter(NewTimetableActivity.this, android.R.layout.simple_spinner_dropdown_item, divisionArrays);
                    divisionSpinner.setAdapter(adapter1);
                    divisionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                            String sDiv = (String) adapterView.getSelectedItem();
                            for (Map.Entry<String, String> entry : sClass.getDivisions().entrySet()) {
                                String key = entry.getKey();
                                String value = entry.getValue();
                                if (sDiv.equals(value)){
                                    selectedClass = sClass.getId();
                                    selectedDivision = key;
                                    break;
                                }else {
                                    selectedDivision = "";
                                }
                            }

                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> adapterView) {

                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
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

    void prepareClassData(JSONArray apiResponse) {
        try {
            classes.clear();
            StudentClass aClass1 = new StudentClass();
            aClass1.setName("Select Class");
            aClass1.setId("");
            classes.add(aClass1);
            for (int i = 0; i < apiResponse.length(); i++) {
                JSONObject classObj = apiResponse.getJSONObject(i);
                StudentClass aClass = new StudentClass();
                aClass.setId(classObj.getString("class_id"));
                aClass.setName(classObj.getString("class_name"));
                for (int j = 0; j < classObj.getJSONArray("division_list").length(); j++) {
                    JSONObject obj = classObj.getJSONArray("division_list").getJSONObject(j);
                    aClass.addDivision(obj.getString("division_id"), obj.getString("division_name"));
                }
                classes.add(aClass);
            }
            spinnerImplementation(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    void showPicPopup() {
        if (appSingleTone.checkAndRequestPermissions()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AlertDialogStyle));
            builder.setTitle("Select option");
            final CharSequence[] items = {"Gallery", "Camera", "Files"};
            builder.setItems(items, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int item) {
                    if (item == 0) {
                        isCamera = false;
                        //Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        //startActivityForResult(i, Constant.RESULT_LOAD_IMAGE);
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        i.setType("image/*");
                        startActivityForResult(i, Constant.RESULT_LOAD_IMAGE);
                    } else if (item == 1) {
                        isCamera = true;
                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(cameraIntent, Constant.CAMERA_REQUEST);
                    } else {
                        try {

                            String folderPath = Environment.getRootDirectory() + "";
                            Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            Uri myUri = Uri.parse(folderPath);
                            intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                            intent.setDataAndType(myUri, "file/*");
                            startActivityForResult(intent, Constant.PICKFILE_RESULT_CODE);
                        } catch (ActivityNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isValidFile = false;
        if (requestCode == Constant.RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            fileUri = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(fileUri, filePathColumn, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                picturePath = fileUri.getPath();
                isValidFile = true;
            } else {
                int idx = cursor.getColumnIndex(MediaStore.Images.Media.DATA); // Is this the wrong way to get the image cursor?
                Log.d("", "came here = " + idx); // Here idx us -1!!!
                cursor.moveToFirst();
                picturePath = cursor.getString(idx);
                isValidFile = true;
                cursor.close();
            }
            File f = new File(picturePath);
            imageView.setImageURI(fileUri);
            int file_size = Integer.parseInt(String.valueOf(f.length() / 1024));
            fileName = picturePath.substring(picturePath.lastIndexOf("/") + 1);
            int lastDotPosition = fileName.lastIndexOf('.');
            if (lastDotPosition > 0) {
                String string3 = fileName.substring(lastDotPosition + 1);
                extension = string3.toLowerCase();
                Log.e("extension", "extension" + extension);
            }
            if (extension.equalsIgnoreCase("tif") || extension.equalsIgnoreCase("tiff") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png")) {
                mediaType = "Image";
                if (file_size > 5120) {
                    showToast(getString(R.string.validation_image_size_msg));
                } else isValidFile = true;
            } else {
                if (file_size > 15360) {
                    showToast(getString(R.string.validation_video_size_msg));

                } else if (file_size > 10240) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(
                            NewTimetableActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    builder1.setTitle("Warning")
                            .setMessage(
                                    getString(R.string.file_size_is_large_validation))
                            .setPositiveButton(
                                    "YES",
                                    new DialogInterface.OnClickListener() {
                                        @Override

                                        public void onClick(
                                                DialogInterface dialog, int which) {
                                            isValidFile = true;
                                            dialog1.cancel();
                                        }
                                    })
                            .setNegativeButton(
                                    "NO",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialog,
                                                int which) {
                                            dialog1.cancel();
                                        }
                                    });
                    dialog1 = builder1.create();
                    dialog1.show();
                    AlertDialog alert = builder1.create();
                    alert.show();
                } else isValidFile = true;

            }
        } else if (requestCode == Constant.CAMERA_REQUEST && resultCode == RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            fileUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(getRealPathFromURI(fileUri));
            selectedFileText.setText(finalFile.getName());
            picturePath = finalFile.toString();
            mediaType = "Image";
            fileName = fileUri.getLastPathSegment();
            imageView.setImageBitmap(photo);
            int file_size = Integer.parseInt(String.valueOf(finalFile.length() / 1024));
            if (file_size > 5120) {
                showToast(getString(R.string.validation_image_size_msg));
            } else isValidFile = true;
        } else if (requestCode == Constant.PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {

            fileUri = data.getData();
            if (fileUri.getScheme().compareTo("content") == 0) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(fileUri, projection, null, null, null);
                if (cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    final Uri filePathUri = Uri.parse(cursor.getString(column_index));
                    fileName = filePathUri.getLastPathSegment();
                    picturePath = filePathUri.getPath();
                    int file_size = Integer.parseInt(String.valueOf(picturePath.length() / 1024));

                    int lastDotPosition = fileName.lastIndexOf('.');
                    if (lastDotPosition > 0) {
                        String string3 = fileName.substring(lastDotPosition + 1);
                        extension = string3.toLowerCase();
                        if (extension.equalsIgnoreCase("pdf")) {
                            isValidFile = true;
                        } else if (extension.equalsIgnoreCase("tif") || extension.equalsIgnoreCase("tiff") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png")) {
                            mediaType = "Image";
                            imageView.setImageURI(fileUri);
                            if (file_size > 5120) {
                                showToast(getString(R.string.validation_image_size_msg));
                            } else isValidFile = true;

                        } else {
                            showToast(getString(R.string.extension_validation));
                        }
                    }
                    cursor.close();
                }
            } else if (fileUri.getScheme().compareTo("file") == 0) {
                picturePath = data.getData().getPath();
                File f = new File(picturePath);
                int file_size = Integer.parseInt(String.valueOf(f.length() / 1024));
                Log.e("file_size", "file_size" + file_size);
                fileName = data.getData().getLastPathSegment();
                int lastDotPosition = fileName.lastIndexOf('.');
                if (lastDotPosition > 0) {
                    String string3 = fileName.substring(lastDotPosition + 1);
                    extension = string3.toLowerCase();
                    if (extension.equalsIgnoreCase("pdf")) {
                        mediaType = "Pdf";
                        isValidFile = true;
                    } else if (extension.equalsIgnoreCase("tif") || extension.equalsIgnoreCase("tiff") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png")) {
                        mediaType = "Image";
                        if (file_size > 5120) {
                            showToast(getString(R.string.validation_image_size_msg));
                        } else isValidFile = true;

                    } else {
                        showToast(getString(R.string.extension_validation));
                    }
                }
            }

        }
        if (isValidFile) {
            selectedFileText.setText(fileName);
            popUp("");
        }
    }

    private void popUp(final String date) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this, AlertDialog.THEME_DEVICE_DEFAULT_DARK);
        builder.setCancelable(false);
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.upload_confirm, null);
        builder.setView(promptsView);
        TextView title = promptsView.findViewById(R.id.title);
        title.setText("Attachment");
        TextView already = promptsView.findViewById(R.id.already);
        TextView selectedFileText = promptsView.findViewById(R.id.selectedFileText);
        selectedFileText.setText(fileName);
        AppCompatButton yesButton = promptsView.findViewById(R.id.yesButton);
        AppCompatButton noButton = promptsView.findViewById(R.id.noButton);
        ImageView cross = promptsView.findViewById(R.id.cross);
        cross.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.cancel();
            }
        });
        alertDialog = builder.create();
        alertDialog.show();

    }

    String toBase64(Bitmap bitmap) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            fileBase64Str = Base64.encodeToString(byteArray, Base64.DEFAULT);
            return fileBase64Str;
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

    public void submitHomework() {

        try {
            String url = appSingleTone.createTimetable;
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", selectedClass);
            executeAPI.addPostParam("title", titleEditText.getText().toString());
            executeAPI.addPostParam("media_url", fileBase64Str);
            executeAPI.addPostParam("media_type", Constant.mediaTypes.get(mediaType.toLowerCase()));
            executeAPI.addPostParam("sender_user_id", userSession.getAttribute("user_id"));
            executeAPI.addPostParam("division_id", selectedDivision);
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.getInt("error_code") == 200) {
                            showToast("New TimeTable Created");
                            TimeTableFragment.IS_ADD = true;
                            finish();
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

}
