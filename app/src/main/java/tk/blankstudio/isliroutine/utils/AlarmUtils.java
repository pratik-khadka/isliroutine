package tk.blankstudio.isliroutine.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import tk.blankstudio.isliroutine.notification.NotificationPublisher;
import tk.blankstudio.isliroutine.notification.NotificationReceiver;
import tk.blankstudio.isliroutine.notification.NotificationService;

/**
 * Created by deadsec on 11/24/17.
 * Class comprises alarm manager helper methods.
 */

public class AlarmUtils {

    public static final String TAG = AlarmUtils.class.getSimpleName();

    public static void addAlarm(Context context, Intent intent, int requestCode, Calendar calendar) {

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_NO_CREATE);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        // this is done because FLAG_NO_CREATE returns null if there is no pending intent of request code
        // so if pending intent doesnot exist, then we need to create the new pending intent and
        // also register the alarm
        // if exist then don't do any thing.. :<

        if (pendingIntent == null) {
            pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            Log.d(TAG, "addAlarm: on alarm previously does not exist so creating new one");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            } else {
                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
            Log.d(TAG, "Notification addAlarm: to alarm manager: " + requestCode);
            saveAlarmId(context, requestCode);
        } else {
            Log.d(TAG, "addAlarm: alarm previously exist already");
        }

    }

    public static void addRepeatingAlarm(Context context, Intent intent, int requestCode, Calendar calendar) {

        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
        saveAlarmId(context, requestCode);
    }

    public static void cancelAlarm(Context context, Intent intent, int notificationId) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pendingIntent);
        pendingIntent.cancel();

        removeAlarmId(context, notificationId);
    }

    public static PendingIntent findAlarm(Context context, Intent intent, int notificationId) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_NO_CREATE);
        if (pendingIntent != null) {
            Log.d(TAG, "findAlarm: not equals to null");
        } else {
            Log.d(TAG, "findAlarm: equals to null");
        }
        return pendingIntent;
    }

    public static void cancelAllAlarms(Context context) {
        // for the normal notification setter intent
        Intent intent1 = new Intent(context, NotificationPublisher.class);
        //for the daily repeating setting intent
        Intent intent2 = new Intent(context, NotificationReceiver.class);
        for (int idAlarm : getAlarmIds(context)) {
            // also remove the repeating daily scheduling alarm
            if (idAlarm == NotificationService.REQ_CODE_SET_DAILY_REPEATING) {
                cancelAlarm(context, intent2, idAlarm);
            } else {
                cancelAlarm(context, intent1, idAlarm);
            }
        }
    }

    public static boolean hasAlarm(Context context, Intent intent, int notificationId) {
        return PendingIntent.getBroadcast(context, notificationId, intent, PendingIntent.FLAG_NO_CREATE) != null;
    }

    private static void saveAlarmId(Context context, int id) {
        List<Integer> alarmIds = getAlarmIds(context);

        if (alarmIds.contains(id)) {
            return;
        }

        alarmIds.add(id);

        saveIdsInPreferences(context, alarmIds);
    }

    private static void removeAlarmId(Context context, int id) {
        List<Integer> idsAlarms = getAlarmIds(context);

        for (int i = 0; i < idsAlarms.size(); i++) {
            if (idsAlarms.get(i) == id)
                idsAlarms.remove(i);
        }

        saveIdsInPreferences(context, idsAlarms);
    }

    private static List<Integer> getAlarmIds(Context context) {
        List<Integer> ids = new ArrayList<>();
        try {
            JSONArray jsonArray2 = new JSONArray(PreferenceUtils.get(context).getAlarmIds());

            for (int i = 0; i < jsonArray2.length(); i++) {
                ids.add(jsonArray2.getInt(i));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ids;
    }

    private static void saveIdsInPreferences(Context context, List<Integer> listIds) {
        JSONArray jsonArray = new JSONArray();
        for (Integer idAlarm : listIds) {
            jsonArray.put(idAlarm);
        }
        PreferenceUtils.get(context).setAlarmIds(jsonArray.toString());
    }
}
