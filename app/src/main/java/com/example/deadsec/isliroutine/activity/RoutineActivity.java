package com.example.deadsec.isliroutine.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deadsec.isliroutine.loader.ClassDataLab;
import com.example.deadsec.isliroutine.model.Course;
import com.example.deadsec.isliroutine.model.Lession;
import com.example.deadsec.isliroutine.model.Room;
import com.example.deadsec.isliroutine.model.Teacher;
import com.example.deadsec.isliroutine.model.TimeTable;
import com.example.deadsec.isliroutine.utils.ApiClient;
import com.example.deadsec.isliroutine.utils.ApiInterface;
import com.example.deadsec.isliroutine.utils.NotificationHandler;
import com.example.deadsec.isliroutine.R;
import com.example.deadsec.isliroutine.fragment.DailyClassFragment;
import com.example.deadsec.isliroutine.model.Day;
import com.example.deadsec.isliroutine.utils.PreferenceUtils;
import com.example.deadsec.isliroutine.utils.SilentService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RoutineActivity extends AppCompatActivity {

    public static final String TAG = RoutineActivity.class.getSimpleName();

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ApiInterface apiInterface;
    private ProgressDialog progressDoalog;
    private TextView toolbarTitle;
    private ViewPager mViewPager;
    private int dissmissCounter=0;
    private int dissmissMax=0;
    private int groupIndex;
    private boolean doubleBackPressStatus = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routine);


        progressDoalog = new ProgressDialog(RoutineActivity.this);
        progressDoalog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDoalog.setMax(100);
        groupIndex = getIntent().getIntExtra("GROUPINDEX", 0);
        if (groupIndex == 0) {
            groupIndex = PreferenceUtils.get(this).getGroupYear();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (PreferenceUtils.get(this).getTimeTableInitialized() != true) {
            progressDoalog.setMessage("This usually takes less than a second ");
            progressDoalog.setTitle("Downloading Classes");
            progressDoalog.show();
            loadTimeTable(groupIndex);
        } else {
            init();
        }
    }

    public void init() {

        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        toolbarTitle=(TextView)findViewById(R.id.toolbar_title);
        String groupName =ClassDataLab.get(this).getGroupName(String.valueOf(PreferenceUtils.get(this).getGroupYear())).toLowerCase();
        String coloredText = "<font color=#ed3237>Isli</font> <font color=#3e4095>Routine</font>  <font color=#3e4095>"+groupName+"</font>" ;
        toolbarTitle.setText(Html.fromHtml(coloredText));

        //NotificationHandler.scheduleNotification(this,  5000);
        startService(new Intent(this, SilentService.class));

        //mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        //tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        mViewPager.setOffscreenPageLimit(7);
        setupViewPager(mViewPager);
        tabLayout.setupWithViewPager(mViewPager);
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        mViewPager.setCurrentItem(day, true);

    }

    private void setupViewPager(ViewPager viewPager) {
        mSectionsPagerAdapter.addFragment(DailyClassFragment.newInstance(Day.SUNDAY), "Sun");
        mSectionsPagerAdapter.addFragment(DailyClassFragment.newInstance(Day.MONDAY), "Mon");
        mSectionsPagerAdapter.addFragment(DailyClassFragment.newInstance(Day.TUESDAY), "Tue");
        mSectionsPagerAdapter.addFragment(DailyClassFragment.newInstance(Day.WEDNESDAY), "Wed");
        mSectionsPagerAdapter.addFragment(DailyClassFragment.newInstance(Day.THURSDAY), "Thu");
        mSectionsPagerAdapter.addFragment(DailyClassFragment.newInstance(Day.FRIDAY), "Fri");
        viewPager.setAdapter(mSectionsPagerAdapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this,SettingsActivity.class));
        }

        return super.onOptionsItemSelected(item);
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return mFragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void loadTimeTable(int groupId) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<TimeTable>> call = apiInterface.timeTableList(String.valueOf(groupId));
        call.enqueue(new Callback<List<TimeTable>>() {
            @Override
            public void onResponse(Call<List<TimeTable>> call, Response<List<TimeTable>> response) {
                List<TimeTable> timeTables = response.body();
                Log.d("response is: ", response.toString());
                dissmissMax=(timeTables.size()*4)+1;

                for (TimeTable timeTable : timeTables) {
                    ClassDataLab.get(RoutineActivity.this).addToTimeTable(timeTable);
                    loadTeacherTable(timeTable.getTeacherId());
                    loadCourseTable(timeTable.getCourseId());
                    loadLessionTable(timeTable.getLessionId());
                    loadRoomTable(timeTable.getRoomId());
                }
                dissmissCounter();
                PreferenceUtils.get(RoutineActivity.this).setTimeTableInitialized(true);
            }

            @Override
            public void onFailure(Call<List<TimeTable>> call, Throwable t) {
                Log.d("response is failed", "becuase", t);
            }
        });
        Log.d("response is: ", "Completed");
    }


    public void loadTeacherTable(int groupId) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Teacher>> call = apiInterface.teacherList(String.valueOf(groupId));
        call.enqueue(new Callback<List<Teacher>>() {
            @Override
            public void onResponse(Call<List<Teacher>> call, Response<List<Teacher>> response) {
                List<Teacher> teachers = response.body();
                Log.d("response is: ", response.toString());
                for (Teacher teacher : teachers) {
                    ClassDataLab.get(RoutineActivity.this).addToTeacher(teacher);
                }
                dissmissCounter();
            }

            @Override
            public void onFailure(Call<List<Teacher>> call, Throwable t) {
                Log.d("response is failed", "becuase", t);
            }
        });
        Log.d("response is: ", "Completed");
    }


    public void loadLessionTable(int groupId) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Lession>> call = apiInterface.lessionList(String.valueOf(groupId));
        call.enqueue(new Callback<List<Lession>>() {
            @Override
            public void onResponse(Call<List<Lession>> call, Response<List<Lession>> response) {
                List<Lession> lessions = response.body();
                Log.d("response is: ", response.toString());
                for (Lession lession : lessions) {
                    ClassDataLab.get(RoutineActivity.this).addToLession(lession);
                }
                dissmissCounter();
            }

            @Override
            public void onFailure(Call<List<Lession>> call, Throwable t) {
                Log.d("response is failed", "becuase", t);
            }
        });
        Log.d("response is: ", "Completed");
    }


    public void loadRoomTable(int groupId) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Room>> call = apiInterface.roomList(String.valueOf(groupId));
        call.enqueue(new Callback<List<Room>>() {
            @Override
            public void onResponse(Call<List<Room>> call, Response<List<Room>> response) {
                List<Room> rooms = response.body();
                Log.d("response is: ", response.toString());
                for (Room room : rooms) {
                    ClassDataLab.get(RoutineActivity.this).addToRoom(room);
                }
                dissmissCounter();
            }

            @Override
            public void onFailure(Call<List<Room>> call, Throwable t) {
                Log.d("response is failed", "becuase", t);
            }
        });
        Log.d("response is: ", "Completed");
    }

    public void loadCourseTable(int groupId) {
        apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Course>> call = apiInterface.courseList(String.valueOf(groupId));
        call.enqueue(new Callback<List<Course>>() {
            @Override
            public void onResponse(Call<List<Course>> call, Response<List<Course>> response) {
                List<Course> courses = response.body();
                Log.d("response is: ", response.toString());
                for (Course course : courses) {
                    ClassDataLab.get(RoutineActivity.this).addToCourse(course);
                }
                dissmissCounter();
            }

            @Override
            public void onFailure(Call<List<Course>> call, Throwable t) {
                Log.d("response is failed", "becuase", t);
            }
        });
        Log.d("response is: ", "Completed");
    }

    public void dissmissCounter() {
        dissmissCounter++;
        if(dissmissCounter==dissmissMax) {
            progressDoalog.dismiss();
            init();
        }
    }

    @Override
    public void onBackPressed() {
        if(doubleBackPressStatus) {
            finish();
            super.onBackPressed();
        }
        doubleBackPressStatus=true;
        Toast.makeText(this,"Please click back again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackPressStatus=false;
            }
        },2000);

    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
