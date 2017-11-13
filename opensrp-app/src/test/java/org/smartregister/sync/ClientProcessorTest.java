package org.smartregister.sync;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.rule.PowerMockRule;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.android.controller.ActivityController;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.clientandeventmodel.Client;
import org.smartregister.repository.DetailsRepository;
import org.smartregister.repository.EventClientRepository;
import org.smartregister.sync.mock.MockActivity;
import org.smartregister.sync.mock.MockEditor;
import org.smartregister.util.AssetHandler;
import org.smartregister.view.activity.DrishtiApplication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * Created by raihan on 11/6/17.
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest({DrishtiApplication.class, AssetHandler.class, CloudantDataHandler.class, PreferenceManager.class})
@PowerMockIgnore({"javax.xml.*", "org.xml.sax.*", "org.w3c.dom.*", "org.springframework.context.*", "org.apache.log4j.*"})
public class ClientProcessorTest extends BaseUnitTest {

    @Mock
    private Context context;

    @Rule
    public PowerMockRule rule = new PowerMockRule();

    private ClientProcessor clientProcessor;

    @Mock
    CoreLibrary coreLibrary;

    @Mock
    DetailsRepository detailsRepository;
    @Mock
    CloudantDataHandler cloudantDataHandler;

    @Mock
    SharedPreferences sharedPreferences;
    private static final String LAST_SYNC_DATE = "LAST_SYNC_DATE";
//    private ActivityController<MockActivity> controller;
//    private MockActivity activity;
    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        CoreLibrary.init(context);
        Mockito.when(context.detailsRepository()).thenReturn(detailsRepository);

