package com.uoft.gobblgobble.schedulefiller;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
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


import java.util.ArrayList;
import java.util.Objects;

public class display_schedulable_courses extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_schedulable_courses);


        final ViewPager myPager = (ViewPager) findViewById(R.id.mainPager);

        DBhelper myHelper = new DBhelper(this);

        ArrayList<ArrayList<fillCourse.condensedCourseTotal>> pagerArray = new ArrayList<>();

        ArrayList<fillCourse.condensedCourseTotal> fitsInSchedule = getIntent().getParcelableArrayListExtra("courses");

        pagerArray.add(fitsInSchedule);
        pagerArray.add(myHelper.getAllCourses());
        final customPagerAdapter myAdapter = new customPagerAdapter(pagerArray, this, myHelper);
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
        public ArrayList<ArrayList<fillCourse.condensedCourseTotal>> sharedList;
        public Context sharedContext;
        public DBhelper myHelper;
        public LinearLayout savedViewHolder;

        public customPagerAdapter(ArrayList<ArrayList<fillCourse.condensedCourseTotal>> sharedList, Context sharedContext, DBhelper myHelper) {
            this.sharedContext = sharedContext;
            this.sharedList = sharedList;
            this.myHelper = myHelper;
            this.savedViewHolder = null;
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
        public Object instantiateItem(final ViewGroup container, int position) {
            View mainView = LayoutInflater.from(sharedContext).inflate(R.layout.pager_item, container, false);
            Toolbar toolbar = (Toolbar) mainView.findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);


            ArrayList<fillCourse.condensedCourseTotal> fitsInSchedule = sharedList.get(position % sharedList.size());

            MobileAds.initialize(sharedContext, "ca-app-pub-5269640446877970~6452380842");

            AdView adView = (AdView) mainView.findViewById(R.id.testAd);
            AdRequest request = new AdRequest.Builder().build();
            adView.loadAd(request);

            TextView filler = (TextView) mainView.findViewById(R.id.fillerNoCourse);
            boolean holderHasFiller = true;

            LinearLayout holder = (LinearLayout) mainView.findViewById(R.id.courseHolders);

            //Toolbar topToolbar = (Toolbar) mainView.findViewById(R.id.toolbar);
            //setSupportActionBar(topToolbar);
            //getSupportActionBar().setTitle("");

            if(position == 1)
            {
                filler.setText("Couldn't find any saved courses :(");
            }


            for (final fillCourse.condensedCourseTotal ourCourse : fitsInSchedule) {
                Log.d("NewElement", "Instantiating course: " + ourCourse.courseCode);
                if (holderHasFiller) {
                    holder.removeView(filler);
                    holderHasFiller = false;
                }
                View toReturn = LayoutInflater.from(sharedContext).inflate(R.layout.artsci_course_info, holder, false);
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
                if (ourCourse.courseSection.contains("S") && (int) ourCourse.courseSession.charAt(ourCourse.courseSession.length() - 1) == 57) {
                    int temp = Integer.parseInt(ourCourse.courseSession);
                    courseSession.setText("Session: " + Integer.toString(temp + 2));
                } else {
                    courseSession.setText("Session: " + ourCourse.courseSession);
                }

                if (position == 0) {
                    toReturn.setOnLongClickListener(new View.OnLongClickListener() {
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
                            popUpDialog.setNegativeButton("Add To Saved List", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    boolean addFlag = true;
                                    for(fillCourse.condensedCourseTotal checkCourse : sharedList.get(1)) {
                                        if(checkCourse.courseCode.length() == ourCourse.courseCode.length() && Objects.equals(checkCourse.courseCode, ourCourse.courseCode));
                                        {
                                            boolean innerFlag = true;
                                            for(int j = 0; j < checkCourse.courseCode.length(); j++)
                                            {
                                                if(!Objects.equals(ourCourse.courseCode.charAt(j), checkCourse.courseCode.charAt(j)))
                                                {
                                                    innerFlag = false;
                                                    break;
                                                }
                                            }
                                            if(innerFlag) {
                                                addFlag = false;
                                                Log.d("DoesContain", checkCourse.courseCode + " and " + ourCourse.courseCode);
                                            }
                                        }
                                    }
                                    if(addFlag) {
                                        sharedList.get(1).add(ourCourse);
                                        //ADD TO DATABASE
                                        myHelper.insertCourse(ourCourse);
                                    }
                                    reinstantiateSavedViews(sharedList.get(1));
                                }
                            });
                            popUpDialog.show();
                            return (true);
                        }
                    });
                } else {
                    toReturn.setOnLongClickListener(new View.OnLongClickListener() {
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
                    });
                }
                holder.addView(toReturn);
            }
            if(position == 1) { savedViewHolder = holder; };
            container.addView(mainView);
            return (mainView);
        }

        private void reinstantiateSavedViews(ArrayList<fillCourse.condensedCourseTotal> listToReinstance) {
            savedViewHolder.removeAllViews();
            Log.d("ChildCount", "Total Child Count is: " + Integer.toString(savedViewHolder.getChildCount()));
            for (final fillCourse.condensedCourseTotal ourCourse : listToReinstance) {
                Log.d("NewElement", "Instantiating course: " + ourCourse.courseCode);

                View toReturn = LayoutInflater.from(sharedContext).inflate(R.layout.artsci_course_info, savedViewHolder, false);
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
                            "courseSession text)"
            );
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            db.execSQL("DROP TABLE IF EXISTS courses");
            onCreate(db);
        }

        public boolean insertCourse (fillCourse.condensedCourseTotal courseToAdd) {
            SQLiteDatabase db = this.getWritableDatabase();
            ContentValues contentValues = new ContentValues();
            contentValues.put("courseCode", courseToAdd.courseCode);
            contentValues.put("courseTitle", courseToAdd.courseTitle);
            contentValues.put("courseDescrip", courseToAdd.courseDescrip);
            contentValues.put("coursePrereqs", courseToAdd.coursePrereqs);
            contentValues.put("courseSection", courseToAdd.courseSection);
            contentValues.put("courseSession", courseToAdd.courseSession);
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

        public ArrayList<fillCourse.condensedCourseTotal> getAllCourses() {
            ArrayList<fillCourse.condensedCourseTotal> arrayToReturn = new ArrayList<>();

            //hp = new HashMap();
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor =  db.rawQuery( "select * from courses", null );
            cursor.moveToFirst();
            ArrayList<fillCourse.condensedCourseTotal> listToReturn = new ArrayList<>();
            //String courseCode, String courseDescrip, String coursePrereqs, String courseSection, String courseTitle, String courseSession
            while(cursor.isAfterLast() == false){
                fillCourse.condensedCourseTotal nextCourse = new fillCourse.condensedCourseTotal(
                        cursor.getString(0),
                        cursor.getString(2),
                        cursor.getString(3),
                        cursor.getString(4),
                        cursor.getString(1),
                        cursor.getString(5)
                );
                listToReturn.add(nextCourse);
                cursor.moveToNext();
            }
            return listToReturn;
        }
    }
}
