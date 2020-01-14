package com.mak.classportal;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.mak.classportal.utilities.ExecuteAPI;

import org.json.JSONObject;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
    public void loginOn(View view){
        Intent intent = new Intent(LoginActivity.this, RootActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
        finish();
        try {
            String url = "http://nikvay.com/demo/schoolApp/ws-login";
            JSONObject object = new JSONObject();
            object.put("user_name", "student1");
            object.put("password", "123456");
            ExecuteAPI executeAPI = new ExecuteAPI(this, url, object);
            executeAPI.addHeader("appToken", "eyJ1bmlxAiOiJKV1QiLCJZVN0YW1wIjoiMjAyMC");
            executeAPI.addHeader("role_id", "Student");
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());

                }

                @Override
                public void onErrorResponse(VolleyError result, int mStatusCode, JSONObject errorResponse) {
                    Log.d("Result", errorResponse.toString());
                }
            });
            executeAPI.showProcessBar(true);
            executeAPI.execute(Request.Method.POST);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
