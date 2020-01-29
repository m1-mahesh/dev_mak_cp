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
    //public String baseUrl = "https://dev.api.patients.migenesys.com/";

    public String signIn = "http://nikvay.com/demo/schoolApp/ws-login";
    public String classList = "http://nikvay.com/demo/schoolApp/ws-class-list";
    public String testList = "http://nikvay.com/demo/schoolApp/ws-online-test-list";
    public String questionList = "http://nikvay.com/demo/schoolApp/ws-online-test-question-list";

    //public String getPatientAccount = baseUrl + "accounts/";
    //public String getPatientConsent = baseUrl + "accounts/%s/consents";
    //public String getCountryLookups = "https://dev.api.common.migenesys.com/metadata/country_lookups";
    //public String getProviderOrg = "https://dev.api.providers.migenesys.com/orgs/%s";
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
            BaseColor mColorAccent = new BaseColor(0, 153, 204, 255);
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
            mOrderDateValueParagraph.add(new Chunk(" Mr. Mahesh Ashok Kharat", valueFont));
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
            mOrderDateValueParagraph.add(new Chunk(" BCA(Bachelor Computer Application)", valueFont));
            document.add(mOrderDateValueParagraph);

            Font valueFontSub = new Font(urName, 15, Font.BOLD, mColorAccent);
            Font keyFontSub = new Font(urName, 15, Font.BOLD, BaseColor.BLACK);
            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(35);
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
            mOrderDateValueParagraph.add(new Chunk(" Software Engineer", valueFont));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setIndentationLeft(35);
            mOrderDateValueParagraph.add(new Chunk("Company: ", keyFontSub));
            mOrderDateValueParagraph.add(new Chunk(" Ambrosial Techoffring PVT. LTD.(Magarpatta City, Pune)", valueFontSub));
            document.add(mOrderDateValueParagraph);

            mOrderDateValueParagraph.clear();
            mOrderDateValueParagraph.setSpacingBefore(20);
            mOrderDateValueParagraph.setSpacingAfter(20);
            mOrderDateValueParagraph.setIndentationLeft(30);
            mOrderDateValueParagraph.add(new Chunk("Annual Income: ", keyFont));
            mOrderDateValueParagraph.add(new Chunk(" 000000.00", valueFont));
            document.add(mOrderDateValueParagraph);
            document.add(new Chunk(lineSeparator));

            Paragraph p = new Paragraph();
            Chunk c = new Chunk("The MAK Image ");
            p.add(c);
            try {
                InputStream ims = context.getAssets().open("mak.jpg");
                Bitmap bmp = BitmapFactory.decodeStream(ims);
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
                Image i = Image.getInstance(stream.toByteArray());
                i.scaleToFit(300f, 400f);
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
