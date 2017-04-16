package com.uoft.gobblgobble.schedulefiller;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.uoft.gobblgobble.schedulefiller.R;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;


import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Objects;

public class display_schedulable_courses extends AppCompatActivity {


    //clientid: 520022971062-0oe9q78f1b72f917kuf8igfujb2tlman.apps.googleusercontent.com
    //client_secret: 2WpqiXlNZUxxzIhwrF_Wubbq

    String oauthCode;

    public static class flagClass
    {
        public boolean flag;
        public flagClass()
        {
            this.flag = false;
        }
    }

    public static class stringClass
    {
        public String myString;
        public stringClass() { myString = ""; }
    }

    public static class intClass
    {
        public int myInt;
        public intClass() { myInt = 0; }
    }

    public static class extendedCondensed {
        String courseCode;
        String courseTitle;
        String courseDescrip;
        String coursePrereqs;
        String courseSection;
        String courseSession;
        String lectureTimes;
        String tutorialTimes;
        String practicalTimes;
        fillCourse.largerMatchesLists myMatchInfo;

        public extendedCondensed(String courseCode, String courseDescrip, String coursePrereqs, String courseSection, String courseTitle, String courseSession, String lectureTimes, String tutorialTimes, String practicalTimes) {
            this.courseSession = courseSession;
            this.courseCode = courseCode;
            this.courseDescrip = courseDescrip;
            this.coursePrereqs = coursePrereqs;
            this.courseSection = courseSection;
            this.courseTitle = courseTitle;
            this.lectureTimes = lectureTimes;
            this.tutorialTimes = tutorialTimes;
            this.practicalTimes = practicalTimes;
        }
    }
    public static class extendedCondensedWithoutMatchInfo implements Parcelable
    {
        String courseCode;
        String courseTitle;
        String courseDescrip;
        String coursePrereqs;
        String courseSection;
        String courseSession;
        String lectureTimes;
        String tutorialTimes;
        String practicalTimes;
        public extendedCondensedWithoutMatchInfo(String courseCode, String courseDescrip, String coursePrereqs, String courseSection, String courseTitle, String courseSession, String lectureTimes, String tutorialTimes, String practicalTimes) {
            this.courseSession = courseSession;
            this.courseCode = courseCode;
            this.courseDescrip = courseDescrip;
            this.coursePrereqs = coursePrereqs;
            this.courseSection = courseSection;
            this.courseTitle = courseTitle;
            this.lectureTimes = lectureTimes;
            this.tutorialTimes = tutorialTimes;
            this.practicalTimes = practicalTimes;
        }
        public extendedCondensedWithoutMatchInfo(Parcel in)
        {
            this.courseCode = in.readString();
            this.courseTitle = in.readString();
            this.courseDescrip = in.readString();
            this.coursePrereqs = in.readString();
            this.courseSection = in.readString();
            this.courseSession = in.readString();
            this.lectureTimes = in.readString();
            this.tutorialTimes = in.readString();
            this.practicalTimes = in.readString();
        }
        @Override
        public int describeContents() {
            return 0;
        }
        @Override
        public void writeToParcel(Parcel parcel, int i) {
            parcel.writeString(courseCode);
            parcel.writeString(courseTitle);
            parcel.writeString(courseDescrip);
            parcel.writeString(coursePrereqs);
            parcel.writeString(courseSection);
            parcel.writeString(courseSession);
            parcel.writeString(lectureTimes);
            parcel.writeString(tutorialTimes);
            parcel.writeString(practicalTimes);
        }
        public static Creator<extendedCondensedWithoutMatchInfo> CREATOR = new Creator<extendedCondensedWithoutMatchInfo>() {
            @Override
            public extendedCondensedWithoutMatchInfo createFromParcel(Parcel parcel) {
                return (new extendedCondensedWithoutMatchInfo(parcel));
            }

            @Override
            public extendedCondensedWithoutMatchInfo[] newArray(int i) {
                return new extendedCondensedWithoutMatchInfo[i];
            }
        };
    }
    public static class extendedCondensedSavedExtra {
        String courseCode;
        String courseTitle;
        String courseDescrip;
        String coursePrereqs;
        String courseSection;
        String courseSession;
        String lectureTimes;
        String tutorialTimes;
        String practicalTimes;
        fillCourse.largerMatchesLists myMatchInfo;

        public extendedCondensedSavedExtra(String courseCode, String courseDescrip, String coursePrereqs, String courseSection, String courseTitle, String courseSession, String lectureTimes, String tutorialTimes, String practicalTimes, fillCourse.largerMatchesLists myMatchInfo) {
            this.courseSession = courseSession;
            this.courseCode = courseCode;
            this.courseDescrip = courseDescrip;
            this.coursePrereqs = coursePrereqs;
            this.courseSection = courseSection;
            this.courseTitle = courseTitle;
            this.lectureTimes = lectureTimes;
            this.tutorialTimes = tutorialTimes;
            this.practicalTimes = practicalTimes;
            this.myMatchInfo = myMatchInfo;
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_schedulable_courses);


        final ViewPager myPager = (ViewPager) findViewById(R.id.mainPager);

        DBhelper myHelper = new DBhelper(this);

        ArrayList<ArrayList<extendedCondensed>> pagerArray = new ArrayList<>();

        ArrayList<fillCourse.largerMatchesLists> fitsInScheduleTimes = getIntent().getParcelableArrayListExtra("coursesTimes");
        ArrayList<fillCourse.condensedCourseTotal> fitsInScheduleCondensed = getIntent().getParcelableArrayListExtra("coursesCondensed");
        ArrayList<extendedCondensed> fitsInSchedule = new ArrayList<>();
        ArrayList<extendedCondensedSavedExtra> fitsInScheduleExtraData = new ArrayList<>();

