package org.opensrp.service;

import org.opensrp.domain.ServiceProvided;
import org.opensrp.repository.AllServicesProvided;

import java.util.List;

public class ServiceProvidedService {
    private AllServicesProvided allServiceProvided;

    public ServiceProvidedService(AllServicesProvided allServicesProvided) {
        this.allServiceProvided = allServicesProvided;
    }

    public List<ServiceProvided> findByEntityIdAndServiceNames(String entityId, String... names) {
        return allServiceProvided.findByEntityIdAndServiceNames(entityId, names);
    }

    public void add(ServiceProvided serviceProvided) {
        allServiceProvided.add(serviceProvided);
    }
}
