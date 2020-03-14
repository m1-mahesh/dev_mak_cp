package com.mak.classportal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.ExecuteAPI;
import com.mak.classportal.utilities.UserSession;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText userId;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    AppSingleTone appSingleTone;
    UserSession userSession;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        userId = findViewById(R.id.username_edit_text);
        appSingleTone = new AppSingleTone(this);
        /*
        This activity is created and opened when SplashScreen finishes its animations.
        To ensure a smooth transition between activities, the activity creation animation
        is removed.
        RelativeLayout with EditTexts and Button is animated with a default fade in.
         */

        overridePendingTransition(0,0);
        View relativeLayout=findViewById(R.id.login_container);
        Animation animation= AnimationUtils.loadAnimation(this,android.R.anim.fade_in);
        relativeLayout.startAnimation(animation);
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
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(tostLayout);
        toast.show();
    }
    boolean validateFields(){

        if (!userId.getText().toString().equals("")){
            return true;
        }else{
            showToast("'Mobile Number' should not be empty, Please Enter UserId");
        }
        return false;
    }
    public void loginView(View view) {

        if (validateFields()) {

            try {
                String url = appSingleTone.signIn;

                ExecuteAPI executeAPI = new ExecuteAPI(this, url, null);
                executeAPI.addPostParam("mobile", userId.getText().toString());
                executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                    @Override
                    public void onResponse(JSONObject result) {
                        Log.d("Result", result.toString());
                        try {
                            OtpActivity.MOBILE_NUMBER = userId.getText().toString();
                            Intent intent = new Intent(LoginActivity.this, OtpActivity.class);
                            LoginActivity.this.startActivity(intent);
                            overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                            finish();
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
    }
}
