package com.mak.classportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.mak.classportal.fragment.ClassFragment;
import com.mak.classportal.fragment.CompactCalendarFragment;
import com.mak.classportal.fragment.DashboardFragment;
import com.mak.classportal.fragment.HomeworkFragment;
import com.mak.classportal.fragment.NoticeFragment;
import com.mak.classportal.fragment.PaperListFragment;
import com.mak.classportal.fragment.ProfileFragment;
import com.mak.classportal.fragment.TestResultFragment;
import com.mak.classportal.fragment.TimeTableFragment;
import com.mak.classportal.fragment.VideosFragment;
import com.mak.classportal.permission.PermissionsActivity;
import com.mak.classportal.permission.PermissionsChecker;
import com.mak.classportal.utilities.AppSingleTone;
import com.mak.classportal.utilities.Constant;
import com.mak.classportal.utilities.FileUtils;
import com.mak.classportal.utilities.UserSession;

import static com.mak.classportal.permission.PermissionsActivity.PERMISSION_REQUEST_CODE;
import static com.mak.classportal.permission.PermissionsChecker.REQUIRED_PERMISSION;

public class RootActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private FrameLayout frameLayout;
    private NavigationView navigationView;
    PermissionsChecker checker;
    SharedPreferences sharedPreferences;
    UserSession userSession;
    AppSingleTone appSingleTone;
    public static boolean hasPermissionToCreate = false;
    public static boolean hasPermissionToView= false;
    public static boolean hasPermissionToDelete= false;
    public static boolean isTeacher= false;
    public static boolean isStudent= false;
    public static int defaultMenu= 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        checker = new PermissionsChecker(this);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        appSingleTone = new AppSingleTone(this);
        if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    PermissionsActivity.startActivityForResult(RootActivity.this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
                }
            }, 2000);

        }
        initializeViews();
        toggleDrawer();
        initializeDefaultFragment(savedInstanceState,defaultMenu);
        initiatePermissions();
