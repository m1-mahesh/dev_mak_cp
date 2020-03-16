package com.nikvay.drnitingroup.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.nikvay.drnitingroup.R;
import com.nikvay.drnitingroup.utilities.AppSingleTone;
import com.nikvay.drnitingroup.utilities.ExecuteAPI;
import com.nikvay.drnitingroup.utilities.UserSession;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class CompactCalendarFragment extends Fragment {

    private static final String TAG = "Calender";
    private Calendar currentCalender = Calendar.getInstance(Locale.getDefault());
    private SimpleDateFormat dateFormatForDisplaying = new SimpleDateFormat("dd-M-yyyy hh:mm:ss a", Locale.getDefault());
    private SimpleDateFormat dateFormatForMonth = new SimpleDateFormat("MMM - yyyy", Locale.getDefault());
    private SimpleDateFormat monthFormat = new SimpleDateFormat("MM", Locale.getDefault());
    private boolean shouldShow = false;
    private CompactCalendarView compactCalendarView;

    AppSingleTone appSingleTone;
    SharedPreferences sharedPreferences;
    UserSession userSession;
    TextView totalAbsentText, totalPresentText;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View mainTabView = inflater.inflate(R.layout.calendar_activity,container,false);

        appSingleTone = new AppSingleTone(getContext());
        sharedPreferences = getContext().getSharedPreferences("User", Context.MODE_PRIVATE);
        userSession = new UserSession(sharedPreferences, sharedPreferences.edit());

        totalAbsentText = mainTabView.findViewById(R.id.absentCount);
        totalPresentText = mainTabView.findViewById(R.id.presentCount);

        final List<String> mutableBookings = new ArrayList<>();

        final ImageView showPreviousMonthBut = mainTabView.findViewById(R.id.prev_button);
        final ImageView showNextMonthBut = mainTabView.findViewById(R.id.next_button);
        TextView calTitle = mainTabView.findViewById(R.id.calTitle);


        compactCalendarView = mainTabView.findViewById(R.id.compactcalendar_view);

        // below allows you to configure color for the current day in the month
        // compactCalendarView.setCurrentDayBackgroundColor(getResources().getColor(R.color.black));
        // below allows you to configure colors for the current day the user has selected
        compactCalendarView.setCurrentSelectedDayBackgroundColor(getResources().getColor(R.color.colorPrimary));
        compactCalendarView.setUseThreeLetterAbbreviation(false);
//        compactCalendarView.setFirstDayOfWeek(Calendar.MONDAY);
        compactCalendarView.setIsRtl(false);
        compactCalendarView.displayOtherMonthDays(false);
        //compactCalendarView.setIsRtl(true);



        // below line will display Sunday as the first day of the week
        // compactCalendarView.setShouldShowMondayAsFirstDay(false);

        // disable scrolling calendar
        // compactCalendarView.shouldScrollMonth(false);

        // show days from other months as greyed out days
        // compactCalendarView.displayOtherMonthDays(true);

        // show Sunday as first day of month
        // compactCalendarView.setShouldShowMondayAsFirstDay(false);

        //set initial title
        calTitle.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));

        //set title on calendar scroll
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(Date dateClicked) {
                List<Event> bookingsFromMap = compactCalendarView.getEvents(dateClicked);
                Log.d(TAG, "inside onclick " + dateFormatForDisplaying.format(dateClicked));
                if (bookingsFromMap != null) {
                    Log.d(TAG, bookingsFromMap.toString());
                    mutableBookings.clear();
                    for (Event booking : bookingsFromMap) {
                        mutableBookings.add((String) booking.getData());
                    }
                }

            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                calTitle.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
                getStudentAttendance(Integer.parseInt(monthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth())));
            }
        });

        showPreviousMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.scrollLeft();
                calTitle.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
                getStudentAttendance(Integer.parseInt(monthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth())));
            }
        });

        showNextMonthBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compactCalendarView.scrollRight();
                calTitle.setText(dateFormatForMonth.format(compactCalendarView.getFirstDayOfCurrentMonth()));
                getStudentAttendance(Integer.parseInt(monthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth())));
            }
        });


        compactCalendarView.setAnimationListener(new CompactCalendarView.CompactCalendarAnimationListener() {
            @Override
            public void onOpened() {
            }

            @Override
            public void onClosed() {
            }
        });


        // uncomment below to show indicators above small indicator events
        // compactCalendarView.shouldDrawIndicatorsBelowSelectedDays(true);

        // uncomment below to open onCreate
        //openCalendarOnCreate(v);

        getStudentAttendance(Integer.parseInt(monthFormat.format(compactCalendarView.getFirstDayOfCurrentMonth())));
        return mainTabView;
    }

    @NonNull
    private View.OnClickListener getCalendarShowLis() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!compactCalendarView.isAnimating()) {
                    if (shouldShow) {
                        compactCalendarView.showCalendar();
                    } else {
                        compactCalendarView.hideCalendar();
                    }
                    shouldShow = !shouldShow;
                }
            }
        };
    }

    @NonNull
    private View.OnClickListener getCalendarExposeLis() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!compactCalendarView.isAnimating()) {
                    if (shouldShow) {
                        compactCalendarView.showCalendarWithAnimation();
                    } else {
                        compactCalendarView.hideCalendarWithAnimation();
                    }
                    shouldShow = !shouldShow;
                }
            }
        };
    }

    private void openCalendarOnCreate(View v) {
        final RelativeLayout layout = v.findViewById(R.id.main_content);
        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (Build.VERSION.SDK_INT < 16) {
                    layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                compactCalendarView.showCalendarWithAnimation();
            }
        });
    }

    private List<Event> getEvents(long timeInMillis) {
        return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
    }

    private void setToMidnight(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    HashMap<String, String> attendanceData = new HashMap<>();
    int totalAbsent = 0, totalPresent = 0;
    void addEvents(){
        try {
            compactCalendarView.removeAllEvents();
            Calendar calendar = Calendar.getInstance();
            Calendar todayCalendar1 = Calendar.getInstance();
            calendar.setTime(compactCalendarView.getFirstDayOfCurrentMonth());
            int day = calendar.get(Calendar.DATE);
            calendar.set(Calendar.DATE, day);
            for(int i = 0; i<currentCalender.getActualMaximum(Calendar.DATE); i ++){
                if (i !=0)
                    calendar.add(Calendar.DAY_OF_MONTH, 1);
                if (todayCalendar1.after(calendar)) {
                    String formattedDate = dateFormat.format(calendar.getTime());
                    Date date = dateFormat.parse(formattedDate);
                    boolean isPresent = false;
                    if (attendanceData.containsKey(formattedDate)) {
                        if (attendanceData.get(formattedDate).equals("0"))
                            isPresent = false;
                        else isPresent = true;
                    }
                    if (date != null && !isPresent) {
                        totalAbsent ++;
                        currentCalender.setTime(date);
                        Date firstDayOfMonth = currentCalender.getTime();
                        currentCalender.setTime(firstDayOfMonth);
                        setToMidnight(currentCalender);
                        long timeInMillis = currentCalender.getTimeInMillis();

                        List<Event> events = getEvents(timeInMillis);
                        if (events != null)
                            compactCalendarView.addEvents(events);
                    }else totalPresent++;
                }else break;
            }
            compactCalendarView.invalidate();
            totalPresentText.setText("Total Present: "+totalPresent+" days");
            totalAbsentText.setText("Total Absent: "+totalAbsent+" days");
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    void parseTestList(JSONObject jsonObject){
        try {
            attendanceData.clear();
            totalAbsent = 0;
            totalPresent = 0;
            JSONArray attendanceList = jsonObject.getJSONArray("attendances_list");
            for(int i=0; i<attendanceList.length();i++) {
                JSONObject object = attendanceList.getJSONObject(i);
                attendanceData.put(object.getString("attendance_date"), object.getString("attendance_status"));
            }
            addEvents();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        if (userSession!=null&&appSingleTone!=null)
//            getStudentAttendance("2");
    }

    public void getStudentAttendance(int month) {

        try {
            Log.e("Selected Month"+month, "");
            String url = appSingleTone.studentAttendance;
            ExecuteAPI executeAPI = new ExecuteAPI(getContext(), url, null);
            executeAPI.addHeader("Token", userSession.getAttribute("auth_token"));
            Log.e("Org id", userSession.getAttribute("org_id"));
            executeAPI.addPostParam("month", ""+month);
            executeAPI.addPostParam("student_id", userSession.getAttribute("user_id"));
            executeAPI.executeCallback(new ExecuteAPI.OnTaskCompleted() {
                @Override
                public void onResponse(JSONObject result) {
                    Log.d("Result", result.toString());
                    parseTestList(result);
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