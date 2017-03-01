package com.uoft.gobblgobble.schedulefiller;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.uoft.gobblgobble.schedulefiller.R;

import java.util.ArrayList;

public class display_schedulable_courses extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_schedulable_courses);
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


        for(fillCourse.condensedCourseTotal ourCourse : fitsInSchedule)
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
            courseSession.setText("Session: " + ourCourse.courseSession);

            toReturn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder popUpDialog = new AlertDialog.Builder(display_schedulable_courses.this, R.style.CustomDialog);
                    popUpDialog.setMessage("More Options");
                    popUpDialog.setPositiveButton("Browser", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
                            browserIntent.setData(Uri.parse("http://coursefinder.utoronto.ca/course-search/search/courseInquiry?methodToCall=start&viewId=CourseDetails-InquiryView&courseId=" + courseCode.getText().toString() + courseSection.getText().toString() + courseSession.getText().toString()));
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
        }

    }
}
