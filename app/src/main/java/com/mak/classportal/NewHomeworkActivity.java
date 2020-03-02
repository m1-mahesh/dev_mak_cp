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

public class NewHomeworkActivity extends AppCompatActivity implements View.OnClickListener {

    public Map<String, String> selectedDivisions = new HashMap<>();
    EditText txtDate, titleEditText, descriptionEditText;
    TextView customToast, attachmentText, selectTxt;
    GridLayout divisionsView;
    LayoutInflater inflater;
    View tostLayout;
    Calendar c;
    Spinner classSpinner, subjectSpinner;
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    ArrayList<StudentClass> classes = new ArrayList<>();
    ArrayList<SubjectData> subjects = new ArrayList<>();
    String selectedClass = "", selectedSubject = "";
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
    ProgressDialog progressDialog;
    private int mYear, mMonth, mDay, mHour, mMinute;

    public static String convertFileToByteArray(String picturePath) {
        byte[] byteArray = null;
        try {
            String extension = picturePath.substring(picturePath.lastIndexOf("."));
            String mimeTypeMap = MimeTypeMap.getFileExtensionFromUrl(extension);
            String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(mimeTypeMap);
            File file = new File(picturePath);
            InputStream inputStream = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            byte[] b = new byte[1024 * 11];
            int bytesRead = 0;

            while ((bytesRead = inputStream.read(b)) != -1) {
                bos.write(b, 0, bytesRead);
            }

            byteArray = bos.toByteArray();

            Log.e("Byte array", ">" + byteArray);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return Base64.encodeToString(byteArray, Base64.NO_WRAP);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homework_notice);
        ((TextView) findViewById(R.id.tvTitle)).setText(R.string.new_homework);
        txtDate = findViewById(R.id.date_edit_text);
        titleEditText = findViewById(R.id.title_edit_text);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        attachmentText = findViewById(R.id.attachment);
        classSpinner = findViewById(R.id.selectClass);
        subjectSpinner = findViewById(R.id.selectSubject);
        divisionsView = findViewById(R.id.divisionsLayout);
        selectTxt = findViewById(R.id.selectTxt);
        imageView = findViewById(R.id.imageView);
        appSingleTone = new AppSingleTone(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        attachmentIc = findViewById(R.id.attachmentIcon);
        selectedFileText = findViewById(R.id.selectedFileText);
        attachmentText.setOnClickListener(this);
        attachmentIc.setOnClickListener(this);
        attachmentText.setOnClickListener(this);
        txtDate.setOnClickListener(this);
        txtDate.setTag(txtDate.getKeyListener());
        txtDate.setKeyListener(null);

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
                if (selectedClass.equals(""))
                    showToast("Please Select Class");
                else if (selectedDivisions.size() == 0)
                    showToast("Please Select Division");
                else if (selectedSubject.equals(""))
                    showToast("Please Select Subject");
                else if (titleEditText.getText().toString().equals(""))
                    showToast("Please Enter Homework Title");
                else if (txtDate.getText().toString().equals(""))
                    showToast("Please Enter Submission Date");
                else if (descriptionEditText.getText().toString().equals(""))
                    showToast("Please Enter Homework Description");
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
                    if (!selectedClass.equals("")) {
                        getSubjectChapters();
                        selectTxt.setVisibility(View.VISIBLE);
                        divisionsView.setVisibility(View.VISIBLE);
                        subjectSpinner.setVisibility(View.VISIBLE);
                    }
                    divisionsView.removeAllViews();

                    for (Map.Entry<String, String> entry : sClass.getDivisions().entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                        CheckBox checkBox = new CheckBox(NewHomeworkActivity.this);
                        checkBox.setText(value);
                        params.setMargins(10, 10, 0, 0);
                        checkBox.setTypeface(ResourcesCompat.getFont(NewHomeworkActivity.this, R.font.proximanovaregular));
                        checkBox.setLayoutParams(params);
                        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                if (isChecked)
                                    selectedDivisions.put(key, value);
                                else selectedDivisions.remove(key);

                            }
                        });
                        divisionsView.addView(checkBox);
                    }

                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        } else {
            ArrayAdapter<SubjectData> spinnerAdapter = new ArrayAdapter<>(NewHomeworkActivity.this, android.R.layout.simple_spinner_item, subjects);
            spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            subjectSpinner.setAdapter(spinnerAdapter);
            subjectSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    SubjectData swt = (SubjectData) parent.getItemAtPosition(position);
                    selectedSubject = swt.getId();
                    if (!selectedSubject.equals("") && selectedDivisions.size() > 0) {
                        swt.setClassId(selectedClass);
                        swt.divisions = selectedDivisions;
                        SelectQuestionsActivity.subjectData = swt;
                    }
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
                for (int j = 0; j < classObj.getJSONArray("division_list").length(); j++) {
                    JSONObject obj = classObj.getJSONArray("division_list").getJSONObject(j);
                    aClass.addDivision(obj.getString("division_id"), obj.getString("division_name"));
                    SelectQuestionsActivity.divisions.put(obj.getString("division_id"), obj.getString("division_name"));
                }
                classes.add(aClass);
            }
            spinnerImplementation(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void prepareSubjectChapter(JSONArray apiResponse) {
        try {
            subjects.clear();
            SubjectData sub = new SubjectData();
            sub.setName("Select Subject");
            sub.setId("");
            subjects.add(sub);
            for (int i = 0; i < apiResponse.length(); i++) {
                JSONObject classObj = apiResponse.getJSONObject(i);
                SubjectData aClass = new SubjectData();
                aClass.setId(classObj.getString("id"));
                aClass.setName(classObj.getString("subject_name"));
                for (int j = 0; j < classObj.getJSONArray("chapter_list").length(); j++) {
                    JSONObject obj = classObj.getJSONArray("chapter_list").getJSONObject(j);
                    aClass.addChapter(obj.getString("id"), obj.getString("chapter_name"));
                }
                Log.e("", aClass.id);
                subjects.add(aClass);
            }
            spinnerImplementation(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void getSubjectChapters() {

        try {
            String url = appSingleTone.subjectChapterList;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", selectedClass);
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.has("subject_list")) {
                            JSONArray object = result.getJSONArray("subject_list");
                            prepareSubjectChapter(object);
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
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            if (cursor == null) { // Source is Dropbox or other similar local file path
                picturePath = selectedImage.getPath();
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
            imageView.setImageURI(selectedImage);
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
                            NewHomeworkActivity.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
                    builder1.setTitle("Warning")
                            .setMessage(
                                    getString(R.string.file_size_is_large_validation))
                            .setPositiveButton(
                                    "YES",
                                    new android.content.DialogInterface.OnClickListener() {
                                        @Override

                                        public void onClick(
                                                DialogInterface dialog, int which) {
                                            isValidFile = true;
                                            dialog1.cancel();
                                        }
                                    })
                            .setNegativeButton(
                                    "NO",
                                    new android.content.DialogInterface.OnClickListener() {
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
            Uri tempUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(getRealPathFromURI(tempUri));
            selectedFileText.setText(finalFile.getName());
            picturePath = finalFile.toString();
            mediaType = "Image";
            fileName = tempUri.getLastPathSegment();
            imageView.setImageBitmap(photo);
            int file_size = Integer.parseInt(String.valueOf(finalFile.length() / 1024));
            if (file_size > 5120) {
                showToast(getString(R.string.validation_image_size_msg));
            } else isValidFile = true;
        } else if (requestCode == Constant.PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {

            Uri uri = data.getData();
            if (uri.getScheme().compareTo("content") == 0) {
                String[] projection = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
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
                            imageView.setImageURI(uri);
                            if (file_size > 5120) {
                                showToast(getString(R.string.validation_image_size_msg));
                            } else isValidFile = true;

                        } else {
                            showToast(getString(R.string.extension_validation));
                        }
                    }
                    cursor.close();
                }
            } else if (uri.getScheme().compareTo("file") == 0) {
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
                if (picturePath != null && !picturePath.equals(""))
                    new MakeBase64().execute(picturePath);
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
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
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
            String url = appSingleTone.submitHomework;

            JSONArray jsonArray = new JSONArray();
            for (Map.Entry<String, String> entry : selectedDivisions.entrySet()) {
                String key = entry.getKey();
                jsonArray.put(key);
            }
//            fileBase64Str
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("org_id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("class_id", selectedClass);
            executeAPI.addPostParam("homework_subject", selectedSubject);
            executeAPI.addPostParam("title", titleEditText.getText().toString());
            executeAPI.addPostParam("homework_message", descriptionEditText.getText().toString());
            executeAPI.addPostParam("submission_date", txtDate.getText().toString());
            executeAPI.addPostParam("media_attachment", fileBase64Str);
            executeAPI.addPostParam("media_type", Constant.mediaTypes.get(mediaType.toLowerCase()));
            executeAPI.addPostParam("sender_user_id", userSession.getAttribute("user_id"));
            executeAPI.addPostParam("division_id_array", jsonArray.toString());
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    try {
                        if (result.getInt("error_code") == 200) {
                            showToast("New Homework Created");
                            HomeworkFragment.IS_ADD = true;
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

    class MakeBase64 extends AsyncTask<String, Boolean, String> {

        protected void onPreExecute() {
            progressDialog = new ProgressDialog(NewHomeworkActivity.this);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        protected String doInBackground(String... params) {
            String filePath = params[0];
            try {
                if (filePath != null && !filePath.equals("")) {
                    if (mediaType.equalsIgnoreCase("image")) {
                        Bitmap bitmap = BitmapFactory.decodeFile(filePath);
//                        Bitmap resizedBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, false);
                        fileBase64Str = toBase64(bitmap);
                    } else {
                        fileBase64Str = encodeFileToBase64Binary(new File(filePath));
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return fileBase64Str;
        }

        public String convertBitmapToString(Bitmap bitmap) {
            String encodedImage = "";

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            try {
                encodedImage = URLEncoder.encode(Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

            return encodedImage;
        }

        private String encodeFileToBase64Binary(File yourFile) {
            int size = (int) yourFile.length();
            byte[] bytes = new byte[size];
            try {
                BufferedInputStream buf = new BufferedInputStream(new FileInputStream(yourFile));
                buf.read(bytes, 0, bytes.length);
                buf.close();
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            String encoded = Base64.encodeToString(bytes, Base64.NO_WRAP);
            return encoded;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (progressDialog != null)
                progressDialog.dismiss();
        }

    }
}
