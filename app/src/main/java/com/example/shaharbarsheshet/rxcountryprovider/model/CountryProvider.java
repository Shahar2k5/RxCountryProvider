package com.example.shaharbarsheshet.rxcountryprovider.model;

/**
 * Created by Shahar Barsheshet on 12/07/16.
 */
public class CountryProvider
{
    private String mCountryCode;
    private CountryProviderType mProviderType;

    public CountryProvider(String countryCode, CountryProviderType providerType)
    {
        mCountryCode = countryCode;
        mProviderType = providerType;
    }


    public String getCountryCode()
    {
        return mCountryCode;
    }

    public void setCountryCode(String countryCode)
    {
        mCountryCode = countryCode;
    }

    public CountryProviderType getProviderType()
    {
        return mProviderType;
    }

    public void setProviderType(CountryProviderType providerType)
    {
        mProviderType = providerType;
    }

    @Override
    public String toString()
    {
        return "CountryProvider{" +
                "mCountryCode='" + mCountryCode + '\'' +
                ", mProviderType=" + mProviderType +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (o instanceof CountryProvider)
        {
            CountryProvider provider = (CountryProvider) o;
            return provider.mCountryCode.equals(mCountryCode) && provider.mProviderType.getValue() == mProviderType.getValue();
        }
        return false;

    }
}
