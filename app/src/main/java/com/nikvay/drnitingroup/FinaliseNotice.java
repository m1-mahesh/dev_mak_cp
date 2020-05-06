package com.nikvay.drnitingroup;

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
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.nikvay.drnitingroup.fragment.NoticeFragment;
import com.nikvay.drnitingroup.fragment.NoticeStepTwoFragment;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class FinaliseNotice extends AppCompatActivity implements View.OnClickListener {

    // Will be one_class divisions, division students, all(divisions, students, all)
    public static String NOTICE_TYPE = "";
    public static String CLASS_ID = "";
    public static ArrayList<String> selectedStudents = new ArrayList<>();
    public static ArrayList<String> selectedDivisions = new ArrayList<>();
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
    TextView attachmentText;
    EditText titleEditText, descriptionEditText;
    Button saveButton;
    ImageView attachmentIc;
    TextView selectedFileText;
    JSONArray studentIdArray = new JSONArray();
    JSONArray divisionIdArray = new JSONArray();
    String noticeType = "";
    Dialog dialog1;
    boolean isValidFile = false;
    String fileBase64Str = "";
    String extension, mediaType = "";
    Dialog alertDialog;
    String fileName = "";
    ProgressDialog progressDialog;
    private int mYear, mMonth, mDay, mHour, mMinute;
    Uri fileUri;
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

    void prepareSubmitData() {
        if (NOTICE_TYPE.equals(Constant.NOTICE_TYPE_STUDENTS)) {
            CLASS_ID = "";
            for (int i = 0; i < SelectStudents.selectedStudents.size(); i++) {
                String q = SelectStudents.selectedStudents.get(i);
                studentIdArray.put(q);
            }
            noticeType = "3";
        } else if (NOTICE_TYPE.equals(Constant.NOTICE_TYPE_DIVISION)) {
            noticeType = "1";
            for (Map.Entry<String, String> entry : NoticeStepTwoFragment.selectedDivisions.entrySet()) {
                String key = entry.getKey();
                divisionIdArray.put(key);
            }
        } else {
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

                                txtDate.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

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

    boolean validateFields() {

        boolean isValid = false;
        if (!titleEditText.getText().toString().equals(""))
            isValid = true;
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
                            Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            intent.setType("application/pdf images/*");
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
                imageView.setImageURI(fileUri);
                if (file_size > 5120) {
                    showToast(getString(R.string.validation_image_size_msg));
                } else isValidFile = true;
            } else {
                if (file_size > 15360) {
                    showToast(getString(R.string.validation_video_size_msg));

                } else if (file_size > 10240) {

                    AlertDialog.Builder builder1 = new AlertDialog.Builder(
                            FinaliseNotice.this, AlertDialog.THEME_DEVICE_DEFAULT_LIGHT);
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
            fileUri = getImageUri(getApplicationContext(), photo);
            File finalFile = new File(getRealPathFromURI(fileUri));
            mediaType = "Image";
            selectedFileText.setText(finalFile.getName());
            picturePath = finalFile.toString();
            fileName = fileUri.getLastPathSegment();
            imageView.setImageBitmap(photo);
            int file_size = Integer.parseInt(String.valueOf(finalFile.length() / 1024));
            if (file_size > 5120) {
                showToast(getString(R.string.validation_image_size_msg));
            } else isValidFile = true;
        } else if (requestCode == Constant.PICKFILE_RESULT_CODE && resultCode == RESULT_OK) {

            fileUri = data.getData();
            if (fileUri.getScheme().compareTo("content") == 0) {
                Cursor cursor = null;
                try {
                    File f = new File(fileUri.toString());
                    int file_size = Integer.parseInt(String.valueOf(f.length() / 1024));
                    cursor = this.getContentResolver().query(fileUri, null, null, null, null);
                    if (cursor != null && cursor.moveToFirst()) {
                        fileName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                    }
                    int lastDotPosition = fileName.lastIndexOf('.');
                    if (lastDotPosition > 0) {
                        String string3 = fileName.substring(lastDotPosition + 1);
                        extension = string3.toLowerCase();
                        if (extension.equalsIgnoreCase("pdf")) {
                            mediaType = "Pdf";
                            if (file_size > 5120) {
                                showToast(getString(R.string.validation_image_size_msg));
                            } else isValidFile = true;
                        } else if (extension.equalsIgnoreCase("tif") || extension.equalsIgnoreCase("tiff") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("png")) {
                            mediaType = "Image";
                            if (file_size > 5120) {
                                showToast(getString(R.string.validation_image_size_msg));
                            } else isValidFile = true;

                        }  else if (extension.equalsIgnoreCase("doc") || extension.equalsIgnoreCase("docx") ) {
                            mediaType = "Doc";
                            if (file_size > 5120) {
                                showToast(getString(R.string.validation_image_size_msg));
                            } else isValidFile = true;
                        }else{
                            showToast(getString(R.string.extension_validation));
                        }
                    }
                } finally {
                    cursor.close();
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
                if (alertDialog != null)
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
            Bitmap[] bitmaps = null;
            InputStream iStream = null;
            if (mediaType.equalsIgnoreCase("pdf")||mediaType.equalsIgnoreCase("doc")) {
                iStream = getContentResolver().openInputStream(fileUri);
            }else if(!picturePath.equals("")){
                bitmaps = new Bitmap[1];
                bitmaps[0] = MediaStore.Images.Media.getBitmap(FinaliseNotice.this.getContentResolver() , fileUri);
            }

            String url = appSingleTone.submitNotice;

            ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            executeAPI.addPostParam("title", titleEditText.getText().toString());
            executeAPI.addPostParam("description", descriptionEditText.getText().toString());
            executeAPI.addPostParam("media_type", Constant.mediaTypes.get(mediaType.toLowerCase()));
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
                            NoticeFragment.IS_ADD = true;
                            finish();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                    Log.d("Result", errorResponse.toString());
                }
            });
            executeAPI.showProcessBar(true);

            if (bitmaps==null && iStream!=null) {
                executeAPI.setContentType(Constant.CONTENT_TYPE_PDF_DOC);
                executeAPI.setFileName(fileName);
                executeAPI.executeMultiPartRequest(Request.Method.POST, null, "media_base64", iStream);
            }else if(bitmaps!=null) {
                executeAPI.setContentType(Constant.CONTENT_TYPE_IMAGE);
                executeAPI.executeMultiPartRequest(Request.Method.POST, bitmaps, "media_base64", null);
            }else{
                executeAPI.executeMultiPartRequest(Request.Method.POST, null, "media_attachment", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
