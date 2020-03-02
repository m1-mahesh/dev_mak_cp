package com.mak.classportal.utilities;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class Constant {
    public static final String CLOSE = "Close";
    public static final String BUILDING = "Home";
    public static final String BOOK = "Subjects";
    public static final String PAINT = "Papers";
    public static final String CASE = "Exams";
    public static final String SHOP = "Results";
    public static final String NOTICE = "Notice";
    public static final String MOVIE = "Video";
    public static final String TAKE_ATTENDENCE = "Take Attendance";
    public static final String TAKE_TEST = "Schedule Test";
    public static final String TEST_RESULT = "Test Result";
    public static final String SELECT_SUBJECT = "Select Subject";
    public static final String SELECT_CHAPTER = "Select Chapter";
    public static final String SELECT_QUESTION = "Select Question";
    public static final String HOMEWORK = "Homwork";
    public static final String TIME_TABLE = "TimeTable";
    public static final String PAPER = "Generate Papers";

    public static final String ROLE_STUDENT = "Student";
    public static final String ROLE_TEACHER = "Teacher";
    public static final String ROLE_ADMIN = "Admin";
    public static boolean IS_PAPER = false;
    public static int ACTIVITY_FINISH_REQUEST_CODE = 1919;
    public static int ACTIVITY_BACK_REQUEST_CODE = 2929;
    public static Calendar startTime = null;


    public static int RESULT_LOAD_IMAGE = 100;
    public static int CAMERA_REQUEST = 200;
    public static int PICKFILE_RESULT_CODE = 300;

    public final static int TAB_INDEX_0 = 0;
    public final static int TAB_INDEX_1 = 1;
    public final static int TAB_INDEX_2 = 2;

    public static final String NOTICE_TYPE_DIVISION = "divisions";
    public static final String NOTICE_TYPE_STUDENTS = "students";
    public static final String NOTICE_TYPE_ALL = "all";
    /*
        Student attempted test with result
        Teacher Created List with class name and id
        Division data with single letter
        Test questions not coming
        Active and upcoming test for student

    */
   public static HashMap<String, String> mediaTypes = new HashMap<>();



}
