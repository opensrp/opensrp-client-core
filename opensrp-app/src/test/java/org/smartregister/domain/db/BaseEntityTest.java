package org.smartregister.domain.db;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.smartregister.domain.db.mock.BaseEntityMock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by kaderchowdhury on 21/11/17.
 */

public class BaseEntityTest {

    private String baseEntityId;

    private Map<String, String> identifiers = new HashMap<>();

    private List<Address> addresses = new ArrayList<>();

    private Map<String, Object> attributes = new HashMap<String,Object>();
    BaseEntityMock baseEntity;

    @Before
    public void setUp() {
        baseEntity = new BaseEntityMock(baseEntityId,identifiers);
    }

    @Test
    public void assertConstructors(){
        Assert.assertNotNull(new BaseEntityMock(baseEntityId,identifiers));
        Assert.assertNotNull(new BaseEntityMock(baseEntityId,identifiers,attributes));
        Assert.assertNotNull(new BaseEntityMock(baseEntityId,identifiers,attributes,addresses));
    }
    
    public String getBaseEntityId() {
        return baseEntity.getBaseEntityId();
    }

    @Test
    public void setBaseEntityId() {
        baseEntity.setBaseEntityId(baseEntityId);
        Assert.assertEquals(getBaseEntityId(),baseEntityId);
    }

    
    public List<Address> getAddresses() {
        return baseEntity.getAddresses();
    }


    public Address getAddress(String addressType) {
        return baseEntity.getAddress(addressType);
    }

    @Test
    public void setAddresses() {
        baseEntity.setAddresses(addresses);
        Assert.assertEquals(getAddresses(),addresses);
    }

    
    public void addAddress(Address address) {
        baseEntity.addAddress(address);
    }

    
    public Map<String, Object> getAttributes() {
        return baseEntity.getAttributes();
    }

    
    public Object getAttribute(String name) {
        return baseEntity.getAttribute(name);
    }

    @Test
    public void setAttributes() {
        baseEntity.setAttributes(attributes);
        Assert.assertEquals(getAttributes(),attributes);
    }

    
    public void addAttribute(String name, Object value) {
        baseEntity.addAttribute(name, value);
    }

    
    public void removeAttribute(String name) {
        baseEntity.removeAttribute(name);
    }

    
    public Map<String, String> getIdentifiers() {
        return baseEntity.getIdentifiers();
    }

    
    public String getIdentifier(String identifierType) {
        return baseEntity.getIdentifier(identifierType);
    }

    @Test
    public void setIdentifiers() {
        baseEntity.setIdentifiers(identifiers);
        Assert.assertEquals(getIdentifiers(),identifiers);
    }

    
    public void addIdentifier(String identifierType, String identifier) {
        baseEntity.addIdentifier(identifierType, identifier);
    }

    
    public void removeIdentifier(String identifierType) {
        baseEntity.removeIdentifier(identifierType);
    }

    @Test
    public void withBaseEntityId() {
        Assert.assertNotNull(baseEntity.withBaseEntityId(baseEntityId));
    }

    @Test
    public void withIdentifiers() {
        Assert.assertNotNull(baseEntity.withIdentifiers(identifiers));
    }

    @Test
    public void withIdentifier() {
        Assert.assertNotNull(baseEntity.withIdentifier("", ""));
    }

    @Test
    public void withAddresses() {
        Assert.assertNotNull(baseEntity.withAddresses(addresses));
    }

    @Test
    public void withAddress() {
        Address address = new Address();
        Assert.assertNotNull(baseEntity.withAddress(address));
    }

    
    public BaseEntity withAttributes(Map<String, Object> attributes) {
        return baseEntity.withAttributes(attributes);
    }

    
    public BaseEntity withAttribute(String name, Object value) {
        return baseEntity.withAttribute(name, value);
    }
}