//        appSingleTone.createMyPdf(FileUtils.getAppPath(this) + "medata.pdf");
    }
     /* Initialize all widgets
     */
    private void initializeViews() {
        toolbar = findViewById(R.id.toolbar_id);
        toolbar.setTitle(R.string.app_name);
        setSupportActionBar(toolbar);
        drawerLayout = findViewById(R.id.drawer_layout_id);
        frameLayout = findViewById(R.id.framelayout_id);
        navigationView = findViewById(R.id.navigationview_id);
        navigationView.setItemIconTintList(null);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.removeHeaderView(navigationView.getHeaderView(0));
        View hView = null;
        if (userSession.isTeacher()) {
            navigationView.inflateHeaderView(R.layout.teacher_header_layout);
            hView = navigationView.getHeaderView(0);
        }
        else if (userSession.isStudent()){
            navigationView.inflateHeaderView(R.layout.nav_header_layout);
            hView = navigationView.getHeaderView(0);
            ((TextView) hView.findViewById(R.id.classText)).setText(userSession.getAttribute("class_name"));
            ((TextView) hView.findViewById(R.id.division)).setText(userSession.getAttribute("division"));
        }

        try {
            ((TextView) hView.findViewById(R.id.nav_header_name_id)).setText(userSession.getAttribute("name"));
            ((TextView) hView.findViewById(R.id.nav_header_emailaddress_id)).setText(userSession.getAttribute("email"));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Checks if the savedInstanceState is null - onCreate() is ran
     * If so, display fragment of navigation drawer menu at position itemIndex and
     * set checked status as true
     * @param savedInstanceState
     * @param itemIndex
     */
    private void initializeDefaultFragment(Bundle savedInstanceState, int itemIndex){
        if (savedInstanceState == null){
            MenuItem menuItem = navigationView.getMenu().getItem(itemIndex).setChecked(true);
            onNavigationItemSelected(menuItem);
        }
    }

    /**
     * Creates an instance of the ActionBarDrawerToggle class:
     * 1) Handles opening and closing the navigation drawer
     * 2) Creates a hamburger icon in the toolbar
     * 3) Attaches listener to open/close drawer on icon clicked and rotates the icon
     */
    private void toggleDrawer() {
        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
    }

    @Override
    public void onBackPressed() {
        //Checks if the navigation drawer is open -- If so, close it
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // If drawer is already close -- Do not override original functionality
        else {
            super.onBackPressed();
        }
    }
    private void initiatePermissions(){
        if (userSession.isStudent()){
            hasPermissionToCreate = false;
            hasPermissionToView = false;
        }else if (userSession.isTeacher()){
            hasPermissionToCreate = true;
            hasPermissionToView = true;
        }

        isStudent = userSession.isStudent();
        isTeacher = userSession.isTeacher();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.attendance:
                ClassFragment.menuId = "";
                toolbar.setTitle("Attendance");
                if (userSession.isStudent())
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new CompactCalendarFragment())
                            .commit();
                else if (userSession.isTeacher())
                    getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new ClassFragment())
                            .commit();
                closeDrawer();
                break;
            case R.id.homework:
                toolbar.setTitle("Homework");
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new HomeworkFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.notice:
                toolbar.setTitle("Notice");
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new NoticeFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.videos:
                toolbar.setTitle("Videos");
                /*getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new VideosFragment())
                        .commit();*/
                startActivity(new Intent(RootActivity.this, ViewAttendanceActivity.class));
                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                closeDrawer();
                break;
            case R.id.testSchedule:
                if (userSession.isStudent()){
                    TestsList.CLASS_ID = userSession.getAttribute("class_id");
                    TestsList.CLASS_NAME = userSession.getAttribute("class_name");
                    TestsList.DIVISION_ID = userSession.getAttribute("division_id");
                    startActivity(new Intent(RootActivity.this, TestsList.class));
                    overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }else {
                    startActivity(new Intent(RootActivity.this, TestsList.class));
                    overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
                }
                closeDrawer();
                break;
            case R.id.testResult:
//                toolbar.setTitle("Results");
//                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new TestResultFragment())
//                        .commit();
//                closeDrawer();
                break;
            case R.id.timeTable:
                toolbar.setTitle("Time Table");
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new TimeTableFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.paper:
                toolbar.setTitle("Board Papers");
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new PaperListFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.profile:
                toolbar.setTitle("Profile");
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new ProfileFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.dashboard:
                toolbar.setTitle("Dashboard");
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new DashboardFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.logout:
                userSession.userLogout();
                startActivity(new Intent(RootActivity.this, LoginActivity.class));
                overridePendingTransition(R.anim.leftside_out, R.anim.leftside_in);
                break;

        }
        return true;
    }

    /**
     * Checks if the navigation drawer is open - if so, close it
     */
    private void closeDrawer(){
        if (drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Iterates through all the items in the navigation menu and deselects them:
     * removes the selection color
     */
    private void deSelectCheckedState(){
        int noOfItems = navigationView.getMenu().size();
        for (int i=0; i<noOfItems;i++){
            navigationView.getMenu().getItem(i).setChecked(false);
        }
    }
    void showToast(String toastText) {
        inflater = getLayoutInflater();
        tostLayout = inflater.inflate(R.layout.toast_layout_file,
                (ViewGroup) findViewById(R.id.toast_layout_root));
        customToast = tostLayout.findViewById(R.id.text);
        Toast toast = new Toast(getApplicationContext());
        customToast.setText(toastText);
        customToast.setTypeface(ResourcesCompat.getFont(this, R.font.opensansregular));
        toast.setGravity(Gravity.BOTTOM, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(tostLayout);
        toast.show();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == PermissionsActivity.PERMISSIONS_GRANTED) {
            showToast("Permission Granted to Save");
        } else {
            showToast("Permission not granted, Try again!");
        }
    }
}
