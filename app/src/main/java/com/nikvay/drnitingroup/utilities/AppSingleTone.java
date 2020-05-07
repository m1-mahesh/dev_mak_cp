package com.nikvay.drnitingroup.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.ListItem;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.ZapfDingbatsList;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.itextpdf.text.pdf.draw.VerticalPositionMark;
import com.nikvay.drnitingroup.HomeWorkDetails;
import com.nikvay.drnitingroup.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static com.nikvay.drnitingroup.utilities.LogUtils.LOGE;


/**
 * Created by ambrosial on 16/6/17.
 */

public class AppSingleTone {

    //    https://github.com/wasabeef/awesome-android-ui

    public String signIn = "http://nikvay.com/demo/schoolApp/ws-login";
    public String submitOTP = "http://nikvay.com/demo/schoolApp/ws-submit-otp";
    public String classList = "http://nikvay.com/demo/schoolApp/ws-class-list";
    public String testList = "http://nikvay.com/demo/schoolApp/ws-online-test-list";
    public String questionList = "http://nikvay.com/demo/schoolApp/ws-online-test-question-list";
    public String classDivisionList = "http://nikvay.com/demo/schoolApp/ws-class-division-list";
    public String teacherTestList = "http://nikvay.com/demo/schoolApp/ws-teacher-online-test-list";
    public String subjectChapterList = "http://nikvay.com/demo/schoolApp/ws-subject-chapter-list";
    public String createTest = "http://nikvay.com/demo/schoolApp/ws-create-online-test";
    public String addTestQuestions = "http://nikvay.com/demo/schoolApp/ws-add-question-to-online-test";
    public String submitTest = "http://nikvay.com/demo/schoolApp/ws-submit-test";
    public String attendTestQ = "http://nikvay.com/demo/schoolApp/ws-online-test-question-list-for-student";
    public String testResult = "http://nikvay.com/demo/schoolApp/ws-student-test-result";
    public String testResultAll = "http://nikvay.com/demo/schoolApp/ws-teacher-test-result";
    public String studentListByDivision = "http://nikvay.com/demo/schoolApp/ws-student-list-for-attendence";
    public String submitNotice = "http://nikvay.com/demo/schoolApp/ws-submit-notice";
    public String teacherNoticeList = "http://nikvay.com/demo/schoolApp/ws-teacher-notice-list";
    public String studentNoticeList = "http://nikvay.com/demo/schoolApp/ws-student-notice-list";
    public String submitHomework = "http://nikvay.com/demo/schoolApp/ws-submit-homework";
    public String studentHomeworkList = "http://nikvay.com/demo/schoolApp/ws-homework-list";
    public String teacherHomeworkList = "http://nikvay.com/demo/schoolApp/ws-homework-list-teacher";
    public String submitClassAttendance = "http://nikvay.com/demo/schoolApp/ws-submit-attendence";
    public String studentAttendance = "http://nikvay.com/demo/schoolApp/ws-student-attendence-list";
    public String attendanceByClass = "http://nikvay.com/demo/schoolApp/ws-teacher-attendence-list";
    public String attendanceStatusByClass = "http://nikvay.com/demo/schoolApp/ws-check-attendence-status";
    public String deleteNotice = "http://nikvay.com/demo/schoolApp/ws-delete-notice-for-teacher";
    public String deleteHomework = "http://nikvay.com/demo/schoolApp/ws-delete-homework-for-teacher";
    public String createTimetable = "http://nikvay.com/demo/schoolApp/ws-submit-timetable-for-teacher";
    public String getTimetablesForStudent = "http://nikvay.com/demo/schoolApp/ws-timetable-list";
    public String getTimetablesForTeacher = "http://nikvay.com/demo/schoolApp/ws-timetable-list-for-teacher";
    public String deleteTimetable = "http://nikvay.com/demo/schoolApp/ws-delete-timetable-for-teacher";
    public String getVideos = "http://nikvay.com/demo/schoolApp/ws-video-list";
    public String getTopicVideos = "http://nikvay.com/demo/schoolApp/ws-topic-list";
    public String getBanners = "http://nikvay.com/demo/schoolApp/ws-slider-list";
    public String getInstructionList = "http://nikvay.com/demo/schoolApp/ws-instruction-list";
    public String getTestInstructionList = "http://nikvay.com/demo/schoolApp/ws-online-test-instruction-list";
    public String createNewPaper = "http://nikvay.com/demo/schoolApp/ws-submit-paper-info";
    public String addPaperQuestions = "http://nikvay.com/demo/schoolApp/ws-submit-paper-questions";
    public String paperListForTeacher = "http://nikvay.com/demo/schoolApp/ws-teacher-paper-list";
    public String paperListForAdmin = "http://nikvay.com/demo/schoolApp/ws-all-paper-list";
    public String questionsListForPaper = "http://nikvay.com/demo/schoolApp/ws-paper-genration-question-list";
    public String paperData = "http://nikvay.com/demo/schoolApp/ws-get-paper-data";
    public String editPaperAddQuestions= "http://nikvay.com/demo/schoolApp/ws-add-question-in-paper-edit-mode";
    public String editPaperDeleteQuestions= "http://nikvay.com/demo/schoolApp/ws-delet-question-from-paper";
    public String editPaperSection = "http://nikvay.com/demo/schoolApp/ws-edit-question-heading";


