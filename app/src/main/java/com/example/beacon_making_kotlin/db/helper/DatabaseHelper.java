package com.example.beacon_making_kotlin.db.helper;

import android.content.Context;

public class DatabaseHelper {
    public static void deleteDatabase(Context context, String dbName) {
        context.deleteDatabase(dbName);
    }
}
