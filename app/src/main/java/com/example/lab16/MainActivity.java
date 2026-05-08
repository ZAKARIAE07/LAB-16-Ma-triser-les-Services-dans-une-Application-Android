package com.example.lab16;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvTemps;
    private Button btnStart, btnStop, btnReset;
    private ChronometreService chronometreService;
    private boolean isBound = false;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable updateRunnable;

    // Connexion au service (Bound Service)
    private final ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            ChronometreService.LocalBinder binder = (ChronometreService.LocalBinder) service;
            chronometreService = binder.getService();
            isBound = true;
            demarrerMajUi();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            isBound = false;
            arreterMajUi();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvTemps = findViewById(R.id.tvTemps);
        btnStart = findViewById(R.id.btnStart);
        btnStop = findViewById(R.id.btnStop);
        btnReset = findViewById(R.id.btnReset);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifierPermissionsEtDemarrer();
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopService();
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetService();
            }
        });

        // Runnable pour mettre à jour l'UI chaque seconde
        updateRunnable = new Runnable() {
            @Override
            public void run() {
                if (isBound && chronometreService != null) {
                    tvTemps.setText(formatTemps(chronometreService.getSecondes()));
                    handler.postDelayed(this, 1000);
                }
            }
        };
    }

    private void demarrerMajUi() {
        handler.post(updateRunnable);
    }

    private void arreterMajUi() {
        handler.removeCallbacks(updateRunnable);
    }

    private String formatTemps(int sec) {
        int minutes = sec / 60;
        int secondesRest = sec % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, secondesRest);
    }

    private void verifierPermissionsEtDemarrer() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 101);
            } else {
                startService();
            }
        } else {
            startService();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startService();
        }
    }

    private void startService() {
        Intent intent = new Intent(this, ChronometreService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
        // On lie le service pour pouvoir récupérer le temps via getSecondes()
        bindService(intent, connection, Context.BIND_AUTO_CREATE);
    }

    private void stopService() {
        arreterMajUi();
        Intent intent = new Intent(this, ChronometreService.class);
        intent.setAction("STOP");
        // Envoyer l'action STOP au service
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }

        if (isBound) {
            unbindService(connection);
            isBound = false;
        }
        tvTemps.setText("00:00");
    }

    private void resetService() {
        Intent intent = new Intent(this, ChronometreService.class);
        intent.setAction("RESET");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent);
        } else {
            startService(intent);
        }
    }

    @Override
    protected void onDestroy() {
        arreterMajUi();
        if (isBound) {
            unbindService(connection);
        }
        super.onDestroy();
    }
}
