package com.example.shaharbarsheshet.rxcountryprovider;

import android.support.v7.app.AppCompatActivity;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

public class BaseActivity extends AppCompatActivity implements HasSubscriptions
{

    private CompositeSubscription mCompositeSubscription = new CompositeSubscription();


    @Override
    protected void onResume()
    {
        super.onResume();
    }

    @Override
    protected void onPause()
    {
        super.onPause();

    }

    @Override
    public Subscription addInstanceLifecycleSubscription(Subscription subscription)
    {
        mCompositeSubscription.add(subscription);
        return subscription;
    }
}
