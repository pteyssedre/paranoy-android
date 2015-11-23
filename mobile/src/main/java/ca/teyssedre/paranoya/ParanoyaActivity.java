/**
 * The MIT License (MIT)
 * <p/>
 * Copyright (c) 2015 Pierre Teyssedre
 * <p/>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p/>
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * <p/>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package ca.teyssedre.paranoya;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import ca.teyssedre.crypto.Crypto;
import ca.teyssedre.paranoya.fragments.ContactFragment;
import ca.teyssedre.paranoya.fragments.CreateUserFragment;
import ca.teyssedre.paranoya.fragments.IdentityFragment;
import ca.teyssedre.paranoya.fragments.KeysFragment;
import ca.teyssedre.paranoya.messaging.data.User;
import ca.teyssedre.paranoya.store.sources.ParanoyaUserSource;
import ca.teyssedre.paranoya.utils.FragmentHelper;
import ca.teyssedre.paranoya.utils.SocketClient;

public class ParanoyaActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PERMISSION_REQUEST_CODE = 123;
    private static final String TAG = "ParanoyaActivity";
    private FloatingActionButton fab;
    private FragmentHelper fragHelper;
    private SocketClient socketManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragHelper = new FragmentHelper(this);
        socketManager = new SocketClient(this);

        CheckAccess();

        Crypto.getInstance(this);
        ParanoyaUserSource.getInstance(this);
    }


    //<editor-fold desc="Permissions">
    private void CheckAccess() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.WAKE_LOCK) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_NETWORK_STATE) != PackageManager.PERMISSION_GRANTED
                    || ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED
                    ) {
                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WAKE_LOCK)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_NETWORK_STATE)
                        && ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MODIFY_AUDIO_SETTINGS)
                        ) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                    System.out.println("explanation to do");
                } else {

                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this, new String[]{
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.RECORD_AUDIO,
                            Manifest.permission.WAKE_LOCK,
                            Manifest.permission.ACCESS_NETWORK_STATE,
                            Manifest.permission.MODIFY_AUDIO_SETTINGS,
                            Manifest.permission.CAMERA
                    }, PERMISSION_REQUEST_CODE);

                    // REACTS_PERMISSION_REQUEST_CODE is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_CODE:
                if (permissions.length == grantResults.length) {
                    Log.d(TAG, "All permissions granted");
                }
                break;
        }
    }
    //</editor-fold>

    //<editor-fold desc="Resume and Stop">
    @Override
    protected void onResume() {
        super.onResume();
        if (socketManager != null) {
            socketManager.boundToService();
        }
        List<User> usersByType = ParanoyaUserSource.getInstance().getUsersByType(1);
        if (usersByType != null && usersByType.size() == 0) {
            fragHelper.PushParanoyaFragment(CreateUserFragment.TAG, CreateUserFragment.class);
        } else {
            OnIdentityCreated();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (socketManager != null) {
            socketManager.unboundToService();
        }
    }
    //</editor-fold>

    //<editor-fold desc="Menu and Navigation">
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_keys:
                fragHelper.PushParanoyaFragment(KeysFragment.TAG, KeysFragment.class);
                break;
            case R.id.nav_manage:
                fragHelper.PushParanoyaFragment(IdentityFragment.TAG, IdentityFragment.class);
                break;
            case R.id.nav_search:
                break;
            case R.id.nav_contacts:
                fragHelper.PushParanoyaFragment(ContactFragment.TAG, ContactFragment.class);
                break;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    //</editor-fold>

    public void OnIdentityCreated() {
//        fragHelper.PushFragment(CameraFragment.TAG, CameraFragment.class);
        fragHelper.PushParanoyaFragment(ContactFragment.TAG, ContactFragment.class);
        socketManager.Connect();

    }

    public void LinkFabAction(final View.OnClickListener action) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fab.setVisibility(View.VISIBLE);
                fab.setOnClickListener(action);
            }
        });
    }

    public void HideFab() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                fab.setVisibility(View.GONE);
            }
        });
    }

}
