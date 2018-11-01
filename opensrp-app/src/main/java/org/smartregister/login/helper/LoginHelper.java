package org.smartregister.login.helper;

import android.util.Log;
import android.util.Pair;

import org.apache.commons.lang3.StringUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.domain.form.FormLocation;
import org.smartregister.domain.jsonmapping.Location;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.domain.jsonmapping.util.TreeNode;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.util.AssetHandler;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.BaseLoginContract;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by ndegwamartin on 09/04/2018.
 */

public class LoginHelper {
    private static final String TAG = LoginHelper.class.getCanonicalName();

    private static LoginHelper instance;
    BaseLoginContract.Interactor mLoginInteractor;
    private LoginHelper(BaseLoginContract.Interactor mLoginInteractor) {

        this.mLoginInteractor = mLoginInteractor;
    }

    public static void init(BaseLoginContract.Interactor mLoginInteractor) {
        if (instance == null && mLoginInteractor!=null) {
            instance = new LoginHelper(mLoginInteractor);
        }
    }

    public static LoginHelper getInstance() {
        return instance;
    }




}




