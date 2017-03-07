package com.example.shaharbarsheshet.rxcountryprovider.model;

/**
 * Created by Shahar Barsheshet on 12/07/16.
 */
public enum CountryProviderType
{
    DEFAULT(0),
    IP(1),
    NETWORK_PROVIDER(2),
    LOCATION(3);

    private final int value;

    CountryProviderType(int v)
    {
        value = v;
    }

    public int getValue()
    {
        return value;
    }

    public static CountryProviderType fromValue(int typeCode)
    {
        for (CountryProviderType c : CountryProviderType.values())
        {
            if (c.value == typeCode)
            {
                return c;
            }
        }
        throw new IllegalArgumentException("Invalid CountryProviderType type code: " + typeCode);
    }

}
