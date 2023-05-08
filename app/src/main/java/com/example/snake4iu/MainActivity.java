package com.example.snake4iu;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createNotificationChannelAlarm();

        setAlarm();

    }

    private void createNotificationChannelAlarm() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "awayMessageNotificationChannel";
            String description = "Kanal für die Benachrichtigung";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("awayMessage", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void setAlarm() {

        alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager != null && !alarmManager.canScheduleExactAlarms()) {
                intent.setAction(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM);
                this.startActivity(intent);
            }
        }

        // Hier wird später die richtige Alarmzeit gesetzt (24 Stunden nach der aktuellen Uhrzeit).
        // Zurzeit ist die App so eingestellt, dass der Alarm sofort beim Start der MainActivity
        // losgeht. Auf einiges Smartphones (auf meinem ZTE zum Beispiel) ist es aber so, dass
        // die Benachrichtigung verzögert gesendet wird. Das variiert je nach Hersteller.
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, 10);

        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.M) {
            alarmManager.setAlarmClock(
                    new AlarmManager.AlarmClockInfo(Calendar.getInstance().getTimeInMillis(), pendingIntent),
                    pendingIntent
            );
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    Calendar.getInstance().getTimeInMillis(),
                    pendingIntent
            );
        }

        Toast.makeText(this, "Alarm wurde gesetzt!", Toast.LENGTH_LONG).show();
    }

    private void cancelAlarm() {
        Intent intent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_MUTABLE);

        if(alarmManager == null) {
            alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        }

        alarmManager.cancel(pendingIntent);
    }

    public void startSnake(View view) {
        Intent intent = new Intent(this, SnakeActivity.class);

        startActivity(intent);
    }

    public void startSettings(View view) {
        Intent intent = new Intent(this, SettingsActivity.class);

        startActivity(intent);
    }

    public void startCredits(View view) {
        Intent intent = new Intent(this, CreditsActivity.class);

        startActivity(intent);
    }

    public void startHighscore(View view) {
        Intent intent = new Intent(this, HighscoreActivity.class);

        startActivity(intent);
    }

    public void sendMail(View view) {
        String[] to = {"example@example.com"};
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setData(Uri.parse("mailto:"));
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, "Weiterempfehlung");
        intent.putExtra(Intent.EXTRA_TEXT, "Spiel doch mal Snake 4 (i)U!");

        startActivity(intent);
        finish();
    }
}