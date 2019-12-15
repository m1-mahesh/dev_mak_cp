package com.mak.classportal.utilities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.core.content.ContextCompat;

import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static androidx.core.app.ActivityCompat.requestPermissions;


/**
 * Created by ambrosial on 16/6/17.
 */

public class AppSingleTone {

    //    https://github.com/wasabeef/awesome-android-ui
    public String baseUrl = "https://dev.api.patients.migenesys.com/";
    public String signIn = "https://dev.api.common.migenesys.com/iam/authenticate";
    public String getPatientAccount = baseUrl + "accounts/";
    public String getPatientConsent = baseUrl + "accounts/%s/consents";
    public String getCountryLookups = "https://dev.api.common.migenesys.com/metadata/country_lookups";
    public String getProviderOrg = "https://dev.api.providers.migenesys.com/orgs/%s";
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

    public void getPatientAccount() {
    }
}
