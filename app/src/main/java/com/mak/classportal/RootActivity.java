package com.mak.classportal;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
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
import com.mak.classportal.fragment.ContentFragment;
import com.mak.classportal.fragment.HomeworkFragment;
import com.mak.classportal.fragment.NoticeFragment;
import com.mak.classportal.fragment.PaperListFragment;
import com.mak.classportal.fragment.ProfileFragment;
import com.mak.classportal.fragment.SubjectFragment;
import com.mak.classportal.fragment.TestResultFragment;
import com.mak.classportal.fragment.TimeTableFragment;
import com.mak.classportal.fragment.VideosFragment;
import com.mak.classportal.permission.PermissionsActivity;
import com.mak.classportal.permission.PermissionsChecker;
import com.mak.classportal.utilities.Constant;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_root);
        checker = new PermissionsChecker(this);
        if (checker.lacksPermissions(REQUIRED_PERMISSION)) {
            PermissionsActivity.startActivityForResult(this, PERMISSION_REQUEST_CODE, REQUIRED_PERMISSION);
        }
        initializeViews();
        toggleDrawer();
        initializeDefaultFragment(savedInstanceState,0);
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
        /*
        navigationView.getMenu().clear();
        navigationView.inflateMenu(R.menu.menu_view);
        */
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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        switch (menuItem.getItemId()){
            case R.id.attendance:
                ClassFragment.menuId = "";
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new ClassFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.homework:
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new HomeworkFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.notice:
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new NoticeFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.videos:
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new VideosFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.testSchedule:
                ClassFragment.menuId = Constant.TAKE_TEST;
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new ClassFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.testResult:
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new TestResultFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.timeTable:
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new TimeTableFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.paper:
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new PaperListFragment())
                        .commit();
                closeDrawer();
                break;
            case R.id.profile:
                getSupportFragmentManager().beginTransaction().replace(R.id.framelayout_id, new ProfileFragment())
                        .commit();
                closeDrawer();
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
