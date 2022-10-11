package org.smartregister.sync.helper;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;
import org.powermock.reflect.Whitebox;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.util.ReflectionHelpers;
import org.smartregister.AllConstants;
import org.smartregister.BaseUnitTest;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.domain.Response;
import org.smartregister.domain.ResponseStatus;
import org.smartregister.domain.jsonmapping.util.LocationTree;
import org.smartregister.dto.UserAssignmentDTO;
import org.smartregister.repository.AllSettings;
import org.smartregister.repository.AllSharedPreferences;
import org.smartregister.repository.LocationRepository;
import org.smartregister.repository.PlanDefinitionRepository;
import org.smartregister.service.HTTPAgent;
import org.smartregister.service.UserService;
import org.smartregister.util.AppProperties;
import org.smartregister.util.SyncUtils;
import org.smartregister.view.controller.ANMLocationController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.nullable;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.smartregister.account.AccountHelper.CONFIGURATION_CONSTANTS.IS_KEYCLOAK_CONFIGURED;
import static org.smartregister.sync.helper.LocationServiceHelper.LOCATION_LAST_SYNC_DATE;
import static org.smartregister.sync.helper.LocationServiceHelper.STRUCTURES_LAST_SYNC_DATE;
import static org.smartregister.sync.helper.PlanIntentServiceHelper.PLAN_LAST_SYNC_DATE;
import static org.smartregister.sync.helper.TaskServiceHelper.TASK_LAST_SYNC_DATE;
import static org.smartregister.sync.helper.ValidateAssignmentHelper.gson;

/**
 * Created by samuelgithengi on 9/18/20.
 */