        for(int i = 0; i < fitsInScheduleCondensed.size(); i++)
        {
            int count = 1;
            String lec =  "\n\nLectures: \n";
            for(fillCourse.largerMatches nextLec : fitsInScheduleTimes.get(i).lectureLargerMatches)
            {
                if(nextLec.day.contains("X")) {
                    if(count > 1)
                    {
                        lec += "\n";
                    }
                    lec += "Lecture Option - " + Integer.toString(count) + "\n";
                    count++;
                }
                else {
                    lec += nextLec.day + " : " + nextLec.matchStartTime + " - " + nextLec.matchEndTime + "\n";
                }
            }
            count = 1;
            String tut =  "Tutorials: \n";
            for(fillCourse.largerMatches nextTut : fitsInScheduleTimes.get(i).tutorialLargerMatches)
            {
                if(nextTut.day.contains("X")) {
                    if(count > 1)
                    {
                        tut += "\n";
                    }
                    tut += "Tutorial Option - " + Integer.toString(count) + "\n";
                    count++;
                }
                else {
                    tut += nextTut.day + " : " + nextTut.matchStartTime + " - " + nextTut.matchEndTime + "\n";
                }
            }
            count = 1;
            String pra =  "Practicals: \n";
            for(fillCourse.largerMatches nextPra : fitsInScheduleTimes.get(i).practicalLargerMatches)
            {
                if(nextPra.day.contains("X")) {
                    if(count > 1)
                    {
                        pra += "\n";
                    }
                    pra += "Practical Option - " + Integer.toString(count) + "\n";
                    count++;
                }
                else {
                    pra += nextPra.day + " : " + nextPra.matchStartTime + " - " + nextPra.matchEndTime + "\n";
                }
            }
            if(fitsInScheduleTimes.get(i).lectureLargerMatches.size() == 1)
            {
                lec = "";
            }
            if(fitsInScheduleTimes.get(i).tutorialLargerMatches.size() == 1)
            {
                tut = "";
            }
            if(fitsInScheduleTimes.get(i).practicalLargerMatches.size() == 1)
            {
                pra = "";
            }
            fitsInSchedule.add(new extendedCondensed(
                    fitsInScheduleCondensed.get(i).courseCode,
                    fitsInScheduleCondensed.get(i).courseDescrip,
                    fitsInScheduleCondensed.get(i).coursePrereqs,
                    fitsInScheduleCondensed.get(i).courseSection,
                    fitsInScheduleCondensed.get(i).courseTitle,
                    fitsInScheduleCondensed.get(i).courseSession,
                    lec,
                    tut,
                    pra
            ));
            fitsInScheduleExtraData.add(new extendedCondensedSavedExtra(
                    fitsInScheduleCondensed.get(i).courseCode,
                    fitsInScheduleCondensed.get(i).courseDescrip,
                    fitsInScheduleCondensed.get(i).coursePrereqs,
                    fitsInScheduleCondensed.get(i).courseSection,
                    fitsInScheduleCondensed.get(i).courseTitle,
                    fitsInScheduleCondensed.get(i).courseSession,
                    lec,
                    tut,
                    pra,
                    fitsInScheduleTimes.get(i)
            ));

        }

        for(int i = 0; i < fitsInScheduleTimes.size(); i++)
        {
            Log.d("Course", fitsInSchedule.get(i).courseCode);
            for(fillCourse.largerMatches courseTimes : fitsInScheduleTimes.get(i).lectureLargerMatches)
            {
                Log.d("Lecture", "Start: " + courseTimes.matchStartTime + " End: " + courseTimes.matchEndTime + " Day: " + courseTimes.day);
            }
            for(fillCourse.largerMatches courseTimes : fitsInScheduleTimes.get(i).tutorialLargerMatches)
            {
                Log.d("Tutorial", "Start: " + courseTimes.matchStartTime + " End: " + courseTimes.matchEndTime + " Day: " + courseTimes.day);
            }
            for(fillCourse.largerMatches courseTimes : fitsInScheduleTimes.get(i).practicalLargerMatches)
            {
                Log.d("Practical", "Start: " + courseTimes.matchStartTime + " End: " + courseTimes.matchEndTime + " Day: " + courseTimes.day);
            }
        }

        pagerArray.add(fitsInSchedule);
        pagerArray.add(myHelper.getAllCourses());
        final customPagerAdapter myAdapter = new customPagerAdapter(pagerArray, this, myHelper, fitsInScheduleExtraData);
        myPager.setAdapter(myAdapter);

        final Button showSchedulable = (Button) findViewById(R.id.schedulableCourses);
        final Button showSaved = (Button) findViewById(R.id.savedCourses);

        showSchedulable.setBackgroundColor(Color.WHITE);
        showSaved.setBackgroundColor(Color.WHITE);
        showSchedulable.setTextColor(Color.BLACK);
        showSaved.setTextColor(Color.BLACK);

        //Toolbar topToolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(topToolbar);
        //getSupportActionBar().setTitle("");

