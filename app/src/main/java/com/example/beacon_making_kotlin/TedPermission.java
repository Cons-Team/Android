package com.example.beacon_making_kotlin;

import com.example.tedpermission.PermissionBuilder;

public class TedPermission {
    public static final String TAG = TedPermission.class.getSimpleName();

    public static Builder create() {
        return new Builder();
    }

    public static class Builder extends PermissionBuilder<Builder> {

        public void check() {
            checkPermissions();
        }

    }
}
