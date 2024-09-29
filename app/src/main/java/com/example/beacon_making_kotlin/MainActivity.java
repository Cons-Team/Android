package com.example.beacon_making_kotlin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.bluetooth.BluetoothAdapter;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.davemorrissey.labs.subscaleview.ImageSource;
import com.davemorrissey.labs.subscaleview.SubsamplingScaleImageView;
import com.example.beacon_making_kotlin.Menu.MainMenuAdapter;
import com.example.beacon_making_kotlin.Menu.MainMenuGroup;
import com.example.beacon_making_kotlin.Menu.SettingMenuAdapter;
import com.example.beacon_making_kotlin.Menu.SettingMenuGroup;
import com.example.beacon_making_kotlin.MetroMap.Metro_map_fragment;
import com.example.beacon_making_kotlin.beaconfind.BeaconBackgroundService;
import com.example.beacon_making_kotlin.beaconfind.LoadingDialog;
import com.example.beacon_making_kotlin.db.database.ConsDatabase;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    Metro_map_fragment metro_map_fragment;

    //Layout
    DrawerLayout drawerLayout;
    ImageButton menu_button;

    //Menu
    NavigationView navigationView;
    ExpandableListView settingMenuList;
    ListView mainMenuList;

    //Menu Link
    ArrayList<MainMenuGroup> mainMenus;
    ArrayList<SettingMenuGroup> settingMenus;

    //Toolbar
    Toolbar toolbar;
    TextView page_title;

    SharedPreferences preferces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        preferces = getSharedPreferences("Setting", 0);
        metro_map_fragment = new Metro_map_fragment();

        //FragmentManager
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.fragment_container_view, metro_map_fragment).commitAllowingStateLoss();

        drawerLayout = (DrawerLayout) findViewById(R.id.main);
        menu_button = (ImageButton) findViewById(R.id.menu_button);

        menu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getId() == R.id.menu_button){
                    if(drawerLayout.isDrawerOpen(R.id.main)){
                        drawerLayout.closeDrawer(GravityCompat.START);
                    }
                    else{
                        drawerLayout.openDrawer(GravityCompat.START);
                    }
                }
            }
        });

        //Menu
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        setMainMenu();
        setSettingMenu();

        //Toolbar
        toolbar = findViewById(R.id.toolbar);
    }

    public void setMainMenu(){
        mainMenuList = (ListView) findViewById(R.id.main_menu);


        mainMenus = new ArrayList<>();
        mainMenus.add(new MainMenuGroup(getResources().getString(R.string.lostAndFind), this.getResources().getDrawable(R.drawable.icon_lost_property, null)));

        MainMenuAdapter menuLinkAdapter = new MainMenuAdapter(this, mainMenus);
        mainMenuList.setAdapter(menuLinkAdapter);

        mainMenuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                drawerLayout.closeDrawer(GravityCompat.START);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.lost112.go.kr/lost/lostList.do"));
                startActivity(intent);
            }
        });

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void setSettingMenu(){
        settingMenuList = (ExpandableListView) findViewById(R.id.setting_menu);

        //Font
        settingMenus = new ArrayList<>();
        SettingMenuGroup item = new SettingMenuGroup(getResources().getString(R.string.fontSize), this.getResources().getDrawable(R.drawable.icon_font, null));
        item.child.add(getResources().getString(R.string.fontSizeSmall));
        item.child.add(getResources().getString(R.string.fontSizeNormal));
        item.child.add(getResources().getString(R.string.fontSizeBig));
        settingMenus.add(item);

        //hand mode
        if(preferces.getString("mode", "오른손 모드").equals("오른손 모드")){
            item = new SettingMenuGroup(getResources().getString(R.string.hand_mode_left), this.getResources().getDrawable(R.drawable.icon_left_hand, null));
        }
        else {
            item = new SettingMenuGroup(getResources().getString(R.string.hand_mode_right), this.getResources().getDrawable(R.drawable.icon_right_hand, null));
        }
        settingMenus.add(item);

        //Theme
        item = new SettingMenuGroup(getResources().getString(R.string.menu_theme), this.getResources().getDrawable(R.drawable.icon_theme, null));
        settingMenus.add(item);

        SettingMenuAdapter adapter = new SettingMenuAdapter(getApplicationContext(), R.layout.setting_menu_group, R.layout.setting_menu_child, settingMenus);
        settingMenuList.setGroupIndicator(null);
        settingMenuList.setAdapter(adapter);

        settingMenuList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                String groupName = settingMenus.get(groupPosition).groupName;

                SharedPreferences.Editor editor = preferces.edit();

                if(groupName.equals(getResources().getString(R.string.menu_theme))) {
                    if (getResources().getString(R.string.menu_theme).equals("야간 모드")){
                        editor.putString("theme", "Night");
                    }
                    else{
                        editor.putString("theme", "Day");
                    }
                    editor.commit();
                    reStartApp();
                    return true;
                }

                if(groupName.equals(getResources().getString(R.string.hand_mode_right)) || groupName.equals(getResources().getString(R.string.hand_mode_left))){
                    editor.putString("mode", groupName);
                    metro_map_fragment.navBtnChange(groupName);
                    editor.commit();
                    setSettingMenu();
                    return true;
                }

                return false;
            }
        });

        settingMenuList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                String child = settingMenus.get(groupPosition).child.get(childPosition);
                SharedPreferences.Editor editor = preferces.edit();

                if(settingMenus.get(groupPosition).groupName.equals(getResources().getString(R.string.fontSize))){
                    editor.putString("fontSize", child);
                    reStartApp();
                }
                editor.commit();

                return true;
            }
        });
    }

    public void reStartApp(){
        PackageManager packageManager = this.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = Intent.makeRestartActivityTask(componentName);
        this.startActivity(mainIntent);
        System.exit(0);
    }

//    /** bluetooth state check */
//    public static boolean bluetoothStateCheck(){
//        BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
//
//        if (btadapter.isEnabled()) {
//            Log.d("ble_stat", "on_device");
//            //Toast.makeText(MainActivity.this, "on_device", Toast.LENGTH_SHORT).show();
//
//            return true;
//        } else {
//            Log.d("ble_stat", "꺼져있거나 블루투스 기능이 없습니다.");
//            //Toast.makeText(MainActivity.this, "꺼져있거나 블루투스 기능이 없습니다.", Toast.LENGTH_SHORT).show();
//
//            if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED) {
//                startActivityForResult(new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE), 1);
//            }
//
//            return false;
//        }
//    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        return false;
    }
}