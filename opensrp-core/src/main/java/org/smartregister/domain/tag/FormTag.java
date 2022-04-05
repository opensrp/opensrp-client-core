package org.smartregister.domain.tag;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Created by keyman on 05/07/2018.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class FormTag {

    public String providerId;
    public String locationId;
    public String childLocationId;
    public String team;
    public String teamId;
    public Integer appVersion;
    public Integer databaseVersion;
    public String formSubmissionId;
    public String appVersionName;

}
