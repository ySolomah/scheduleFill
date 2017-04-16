package com.uoft.gobblgobble.schedulefiller;

import android.*;
import android.Manifest;
import android.accounts.AccountManager;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.icu.text.LocaleDisplayNames;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsCallback;
import android.support.customtabs.CustomTabsClient;
import android.support.customtabs.CustomTabsIntent;
import android.support.customtabs.CustomTabsServiceConnection;
import android.support.customtabs.CustomTabsSession;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fasterxml.jackson.core.util.ByteArrayBuilder;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.Scopes;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.ExponentialBackOff;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class calender_sync extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    public String myID;

    public static class CalendarModelHelper
    {
        public com.google.api.services.calendar.model.Calendar mCalendar;
        public CalendarModelHelper() { this.mCalendar = null; }
    }

    public static class EventsHelper
    {
        public Events events;
        public EventsHelper()
        {
            this.events = null;
        }
    }

    public static class CalendarHelper
    {
        public Calendar myCalendar;
        public CalendarHelper()
        {
            myCalendar = null;
        }
    }
    public static class ClassHolder
    {
        public String courseCode;
        public String courseTitle;
        public String courseDescrip;
        public String coursePrereqs;
        public String courseSection;
        public String courseSession;
        public ArrayList<ClassEvent> myEvents;
        public ClassHolder(String courseCode, String courseTitle, String courseDescrip, String coursePrereqs, String courseSection, String courseSession)
        {
            this.myEvents = new ArrayList<>();
            this.courseCode = courseCode;
            this.courseTitle = courseTitle;
            this.courseDescrip = courseDescrip;
            this.coursePrereqs = coursePrereqs;
            this.courseSection = courseSection;
            this.courseSession = courseSession;
        }
        public String Summarize()
        {
            return("Code: " + courseCode + "\n" + "Course Title:" + courseTitle + "\n" + "Course Section: " +  courseSection);
        }
    }
    public static class ClassEvent
    {

        public String startTime;
        public String endTime;
        public String day;
        public String lecTutPra;
        public ClassEvent(String startTime, String endTime, String day, String lecTutPra)
        {
            this.startTime = startTime;
            this.endTime = endTime;
            this.day = day;
            this.lecTutPra = lecTutPra;
        }
    }

    GoogleAccountCredential mCredential;
    SharedPreferences sharedPreferences;

    public int dummyCounter;
    public ArrayList<display_schedulable_courses.extendedCondensedWithoutMatchInfo> dataSyncList;
    public ArrayList<ClassHolder> coursesToSyncUp;

    public String mainID;

    static final int REQUEST_ACCOUNT_PICKER = 1000;
    static final int REQUEST_AUTHORIZATION = 1001;
    static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
    static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

    private static String[] SCOPES = {CalendarScopes.CALENDAR, CalendarScopes.CALENDAR_READONLY, };

    private static final String PREF_ACCOUNT_NAME = "accountName";

    ProgressDialog mProgress;



    public static final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";

    public LinearLayout mainHolder;
    public CalendarModelHelper ourCalendarSelection;
    public CalendarListEntry ourPrexistingCalendar;

    @Override
    public void onPermissionsDenied(int a, List<String> b) {
        Toast.makeText(calender_sync.this, "Permission denied", Toast.LENGTH_LONG).show();
    }
    @Override
    public void onPermissionsGranted(int a, List<String> b) {
        Toast.makeText(calender_sync.this, "Permission granted", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender_sync);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ourCalendarSelection = new CalendarModelHelper();
        ourPrexistingCalendar = null;

        dataSyncList = getIntent().getParcelableArrayListExtra("coursesToSync");
        coursesToSyncUp = new ArrayList<>();

        for(int i = 0; i < dataSyncList.size(); i++)
        {
            ClassHolder nextCourse = new ClassHolder(dataSyncList.get(i).courseCode,
                    dataSyncList.get(i).courseTitle,
                    dataSyncList.get(i).courseDescrip,
                    dataSyncList.get(i).coursePrereqs,
                    dataSyncList.get(i).courseSection,
                    dataSyncList.get(i).courseSession
                    );
            nextCourse.myEvents.addAll(getAllEvents(dataSyncList.get(i)));
            coursesToSyncUp.add(nextCourse);
        }

        for(int m = 0; m < coursesToSyncUp.size(); m++)
        {
            ClassHolder myClass = coursesToSyncUp.get(m);
            Log.d("CourseInfo", "Code: " + myClass.courseCode + " \nDescrip: " + myClass.courseDescrip + " \nPrereq: " + myClass.coursePrereqs + " \nTitle: " + myClass.courseTitle + " \nSection: " + myClass.courseSection + " \nSession: " + myClass.courseSession);
            for(int n = 0; n < myClass.myEvents.size(); n++)
            {
                Log.d(myClass.myEvents.get(n).lecTutPra, "Day: " + myClass.myEvents.get(n).day + " Start: " + myClass.myEvents.get(n).startTime + " End: " + myClass.myEvents.get(n).endTime);
            }
        }



        FloatingActionButton mySyncButton = (FloatingActionButton) findViewById(R.id.fab);
        mySyncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getResultsFromApi();
                Toast.makeText(calender_sync.this, "Getting calendar events", Toast.LENGTH_LONG).show();
            }
        });

        sharedPreferences =
                PreferenceManager.getDefaultSharedPreferences(this);

        dummyCounter = 0;

        dummyCounter = sharedPreferences.getInt("dummyCount", 0);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("dummyCount", dummyCounter + 1);
        editor.commit();

        boolean tokenCheck = sharedPreferences.getBoolean("HAVE_AUTHORIZATION",false);

        LinearLayout contentHolder = (LinearLayout) findViewById(R.id.contentHolderCalenderSync);
        this.mainHolder = contentHolder;

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("Calling Google Calendar API...");

        mCredential = GoogleAccountCredential.usingOAuth2(getApplicationContext(), Arrays.asList(CalendarScopes.CALENDAR)).setBackOff(new ExponentialBackOff());

        getResultsFromApi();

