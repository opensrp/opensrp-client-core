package org.smartregister.domain.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseEntity extends BaseDataObject {

    private String baseEntityId;

    private Map<String, String> identifiers;

    private List<Address> addresses;

    private Map<String, Object> attributes;

    protected BaseEntity() {
    }

    public BaseEntity(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public BaseEntity(String baseEntityId, Map<String, String> identifiers) {
        this.baseEntityId = baseEntityId;
        this.identifiers = identifiers;
    }

    public BaseEntity(String baseEntityId, Map<String, String> identifiers, Map<String, Object> attributes) {
        this.baseEntityId = baseEntityId;
        this.identifiers = identifiers;
        this.attributes = attributes;
    }

    public BaseEntity(String baseEntityId, Map<String, String> identifiers, Map<String, Object> attributes, List<Address> addresses) {
        this.baseEntityId = baseEntityId;
        this.identifiers = identifiers;
        this.attributes = attributes;
        this.addresses = addresses;
    }

    public String getBaseEntityId() {
        return baseEntityId;
    }

    public void setBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
    }

    public List<Address> getAddresses() {
        if (addresses == null) {
            addresses = new ArrayList<Address>();
        }
        return addresses;
    }

    public Address getAddress(String addressType) {
        for (Address address : addresses) {
            if (address.getAddressType().equalsIgnoreCase(addressType)) {
                return address;
            }
        }
        return null;
    }

    /**
     * WARNING: Overrides all existing addresses
     *
     * @param addresses
     * @return
     */
    public void setAddresses(List<Address> addresses) {
        this.addresses = addresses;
    }

    public void addAddress(Address address) {
        if (addresses == null) {
            addresses = new ArrayList<Address>();
        }
        addresses.add(address);
    }

    public Map<String, Object> getAttributes() {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }
        return attributes;
    }

    public Object getAttribute(String name) {
        if (attributes == null) {
            return null;
        }
        for (String k : attributes.keySet()) {
            if (k.equalsIgnoreCase(name)) {
                return attributes.get(k);
            }
        }
        return null;
    }

    /**
     * WARNING: Overrides all existing attributes
     *
     * @param attributes
     * @return
     */
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public void addAttribute(String name, Object value) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }

        attributes.put(name, value);
    }

    public void removeAttribute(String name) {
        attributes.remove(name);
    }

    public Map<String, String> getIdentifiers() {
        if (identifiers == null) {
            identifiers = new HashMap<String, String>();
        }
        return identifiers;
    }

    public String getIdentifier(String identifierType) {
        if (identifiers == null) {
            return null;
        }
        for (String k : identifiers.keySet()) {
            if (k.equalsIgnoreCase(identifierType)) {
                return identifiers.get(k);
            }
        }
        return null;
    }

    public void setIdentifiers(Map<String, String> identifiers) {
        this.identifiers = identifiers;
    }

    public void addIdentifier(String identifierType, String identifier) {
        if (identifiers == null) {
            identifiers = new HashMap<String, String>();
        }

        identifiers.put(identifierType, identifier);
    }

    public void removeIdentifier(String identifierType) {
        identifiers.remove(identifierType);
    }

    public BaseEntity withBaseEntityId(String baseEntityId) {
        this.baseEntityId = baseEntityId;
        return this;
    }

    /**
     * WARNING: Overrides all existing identifiers
     *
     * @param identifiers
     * @return
     */
    public BaseEntity withIdentifiers(Map<String, String> identifiers) {
        this.identifiers = identifiers;
        return this;
    }

    public BaseEntity withIdentifier(String identifierType, String identifier) {
        if (identifiers == null) {
            identifiers = new HashMap<String, String>();
        }
        identifiers.put(identifierType, identifier);
        return this;
    }

    /**
     * WARNING: Overrides all existing addresses
     *
     * @param addresses
     * @return
     */
    public BaseEntity withAddresses(List<Address> addresses) {
        this.addresses = addresses;
        return this;
    }

    public BaseEntity withAddress(Address address) {
        if (addresses == null) {
            addresses = new ArrayList<Address>();
        }
        addresses.add(address);
        return this;
    }

    /**
     * WARNING: Overrides all existing attributes
     *
     * @param attributes
     * @return
     */
    public BaseEntity withAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
        return this;
    }

    public BaseEntity withAttribute(String name, Object value) {
        if (attributes == null) {
            attributes = new HashMap<String, Object>();
        }
        attributes.put(name, value);
        return this;
    }
}
