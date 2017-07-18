package org.ei.opensrp.view.contract;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.ei.opensrp.util.StringUtil;

import java.util.ArrayList;

public class Villages extends ArrayList<Village> {
    public Iterable<String> getVillageNames() {
        return Iterables.transform(this, new Function<Village, String>() {
            @Override
            public String apply(Village village) {
                return StringUtil.humanize(village.name());
            }
        });
    }
}
