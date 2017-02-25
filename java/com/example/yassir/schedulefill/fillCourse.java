package com.example.yassir.schedulefill;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.JsonReader;
import android.util.JsonToken;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import com.gargoylesoftware.htmlunit.WebClient;
//import com.gargoylesoftware.htmlunit.html.HtmlPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class fillCourse extends AppCompatActivity {

    public class linearLayoutCollect
    {
        public int height;
        public LinearLayout ourLayout;
        public linearLayoutCollect(int height, LinearLayout ourLayout)
        {
            this.height = height;
            this.ourLayout = ourLayout;
        }
    }
    public class stringClass
    {
        public String myString;
        public stringClass() { myString = ""; }
    }

    public class flagClass
    {
        public boolean flag;
        public flagClass()
        {
            flag = false;
        }
    }

    public class MyJavaScriptInterface
    {
        private Context ctx;
        public String html;
        MyJavaScriptInterface(Context context, String html)
        {
            this.ctx = context;
            this.html = html;
        }
        @JavascriptInterface
        public void showHTML(String html)
        {
            this.html = html;
        }
    }

    public class Selections
    {
        public String name;
        public String description;
        public Selections(String name, String description)
        {
            this.name = name;
            this.description = description;
        }
    }

    public ArrayList<Integer> mondayTimes;
    public ArrayList<Integer> tuesdayTimes;
    public ArrayList<Integer> wednesdayTimes;
    public ArrayList<Integer> thursdayTimes;
    public ArrayList<Integer> fridayTimes;


    public flagClass stopTextFlag;
    public Queue<String> textQueue;
    public flagClass listViewFlag;

    public class timeSlot{
        public int startTime;
        public int endTime;
        public String meetingDay;
        public timeSlot(int startTime, int endTime, String meetingDay)
        {
            this.startTime = startTime;
            this.endTime = endTime;
            this.meetingDay = meetingDay;
        }
    }
    public class courseTotal
    {
        String courseCode;
        String courseTitle;
        String courseDescrip;
        String coursePrereqs;
        String courseSection;
        String courseSession;
        ArrayList<ArrayList<timeSlot>> lec;
        ArrayList<ArrayList<timeSlot>> tut;
        ArrayList<ArrayList<timeSlot>> pra;
        public courseTotal(String courseCode, String courseDescrip, String coursePrereqs, String courseSection, String courseTitle, String courseSession)
        {
            this.courseSession = courseSession;
            this.courseCode = courseCode;
            this.courseDescrip = courseDescrip;
            this.coursePrereqs = coursePrereqs;
            this.courseSection = courseSection;
            this.courseTitle = courseTitle;
            lec = new ArrayList<>();
            tut = new ArrayList<>();
            pra = new ArrayList<>();
        }
    }

    public static class condensedCourseTotal implements Parcelable
    {
        String courseCode;
        String courseTitle;
        String courseDescrip;
        String coursePrereqs;
        String courseSection;
        String courseSession;
        public condensedCourseTotal(String courseCode, String courseDescrip, String coursePrereqs, String courseSection, String courseTitle, String courseSession)
        {
            this.courseSession = courseSession;
            this.courseCode = courseCode;
            this.courseDescrip = courseDescrip;
            this.coursePrereqs = coursePrereqs;
            this.courseSection = courseSection;
            this.courseTitle = courseTitle;
        }

        public condensedCourseTotal(Parcel in)
        {
            this.courseCode = in.readString();
            this.courseTitle = in.readString();
            this.courseDescrip = in.readString();
            this.coursePrereqs = in.readString();
            this.courseSection = in.readString();
            this.courseSession = in.readString();
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
        }

        public static final Creator<condensedCourseTotal> CREATOR = new Creator<condensedCourseTotal>()
        {
            public condensedCourseTotal createFromParcel(Parcel in)
            {
                return new condensedCourseTotal(in);
            }
            public condensedCourseTotal[] newArray(int size)
            {
                return new condensedCourseTotal[size];
            }
        };
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fill_course);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        stopTextFlag = new flagClass();
        listViewFlag = new flagClass();

        textQueue = new LinkedList<>();


        //final WebView loadHtml = (WebView) findViewById(R.id.loadHtml);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        String baseUrl = "https://timetable.iit.artsci.utoronto.ca/api/courses?org=";
        final ArrayList<Selections> selectionsList = new ArrayList<>();
        final ArrayList<String> orgNames = new ArrayList<>();
        orgNames.add("");
        final ArrayList<String> orgDescrip = new ArrayList<>();
        orgDescrip.add("Show All - WARNING!!! LARGE DATA");
        Thread orgThreads = new Thread(new Runnable() {
           @Override
            public void run() {
               final String parseString = "https://timetable.iit.artsci.utoronto.ca/api/orgs";
               String htmlData = "";
               try {
                   //htmlData = Jsoup.connect(parseString).get().toString();
                   InputStream is = new URL(parseString).openStream();
                   StringBuilder ourString = new StringBuilder();
                   int cp;
                   while((cp = is.read()) != -1)
                   {
                        ourString.append((char) cp);
                   }
                   JSONObject data = null;
                   try {
                       //textQueue.add(ourString.toString());
                       data = new JSONObject(ourString.toString());
                       try {
                           for (int i = 0; i < data.getJSONObject("orgs").names().length(); i++) {
                               //textQueue.add("IN FOR");
                               //textQueue.add("Number of name: " + Integer.toString(data.getJSONObject("orgs").names().length()));
                               JSONObject nextElement = null;
                               String name = (String) data.getJSONObject("orgs").names().get(i);
                               //textQueue.add("GOT NAME: " + name);
                               String text = (String) data.getJSONObject("orgs").get(name);
                               //textQueue.add("GOT TEXT: " + text);
                               //textQueue.add("KEY: " +  name);
                               //textQueue.add("KEY VALUE: " + text);
                               orgNames.add(name);
                               orgDescrip.add(text);

                                selectionsList.add(new Selections(name, text));

                               /*
                               //nextElement = data.getJSONObject("orgs").names().get(i)
                               textQueue.add("GOT ELEMENT");
                               Iterator<String> keys = nextElement.keys();
                               textQueue.add("GOT KEYS");
                               String key = "";
                               while(keys.hasNext())
                               {
                                   key = keys.next();
                                   textQueue.add("KEY: " +  key);
                                   textQueue.add("KEY VALUE: " + nextElement.get(key));
                                   orgNames.add(key);
                                   orgDescrip.add(nextElement.getString(key));
                               }
                               */
                           }
                       }
                       catch (JSONException e)
                       {
                           textQueue.add("Json parse failed");
                       }
                   }

                   catch (JSONException e)
                   {
                       textQueue.add("Json Except");
                   }

               }
               catch(IOException e)
               {
                   textQueue.add("Failed to grab data");
               }

            }
        });
        orgThreads.start();


        final Thread textThread = new Thread(new Runnable() {
            @Override
            public void run() {
                String nextToSend = "";
                while(!stopTextFlag.flag)
                {
                    if(!textQueue.isEmpty() && (nextToSend = textQueue.remove()) != null && !nextToSend.isEmpty())
                    {
                        sendTexts(nextToSend);
                        try
                        {
                            Thread.sleep(50);
                        }
                        catch (InterruptedException e)
                        {
                            Log.d("Fail", "Thread Sleep");
                        }

                    }
                    else
                    {
                        try {
                            Thread.sleep(200);
                        }
                        catch (InterruptedException e)
                        {
                            Log.d("Fail", "Thread Sleep");
                        }
                    }
                }
            }
        });
        textThread.start();

        FloatingActionButton[] dayPlus = new FloatingActionButton[5];
        dayPlus[0] = (FloatingActionButton) findViewById(R.id.mondayPlus);
        dayPlus[1] = (FloatingActionButton) findViewById(R.id.tuesdayPlus);
        dayPlus[2] = (FloatingActionButton) findViewById(R.id.wednesdayPlus);
        dayPlus[3] = (FloatingActionButton) findViewById(R.id.thursdayPlus);
        dayPlus[4] = (FloatingActionButton) findViewById(R.id.fridayPlus);

        LinearLayout[] dayLinearLayouts = new LinearLayout[5];
        dayLinearLayouts[0] = (LinearLayout) findViewById(R.id.mondaySpinners);
        dayLinearLayouts[1] = (LinearLayout) findViewById(R.id.tuesdaySpinners);
        dayLinearLayouts[2] = (LinearLayout) findViewById(R.id.wednesdaySpinners);
        dayLinearLayouts[3] = (LinearLayout) findViewById(R.id.thursdaySpinners);
        dayLinearLayouts[4] = (LinearLayout) findViewById(R.id.fridaySpinners);

        mondayTimes = new ArrayList<>();
        tuesdayTimes = new ArrayList<>();
        wednesdayTimes = new ArrayList<>();
        thursdayTimes = new ArrayList<>();
        fridayTimes = new ArrayList<>();

        for(int i = 0; i < 5; i++)
        {
            ArrayList<Integer> myList = null;
            if(i == 0) { myList = mondayTimes; }
            if(i == 1) { myList = tuesdayTimes; }
            if(i == 2) { myList = wednesdayTimes; }
            if(i == 3) { myList = thursdayTimes; }
            if(i == 4) { myList = fridayTimes; }
            dayPlus[i].setOnClickListener(new plusButton(dayLinearLayouts[i], this, myList));
            textQueue.add("Added element: " + Integer.toString(i));
        }


        try {
            orgThreads.join();
        }
        catch (InterruptedException e)
        {
            Log.d("Thread Join", "Interrupted");
        }
        String totalOrg = "";
        int x = 0;
        for(String additionalName : orgNames)
        {
            if(x == 0 || totalOrg.isEmpty() || totalOrg.equals("")) { x++; }
            else { totalOrg = totalOrg + ","; }
            totalOrg = totalOrg + additionalName;
            Log.d("Show All: " , "Data: " + totalOrg);
        }

        EditText departmentSelection = (EditText) findViewById(R.id.departmentSearch);
        Button departmentClear = (Button) findViewById(R.id.clearOrgList);
        final TextView departmentsSelected = (TextView) findViewById(R.id.orgList);
        //final ListView departmentList = (ListView) findViewById(R.id.departmentSelections);

        departmentClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        departmentsSelected.setText("");
                    }
                });
            }
        });

        final ArrayList<Selections> SelectionsToBeShown = new ArrayList<>();
        for(Selections populateSelections : selectionsList)
        {
            SelectionsToBeShown.add(populateSelections);
        }
        //final selectionsAdapter departmentAdapter = new selectionsAdapter(this, -1, SelectionsToBeShown, departmentsSelected);
        //departmentList.setAdapter(departmentAdapter);
        //setListViewHeightBasedOnChildren(departmentList, listViewFlag.flag);
        final LinearLayout orgHolder = (LinearLayout) findViewById(R.id.holdOrgs);
        final ArrayList<Integer> inflatedLayoutHeights = new ArrayList<>();
        final ArrayList<linearLayoutCollect> inflatedLayouts = new ArrayList<>();
        for(Selections selectionToInflate : SelectionsToBeShown)
        {
            LinearLayout nextInflation = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.department_listview_element, orgHolder, false);
            orgHolder.addView(nextInflation);
            final TextView code = (TextView) nextInflation.findViewById(R.id.departmentCode);
            final TextView descrip = (TextView) nextInflation.findViewById(R.id.departmentText);
            code.setText(selectionToInflate.name);
            descrip.setText(selectionToInflate.description);
            nextInflation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    departmentsSelected.setText(departmentsSelected.getText().toString() + code.getText() + ",");
                }
            });
            inflatedLayoutHeights.add(nextInflation.getMeasuredHeight());
            inflatedLayouts.add(new linearLayoutCollect(-1, nextInflation));
        }


        departmentSelection.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int a, int i1, int i2) {
                /*
                SelectionsToBeShown.clear();
                for(Selections nextSel : selectionsList)
                {
                    if(nextSel.description.toLowerCase().contains(charSequence.toString().toLowerCase()))
                    {
                        SelectionsToBeShown.add(nextSel);
                    }
                }

                //departmentAdapter.notifyDataSetChanged();
                //setListViewHeightBasedOnChildren(departmentList, listViewFlag.flag);

                orgHolder.removeAllViewsInLayout();
                inflatedLayouts.clear();
                for(Selections selectionToInflate : SelectionsToBeShown)
                {
                        LinearLayout nextInflation = (LinearLayout) LayoutInflater.from(getApplicationContext()).inflate(R.layout.department_listview_element, orgHolder, false);
                        orgHolder.addView(nextInflation);
                        final TextView code = (TextView) nextInflation.findViewById(R.id.departmentCode);
                        final TextView descrip = (TextView) nextInflation.findViewById(R.id.departmentText);
                        code.setText(selectionToInflate.name);
                        descrip.setText(selectionToInflate.description);
                        nextInflation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                departmentsSelected.setText(departmentsSelected.getText().toString() + code.getText() + ",");
                            }
                        });
                        inflatedLayouts.add(nextInflation);
                }*/
                for(int i = 0; i < inflatedLayouts.size(); i++)
                {
                    linearLayoutCollect nextLayout = inflatedLayouts.get(i);
                    ViewGroup.LayoutParams params = inflatedLayouts.get(i).ourLayout.getLayoutParams();
                    if(nextLayout.height == -1)
                    {
                        nextLayout.height = nextLayout.ourLayout.getMeasuredHeight();
                    }
                    if(!(((TextView)nextLayout.ourLayout.findViewById(R.id.departmentText)).getText().toString().toLowerCase().contains(charSequence.toString().toLowerCase())))
                    {
                        //inflatedLayouts.get(i).setVisibility(View.INVISIBLE);
                        params.height = 0;
                    }
                    else
                    {
                        Log.d("Course Info", "Course Info: " + nextLayout.ourLayout.findViewById(R.id.departmentText).toString().toLowerCase());
                        Log.d("Course Info", "charseq: " + charSequence.toString().toLowerCase());
                        //inflatedLayouts.get(i).setVisibility(View.VISIBLE);
                        params.height = nextLayout.height;
                    }
                    Log.d("Layout Height", "Inflated height was: " + nextLayout.height);
                    inflatedLayouts.get(i).ourLayout.setLayoutParams(params);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        CheckBox hideListView = (CheckBox) findViewById(R.id.hideListView);
        hideListView.setText("Hide List");
        hideListView.setOnCheckedChangeListener(new checkBoxSimple(listViewFlag, inflatedLayouts));


        final ArrayList<courseTotal> possibleCourses = new ArrayList<>();
        final ArrayList<courseTotal> failedToAddCourses = new ArrayList<>();
        final ArrayList<courseTotal> fitsInSchedule = new ArrayList<courseTotal>();
        final ArrayList<condensedCourseTotal> transferToNext = new ArrayList<>();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                textQueue.add("MONDAY: ");
                int count = 0;
                for(int i : mondayTimes)
                {
                    if(count%2 == 0) {
                        textQueue.add("START: " + Integer.toString(i));
                    }
                    else
                    {
                        textQueue.add("END: " + Integer.toString(i));
                    }
                    count++;
                }
                textQueue.add("TUESDAY: ");
                count = 0;
                for(int i : tuesdayTimes)
                {
                    if(count%2 == 0) {
                        textQueue.add("START: " + Integer.toString(i));
                    }
                    else
                    {
                        textQueue.add("END: " + Integer.toString(i));
                    }
                    count++;
                }
                textQueue.add("WEDNESDAY: ");
                count = 0;
                for(int i : wednesdayTimes)
                {
                    if(count%2 == 0) {
                        textQueue.add("START: " + Integer.toString(i));
                    }
                    else
                    {
                        textQueue.add("END: " + Integer.toString(i));
                    }
                    count++;
                }
                textQueue.add("THURSDAY: ");
                count = 0;
                for(int i : thursdayTimes)
                {
                    if(count%2 == 0) {
                        textQueue.add("START: " + Integer.toString(i));
                    }
                    else
                    {
                        textQueue.add("END: " + Integer.toString(i));
                    }
                    count++;
                }
                textQueue.add("FRIDAY: ");
                count = 0;
                for(int i : fridayTimes)
                {
                    if(count%2 == 0) {
                        textQueue.add("START: " + Integer.toString(i));
                    }
                    else
                    {
                        textQueue.add("END: " + Integer.toString(i));
                    }
                    count++;
                }

                //for(int i = 0; i < orgNames.size(); i++)
                //{
                //textQueue.add(orgNames.get(i));
                //textQueue.add(orgDescrip.get(i));
                //Log.d("Name", orgNames.get(i));
                //Log.d("Descrip", orgDescrip.get(i));
                //}
                possibleCourses.clear();
                failedToAddCourses.clear();
                fitsInSchedule.clear();
                transferToNext.clear();
                final String urlToUse = "https://timetable.iit.artsci.utoronto.ca/api/courses?org=" + departmentsSelected.getText().toString();
                textQueue.add("URL used: " + urlToUse);
                Thread networkThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            //Document courseData = Jsoup.connect(urlToUse).get();
                            //String courseHtml = courseData.body()
                            InputStream is = new URL(urlToUse).openStream();
                            StringBuilder ourString = new StringBuilder();
                            int cp;
                            while((cp = is.read()) != -1)
                            {
                                ourString.append((char) cp);
                            }
                            JSONObject data = null;
                            //textQueue.add(ourString.toString());
                            try {

                                //String name = (String) data.getJSONObject("orgs").names().get(i);
                                //String text = (String) data.getJSONObject("orgs").get(name);

                                data = new JSONObject(ourString.toString());
                                //int numOfCourses = data.names().length();
                                //JSONArray namesOfCourses = data.names();
                                //for(int i = 0 ;i < numOfCourses; i++)

                                Iterator<String> keys = data.keys();
                                while(keys.hasNext())
                                {
                                    Log.d("Note", "1");
                                    boolean addFlag = true;
                                    String key = keys.next();
                                    JSONObject courseToBeAdded = data.getJSONObject(key);
                                    String courseCode = courseToBeAdded.getString("code");
                                    String courseDescrip = courseToBeAdded.getString("courseDescription");
                                    String coursePrereqs = courseToBeAdded.getString("prerequisite");
                                    String courseTitle = courseToBeAdded.getString("courseTitle");
                                    String courseSection = courseToBeAdded.getString("section");
                                    String courseSession = courseToBeAdded.getString("session");
                                    courseTotal populateMe = new courseTotal(courseCode, courseDescrip, coursePrereqs, courseSection, courseTitle, courseSession);
                                    JSONObject meetings = courseToBeAdded.getJSONObject("meetings");
                                    Iterator<String> meetingKeys = null;
                                    if(meetings != null) {
                                        meetingKeys = meetings.keys();
                                    }
                                    else
                                    {
                                        addFlag = false;
                                    }
                                    while(meetingKeys.hasNext() && addFlag)
                                    {
                                        //Log.d("Note", "2");
                                        String meetingKey = meetingKeys.next();
                                        //Log.d("Note", "a");
                                        JSONObject meetingObject = meetings.getJSONObject(meetingKey);
                                        //Log.d("Note", "b");
                                        try {
                                            JSONObject schedule = meetingObject.getJSONObject("schedule");
                                            //Log.d("Note", "c");
                                            Iterator<String> scheduleKeys = null;
                                            if (schedule != null) {
                                                scheduleKeys = schedule.keys();
                                            } else {
                                                addFlag = false;
                                            }
                                            ArrayList<timeSlot> infoCollection = new ArrayList<timeSlot>();
                                            while (scheduleKeys.hasNext() && addFlag) {
                                                //Log.d("Note", "3");
                                                String scheduleKey = scheduleKeys.next();
                                                JSONObject info = schedule.getJSONObject(scheduleKey);
                                                if (info != null) {
                                                    //Log.d("Note", "4");
                                                    String day = info.getString("meetingDay");
                                                    String startTime = info.getString("meetingStartTime");
                                                    String endTime = info.getString("meetingEndTime");
                                                    Log.d("timeInfo", "START: " + startTime + " END: " + endTime + " CourseCode: " + courseCode + " MeetingKey: " + meetingKey);
                                                    if (!(startTime.contains("null") || endTime.contains("null"))) {
                                                        int startInt = Integer.parseInt(startTime.split(":")[0]);
                                                        int endInt = Integer.parseInt(endTime.split(":")[0]);
                                                        timeSlot nextToBeAdded = new timeSlot(startInt, endInt, day);
                                                        infoCollection.add(nextToBeAdded);
                                                    } else {
                                                        addFlag = false;
                                                    }
                                                }
                                            }

                                            if (meetingKey.contains("LEC")) {
                                                populateMe.lec.add(infoCollection);
                                            } else if (meetingKey.contains("TUT")) {
                                                populateMe.tut.add(infoCollection);
                                            } else //PRA
                                            {
                                                populateMe.pra.add(infoCollection);
                                            }
                                        }
                                        catch(JSONException e)
                                        {
                                            textQueue.add("Schedule Failure");
                                        }
                                    }
                                    if(addFlag) {
                                        possibleCourses.add(populateMe);
                                    }
                                    else {
                                        failedToAddCourses.add(populateMe);
                                    }
                                }
                                for(courseTotal x : possibleCourses)
                                {
                                    textQueue.add("Successfully added: " + x.courseCode);
                                }
                                for(courseTotal x : failedToAddCourses)
                                {
                                    textQueue.add("Failed to add: " + x.courseCode);
                                }
                            }
                            catch (JSONException e)
                            {
                                textQueue.add("JSON fail");
                            }
                        }
                        catch (IOException e)
                        {
                            textQueue.add("IOEXCEPT fab");
                        }
                    }
                });
                networkThread.start();
                try {
                    networkThread.join();
                }
                catch (InterruptedException e)
                {
                    textQueue.add("X");
                }
                for(courseTotal evaluateCourse : possibleCourses)
                {
                    if(evaluateCourse.lec.size() == 0 && evaluateCourse.tut.size() == 0 && evaluateCourse.pra.size() == 0)
                    {
                        continue;
                    }
                    //textQueue.add("Trying to match course: " + evaluateCourse.courseCode);
                    boolean matchLec = matchTotal(evaluateCourse, evaluateCourse.lec, mondayTimes, tuesdayTimes, wednesdayTimes, thursdayTimes, fridayTimes);
                    boolean matchTut = matchTotal(evaluateCourse, evaluateCourse.tut, mondayTimes, tuesdayTimes, wednesdayTimes, thursdayTimes, fridayTimes);
                    boolean matchPra = matchTotal(evaluateCourse, evaluateCourse.pra, mondayTimes, tuesdayTimes, wednesdayTimes, thursdayTimes, fridayTimes);
                    if(matchLec && matchTut && matchPra)
                    {
                        fitsInSchedule.add(evaluateCourse);
                        transferToNext.add(new condensedCourseTotal(evaluateCourse.courseCode, evaluateCourse.courseDescrip, evaluateCourse.coursePrereqs, evaluateCourse.courseSection, evaluateCourse.courseTitle, evaluateCourse.courseSession));
                    }
                    /*for(ArrayList<timeSlot> nextLec : evaluateCourse.lec)
                    {
                        matchLec = false;
                        boolean matchFlag = true;
                        for(timeSlot time : nextLec)
                        {
                            if(time.meetingDay.contains("MO"))
                            {
                                if(matchTime(time, mondayTimes)) { break; }
                                else { matchFlag = false; }
                            }
                            if(time.meetingDay.contains("TU"))
                            {
                                if(matchTime(time, tuesdayTimes)) { break; }
                                else { matchFlag = false; }
                            }
                            if(time.meetingDay.contains("WE"))
                            {
                                if(matchTime(time, wednesdayTimes)) { break; }
                                else { matchFlag = false; }
                            }
                            if(time.meetingDay.contains("TH"))
                            {
                                if(matchTime(time, thursdayTimes)) { break; }
                                else { matchFlag = false; }
                            }
                            if(time.meetingDay.contains("FR"))
                            {
                                if(matchTime(time, fridayTimes)) { break; }
                                else { matchFlag = false; }
                            }
                        }
                        if(matchFlag) { matchLec = true; break; }
                    }*/
                }
                for(courseTotal availableToPutInSchedule : fitsInSchedule)
                {
                    textQueue.add("Fits in schedule: " + availableToPutInSchedule.courseCode + " Section: " + availableToPutInSchedule.courseSection);
                }
                Intent nextAct = new Intent(getApplicationContext(), display_schedulable_courses.class);
                nextAct.putParcelableArrayListExtra("courses", transferToNext);
                startActivity(nextAct);

            }

        });
    }

    public static boolean matchTotal(courseTotal course, ArrayList<ArrayList<timeSlot>> matchAgainst, ArrayList<Integer> mondayTimes, ArrayList<Integer> tuesdayTimes, ArrayList<Integer> wednesdayTimes, ArrayList<Integer> thursdayTimes, ArrayList<Integer> fridayTimes)
    {
        if(matchAgainst.size() == 0) { return(true); }
        for(ArrayList<timeSlot> nextLec : matchAgainst)
        {
            boolean matchFlag = true;
            for(timeSlot time : nextLec)
            {
                if(time.meetingDay.contains("MO"))
                {
                    if(matchTime(time, mondayTimes, course)) { }
                    else { matchFlag = false; break;}
                }
                if(time.meetingDay.contains("TU"))
                {
                    if(matchTime(time, tuesdayTimes, course)) { }
                    else { matchFlag = false; break;}
                }
                if(time.meetingDay.contains("WE"))
                {
                    if(matchTime(time, wednesdayTimes, course)) { }
                    else { matchFlag = false; break;}
                }
                if(time.meetingDay.contains("TH"))
                {
                    if(matchTime(time, thursdayTimes, course)) { }
                    else { matchFlag = false; break;}
                }
                if(time.meetingDay.contains("FR"))
                {
                    if(matchTime(time, fridayTimes, course)) { }
                    else { matchFlag = false; break;}
                }
            }
            if(matchFlag) { return(true); }
        }
        return(false);
    }

    public static boolean matchTime(timeSlot time, ArrayList<Integer> scheduleFitting, courseTotal course)
    {
        for(int i = 0; i < scheduleFitting.size()/2; i++)
        {
            if(time.startTime >= scheduleFitting.get(i*2) && time.endTime <= scheduleFitting.get((i*2) + 1))
            {
                //Log.d("MATCH","Matched: " + course.courseCode + " With Start: " + Integer.toString(time.startTime) + " With End: " + Integer.toString(time.endTime));
                return(true);
            }
        }
        return(false);
    }












    protected class checkBoxSimple implements CompoundButton.OnCheckedChangeListener
    {
        flagClass myFlag;
        ArrayList<linearLayoutCollect> inflatedLayouts ;
        public checkBoxSimple(flagClass myFlag, ArrayList<linearLayoutCollect> inflatedLayouts)
        {
            this.myFlag = myFlag;
            this.inflatedLayouts = inflatedLayouts;
        }

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean b)
        {
            if(b)
            {
                myFlag.flag = true;
                for(linearLayoutCollect nextLay : inflatedLayouts)
                {
                    ViewGroup.LayoutParams params =  nextLay.ourLayout.getLayoutParams();
                    params.height = 0;
                    nextLay.ourLayout.setLayoutParams(params);
                }
            }
            else
            {
                myFlag.flag = false;
                for(linearLayoutCollect nextLay : inflatedLayouts)
                {
                    ViewGroup.LayoutParams params =  nextLay.ourLayout.getLayoutParams();
                    params.height = nextLay.height;
                    nextLay.ourLayout.setLayoutParams(params);
                }
            }
            //setListViewHeightBasedOnChildren(departmentList, listViewFlag.flag);
        }
    }

    protected class selectionsAdapter extends ArrayAdapter<Selections>
    {
        private Context sharedContext;
        private ArrayList<Selections> ourList;
        public TextView selectionsTextView;

        public selectionsAdapter(Context context, int resource, ArrayList<Selections> ourList, TextView selectionsTextView)
        {
            super(context, resource, ourList);
            this.sharedContext = context;
            this.ourList = ourList;
            this.selectionsTextView = selectionsTextView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View viewToUse = convertView;
            if(viewToUse == null)
            {
                viewToUse = getLayoutInflater().inflate(R.layout.department_listview_element, parent, false);
            }
            final TextView departmentCode = (TextView) viewToUse.findViewById(R.id.departmentCode);
            TextView departmentDescrip = (TextView) viewToUse.findViewById(R.id.departmentText);
            Selections fillElement = ourList.get(position);
            departmentCode.setText(fillElement.name);
            departmentDescrip.setText(fillElement.description);
            viewToUse.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            selectionsTextView.setText(selectionsTextView.getText().toString() + departmentCode.getText().toString() + ",");
                        }
                    });
                }
            });
            return(viewToUse);
        }
    }

    protected class plusButton implements View.OnClickListener
    {
        public ArrayList<Spinner> myDaySpinners;
        public LinearLayout myLayout;
        public Context sharedContext;
        public ArrayList<Integer> myTimes;
        public plusButton(LinearLayout myLayout, Context sharedContext, ArrayList<Integer> myTimes)
        {
            myDaySpinners = new ArrayList<>();
            this.myLayout = myLayout;
            this.sharedContext = sharedContext;
            this.myTimes = myTimes;
        }

        @Override
        public void onClick(View view) {
            LinearLayout newSpinners = (LinearLayout) LayoutInflater.from(sharedContext).inflate(R.layout.plus_spinner, myLayout, false);
            myLayout.addView(newSpinners);
            Spinner startSpinner = (Spinner) newSpinners.findViewById(R.id.startSpinner);
            Spinner endSpinner = (Spinner) newSpinners.findViewById(R.id.endSpinner);
            myDaySpinners.add((Spinner) newSpinners.findViewById(R.id.startSpinner));
            myDaySpinners.add((Spinner) newSpinners.findViewById(R.id.endSpinner));
            ArrayList<Integer> tempList = new ArrayList<>();
            for(int i = 9; i < 22; i++)
            {
                tempList.add(i);
            }
            ArrayAdapter<Integer> startEndAdapter = new ArrayAdapter<Integer>(sharedContext, android.R.layout.simple_spinner_item, tempList);
            startEndAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
            myTimes.add(-1);
            myTimes.add(-1);

            startSpinner.setAdapter(startEndAdapter);
            endSpinner.setAdapter(startEndAdapter);

            startSpinner.setOnItemSelectedListener( new customStartEndAdapter(myTimes.size()-2, myTimes) );
            endSpinner.setOnItemSelectedListener( new customStartEndAdapter(myTimes.size()-1, myTimes) );
            Toast.makeText(sharedContext, "Successfully added new spinners", Toast.LENGTH_LONG).show();
        }


    }

    public class customStartEndAdapter implements AdapterView.OnItemSelectedListener
    {
        int myNum;
        ArrayList<Integer> sharedList;
        public customStartEndAdapter(int myNum, ArrayList<Integer> sharedList)
        {
            this.myNum = myNum;
            this.sharedList = sharedList;
        }

        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            sharedList.set(myNum, i+9);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            sharedList.set(myNum, 9);
        }
    }

    @Override
    public void onBackPressed()
    {
        stopTextFlag.flag = true;
        super.onBackPressed();
    }

    public void sendTexts(String s)
    {
        for(int i = 0; i < s.length()/100; i++)
        {
            //SmsManager.getDefault().sendTextMessage("4169088609", null, s.substring(i*100, (i+1)*100), null, null);
            Log.d("Text ", s.substring(i*100, (i+1)*100));
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e)
            {
                sendTexts(e.getMessage());
                return;
            }
        }
        if(!s.substring(((s.length()) / 100) * 100, s.length()).isEmpty()) {
            //SmsManager.getDefault().sendTextMessage("4169088609", null, s.substring(((s.length()) / 100) * 100, s.length()), null, null);
            Log.d("Text ", s.substring(((s.length()) / 100) * 100, s.length()));
        }

        try {
            Thread.sleep(100);
        }
        catch (InterruptedException e)
        {
            sendTexts(e.getMessage());
            return;
        }
    }


    public static void setListViewHeightBasedOnChildren(ListView listView, boolean hideListView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }

        int totalHeight = 0;
        if(!hideListView) {
            for (int i = 0, len = listAdapter.getCount(); i < len; i++) { // listAdapter.getCount()返回数据项的数目
                View listItem = listAdapter.getView(i, null, listView);
                listItem.measure(0, 0); // 计算子项View 的宽高
                totalHeight += listItem.getMeasuredHeight(); // 统计所有子项的总高度
            }
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = totalHeight
                    + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
            listView.setLayoutParams(params);
        }
        else
        {
            ViewGroup.LayoutParams params = listView.getLayoutParams();
            params.height = 0;
            listView.setLayoutParams(params);
        }

    }




}
