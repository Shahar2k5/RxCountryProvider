package com.example.shaharbarsheshet.rxcountryprovider.location;

import android.content.Context;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;

import com.google.android.gms.location.LocationRequest;

import java.util.List;
import java.util.concurrent.TimeUnit;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Shahar Barsheshet on 19/08/2015.
 * .
 */
public class LocationService implements LocationServiceInterface
{
    public static final int LOCATION_UPDATES_INTERVAL_IN_MS = 2000;
    public static final float LOCATION_UPDATES_SMALLEST_DISPLACEMENT_IN_METERS = 5.0f;
    public static final Scheduler WORK_SCHEDULER = Schedulers.computation();
    public static final Scheduler RESULT_SCHEDULER = AndroidSchedulers.mainThread();
    private final Object mLock = new Object();
    private ReactiveLocationProvider mLocationProvider;
    private Observable<Location> mLocationObs;
    private LocationRequest mLocationRequest;

    public LocationService(Context context)
    {
        mLocationProvider = new ReactiveLocationProvider(context);
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setFastestInterval(LOCATION_UPDATES_INTERVAL_IN_MS)
                .setInterval(LOCATION_UPDATES_INTERVAL_IN_MS)
                .setSmallestDisplacement(LOCATION_UPDATES_SMALLEST_DISPLACEMENT_IN_METERS);
    }

    /**
     * Use getLocationSettings() instead to determine if location services available.
     */
    public static boolean isLocationServicesAvailable(Context context)
    {
        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gpsEnabled = false;
        try
        {
            gpsEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch (Exception ignored)
        {
        }
        return gpsEnabled;
    }

    @Override
    public Observable<Location> getLastKnownLocation()
    {
        return mLocationProvider.getLastKnownLocation()
                .subscribeOn(WORK_SCHEDULER)
                .observeOn(RESULT_SCHEDULER);
    }

    public Observable<Location> getLocationObservable()
    {
        synchronized (mLock)
        {
            createObservablesIfNeeded();
            return mLocationObs;
        }
    }

    @Override
    public Observable<List<Address>> getReverseGeocodeObservable(Location location)
    {
        return mLocationProvider.getReverseGeocodeObservable(location.getLatitude(), location.getLongitude(), 1).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private void createObservablesIfNeeded()
    {
        synchronized (mLock)
        {
            if (mLocationObs != null)
            {
                return;
            }
            Observable<Location> locationObservable = mLocationProvider.getUpdatedLocation(mLocationRequest);

            // 2 seconds or 5 meters location updates
            mLocationObs = locationObservable
                    .debounce(LOCATION_UPDATES_INTERVAL_IN_MS, TimeUnit.MILLISECONDS)
                    .startWith(getLastKnownLocation())
                    .subscribeOn(WORK_SCHEDULER)
                    .observeOn(RESULT_SCHEDULER)
                    .replay(1)
                    .refCount();
        }

    }

}
