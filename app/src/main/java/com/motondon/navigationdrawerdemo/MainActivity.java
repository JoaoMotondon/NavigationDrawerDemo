package com.motondon.navigationdrawerdemo;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * This app is intended to demonstrate how to setup basic functionalities for the navigation drawer component.
 *
 * Further information can be found at:
 *   - https://github.com/codepath/android_guides/wiki/Fragment-Navigation-Drawer
 *   - http://antonioleiva.com/navigation-view/
 *   - https://medium.com/android-news/navigation-drawer-styling-according-material-design-5306190da08f#.k63do1w5z
 *   - http://stackoverflow.com/questions/26754940/appcompatv7-v21-navigation-drawer-not-showing-hamburger-icon
 *   - http://blog.grafixartist.com/easy-navigation-drawer-with-design-support-library/
 *   - http://blog.raffaeu.com/archive/2015/04/11/android-and-the-transparent-status-bar.aspx
 *
 * It also uses an external library called CircleImageView in order to add a round view for the profile (in the navigation
 * drawer header). Details at: https://github.com/hdodenhof/CircleImageView
 *
 */
public class MainActivity extends AppCompatActivity {
    private static final String TAG = MainActivity.class.getSimpleName();

    // Attributes to control some drawer menu items states during orientation change
    private static String STATE_SELECTED_POSITION = "STATE_SELECTED_POSITION";
    private static String WIFI_STATE = "WIFI_STATE";
    private static String BLUETOOTH_STATE = "BLUETOOTH_STATE";
    private static String LOCATION_STATE= "LOCATION_STATE";
    private static String NOTIFICATION_STATE = "NOTIFICATION_STATE";

    private DrawerLayout mDrawerLayout;

    // This is the navigation drawer itself.
    private NavigationView mNavigationView;

    // Used to show a animated toggle button in the toolbar.
    private ActionBarDrawerToggle mDrawerToggle;

    // This is the toolbar which will replace default action bar, so that will be possible for the navigation drawer to
    // slide over it when it is open.
    private Toolbar mToolbar;

    // Used to restore navigation drawer selected menu during orientation change.
    private Integer mCurrentSelectedPosition = -1;

    // Keep a reference to the current fragment. This will be useful on screen rotation.
    private BaseFragment mCurrentFragment = null;

    // Used to control the navigation drawer item icons.
    private boolean wifiOn;
    private boolean bluetoothOn;
    private boolean locationOn;
    private boolean notificationOn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate()");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // In order to slide our navigation drawer over the ActionBar, we need to use the new Toolbar
        // To use the Toolbar as an ActionBar, we need first disable the default ActionBar. This can be done by setting the app
        // theme in styles.xml file. Otherwise we will get a java.lang.IllegalStateException.
        // We defined this toolbar in a separate xml file only for the sake of clarity.
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationView = (NavigationView) findViewById(R.id.nvView);

        // By default there is not a hamburger animation icon in the toolbar. So, if we want it, we must set it up manually.
        // This is what the method below does.
        setupDrawerToggle();

        // Now add a listener to the NavigationView view, so that it can handle user actions.
        setupDrawerContent(mNavigationView);

        // Setup navigation drawer header information such as profile image, name and e-mail
        setupProfileInformation();

