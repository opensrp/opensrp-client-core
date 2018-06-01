package org.smartregister.domain.jsonmapping;

import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TeamMember;

/**
 * Created by keyman on 2/28/2018.
 */

public class LoginResponseData {
    public User user;
    public Time time;
    public LocationTree locations;
    public TeamMember team;
}
