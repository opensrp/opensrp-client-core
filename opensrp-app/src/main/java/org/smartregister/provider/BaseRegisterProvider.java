package org.smartregister.provider;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.apache.commons.lang3.text.WordUtils;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.commonregistry.CommonPersonObjectClient;
import org.smartregister.configuration.RegisterProviderMetadata;
import org.smartregister.configuration.BaseRegisterRowOptions;
import org.smartregister.configuration.ConfigurableComponent;
import org.smartregister.configuration.ConfigurableComponentImpl;
import org.smartregister.configuration.ModuleConfiguration;
import org.smartregister.cursoradapter.RecyclerViewProvider;
import org.smartregister.holders.BaseRegisterViewHolder;
import org.smartregister.holders.FooterViewHolder;
import org.smartregister.util.ConfigurationInstancesHelper;
import org.smartregister.util.RegisterViewConstants;
import org.smartregister.util.Utils;
import org.smartregister.view.contract.SmartRegisterClient;
import org.smartregister.view.contract.SmartRegisterClients;
import org.smartregister.view.dialog.FilterOption;
import org.smartregister.view.dialog.ServiceModeOption;
import org.smartregister.view.dialog.SortOption;
import org.smartregister.view.viewholder.OnClickFormLauncher;

import java.text.MessageFormat;
import java.util.Map;


/**
 * Created by Ephraim Kigamba - nek.eam@gmail.com on 24-09-2020
 */

public class BaseRegisterProvider extends ConfigurableComponentImpl implements RecyclerViewProvider<BaseRegisterViewHolder>, ConfigurableComponent {

    private final LayoutInflater inflater;
    private View.OnClickListener onClickListener;
    private View.OnClickListener paginationClickListener;
    private Context context;

    private RegisterProviderMetadata registerProviderMetadata;

    @Nullable
    private BaseRegisterRowOptions baseRegisterRowOptions;

    public BaseRegisterProvider(@NonNull Context context, @NonNull View.OnClickListener onClickListener, @NonNull View.OnClickListener paginationClickListener) {

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.onClickListener = onClickListener;
        this.paginationClickListener = paginationClickListener;
        this.context = context;

        // Get the configuration
        ModuleConfiguration moduleConfiguration = CoreLibrary.getInstance()
                .getModuleConfiguration(CoreLibrary.getInstance().getCurrentModuleName());

        this.registerProviderMetadata = ConfigurationInstancesHelper
                .newInstance(moduleConfiguration.getRegisterProviderMetadata());

        Class<? extends BaseRegisterRowOptions> registerRowOptions = moduleConfiguration.getRegisterRowOptions();
        if (registerRowOptions != null) {
            this.baseRegisterRowOptions = ConfigurationInstancesHelper.newInstance(registerRowOptions);
        }
    }

    public static void fillValue(@Nullable TextView v, @NonNull String value) {
        if (v != null) {
            v.setText(value);
        }
    }

    @Override
    public void getView(Cursor cursor, SmartRegisterClient client, BaseRegisterViewHolder viewHolder) {
        CommonPersonObjectClient pc = (CommonPersonObjectClient) client;

        if (baseRegisterRowOptions != null && baseRegisterRowOptions.isDefaultPopulatePatientColumn()) {
            baseRegisterRowOptions.populateClientRow(cursor, pc, client, viewHolder);
        } else {
            populatePatientColumn(pc, viewHolder);

            if (baseRegisterRowOptions != null) {
                baseRegisterRowOptions.populateClientRow(cursor, pc, client, viewHolder);
            }
        }
    }

    @Override
    public void getFooterView(RecyclerView.ViewHolder viewHolder, int currentPageCount, int totalPageCount, boolean hasNext, boolean hasPrevious) {
        FooterViewHolder footerViewHolder = (FooterViewHolder) viewHolder;
        footerViewHolder.pageInfoView.setText(
                MessageFormat.format(context.getString(R.string.base_register_page_numbering), currentPageCount,
                        totalPageCount));

        footerViewHolder.nextPageView.setVisibility(hasNext ? View.VISIBLE : View.INVISIBLE);
        footerViewHolder.previousPageView.setVisibility(hasPrevious ? View.VISIBLE : View.INVISIBLE);

        footerViewHolder.nextPageView.setOnClickListener(paginationClickListener);
        footerViewHolder.previousPageView.setOnClickListener(paginationClickListener);
    }

    @Override
    public SmartRegisterClients updateClients(FilterOption villageFilter, ServiceModeOption serviceModeOption, FilterOption searchFilter, SortOption sortOption) {
        return null;
    }

    @Override
    public void onServiceModeSelected(ServiceModeOption serviceModeOption) {//Implement Abstract Method
    }

    @Override
    public OnClickFormLauncher newFormLauncher(String formName, String entityId, String metaData) {
        return null;
    }

