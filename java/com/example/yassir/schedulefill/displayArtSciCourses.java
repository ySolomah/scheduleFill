package com.example.yassir.schedulefill;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class displayArtSciCourses extends AppCompatActivity {

    public class headerInfo
    {
        public int height;
        public TextView header;
        public headerInfo(int height, TextView header)
        {
            this.height = height;
            this.header = header;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_art_sci_courses);
        ArrayList<fillCourse.condensedCourseTotal> fitsInSchedule = getIntent().getParcelableArrayListExtra("courses");
        TextView header = (TextView) findViewById(R.id.artsciHeader);
        final headerInfo ourHeaderInfo = new headerInfo(-1, header);
        final ListView listView = (ListView) findViewById(R.id.artsciList);
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i1, int i2) {
                if(ourHeaderInfo.height == -1)
                {
                    ourHeaderInfo.height = ourHeaderInfo.header.getMeasuredHeight();
                }
                if(absListView.getScrollY() > 0)
                {
                    ViewGroup.LayoutParams params = ourHeaderInfo.header.getLayoutParams();
                    if(absListView.getScrollY() >= ourHeaderInfo.height)
                    {
                        params.height = 0;
                    }
                    else
                    {
                        params.height = ourHeaderInfo.height - absListView.getScrollY();
                    }
                    //Log.d("Height", "HEIGHT OF HEADER: " + Integer.toString(ourHeaderInfo.height) + " I SCROLLED: " + absListView.getScrollY());
                    ourHeaderInfo.header.setLayoutParams(params);
                }
            }
        });
        courseFillAdapter adapter = new courseFillAdapter(fitsInSchedule, this);
        listView.setAdapter(adapter);
    }

    public class courseFillAdapter extends ArrayAdapter<fillCourse.condensedCourseTotal>
    {
        public ArrayList<fillCourse.condensedCourseTotal> ourList;
        public Context sharedContext;

        public courseFillAdapter(ArrayList<fillCourse.condensedCourseTotal> ourList, Context sharedContext)
        {
            super(sharedContext, -1 , ourList);
            this.sharedContext = sharedContext;
            this.ourList = ourList;
        }
        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View toReturn = convertView;
            if(toReturn == null)
            {
                toReturn = getLayoutInflater().from(sharedContext).inflate(R.layout.artsci_course_info, parent, false);
            }
            final TextView courseCode = (TextView) toReturn.findViewById(R.id.courseCode);
            final TextView courseTitle = (TextView) toReturn.findViewById(R.id.courseTitle);
            final TextView courseDescrip = (TextView) toReturn.findViewById(R.id.courseDescrip);
            final TextView coursePrereqs = (TextView) toReturn.findViewById(R.id.coursePrereqs);
            final TextView courseSection = (TextView) toReturn.findViewById(R.id.courseSection);
            final TextView courseSession = (TextView) toReturn.findViewById(R.id.courseSession);

            fillCourse.condensedCourseTotal ourCourse = ourList.get(position);

            courseCode.setText("Code: " + ourCourse.courseCode);
            courseTitle.setText("Title: " + ourCourse.courseTitle);
            courseDescrip.setText("Descrip: " + ourCourse.courseDescrip);
            coursePrereqs.setText("Prereqs: " + ourCourse.coursePrereqs);
            courseSection.setText("Section: " + ourCourse.courseSection);
            courseSession.setText("Session: " + ourCourse.courseSession);

            toReturn.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    AlertDialog.Builder popUpDialog = new AlertDialog.Builder(sharedContext);
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
                            Toast newToast = new Toast(sharedContext);
                            newToast.makeText(getApplicationContext(), "Copied!", Toast.LENGTH_SHORT).show();
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
            return (toReturn);
        }
    }
}
