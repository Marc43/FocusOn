package com.example.focuson;

import android.app.DownloadManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.ImageFormat;
import android.media.ImageReader;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

import java.util.List;

import static android.content.pm.PermissionInfo.PROTECTION_NORMAL;

public class MainActivity extends FragmentActivity implements MediaPlayerFragment.OnFragmentInteractionListener, AnalyticsFragment.OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

    private ImageReader imgReader;
    private RequestQueue queue;
    private CameraInteractionToServer cameraInteractionToServer;
    public static final String SERVER_URL = "http://130.82.11.40:8000/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawer_layout);

        final NavigationView navigationView = findViewById(R.id.nav_view);

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        menuItem.setCheckable(true);
                        drawerLayout.closeDrawers();

                        String s = (String) menuItem.getTitleCondensed();
                        DisplayFragment(s);

                        return true;
                    }
                }
        );

        navigationView.getMenu().getItem(0).setChecked(true);
        String s = (String) navigationView.getMenu().getItem(0).getTitleCondensed();
        DisplayFragment(s);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA}, PROTECTION_NORMAL);
            System.out.println("Camera permission requested.");
        }

        queue = Volley.newRequestQueue(this);
        imgReader = ImageReader.newInstance(2048, 1563, ImageFormat.JPEG, 20);
        cameraInteractionToServer = new CameraInteractionToServer(SERVER_URL, imgReader, queue, this);
    }


    private void DisplayFragment(String id) {
        fragmentTransaction = fragmentManager.beginTransaction();
        Fragment tmp;
        switch (id) {
            case "GOOGLE_MAPS":
                tmp = new MapsFragment();
                if (!fragmentTransaction.isEmpty()) {
                    fragmentTransaction.replace(R.id.content_frame, tmp);
                    fragmentTransaction.addToBackStack(null).commit();
                }
                else {
                    fragmentTransaction.add(R.id.content_frame, tmp);
                    fragmentTransaction.commit();
                }
                break;
            case "DYNAMIC_PLAYLIST":
                tmp = new MediaPlayerFragment();
                fragmentTransaction.replace(R.id.content_frame, tmp);
                fragmentTransaction.addToBackStack(null).commit();
                break;
            case "ANALYTICS":
                tmp = new AnalyticsFragment();
                fragmentTransaction.replace(R.id.content_frame, tmp);
                fragmentTransaction.addToBackStack(null).commit();
                break;
            default:
                System.err.println("Seems like we did not implement that.");
                break;
        }
    }

    public void onFragmentInteraction(Uri uri) {
        return;
    }

}