        showSchedulable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myPager.setCurrentItem(0);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(display_schedulable_courses.this, "Completed", Toast.LENGTH_SHORT).show();
                        myAdapter.notifyDataSetChanged();
                    }
                });
            }
        });

        showSaved.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myPager.setCurrentItem(1);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(display_schedulable_courses.this, "Completed", Toast.LENGTH_SHORT).show();
                        myAdapter.notifyDataSetChanged();
                    }
                });
            }
        });



        /*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        ArrayList<fillCourse.condensedCourseTotal> fitsInSchedule = getIntent().getParcelableArrayListExtra("courses");

        MobileAds.initialize(this, "ca-app-pub-5269640446877970~6452380842");

        AdView adView = (AdView) findViewById(R.id.testAd);
        AdRequest request = new AdRequest.Builder().build();
        adView.loadAd(request);

        TextView filler = (TextView) findViewById(R.id.fillerNoCourse);
        boolean holderHasFiller = true;

        LinearLayout holder = (LinearLayout) findViewById(R.id.courseHolders);

        Toolbar topToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(topToolbar);
        getSupportActionBar().setTitle("");


        for(final fillCourse.condensedCourseTotal ourCourse : fitsInSchedule)
        {
            if(holderHasFiller) {
                holder.removeView(filler);
                holderHasFiller = false;
            }
            View toReturn = LayoutInflater.from(this).inflate(R.layout.artsci_course_info, holder, false);
            final TextView courseCode = (TextView) toReturn.findViewById(R.id.courseCode);
            final TextView courseTitle = (TextView) toReturn.findViewById(R.id.courseTitle);
            final TextView courseDescrip = (TextView) toReturn.findViewById(R.id.courseDescrip);
            final TextView coursePrereqs = (TextView) toReturn.findViewById(R.id.coursePrereqs);
            final TextView courseSection = (TextView) toReturn.findViewById(R.id.courseSection);
            final TextView courseSession = (TextView) toReturn.findViewById(R.id.courseSession);

            courseCode.setText("Code: " + ourCourse.courseCode);
            courseTitle.setText("Title: " + ourCourse.courseTitle);
            courseDescrip.setText("Descrip: " + ourCourse.courseDescrip);
            coursePrereqs.setText("Prereqs: " + ourCourse.coursePrereqs);
            courseSection.setText("Section: " + ourCourse.courseSection);
            if(ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length()-1) == 57)
            {
                int temp = Integer.parseInt(ourCourse.courseSession);
                courseSession.setText("Session: " + Integer.toString(temp+2));
            }
            else {
                courseSession.setText("Session: " + ourCourse.courseSession);
            }

            toReturn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder popUpDialog = new AlertDialog.Builder(display_schedulable_courses.this, R.style.CustomDialog);
                    popUpDialog.setMessage("More Options");
                    popUpDialog.setPositiveButton("Browser", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            if(ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length()-1) == 57)
                            {
                                int temp = Integer.parseInt(ourCourse.courseSession);
                                browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + Integer.toString(temp+2)));
                            }
                            else
                            {
                                browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + ourCourse.courseSession));
                            }
                            startActivity(browserIntent);
                        }
                    });
                    popUpDialog.setNeutralButton("Copy Info", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                            ClipData clip = ClipData.newPlainText("Copied", "Code: " +
                                    courseCode.getText().toString() +
                                    "\nTitle: " +
                                    courseTitle.getText().toString() +
                                    "\nDescription: " +
                                    courseDescrip.getText().toString() +
                                    "\nPrereqs: " +
                                    coursePrereqs.getText().toString() +
                                    "\nSession " + courseSession.getText().toString() + "-" + courseSection.getText().toString());
                            clipboard.setPrimaryClip(clip);
                            Toast newToast = new Toast(display_schedulable_courses.this);
                            newToast.makeText(display_schedulable_courses.this, "Copied!", Toast.LENGTH_SHORT).show();
                        }
                    });
                    popUpDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) { }
                    });
                    popUpDialog.show();
                    return(true);
                }
            });
            holder.addView(toReturn);
        }*/
    }

    protected class customPagerAdapter extends PagerAdapter {
        public ArrayList<ArrayList<extendedCondensed>> sharedList;
        public Context sharedContext;
        public DBhelper myHelper;
        public LinearLayout savedViewHolder;
        public TextView savedCoursesTextView;
        public ArrayList<extendedCondensedSavedExtra> matchListExtendedInfo;

        public customPagerAdapter(ArrayList<ArrayList<extendedCondensed>> sharedList, Context sharedContext, DBhelper myHelper, ArrayList<extendedCondensedSavedExtra> matchListExtendedInfo) {
            this.sharedContext = sharedContext;
            this.sharedList = sharedList;
            this.myHelper = myHelper;
            this.savedViewHolder = null;
            this.matchListExtendedInfo = matchListExtendedInfo;
        }

        @Override
        public int getCount() {
            return (sharedList.size());
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return (view == (View) object);
        }

        @Override
        public Object instantiateItem(final ViewGroup container,final int position) {
            View mainView = LayoutInflater.from(sharedContext).inflate(R.layout.pager_item, container, false);
            Toolbar toolbar = (Toolbar) mainView.findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            final ArrayList<extendedCondensed> fitsInSchedule = sharedList.get(position % sharedList.size());

            MobileAds.initialize(sharedContext, "ca-app-pub-5269640446877970~6452380842");

            AdView adView = (AdView) mainView.findViewById(R.id.testAd);
            AdRequest request = new AdRequest.Builder().build();
            adView.loadAd(request);

            TextView filler = (TextView) mainView.findViewById(R.id.fillerNoCourse);
            boolean holderHasFiller = true;

            LinearLayout rel = (LinearLayout) mainView.findViewById(R.id.relHolder);

            if(position == 1) {
                View calenderButton = LayoutInflater.from(sharedContext).inflate(R.layout.calender_login, rel, false);
                rel.addView(calenderButton);
                Button calenderLogin = (Button) calenderButton.findViewById(R.id.calenderLogin);
                calenderLogin.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(fitsInSchedule.size() == 0)
                        {
                            Toast.makeText(sharedContext, "Add courses first!", Toast.LENGTH_LONG).show();
                        }
                        else
                        {
                            ArrayList<extendedCondensedWithoutMatchInfo> calendarSyncList = new ArrayList<extendedCondensedWithoutMatchInfo>();
                            for(int i = 0; i < fitsInSchedule.size(); i++)
                            {
                                calendarSyncList.add(new extendedCondensedWithoutMatchInfo(
                                        fitsInSchedule.get(i).courseCode,
                                        fitsInSchedule.get(i).courseDescrip,
                                        fitsInSchedule.get(i).coursePrereqs,
                                        fitsInSchedule.get(i).courseSection,
                                        fitsInSchedule.get(i).courseTitle,
                                        fitsInSchedule.get(i).courseSession,
                                        fitsInSchedule.get(i).lectureTimes,
                                        fitsInSchedule.get(i).tutorialTimes,
                                        fitsInSchedule.get(i).practicalTimes
                                        ));
                            }
                            Intent i = new Intent(sharedContext, calender_sync.class);
                            i.putParcelableArrayListExtra("coursesToSync", calendarSyncList);
                            startActivityForResult(i, 1);
                        }
                    }
                });
            }


            LinearLayout holder = (LinearLayout) mainView.findViewById(R.id.courseHolders);

            //Toolbar topToolbar = (Toolbar) mainView.findViewById(R.id.toolbar);
            //setSupportActionBar(topToolbar);
            //getSupportActionBar().setTitle("");

            if(position == 1)
            {
                filler.setText("Couldn't find any saved courses :(");
            }

            final intClass counter = new intClass();
            for (final extendedCondensed ourCourse : fitsInSchedule) {
                final extendedCondensedSavedExtra ourCourseExtraInfo;
                if(position == 0) {
                    ourCourseExtraInfo = matchListExtendedInfo.get(counter.myInt);
                }
                else {
                    ourCourseExtraInfo = null;
                }
                Log.d("FitsInScheduleSize", "Size is: " + fitsInSchedule.size());
                Log.d("NewElement", "Instantiating course: " + ourCourse.courseCode);
                if (holderHasFiller) {
                    rel.removeView(filler);
                    holderHasFiller = false;
                }
                View toReturn = LayoutInflater.from(sharedContext).inflate(R.layout.artsci_course_info, holder, false);
                final TextView courseCode = (TextView) toReturn.findViewById(R.id.courseCode);
                final TextView courseTitle = (TextView) toReturn.findViewById(R.id.courseTitle);
                final TextView courseDescrip = (TextView) toReturn.findViewById(R.id.courseDescrip);
                final TextView coursePrereqs = (TextView) toReturn.findViewById(R.id.coursePrereqs);
                final TextView courseSection = (TextView) toReturn.findViewById(R.id.courseSection);
                final TextView courseSession = (TextView) toReturn.findViewById(R.id.courseSession);
                final TextView lectureTimes = (TextView) toReturn.findViewById(R.id.lectureTimes);
                final TextView tutorialTimes = (TextView) toReturn.findViewById(R.id.tutorialTimes);
                final TextView practicalTimes = (TextView) toReturn.findViewById(R.id.practicalTimes);

                final AppCompatButton expandButton = (AppCompatButton) toReturn.findViewById(R.id.expandButton);
                final LinearLayout detailsLayout = (LinearLayout) toReturn.findViewById(R.id.hideDetails);
                LinearLayout totalLayout = (LinearLayout) toReturn.findViewById(R.id.totalCourseHolder);
                final flagClass expandFlag = new flagClass();
                expandFlag.flag = false;
                detailsLayout.setVisibility(View.GONE);
                expandButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(expandFlag.flag)
                        {
                            detailsLayout.setVisibility(View.GONE);
                            expandFlag.flag = false;
                            expandButton.setText("Expand");
                        }
                        else
                        {
                            detailsLayout.setVisibility(View.VISIBLE);
                            expandFlag.flag = true;
                            expandButton.setText("Collapse");
                        }
                    }
                });
                totalLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(expandFlag.flag)
                        {
                            detailsLayout.setVisibility(View.GONE);
                            expandFlag.flag = false;
                            expandButton.setText("Expand");
                        }
                        else
                        {
                            detailsLayout.setVisibility(View.VISIBLE);
                            expandFlag.flag = true;
                            expandButton.setText("Collapse");
                        }
                    }
                });

                courseCode.setText("Code: " + ourCourse.courseCode);
                courseTitle.setText("Title: " + ourCourse.courseTitle);
                courseDescrip.setText("Descrip: " + ourCourse.courseDescrip);
                coursePrereqs.setText("Prereqs: " + ourCourse.coursePrereqs);
                courseSection.setText("Section: " + ourCourse.courseSection);
                lectureTimes.setText(ourCourse.lectureTimes);
                tutorialTimes.setText(ourCourse.tutorialTimes);
                practicalTimes.setText(ourCourse.practicalTimes);

                if (ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length() - 1) == 57) {
                    int temp = Integer.parseInt(ourCourse.courseSession);
                    courseSession.setText("Session: " + Integer.toString(temp + 2));
                } else {
                    courseSession.setText("Session: " + ourCourse.courseSession);
                }

                //if (position == 0) {
                    toReturn.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            AlertDialog.Builder popUpDialog = new AlertDialog.Builder(display_schedulable_courses.this, R.style.CustomDialog);
                            final AlertDialog cancelDialog = popUpDialog.create();
                            popUpDialog.setMessage("More Options");
                            popUpDialog.setPositiveButton("More Info", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    /*
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                    if (ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length() - 1) == 57) {
                                        int temp = Integer.parseInt(ourCourse.courseSession);
                                        browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + Integer.toString(temp + 2)));
                                    } else {
                                        browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + ourCourse.courseSession));
                                    }

                                    startActivity(browserIntent);
                                    */

                                    final AlertDialog.Builder browserDialog = new AlertDialog.Builder(display_schedulable_courses.this, R.style.CustomDialog);
                                    View webViewContent = LayoutInflater.from(display_schedulable_courses.this).inflate(R.layout.display_course_info_webview, null);
                                    browserDialog.setView(webViewContent);
                                    final Dialog cancelDialog2 = browserDialog.create();
                                    browserDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    browserDialog.setPositiveButton("Open In Browser", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                            if (ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length() - 1) == 57) {
                                                int temp = Integer.parseInt(ourCourse.courseSession);
                                                browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + Integer.toString(temp + 2)));
                                            } else {
                                                browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + ourCourse.courseSession));
                                            }
                                            startActivity(browserIntent);
                                        }
                                    });
                                    WebView courseWebView = (WebView) webViewContent.findViewById(R.id.webViewCourseInfo);
                                    WebViewClient courseWebViewClient = new WebViewClient();
                                    courseWebView.getSettings().setJavaScriptEnabled(true);
                                    //courseWebView.loadUrl();
                                    if (ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length() - 1) == 57) {
                                        int temp = Integer.parseInt(ourCourse.courseSession);
                                        courseWebView.loadUrl("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + Integer.toString(temp + 2));
                                    } else {
                                        courseWebView.loadUrl("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + ourCourse.courseSession);
                                    }
                                    browserDialog.show();

                                }
                            });
                            popUpDialog.setNeutralButton("Copy Info", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("Copied", "Code: " +
                                            courseCode.getText().toString() +
                                            "\nTitle: " +
                                            courseTitle.getText().toString() +
                                            "\nDescription: " +
                                            courseDescrip.getText().toString() +
                                            "\nPrereqs: " +
                                            coursePrereqs.getText().toString() +
                                            "\nSession " + courseSession.getText().toString() + "-" + courseSection.getText().toString());
                                    clipboard.setPrimaryClip(clip);
                                    Toast newToast = new Toast(display_schedulable_courses.this);
                                    newToast.makeText(display_schedulable_courses.this, "Copied!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            if(position == 0) {
                                popUpDialog.setNegativeButton("Add To Saved List", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        boolean addFlag = true;
                                        for (extendedCondensed checkCourse : sharedList.get(1)) {
                                            if (checkCourse.courseCode.length() == ourCourse.courseCode.length() && Objects.equals(checkCourse.courseCode, ourCourse.courseCode))
                                            {
                                                boolean innerFlag = true;
                                                for (int j = 0; j < checkCourse.courseCode.length(); j++) {
                                                    if (!Objects.equals(ourCourse.courseCode.charAt(j), checkCourse.courseCode.charAt(j))) {
                                                        innerFlag = false;
                                                        break;
                                                    }
                                                }
                                                if (innerFlag) {
                                                    addFlag = false;
                                                    Log.d("DoesContain", checkCourse.courseCode + " and " + ourCourse.courseCode);
                                                }
                                            }
                                        }
                                        if (addFlag) {
                                            final stringClass lectureOption = new stringClass();
                                            final stringClass tutorialOption = new stringClass();
                                            final stringClass practicalOption = new stringClass();
                                            final flagClass lecFlagFinish = new flagClass();
                                            final flagClass tutFlagFinish = new flagClass();
                                            final flagClass praFlagFinish = new flagClass();
                                            if(!ourCourse.lectureTimes.isEmpty()) {
                                                lectureOption.myString = "Lectures Selected: " + "\n";
                                                Log.d("Not Empty", "LECTURES NOT EMPTY");
                                                AlertDialog.Builder lecChoiceDialog = new AlertDialog.Builder(display_schedulable_courses.this);
                                                lecChoiceDialog.setTitle("Select a lecture option");
                                                ArrayList<String> lectureSelections = new ArrayList<String>();
                                                String addStringToLectureSelections = "";
                                                int countX = 0;
                                                for(fillCourse.largerMatches nextLec : ourCourseExtraInfo.myMatchInfo.lectureLargerMatches)
                                                {
                                                    boolean firstX = false;
                                                    if(nextLec.day.contains("X")) {
                                                        Log.d("XTAG", "ANOTHER X");
                                                        if(!firstX)
                                                        {
                                                            firstX = true;
                                                        }
                                                        /*if(count > 1)
                                                        {
                                                            lec += "\n";
                                                        }
                                                        lec += "Lecture Option - " + Integer.toString(count) + "\n";
                                                        count++;*/
                                                        if(countX != 0) {
                                                            lectureSelections.add(addStringToLectureSelections);
                                                            addStringToLectureSelections = "";
                                                        }
                                                        countX++;
                                                    }
                                                    else {
                                                        addStringToLectureSelections = addStringToLectureSelections + nextLec.day + " : " + nextLec.matchStartTime + " - " + nextLec.matchEndTime + "\n";
                                                        //lectureSelections.add(nextLec.day + " : " + nextLec.matchStartTime + " - " + nextLec.matchEndTime);
                                                    }
                                                }
                                                lectureSelections.add(addStringToLectureSelections);
                                                lectureSelections.add("None");
                                                final String[] lectureStrings = new String[lectureSelections.size()];
                                                final boolean[] lectureStringsTrack = new boolean[lectureSelections.size()];
                                                for (int j = 0; j < lectureSelections.size(); j++)
                                                {
                                                    lectureStrings[j] = lectureSelections.get(j);
                                                    lectureStringsTrack[j] = false;
                                                }
                                                lecChoiceDialog.setMultiChoiceItems(lectureStrings, lectureStringsTrack, new DialogInterface.OnMultiChoiceClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                                        lectureStringsTrack[i] = b;
                                                    }
                                                });
                                                lecChoiceDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        for(int k = 0; k < lectureStrings.length; k++)
                                                        {
                                                            if(lectureStringsTrack[k])
                                                            {
                                                                if(!lectureStrings[k].contains("None")) {
                                                                    lectureOption.myString = lectureOption.myString + lectureStrings[k] + "\n";
                                                                }
                                                                else
                                                                {
                                                                    Log.d("Selected None", "A none was selected: " + lectureStrings[k]);
                                                                }
                                                            }
                                                        }
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                lecChoiceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialogInterface) {
                                                        lecFlagFinish.flag = true;
                                                        if(lectureOption.myString.length() == 0)
                                                        {
                                                            for(int k = 0; k < lectureStrings.length; k++)
                                                            {
                                                                if(lectureStringsTrack[k])
                                                                {
                                                                    if(!lectureStrings[k].contains("None")) {
                                                                        lectureOption.myString = lectureOption.myString + lectureStrings[k] + "\n";
                                                                    }
                                                                    else
                                                                    {
                                                                        Log.d("Selected None", "A none was selected: " + lectureStrings[k]);
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                });
                                                lecChoiceDialog.show();
                                            }
                                            else { lecFlagFinish.flag = true; }



                                            if(!ourCourse.tutorialTimes.isEmpty()) {
                                                tutorialOption.myString = "Tutorials Selected: " + "\n";
                                                Log.d("Not Empty", "TUTORIALS NOT EMPTY");
                                                AlertDialog.Builder tutChoiceDialog = new AlertDialog.Builder(display_schedulable_courses.this);
                                                tutChoiceDialog.setTitle("Select a tutorial option");
                                                ArrayList<String> tutorialSelections = new ArrayList<String>();
                                                String addStringToTutorialSelections = "";
                                                int countX = 0;
                                                for(fillCourse.largerMatches nextTut : ourCourseExtraInfo.myMatchInfo.tutorialLargerMatches)
                                                {
                                                    if(nextTut.day.contains("X")) {
                                                        Log.d("XTAGTUT", "Value of addstring: " + addStringToTutorialSelections);
                                                        if(countX != 0) {
                                                            tutorialSelections.add(addStringToTutorialSelections);
                                                            addStringToTutorialSelections = "";
                                                        }
                                                        countX++;
                                                    }
                                                    else {
                                                        addStringToTutorialSelections = addStringToTutorialSelections + nextTut.day + " : " + nextTut.matchStartTime + " - " + nextTut.matchEndTime + "\n";
                                                        //lectureSelections.add(nextLec.day + " : " + nextLec.matchStartTime + " - " + nextLec.matchEndTime);
                                                    }
                                                }
                                                tutorialSelections.add(addStringToTutorialSelections);
                                                tutorialSelections.add("None");
                                                final String[] tutorialStrings = new String[tutorialSelections.size()];
                                                final boolean[] tutorialStringTracks = new boolean[tutorialSelections.size()];
                                                for (int j = 0; j < tutorialSelections.size(); j++)
                                                {
                                                    tutorialStrings[j] = tutorialSelections.get(j);
                                                    tutorialStringTracks[j] = false;
                                                }
                                                tutChoiceDialog.setMultiChoiceItems(tutorialStrings, tutorialStringTracks, new DialogInterface.OnMultiChoiceClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                                        tutorialStringTracks[i] = b;
                                                    }
                                                });
                                                tutChoiceDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        for(int k = 0; k < tutorialStrings.length; k++)
                                                        {
                                                            if(tutorialStringTracks[k])
                                                            {
                                                                if(!tutorialStrings[k].contains("None")) {
                                                                    tutorialOption.myString = tutorialOption.myString + tutorialStrings[k] + "\n";
                                                                }
                                                            }
                                                        }
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                tutChoiceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialogInterface) {
                                                        if(tutorialOption.myString.length() == 0)
                                                        {
                                                            for(int k = 0; k < tutorialStrings.length; k++)
                                                            {
                                                                if(tutorialStringTracks[k])
                                                                {
                                                                    if(!tutorialStrings[k].contains("None")) {
                                                                        tutorialOption.myString = tutorialOption.myString + tutorialStrings[k] + "\n";
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        tutFlagFinish.flag = true;
                                                    }
                                                });
                                                tutChoiceDialog.show();
                                            }
                                            else { tutFlagFinish.flag = true; }


                                            if(!ourCourse.practicalTimes.isEmpty()) {
                                                practicalOption.myString = "Practicals Selected: " + "\n";
                                                Log.d("Not Empty", "PRACTICALS NOT EMPTY");
                                                AlertDialog.Builder praChoiceDialog = new AlertDialog.Builder(display_schedulable_courses.this);
                                                praChoiceDialog.setTitle("Select a practical option");
                                                ArrayList<String> practicalSelections = new ArrayList<String>();
                                                String addStringToPracticalSelections = "";
                                                int countX = 0;
                                                for(fillCourse.largerMatches nextPra : ourCourseExtraInfo.myMatchInfo.practicalLargerMatches)
                                                {
                                                    boolean firstX = false;
                                                    if(nextPra.day.contains("X")) {
                                                        if(!firstX)
                                                        {
                                                            firstX = true;
                                                        }
                                                        /*if(count > 1)
                                                        {
                                                            lec += "\n";
                                                        }
                                                        lec += "Lecture Option - " + Integer.toString(count) + "\n";
                                                        count++;*/
                                                        if(countX != 0) {
                                                            practicalSelections.add(addStringToPracticalSelections);
                                                            addStringToPracticalSelections = "";
                                                        }
                                                        countX++;
                                                    }
                                                    else {
                                                        addStringToPracticalSelections = addStringToPracticalSelections + nextPra.day + " : " + nextPra.matchStartTime + " - " + nextPra.matchEndTime + "\n";
                                                        //lectureSelections.add(nextLec.day + " : " + nextLec.matchStartTime + " - " + nextLec.matchEndTime);
                                                    }
                                                }
                                                practicalSelections.add(addStringToPracticalSelections);
                                                practicalSelections.add("None");
                                                final String[] practicalStrings = new String[practicalSelections.size()];
                                                final boolean[] practicalStringTracks = new boolean[practicalSelections.size()];
                                                for (int j = 0; j < practicalSelections.size(); j++)
                                                {
                                                    practicalStrings[j] = practicalSelections.get(j);
                                                    practicalStringTracks[j] = false;
                                                }
                                                praChoiceDialog.setMultiChoiceItems(practicalStrings, practicalStringTracks, new DialogInterface.OnMultiChoiceClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                                                        practicalStringTracks[i] = b;
                                                    }
                                                });
                                                praChoiceDialog.setPositiveButton("Done", new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialogInterface, int i) {
                                                        for(int k = 0; k < practicalStrings.length; k++)
                                                        {
                                                            if(practicalStringTracks[k])
                                                            {
                                                                if(!practicalStrings[k].contains("None"))
                                                                {
                                                                    practicalOption.myString = practicalOption.myString + practicalStrings[k] + "\n";
                                                                }
                                                            }
                                                        }
                                                        dialogInterface.dismiss();
                                                    }
                                                });
                                                praChoiceDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                                    @Override
                                                    public void onDismiss(DialogInterface dialogInterface) {
                                                        if(practicalOption.myString.length() == 0)
                                                        {
                                                            for(int k = 0; k < practicalStrings.length; k++)
                                                            {
                                                                if(practicalStringTracks[k])
                                                                {
                                                                    if(!practicalStrings[k].contains("None"))
                                                                    {
                                                                        practicalOption.myString = practicalOption.myString + practicalStrings[k] + "\n";
                                                                    }
                                                                }
                                                            }
                                                        }
                                                        praFlagFinish.flag = true;
                                                    }
                                                });
                                                praChoiceDialog.show();
                                            }
                                            else { praFlagFinish.flag = true; }


                                            Thread waitForDialogThread = new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    while(!(tutFlagFinish.flag && lecFlagFinish.flag && praFlagFinish.flag))
                                                    {
                                                        try
                                                        {
                                                            Thread.sleep(50);
                                                        }
                                                        catch (InterruptedException e)
                                                        {

                                                        }

                                                    }
                                                    extendedCondensed newCourseToAdd = new extendedCondensed(
                                                            ourCourse.courseCode,
                                                            ourCourse.courseDescrip,
                                                            ourCourse.coursePrereqs,
                                                            ourCourse.courseSection,
                                                            ourCourse.courseTitle,
                                                            ourCourse.courseSession,
                                                            lectureOption.myString,
                                                            tutorialOption.myString,
                                                            practicalOption.myString
                                                    );
                                                    sharedList.get(1).add(newCourseToAdd);
                                                    //ADD TO DATABASE
                                                    myHelper.insertCourse(newCourseToAdd);

                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            reinstantiateSavedViews(sharedList.get(1));
                                                        }
                                                    });

                                                }
                                            });
                                            waitForDialogThread.start();
                                            /*
                                            extendedCondensed newCourseToAdd = new extendedCondensed(
                                                    ourCourse.courseCode,
                                                    ourCourse.courseDescrip,
                                                    ourCourse.coursePrereqs,
                                                    ourCourse.courseSection,
                                                    ourCourse.courseTitle,
                                                    ourCourse.courseSession,
                                                    lectureOption.myString,
                                                    tutorialOption.myString,
                                                    practicalOption.myString
                                            );
                                            sharedList.get(1).add(newCourseToAdd);
                                            //ADD TO DATABASE
                                            myHelper.insertCourse(newCourseToAdd);
                                            */
                                        }
                                        else {
                                            reinstantiateSavedViews(sharedList.get(1));
                                        }
                                    }
                                });
                            }
                            else
                            {
                                popUpDialog.setNegativeButton("Remove From Saved List", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        if(sharedList.get(1).indexOf(ourCourse) >= 0) {
                                            sharedList.get(1).remove(sharedList.get(1).indexOf(ourCourse));
                                        }
                                        //REMOVE FROM DATABASE
                                        myHelper.deleteCourse(ourCourse.courseCode);
                                        reinstantiateSavedViews(sharedList.get(1));
                                    }
                                });
                            }
                            popUpDialog.show();
                            return (true);
                        }
                    });
                //} else {
                   /* toReturn.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View view) {
                            AlertDialog.Builder popUpDialog = new AlertDialog.Builder(display_schedulable_courses.this, R.style.CustomDialog);
                            popUpDialog.setMessage("More Options");
                            popUpDialog.setPositiveButton("Browser", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                    if (ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length() - 1) == 57) {
                                        int temp = Integer.parseInt(ourCourse.courseSession);
                                        browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + Integer.toString(temp + 2)));
                                    } else {
                                        browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + ourCourse.courseSession));
                                    }
                                    startActivity(browserIntent);
                                }
                            });
                            popUpDialog.setNeutralButton("Copy Info", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                    ClipData clip = ClipData.newPlainText("Copied", "Code: " +
                                            courseCode.getText().toString() +
                                            "\nTitle: " +
                                            courseTitle.getText().toString() +
                                            "\nDescription: " +
                                            courseDescrip.getText().toString() +
                                            "\nPrereqs: " +
                                            coursePrereqs.getText().toString() +
                                            "\nSession " + courseSession.getText().toString() + "-" + courseSection.getText().toString());
                                    clipboard.setPrimaryClip(clip);
                                    Toast newToast = new Toast(display_schedulable_courses.this);
                                    newToast.makeText(display_schedulable_courses.this, "Copied!", Toast.LENGTH_SHORT).show();
                                }
                            });
                            popUpDialog.setNegativeButton("Remove From Saved List", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if(sharedList.get(1).indexOf(ourCourse) >= 0) {
                                        sharedList.get(1).remove(sharedList.get(1).indexOf(ourCourse));
                                    }
                                    //REMOVE FROM DATABASE
                                    myHelper.deleteCourse(ourCourse.courseCode);
                                    reinstantiateSavedViews(sharedList.get(1));
                                }
                            });
                            popUpDialog.show();
                            return (true);
                        }
                    });*/
               // }
                holder.addView(toReturn);
                counter.myInt++;
            }
            if(position == 1) { savedViewHolder = holder; savedCoursesTextView = filler; }
            container.addView(mainView);
            return (mainView);
        }

        private void reinstantiateSavedViews(ArrayList<extendedCondensed> listToReinstance) {
            Log.d("ChildCount", "Total Child Count is: " + Integer.toString(savedViewHolder.getChildCount()));
            savedViewHolder.removeAllViews();
            if(listToReinstance.size() == 0) { savedCoursesTextView.setText("Couldn't find any saved courses :("); }
            else
            {
                savedCoursesTextView.setText("");
            }
            for (final extendedCondensed ourCourse : listToReinstance) {
                Log.d("NewElement", "Instantiating course: " + ourCourse.courseCode);

                View toReturn = LayoutInflater.from(sharedContext).inflate(R.layout.artsci_course_info, savedViewHolder, false);
                final TextView courseCode = (TextView) toReturn.findViewById(R.id.courseCode);
                final TextView courseTitle = (TextView) toReturn.findViewById(R.id.courseTitle);
                final TextView courseDescrip = (TextView) toReturn.findViewById(R.id.courseDescrip);
                final TextView coursePrereqs = (TextView) toReturn.findViewById(R.id.coursePrereqs);
                final TextView courseSection = (TextView) toReturn.findViewById(R.id.courseSection);
                final TextView courseSession = (TextView) toReturn.findViewById(R.id.courseSession);
                final TextView lectureTimes = (TextView) toReturn.findViewById(R.id.lectureTimes);
                final TextView tutorialTimes = (TextView) toReturn.findViewById(R.id.tutorialTimes);
                final TextView practicalTimes = (TextView) toReturn.findViewById(R.id.practicalTimes);


                courseCode.setText("Code: " + ourCourse.courseCode);
                courseTitle.setText("Title: " + ourCourse.courseTitle);
                courseDescrip.setText("Descrip: " + ourCourse.courseDescrip);
                coursePrereqs.setText("Prereqs: " + ourCourse.coursePrereqs);
                courseSection.setText("Section: " + ourCourse.courseSection);
                lectureTimes.setText(ourCourse.lectureTimes);
                tutorialTimes.setText(ourCourse.tutorialTimes);
                practicalTimes.setText(ourCourse.practicalTimes);

                final AppCompatButton expandButton = (AppCompatButton) toReturn.findViewById(R.id.expandButton);
                final LinearLayout detailsLayout = (LinearLayout) toReturn.findViewById(R.id.hideDetails);
                final LinearLayout totalLayout = (LinearLayout) toReturn.findViewById(R.id.totalCourseHolder);
                final flagClass expandFlag = new flagClass();
                expandFlag.flag = false;
                detailsLayout.setVisibility(View.GONE);
                expandButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(expandFlag.flag)
                        {
                            detailsLayout.setVisibility(View.GONE);
                            expandFlag.flag = false;
                            expandButton.setText("Expand");
                        }
                        else
                        {
                            detailsLayout.setVisibility(View.VISIBLE);
                            expandFlag.flag = true;
                            expandButton.setText("Collapse");
                        }
                    }
                });
                totalLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(expandFlag.flag)
                        {
                            detailsLayout.setVisibility(View.GONE);
                            expandFlag.flag = false;
                            expandButton.setText("Expand");
                        }
                        else
                        {
                            detailsLayout.setVisibility(View.VISIBLE);
                            expandFlag.flag = true;
                            expandButton.setText("Collapse");
                        }
                    }
                });

                if (ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length() - 1) == 57) {
                    int temp = Integer.parseInt(ourCourse.courseSession);
                    courseSession.setText("Session: " + Integer.toString(temp + 2));
                } else {
                    courseSession.setText("Session: " + ourCourse.courseSession);
                }
                toReturn.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        AlertDialog.Builder popUpDialog = new AlertDialog.Builder(display_schedulable_courses.this, R.style.CustomDialog);
                        popUpDialog.setMessage("More Options");
                        /*popUpDialog.setPositiveButton("More Info", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                if (ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length() - 1) == 57) {
                                    int temp = Integer.parseInt(ourCourse.courseSession);
                                    browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + Integer.toString(temp + 2)));
                                } else {
                                    browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + ourCourse.courseSession));
                                }
                                startActivity(browserIntent);
                            }
                        });*/
                        popUpDialog.setPositiveButton("More Info", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                    /*
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                    if (ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length() - 1) == 57) {
                                        int temp = Integer.parseInt(ourCourse.courseSession);
                                        browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + Integer.toString(temp + 2)));
                                    } else {
                                        browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + ourCourse.courseSession));
                                    }

                                    startActivity(browserIntent);
                                    */

                                final AlertDialog.Builder browserDialog = new AlertDialog.Builder(display_schedulable_courses.this, R.style.CustomDialog);
                                View webViewContent = LayoutInflater.from(display_schedulable_courses.this).inflate(R.layout.display_course_info_webview, null);
                                browserDialog.setView(webViewContent);
                                final Dialog cancelDialog2 = browserDialog.create();
                                browserDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        dialogInterface.dismiss();
                                    }
                                });
                                browserDialog.setPositiveButton("Open In Browser", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                                        if (ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length() - 1) == 57) {
                                            int temp = Integer.parseInt(ourCourse.courseSession);
                                            browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + Integer.toString(temp + 2)));
                                        } else {
                                            browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + ourCourse.courseSession));
                                        }
                                        startActivity(browserIntent);
                                    }
                                });
                                WebView courseWebView = (WebView) webViewContent.findViewById(R.id.webViewCourseInfo);
                                WebViewClient courseWebViewClient = new WebViewClient();
                                courseWebView.getSettings().setJavaScriptEnabled(true);
                                //courseWebView.loadUrl();
                                if (ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length() - 1) == 57) {
                                    int temp = Integer.parseInt(ourCourse.courseSession);
                                    courseWebView.loadUrl("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + Integer.toString(temp + 2));
                                } else {
                                    courseWebView.loadUrl("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + ourCourse.courseCode + ourCourse.courseSection + ourCourse.courseSession);
                                }
                                browserDialog.show();

                            }
                        });
                        popUpDialog.setNeutralButton("Copy Info", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                                ClipData clip = ClipData.newPlainText("Copied", "Code: " +
                                        courseCode.getText().toString() +
                                        "\nTitle: " +
                                        courseTitle.getText().toString() +
                                        "\nDescription: " +
                                        courseDescrip.getText().toString() +
                                        "\nPrereqs: " +
                                        coursePrereqs.getText().toString() +
                                        "\nSession " + courseSession.getText().toString() + "-" + courseSection.getText().toString());
                                clipboard.setPrimaryClip(clip);
                                Toast newToast = new Toast(display_schedulable_courses.this);
                                newToast.makeText(display_schedulable_courses.this, "Copied!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        popUpDialog.setNegativeButton("Remove From Saved List", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(sharedList.get(1).indexOf(ourCourse) >= 0) {
                                    sharedList.get(1).remove(sharedList.get(1).indexOf(ourCourse));
                                }
                                //REMOVE FROM DATABASE
                                myHelper.deleteCourse(ourCourse.courseCode);
                                reinstantiateSavedViews(sharedList.get(1));
                            }
                        });
                        popUpDialog.show();
                        return(true);
                    }
                });
                savedViewHolder.addView(toReturn);
            }
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    public class DBhelper extends SQLiteOpenHelper
    {
        /*
        String courseCode;
        String courseTitle;
        String courseDescrip;
        String coursePrereqs;
        String courseSection;
        String courseSession;
        */
        private Context sharedContext;
        public static final String DATABASE_NAME = "savedCourses.db";
        public DBhelper(Context sharedContext)
        {
            super(sharedContext, DATABASE_NAME, null, 1);
            this.sharedContext = sharedContext;
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            // TODO Auto-generated method stub
            db.execSQL(
                    "create table courses " +
                            "(" +
                            "courseCode text," +
                            "courseTitle text," +
                            "courseDescrip text," +
                            "coursePrereqs text," +
                            "courseSection text," +
                            "courseSession text," +
                            "lectureTimes text," +
                            "tutorialTimes text," +
                            "practicalTimes text)"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS courses");
            onCreate(db);
        }

        public boolean insertCourse (extendedCondensed courseToAdd) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("courseCode", courseToAdd.courseCode);
            contentValues.put("courseTitle", courseToAdd.courseTitle);
            contentValues.put("courseDescrip", courseToAdd.courseDescrip);
            contentValues.put("coursePrereqs", courseToAdd.coursePrereqs);
            contentValues.put("courseSection", courseToAdd.courseSection);
            contentValues.put("courseSession", courseToAdd.courseSession);
            contentValues.put("lectureTimes", courseToAdd.lectureTimes);
            contentValues.put("tutorialTimes", courseToAdd.tutorialTimes);
            contentValues.put("practicalTimes", courseToAdd.practicalTimes);
            db.insert("courses", null, contentValues);
            return true;
        }

        /*public Cursor getData(int id) {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor res =  db.rawQuery( "select * from courses where id="+id+"", null );
            return res;
        }*/

        public int numberOfRows(){
            SQLiteDatabase db = this.getReadableDatabase();
            int numRows = (int) DatabaseUtils.queryNumEntries(db, "courses");
            return numRows;
        }

        public Integer deleteCourse (String courseCode) {
            SQLiteDatabase db = this.getWritableDatabase();
            Toast.makeText(sharedContext, "Deleting", Toast.LENGTH_SHORT).show();
            return db.delete("courses",
                    "courseCode = ? ",
                    new String[] { courseCode });
        }

        public ArrayList<extendedCondensed> getAllCourses() {
            ArrayList<extendedCondensed> arrayToReturn = new ArrayList<>();

            //hp = new HashMap();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor =  db.rawQuery( "select * from courses", null );
            cursor.moveToFirst();
            ArrayList<extendedCondensed> listToReturn = new ArrayList<>();
            //String courseCode, String courseDescrip, String coursePrereqs, String courseSection, String courseTitle, String courseSession
            while(cursor.isAfterLast() == false){
                extendedCondensed nextCourse = new extendedCondensed(
                        cursor.getString(0),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(1),
                        cursor.getString(5),
                        cursor.getString(6),
                        cursor.getString(7),
                        cursor.getString(8)
                );
                listToReturn.add(nextCourse);
                cursor.moveToNext();
            }
            return listToReturn;
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 1)
        {
            if(resultCode == Activity.RESULT_OK)
            {
                oauthCode = data.getStringExtra("code");
                Toast.makeText(this, "Success!", Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(this, "Failed", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
