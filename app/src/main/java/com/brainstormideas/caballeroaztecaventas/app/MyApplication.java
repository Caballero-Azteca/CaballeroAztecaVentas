package com.brainstormideas.caballeroaztecaventas.app;

import android.content.Context;
import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;
import androidx.room.Room;

import com.brainstormideas.caballeroaztecaventas.data.local.database.RoomLocalDatabase;

public class MyApplication extends MultiDexApplication {
    private RoomLocalDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        database = Room.databaseBuilder(getApplicationContext(),
                RoomLocalDatabase.class, "local_database").build();
    }

    public RoomLocalDatabase getDatabase() {
        return database;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