        // Only setup drawer menu items when savedInstanceState is null. For orientation change, see onRestoreInstanceState() implementation method.
        // Maybe in a real application, these values should come from persistent place such as a database.
        if (savedInstanceState == null) {

            // Default position is 0 (Home fragment)
            mCurrentSelectedPosition = 0;

            // Also, when this fragment is being opened for the first time, mark the "Home" drawer menu as checked
            // In a real application, this might not be the desired behavior, since we could setup a drawer menu item based on a condition
            // such as a value got from a database, or from the app settings, but this is just to demonstrate how we can force an item
            // to be checked at the startup.
            mNavigationView.setCheckedItem(R.id.nav_drawer_home_item);

            // These four properties are used to control the navigation drawer items icons. Maybe in a real application, these values should be read
            // from the device sensors (or anywhere else)
            wifiOn = true;
            bluetoothOn = true;
            locationOn = true;
            notificationOn = false;
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d(TAG, "onOptionsItemSelected()");

        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        // The action bar home/up action should open or close the drawer.
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called after onStart().
     *
     * Note: Make sure to override the method with only a single `Bundle` argument
     *
     * @param savedInstanceState
     */
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onPostCreate()");

        super.onPostCreate(savedInstanceState);

        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.d(TAG, "onConfigurationChanged()");

        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d(TAG, "onRestoreInstanceState()");

        super.onRestoreInstanceState(savedInstanceState);
        mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION, -1);
        wifiOn = savedInstanceState.getBoolean(WIFI_STATE);
        bluetoothOn = savedInstanceState.getBoolean(BLUETOOTH_STATE);
        locationOn = savedInstanceState.getBoolean(LOCATION_STATE);
        notificationOn= savedInstanceState.getBoolean(NOTIFICATION_STATE);

        setupNavigationDrawer();