        PowerMockito.mockStatic(AssetHandler.class);
        PowerMockito.when(AssetHandler.readFileFromAssetsFolder("ec_client_classification.json", context.applicationContext())).thenReturn(ClientData.clientClassificationJson);
        PowerMockito.when(AssetHandler.readFileFromAssetsFolder("ec_client_fields.json", context.applicationContext())).thenReturn(ClientData.ec_client_fields_json);
        PowerMockito.when(AssetHandler.readFileFromAssetsFolder("ec_client_alerts.json", context.applicationContext())).thenReturn(ClientData.ec_client_alerts);
    }

    @Test
    public void processClientTest() throws Exception {

        clientProcessor = new ClientProcessor(context.applicationContext());
        PowerMockito.mockStatic(PreferenceManager.class);
        PowerMockito.when(PreferenceManager.getDefaultSharedPreferences(context.applicationContext())).thenReturn(sharedPreferences);
        PowerMockito.when(sharedPreferences.getLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(0l);
        SharedPreferences.Editor edit = Mockito.mock(SharedPreferences.Editor.class);
        PowerMockito.when(sharedPreferences.edit()).thenReturn(edit);
        PowerMockito.when(edit.putLong(Mockito.anyString(), Mockito.anyLong())).thenReturn(MockEditor.getEditor());

        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        final ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        for(int i = 0;i<eventArray.length();i++){
            eventList.add(eventArray.getJSONObject(i));
        }
//        JSONArray alertArray = new JSONArray(ClientData.ec_client_alerts);
//        ArrayList<JSONObject> alertList = new ArrayList<JSONObject>();
//        for(int i = 0;i<alertArray.length();i++){
//            alertList.add(alertArray.getJSONObject(i));
//        }
        ArrayList<JSONObject>eventsAndAlerts = new ArrayList<JSONObject>();
        eventsAndAlerts.addAll(eventList);
//        eventsAndAlerts.addAll(alertList);
        PowerMockito.mockStatic(CloudantDataHandler.class);
        PowerMockito.when(CloudantDataHandler.getInstance(context.applicationContext())).thenReturn(cloudantDataHandler);
        PowerMockito.when(cloudantDataHandler.getUpdatedEventsAndAlerts(Mockito.any(Date.class))).thenReturn(eventsAndAlerts);
        clientProcessor.processClient();
        clientProcessor.processClient(eventsAndAlerts);
    }

    @Test
    public void instantiatesSuccessfullyOnConstructorCall() throws Exception {
        PowerMockito.mockStatic(CloudantDataHandler.class);
        PowerMockito.when(CloudantDataHandler.getInstance(Mockito.any(android.content.Context.class))).thenReturn(cloudantDataHandler);
        Assert.assertNotNull(new ClientProcessor(context.applicationContext()));
        Assert.assertNotNull(ClientProcessor.getInstance(context.applicationContext()));

    }

    @Test
    public void ProcessEventReturnsNotNull() throws Exception {
        clientProcessor = new ClientProcessor(context.applicationContext());
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        final ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        for(int i = 0;i<eventArray.length();i++){
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<JSONObject>();
        for(int i = 0;i<clientArray.length();i++){
            clientList.add(clientArray.getJSONObject(i));
        }
        Assert.assertNotNull(clientProcessor.processEvent(eventList.get(0),clientList.get(0),new JSONObject(ClientData.clientClassificationJson)));

    }

    @Test
    public void getClientAddressAsMapReturnsNotNull() throws Exception {
        clientProcessor = new ClientProcessor(context.applicationContext());
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        final ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        for(int i = 0;i<eventArray.length();i++){
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<JSONObject>();
        for(int i = 0;i<clientArray.length();i++){
            clientList.add(clientArray.getJSONObject(i));
        }
        Assert.assertNotNull(clientProcessor.getClientAddressAsMap(clientList.get(0)));

    }

    @Test
    public void updateClientDetailsTableCallsSaveClientDetails() throws Exception {
        clientProcessor = new ClientProcessor(context.applicationContext());
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        final ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        for(int i = 0;i<eventArray.length();i++){
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<JSONObject>();
        for(int i = 0;i<clientArray.length();i++){
            clientList.add(clientArray.getJSONObject(i));
        }
        ClientProcessor clientProcessorspy = Mockito.spy(clientProcessor);
        clientProcessorspy.updateClientDetailsTable(eventList.get(0),clientList.get(0));
        Mockito.verify(clientProcessorspy,Mockito.atLeastOnce()).saveClientDetails(anyString(),ArgumentMatchers.<String, String>anyMap(),anyLong());

    }

    @Test
    public void processCaseModelReturnsNotNUll() throws Exception {
        clientProcessor = new ClientProcessor(context.applicationContext());
        JSONArray eventArray = new JSONArray(ClientData.eventJsonArray);
        final ArrayList<JSONObject> eventList = new ArrayList<JSONObject>();
        for(int i = 0;i<eventArray.length();i++){
            eventList.add(eventArray.getJSONObject(i));
        }
        JSONArray clientArray = new JSONArray(ClientData.clientJsonArray);
        ArrayList<JSONObject> clientList = new ArrayList<JSONObject>();
        for(int i = 0;i<clientArray.length();i++){
            clientList.add(clientArray.getJSONObject(i));
        }
//        ClientProcessor clientProcessorspy = Mockito.spy(clientProcessor);
        JSONObject clientClassification = new JSONObject(ClientData.clientClassificationJson);
        JSONArray clientClassificationArray = clientClassification.getJSONArray("case_classification_rules");
        JSONObject clientClassificationRulesObject = clientClassificationArray.getJSONObject(0);
        JSONArray creates_case = clientClassificationRulesObject.getJSONObject("rule").getJSONArray("fields").getJSONObject(0).getJSONArray("creates_case");
        Boolean b = clientProcessor.processCaseModel(eventList.get(0), clientList.get(0), creates_case);
//        ClientProcessor clientProcessorspy = Mockito.spy(clientProcessor);
        //Mockito.when(clientProcessor.getFileContents(anyString())).thenReturn(ec_client_fields_json);
//        for(int i=0;i<eventList.size();i++){
//            for(int j =0;j<clientList.size();j++){
//                for(int k =0;k<clientClassificationArray.length();k++){
//                    JSONObject clientClassificationRulesObject = clientClassificationArray.getJSONObject(k);
//                    JSONArray fieldArray = clientClassificationRulesObject.getJSONObject("rule").getJSONArray("fields");
//                    for(int f = 0; f<fieldArray.length();f++){
//                        JSONArray creates_case = fieldArray.getJSONObject(f).getJSONArray("creates_case");
//                        Boolean b = clientProcessor.processCaseModel(eventList.get(i),clientList.get(j),creates_case);
//                        if(b == null)
//                            System.out.println("NULL FOR: "+i+" "+j+" "+k+" "+f);
//                        else
//                            System.out.println(b.booleanValue()+"FOR: "+i+" "+j+" "+k+" "+f);
//                    }
//
//                }
//            }
//        }

//        Assert.assertEquals(Boolean.FALSE,clientProcessor.processCaseModel(eventList.get(0),clientList.get(0),creates_case));

    }




}
