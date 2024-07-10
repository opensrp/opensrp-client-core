package org.smartregister.location;

import java.io.Serializable;

public class SSLocations implements Serializable {
    public BaseLocation division;
    public BaseLocation country;
    public BaseLocation district;
    public BaseLocation city_corporation_upazila;
    public BaseLocation pourasabha;
    public BaseLocation union_ward;
    public BaseLocation village;

}