/*
        speedUpChromeTabs();

        CustomTabsClient.bindCustomTabsService(this, CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);

        mCustomTabsServiceConnection.onCustomTabsServiceConnected();

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Please login using the following page!", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        WebView loginView = (WebView) findViewById(R.id.googleLogin);
        loginView.getSettings().setJavaScriptEnabled(true);


        String clientId = "520022971062-0oe9q78f1b72f917kuf8igfujb2tlman.apps.googleusercontent.com";
        //String clientId = "520022971062-lv9e8aju30s57vudcg4thvtbqnsdefc9.apps.googleusercontent.com"; //ANDROID REG
        String clientSecret =  "2WpqiXlNZUxxzIhwrF_Wubbq";
        //String redirectUri = "https://logincallback.com";
        //String redirectUri = "com.uoft.gobblgobble.schedulefiller:/AccessTokenReceiver";
        String redirectUri = "https:www//google.ca";
        String baseUrl =  "https://accounts.google.com/o/oauth2/v2/auth?";
        String scope =  "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fcalendar";
        String prompt = "consent";
        String responseType = "code";
        String accessType = "offline";

        //clientid: 520022971062-0oe9q78f1b72f917kuf8igfujb2tlman.apps.googleusercontent.com
        //client_secret: 2WpqiXlNZUxxzIhwrF_Wubbq
        //https://accounts.google.com/o/oauth2/v2/auth?redirect_uri=https://google.ca&prompt=consent&response_type=code&client_id=520022971062-0oe9q78f1b72f917kuf8igfujb2tlman.apps.googleusercontent.com&scope=&access_type=offline
        boolean tokenCheck = sharedPreferences.getBoolean("HAVE_AUTHORIZATION",false);

        if(tokenCheck == false) {
            customWebViewClient ourClient = new customWebViewClient(this,
                    "520022971062-0oe9q78f1b72f917kuf8igfujb2tlman.apps.googleusercontent.com",
                    "2WpqiXlNZUxxzIhwrF_Wubbq",
                    "https://google.ca",
                    "https://accounts.google.com/o/oauth2/v2/auth?",
                    "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fcalendar",
                    "consent",
                    "code",
                    "offline");

            //loginView.setWebViewClient(ourClient);
            //loginView.loadUrl(baseUrl + "redirect_uri=" + redirectUri + "&prompt=" + prompt + "&response_type=" + responseType + "&client_id=" + clientId + "&scope=" + scope + "&access_type=" + accessType);
            String oauthUrl = baseUrl + "redirect_uri=" + redirectUri + "&prompt=" + prompt + "&response_type=" + responseType + "&client_id=" + clientId + "&scope=" + scope + "&access_type=" + accessType;
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.enableUrlBarHiding();
            builder.setToolbarColor(Color.BLUE);

            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(oauthUrl));
        }
        */
    }

    private boolean isGooglePlayServicesAvailable() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        return connectionStatusCode == ConnectionResult.SUCCESS;
    }

    private void acquireGooglePlayServices() {
        GoogleApiAvailability apiAvailability =
                GoogleApiAvailability.getInstance();
        final int connectionStatusCode =
                apiAvailability.isGooglePlayServicesAvailable(this);
        if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
            showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
        }
    }

    void showGooglePlayServicesAvailabilityErrorDialog(
            final int connectionStatusCode) {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        Dialog dialog = apiAvailability.getErrorDialog(
                calender_sync.this,
                connectionStatusCode,
                REQUEST_GOOGLE_PLAY_SERVICES);
        dialog.show();
    }

    private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
        private com.google.api.services.calendar.Calendar mService = null;
        private Exception mLastError = null;

        protected TextView CreateNewTextView()
        {
            final TextView errorTextView = (TextView) LayoutInflater.from(calender_sync.this).inflate(R.layout.error_text_view, mainHolder, false);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainHolder.addView(errorTextView);
                }
            });
            errorTextView.setText("helloWorld! \n");
            return(errorTextView);
        }

        protected TextView ChangeTextValue(final String newInfo, final TextView errorTextView)
        {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    errorTextView.setText(newInfo);
                }
            });
            return(errorTextView);
        }

        MakeRequestTask(final GoogleAccountCredential credential) {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

            final TextView errorTextView = CreateNewTextView();

            Thread newRunnerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        final String token = credential.getToken();
                        Log.d("TOKEN", token);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ChangeTextValue("Credential: " + credential.getSelectedAccountName() + "\n" + "Scope: " + credential.getScope() + "\nToken: " + token, errorTextView);
                            }
                        });
                    }

                    catch (GoogleAuthException e)
                    {
                        ChangeTextValue(errorTextView.getText() + "\nERROR: " + e.getMessage(), errorTextView);
                    }
                    catch (IOException e)
                    {
                        ChangeTextValue(errorTextView.getText() + "\nERROR: " + e.getMessage(), errorTextView);
                    }
                }
            });
            newRunnerThread.start();


            mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, credential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();

            if(mService == null) { Toast.makeText(calender_sync.this, "mSERVICE IS NULL", Toast.LENGTH_LONG).show(); }
        }

        /**
         * Background task to call Google Calendar API.
         * @param params no parameters needed for this task.
         */
        @Override
        protected List<String> doInBackground(Void... params) {
            try {
                List<String> courseListings = grabCourseCalendar();
                courseListings.addAll(getDataFromApi());
                return(courseListings);
                //return (getDataFromApi());
            } catch (Exception e) {
                mLastError = e;
                cancel(true);
                return null;
            }
        }

        /**
         * Fetch a list of the next 10 events from the primary calendar.
         * @return List of Strings describing returned events.
         * @throws IOException
         */
        private List<String> grabCourseCalendar() throws IOException
        {
            return( new ArrayList<String>() );
            //calendarToBeAdded.setTimeZone("Canada/EST");
            //calendarToBeAdded.setId("TestNewCalendar");
        }

        private List<String> getDataFromApi() throws IOException {
            TextView errorTextView = CreateNewTextView();
            //mainHolder.addView(errorTextView);
            //errorTextView.setText("Getting Data!");
            ChangeTextValue("Getting Data!", errorTextView);
            Log.d("Added", "ADDED: " + errorTextView.getText());
            // List the next 10 events from the primary calendar.
            DateTime now = new DateTime(System.currentTimeMillis());
            ArrayList<String> eventStrings = new ArrayList<String>();
            Log.d("Added", "GETTING DATA NOW FROM: " + myID);

            Events events = mService.events().list(myID)
                    .setOrderBy("startTime")
                    .setSingleEvents(true)
                    .execute();
            List<Event> items = events.getItems();

            Log.d("Added", "GOT DATA NOW");


            for (Event event : items) {
                Log.d("Added", "ADDING NEXT EVENT");

                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    // All-day events don't have start times, so just use
                    // the start date.
                    start = event.getStart().getDate();
                }
                eventStrings.add(
                        String.format("%s (%s)", event.getSummary(), start));
            }
            return eventStrings;
        }


        @Override
        protected void onPreExecute() {
            //mOutputText.setText("");
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mainHolder.removeAllViews();
                }
            });
            //mProgress.show();
            TextView errorTextView = CreateNewTextView();
            //mainHolder.addView(errorTextView);
            //errorTextView.setText("Pre Execute completed");
            ChangeTextValue("Pre Execute Completed", errorTextView);
            TextView infoTextView = CreateNewTextView();
            if(ourCalendarSelection != null && ourCalendarSelection.mCalendar != null)
            {
                ChangeTextValue("New Calendar: " + ourCalendarSelection.mCalendar.getSummary(), infoTextView);
            }
            if(ourPrexistingCalendar != null)
            {
                ChangeTextValue("Selected Calendar: " + ourPrexistingCalendar.getSummary(), infoTextView);
            }
            Log.d("Added", "ADDED: " + errorTextView.getText());

        }

        @Override
        protected void onPostExecute(List<String> output) {
            mProgress.hide();
            if (output == null || output.size() == 0) {
                TextView errorTextView = CreateNewTextView();
                //mainHolder.addView(errorTextView);
                ChangeTextValue("No results", errorTextView);
            } else {
                output.add(0, "Data retrieved using the Google Calendar API:");
                for(String s : output)
                {
                    TextView errorTextView = CreateNewTextView();
                    //mainHolder.addView(errorTextView);
                    ChangeTextValue(s, errorTextView);
                    Log.d("Added", "ADDED: " + errorTextView.getText());
                }
            }
        }

        @Override
        protected void onCancelled() {
            mProgress.hide();
            if (mLastError != null) {
                if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                    showGooglePlayServicesAvailabilityErrorDialog(
                            ((GooglePlayServicesAvailabilityIOException) mLastError)
                                    .getConnectionStatusCode());
                } else if (mLastError instanceof UserRecoverableAuthIOException) {
                    startActivityForResult(
                            ((UserRecoverableAuthIOException) mLastError).getIntent(),
                            calender_sync.REQUEST_AUTHORIZATION);
                } else if (mLastError instanceof IOException){
                    Toast.makeText(calender_sync.this, "IO EXCEPTION", Toast.LENGTH_LONG).show();
                    Log.d("ErrorInfo", mLastError.getMessage() + "\n" + mLastError.getLocalizedMessage());
                    Toast.makeText(calender_sync.this, "The following error occurred:\n"
                            + mLastError.getMessage() + " " + mLastError.getLocalizedMessage(), Toast.LENGTH_LONG).show();

                    TextView errorTextView = CreateNewTextView();
                    ChangeTextValue("The following error occurred:\n"
                            + mLastError.getMessage() + " " + mLastError.getLocalizedMessage(), errorTextView);
                } else {
                    TextView errorTextView = CreateNewTextView();
                    ChangeTextValue("The following error occurred:\n"
                            + mLastError.getMessage() + " " + mLastError.getLocalizedMessage(), errorTextView);
                    Log.d("ErrorInfo", mLastError.getMessage() + "\n" + mLastError.getLocalizedMessage());
                }
            } else {
                TextView errorTextView = CreateNewTextView();
                ChangeTextValue("Request Cancelled", errorTextView);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode)
        {
            case REQUEST_GOOGLE_PLAY_SERVICES:
                if(resultCode != RESULT_OK)
                {
                    TextView errorTextView = (TextView) LayoutInflater.from(calender_sync.this).inflate(R.layout.error_text_view, mainHolder, false);
                    mainHolder.addView(errorTextView);
                    errorTextView.setText("Please install google play services!");
                }
                else
                {
                    getResultsFromApi();
                }
                break;
            case REQUEST_ACCOUNT_PICKER:
                if(resultCode == RESULT_OK && data != null && data.getExtras() != null)
                {
                    String accName = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                    if(accName != null)
                    {
                        SharedPreferences settings = getPreferences(Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString(PREF_ACCOUNT_NAME, accName);
                        editor.apply();
                        mCredential.setSelectedAccountName(accName);
                        getResultsFromApi();
                    }
                }
                break;
            case REQUEST_AUTHORIZATION:
                if(resultCode == RESULT_OK)
                {
                    getResultsFromApi();
                }
                break;
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
    private void chooseAccount() {
        if (EasyPermissions.hasPermissions(
                this, Manifest.permission.GET_ACCOUNTS)) {
            String accountName = getPreferences(Context.MODE_PRIVATE)
                    .getString(PREF_ACCOUNT_NAME, null);
            if (accountName != null) {
                mCredential.setSelectedAccountName(accountName);
                Log.d("Set account", "SET TO: " + mCredential.getSelectedAccountName());
                getResultsFromApi();
            } else {
                Log.d("Set account", "PICKING ACCOUNT");

                // Start a dialog from which the user can choose an account
                startActivityForResult(
                        mCredential.newChooseAccountIntent(),
                        REQUEST_ACCOUNT_PICKER);
            }
        } else {
            // Request the GET_ACCOUNTS permission via a user dialog
            EasyPermissions.requestPermissions(
                    this,
                    "This app needs to access your Google account (via Contacts).",
                    REQUEST_PERMISSION_GET_ACCOUNTS,
                    android.Manifest.permission.GET_ACCOUNTS);
        }
    }

    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    protected ArrayList<ClassEvent> getAllEvents(display_schedulable_courses.extendedCondensedWithoutMatchInfo myCourse)
    {
        Log.d("CourseInfo", "Course is: \n" + myCourse.courseCode + " Lecture time: \n" + myCourse.lectureTimes);
        ArrayList<ClassEvent> eventsToReturn = new ArrayList<>();
        Log.d("Outputting Lectures", "Now");
        eventsToReturn.addAll(parseCourseInfo(myCourse.lectureTimes, "Lecture"));
        Log.d("Outputting Tutorials", "Now");
        eventsToReturn.addAll(parseCourseInfo(myCourse.tutorialTimes, "Tutorial"));
        Log.d("Outputting Practicals", "Now");
        eventsToReturn.addAll(parseCourseInfo(myCourse.practicalTimes, "Practical"));
        return (eventsToReturn);
    }
    protected ArrayList<ClassEvent> parseCourseInfo(String info, String lecTutPra)
    {
        ArrayList<ClassEvent> eventsToReturn =  new ArrayList<>();
        String[] lectureTimeSplit = info.split("[ \n]");
        int totalNumberOfLectures = (lectureTimeSplit.length - 3)/5;
        Log.d("Total: ", Integer.toString(totalNumberOfLectures));
        int sillyCounter = 0;
        for(int k = 0; k < totalNumberOfLectures; k++)
        {
            if(k*5+3+4+sillyCounter < lectureTimeSplit.length) {
                Log.d(lecTutPra, "Day: " + lectureTimeSplit[k * 5 + 3 + sillyCounter] + " Start: " + lectureTimeSplit[k * 5 + 3 + 2 + sillyCounter] + " End: " + lectureTimeSplit[k * 5 + 3 + 4 + sillyCounter]);
                eventsToReturn.add(new ClassEvent(lectureTimeSplit[k * 5 + 3 + 2 + sillyCounter], lectureTimeSplit[k * 5 + 3 + 4 + sillyCounter], lectureTimeSplit[k * 5 + 3 + sillyCounter], lecTutPra));
            }
            if(k*5+5+3+sillyCounter < lectureTimeSplit.length)
            {
                if(lectureTimeSplit[k*5+5+3+sillyCounter].isEmpty())
                {
                    sillyCounter++;
                }
            }
        }
        int sillyCount = 0;
        for(int j = 0; j < lectureTimeSplit.length; j++)
        {
            if(lectureTimeSplit[j].isEmpty())
            {
                sillyCount++;
                Log.d("Silly", "At: " + j + " Which equals: " + lectureTimeSplit[j]);
            }
            Log.d("Info: " + j, "Next: " + lectureTimeSplit[j]);
        }
        return (eventsToReturn);
    }

    private void getResultsFromApi() {
        if (!isGooglePlayServicesAvailable()) {
            acquireGooglePlayServices();
        } else if (mCredential.getSelectedAccountName() == null) {
            chooseAccount();
        } else if (!isDeviceOnline()) {
            TextView errorTextView = (TextView) LayoutInflater.from(calender_sync.this).inflate(R.layout.error_text_view, mainHolder, false);
            mainHolder.addView(errorTextView);
            errorTextView.setText("No internet connection");
        } else {
            HttpTransport transport = AndroidHttp.newCompatibleTransport();
            JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
            final Calendar mService = new com.google.api.services.calendar.Calendar.Builder(
                    transport, jsonFactory, mCredential)
                    .setApplicationName("Google Calendar API Android Quickstart")
                    .build();
            Thread networkThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    //final ProgressDialog mProgressDialog = new ProgressDialog(calender_sync.this);
                    final ArrayList<String> returnEventInfo = new ArrayList<>();
                    final com.google.api.services.calendar.model.Calendar calendarToBeAdded = new com.google.api.services.calendar.model.Calendar();
                    calendarToBeAdded.setSummary("Dummy123" + dummyCounter);
                    Log.d("Scopes", mCredential.getScope());
                    Log.d("CalendarList", "Getting calendar list");
                    try {
                        //mService.calendars().insert(calendarToBeAdded).execute();
                        final CalendarList calendarList = mService.calendarList().list().setPageToken(null).execute();
                        Log.d("CalendarList", "Got Calendar list");
                        final List<CalendarListEntry> items = calendarList.getItems();
                        String[] entriesForDialog = new String[items.size() + 1];
                        entriesForDialog[items.size()] = "Create New Calendar";
                        Log.d("Got List Items", "Length is: " + items.size());
                        int count = 0;
                        for (final CalendarListEntry item : items) {
                            Log.d("CalendarEntry", "Next Calendar: " + item.getSummary() + "Id : " + item.getId());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    TextView errorTextView = (TextView) LayoutInflater.from(calender_sync.this).inflate(R.layout.error_text_view, mainHolder, false);
                                    mainHolder.addView(errorTextView);
                                    errorTextView.setText("Next Calendar: " + item.getSummary() + "Id : " + item.getId());
                                }
                            });
                            if (count == 0) {
                                entriesForDialog[count] = "Primary";
                            } else {
                                entriesForDialog[count] = item.getSummary();
                            }
                            count++;
                            Log.d("CalendarEntry", "Next Calendar: " + item.getSummary() + "Id : " + item.getId());
                        }
                        final display_schedulable_courses.intClass SelectedInt = new display_schedulable_courses.intClass();
                        final display_schedulable_courses.stringClass newCalendarName = new display_schedulable_courses.stringClass();
                        SelectedInt.myInt = 0;
                        final AlertDialog.Builder selectMenu = new AlertDialog.Builder(calender_sync.this);
                        selectMenu.setTitle("Select Calendar");
                        selectMenu.setCancelable(false);
                        selectMenu.setSingleChoiceItems(entriesForDialog, 0, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(final DialogInterface dialogInterface, int i) {
                                SelectedInt.myInt = ((AlertDialog) dialogInterface).getListView().getCheckedItemPosition();
                                Log.d("SelectedInt", Integer.toString(SelectedInt.myInt));
                                if (SelectedInt.myInt == items.size()) {
                                    Log.d("New Calendar", "");
                                    LinearLayout editTextHolder = (LinearLayout) LayoutInflater.from(calender_sync.this).inflate(R.layout.edit_text, null);
                                    final AlertDialog.Builder editTextDialog = new AlertDialog.Builder(calender_sync.this);
                                    editTextDialog.setView(editTextHolder);
                                    final EditText editTextBox = (EditText) editTextHolder.findViewById(R.id.editText);
                                    editTextBox.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                            imm.showSoftInput(editTextBox, 0);
                                        }
                                    });
                                    newCalendarName.myString = editTextBox.getText().toString();
                                    editTextDialog.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    editTextDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
                                        @Override
                                        public void onDismiss(DialogInterface dialogInterfaceInner) {
                                            newCalendarName.myString = editTextBox.getText().toString();
                                            dialogInterface.dismiss();
                                        }
                                    });
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            editTextDialog.create().show();
                                        }
                                    });
                                } else {
                                    dialogInterface.dismiss();
                                }
                            }
                        });
                        selectMenu.setPositiveButton("Finish", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        selectMenu.setOnDismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                //try {
                                    Log.d("SelectedInt", "Is: " + SelectedInt.myInt);

                                    String ID;

                                    if (SelectedInt.myInt == items.size()) {
                                        Log.d("New Calendar Name", newCalendarName.myString);
                                        calendarToBeAdded.setTimeZone("America/New_York");
                                        calendarToBeAdded.setSummary(newCalendarName.myString);
                                        Log.d("New Calendar Name2", newCalendarName.myString);

                                        final CalendarModelHelper createdCalendar = new CalendarModelHelper();
                                        Log.d("New Calendar Name3", newCalendarName.myString);

                                        /*runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mProgress.setMessage("Adding new calendar...");
                                                mProgress.show();
                                            }
                                        });*/

                                        final display_schedulable_courses.flagClass ourFinFlag = new display_schedulable_courses.flagClass();
                                        ourFinFlag.flag = false;

                                        Thread newNetworkThread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    Log.d("InsertingNewCalendar", "now");
                                                    /*try {
                                                        Log.d("InTry", "TryingToGetToken");
                                                        String ourToken = mCredential.getToken();
                                                        Log.d("TOKEN", ourToken);

                                                        String data = "summary=" + calendarToBeAdded.getSummary();
                                                        String encodedData = URLEncoder.encode(data, "UTF-8");
                                                        URL postURL = new URL("https://googleapis.com/calendar/v3/calendars");
                                                        HttpURLConnection conn = (HttpURLConnection) postURL.openConnection();
                                                        conn.setDoOutput(true);
                                                        conn.setRequestMethod("POST");
                                                        String credentials = mCredential.getSelectedAccountName() + ":" + ourToken;
                                                        String basicAuth = "Basic " + new String(Base64.encode(credentials.getBytes(), Base64.DEFAULT));
                                                        conn.setRequestProperty("Authorization", basicAuth);
                                                        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                                                        conn.setRequestProperty("Content-Length", String.valueOf(encodedData.length()));
                                                        OutputStream os = conn.getOutputStream();
                                                        os.write(encodedData.getBytes());

                                                        conn.getInputStream();
                                                        BufferedInputStream inputStream = new BufferedInputStream(conn.getInputStream());
                                                        StringBuilder buffer = new StringBuilder();
                                                        boolean finishedReading = false;
                                                        int readIn = 0;
                                                        while(!finishedReading)
                                                        {
                                                            readIn = inputStream.read();
                                                            if(readIn == -1) { finishedReading = true; break; }
                                                            else
                                                            {
                                                                buffer.append((char) readIn);
                                                            }
                                                        }

                                                        Log.d("Response", buffer.toString());
                                                    }
                                                    catch (GoogleAuthException e) { Log.d("FailedGettingToken", "failed"); }
                                                    */
                                                    //createdCalendar.mCalendar = mService.calendars().insert(calendarToBeAdded).execute();
                                                    insertNewCalendar(calendarToBeAdded, createdCalendar);
                                                    Log.d("Creating Event", "Creating event now...");
                                                    /*final Event event = new Event();
                                                    Log.d("New event", "New event created");
                                                    event.setSummary("Dummy Event");
                                                    event.setLocation("University of Toronto St George");
                                                    event.setDescription("Hello World!");
                                                    Log.d("New event", "ParametersSet");
                                                    ArrayList<EventAttendee> attendees = new ArrayList<>();
                                                    Log.d("SelectedAcc", mCredential.getSelectedAccountName());
                                                    attendees.add(new EventAttendee().setEmail(mCredential.getSelectedAccountName()));
                                                    event.setStart(new EventDateTime().setDateTime(DateTime.parseRfc3339("2017-04-08T10:00:00.000-07:00")).setTimeZone("America/New_York"));
                                                    event.setEnd(new EventDateTime().setDateTime(DateTime.parseRfc3339("2017-04-08T10:30:00.000-07:00")).setTimeZone("America/New_York"));
                                                    event.setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;UNTIL=20170421T170000Z"));*/

                                                    Log.d("NewCalendarInserted", "adg");
                                                    while(createdCalendar.mCalendar == null) { }
                                                    ourCalendarSelection.mCalendar = createdCalendar.mCalendar;
                                                    //createNewEvent(mService, event, createdCalendar.mCalendar.getId());
                                                    for(int h = 0; h < coursesToSyncUp.size(); h++)
                                                    {
                                                        for(int g = 0; g < coursesToSyncUp.get(h).myEvents.size(); g++)
                                                        {
                                                            /*ClassEvent myEvent = coursesToSyncUp.get(h).myEvents.get(g);
                                                            Event nextCourseToAdd = new Event();
                                                            nextCourseToAdd.setSummary(myEvent.lecTutPra + "\n" + coursesToSyncUp.get(h).Summarize());
                                                            nextCourseToAdd.setLocation("University of Toronto St. George");
                                                            nextCourseToAdd.setDescription(myEvent.lecTutPra + "\n" +coursesToSyncUp.get(h).courseDescrip);
                                                            ArrayList<EventAttendee> attendees = new ArrayList<EventAttendee>();
                                                            attendees.add(new EventAttendee().setEmail(mCredential.getSelectedAccountName()));
                                                            String start;
                                                            String end;
                                                            int sectionCase = 0;
                                                            if(coursesToSyncUp.get(h).courseSection.contains("F"))
                                                            {
                                                                start = "2017-09-";
                                                                end = "2017-09-";
                                                                sectionCase = 0;
                                                            }
                                                            else if (coursesToSyncUp.get(h).courseSection.contains("S"))
                                                            {
                                                                start = "2018-01-";
                                                                end = "2018-01-";
                                                                sectionCase = 1;
                                                            }
                                                            else
                                                            {
                                                                start = "2017-09-";
                                                                end = "2017-09-";
                                                                sectionCase = 2;
                                                            }
                                                            int forward = 0;
                                                            if (myEvent.day.contains("Mon"))
                                                            {
                                                                forward = 0;
                                                            }
                                                            else if(myEvent.day.contains("Tues"))
                                                            {
                                                                forward = 1;
                                                            }
                                                            else if(myEvent.day.contains("Wed"))
                                                            {
                                                                forward = 2;
                                                            }
                                                            else if(myEvent.day.contains("Thur"))
                                                            {
                                                                forward = 3;
                                                            }
                                                            else if(myEvent.day.contains("Fri"))
                                                            {
                                                                forward = 4;
                                                            }
                                                            else
                                                            {
                                                                forward = 0;
                                                            }
                                                            int startDate = 0;
                                                            if(start.contains("2017"))
                                                            {
                                                                startDate = 4;
                                                                //"2017-04-08T10:00:00.000-07:00"

                                                            }
                                                            else
                                                            {
                                                                startDate = 1;
                                                            }
                                                            if(myEvent.startTime.length() == 1)
                                                            {
                                                                myEvent.startTime = "0" + myEvent.startTime;
                                                            }
                                                            start = start + "0" + Integer.toString(startDate+forward) + "T" + myEvent.startTime + ":00:00.000-04:00";
                                                            Log.d("StartTime", start);
                                                            end = end + "0" + Integer.toString(startDate+forward) + "T" + myEvent.endTime + ":00:00.000-04:00";
                                                            Log.d("EndTime", end);
                                                            nextCourseToAdd.setStart(new EventDateTime().setDateTime(DateTime.parseRfc3339(start)).setTimeZone("America/Toronto"));
                                                            nextCourseToAdd.setEnd(new EventDateTime().setDateTime(DateTime.parseRfc3339(end)).setTimeZone("America/Toronto"));
                                                            if(sectionCase == 0) {
                                                                nextCourseToAdd.setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;UNTIL=20171215T230000Z"));
                                                            }
                                                            else
                                                            {
                                                                nextCourseToAdd.setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;UNTIL=20180415T230000Z"));
                                                            }
                                                            Log.d("CreatedNewEvent", nextCourseToAdd.getSummary());*/
                                                            Event nextCourseToAdd = null;
                                                            if(coursesToSyncUp.get(h).courseSection.contains("Y"))
                                                            {
                                                                nextCourseToAdd = createFallEvent(coursesToSyncUp.get(h).myEvents.get(g), h);
                                                                createNewEvent(mService, nextCourseToAdd, createdCalendar.mCalendar.getId());
                                                                Event nextCourseTwo = createWinterEvent(coursesToSyncUp.get(h).myEvents.get(g), h);
                                                                createNewEvent(mService, nextCourseTwo, createdCalendar.mCalendar.getId());
                                                            }
                                                            else if(coursesToSyncUp.get(h).courseSection.contains("F")) {
                                                                nextCourseToAdd = createFallEvent(coursesToSyncUp.get(h).myEvents.get(g), h);
                                                                createNewEvent(mService, nextCourseToAdd, createdCalendar.mCalendar.getId());
                                                            }
                                                            else
                                                            {
                                                                nextCourseToAdd = createWinterEvent(coursesToSyncUp.get(h).myEvents.get(g), h);
                                                                createNewEvent(mService, nextCourseToAdd, createdCalendar.mCalendar.getId());
                                                            }
                                                        }
                                                    }
                                                    mainID = createdCalendar.mCalendar.getId();
                                                    Log.d("CreatedNewCalendar", "Added Calendar: " + createdCalendar.mCalendar.getId() + " Summary: " + createdCalendar.mCalendar.getSummary());
                                                    myID = createdCalendar.mCalendar.getId();

                                                    Thread newMakeRequest = new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new MakeRequestTask(mCredential).execute();
                                                            Log.d("THREAD", "STARTING NEXT THREAD");
                                                        }
                                                    });
                                                    newMakeRequest.start();
                                                }
                                                catch (IOException e)
                                                {
                                                    Log.d("IOE", "cvs");
                                                }

                                                ourFinFlag.flag = true;
                                            }
                                        });
                                        Log.d("startingThread", newCalendarName.myString);

                                        newNetworkThread.start();
                                        /*Thread waitingThread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                while(!ourFinFlag.flag) {
                                                    try {
                                                        Thread.sleep(100);
                                                    }
                                                    catch (InterruptedException e) { }
                                                }
                                            }
                                        });
                                        waitingThread.start();*/
                                        /*try {
                                            Log.d("joiningThread", newCalendarName.myString);
                                            //newNetworkThread.join();
                                            waitingThread.join();
                                        }
                                        catch (InterruptedException e) { }
                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                mProgress.hide();
                                            }
                                        });*/

                                    } else {
                                        ID = items.get(SelectedInt.myInt).getId();
                                        ourPrexistingCalendar = items.get(SelectedInt.myInt);


                                        Log.d("Creating Event", "Creating event now...");
                                        /*final Event event = new Event();
                                        Log.d("New event", "New event created");
                                        event.setSummary("Dummy Event");
                                        event.setLocation("University of Toronto St George");
                                        event.setDescription("Hello World!");
                                        Log.d("New event", "ParametersSet");
                                        ArrayList<EventAttendee> attendees = new ArrayList<>();
                                        Log.d("SelectedAcc", mCredential.getSelectedAccountName());
                                        attendees.add(new EventAttendee().setEmail(mCredential.getSelectedAccountName()));
                                        event.setStart(new EventDateTime().setDateTime(DateTime.parseRfc3339("2017-04-08T10:00:00.000-07:00")).setTimeZone("America/New_York"));
                                        event.setEnd(new EventDateTime().setDateTime(DateTime.parseRfc3339("2017-04-08T10:30:00.000-07:00")).setTimeZone("America/New_York"));
                                        event.setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;UNTIL=20170421T170000Z"));
                                        Log.d("CreatedEvent", "Event: " + event.getSummary());*/
                                        final EventsHelper events = new EventsHelper();
                                        final String IDcopy = ID;
                                        Thread serviceThread = new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                try {
                                                    //mService.events().insert(IDcopy, event).execute();
                                                    for(int h = 0; h < coursesToSyncUp.size(); h++)
                                                    {
                                                        for(int g = 0; g < coursesToSyncUp.get(h).myEvents.size(); g++)
                                                        {
                                                            /*ClassEvent myEvent = coursesToSyncUp.get(h).myEvents.get(g);
                                                            Event nextCourseToAdd = new Event();
                                                            nextCourseToAdd.setSummary(myEvent.lecTutPra + "\n" + coursesToSyncUp.get(h).Summarize());
                                                            nextCourseToAdd.setLocation("University of Toronto St. George");
                                                            nextCourseToAdd.setDescription(myEvent.lecTutPra + "\n" +coursesToSyncUp.get(h).courseDescrip);
                                                            ArrayList<EventAttendee> attendees = new ArrayList<EventAttendee>();
                                                            attendees.add(new EventAttendee().setEmail(mCredential.getSelectedAccountName()));
                                                            String start;
                                                            String end;
                                                            int sectionCase = 0;
                                                            if(coursesToSyncUp.get(h).courseSection.contains("F"))
                                                            {
                                                                start = "2017-09-";
                                                                end = "2017-09-";
                                                                sectionCase = 0;
                                                            }
                                                            else if (coursesToSyncUp.get(h).courseSection.contains("S"))
                                                            {
                                                                start = "2018-01-";
                                                                end = "2018-01-";
                                                                sectionCase = 1;
                                                            }
                                                            else
                                                            {
                                                                start = "2017-09-";
                                                                end = "2017-09-";
                                                                sectionCase = 2;
                                                            }
                                                            int forward = 0;
                                                            if (myEvent.day.contains("Mon"))
                                                            {
                                                                forward = 0;
                                                            }
                                                            else if(myEvent.day.contains("Tues"))
                                                            {
                                                                forward = 1;
                                                            }
                                                            else if(myEvent.day.contains("Wed"))
                                                            {
                                                                forward = 2;
                                                            }
                                                            else if(myEvent.day.contains("Thur"))
                                                            {
                                                                forward = 3;
                                                            }
                                                            else if(myEvent.day.contains("Fri"))
                                                            {
                                                                forward = 4;
                                                            }
                                                            else
                                                            {
                                                                forward = 0;
                                                            }
                                                            int startDate = 0;
                                                            if(start.contains("2017"))
                                                            {
                                                                startDate = 4;
                                                                //"2017-04-08T10:00:00.000-07:00"

                                                            }
                                                            else
                                                            {
                                                                startDate = 1;
                                                            }
                                                            if(myEvent.startTime.length() == 1)
                                                            {
                                                                myEvent.startTime = "0" + myEvent.startTime;
                                                            }
                                                            start = start + "0" + Integer.toString(startDate+forward) + "T" + myEvent.startTime + ":00:00.000-04:00";
                                                            Log.d("StartTime", start);
                                                            end = end + "0" + Integer.toString(startDate+forward) + "T" + myEvent.endTime + ":00:00.000-04:00";
                                                            Log.d("EndTime", end);
                                                            nextCourseToAdd.setStart(new EventDateTime().setDateTime(DateTime.parseRfc3339(start)).setTimeZone("America/New_York"));
                                                            nextCourseToAdd.setEnd(new EventDateTime().setDateTime(DateTime.parseRfc3339(end)).setTimeZone("America/New_York"));
                                                            if(sectionCase == 0) {
                                                                nextCourseToAdd.setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;UNTIL=20171215T230000Z"));
                                                            }
                                                            else
                                                            {
                                                                nextCourseToAdd.setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;UNTIL=20180415T230000Z"));
                                                            }
                                                            Log.d("CreatedNewEvent", nextCourseToAdd.getSummary());
                                                            Log.d("InsertingIntoCalendar", "ID: " + IDcopy);
                                                            Log.d("InsertingIntoCalendar", "Summary: " + items.get(SelectedInt.myInt).getSummary());

                                                            createNewEvent(mService, nextCourseToAdd, IDcopy);
                                                            */
                                                            Event nextCourseToAdd = null;
                                                            if(coursesToSyncUp.get(h).courseSection.contains("Y"))
                                                            {
                                                                nextCourseToAdd = createFallEvent(coursesToSyncUp.get(h).myEvents.get(g), h);
                                                                createNewEvent(mService, nextCourseToAdd, IDcopy);
                                                                Event nextCourseTwo = createWinterEvent(coursesToSyncUp.get(h).myEvents.get(g), h);
                                                                createNewEvent(mService, nextCourseTwo, IDcopy);
                                                            }
                                                            else if(coursesToSyncUp.get(h).courseSection.contains("F")) {
                                                                nextCourseToAdd = createFallEvent(coursesToSyncUp.get(h).myEvents.get(g), h);
                                                                createNewEvent(mService, nextCourseToAdd, IDcopy);
                                                            }
                                                            else
                                                            {
                                                                nextCourseToAdd = createWinterEvent(coursesToSyncUp.get(h).myEvents.get(g), h);
                                                                createNewEvent(mService, nextCourseToAdd, IDcopy);
                                                            }
                                                        }
                                                    }

                                                    //Log.d("Events", "Grabbing events list");
                                                    //events.events = mService.events().list(IDcopy).execute();
                                                    myID = IDcopy;
                                                    Thread newMakeRequest = new Thread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            new MakeRequestTask(mCredential).execute();
                                                            Log.d("THREAD", "STARTING NEXT THREAD");
                                                        }
                                                    });
                                                    newMakeRequest.start();
                                                } catch (IOException e) {
                                                    Log.d("IOEXCEPT", "");
                                                }


                                            }
                                        });
                                        serviceThread.start();
                                        try {
                                            serviceThread.join();
                                        } catch (InterruptedException e) {
                                        }

                                        //List<Event> calendarItems = events.events.getItems();
                                        /*for (Event nextEvent : calendarItems) {
                                            returnEventInfo.add(nextEvent.getSummary());
                                        }*/
                                        //}
                                /*catch (IOException e) {
                                    Toast.makeText(calender_sync.this, "IOEXCEPTION", Toast.LENGTH_SHORT).show();
                                }*/

                                    }
                            }
                        });
                        //mProgressDialog.dismiss();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                selectMenu.create().show();
                            }
                        });
                    } catch (IOException e) {
                        Log.d("IOEXCEPTION", e.getMessage());
                    }
                }
            });
            networkThread.start();
        }
    }

    protected Event createFallEvent(ClassEvent myEvent, int h)
    {
        //ClassEvent myEvent = coursesToSyncUp.get(h).myEvents.get(g);
        Event nextCourseToAdd = new Event();
        nextCourseToAdd.setSummary(myEvent.lecTutPra + "\n" + coursesToSyncUp.get(h).Summarize());
        nextCourseToAdd.setLocation("University of Toronto St. George");
        nextCourseToAdd.setDescription(myEvent.lecTutPra + "\n" +coursesToSyncUp.get(h).courseDescrip);
        ArrayList<EventAttendee> attendees = new ArrayList<EventAttendee>();
        attendees.add(new EventAttendee().setEmail(mCredential.getSelectedAccountName()));
        String start;
        String end;
        start = "2017-09-";
        end = "2017-09-";
        int forward = 0;
        if (myEvent.day.contains("Mon"))
        {
            forward = 0;
        }
        else if(myEvent.day.contains("Tues"))
        {
            forward = 1;
        }
        else if(myEvent.day.contains("Wed"))
        {
            forward = 2;
        }
        else if(myEvent.day.contains("Thur"))
        {
            forward = 3;
        }
        else if(myEvent.day.contains("Fri"))
        {
            forward = 4;
        }
        else
        {
            forward = 0;
        }
        int startDate = 4;
        if(myEvent.startTime.length() == 1)
        {
            myEvent.startTime = "0" + myEvent.startTime;
        }
        start = start + "0" + Integer.toString(startDate+forward) + "T" + myEvent.startTime + ":00:00.000-04:00";
        Log.d("StartTime", start);
        end = end + "0" + Integer.toString(startDate+forward) + "T" + myEvent.endTime + ":00:00.000-04:00";
        Log.d("EndTime", end);
        nextCourseToAdd.setStart(new EventDateTime().setDateTime(DateTime.parseRfc3339(start)).setTimeZone("America/Toronto"));
        nextCourseToAdd.setEnd(new EventDateTime().setDateTime(DateTime.parseRfc3339(end)).setTimeZone("America/Toronto"));
        nextCourseToAdd.setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;UNTIL=20171215T230000Z"));
        return(nextCourseToAdd);
    }

    protected Event createWinterEvent(ClassEvent myEvent, int h)
    {
        //ClassEvent myEvent = coursesToSyncUp.get(h).myEvents.get(g);
        Event nextCourseToAdd = new Event();
        nextCourseToAdd.setSummary(myEvent.lecTutPra + "\n" + coursesToSyncUp.get(h).Summarize());
        nextCourseToAdd.setLocation("University of Toronto St. George");
        nextCourseToAdd.setDescription(myEvent.lecTutPra + "\n" +coursesToSyncUp.get(h).courseDescrip);
        ArrayList<EventAttendee> attendees = new ArrayList<EventAttendee>();
        attendees.add(new EventAttendee().setEmail(mCredential.getSelectedAccountName()));
        String start;
        String end;
        start = "2018-01-";
        end = "2018-01-";
        int forward = 0;
        if (myEvent.day.contains("Mon"))
        {
            forward = 0;
        }
        else if(myEvent.day.contains("Tues"))
        {
            forward = 1;
        }
        else if(myEvent.day.contains("Wed"))
        {
            forward = 2;
        }
        else if(myEvent.day.contains("Thur"))
        {
            forward = 3;
        }
        else if(myEvent.day.contains("Fri"))
        {
            forward = 4;
        }
        else
        {
            forward = 0;
        }
        int startDate = 1;
        if(myEvent.startTime.length() == 1)
        {
            myEvent.startTime = "0" + myEvent.startTime;
        }
        start = start + "0" + Integer.toString(startDate+forward) + "T" + myEvent.startTime + ":00:00.000-05:00";
        Log.d("StartTime", start);
        end = end + "0" + Integer.toString(startDate+forward) + "T" + myEvent.endTime + ":00:00.000-05:00";
        Log.d("EndTime", end);
        nextCourseToAdd.setStart(new EventDateTime().setDateTime(DateTime.parseRfc3339(start)).setTimeZone("America/Toronto"));
        nextCourseToAdd.setEnd(new EventDateTime().setDateTime(DateTime.parseRfc3339(end)).setTimeZone("America/Toronto"));
        nextCourseToAdd.setRecurrence(Arrays.asList("RRULE:FREQ=WEEKLY;UNTIL=20180415T230000Z"));
        return(nextCourseToAdd);
    }

    protected void syncCalendar(final GoogleAccountCredential credential, ProgressDialog mProgressDialog)
    {
        //mProgressDialog.setMessage("Please Follow Prompts");
        //mProgressDialog.show();
        HttpTransport transport = AndroidHttp.newCompatibleTransport();
        JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
        Calendar mService = new com.google.api.services.calendar.Calendar.Builder(
                transport, jsonFactory, credential)
                .setApplicationName("Google Calendar API Android Quickstart")
                .build();
        insertEvents(mService, mProgressDialog);
    }
    protected void insertEvents(final Calendar mService, ProgressDialog mProgressDialog)
    {

    }

    public void insertNewCalendar(final com.google.api.services.calendar.model.Calendar calendarToBeAdded, final CalendarModelHelper createdCalendar) throws  IOException
    {
        /*Thread addCalendarThread = new Thread(new Runnable() {
            @Override
            public void run() {*/
                try {
                    HttpTransport transport = AndroidHttp.newCompatibleTransport();
                    JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                    final Calendar mService = new com.google.api.services.calendar.Calendar.Builder(
                            transport, jsonFactory, mCredential)
                            .setApplicationName("Google Calendar API Android Quickstart")
                            .build();
                    com.google.api.services.calendar.model.Calendar ourCalendar = mService.calendars().insert(calendarToBeAdded).execute();
                    createdCalendar.mCalendar = ourCalendar;
                }
                catch (IOException e) { Toast.makeText(calender_sync.this, "IOEXCEPT", Toast.LENGTH_LONG).show();}
            /*}
        });
        addCalendarThread.start();*/
    }
    public void createNewEvent(final Calendar mService, final Event event, final String IDcopy) throws IOException
    {
        Thread serviceThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    mService.events().insert(IDcopy, event).execute();
                }
                catch (IOException e) {
                    Log.d("IOEXCEPT", "");
                }


            }
        });
        serviceThread.start();
    }







    @Override
    public void onBackPressed()
    {
        Intent returnIntent = new Intent();
        if(mCredential != null)
        {
            returnIntent.putExtra("code", "-1");
            setResult(Activity.RESULT_OK, returnIntent);
        }
        else
        {
            returnIntent.putExtra("code", "-1");
            setResult(Activity.RESULT_CANCELED, returnIntent);
        }
        super.onBackPressed();
        finish();
    }
}
