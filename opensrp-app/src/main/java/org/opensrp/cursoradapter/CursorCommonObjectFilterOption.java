package org.opensrp.cursoradapter;

import net.sqlcipher.Cursor;

import org.opensrp.commonregistry.CommonPersonObjectClient;
import org.opensrp.view.contract.SmartRegisterClient;
import org.opensrp.view.dialog.FilterOption;

import static org.opensrp.util.StringUtil.humanize;

public class CursorCommonObjectFilterOption implements CursorFilterOption {

    private final String filterOptionName;
    String filterString;


    public CursorCommonObjectFilterOption(String filteroptionname,String filterString) {
       this.filterString = filterString;
        this.filterOptionName = filteroptionname;
    }

    @Override
    public String name() {
        return filterOptionName;
    }

    @Override
    public String filter() {

        return filterString;
    }

    @Override
    public boolean filter(SmartRegisterClient client) {
        return false;
    }
}
