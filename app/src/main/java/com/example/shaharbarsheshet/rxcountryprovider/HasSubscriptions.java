package com.example.shaharbarsheshet.rxcountryprovider;

import rx.Subscription;

/**
 * Created by Shahar Barsheshet on 27/08/2015.
 */
public interface HasSubscriptions
{
    Subscription addInstanceLifecycleSubscription(Subscription subscription);
}