    Context context;
    UserSession userSession;
    SharedPreferences sharedPreferences;

    public AppSingleTone(Context context) {
        this.context = context;
        sharedPreferences = this.context.getSharedPreferences("User", Context.MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
    }

    private static boolean isValidPhone(String phone) {
        String phonePattern = "^([1-9]{1}[0-9]{9})$";
        Pattern pattern = Pattern.compile(phonePattern);
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    private static boolean isValidPin(String pinCode) {
        String phonePattern = "^[0-9]{1,6}$";
        Pattern pattern = Pattern.compile(phonePattern);
        Matcher matcher = pattern.matcher(pinCode);
        return matcher.matches();
    }

    //FIRST NAME
    private static boolean isFirstName(String firstName) {
        String firstNamePattern = "[a-zA-Z ]+$";
        Pattern pattern = Pattern.compile(firstNamePattern);
        Matcher matcher = pattern.matcher(firstName);
        return matcher.matches();
    }

    public boolean isNotEmpty(EditText editText) {
        return editText.getText().toString().length() > 0;
    }

    //phone number
    public boolean validatePhoneNo(EditText phoneNumber, TextInputLayout phoneno_layout) {
        if (phoneNumber.getText().toString().trim().isEmpty() || !isValidPhone(phoneNumber.getText().toString())) {
            phoneno_layout.setError("Expected format: 10 digits of your mobile number without starting with 0 or country code");
            requestFocus(phoneNumber);

            return false;
        } else {
            phoneno_layout.setError(null);

        }

        return true;
    }

    public boolean validatePasswordLogin(EditText password, TextInputLayout password_layout) {
        if (password.getText().toString().trim().isEmpty()) {
            password_layout.setError("Please Enter Password");
            requestFocus(password);

            return false;
        } else {
            password_layout.setError(null);

        }

        return true;
    }

    public boolean isValidName(EditText firstName, TextInputLayout firstname_layout) {

        if (firstName.getText().toString().trim().isEmpty() || !isFirstName(firstName.getText().toString())) {
            firstname_layout.setError("Should not be empty,Should not contain any number, Special character");
            requestFocus(firstName);

            return false;
        } else {
            firstname_layout.setError(null);


        }

        return true;
    }


    public void requestFocus(View view) {
        if (view.requestFocus()) {
            ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    public boolean checkAndRequestPermissions() {
        int cameraPermission = ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA);
        int storagePermission = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (storagePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (cameraPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            requestPermissions((Activity) context, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), 100);

            return false;
        }
        return true;
    }


    public void showDialogOK(String title, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("SETTINGS", okListener)
                .setNegativeButton("NOT NOW", okListener)
                .create()
                .show();
    }

    public PdfPCell getCell(String text, int alignment, Font font, boolean isMargin, int colSpan) {
        Paragraph p = new Paragraph(text, font);
        if(isMargin)
            p.setIndentationLeft(30);
        if (alignment == PdfPCell.ALIGN_RIGHT)
            p.setAlignment(Element.ALIGN_RIGHT);
        else if(alignment == PdfPCell.ALIGN_CENTER)
            p.setAlignment(Element.ALIGN_CENTER);
        PdfPCell cell = new PdfPCell();

        if(colSpan>0) {
            p.setAlignment(Element.ALIGN_CENTER);
            cell.setColspan(colSpan);
        }
        cell.addElement(p);
        cell.setPadding(5);
        cell.setHorizontalAlignment(alignment);
        cell.setBorder(PdfPCell.NO_BORDER);
        return cell;
    }
    public void createPdf(String fileName) {
        if (jsonObject!=null) {
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("Creating...");
            pDialog.show();
            pDialog.setCancelable(false);
            new CreatePDF().execute(fileName);
        }
    }

    ProgressDialog pDialog;

    public void downloadFile(Context context, String mUrl, String fileNameStr) {
        pDialog = new ProgressDialog(context);
        pDialog.setMessage("Downloading...");
        pDialog.show();
        pDialog.setCancelable(false);

        InputStreamVolleyRequest request = new InputStreamVolleyRequest(Request.Method.GET, mUrl,
                new Response.Listener<byte[]>() {
                    @Override
                    public void onResponse(byte[] response) {
                        // TODO handle the response
                        try {
                            if (response != null) {

                                FileOutputStream outputStream;
//                                String name = noticeData.getId() +"."+ fileExt;
                                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);

                                outputStream = new FileOutputStream(path.getAbsolutePath() + "/"+fileNameStr);
                                outputStream.write(response);
                                outputStream.close();
                                if (path.exists()) {
                                    String pathString = path.getAbsoluteFile() + "/"+fileNameStr;
                                    File newFile = new File(pathString);
                                    if (newFile.exists()) {
                                        Log.e("Path: ", newFile.getAbsolutePath());
                                        try {
                                            // Output stream
                                            File downloadedFile = new File(newFile.getAbsolutePath());
                                            FileUtils.openFile(context, downloadedFile);
                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }
                            if (pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                        } catch (Exception e) {
                            // TODO Auto-generated catch block
                            Log.d("KEY_ERROR", "UNABLE TO DOWNLOAD FILE");
                            e.printStackTrace();
                            if (pDialog != null && pDialog.isShowing())
                                pDialog.dismiss();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO handle the error
                error.printStackTrace();
                if (pDialog != null && pDialog.isShowing())
                    pDialog.dismiss();
            }
        }, null);
        RequestQueue mRequestQueue = Volley.newRequestQueue(context, new HurlStack());
        mRequestQueue.add(request);
    }
    public long getDiffInMin(Date d1, Date d2){
        try {
            //in milliseconds
            long diff = d2.getTime() - d1.getTime();

            long diffSeconds = diff / 1000 % 60;
            long diffMinutes = diff / (60 * 1000) % 60;
            long diffHours = diff / (60 * 60 * 1000) % 24;
            long diffDays = diff / (24 * 60 * 60 * 1000);

           return diffMinutes;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Constant.UNDEFINED_TIME;
    }

    public static JSONObject jsonObject;
    class CreatePDF extends AsyncTask<String, String, String> {
        Document document = new Document();
        String filePath = null;
        protected String doInBackground(String... urls) {
            try {
                String fileName = urls[0];
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                String dest = path.getAbsolutePath() +"/"+fileName;
                File oldFile = new File(dest);
                if (oldFile.exists()) {
                    oldFile.delete();
                }

                try {
                    /**
                     * Creating Document
                     */
                    // Location to save
                    PdfWriter.getInstance(document, new FileOutputStream(dest));

                    // Open to write
                    document.open();

                    // Document Settings
                    document.setPageSize(PageSize.A4);
                    document.addCreationDate();
                    document.addAuthor("MAK Pvt. Ltd");
                    document.addCreator("Mahesh Kharat");

                    /***
                     * Variables for further use....
                     */
                    JSONObject paperInfo = jsonObject.getJSONObject("paper_information");
                    JSONArray instructionArray = jsonObject.getJSONArray("instructions");
                    JSONArray paperDataArray = jsonObject.getJSONArray("paper_data");
                    BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
                    float mHeadingFontSize = 20.0f;
                    float mValueFontSize = 18.0f;

                    /**
                     * How to USE FONT....
                     */
                    BaseFont urName = BaseFont.createFont("res/font/playfairdisplayregular.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    BaseFont title = BaseFont.createFont("res/font/proximanovaregular.otf", BaseFont.IDENTITY_H, BaseFont.EMBEDDED);
                    // LINE SEPARATOR
                    LineSeparator lineSeparator = new LineSeparator();
                    lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));
                    // Title Order Details...
                    // Adding Title....

                    Paragraph top = new Paragraph();
                    try {
                        InputStream ims = context.getAssets().open("pdf_top_ic.png");
                        Bitmap bmp = BitmapFactory.decodeStream(ims);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        Image i = Image.getInstance(stream.toByteArray());
                        i.setAlignment(Element.ALIGN_CENTER);
                        i.scaleToFit(500f, 310f);
                        top.add(i);
                        document.add(top);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    PdfPTable table = new PdfPTable(3);
                    table.setSpacingBefore(20);

                    Font headFont = new Font(urName, 15.0f, Font.NORMAL, BaseColor.BLACK);
                    Font centerHeadFont = new Font(urName, 18.0f, Font.NORMAL, BaseColor.BLACK);

                    table.setWidthPercentage(100);
                    table.addCell(getCell(paperInfo.getString("exam_date"), PdfPCell.ALIGN_LEFT, headFont, false,0));
                    table.addCell(getCell(paperInfo.getString("subject_name"), PdfPCell.ALIGN_CENTER, centerHeadFont, false,0));
                    table.addCell(getCell(paperInfo.getString("class_name"), PdfPCell.ALIGN_RIGHT, headFont, false, 0));

                    table.addCell(getCell(paperInfo.getString("title"), PdfPCell.ALIGN_CENTER, centerHeadFont, false,3));

                    table.addCell(getCell("Time: "+paperInfo.getString("exam_time_hr")+" Hr", PdfPCell.ALIGN_LEFT, headFont, false, 0));
                    table.addCell(getCell("", PdfPCell.ALIGN_CENTER, centerHeadFont, false, 0));
                    table.addCell(getCell(paperInfo.getString("total_marks"), PdfPCell.ALIGN_RIGHT, headFont, false, 0));
                    document.add(table);
                    document.add(new Chunk(lineSeparator));
                    Paragraph instructions = new Paragraph(new Chunk("Instructions:", headFont));
                    instructions.setSpacingBefore(10);
                    document.add(instructions);
                    com.itextpdf.text.List orderedList = new com.itextpdf.text.List(com.itextpdf.text.List.ORDERED);
                    orderedList.setIndentationLeft(20);
                    for(int k=0;k<instructionArray.length();k++){
                        JSONObject inJsonObject = instructionArray.getJSONObject(k);
                        Font optionFont = new Font(urName, 14.0f, Font.NORMAL, BaseColor.BLACK);
                        ListItem item=new ListItem(inJsonObject.getString("title"));
                        item.setFont(optionFont);
                        orderedList.add(item);
                    }
                    document.add(orderedList);
                    Font marks = new Font(urName, mHeadingFontSize, Font.BOLD, BaseColor.BLACK);
                    Chunk marksChunk = new Chunk("", marks); // For Section
                    Paragraph marksIdParagraph = new Paragraph(marksChunk);
                    marksIdParagraph.setAlignment(Element.ALIGN_CENTER);
                    marksIdParagraph.setSpacingBefore(20);
                    document.add(marksIdParagraph);

                    int sectionChar = 65;
                    for(int j=0;j<paperDataArray.length();j++){
                        JSONObject jsonObject = paperDataArray.getJSONObject(j);
                        Chunk glue = new Chunk(new VerticalPositionMark());
                        Paragraph p1 = new Paragraph("Section-"+((char)sectionChar), marks);
                        sectionChar ++;
                        p1.setAlignment(Element.ALIGN_CENTER);
                        p1.setFont(marks);
                        p1.setSpacingBefore(20);
                        document.add(p1);
                        Paragraph p = new Paragraph("Q."+(j+1)+" "+jsonObject.getString("question_heading"), marks);
                        p.setFont(marks);
                        p.add(new Chunk(glue));
                        p.add(jsonObject.getString("marks")+"M");
                        p.setSpacingBefore(20);
                        p.setSpacingAfter(20);
                        document.add(p);
                        for(int i=0;i<jsonObject.getJSONArray("questions_list").length(); i++) {
                            JSONObject questions = jsonObject.getJSONArray("questions_list").getJSONObject(i);
                            Font mOrderDateValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
                            Chunk mOrderDateValueChunk = new Chunk((i+1)+". "+questions.getString("questions"), mOrderDateValueFont);
                            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
                            mOrderDateValueParagraph.setSpacingBefore(15);
                            mOrderDateValueParagraph.setIndentationLeft(20);
                            document.add(mOrderDateValueParagraph);
                            String imageUrl = questions.getString("image");
                            if(imageUrl!=null && !imageUrl.equals("null") && !imageUrl.equals("")) {
                                try {
                                    Image image = Image.getInstance(new URL(imageUrl));
                                    image.setSpacingBefore(15);
                                    image.setAlignment(Element.ALIGN_CENTER);
                                    image.scaleToFit(200f, 200f);
                                    document.add(image);

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            JSONArray options = questions.getJSONArray("options");
                            if (options.length()>0) {
                                PdfPTable optionTable = new PdfPTable(2);
                                optionTable.setSpacingBefore(10);
                                Font optionFont = new Font(urName, 14.0f, Font.NORMAL, BaseColor.BLACK);

                                optionTable.setWidthPercentage(100);
                                for (int o = 0; o < options.length(); o++) {
                                    JSONObject option = options.getJSONObject(o);
                                    optionTable.addCell(getCell((o + 1) + ". " + option.getString("option_value"), PdfPCell.UNDEFINED, optionFont, true, 0));
                                }
                                document.add(optionTable);
                            }
                        }
                    }
                    document.add(new Chunk(lineSeparator));
                    Paragraph last = new Paragraph("* All The Best *");
                    last.setFont(centerHeadFont);
                    last.setAlignment(Element.ALIGN_CENTER);
                    document.add(last);



                    filePath = dest;
                    return dest;

                } catch (IOException | DocumentException ie) {
                    LOGE("createPdf: Error " + ie.getLocalizedMessage());
                } catch (ActivityNotFoundException ae) {
                    Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {

                if (pDialog!=null&&pDialog.isShowing())
                    pDialog.dismiss();
            }
            return null;
        }

        protected void onPostExecute(String feed) {
            // TODO: check this.exception
            // TODO: do something with the feed
            document.close();
            if (pDialog!=null&&pDialog.isShowing())
                pDialog.dismiss();
            try {
                if(filePath!=null&& !filePath.equals(""))
                FileUtils.openFile(context, new File(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
