package com.nikvay.drnitingroup;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.nikvay.drnitingroup.fragment.ActiveTestsTabFragment;
import com.nikvay.drnitingroup.fragment.AttemptedTestsTabFragment;
import com.nikvay.drnitingroup.fragment.UpcommingTestsTabFragment;
import com.nikvay.drnitingroup.modales.TestData;
import com.nikvay.drnitingroup.utilities.Constant;
import com.nikvay.drnitingroup.utilities.UserSession;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TestsList extends AppCompatActivity {

    TextView customToast;
    LayoutInflater inflater;
    View tostLayout;
    Fragment contentFragment;
    RecyclerView mRecyclerView;
    ArrayList<TestData> allClassData = new ArrayList<>();
    Toolbar toolbar;
    public static String CLASS_ID ="";
    public static String CLASS_NAME ="";
    public static String DIVISION_ID ="";
    UserSession userSession;
    SharedPreferences sharedPreferences;
    FloatingActionButton fab;
    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tests);
        sharedPreferences = getSharedPreferences("User", MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());
        ((TextView)findViewById(R.id.tvTitle)).setText(CLASS_NAME);
        setupToolbar();
        Constant.IS_PAPER = false;
        Constant.ADD_Q_IN_PAPER = false;
        Constant.DELETE_Q_IN_PAPER = false;
        setupViewPager();

        setupCollapsingToolbar();
        /**setContentView(R.layout.activity_tests_list);
         mRecyclerView = findViewById(R.id.scheduledTest);
         ((TextView)findViewById(R.id.tvTitle)).setText("Scheduled Tests");
         getTestList();
         ScheduledTestsAdapter adapter1 = new ScheduledTestsAdapter(this, allClassData, false);
         mRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
         mRecyclerView.setAdapter(adapter1);
         FloatingActionButton fab = findViewById(R.id.fab);
         fab.setOnClickListener(new View.OnClickListener() {
        @Override public void onClick(View view) {
        //                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
        //                        .setAction("Action", null).show();
        startActivity(new Intent(TestsList.this, NewTestActivity.class));
        overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
        }
        });*/
        fab = findViewById(R.id.fab);
        if (!RootActivity.hasPermissionToCreate) {
            fab.setVisibility(View.GONE);
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
                NewTestActivity.isTest = true;
                NewTestActivity.classId = CLASS_ID;
                startActivity(new Intent(TestsList.this, NewTestActivity.class));
                overridePendingTransition(R.anim.leftside_in, R.anim.leftside_out);
            }
        });

    }


    private void setupCollapsingToolbar() {
        final CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(
                R.id.htab_collapse_toolbar);

        collapsingToolbar.setTitleEnabled(false);
    }

    private void setupToolbar() {
        toolbar = (Toolbar) findViewById(R.id.htab_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Tests");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setupViewPager() {
        final ViewPager viewPager = (ViewPager) findViewById(R.id.htab_viewpager);
        setupViewPager(viewPager);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.htab_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new ActiveTestsTabFragment(), "ACTIVE");
        adapter.addFrag(new UpcommingTestsTabFragment(), "UPCOMING");
        adapter.addFrag(new AttemptedTestsTabFragment(), "ATTEMPTED");

        viewPager.setAdapter(adapter);
    }

    static class ViewPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);


    }
}