        // After restoring our data from the bundle, if a drawer menu item was previously checked, make sure to check it again.
        if (mCurrentSelectedPosition > -1) {

            Log.d(TAG, "onRestoreInstanceState() - Set drawer menu position: " + mCurrentSelectedPosition + " to checked");
            // On orientation change, set the current drawer menu item as checked
            Menu menu = mNavigationView.getMenu();
            menu.getItem(mCurrentSelectedPosition).setChecked(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Log.d(TAG, "onSaveInstanceState()");

        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
        outState.putBoolean(WIFI_STATE, wifiOn);
        outState.putBoolean(BLUETOOTH_STATE, bluetoothOn);
        outState.putBoolean(LOCATION_STATE, locationOn);
        outState.putBoolean(NOTIFICATION_STATE, notificationOn);

        super.onSaveInstanceState(outState);
    }

    /**
     * In order for the hamburger icon to be animated when the drawer is being opened and closed, we need to use
     * the ActionBarDrawerToggle class.
     *
     * So, this is what this method for. See link below for details:
     * http://stackoverflow.com/questions/26754940/appcompatv7-v21-navigation-drawer-not-showing-hamburger-icon
     *
     * Also, when using the ActionBarDrawerToggle, you must call it during onPostCreate() and onConfigurationChanged(). See them  for details
     *
     * Although we will not do any action after a drawer is opened or closed, this code demonstrates how to handle these events. Maybe it can
     * be useful some day.
     *
     */
    private void setupDrawerToggle() {
        Log.d(TAG, "setupDrawerToggle()");

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,  R.string.drawer_close) {
            public void onDrawerClosed(View view) {
                Log.d(TAG, "setupDrawerToggle::onDrawerClosed()");
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                Log.d(TAG, "setupDrawerToggle::onDrawerOpened()");
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);

        // Add drawer toggle to the drawer layout listener.
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    /**
     * Set all the required listeners for the navigation drawer menu items. Note we have a custom item (SwitchCompact view), so
     * we have to add a separate listener for its clicks.
     *
     * @param navigationView
     */
    private void setupDrawerContent(NavigationView navigationView) {
        Log.d(TAG, "setupDrawerContent()");

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });

        // Since we have a custom menu item in our navigation drawer (i.e.: a SwitchCompact view), we must add a listener
        // for the click event manually. This is what we are doing on the next lines.
        Menu menu = mNavigationView.getMenu();
        final MenuItem navigationItemNotificationSwitch = menu.findItem(R.id.nav_drawer_notification_item);
        SwitchCompat actionView = (SwitchCompat) MenuItemCompat.getActionView(navigationItemNotificationSwitch);
        actionView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Change icon according to the current state
                notificationOn = !notificationOn;
                navigationItemNotificationSwitch.setIcon(notificationOn ? R.drawable.ic_notifications_black_24dp : R.drawable.ic_notifications_off_black_24dp);

                // Since on this example we will show a snackbar, we need to close the drawer first. Maybe in a real
                // application we do not want to close the drawer when changing a switchCompact state.
                mDrawerLayout.closeDrawers();
                Snackbar.make(mDrawerLayout, "You clicked on switch menu item", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * This method is intended to demonstrate how to access navigation drawer header information and manipulate them.
     *
     * For this example, we add an image to the profile imageView and a fake email account. Also we add a listener to the
     * email view so that we can call an external e-mail app to send an e-mail to that person.
     *
     */
    private void setupProfileInformation() {
        Log.d(TAG, "setupProfileInformation()");

        // Lines below demonstrate on how to get a reference for navigation drawer header text views and change them
        View hView =  mNavigationView.getHeaderView(0);
        CircleImageView nav_image = (CircleImageView) hView.findViewById(R.id.profile_image);
        nav_image.setImageResource(R.drawable.john_lennon_photo);
        TextView nav_user = (TextView)hView.findViewById(R.id.name);
        nav_user.setText("John Lennon");
        final TextView nav_email = (TextView)hView.findViewById(R.id.email);
        nav_email.setText("john.lennon@gmail.com");

        nav_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "nav_email::onClick()");

                mDrawerLayout.closeDrawers();
                // TODO: Here we could call an Intent.ACTION_SEND to call an external app to send an email.
                Snackbar.make(mDrawerLayout, "E-mail to " + nav_email.getText().toString() + " sent successfully", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void selectDrawerItem(MenuItem item) {
        Log.d(TAG, "selectDrawerItem()");

        switch (item.getItemId()) {
            case R.id.nav_drawer_home_item:
                Log.d(TAG, "selectDrawerItem() - Item: nav_drawer_home_item");
                mCurrentSelectedPosition = 0;
                item.setChecked(true);
                break;

            case R.id.nav_drawer_my_location_item:
                Log.d(TAG, "selectDrawerItem() - Item: nav_drawer_my_location_item");
                mCurrentSelectedPosition = 1;
                item.setChecked(true);
                break;

            case R.id.nav_drawer_sync_data_item:
                Log.d(TAG, "selectDrawerItem() - Item: nav_drawer_sync_data_item");
                mCurrentSelectedPosition = 2;
                item.setChecked(true);
                break;

            case R.id.nav_drawer_cloud_upload_item:
                Log.d(TAG, "selectDrawerItem() - Item: nav_drawer_cloud_upload_item");
                mCurrentSelectedPosition = 3;
                item.setChecked(true);
                break;

            case R.id.nav_drawer_wifi_item:
                Log.d(TAG, "selectDrawerItem() - Item: nav_drawer_wifi_item");

                // For this item, change icon according to the current state (this flag is used later in the setupNavigationDrawer method)
                wifiOn = !wifiOn;
                break;

            case R.id.nav_drawer_bluetooth_item:
                Log.d(TAG, "selectDrawerItem() - Item: nav_drawer_bluetooth_item");

                // For this item, change icon according to the current state (this flag is used later in the setupNavigationDrawer method)
                bluetoothOn = !bluetoothOn;
                break;

            case R.id.nav_drawer_location_item:
                Log.d(TAG, "selectDrawerItem() - Item: nav_drawer_location_item");

                // For this item, change icon according to the current state (this flag is used later in the setupNavigationDrawer method)
                locationOn = !locationOn;
                break;

            // This is a special case, which we do not replace the current fragment, but open a new activity and do not change current fragment
            // reference. This is useful when we want to open a new activity but keep the current fragment reference (in our case one of the
            // wifi, bluetooth or location items). As soon as this activity is closed, current fragment will be shown again.
            case R.id.nav_help_activity:
                Log.d(TAG, "selectDrawerItem() - Item: nav_help_activity");

                // Close drawer. Otherwise, when HelpActivity is finished, it will be opened. Depends on the case, this might be the desired
                // behavior. If so, just comment out line below.
                mDrawerLayout.closeDrawers();

                // Open the HelpActivity. It will be placed over the MainActivity.
                Intent i = new Intent(getApplicationContext(), HelpActivity.class);
                startActivity(i);

                // Notice we do not call "break", but "return", since there is nothing else to do for this case!
                return;

             default:
                 // Maybe we should do something here.
                 Log.d(TAG, "selectDrawerItem() - Invalid selected item: " + item.getItemId());
        }

        setupNavigationDrawer();

        // Close the navigation drawer if it is different from SwitchCompact view, since for this view there is
        // an special listener which will take care of close the drawer
        if (item.getItemId() != R.id.nav_drawer_notification_item) {
            mDrawerLayout.closeDrawers();
        }
    }

    private void setupNavigationDrawer() {
        Log.d(TAG, "setupNavigationDrawer()");

        // First we need to setup current fragment based on the selected position.
        switch (mCurrentSelectedPosition) {
            case 0:
                mCurrentFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(HomeFragment.TAG);
                if (mCurrentFragment == null) {
                    Log.i(TAG, "setupNavigationDrawer() - Restoring HomeFragment...");
                    mCurrentFragment = new HomeFragment();
                }
                break;

            case 1:
                mCurrentFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(MyLocationFragment.TAG);
                if (mCurrentFragment == null) {
                    Log.i(TAG, "setupNavigationDrawer() - Restoring MyLocationFragment...");
                    mCurrentFragment = new MyLocationFragment();
                }
                break;

            case 2:
                mCurrentFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(SyncDataFragment.TAG);
                if (mCurrentFragment == null) {
                    Log.i(TAG, "setupNavigationDrawer() - Restoring SyncDataFragment...");
                    mCurrentFragment = new SyncDataFragment();
                }
                break;

            case 3:
                mCurrentFragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(CloudUploadFragment.TAG);
                if (mCurrentFragment == null) {
                    Log.i(TAG, "setupNavigationDrawer() - Restoring CloudUploadFragment...");
                    mCurrentFragment = new CloudUploadFragment();
                }
                break;
        }

        // Now, replace fragmentManager with the current fragment.
        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, mCurrentFragment).commit();

        // And set the app title
        setTitle(mCurrentFragment.getTitle());

        // Now it is time to update item icons based on the state of wifi, bluetooth, location and notification flags.
        mNavigationView.getMenu().findItem(R.id.nav_drawer_wifi_item).setIcon(wifiOn ? R.drawable.ic_network_wifi_black_24dp : R.drawable.ic_signal_wifi_off_black_24dp);
        mNavigationView.getMenu().findItem(R.id.nav_drawer_wifi_item).setTitle(wifiOn ? "Wifi On" : "Wifi Off");

        mNavigationView.getMenu().findItem(R.id.nav_drawer_bluetooth_item).setIcon(bluetoothOn ? R.drawable.ic_bluetooth_black_24dp : R.drawable.ic_bluetooth_disabled_black_24dp);
        mNavigationView.getMenu().findItem(R.id.nav_drawer_bluetooth_item).setTitle(bluetoothOn ? "Bluetooth On" : "Bluetooth Off");

        mNavigationView.getMenu().findItem(R.id.nav_drawer_location_item).setIcon(locationOn ? R.drawable.ic_location_on_black_24dp : R.drawable.ic_location_off_black_24dp);
        mNavigationView.getMenu().findItem(R.id.nav_drawer_location_item).setTitle(locationOn ? "Location On" : "Location Off");

        mNavigationView.getMenu().findItem(R.id.nav_drawer_notification_item).setIcon(notificationOn ? R.drawable.ic_notifications_black_24dp : R.drawable.ic_notifications_off_black_24dp);
    }
}
