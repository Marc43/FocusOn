package com.example.focuson;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;

import java.util.List;


public class MainActivity extends FragmentActivity implements MediaPlayerFragment.OnFragmentInteractionListener {

    private DrawerLayout drawerLayout;
    private FragmentManager fragmentManager = getSupportFragmentManager();
    private FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

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
            default:
                System.err.println("Seems like we did not implement that.");
                break;
        }
    }

    public void onFragmentInteraction(Uri uri) {
        return;
    }



}
