package com.example.shaharbarsheshet.rxcountryprovider;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.example.shaharbarsheshet.rxcountryprovider.model.CurrentCountryManager;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rx.android.schedulers.AndroidSchedulers;

public class MainActivity extends BaseActivity
{

    private static final int LOCATION_REQ_CODE = 1000;
    private CurrentCountryManager mCountryManager;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mCountryManager = new CurrentCountryManager(this);

        locationTask();
    }

    private void startCountryLocator()
    {
        addInstanceLifecycleSubscription(mCountryManager.getCountryCode()
                .filter(countryProvider -> countryProvider != null)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(countryProvider ->
                        Toast.makeText(this, "Got country " + countryProvider.getCountryCode()
                                        + " from provider: " + countryProvider.getProviderType().name()
                                , Toast.LENGTH_LONG).show()));
    }

    @AfterPermissionGranted(LOCATION_REQ_CODE)
    public void locationTask()
    {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.ACCESS_FINE_LOCATION))
        {
            // Have permission, do the thing!
            startCountryLocator();
        }
        else
        {
            // Ask for the location permission
            EasyPermissions.requestPermissions(this, getString(R.string.rationale_location),
                    LOCATION_REQ_CODE, Manifest.permission.ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // EasyPermissions handles the request result.
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

}
