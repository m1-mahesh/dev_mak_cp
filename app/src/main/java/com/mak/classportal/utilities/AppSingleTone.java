package com.mak.classportal.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

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
import com.itextpdf.text.ZapfDingbatsList;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.codec.Base64;
import com.itextpdf.text.pdf.draw.LineSeparator;
import com.mak.classportal.R;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static androidx.core.app.ActivityCompat.requestPermissions;
import static com.mak.classportal.utilities.LogUtils.LOGE;


/**
 * Created by ambrosial on 16/6/17.
 */

public class AppSingleTone {

    //    https://github.com/wasabeef/awesome-android-ui

    public String signIn = "http://nikvay.com/demo/schoolApp/ws-login";
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

    public void createPdf(String dest) {

        if (new File(dest).exists()) {
            new File(dest).delete();
        }

        try {
            /**
             * Creating Document
             */
            Document document = new Document();

            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(dest));

            // Open to write
            document.open();

            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("MAK LTD");
            document.addCreator("Mahesh Kharat");

            /***
             * Variables for further use....
             */
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
            float mHeadingFontSize = 20.0f;
            float mValueFontSize = 26.0f;

            /**
             * How to USE FONT....
             */
            BaseFont urName = BaseFont.createFont("res/font/opensanssemibold.ttf", "UTF-8", BaseFont.EMBEDDED);
            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            // Title Order Details...
            // Adding Title....
            Font mOrderDetailsTitleFont = new Font(urName, 36.0f, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDetailsTitleChunk = new Chunk("Class Portal", mOrderDetailsTitleFont);
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraph);

            // Fields of Order Details...
            // Adding Chunks for Title and value
            Font mOrderIdFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderIdChunk = new Chunk("Class: 10 SDT", mOrderIdFont);
            Paragraph mOrderIdParagraph = new Paragraph(mOrderIdChunk);
            document.add(mOrderIdParagraph);

            Font marks = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk marksChunk = new Chunk("Marks: 100", marks);
            Paragraph marksIdParagraph = new Paragraph(marksChunk);
            marksIdParagraph.setAlignment(Element.ALIGN_RIGHT);
            document.add(marksIdParagraph);
            document.add(new Paragraph(""));
            Chunk date = new Chunk("Date: 12-01-2020", marks);
            Paragraph dateP = new Paragraph(date);
            dateP.setAlignment(Element.ALIGN_RIGHT);
            document.add(dateP);
            document.add(new Paragraph(""));
            Chunk time = new Chunk("Time: 3 Hour", marks);
            Paragraph timeP = new Paragraph(time);
            timeP.setAlignment(Element.ALIGN_RIGHT);
            document.add(timeP);


            // Adding Line Breakable Space....
            document.add(new Paragraph(""));
            // Adding Horizontal Line...
            document.add(new Chunk(lineSeparator));
            // Adding Line Breakable Space....
            document.add(new Paragraph(""));

            // Fields of Order Details...

            Font mOrderDateValueFont = new Font(urName, mValueFontSize, Font.NORMAL, BaseColor.BLACK);
            Chunk mOrderDateValueChunk = new Chunk("1. Who is making the web standards?", mOrderDateValueFont);
            Paragraph mOrderDateValueParagraph = new Paragraph(mOrderDateValueChunk);
            document.add(mOrderDateValueParagraph);

            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            Font mOrderDateFont = new Font(urName, mHeadingFontSize, Font.NORMAL, mColorAccent);
            Chunk mOrderDateChunk = new Chunk("  1. Google", mOrderDateFont);
            Paragraph mOrderDateParagraph = new Paragraph(mOrderDateChunk);
            document.add(mOrderDateParagraph);
            document.add(new Paragraph(""));
            Chunk o2 = new Chunk("  2. Microsoft", mOrderDateFont);
            document.add(new Paragraph(o2));
            Chunk o3 = new Chunk("  3. Facebook", mOrderDateFont);
            document.add(new Paragraph(o3));
            Chunk o4 = new Chunk("  4. Amazon", mOrderDateFont);
            document.add(new Paragraph(o4));
            document.close();

            FileUtils.openFile(context, new File(dest));

        } catch (IOException | DocumentException ie) {
            LOGE("createPdf: Error " + ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }

    public void createMyPdf(String dest) {

        if (new File(dest).exists()) {
            new File(dest).delete();
        }

        try {
            /**
             * Creating Document
             */
            Document document = new Document();

            // Location to save
            PdfWriter.getInstance(document, new FileOutputStream(dest));

            // Open to write
            document.open();

            // Document Settings
            document.setPageSize(PageSize.A4);
            document.addCreationDate();
            document.addAuthor("MAK LTD");
            document.addCreator("Mahesh Kharat");

            /***
             * Variables for further use....
             */
            BaseColor mColorAccent = new BaseColor(0, 0, 0, 255);
            float mHeadingFontSize = 20.0f;
            float mValueFontSize = 26.0f;

            /**
             * How to USE FONT....
             */
            BaseFont urName = BaseFont.createFont("res/font/opensanssemibold.ttf", "UTF-8", BaseFont.EMBEDDED);
            BaseFont title = BaseFont.createFont("res/font/proximanovaregular.otf", "UTF-8", BaseFont.EMBEDDED);
            // LINE SEPARATOR
            LineSeparator lineSeparator = new LineSeparator();
            lineSeparator.setLineColor(new BaseColor(0, 0, 0, 68));

            // Title Order Details...
            // Adding Title....
            Font mOrderDetailsTitleFont = new Font(title, 20.0f, Font.BOLD, BaseColor.BLACK);
            Chunk mOrderDetailsTitleChunk = new Chunk("|| Shree Ganesh ||", mOrderDetailsTitleFont);
            Paragraph mOrderDetailsTitleParagraph = new Paragraph(mOrderDetailsTitleChunk);
            mOrderDetailsTitleParagraph.setAlignment(Element.ALIGN_CENTER);
            document.add(mOrderDetailsTitleParagraph);

            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));
            // Fields of Order Details...
            // Adding Chunks for Title and value

            Font font = FontFactory.getFont(FontFactory.HELVETICA, 26, Font.BOLD);
            ZapfDingbatsList zapfDingbatsList2 =
                    new ZapfDingbatsList(43, 30);

            zapfDingbatsList2.add(new ListItem("Personal Details", font));
            document.add(zapfDingbatsList2);

            // Adding Line Breakable Space....
            document.add(new Paragraph(""));
            // Adding Horizontal Line...

            // Adding Line Breakable Space....
            document.add(new Paragraph(""));

            // Fields of Order Details...

            Font valueFont = new Font(urName, 20, Font.BOLD, mColorAccent);
            Font keyFont = new Font(urName, 20, Font.BOLD, BaseColor.BLACK);
            Paragraph mOrderDateValueParagraph = new Paragraph();
            mOrderDateValueParagraph.setSpacingBefore(30);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Name: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" Mahesh Ashok Kharat", valueFont));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("DOB: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" 13-08-1995", valueFont));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Birth Place: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" Gursale, Tal. Pandharpur.", valueFont));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Rashi: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" Lion", valueFont));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Height: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" 5.10 Ft", valueFont));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Blood Group: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" O+", valueFont));
            document.add(mOrderDateValueParagraph);


            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Color: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" Savala", valueFont));
            document.add(mOrderDateValueParagraph);

            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            document.add(new Paragraph(""));
            document.add(new Chunk(lineSeparator));

            com.itextpdf.text.List education = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            education.add(new ListItem("Eduction", font));
            education.setIndentationLeft(20);
            document.add(education);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Education: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" BCA(Bachelor in Computer Application)", valueFont));
            document.add(mOrderDateValueParagraph);

            Font valueFontSub = new Font(urName, 15, Font.BOLD, mColorAccent);
            Font keyFontSub = new Font(urName, 15, Font.BOLD, BaseColor.BLACK);
            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(10);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("College: ", keyFontSub));
            mOrderDateValueParagraph.add(new Chunk(" Sangola Collage, Sangola (Yr 2013-2016)", valueFontSub));
            document.add(mOrderDateValueParagraph);

            com.itextpdf.text.List job = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            job.add(new ListItem("Occupation", font));
            job.setIndentationLeft(20);
            document.add(job);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Name: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" Software Engineer (Python Developer)", valueFont));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(10);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Company: ", keyFontSub));
            mOrderDateValueParagraph.add(new Chunk(" Ambrosial Techoffring Pvt. LTD.(Magarpatta City, Pune)", valueFontSub));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setSpacingAfter(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Annual Income: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" 5,16,000.00", valueFont));
            document.add(mOrderDateValueParagraph);
            document.add(new Chunk(lineSeparator));

            zapfDingbatsList2 = new ZapfDingbatsList(43, 30);
            zapfDingbatsList2.add(new ListItem("Family Details", font));
            document.add(zapfDingbatsList2);
            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Father: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" Ashok Dhula Kharat", valueFont));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(10);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Mother: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" Laxmi Ashok Kharat", valueFont));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(10);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Father Occupation: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" Farming", valueFont));
            document.add(mOrderDateValueParagraph);

            com.itextpdf.text.List jo = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            jo.add(new ListItem("Dewak: Pach Palawa", font));
            jo.setIndentationLeft(20);
            document.add(jo);
            com.itextpdf.text.List job2 = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            job2.add(new ListItem("Sisters:", font));
            job2.setIndentationLeft(20);
            document.add(job2);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("1. Bhagyashree Shirish Kokare", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" (Solapur)", valueFontSub));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("2. Urmila Eknath Kale", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" (Bembale, Pandharpur)", valueFontSub));
            document.add(mOrderDateValueParagraph);


            com.itextpdf.text.List job3 = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            job3.add(new ListItem("Brothers: None", font));
            job3.setIndentationLeft(20);
            document.add(job3);

            com.itextpdf.text.List job4 = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            job4.add(new ListItem("Mama:", font));
            job4.setIndentationLeft(20);
            document.add(job4);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("1. Laxman Sadashiv Khandekar", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" (Ropale, Madha)", valueFontSub));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("2. Uttreshwar Sadashiv Khandekar", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" (Ropale, Madha)", valueFontSub));
            document.add(mOrderDateValueParagraph);

            com.itextpdf.text.List job5 = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            job5.add(new ListItem("Other Relatives:", font));
            job5.setIndentationLeft(20);
            document.add(job5);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("( Salgar, Gavade, Gadhave, Lawate, Hegade, Tarate..etc)", keyFont));
            document.add(mOrderDateValueParagraph);

            com.itextpdf.text.List job6 = new com.itextpdf.text.List(com.itextpdf.text.List.UNORDERED);
            job6.add(new ListItem("Full Address:", font));
            job6.setIndentationLeft(20);
            document.add(job6);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk(" Ashok Dhula Kharat ", keyFont));
            document.add(mOrderDateValueParagraph);
            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(5);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk(" AP- Gursale, Tal- Pandharpur, Dist- Solapur", keyFont));
            document.add(mOrderDateValueParagraph);
            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(5);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk(" Mo. 9604554815, ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" Mo. 7719846014", keyFont));
            document.add(mOrderDateValueParagraph);



            Paragraph p = new Paragraph();
            Chunk c = new Chunk("Photos -->");
            p.setAlignment(Element.ALIGN_CENTER);
            p.add(c);
            try {
                InputStream ims = context.getAssets().open("mak.jpg");
                Bitmap bmp = BitmapFactory.decodeStream(ims);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image i = Image.getInstance(stream.toByteArray());
                i.setAlignment(Element.ALIGN_CENTER);
                i.scaleToFit(400f, 600f);
                p.add(i);

            }catch (Exception e){
                e.printStackTrace();
            }

            document.add(p);

            document.close();

            FileUtils.openFile(context, new File(dest));

        } catch (IOException | DocumentException ie) {
            LOGE("createPdf: Error " + ie.getLocalizedMessage());
        } catch (ActivityNotFoundException ae) {
            Toast.makeText(context, "No application found to open this file.", Toast.LENGTH_SHORT).show();
        }
    }
}
