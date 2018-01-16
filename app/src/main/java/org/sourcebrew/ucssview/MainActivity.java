package org.sourcebrew.ucssview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import org.sourcebrew.ucssview.mvc.controllers.UCSSController;

public class MainActivity extends AppCompatActivity {

    ViewPager viewPager;
    UCSSController adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getPermission();

    }

    private void getPermission() {
        int result =
                checkSelfPermission(Manifest.permission.INTERNET);

        Log.e("GETTER_PERMISSION", "RESULT = " + result);
        if (result == PackageManager.PERMISSION_GRANTED) {
            initialize();
        } else {
            requestPermissions(
                    new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.INTERNET
                    },
                    101
            );
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // initialize();
                } else {
                    //not granted
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void initialize() {
        if (viewPager == null) {
            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            //setSupportActionBar(toolbar);

            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
            viewPager = (ViewPager) findViewById(R.id.pager);
            adapter = new UCSSController(
                    this,
                    getSupportFragmentManager(),
                    tabLayout,
                    viewPager
            );
        }
    }
}
