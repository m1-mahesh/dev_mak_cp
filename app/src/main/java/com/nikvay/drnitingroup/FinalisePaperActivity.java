package com.nikvay.drnitingroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.res.ResourcesCompat;

import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.nikvay.drnitingroup.permission.PermissionsActivity;
import com.nikvay.drnitingroup.permission.PermissionsChecker;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.FileUtils;

import static com.nikvay.drnitingroup.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.nikvay.drnitingroup.permission.PermissionsChecker.REQUIRED_PERMISSION;

public class FinalisePaperActivity extends AppCompatActivity {

    PermissionsChecker checker;
    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    AppSingleTone appSingleTone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_finalise_paper);
        appSingleTone = new AppSingleTone(this);
        checker = new PermissionsChecker(this);
        if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
            PermissionsActivity.startActivityForResult(this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
        }
        CardView cardView = findViewById(R.id.paperCard);
        cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("Downloading...");
                appSingleTone.createPdf(FileUtils.getAppPath(getBaseContext()) + "paper.pdf");
            }
        });
        ((Button) findViewById(R.id.saveButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(tostLayout);
        toast.show();
    }
}
