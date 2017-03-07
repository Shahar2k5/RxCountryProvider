package com.example.shaharbarsheshet.rxcountryprovider.model;

import android.Manifest;
import android.content.Context;
import android.telephony.TelephonyManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.shaharbarsheshet.rxcountryprovider.location.LocationService;
import com.example.shaharbarsheshet.rxcountryprovider.utils.NetworkUtils;

import org.json.JSONException;

import pub.devrel.easypermissions.EasyPermissions;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Shahar Barsheshet on 10/07/16.
 */
public class CurrentCountryManager
{
    private final RequestQueue mRequestQueue;
    private Context mContext;
    private LocationService mLocationService;

    public CurrentCountryManager(Context context)
    {
        mContext = context;
        mLocationService = new LocationService(context);
        mRequestQueue = Volley.newRequestQueue(context);


    }


    public Observable<CountryProvider> getCountryCode()
    {
        Observable<CountryProvider> locationObservable = getCountryFromLocationProvider();
        Observable<CountryProvider> ipObservable = getCountryFromNetwork();
        Observable<CountryProvider> providerObservable = getCountryFromNetworkProvider();

        return Observable.merge(providerObservable, locationObservable, ipObservable)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .startWith(new CountryProvider("il", CountryProviderType.DEFAULT))
                .scan((countryProvider, countryProvider2) ->
                {
                    if (countryProvider2 == null)
                    {
                        return countryProvider;
                    }
                    int value1 = countryProvider.getProviderType().getValue();
                    int value2 = countryProvider2.getProviderType().getValue();
                    return value1 > value2 ? countryProvider : countryProvider2;
                })
                .distinctUntilChanged()
                .onErrorReturn(null);
    }

    public Observable<CountryProvider> getCountryFromNetworkProvider()
    {
        try
        {
            TelephonyManager tm = (TelephonyManager) mContext.getSystemService(Context.TELEPHONY_SERVICE);
            String countryCode = tm.getNetworkCountryIso();
            return countryCode != null && countryCode.length() > 0 ? Observable.just(new CountryProvider(countryCode.toUpperCase(), CountryProviderType.NETWORK_PROVIDER)) : Observable.empty();
        }
        catch (Exception e)
        {
            return Observable.empty();
        }
    }


    public Observable<CountryProvider> getCountryFromLocationProvider()
    {
        if (!EasyPermissions.hasPermissions(mContext, Manifest.permission.ACCESS_FINE_LOCATION)
                || !LocationService.isLocationServicesAvailable(mContext))
        {
            return Observable.empty();
        }

        return mLocationService.getLocationObservable()
                .first()
                .flatMap(location1 ->
                        mLocationService.getReverseGeocodeObservable(location1)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                )
                .subscribeOn((Schedulers.io()))
                .observeOn(AndroidSchedulers.mainThread())
                .onErrorReturn(throwable -> null)
                .doOnError(Throwable::printStackTrace)
                .map(addresses ->
                {
                    if (addresses != null && addresses.size() > 0)
                    {
                        String countryCode = addresses.get(0).getCountryCode();
                        return countryCode != null && countryCode.length() > 0 ? new CountryProvider(countryCode, CountryProviderType.LOCATION) : null;
                    }
                    return null;
                });
    }


    public Observable<CountryProvider> getCountryFromNetwork()
    {
        Observable<CountryProvider> observable = Observable
                .create(subscriber ->
                {
                    if (NetworkUtils.isNetworkAvailable(mContext))
                    {
                        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, "http://ip-api.com/json", null, response ->
                        {
                            try
                            {
                                if (response.has("countryCode"))
                                {
                                    String cCode = response.getString("countryCode");
                                    if (cCode != null && cCode.length() > 0)
                                    {
                                        subscriber.onNext(new CountryProvider(cCode, CountryProviderType.IP));
                                        subscriber.onCompleted();
                                    }
                                    else
                                    {
                                        subscriber.onError(null);
                                    }
                                }
                                else
                                {
                                    subscriber.onCompleted();
                                }
                            }
                            catch (JSONException e)
                            {
                                subscriber.onError(e);
                            }

                        }, subscriber::onError);
                        request.setShouldCache(false);
                        mRequestQueue.add(request);
                    }
                    else
                    {
                        subscriber.onError(null);
                    }
                });
        return observable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