    @Override
    public LayoutInflater inflater() {
        return inflater;
    }

    @Override
    public BaseRegisterViewHolder createViewHolder(ViewGroup parent) {
        int resId = R.layout.base_configurable_register_list_row;

        if (baseRegisterRowOptions != null
                && baseRegisterRowOptions.useCustomViewLayout()
                && baseRegisterRowOptions.getCustomViewLayoutId() != 0) {
            resId = baseRegisterRowOptions.getCustomViewLayoutId();
        }

        View view = inflater.inflate(resId, parent, false);

        if (baseRegisterRowOptions != null && baseRegisterRowOptions.isCustomViewHolder()) {
            return baseRegisterRowOptions.createCustomViewHolder(view);
        } else {
            return new BaseRegisterViewHolder(view);
        }
    }

    @Override
    public RecyclerView.ViewHolder createFooterHolder(ViewGroup parent) {
        View view = inflater.inflate(R.layout.smart_register_pagination, parent, false);
        return new FooterViewHolder(view);
    }

    @Override
    public boolean isFooterViewHolder(RecyclerView.ViewHolder viewHolder) {
        return viewHolder instanceof FooterViewHolder;
    }

    public void populatePatientColumn(CommonPersonObjectClient commonPersonObjectClient, BaseRegisterViewHolder viewHolder) {
        Map<String, String> patientColumnMaps = commonPersonObjectClient.getColumnmaps();

        if (registerProviderMetadata.isClientHaveGuardianDetails(patientColumnMaps)) {
            viewHolder.showGuardianName();

            String parentFirstName = registerProviderMetadata.getGuardianFirstName(patientColumnMaps);
            String parentLastName = registerProviderMetadata.getGuardianLastName(patientColumnMaps);
            String parentMiddleName = registerProviderMetadata.getGuardianMiddleName(patientColumnMaps);

            String parentName = context.getResources().getString(R.string.care_giver_initials)
                    + ": "
                    + org.smartregister.util.Utils.getName(parentFirstName, parentMiddleName + " " + parentLastName);
            fillValue(viewHolder.textViewParentName, WordUtils.capitalize(parentName));
        } else {
            viewHolder.removeGuardianName();
        }

        String firstName = registerProviderMetadata.getClientFirstName(patientColumnMaps);
        String middleName = registerProviderMetadata.getClientMiddleName(patientColumnMaps);
        String lastName = registerProviderMetadata.getClientLastName(patientColumnMaps);
        String childName = org.smartregister.util.Utils.getName(firstName, middleName + " " + lastName);

        String dobString = Utils.getDuration(registerProviderMetadata.getDob(patientColumnMaps));
        String translatedYearInitial = context.getResources().getString(R.string.abbrv_years);
        fillValue(viewHolder.textViewChildName, WordUtils.capitalize(childName) + ", " +
                WordUtils.capitalize(Utils.getClientAge(dobString, translatedYearInitial)));
        String registerType = registerProviderMetadata.getRegisterType(patientColumnMaps);

        if (!TextUtils.isEmpty(registerType)) {
            viewHolder.showRegisterType();
            fillValue(viewHolder.tvRegisterType, registerType);
        } else {
            viewHolder.hideRegisterType();
        }

        setAddressAndGender(commonPersonObjectClient, viewHolder);
        addButtonClickListeners(commonPersonObjectClient, viewHolder);
    }

    public void setAddressAndGender(CommonPersonObjectClient pc, BaseRegisterViewHolder viewHolder) {
        Map<String, String> patientColumnMaps = pc.getColumnmaps();
        String address = registerProviderMetadata.getHomeAddress(patientColumnMaps);
        String gender = registerProviderMetadata.getGender(patientColumnMaps);

        fillValue(viewHolder.textViewGender, gender);

        if (TextUtils.isEmpty(address)) {
            viewHolder.removePersonLocation();
        } else {
            viewHolder.showPersonLocation();
            fillValue(viewHolder.tvLocation, address);
        }
    }

    public void addButtonClickListeners(@NonNull CommonPersonObjectClient client, BaseRegisterViewHolder viewHolder) {
        View patient = viewHolder.childColumn;
        attachPatientOnclickListener(RegisterViewConstants.Provider.CHILD_COLUMN, patient, client);
        attachPatientOnclickListener(RegisterViewConstants.Provider.ACTION_BUTTON_COLUMN, viewHolder.dueButton, client);
    }

    public void attachPatientOnclickListener(@NonNull String viewType, @NonNull View view, @NonNull CommonPersonObjectClient client) {
        view.setOnClickListener(onClickListener);
        view.setTag(R.id.VIEW_TYPE, viewType);
        view.setTag(R.id.VIEW_CLIENT, client);
    }
}