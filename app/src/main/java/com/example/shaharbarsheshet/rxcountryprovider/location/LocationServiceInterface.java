package com.example.shaharbarsheshet.rxcountryprovider.location;

import android.location.Address;
import android.location.Location;

import java.util.List;

import rx.Observable;

public interface LocationServiceInterface
{
    Observable<Location> getLastKnownLocation();

    Observable<Location> getLocationObservable();

    Observable<List<Address>> getReverseGeocodeObservable(Location location);
}