public class ValidateAssignmentHelperTest extends BaseUnitTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private SyncUtils syncUtils;

    @Mock
    private Context context;

    @Mock
    private AllSettings settingsRepository;

    @Mock
    private UserService userService;

    @Mock
    private AllSharedPreferences allSharedPreferences;

    @Mock
    private ANMLocationController anmLocationController;

    @Mock
    private HTTPAgent httpAgent;

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private PlanDefinitionRepository planDefinitionRepository;

    @Captor
    private ArgumentCaptor<String> stringArgumentCaptor;

    private ValidateAssignmentHelper validateAssignmentHelper;

    private UserAssignmentDTO userAssignment;

    private static String locationHierarchy = "{\"locationsHierarchy\":{\"map\":{\"ecfbf048-fb7a-47d8-a12b-61bf5d2a6e7b\":{\"children\":{\"b4d25f09-1365-41aa-ac37-f5768d9075bf\":{\"children\":{\"7d1297d2-a7e7-4896-a391-31d14cab9d32\":{\"children\":{\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\":{\"children\":{\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\":{\"children\":{\"b4d3fbde-3686-4472-b3c4-7e28ba455168\":{\"id\":\"b4d3fbde-3686-4472-b3c4-7e28ba455168\",\"label\":\"ksh_6(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"b4d3fbde-3686-4472-b3c4-7e28ba455168\",\"name\":\"ksh_6(2020)\",\"parentLocation\":{\"locationId\":\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\"},\"6be2f032-ab8e-4f0d-999c-d951f7040418\":{\"id\":\"6be2f032-ab8e-4f0d-999c-d951f7040418\",\"label\":\"ksh_49(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"6be2f032-ab8e-4f0d-999c-d951f7040418\",\"name\":\"ksh_49(2020)\",\"parentLocation\":{\"locationId\":\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\"},\"78db3a15-249b-45ac-94fb-fa00c7c96033\":{\"id\":\"78db3a15-249b-45ac-94fb-fa00c7c96033\",\"label\":\"ksh_8(2020) other\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"78db3a15-249b-45ac-94fb-fa00c7c96033\",\"name\":\"ksh_8(2020) other\",\"parentLocation\":{\"locationId\":\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\"},\"009537b2-3722-4254-9aa6-1a0288ecfe41\":{\"id\":\"009537b2-3722-4254-9aa6-1a0288ecfe41\",\"label\":\"ksh_189(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"009537b2-3722-4254-9aa6-1a0288ecfe41\",\"name\":\"ksh_189(2020)\",\"parentLocation\":{\"locationId\":\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\"}},\"id\":\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\",\"label\":\"chandiwe Location\",\"node\":{\"attributes\":{\"geographicLevel\":4},\"locationId\":\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\",\"name\":\"chandiwe Location\",\"parentLocation\":{\"locationId\":\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Zones\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\"},\"61839c62-f517-4375-8a11-518bdca4764a\":{\"children\":{\"dd7fb291-d364-4300-b36e-ecd50a2c0b56\":{\"id\":\"dd7fb291-d364-4300-b36e-ecd50a2c0b56\",\"label\":\"ksh_101(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"dd7fb291-d364-4300-b36e-ecd50a2c0b56\",\"name\":\"ksh_101(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"e4fbbdd7-1bc6-4fa2-9cda-66f5f40d1af4\":{\"id\":\"e4fbbdd7-1bc6-4fa2-9cda-66f5f40d1af4\",\"label\":\"ksh_48(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"e4fbbdd7-1bc6-4fa2-9cda-66f5f40d1af4\",\"name\":\"ksh_48(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"73b065fd-9b3f-4a63-a43e-5d13a9bd554a\":{\"id\":\"73b065fd-9b3f-4a63-a43e-5d13a9bd554a\",\"label\":\"ksh_45(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"73b065fd-9b3f-4a63-a43e-5d13a9bd554a\",\"name\":\"ksh_45(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"2c211a66-3f0f-4e4f-a9b4-84000dd56c83\":{\"id\":\"2c211a66-3f0f-4e4f-a9b4-84000dd56c83\",\"label\":\"ksh_182(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"2c211a66-3f0f-4e4f-a9b4-84000dd56c83\",\"name\":\"ksh_182(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"d0a69405-4077-4246-85a5-ab47c2b95b65\":{\"id\":\"d0a69405-4077-4246-85a5-ab47c2b95b65\",\"label\":\"ksh_167(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"d0a69405-4077-4246-85a5-ab47c2b95b65\",\"name\":\"ksh_167(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"2f9d2079-0955-42be-a22c-14454cbf6803\":{\"id\":\"2f9d2079-0955-42be-a22c-14454cbf6803\",\"label\":\"ksh_200(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"2f9d2079-0955-42be-a22c-14454cbf6803\",\"name\":\"ksh_200(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"98e06915-08c4-4b81-9adf-3684ac4d8aad\":{\"id\":\"98e06915-08c4-4b81-9adf-3684ac4d8aad\",\"label\":\"ksh_106(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"98e06915-08c4-4b81-9adf-3684ac4d8aad\",\"name\":\"ksh_106(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"ace9f179-36bf-45c6-a912-4f60b21d359f\":{\"id\":\"ace9f179-36bf-45c6-a912-4f60b21d359f\",\"label\":\"ksh_98(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"ace9f179-36bf-45c6-a912-4f60b21d359f\",\"name\":\"ksh_98(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"4a8d1e0a-9b95-4343-94c0-e15923547f3c\":{\"id\":\"4a8d1e0a-9b95-4343-94c0-e15923547f3c\",\"label\":\"ksh_75(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"4a8d1e0a-9b95-4343-94c0-e15923547f3c\",\"name\":\"ksh_75(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"38a55422-1046-4aea-86ee-3a8f935604de\":{\"id\":\"38a55422-1046-4aea-86ee-3a8f935604de\",\"label\":\"ksh_64(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"38a55422-1046-4aea-86ee-3a8f935604de\",\"name\":\"ksh_64(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"88f7e352-cc9b-4fcc-a5b2-2a5fc5ca2bc8\":{\"id\":\"88f7e352-cc9b-4fcc-a5b2-2a5fc5ca2bc8\",\"label\":\"ksh_116(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"88f7e352-cc9b-4fcc-a5b2-2a5fc5ca2bc8\",\"name\":\"ksh_116(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"dc67e4a5-a5bd-4900-9999-ef8deab2ebfb\":{\"id\":\"dc67e4a5-a5bd-4900-9999-ef8deab2ebfb\",\"label\":\"ksh_73(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"dc67e4a5-a5bd-4900-9999-ef8deab2ebfb\",\"name\":\"ksh_73(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"70d12e8a-ec28-4b79-90d3-15c7602c6e66\":{\"id\":\"70d12e8a-ec28-4b79-90d3-15c7602c6e66\",\"label\":\"ksh_53(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"70d12e8a-ec28-4b79-90d3-15c7602c6e66\",\"name\":\"ksh_53(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"f2f5d806-4ad1-4cee-9e5b-74c5298aa41a\":{\"id\":\"f2f5d806-4ad1-4cee-9e5b-74c5298aa41a\",\"label\":\"ksh_91(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"f2f5d806-4ad1-4cee-9e5b-74c5298aa41a\",\"name\":\"ksh_91(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"93f631fd-1142-4190-9433-15a0c26c1911\":{\"id\":\"93f631fd-1142-4190-9433-15a0c26c1911\",\"label\":\"ksh_79(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"93f631fd-1142-4190-9433-15a0c26c1911\",\"name\":\"ksh_79(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"7846f6f5-aa97-4286-a05d-7ec91eeef2c7\":{\"id\":\"7846f6f5-aa97-4286-a05d-7ec91eeef2c7\",\"label\":\"ksh_183(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"7846f6f5-aa97-4286-a05d-7ec91eeef2c7\",\"name\":\"ksh_183(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"c7265a03-7614-4fb9-90b4-a420c59abe22\":{\"id\":\"c7265a03-7614-4fb9-90b4-a420c59abe22\",\"label\":\"ksh_195(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"c7265a03-7614-4fb9-90b4-a420c59abe22\",\"name\":\"ksh_195(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"cbfbc00e-4909-4c7a-986b-13ce408ba533\":{\"id\":\"cbfbc00e-4909-4c7a-986b-13ce408ba533\",\"label\":\"ksh_24(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"cbfbc00e-4909-4c7a-986b-13ce408ba533\",\"name\":\"ksh_24(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"dae968c3-a920-459e-bcaf-854d8135d08e\":{\"id\":\"dae968c3-a920-459e-bcaf-854d8135d08e\",\"label\":\"ksh_193(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"dae968c3-a920-459e-bcaf-854d8135d08e\",\"name\":\"ksh_193(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"32420842-869b-47a0-840d-037a99ae2e3b\":{\"id\":\"32420842-869b-47a0-840d-037a99ae2e3b\",\"label\":\"ksh_27(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"32420842-869b-47a0-840d-037a99ae2e3b\",\"name\":\"ksh_27(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"5b9ab1ac-166a-459a-b0e5-8e4ad85435c1\":{\"id\":\"5b9ab1ac-166a-459a-b0e5-8e4ad85435c1\",\"label\":\"ksh_134(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"5b9ab1ac-166a-459a-b0e5-8e4ad85435c1\",\"name\":\"ksh_134(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"29193e49-2c07-435d-b993-a4f59ed252ad\":{\"id\":\"29193e49-2c07-435d-b993-a4f59ed252ad\",\"label\":\"ksh_168(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"29193e49-2c07-435d-b993-a4f59ed252ad\",\"name\":\"ksh_168(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"36f559a4-ece3-4cd0-b387-a913e61cb448\":{\"id\":\"36f559a4-ece3-4cd0-b387-a913e61cb448\",\"label\":\"ksh_156(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"36f559a4-ece3-4cd0-b387-a913e61cb448\",\"name\":\"ksh_156(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"b28dfac7-d19b-492c-8c02-3bf044e73da8\":{\"id\":\"b28dfac7-d19b-492c-8c02-3bf044e73da8\",\"label\":\"ksh_164(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"b28dfac7-d19b-492c-8c02-3bf044e73da8\",\"name\":\"ksh_164(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"127e1601-2301-410b-ae6e-ccb9664613f7\":{\"id\":\"127e1601-2301-410b-ae6e-ccb9664613f7\",\"label\":\"ksh_115(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"127e1601-2301-410b-ae6e-ccb9664613f7\",\"name\":\"ksh_115(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"2efef2dd-dc23-46a3-86af-7c79b53cbcaa\":{\"id\":\"2efef2dd-dc23-46a3-86af-7c79b53cbcaa\",\"label\":\"ksh_111(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"2efef2dd-dc23-46a3-86af-7c79b53cbcaa\",\"name\":\"ksh_111(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"669f2894-7a53-411a-aab6-3a27bfff699e\":{\"id\":\"669f2894-7a53-411a-aab6-3a27bfff699e\",\"label\":\"ksh_135(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"669f2894-7a53-411a-aab6-3a27bfff699e\",\"name\":\"ksh_135(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"2d87af58-d39a-418a-ba9b-b14676fdb493\":{\"id\":\"2d87af58-d39a-418a-ba9b-b14676fdb493\",\"label\":\"ksh_76(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"2d87af58-d39a-418a-ba9b-b14676fdb493\",\"name\":\"ksh_76(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"4693cff0-5188-48c3-9dcb-01c975e0b9d9\":{\"id\":\"4693cff0-5188-48c3-9dcb-01c975e0b9d9\",\"label\":\"ksh_90(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"4693cff0-5188-48c3-9dcb-01c975e0b9d9\",\"name\":\"ksh_90(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"c587142c-171d-4426-8c54-b5864121cf05\":{\"id\":\"c587142c-171d-4426-8c54-b5864121cf05\",\"label\":\"ksh_158(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"c587142c-171d-4426-8c54-b5864121cf05\",\"name\":\"ksh_158(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"5343969b-847e-48be-9d16-bba1a3cd2d37\":{\"id\":\"5343969b-847e-48be-9d16-bba1a3cd2d37\",\"label\":\"ksh_104(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"5343969b-847e-48be-9d16-bba1a3cd2d37\",\"name\":\"ksh_104(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"feb02de2-571d-41f0-8aaa-5c38c153b1a6\":{\"id\":\"feb02de2-571d-41f0-8aaa-5c38c153b1a6\",\"label\":\"ksh_29(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"feb02de2-571d-41f0-8aaa-5c38c153b1a6\",\"name\":\"ksh_29(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"063b2844-f0fc-4fa5-828f-99157754769b\":{\"id\":\"063b2844-f0fc-4fa5-828f-99157754769b\",\"label\":\"ksh_105(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"063b2844-f0fc-4fa5-828f-99157754769b\",\"name\":\"ksh_105(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"0b3d0fff-a561-4406-92d2-5b3bdbeb44b1\":{\"id\":\"0b3d0fff-a561-4406-92d2-5b3bdbeb44b1\",\"label\":\"ksh_94(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"0b3d0fff-a561-4406-92d2-5b3bdbeb44b1\",\"name\":\"ksh_94(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"3c8f3ca8-c46a-427d-b83e-42fd68054f45\":{\"id\":\"3c8f3ca8-c46a-427d-b83e-42fd68054f45\",\"label\":\"ksh_184(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"3c8f3ca8-c46a-427d-b83e-42fd68054f45\",\"name\":\"ksh_184(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"0761a51c-1c7a-45c6-87e6-bae383493b2d\":{\"id\":\"0761a51c-1c7a-45c6-87e6-bae383493b2d\",\"label\":\"ksh_137(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"0761a51c-1c7a-45c6-87e6-bae383493b2d\",\"name\":\"ksh_137(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"6d9ed5c0-a5d9-479d-a1ad-33e676dba6aa\":{\"id\":\"6d9ed5c0-a5d9-479d-a1ad-33e676dba6aa\",\"label\":\"ksh_43(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"6d9ed5c0-a5d9-479d-a1ad-33e676dba6aa\",\"name\":\"ksh_43(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"26187afe-9c4a-4508-8e51-d4228eab2b7e\":{\"id\":\"26187afe-9c4a-4508-8e51-d4228eab2b7e\",\"label\":\"ksh_174(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"26187afe-9c4a-4508-8e51-d4228eab2b7e\",\"name\":\"ksh_174(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"af0b83ac-bf64-4db9-ac69-2399888a744f\":{\"id\":\"af0b83ac-bf64-4db9-ac69-2399888a744f\",\"label\":\"ksh_188(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"af0b83ac-bf64-4db9-ac69-2399888a744f\",\"name\":\"ksh_188(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"31c5c257-fc82-4919-a91f-f9e7d1e26fd0\":{\"id\":\"31c5c257-fc82-4919-a91f-f9e7d1e26fd0\",\"label\":\"ksh_166(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"31c5c257-fc82-4919-a91f-f9e7d1e26fd0\",\"name\":\"ksh_166(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"63f646fd-9fee-4e0b-bf08-4eff1e6b906b\":{\"id\":\"63f646fd-9fee-4e0b-bf08-4eff1e6b906b\",\"label\":\"ksh_83(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"63f646fd-9fee-4e0b-bf08-4eff1e6b906b\",\"name\":\"ksh_83(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"6ac85751-b1f2-4a3a-b87d-446ee6c475d7\":{\"id\":\"6ac85751-b1f2-4a3a-b87d-446ee6c475d7\",\"label\":\"ksh_39(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"6ac85751-b1f2-4a3a-b87d-446ee6c475d7\",\"name\":\"ksh_39(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"b347d67e-4a64-463f-a2ba-25e105c730de\":{\"id\":\"b347d67e-4a64-463f-a2ba-25e105c730de\",\"label\":\"ksh_107(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"b347d67e-4a64-463f-a2ba-25e105c730de\",\"name\":\"ksh_107(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"18091750-0695-465a-916b-f360353eb492\":{\"id\":\"18091750-0695-465a-916b-f360353eb492\",\"label\":\"ksh_66(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"18091750-0695-465a-916b-f360353eb492\",\"name\":\"ksh_66(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"a5b9abae-1990-4501-80fa-29d2e37ed0f7\":{\"id\":\"a5b9abae-1990-4501-80fa-29d2e37ed0f7\",\"label\":\"ksh_131(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"a5b9abae-1990-4501-80fa-29d2e37ed0f7\",\"name\":\"ksh_131(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"bdcb014f-890b-4483-9630-24b00e7fdcc6\":{\"id\":\"bdcb014f-890b-4483-9630-24b00e7fdcc6\",\"label\":\"ksh_114(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"bdcb014f-890b-4483-9630-24b00e7fdcc6\",\"name\":\"ksh_114(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"98c8fc6b-7c39-461b-a5fb-dd343bcf03b2\":{\"id\":\"98c8fc6b-7c39-461b-a5fb-dd343bcf03b2\",\"label\":\"ksh_96(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"98c8fc6b-7c39-461b-a5fb-dd343bcf03b2\",\"name\":\"ksh_96(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"2b60af50-24d2-49d4-9617-6ae326257a1f\":{\"id\":\"2b60af50-24d2-49d4-9617-6ae326257a1f\",\"label\":\"ksh_133(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"2b60af50-24d2-49d4-9617-6ae326257a1f\",\"name\":\"ksh_133(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"b85ad335-1dc2-48c2-890b-5ce04375bb00\":{\"id\":\"b85ad335-1dc2-48c2-890b-5ce04375bb00\",\"label\":\"ksh_120(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"b85ad335-1dc2-48c2-890b-5ce04375bb00\",\"name\":\"ksh_120(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"8d3c1ddf-6e9d-4cfc-a3eb-243d799469be\":{\"id\":\"8d3c1ddf-6e9d-4cfc-a3eb-243d799469be\",\"label\":\"ksh_18(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"8d3c1ddf-6e9d-4cfc-a3eb-243d799469be\",\"name\":\"ksh_18(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"feb6fbf7-7d97-4500-b499-f29f69bcfa08\":{\"id\":\"feb6fbf7-7d97-4500-b499-f29f69bcfa08\",\"label\":\"ksh_35(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"feb6fbf7-7d97-4500-b499-f29f69bcfa08\",\"name\":\"ksh_35(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"71e233bd-d742-4ec3-aa53-95a57f3ef049\":{\"id\":\"71e233bd-d742-4ec3-aa53-95a57f3ef049\",\"label\":\"ksh_132(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"71e233bd-d742-4ec3-aa53-95a57f3ef049\",\"name\":\"ksh_132(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"8d5e8dc9-1506-4883-b654-b954543b8cc0\":{\"id\":\"8d5e8dc9-1506-4883-b654-b954543b8cc0\",\"label\":\"ksh_52(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"8d5e8dc9-1506-4883-b654-b954543b8cc0\",\"name\":\"ksh_52(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"f148471a-8f7a-4004-89a3-0a86685f1a00\":{\"id\":\"f148471a-8f7a-4004-89a3-0a86685f1a00\",\"label\":\"ksh_145(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"f148471a-8f7a-4004-89a3-0a86685f1a00\",\"name\":\"ksh_145(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"f89e0fee-e50f-44cd-9ea4-03c8b5a3e36b\":{\"id\":\"f89e0fee-e50f-44cd-9ea4-03c8b5a3e36b\",\"label\":\"ksh_127(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"f89e0fee-e50f-44cd-9ea4-03c8b5a3e36b\",\"name\":\"ksh_127(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"b924cf58-2bc7-4cec-9da5-6f9b5329ee51\":{\"id\":\"b924cf58-2bc7-4cec-9da5-6f9b5329ee51\",\"label\":\"ksh_157(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"b924cf58-2bc7-4cec-9da5-6f9b5329ee51\",\"name\":\"ksh_157(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"3b8341b3-1c59-4143-8326-a35b761e28bc\":{\"id\":\"3b8341b3-1c59-4143-8326-a35b761e28bc\",\"label\":\"ksh_25(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"3b8341b3-1c59-4143-8326-a35b761e28bc\",\"name\":\"ksh_25(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"8cb212f1-ee34-4ec8-8326-e3f3f8b123a4\":{\"id\":\"8cb212f1-ee34-4ec8-8326-e3f3f8b123a4\",\"label\":\"ksh_126(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"8cb212f1-ee34-4ec8-8326-e3f3f8b123a4\",\"name\":\"ksh_126(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"ba323089-e23d-4097-aee2-9f3197e5caef\":{\"id\":\"ba323089-e23d-4097-aee2-9f3197e5caef\",\"label\":\"ksh_32(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"ba323089-e23d-4097-aee2-9f3197e5caef\",\"name\":\"ksh_32(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"},\"ffa0254d-55b0-4227-b662-f88f16b9776e\":{\"id\":\"ffa0254d-55b0-4227-b662-f88f16b9776e\",\"label\":\"ksh_46(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"ffa0254d-55b0-4227-b662-f88f16b9776e\",\"name\":\"ksh_46(2020)\",\"parentLocation\":{\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"61839c62-f517-4375-8a11-518bdca4764a\"}},\"id\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"label\":\"kasensele Location\",\"node\":{\"attributes\":{\"geographicLevel\":4},\"locationId\":\"61839c62-f517-4375-8a11-518bdca4764a\",\"name\":\"kasensele Location\",\"parentLocation\":{\"locationId\":\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Zones\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\"},\"4ed8f536-5c08-4203-8a90-a7e13becb01d\":{\"children\":{\"67c5e0a4-132f-457b-b573-9abf5ec95c75\":{\"id\":\"67c5e0a4-132f-457b-b573-9abf5ec95c75\",\"label\":\"ksh_2(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"67c5e0a4-132f-457b-b573-9abf5ec95c75\",\"name\":\"ksh_2(2020)\",\"parentLocation\":{\"locationId\":\"4ed8f536-5c08-4203-8a90-a7e13becb01d\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"4ed8f536-5c08-4203-8a90-a7e13becb01d\"}},\"id\":\"4ed8f536-5c08-4203-8a90-a7e13becb01d\",\"label\":\"nkomba kapepa Location\",\"node\":{\"attributes\":{\"geographicLevel\":4},\"locationId\":\"4ed8f536-5c08-4203-8a90-a7e13becb01d\",\"name\":\"nkomba kapepa Location\",\"parentLocation\":{\"locationId\":\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Zones\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\"},\"c20849ec-8537-49cf-b0d2-f1530c99a98b\":{\"children\":{\"853934ee-d1a6-4b69-9191-59047edbc9a8\":{\"id\":\"853934ee-d1a6-4b69-9191-59047edbc9a8\",\"label\":\"ksh_42(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"853934ee-d1a6-4b69-9191-59047edbc9a8\",\"name\":\"ksh_42(2020)\",\"parentLocation\":{\"locationId\":\"c20849ec-8537-49cf-b0d2-f1530c99a98b\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"c20849ec-8537-49cf-b0d2-f1530c99a98b\"},\"ef5540aa-b4c2-41f9-9052-a4cf63f7f528\":{\"id\":\"ef5540aa-b4c2-41f9-9052-a4cf63f7f528\",\"label\":\"ksh_9(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"ef5540aa-b4c2-41f9-9052-a4cf63f7f528\",\"name\":\"ksh_9(2020)\",\"parentLocation\":{\"locationId\":\"c20849ec-8537-49cf-b0d2-f1530c99a98b\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"c20849ec-8537-49cf-b0d2-f1530c99a98b\"},\"45e59e15-9d30-4099-8739-150f7b893ef7\":{\"id\":\"45e59e15-9d30-4099-8739-150f7b893ef7\",\"label\":\"ksh_78(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"45e59e15-9d30-4099-8739-150f7b893ef7\",\"name\":\"ksh_78(2020)\",\"parentLocation\":{\"locationId\":\"c20849ec-8537-49cf-b0d2-f1530c99a98b\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"c20849ec-8537-49cf-b0d2-f1530c99a98b\"},\"72e1e262-5244-41a3-a23e-6bd87b5de7e7\":{\"id\":\"72e1e262-5244-41a3-a23e-6bd87b5de7e7\",\"label\":\"ksh_34(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"72e1e262-5244-41a3-a23e-6bd87b5de7e7\",\"name\":\"ksh_34(2020)\",\"parentLocation\":{\"locationId\":\"c20849ec-8537-49cf-b0d2-f1530c99a98b\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"c20849ec-8537-49cf-b0d2-f1530c99a98b\"},\"c05a67b9-75d9-4c41-842c-704146a1057f\":{\"id\":\"c05a67b9-75d9-4c41-842c-704146a1057f\",\"label\":\"ksh_3(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":5},\"locationId\":\"c05a67b9-75d9-4c41-842c-704146a1057f\",\"name\":\"ksh_3(2020)\",\"parentLocation\":{\"locationId\":\"c20849ec-8537-49cf-b0d2-f1530c99a98b\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Operational Area\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"c20849ec-8537-49cf-b0d2-f1530c99a98b\"}},\"id\":\"c20849ec-8537-49cf-b0d2-f1530c99a98b\",\"label\":\"mutono Location\",\"node\":{\"attributes\":{\"geographicLevel\":4},\"locationId\":\"c20849ec-8537-49cf-b0d2-f1530c99a98b\",\"name\":\"mutono Location\",\"parentLocation\":{\"locationId\":\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Zones\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\"}},\"id\":\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\",\"label\":\"Kashikishi(2020)\",\"node\":{\"attributes\":{\"geographicLevel\":3},\"locationId\":\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\",\"name\":\"Kashikishi(2020)\",\"parentLocation\":{\"locationId\":\"7d1297d2-a7e7-4896-a391-31d14cab9d32\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Rural Health Centre\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"7d1297d2-a7e7-4896-a391-31d14cab9d32\"}},\"id\":\"7d1297d2-a7e7-4896-a391-31d14cab9d32\",\"label\":\"Nchelenge District (2020)\",\"node\":{\"attributes\":{\"geographicLevel\":2},\"locationId\":\"7d1297d2-a7e7-4896-a391-31d14cab9d32\",\"name\":\"Nchelenge District (2020)\",\"parentLocation\":{\"locationId\":\"b4d25f09-1365-41aa-ac37-f5768d9075bf\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"District\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"b4d25f09-1365-41aa-ac37-f5768d9075bf\"}},\"id\":\"b4d25f09-1365-41aa-ac37-f5768d9075bf\",\"label\":\"Luapula Province (2020)\",\"node\":{\"attributes\":{\"geographicLevel\":1},\"locationId\":\"b4d25f09-1365-41aa-ac37-f5768d9075bf\",\"name\":\"Luapula Province (2020)\",\"parentLocation\":{\"locationId\":\"ecfbf048-fb7a-47d8-a12b-61bf5d2a6e7b\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Province\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"ecfbf048-fb7a-47d8-a12b-61bf5d2a6e7b\"}},\"id\":\"ecfbf048-fb7a-47d8-a12b-61bf5d2a6e7b\",\"label\":\"Zambia(vl 2020)\",\"node\":{\"attributes\":{\"geographicLevel\":0},\"locationId\":\"ecfbf048-fb7a-47d8-a12b-61bf5d2a6e7b\",\"name\":\"Zambia(vl 2020)\",\"tags\":[\"Country\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"}}},\"parentChildren\":{\"b4d25f09-1365-41aa-ac37-f5768d9075bf\":[\"7d1297d2-a7e7-4896-a391-31d14cab9d32\"],\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\":[\"b4d3fbde-3686-4472-b3c4-7e28ba455168\",\"6be2f032-ab8e-4f0d-999c-d951f7040418\",\"78db3a15-249b-45ac-94fb-fa00c7c96033\",\"009537b2-3722-4254-9aa6-1a0288ecfe41\"],\"ecfbf048-fb7a-47d8-a12b-61bf5d2a6e7b\":[\"b4d25f09-1365-41aa-ac37-f5768d9075bf\"],\"7d1297d2-a7e7-4896-a391-31d14cab9d32\":[\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\"],\"61839c62-f517-4375-8a11-518bdca4764a\":[\"dd7fb291-d364-4300-b36e-ecd50a2c0b56\",\"e4fbbdd7-1bc6-4fa2-9cda-66f5f40d1af4\",\"73b065fd-9b3f-4a63-a43e-5d13a9bd554a\",\"2c211a66-3f0f-4e4f-a9b4-84000dd56c83\",\"d0a69405-4077-4246-85a5-ab47c2b95b65\",\"2f9d2079-0955-42be-a22c-14454cbf6803\",\"98e06915-08c4-4b81-9adf-3684ac4d8aad\",\"ace9f179-36bf-45c6-a912-4f60b21d359f\",\"4a8d1e0a-9b95-4343-94c0-e15923547f3c\",\"38a55422-1046-4aea-86ee-3a8f935604de\",\"88f7e352-cc9b-4fcc-a5b2-2a5fc5ca2bc8\",\"dc67e4a5-a5bd-4900-9999-ef8deab2ebfb\",\"70d12e8a-ec28-4b79-90d3-15c7602c6e66\",\"f2f5d806-4ad1-4cee-9e5b-74c5298aa41a\",\"93f631fd-1142-4190-9433-15a0c26c1911\",\"7846f6f5-aa97-4286-a05d-7ec91eeef2c7\",\"c7265a03-7614-4fb9-90b4-a420c59abe22\",\"cbfbc00e-4909-4c7a-986b-13ce408ba533\",\"dae968c3-a920-459e-bcaf-854d8135d08e\",\"32420842-869b-47a0-840d-037a99ae2e3b\",\"5b9ab1ac-166a-459a-b0e5-8e4ad85435c1\",\"29193e49-2c07-435d-b993-a4f59ed252ad\",\"36f559a4-ece3-4cd0-b387-a913e61cb448\",\"b28dfac7-d19b-492c-8c02-3bf044e73da8\",\"127e1601-2301-410b-ae6e-ccb9664613f7\",\"2efef2dd-dc23-46a3-86af-7c79b53cbcaa\",\"669f2894-7a53-411a-aab6-3a27bfff699e\",\"2d87af58-d39a-418a-ba9b-b14676fdb493\",\"4693cff0-5188-48c3-9dcb-01c975e0b9d9\",\"c587142c-171d-4426-8c54-b5864121cf05\",\"5343969b-847e-48be-9d16-bba1a3cd2d37\",\"feb02de2-571d-41f0-8aaa-5c38c153b1a6\",\"063b2844-f0fc-4fa5-828f-99157754769b\",\"0b3d0fff-a561-4406-92d2-5b3bdbeb44b1\",\"3c8f3ca8-c46a-427d-b83e-42fd68054f45\",\"0761a51c-1c7a-45c6-87e6-bae383493b2d\",\"6d9ed5c0-a5d9-479d-a1ad-33e676dba6aa\",\"26187afe-9c4a-4508-8e51-d4228eab2b7e\",\"af0b83ac-bf64-4db9-ac69-2399888a744f\",\"31c5c257-fc82-4919-a91f-f9e7d1e26fd0\",\"63f646fd-9fee-4e0b-bf08-4eff1e6b906b\",\"6ac85751-b1f2-4a3a-b87d-446ee6c475d7\",\"b347d67e-4a64-463f-a2ba-25e105c730de\",\"18091750-0695-465a-916b-f360353eb492\",\"a5b9abae-1990-4501-80fa-29d2e37ed0f7\",\"bdcb014f-890b-4483-9630-24b00e7fdcc6\",\"98c8fc6b-7c39-461b-a5fb-dd343bcf03b2\",\"2b60af50-24d2-49d4-9617-6ae326257a1f\",\"b85ad335-1dc2-48c2-890b-5ce04375bb00\",\"8d3c1ddf-6e9d-4cfc-a3eb-243d799469be\",\"feb6fbf7-7d97-4500-b499-f29f69bcfa08\",\"71e233bd-d742-4ec3-aa53-95a57f3ef049\",\"8d5e8dc9-1506-4883-b654-b954543b8cc0\",\"f148471a-8f7a-4004-89a3-0a86685f1a00\",\"f89e0fee-e50f-44cd-9ea4-03c8b5a3e36b\",\"b924cf58-2bc7-4cec-9da5-6f9b5329ee51\",\"3b8341b3-1c59-4143-8326-a35b761e28bc\",\"8cb212f1-ee34-4ec8-8326-e3f3f8b123a4\",\"ba323089-e23d-4097-aee2-9f3197e5caef\",\"ffa0254d-55b0-4227-b662-f88f16b9776e\"],\"c20849ec-8537-49cf-b0d2-f1530c99a98b\":[\"853934ee-d1a6-4b69-9191-59047edbc9a8\",\"ef5540aa-b4c2-41f9-9052-a4cf63f7f528\",\"45e59e15-9d30-4099-8739-150f7b893ef7\",\"72e1e262-5244-41a3-a23e-6bd87b5de7e7\",\"c05a67b9-75d9-4c41-842c-704146a1057f\"],\"4ed8f536-5c08-4203-8a90-a7e13becb01d\":[\"67c5e0a4-132f-457b-b573-9abf5ec95c75\"],\"dd5ba964-d76f-4f82-a4e3-c3f822d2ae2a\":[\"00800e86-80bf-4c84-b6a7-1d1289b3ac90\",\"61839c62-f517-4375-8a11-518bdca4764a\",\"4ed8f536-5c08-4203-8a90-a7e13becb01d\",\"c20849ec-8537-49cf-b0d2-f1530c99a98b\"]}}}";

    private LocationTree locationTree;

    @Before
    public void setUp() {
        validateAssignmentHelper = spy(new ValidateAssignmentHelper(syncUtils));
        Whitebox.setInternalState(validateAssignmentHelper, "settingsRepository", settingsRepository);
        Whitebox.setInternalState(validateAssignmentHelper, "anmLocationController", anmLocationController);
        Whitebox.setInternalState(validateAssignmentHelper, "userService", userService);
        Whitebox.setInternalState(validateAssignmentHelper, "allSharedPreferences", allSharedPreferences);
        Whitebox.setInternalState(validateAssignmentHelper, "httpAgent", httpAgent);
        Whitebox.setInternalState(validateAssignmentHelper, "locationRepository", locationRepository);
        Whitebox.setInternalState(validateAssignmentHelper, "planDefinitionRepository", planDefinitionRepository);
        when(settingsRepository.fetchANMLocation()).thenReturn(locationHierarchy);
        userAssignment = UserAssignmentDTO.builder()
                .jurisdictions(Collections.singleton("67c5e0a4-132f-457b-b573-9abf5ec95c75"))
                .organizationIds(Collections.singleton(1234L))
                .plans(Collections.singleton("plan1"))
                .build();
        doReturn(RuntimeEnvironment.application.getString(R.string.opensrp_url)).when(validateAssignmentHelper).getFormattedBaseUrl();
        locationTree = gson.fromJson(locationHierarchy, LocationTree.class);
    }


    @Test
    public void testRemoveLocationsFromHierarchyShouldRemoveLocations() throws Exception {
        Set<String> locations = new HashSet<>(Arrays.asList("853934ee-d1a6-4b69-9191-59047edbc9a8", "4ed8f536-5c08-4203-8a90-a7e13becb01d"));
        validateAssignmentHelper.removeLocationsFromHierarchy(locationTree, locations);
        verify(settingsRepository).saveANMLocation(stringArgumentCaptor.capture());
        assertNotEquals(locationHierarchy, stringArgumentCaptor.getValue());
        LocationTree locationTree = gson.fromJson(stringArgumentCaptor.getValue(), LocationTree.class);
        assertFalse(locationTree.hasLocation("853934ee-d1a6-4b69-9191-59047edbc9a8"));
        assertFalse(locationTree.hasLocation("4ed8f536-5c08-4203-8a90-a7e13becb01d"));
        assertFalse(locationTree.hasLocation("67c5e0a4-132f-457b-b573-9abf5ec95c75"));
        verify(anmLocationController).evict();
    }


    @Test
    public void testRemoveLocationsFromHierarchyShouldRemoveParentLocationIfOnlyChildIsRemoved() throws Exception {
        Set<String> locations = Collections.singleton("67c5e0a4-132f-457b-b573-9abf5ec95c75");
        validateAssignmentHelper.removeLocationsFromHierarchy(locationTree, locations);
        verify(settingsRepository).saveANMLocation(stringArgumentCaptor.capture());
        assertNotEquals(locationHierarchy, stringArgumentCaptor.getValue());
        LocationTree locationTree = gson.fromJson(stringArgumentCaptor.getValue(), LocationTree.class);

        assertFalse(locationTree.hasLocation("67c5e0a4-132f-457b-b573-9abf5ec95c75"));
        assertFalse(locationTree.hasLocation("4ed8f536-5c08-4203-8a90-a7e13becb01d"));//parent is removed
        verify(anmLocationController).evict();
    }

    @Test
    public void testRemoveLocationsFromHierarchyShouldLogoffIfDefaultLocationIsRemoved() throws Exception {
        when(allSharedPreferences.fetchDefaultLocalityId(nullable(String.class))).thenReturn("67c5e0a4-132f-457b-b573-9abf5ec95c75");
        Set<String> locations = Collections.singleton("67c5e0a4-132f-457b-b573-9abf5ec95c75");
        validateAssignmentHelper.removeLocationsFromHierarchy(locationTree, locations);
        verify(settingsRepository).saveANMLocation(stringArgumentCaptor.capture());
        assertNotEquals(locationHierarchy, stringArgumentCaptor.getValue());
        LocationTree locationTree = gson.fromJson(stringArgumentCaptor.getValue(), LocationTree.class);

        assertFalse(locationTree.hasLocation("67c5e0a4-132f-457b-b573-9abf5ec95c75"));
        assertFalse(locationTree.hasLocation("4ed8f536-5c08-4203-8a90-a7e13becb01d"));//parent is removed
        verify(anmLocationController).evict();
        verify(syncUtils).logoutUser(R.string.default_location_revoked_logged_off);
    }

    @Test
    public void testValidateUserAssignmentShouldDoNothingIfKeycloakIsNotEnabled() {
        validateAssignmentHelper.validateUserAssignment();
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(locationRepository);

    }

    @Test
    public void testValidateUserAssignmentShouldDoNothingIfAPICallReturnsAnError() {
        when(allSharedPreferences.getBooleanPreference(IS_KEYCLOAK_CONFIGURED)).thenReturn(true);
        when(httpAgent.fetch(anyString())).thenReturn(new Response<>(ResponseStatus.failure, null));
        validateAssignmentHelper.validateUserAssignment();
        verify(allSharedPreferences).getBooleanPreference(IS_KEYCLOAK_CONFIGURED);

        verify(httpAgent).fetch("http://27.147.129.50:9979/rest/organization/user-assignment");
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(locationRepository);

    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUserAssignmentAgentShouldReturnErrorIfAgentIsNull() throws Exception {
        assertNotNull(Whitebox.getInternalState(validateAssignmentHelper, "httpAgent"));
        Whitebox.setInternalState(validateAssignmentHelper, "httpAgent", (HTTPAgent) null);
        Whitebox.invokeMethod(validateAssignmentHelper, "getUserAssignment");
    }


    @Test
    public void testValidateUserAssignmentWithDownloadedLocationsShouldLogoffAndResetSync() throws Exception {

        List<String> results = new ArrayList<>(Collections.singleton("67c5e0a4-132f-457b-b573-9abf5ec95c75"));
        doReturn(results).when(locationRepository).getAllLocationIds();

        when(allSharedPreferences.getBooleanPreference(IS_KEYCLOAK_CONFIGURED)).thenReturn(true);
        when(httpAgent.fetch(anyString())).thenReturn(new Response<>(ResponseStatus.success, gson.toJson(userAssignment)));
        validateAssignmentHelper.validateUserAssignment();

        verify(allSharedPreferences).getBooleanPreference(IS_KEYCLOAK_CONFIGURED);
        verify(httpAgent).fetch(anyString());
        verify(planDefinitionRepository).findAllPlanDefinitionIds();
        verify(locationRepository).getAllLocationIds();
        verify(userService).fetchOrganizations();
        verify(userService).hasSessionExpired();


        verify(syncUtils).logoutUser(R.string.account_new_assignment_logged_off);

        verify(allSharedPreferences).savePreference(LOCATION_LAST_SYNC_DATE, "0");
        verify(allSharedPreferences).savePreference(STRUCTURES_LAST_SYNC_DATE, "0");
        verify(allSharedPreferences).savePreference(PLAN_LAST_SYNC_DATE, "0");
        verify(allSharedPreferences).savePreference(TASK_LAST_SYNC_DATE, "0");
        verify(allSharedPreferences).saveLastSyncDate(0);

        verifyNoMoreInteractions(planDefinitionRepository);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(locationRepository);

    }


    @Test
    public void testValidateUserAssignmentWithAuthenticationLocationsShouldLogoffAndResetSync() throws Exception {
        String anmLocation = "{\"locationsHierarchy\":{\"map\":{\"dfb6293c-5a9a-4b1c-87b1-5f92f3d41d68\":{\"children\":{\"860ce011-9109-4814-b81d-5bfb0977c195\":{\"children\":{\"9e64d862-f96a-4b8f-b4df-a264bbc920c8\":{\"children\":{\"3ccd757d-b7d3-45ef-853b-2b109d8b9b47\":{\"children\":{\"8162ed2e-6de0-4238-8d56-429145d1dd2c\":{\"children\":{\"d934eeb8-9762-4ce3-92d8-def05676ac1a\":{\"id\":\"d934eeb8-9762-4ce3-92d8-def05676ac1a\",\"label\":\"Village 21\",\"node\":{\"attributes\":{\"geographicLevel\":0.0},\"locationId\":\"d934eeb8-9762-4ce3-92d8-def05676ac1a\",\"name\":\"Village 21\",\"parentLocation\":{\"locationId\":\"8162ed2e-6de0-4238-8d56-429145d1dd2c\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"VILLAGE/COMMUNAUTE\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"8162ed2e-6de0-4238-8d56-429145d1dd2c\"}},\"id\":\"8162ed2e-6de0-4238-8d56-429145d1dd2c\",\"label\":\"CAC1\",\"node\":{\"attributes\":{\"geographicLevel\":0.0},\"locationId\":\"8162ed2e-6de0-4238-8d56-429145d1dd2c\",\"name\":\"CAC1\",\"parentLocation\":{\"locationId\":\"3ccd757d-b7d3-45ef-853b-2b109d8b9b47\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"CAC\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"3ccd757d-b7d3-45ef-853b-2b109d8b9b47\"}},\"id\":\"3ccd757d-b7d3-45ef-853b-2b109d8b9b47\",\"label\":\"Wakam\",\"node\":{\"attributes\":{\"geographicLevel\":0.0},\"locationId\":\"3ccd757d-b7d3-45ef-853b-2b109d8b9b47\",\"name\":\"Wakam\",\"parentLocation\":{\"locationId\":\"9e64d862-f96a-4b8f-b4df-a264bbc920c8\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Aire de Sante\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"9e64d862-f96a-4b8f-b4df-a264bbc920c8\"}},\"id\":\"9e64d862-f96a-4b8f-b4df-a264bbc920c8\",\"label\":\"Wesele\",\"node\":{\"attributes\":{\"geographicLevel\":0.0},\"locationId\":\"9e64d862-f96a-4b8f-b4df-a264bbc920c8\",\"name\":\"Wesele\",\"parentLocation\":{\"locationId\":\"860ce011-9109-4814-b81d-5bfb0977c195\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Zone de Sante\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"860ce011-9109-4814-b81d-5bfb0977c195\"}},\"id\":\"860ce011-9109-4814-b81d-5bfb0977c195\",\"label\":\"Wagadugu\",\"node\":{\"attributes\":{\"geographicLevel\":0.0},\"locationId\":\"860ce011-9109-4814-b81d-5bfb0977c195\",\"name\":\"Wagadugu\",\"parentLocation\":{\"locationId\":\"dfb6293c-5a9a-4b1c-87b1-5f92f3d41d68\",\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"tags\":[\"Province(DPS)\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"},\"parent\":\"dfb6293c-5a9a-4b1c-87b1-5f92f3d41d68\"}},\"id\":\"dfb6293c-5a9a-4b1c-87b1-5f92f3d41d68\",\"label\":\"Wakanda\",\"node\":{\"attributes\":{\"geographicLevel\":0.0},\"locationId\":\"dfb6293c-5a9a-4b1c-87b1-5f92f3d41d68\",\"name\":\"Wakanda\",\"tags\":[\"Pays\"],\"serverVersion\":0,\"voided\":false,\"type\":\"Location\"}}},\"parentChildren\":{\"3ccd757d-b7d3-45ef-853b-2b109d8b9b47\":[\"8162ed2e-6de0-4238-8d56-429145d1dd2c\"],\"dfb6293c-5a9a-4b1c-87b1-5f92f3d41d68\":[\"860ce011-9109-4814-b81d-5bfb0977c195\"],\"9e64d862-f96a-4b8f-b4df-a264bbc920c8\":[\"3ccd757d-b7d3-45ef-853b-2b109d8b9b47\"],\"860ce011-9109-4814-b81d-5bfb0977c195\":[\"9e64d862-f96a-4b8f-b4df-a264bbc920c8\"],\"8162ed2e-6de0-4238-8d56-429145d1dd2c\":[\"d934eeb8-9762-4ce3-92d8-def05676ac1a\"]}}}";
        doReturn(anmLocation).when(settingsRepository).fetchANMLocation();

        when(allSharedPreferences.getBooleanPreference(IS_KEYCLOAK_CONFIGURED)).thenReturn(true);
        when(httpAgent.fetch(anyString())).thenReturn(new Response<>(ResponseStatus.success, gson.toJson(userAssignment)));
        validateAssignmentHelper.validateUserAssignment();

        verify(allSharedPreferences).getBooleanPreference(IS_KEYCLOAK_CONFIGURED);
        verify(httpAgent).fetch(anyString());
        verify(planDefinitionRepository).findAllPlanDefinitionIds();
        verify(locationRepository).getAllLocationIds();
        verify(userService).fetchOrganizations();
        verify(userService).hasSessionExpired();


        verify(syncUtils).logoutUser(R.string.account_new_assignment_logged_off);

        verify(allSharedPreferences).savePreference(LOCATION_LAST_SYNC_DATE, "0");
        verify(allSharedPreferences).savePreference(STRUCTURES_LAST_SYNC_DATE, "0");
        verify(allSharedPreferences).savePreference(PLAN_LAST_SYNC_DATE, "0");
        verify(allSharedPreferences).savePreference(TASK_LAST_SYNC_DATE, "0");
        verify(allSharedPreferences).saveLastSyncDate(0);

        verifyNoMoreInteractions(planDefinitionRepository);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(locationRepository);

    }

    @Test
    public void testValidateUserAssignmentShouldClearRemovedAssignments() throws Exception {
        when(allSharedPreferences.getBooleanPreference(IS_KEYCLOAK_CONFIGURED)).thenReturn(true);
        when(httpAgent.fetch(anyString())).thenReturn(new Response<>(ResponseStatus.success, gson.toJson(userAssignment)));
        Set<Long> organizations = new HashSet<>(Arrays.asList(1234L, 1235L));
        List<String> jurisdictions = Arrays.asList("67c5e0a4-132f-457b-b573-9abf5ec95c75", "b4d3fbde-3686-4472-b3c4-7e28ba455168");
        when(userService.fetchOrganizations()).thenReturn(organizations);
        when(locationRepository.getAllLocationIds()).thenReturn(new ArrayList<>(jurisdictions));
        when(userService.fetchJurisdictionIds()).thenReturn(new HashSet<>(jurisdictions));
        when(planDefinitionRepository.findAllPlanDefinitionIds()).thenReturn(new HashSet<>(Arrays.asList("plan1", "plan12")));
        locationTree.deleteLocation("b4d3fbde-3686-4472-b3c4-7e28ba455168");
        when(settingsRepository.fetchANMLocation()).thenReturn(gson.toJson(locationTree));

        validateAssignmentHelper.validateUserAssignment();

        verify(allSharedPreferences, never()).savePreference(anyString(), eq("0"));
        verify(allSharedPreferences, never()).saveLastSyncDate(0);

        verify(allSharedPreferences).getBooleanPreference(IS_KEYCLOAK_CONFIGURED);
        verify(httpAgent).fetch(anyString());
        verify(planDefinitionRepository).findAllPlanDefinitionIds();
        verify(locationRepository).getAllLocationIds();
        verify(userService).fetchOrganizations();

        verify(planDefinitionRepository).deletePlans(Collections.singleton("plan12"));
        verify(userService).saveOrganizations(new ArrayList<>(userAssignment.getOrganizationIds()));

        verify(locationRepository).deleteLocations(Collections.singleton("b4d3fbde-3686-4472-b3c4-7e28ba455168"));
        verify(userService).saveJurisdictionIds(userAssignment.getJurisdictions());

    }

    @Test
    public void testValidateUserAssignmentShouldNotClearRemovedAssignments() throws Exception {
        CoreLibrary originalCoreLibrary = CoreLibrary.getInstance();
        CoreLibrary mockCoreLibrary = spy(originalCoreLibrary);
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", mockCoreLibrary);
        AppProperties appProperties = new AppProperties();
        appProperties.setProperty(AllConstants.PROPERTY.IGNORE_LOCATION_DELETION, Boolean.TRUE.toString());
        doReturn(appProperties).when(context).getAppProperties();
        doReturn(context).when(mockCoreLibrary).context();
        ReflectionHelpers.setStaticField(CoreLibrary.class, "instance", mockCoreLibrary);


        when(allSharedPreferences.getBooleanPreference(IS_KEYCLOAK_CONFIGURED)).thenReturn(true);
        when(httpAgent.fetch(anyString())).thenReturn(new Response<>(ResponseStatus.success, gson.toJson(userAssignment)));
        Set<Long> organizations = new HashSet<>(Arrays.asList(1234L, 1235L));
        List<String> jurisdictions = Arrays.asList("67c5e0a4-132f-457b-b573-9abf5ec95c75", "b4d3fbde-3686-4472-b3c4-7e28ba455168");
        when(userService.fetchOrganizations()).thenReturn(organizations);
        when(locationRepository.getAllLocationIds()).thenReturn(new ArrayList<>(jurisdictions));
        when(userService.fetchJurisdictionIds()).thenReturn(new HashSet<>(jurisdictions));
        when(planDefinitionRepository.findAllPlanDefinitionIds()).thenReturn(new HashSet<>(Arrays.asList("plan1", "plan12")));
        locationTree.deleteLocation("b4d3fbde-3686-4472-b3c4-7e28ba455168");
        when(settingsRepository.fetchANMLocation()).thenReturn(gson.toJson(locationTree));

        validateAssignmentHelper.validateUserAssignment();

        verify(allSharedPreferences, never()).savePreference(anyString(), eq("0"));
        verify(allSharedPreferences, never()).saveLastSyncDate(0);

        verify(allSharedPreferences).getBooleanPreference(IS_KEYCLOAK_CONFIGURED);
        verify(httpAgent).fetch(anyString());
        verify(planDefinitionRepository).findAllPlanDefinitionIds();
        verify(locationRepository).getAllLocationIds();
        verify(userService).fetchOrganizations();

        verifyNoMoreInteractions(planDefinitionRepository);
        verifyNoMoreInteractions(userService);
        verifyNoMoreInteractions(locationRepository);


    }
}
