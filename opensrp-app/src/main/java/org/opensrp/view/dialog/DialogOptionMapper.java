package org.opensrp.view.dialog;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import org.opensrp.util.StringUtil;
import org.opensrp.view.contract.Village;

public class DialogOptionMapper {
    public Iterable<? extends DialogOption> mapToVillageFilterOptions(Iterable<Village> villages) {
        return Iterables.transform(villages, new Function<Village, DialogOption>() {
            @Override
            public DialogOption apply(Village village) {
                return new VillageFilter(StringUtil.humanize(village.name()));
            }
        });
    }
}
