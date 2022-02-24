package org.smartregister.location;

import org.smartregister.domain.db.Address;

import java.util.HashMap;

public class SSLocationHelper {


    public Address getSSAddress(SSLocations ssLocations){
        Address address = new Address();
        address.setAddressType("usual_residence");
        HashMap<String,String> addressMap = new HashMap<>();
        addressMap.put("address1", ssLocations.union_ward.name);
        addressMap.put("address2", ssLocations.city_corporation_upazila.name);
        addressMap.put("address3", ssLocations.pourasabha.name);
        addressMap.put("address8", ssLocations.village.id+"");
        address.setAddressFields(addressMap);
        address.setStateProvince(ssLocations.division.name);
        address.setCityVillage(ssLocations.village.name);
        address.setCountyDistrict(ssLocations.district.name);
        address.setCountry(ssLocations.country.name);
        return address;
    }

}
