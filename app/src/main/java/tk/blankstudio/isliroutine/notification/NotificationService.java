package tk.blankstudio.isliroutine.notification;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.RemoteViews;

import tk.blankstudio.isliroutine.R;
import tk.blankstudio.isliroutine.database.DataLab;
import tk.blankstudio.isliroutine.model.ClassModel;
import tk.blankstudio.isliroutine.utils.AlarmUtils;
import tk.blankstudio.isliroutine.utils.PreferenceUtils;
import tk.blankstudio.isliroutine.widget.RoutineWidgetProvider;

import com.framgia.library.calendardayview.data.IEvent;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by deadsec on 11/13/17.
 */

public class NotificationService extends Service {

    public static final String TAG = NotificationService.class.getSimpleName();
    public static final int REQ_CODE_SET_DAILY_REPEATING = 12345789;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate: called");
        handleTodayNotification();
    }


    /**
     * This method handles the today notification. It gets todays course timetable and
     * sets the schedule for specific time
     */
    public void handleTodayNotification() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEE");
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d).toUpperCase();
        Log.d(TAG, "handleTodayNotification: of date: hr:" + d.getHours() + " min:" + d.getMinutes() + " date:" + d.getDate() + "day: " + d.getDay());

        int groupId = Integer.parseInt(PreferenceUtils.get(this).getDefaultGroupYear());
        List<IEvent> ivents = DataLab.get(this).getEvents(dayOfTheWeek, groupId);
        for (IEvent event : ivents) {
            scheduleNotification(this, (ClassModel) event);
        }

        RoutineWidgetProvider.updateRoutineWidget(this);

    }

    /**
     * This method sets the daily repeating scheduler. It sets the repeated alarm.
     * This method calls the NotificationReceiver.class at 2 o clock in morning . If time is passed,
     * it immediately executes. THis methods job is to make sure that the NotificationReceiver is called at least daily.
     *
     * @param context Context
     * @param choice  whether to set daily repeating notification or not
     */
    public static void setDailyRepeatingNotification(Context context, boolean choice) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.setAction("repeat");
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 2);
        cal.set(Calendar.MINUTE, 0);
        if (choice) {
            AlarmUtils.addRepeatingAlarm(context, intent, REQ_CODE_SET_DAILY_REPEATING, cal);
        } else {
            AlarmUtils.cancelAlarm(context, intent, REQ_CODE_SET_DAILY_REPEATING);
        }
    }

    /**
     * This methods main task is to schedule the notification
     * It make sure that the class model start and endtime alarm is set.
     *
     * @param context    Context
     * @param classModel Classmodel
     */
    public void scheduleNotification(Context context, ClassModel classModel) {

        int startHour = classModel.getStartTime().get(Calendar.HOUR_OF_DAY);
        int startMinute = classModel.getStartTime().get(Calendar.MINUTE);
        int endMinute = classModel.getEndTime().get(Calendar.MINUTE);
        int endHour = classModel.getEndTime().get(Calendar.HOUR_OF_DAY);
        int uId = (int) classModel.getId();

        Intent startClassIntent = new Intent(context, NotificationPublisher.class);
        startClassIntent.putExtra(NotificationPublisher.UID, uId);
        startClassIntent.putExtra(NotificationPublisher.START_HOUR, startHour);
        startClassIntent.putExtra(NotificationPublisher.START_MINUTE, startMinute);
        startClassIntent.putExtra(NotificationPublisher.COURSE_NAME, classModel.getCourseName());
        startClassIntent.putExtra(NotificationPublisher.CLASS_STATUS, NotificationPublisher.CLASS_STARTING);

        Intent endClassIntent = new Intent(context, NotificationPublisher.class);
        endClassIntent.putExtra(NotificationPublisher.UID, uId);
        endClassIntent.putExtra(NotificationPublisher.CLASS_STATUS, NotificationPublisher.CLASS_ENDING);
        endClassIntent.putExtra(NotificationPublisher.END_HOUR, endHour);
        endClassIntent.putExtra(NotificationPublisher.COURSE_NAME, classModel.getCourseName());
        endClassIntent.putExtra(NotificationPublisher.END_MINUTE, endMinute);

        Calendar startCal = Calendar.getInstance();
        startCal.set(Calendar.HOUR_OF_DAY, startHour);
        startCal.set(Calendar.MINUTE, startMinute);
        startCal.set(Calendar.SECOND, 0);

        Calendar endCal = Calendar.getInstance();
        endCal.set(Calendar.HOUR_OF_DAY, endHour);
        endCal.set(Calendar.MINUTE, endMinute);
        endCal.set(Calendar.SECOND, 0);

        if (PreferenceUtils.get(context).getBeforeClassEndsNotification()) {
            // reduce minutes
            int startOffsetMinute = Integer.parseInt(PreferenceUtils.get(context).getBeforeClassEndsNotificationMinutes());
            endCal.add(Calendar.MINUTE, -(startOffsetMinute));
//            if (startOffsetMinute <= endMinute) {
//                endCal.set(Calendar.MINUTE, endMinute - startOffsetMinute);
//            } else {
//                if (startOffsetMinute <= 60) {
//                    endCal.set(Calendar.MINUTE, 60 - startOffsetMinute);
//                    endCal.set(Calendar.HOUR_OF_DAY, endHour - 1);
//                } else {
//                    int hour = startOffsetMinute / 60;
//                    int minute = startOffsetMinute % 60;
//                    endCal.set(Calendar.MINUTE, 60 - minute);
//                    endCal.set(Calendar.HOUR_OF_DAY, endHour - hour);
//                }
//            }
        }
        if (PreferenceUtils.get(context).getBeforeClassStartsNotification()) {
            int startOffsetMinute = Integer.parseInt(PreferenceUtils.get(context).getBeforeClassStartsNotificationMinutes());
            startCal.add(Calendar.MINUTE, -(startOffsetMinute));
        }

        AlarmUtils.addAlarm(context, endClassIntent, (uId + endHour) * endHour, endCal);
        AlarmUtils.addAlarm(context, startClassIntent, (uId + startHour) * startHour, startCal);
        Log.d("NotificationHandler", "Set Alarm at Time at: sh:" + startCal.get(Calendar.HOUR_OF_DAY) + " sm:" + startCal.get(Calendar.MINUTE) + " eh:" + endCal.get(Calendar.HOUR_OF_DAY) + " em:" + endCal.get(Calendar.MINUTE));
    }


}
