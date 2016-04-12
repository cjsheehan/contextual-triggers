package com.keepfit.triggers.activity;


import android.Manifest;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.vision.barcode.Barcode;
import com.keepfit.triggers.R;
import com.keepfit.triggers.listener.PermissionRequestListener;
import com.keepfit.triggers.listener.PermissionResponseListener;
import com.keepfit.triggers.location.LocationService;
import com.keepfit.triggers.utils.enums.TriggerPreference;

import java.util.List;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class SettingsActivity extends AppCompatPreferenceActivity {
    private static final String TAG = "SettingsActivity";

    /**
     * A preference value change listener that updates the preference's summary
     * to reflect its new value.
     */
    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();

            if (preference instanceof ListPreference) {
                // For list preferences, look up the correct display value in
                // the preference's 'entries' list.
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);

                // Set the summary to reflect the new value.
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            }  else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    /**
     * Helper method to determine if the device has an extra-large screen. For
     * example, 10" tablets are extra-large.
     */
    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    /**
     * Binds a preference's summary to its value. More specifically, when the
     * preference's value is changed, its summary (line of text below the
     * preference title) is updated to reflect the value. The summary is also
     * immediately updated upon calling this method. The exact display format is
     * dependent on the type of preference.
     *
     * @see #sBindPreferenceSummaryToValueListener
     */
    private static void bindPreferenceSummaryToValue(Preference preference) {
        if (preference == null) {
            Log.w(TAG, "TriggerPreference not initialized!");
            return;
        }
        // Set the listener to watch for value changes.
        preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);

        // TriggerType the listener immediately with the preference's
        // current value.
        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setupActionBar();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void onBuildHeaders(List<PreferenceActivity.Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    /**
     * This method stops fragment injection in malicious applications.
     * Make sure to deny any unknown fragments here.
     */
    protected boolean isValidFragment(String fragmentName) {
        return PreferenceFragment.class.getName().equals(fragmentName)
                || GeneralPreferenceFragment.class.getName().equals(fragmentName)
                || HistoryPreferenceFragment.class.getName().equals(fragmentName);
    }

    /**
     * This fragment shows general preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class GeneralPreferenceFragment extends PreferenceFragment{
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_general);
            setHasOptionsMenu(true);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.
            bindPreferenceSummaryToValue(findPreference(TriggerPreference.STEP_LENGTH.title));
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * This fragment shows notification preferences only. It is used when the
     * activity is showing a two-pane settings UI.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class HistoryPreferenceFragment extends PreferenceFragment implements PermissionRequestListener {

        private static LocationService locationService;
        PermissionResponseListener permissionResponseListener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.pref_history_settings);
            setHasOptionsMenu(true);

            locationService = new LocationService(getActivity(), this);

            // Bind the summaries of EditText/List/Dialog/Ringtone preferences
            // to their values. When their values change, their summaries are
            // updated to reflect the new value, per the Android Design
            // guidelines.

            final Preference homeLongitude = findPreference(TriggerPreference.HOME_LONGITUDE.title);
            final Preference homeLatitude = findPreference(TriggerPreference.HOME_LATITUDE.title);
            final Preference workLongitude = findPreference(TriggerPreference.WORK_LONGITUDE.title);
            final Preference workLatitude = findPreference(TriggerPreference.WORK_LATITUDE.title);
            final Preference customLongitude = findPreference(TriggerPreference.CUSTOM_LONGITUDE.title);
            final Preference customLatitude = findPreference(TriggerPreference.CUSTOM_LATITUDE.title);
            bindPreferenceSummaryToValue(homeLongitude);
            bindPreferenceSummaryToValue(homeLatitude);
            bindPreferenceSummaryToValue(workLongitude);
            bindPreferenceSummaryToValue(workLatitude);
            bindPreferenceSummaryToValue(customLongitude);
            bindPreferenceSummaryToValue(customLatitude);

            Preference homeCurrentLocationButton = findPreference(TriggerPreference.HOME_CURRENT_LOCATION.title);
            homeCurrentLocationButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setCurrentLocationForPreference(homeLongitude, homeLatitude);
                    return true;
                }
            });
            Preference workCurrentLocationButton = findPreference(TriggerPreference.WORK_CURRENT_LOCATION.title);
            workCurrentLocationButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setCurrentLocationForPreference(workLongitude, workLatitude);
                    return true;
                }
            });
            Preference customCurrentLocationButton = findPreference(TriggerPreference.CUSTOM_CURRENT_LOCATION.title);
            customCurrentLocationButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    setCurrentLocationForPreference(customLongitude, customLatitude);
                    return true;
                }
            });


            final EditTextPreference homeAddress = (EditTextPreference) findPreference(TriggerPreference.HOME_ADDRESS.title);
            homeAddress.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    setCurrentLocationForPreferenceFromAddress(preference, newValue.toString(), homeLongitude, homeLatitude);
                    return true;
                }
            });
            final EditTextPreference workAddress = (EditTextPreference) findPreference(TriggerPreference.WORK_ADDRESS.title);
            workAddress.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    setCurrentLocationForPreferenceFromAddress(preference, newValue.toString(), workLongitude, workLatitude);
                    return true;
                }
            });
            final EditTextPreference customAddress = (EditTextPreference) findPreference(TriggerPreference.CUSTOM_ADDRESS.title);
            customAddress.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    setCurrentLocationForPreferenceFromAddress(preference, newValue.toString(), customLongitude, customLatitude);
                    return true;
                }
            });
        }

        private void setCurrentLocationForPreference(final Preference longitude, final Preference latitude) {
            locationService.requestLocation(new PermissionResponseListener() {
                @Override
                public void permissionGranted(Location location) {
                    updatePreference(longitude, String.valueOf(location.getLongitude()));
                    updatePreference(latitude, String.valueOf(location.getLatitude()));
                }

                @Override
                public void permissionDenied() {

                }
            });
        }

        private void setCurrentLocationForPreferenceFromAddress(Preference addressPreference, String address, final Preference longitude, final Preference latitude) {
            Barcode.GeoPoint point = locationService.getLocationFromAddress(address);
            if (point == null) {
                Toast.makeText(getActivity(), "Could not find location for the address: " + address, Toast.LENGTH_SHORT).show();
                return;
            }
            String lon = String.valueOf(point.lng);
            String lat = String.valueOf(point.lat);
            updatePreference(longitude, lon);
            updatePreference(latitude, lat);
            addressPreference.getEditor().putString(addressPreference.getKey(), address).commit();
            EditTextPreference etp = (EditTextPreference) addressPreference;
            etp.setSummary(address);
        }

        private void updatePreference(Preference preference, String pref) {
            preference.getEditor().putString(preference.getKey(), pref).commit();
            bindPreferenceSummaryToValue(preference);
        }

        @Override
        public void onStart() {
            locationService.connect();
            super.onStart();
        }

        @Override
        public void onStop() {
            locationService.disconnect();
            super.onStop();
        }

        public static final int LOCATION_PERMISSION_CODE = 100;

        @Override
        public void notifyPermissionRequested(PermissionResponseListener permissionResponseListener) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{
                            Manifest.permission
                                    .ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    LOCATION_PERMISSION_CODE);
            this.permissionResponseListener = permissionResponseListener;
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            switch (requestCode) {
                case LOCATION_PERMISSION_CODE:
                    if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        // Permission Granted
                        permissionResponseListener.notifyPermissionGranted();
                    } else {
                        // Permission Denied
                        Toast.makeText(getActivity(), "Location Access Denied", Toast.LENGTH_SHORT)
                                .show();
                        permissionResponseListener.notifyPermissionDenied();
                    }
                    break;
                default:
                    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }

        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            int id = item.getItemId();
            if (id == android.R.id.home) {
                startActivity(new Intent(getActivity(), SettingsActivity.class));
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

}
