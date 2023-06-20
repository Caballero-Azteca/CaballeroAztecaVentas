package com.brainstormideas.caballeroaztecaventas.ui;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.brainstormideas.caballeroaztecaventas.R;
import com.brainstormideas.caballeroaztecaventas.services.MailboxService;
import com.brainstormideas.caballeroaztecaventas.ui.fragments.MainScreen;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    static boolean isInitialized = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializedFirebaseService();

        Intent intent = new Intent(this, MailboxService.class);
        if (!isServiceRunning(MailboxService.class)) {
            this.startService(intent);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, new MainScreen())
                    .commit();
        }
    }

    private void initializedFirebaseService() {
        FirebaseApp.initializeApp(this);
        try {
            if (!isInitialized) {
                FirebaseDatabase.getInstance().setPersistenceEnabled(true);
                FirebaseDatabase.getInstance().getReference("Producto")
                        .keepSynced(true);
                isInitialized = true;
            } else {
                Log.d("ATENCION-FIREBASE:", "Already Initialized");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private boolean isServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        if (manager != null) {
            List<ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(Integer.MAX_VALUE);
            for (ActivityManager.RunningServiceInfo serviceInfo : runningServices) {
                if (serviceClass.getName().equals(serviceInfo.service.getClassName())) {
                    return true;
                }
            }
        }
        return false;
    }
}
