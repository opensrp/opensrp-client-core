package org.smartregister.sample;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.smartregister.commonregistry.CommonRepository;
import org.smartregister.cursoradapter.RecyclerViewFragment;
import org.smartregister.cursoradapter.SmartRegisterQueryBuilder;
import org.smartregister.provider.SmartRegisterClientsProvider;
import org.smartregister.view.activity.SecuredNativeSmartRegisterActivity;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
//        SmartRegisterQueryBuilder srqb = new SmartRegisterQueryBuilder();
//        String query = srqb.searchQueryFts("ec_household",new String[]{"ec_woman","ec_child","ec_member"},"date_removed IS NULL ","ali","",20,0);
//        System.out.println(query);
        String tableName = "ec_household";
        String mainCondition = "date_removed IS NULL";
        SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();

        String[] columns = new String[]{
                tableName + ".relationalid",
                tableName + "." + KEY.LAST_INTERACTED_WITH,
                tableName + "." + KEY.BASE_ENTITY_ID,
                tableName + "." + KEY.FIRST_NAME,
                tableName + "." + KEY.LAST_NAME,
                tableName + "." + KEY.DOB,
                tableName + "." + "Patient_Identifier",
                tableName + "." + KEY.PHONE_NUMBER,
                "(select ec_details.value from ec_details where ec_details.key='address7' and ec_details.base_entity_id=ec_household.id) as para"};
        queryBUilder.SelectInitiateMainTable(tableName, columns);
        String query =  queryBUilder.mainCondition(mainCondition);
        System.out.println(query);
        new SampleFragment().init();
    }
    public static class SampleFragment extends RecyclerViewFragment {

        public void init(){
            SmartRegisterQueryBuilder queryBUilder = new SmartRegisterQueryBuilder();
            tablename = "ec_household";
            String mainCondition = "date_removed IS NULL";
            String FIFTY_YEAR = "1980-10-11";
            filters = "kader";
            joinTables = new String[]{"ec_woman","ec_child","ec_member"};
            Sortqueries = "last_interacted_with DESC";
            String[]  columns = new String[]{
                    tablename + ".relationalid",
                    tablename + "." + KEY.LAST_INTERACTED_WITH,
                    tablename + "." + KEY.BASE_ENTITY_ID,
                    tablename + "." + KEY.FIRST_NAME,
                    tablename + "." + KEY.LAST_NAME,
                    tablename + "." + KEY.DOB,
                    tablename + "." + "Patient_Identifier",
                    tablename + "." + KEY.PHONE_NUMBER,
                    "(select ec_details.value from ec_details where ec_details.key='address7' and ec_details.base_entity_id=ec_household.id) as para",
                    "(select ec_member.dob from ec_member where (ec_member.relational_id = ec_household.id and ec_member.dob < DATE('"+FIFTY_YEAR+"'))) as member_dob" ,
                    "(select ec_woman.dob from ec_woman where (ec_woman.relational_id = ec_household.id and ec_woman.dob < DATE('"+FIFTY_YEAR+"'))) as  woman_dob" ,
                    "(select ec_child.dob from ec_child where (ec_child.relational_id = ec_household.id and ec_child.dob < DATE('"+FIFTY_YEAR+"'))) as child_dob"
            };
            queryBUilder.SelectInitiateMainTable(tablename, columns);
            mainSelect =  queryBUilder.mainCondition(mainCondition);
            condition = "(child_dob IS NOT NULL OR member_dob IS NOT NULL OR woman_dob IS NOT NULL ) ";
            List<String> ids = new ArrayList<String>();
            ids.add("97715f4c-8ce5-4b78-9a06-935506fddc60");
            ids.add("a3998611-939a-4dde-9a04-dc3ea2d92c80");
            ids.add("69fca380-3e16-4664-9d35-249886b12015");
            ids.add("a9af1e88-4d97-45bb-bf7a-bb376c9a9c27");
            String sql = queryBUilder.searchQueryFts(tablename,mainSelect,mainCondition,condition,joinTables,filters,Sortqueries,20, 0);
            String query = queryBUilder.toStringFts(ids, tablename, CommonRepository.ID_COLUMN,
                    Sortqueries,condition);
            System.out.println(sql);
            System.out.println(query);

        }

        @Override
        protected SecuredNativeSmartRegisterActivity.DefaultOptionsProvider getDefaultOptionsProvider() {
            return null;
        }

        @Override
        protected SecuredNativeSmartRegisterActivity.NavBarOptionsProvider getNavBarOptionsProvider() {
            return null;
        }

        @Override
        protected SmartRegisterClientsProvider clientsProvider() {
            return null;
        }

        @Override
        protected void onInitialization() {

        }

        @Override
        protected void startRegistration() {

        }

        @Override
        protected void onCreation() {

        }
    }
    public static final class KEY {
        public static final String Patient_Identifier = "Patient_Identifier";

        public static final String STEPNAME = "stepName";
        public static final String NUMBER_PICKER = "number_picker";
        public static final String FIRST_NAME = "first_name";
        public static final String LAST_NAME = "last_name";
        public static final String BASE_ENTITY_ID = "base_entity_id";
        public static final String DOB = "dob";//Date Of Birth
        public static final String MEMBER_DOB = "member_birth_date";//Date Of Birth
        public static final String DOB_UNKNOWN = "dob_unknown";
        public static final String EDD = "edd";
        public static final String GENDER = "gender";
        public static final String ANC_ID = "anc_id";
        public static final String LAST_INTERACTED_WITH = "last_interacted_with";
        public static final String DATE_REMOVED = "date_removed";
        public static final String PHONE_NUMBER = "phone_number";
        public static final String PHONENUMBER = "phonenumber";
        public static final String CONTACT_PHONE_NUMBER = "contact_phone_number";
        public static final String CONTACT_PHONE_NUMBER_BY_AGE = "contact_phone_number_by_age";

        public static final String ALT_NAME = "alt_name";
        public static final String ALT_PHONE_NUMBER = "alt_phone_number";
        public static final String HOME_ADDRESS = "home_address";
        public static final String AGE = "age";
        public static final String member_Reg_Date = "member_Reg_Date";
        public static final String REMINDERS = "reminders";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
