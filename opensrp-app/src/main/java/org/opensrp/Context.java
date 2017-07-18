package org.opensrp;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;

import org.opensrp.commonregistry.AllCommonsRepository;
import org.opensrp.commonregistry.CommonFtsObject;
import org.opensrp.commonregistry.CommonPersonObjectClients;
import org.opensrp.commonregistry.CommonRepository;
import org.opensrp.commonregistry.CommonRepositoryInformationHolder;
import org.opensrp.repository.AlertRepository;
import org.opensrp.repository.AllAlerts;
import org.opensrp.repository.AllBeneficiaries;
import org.opensrp.repository.AllEligibleCouples;
import org.opensrp.repository.AllReports;
import org.opensrp.repository.AllServicesProvided;
import org.opensrp.repository.AllSettings;
import org.opensrp.repository.AllSharedPreferences;
import org.opensrp.repository.AllTimelineEvents;
import org.opensrp.repository.ChildRepository;
import org.opensrp.repository.DetailsRepository;
import org.opensrp.repository.DrishtiRepository;
import org.opensrp.repository.EligibleCoupleRepository;
import org.opensrp.repository.FormDataRepository;
import org.opensrp.repository.FormsVersionRepository;
import org.opensrp.repository.ImageRepository;
import org.opensrp.repository.MotherRepository;
import org.opensrp.repository.ReportRepository;
import org.opensrp.repository.Repository;
import org.opensrp.repository.ServiceProvidedRepository;
import org.opensrp.repository.SettingsRepository;
import org.opensrp.repository.TimelineEventRepository;
import org.opensrp.service.ANMService;
import org.opensrp.service.ActionService;
import org.opensrp.service.AlertService;
import org.opensrp.service.AllFormVersionSyncService;
import org.opensrp.service.BeneficiaryService;
import org.opensrp.service.ChildService;
import org.opensrp.service.DrishtiService;
import org.opensrp.service.EligibleCoupleService;
import org.opensrp.service.FormSubmissionService;
import org.opensrp.service.FormSubmissionSyncService;
import org.opensrp.service.HTTPAgent;
import org.opensrp.service.MotherService;
import org.opensrp.service.PendingFormSubmissionService;
import org.opensrp.service.ServiceProvidedService;
import org.opensrp.service.UserService;
import org.opensrp.service.ZiggyFileLoader;
import org.opensrp.service.ZiggyService;
import org.opensrp.service.formSubmissionHandler.ANCCloseHandler;
import org.opensrp.service.formSubmissionHandler.ANCInvestigationsHandler;
import org.opensrp.service.formSubmissionHandler.ANCRegistrationHandler;
import org.opensrp.service.formSubmissionHandler.ANCRegistrationOAHandler;
import org.opensrp.service.formSubmissionHandler.ANCVisitHandler;
import org.opensrp.service.formSubmissionHandler.ChildCloseHandler;
import org.opensrp.service.formSubmissionHandler.ChildIllnessHandler;
import org.opensrp.service.formSubmissionHandler.ChildImmunizationsHandler;
import org.opensrp.service.formSubmissionHandler.ChildRegistrationECHandler;
import org.opensrp.service.formSubmissionHandler.ChildRegistrationOAHandler;
import org.opensrp.service.formSubmissionHandler.DeliveryOutcomeHandler;
import org.opensrp.service.formSubmissionHandler.DeliveryPlanHandler;
import org.opensrp.service.formSubmissionHandler.ECCloseHandler;
import org.opensrp.service.formSubmissionHandler.ECEditHandler;
import org.opensrp.service.formSubmissionHandler.ECRegistrationHandler;
import org.opensrp.service.formSubmissionHandler.FPChangeHandler;
import org.opensrp.service.formSubmissionHandler.FPComplicationsHandler;
import org.opensrp.service.formSubmissionHandler.FormSubmissionRouter;
import org.opensrp.service.formSubmissionHandler.HBTestHandler;
import org.opensrp.service.formSubmissionHandler.IFAHandler;
import org.opensrp.service.formSubmissionHandler.PNCCloseHandler;
import org.opensrp.service.formSubmissionHandler.PNCRegistrationOAHandler;
import org.opensrp.service.formSubmissionHandler.PNCVisitHandler;
import org.opensrp.service.formSubmissionHandler.RenewFPProductHandler;
import org.opensrp.service.formSubmissionHandler.TTHandler;
import org.opensrp.service.formSubmissionHandler.VitaminAHandler;
import org.opensrp.sync.SaveANMLocationTask;
import org.opensrp.sync.SaveUserInfoTask;
import org.opensrp.util.Cache;
import org.opensrp.util.Session;
import org.opensrp.view.activity.DrishtiApplication;
import org.opensrp.view.contract.ANCClients;
import org.opensrp.view.contract.ECClients;
import org.opensrp.view.contract.FPClients;
import org.opensrp.view.contract.HomeContext;
import org.opensrp.view.contract.SmartRegisterClients;
import org.opensrp.view.contract.Villages;
import org.opensrp.view.contract.pnc.PNCClients;
import org.opensrp.view.controller.ANMController;
import org.opensrp.view.controller.ANMLocationController;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.preference.PreferenceManager.getDefaultSharedPreferences;
import static android.preference.PreferenceManager.setDefaultValues;

public class Context {
    private android.content.Context applicationContext;
    private static Context context = new Context();
    private static final String TAG = "Context";

    private Repository repository;
    private EligibleCoupleRepository eligibleCoupleRepository;
    private AlertRepository alertRepository;
    private SettingsRepository settingsRepository;
    private ChildRepository childRepository;
    private DetailsRepository detailsRepository;
    private MotherRepository motherRepository;
    private TimelineEventRepository timelineEventRepository;
    private ReportRepository reportRepository;
    private FormDataRepository formDataRepository;
    private ServiceProvidedRepository serviceProvidedRepository;
    private FormsVersionRepository formsVersionRepository;

    private AllSettings allSettings;
    private AllSharedPreferences allSharedPreferences;
    private AllAlerts allAlerts;
    private AllEligibleCouples allEligibleCouples;
    private AllBeneficiaries allBeneficiaries;
    private AllTimelineEvents allTimelineEvents;
    private AllReports allReports;
    private AllServicesProvided allServicesProvided;
    private AllCommonsRepository allCommonPersonObjectsRepository;
    private ImageRepository imageRepository;


    private DrishtiService drishtiService;
    private ActionService actionService;
    private FormSubmissionService formSubmissionService;
    private FormSubmissionSyncService formSubmissionSyncService;
    private ZiggyService ziggyService;
    private UserService userService;
    private AlertService alertService;
    private EligibleCoupleService eligibleCoupleService;
    private MotherService motherService;
    private ChildService childService;
    private ANMService anmService;
    private BeneficiaryService beneficiaryService;
    private ServiceProvidedService serviceProvidedService;
    private PendingFormSubmissionService pendingFormSubmissionService;
    private AllFormVersionSyncService allFormVersionSyncService;

    private Session session;
    private Cache<String> listCache;
    private Cache<SmartRegisterClients> smartRegisterClientsCache;
    private Cache<HomeContext> homeContextCache;
    private Cache<ECClients> ecClientsCache;
    private Cache<FPClients> fpClientsCache;
    private Cache<ANCClients> ancClientsCache;
    private Cache<PNCClients> pncClientsCache;
    private Cache<Villages> villagesCache;
    private Cache<Typeface> typefaceCache;
    private Cache<CommonPersonObjectClients> personObjectClientsCache;

    private HTTPAgent httpAgent;
    private ZiggyFileLoader ziggyFileLoader;

    private FormSubmissionRouter formSubmissionRouter;
    private ECRegistrationHandler ecRegistrationHandler;
    private FPComplicationsHandler fpComplicationsHandler;
    private FPChangeHandler fpChangeHandler;
    private RenewFPProductHandler renewFPProductHandler;
    private ECCloseHandler ecCloseHandler;
    private ANCRegistrationHandler ancRegistrationHandler;
    private ANCRegistrationOAHandler ancRegistrationOAHandler;
    private ANCVisitHandler ancVisitHandler;
    private ANCCloseHandler ancCloseHandler;
    private TTHandler ttHandler;
    private IFAHandler ifaHandler;
    private HBTestHandler hbTestHandler;
    private DeliveryOutcomeHandler deliveryOutcomeHandler;
    private DeliveryPlanHandler deliveryPlanHandler;
    private PNCRegistrationOAHandler pncRegistrationOAHandler;
    private PNCCloseHandler pncCloseHandler;
    private PNCVisitHandler pncVisitHandler;
    private ChildImmunizationsHandler childImmunizationsHandler;
    private ChildRegistrationECHandler childRegistrationECHandler;
    private ChildRegistrationOAHandler childRegistrationOAHandler;
    private ChildCloseHandler childCloseHandler;
    private ChildIllnessHandler childIllnessHandler;
    private VitaminAHandler vitaminAHandler;
    private ECEditHandler ecEditHandler;
    private ANCInvestigationsHandler ancInvestigationsHandler;
    private SaveANMLocationTask saveANMLocationTask;
    private SaveUserInfoTask saveUserInfoTask;

    private ANMController anmController;
    private ANMLocationController anmLocationController;

    protected DristhiConfiguration configuration;

    private CommonFtsObject commonFtsObject;

    private Map<String, String> customHumanReadableConceptResponse;

    ///////////////////common bindtypes///////////////
    public static ArrayList<CommonRepositoryInformationHolder> bindtypes;
    /////////////////////////////////////////////////
    protected Context() {
    }

    public android.content.Context applicationContext() {
        return applicationContext;
    }


    public static Context getInstance() {
        if (context == null){
            context = new Context();
        }
        return context;
    }

    public static Context setInstance(Context context) {
        Context.context = context;
        return context;
    }

    public BeneficiaryService beneficiaryService() {
        if (beneficiaryService == null) {
            beneficiaryService = new BeneficiaryService(allEligibleCouples(), allBeneficiaries());
        }
        return beneficiaryService;
    }

    public Context updateApplicationContext(android.content.Context applicationContext) {
        this.applicationContext = applicationContext;
        return this;
    }

    protected DrishtiService drishtiService() {
        if (drishtiService == null) {
            drishtiService = new DrishtiService(httpAgent(), configuration().dristhiBaseURL());
        }
        return drishtiService;
    }

    public ActionService actionService() {
        if (actionService == null) {
            actionService = new ActionService(drishtiService(), allSettings(), allSharedPreferences(), allReports());
        }
        return actionService;
    }

    public FormSubmissionService formSubmissionService() {
        initRepository();
        if (formSubmissionService == null) {
            if(commonFtsObject != null){
                formSubmissionService = new FormSubmissionService(ziggyService(), formDataRepository(), allSettings(), allCommonsRepositoryMap());
            } else {
                formSubmissionService = new FormSubmissionService(ziggyService(), formDataRepository(), allSettings());
            }
        }
        return formSubmissionService;
    }

    public AllFormVersionSyncService allFormVersionSyncService() {
        if(allFormVersionSyncService == null) {
            allFormVersionSyncService = new AllFormVersionSyncService(httpAgent(),
                    configuration(), formsVersionRepository());
        }
        return allFormVersionSyncService;
    }

    public FormSubmissionRouter formSubmissionRouter() {
        initRepository();
        if (formSubmissionRouter == null) {
            formSubmissionRouter = new FormSubmissionRouter(formDataRepository(), ecRegistrationHandler(),
                    fpComplicationsHandler(), fpChangeHandler(), renewFPProductHandler(), ecCloseHandler(),
                    ancRegistrationHandler(), ancRegistrationOAHandler(), ancVisitHandler(), ancCloseHandler(),
                    ttHandler(), ifaHandler(), hbTestHandler(), deliveryOutcomeHandler(), pncRegistrationOAHandler(),
                    pncCloseHandler(), pncVisitHandler(), childImmunizationsHandler(), childRegistrationECHandler(),
                    childRegistrationOAHandler(), childCloseHandler(), childIllnessHandler(), vitaminAHandler(),
                    deliveryPlanHandler(), ecEditHandler(), ancInvestigationsHandler());
        }
        return formSubmissionRouter;
    }

    private ChildCloseHandler childCloseHandler() {
        if (childCloseHandler == null) {
            childCloseHandler = new ChildCloseHandler(childService());
        }
        return childCloseHandler;
    }

    private ECRegistrationHandler ecRegistrationHandler() {
        if (ecRegistrationHandler == null) {
            ecRegistrationHandler = new ECRegistrationHandler(eligibleCoupleService());
        }
        return ecRegistrationHandler;
    }

    private FPComplicationsHandler fpComplicationsHandler() {
        if (fpComplicationsHandler == null) {
            fpComplicationsHandler = new FPComplicationsHandler(eligibleCoupleService());
        }
        return fpComplicationsHandler;
    }

    private FPChangeHandler fpChangeHandler() {
        if (fpChangeHandler == null) {
            fpChangeHandler = new FPChangeHandler(eligibleCoupleService());
        }
        return fpChangeHandler;
    }

    private RenewFPProductHandler renewFPProductHandler() {
        if (renewFPProductHandler == null) {
            renewFPProductHandler = new RenewFPProductHandler(eligibleCoupleService());
        }
        return renewFPProductHandler;
    }

    private ECCloseHandler ecCloseHandler() {
        if (ecCloseHandler == null) {
            ecCloseHandler = new ECCloseHandler(eligibleCoupleService());
        }
        return ecCloseHandler;
    }

    private ANCRegistrationHandler ancRegistrationHandler() {
        if (ancRegistrationHandler == null) {
            ancRegistrationHandler = new ANCRegistrationHandler(motherService());
        }
        return ancRegistrationHandler;
    }

    private ANCRegistrationOAHandler ancRegistrationOAHandler() {
        if (ancRegistrationOAHandler == null) {
            ancRegistrationOAHandler = new ANCRegistrationOAHandler(motherService());
        }
        return ancRegistrationOAHandler;
    }

    private ANCVisitHandler ancVisitHandler() {
        if (ancVisitHandler == null) {
            ancVisitHandler = new ANCVisitHandler(motherService());
        }
        return ancVisitHandler;
    }

    private ANCCloseHandler ancCloseHandler() {
        if (ancCloseHandler == null) {
            ancCloseHandler = new ANCCloseHandler(motherService());
        }
        return ancCloseHandler;
    }

    private TTHandler ttHandler() {
        if (ttHandler == null) {
            ttHandler = new TTHandler(motherService());
        }
        return ttHandler;
    }

    private IFAHandler ifaHandler() {
        if (ifaHandler == null) {
            ifaHandler = new IFAHandler(motherService());
        }
        return ifaHandler;
    }

    private HBTestHandler hbTestHandler() {
        if (hbTestHandler == null) {
            hbTestHandler = new HBTestHandler(motherService());
        }
        return hbTestHandler;
    }

    private DeliveryOutcomeHandler deliveryOutcomeHandler() {
        if (deliveryOutcomeHandler == null) {
            deliveryOutcomeHandler = new DeliveryOutcomeHandler(motherService(), childService());
        }
        return deliveryOutcomeHandler;
    }

    private DeliveryPlanHandler deliveryPlanHandler() {
        if (deliveryPlanHandler == null) {
            deliveryPlanHandler = new DeliveryPlanHandler(motherService());
        }
        return deliveryPlanHandler;
    }

    private PNCRegistrationOAHandler pncRegistrationOAHandler() {
        if (pncRegistrationOAHandler == null) {
            pncRegistrationOAHandler = new PNCRegistrationOAHandler(childService());
        }
        return pncRegistrationOAHandler;
    }

    private PNCCloseHandler pncCloseHandler() {
        if (pncCloseHandler == null) {
            pncCloseHandler = new PNCCloseHandler(motherService());
        }
        return pncCloseHandler;
    }

    private PNCVisitHandler pncVisitHandler() {
        if (pncVisitHandler == null) {
            pncVisitHandler = new PNCVisitHandler(motherService(), childService());
        }
        return pncVisitHandler;
    }

    private ChildImmunizationsHandler childImmunizationsHandler() {
        if (childImmunizationsHandler == null) {
            childImmunizationsHandler = new ChildImmunizationsHandler(childService());
        }
        return childImmunizationsHandler;
    }

    private ChildIllnessHandler childIllnessHandler() {
        if (childIllnessHandler == null) {
            childIllnessHandler = new ChildIllnessHandler(childService());
        }
        return childIllnessHandler;
    }

    private VitaminAHandler vitaminAHandler() {
        if (vitaminAHandler == null) {
            vitaminAHandler = new VitaminAHandler(childService());
        }
        return vitaminAHandler;
    }

    private ChildRegistrationECHandler childRegistrationECHandler() {
        if (childRegistrationECHandler == null) {
            childRegistrationECHandler = new ChildRegistrationECHandler(childService());
        }
        return childRegistrationECHandler;
    }

    private ChildRegistrationOAHandler childRegistrationOAHandler() {
        if (childRegistrationOAHandler == null) {
            childRegistrationOAHandler = new ChildRegistrationOAHandler(childService());
        }
        return childRegistrationOAHandler;
    }

    private ECEditHandler ecEditHandler() {
        if (ecEditHandler == null) {
            ecEditHandler = new ECEditHandler();
        }
        return ecEditHandler;
    }

    private ANCInvestigationsHandler ancInvestigationsHandler() {
        if (ancInvestigationsHandler == null) {
            ancInvestigationsHandler = new ANCInvestigationsHandler();
        }
        return ancInvestigationsHandler;
    }

    public ZiggyService ziggyService() {
        initRepository();
        if (ziggyService == null) {
            ziggyService = new ZiggyService(ziggyFileLoader(), formDataRepository(), formSubmissionRouter());
        }
        return ziggyService;
    }

    public ZiggyFileLoader ziggyFileLoader() {
        if (ziggyFileLoader == null) {
            ziggyFileLoader = new ZiggyFileLoader("www/ziggy", "www/form", applicationContext().getAssets());
        }
        return ziggyFileLoader;
    }

    public FormSubmissionSyncService formSubmissionSyncService() {
        if (formSubmissionSyncService == null) {
            formSubmissionSyncService = new FormSubmissionSyncService(formSubmissionService(), httpAgent(), formDataRepository(), allSettings(), allSharedPreferences(), configuration());
        }
        return formSubmissionSyncService;
    }

    protected HTTPAgent httpAgent() {
        if (httpAgent == null) {
            httpAgent = new HTTPAgent(applicationContext, allSettings(), allSharedPreferences(), configuration());
        }
        return httpAgent;
    }

    public Repository initRepository() {
        if(configuration().appName().equals(AllConstants.APP_NAME_INDONESIA)) {
            return null;
        }
        if (repository == null) {
                repository = DrishtiApplication.getInstance().getRepository();
        }
        return repository;
    }



    public ArrayList<DrishtiRepository> sharedRepositories(){
        assignbindtypes();
        ArrayList<DrishtiRepository> drishtireposotorylist = new ArrayList<DrishtiRepository>();
        drishtireposotorylist.add(settingsRepository());
        drishtireposotorylist.add(alertRepository());
        drishtireposotorylist.add(eligibleCoupleRepository());
        drishtireposotorylist.add(childRepository());
        drishtireposotorylist.add(timelineEventRepository());
        drishtireposotorylist.add(motherRepository());
        drishtireposotorylist.add(reportRepository());
        drishtireposotorylist.add(formDataRepository());
        drishtireposotorylist.add(serviceProvidedRepository());
        drishtireposotorylist.add(formsVersionRepository());
        drishtireposotorylist.add(imageRepository());
        drishtireposotorylist.add(detailsRepository());
        for(int i = 0;i < bindtypes.size();i++){
            drishtireposotorylist.add(commonrepository(bindtypes.get(i).getBindtypename()));
        }
        return drishtireposotorylist;

    }

    public DrishtiRepository[] sharedRepositoriesArray(){
        ArrayList<DrishtiRepository> drishtiRepositories = sharedRepositories();
        DrishtiRepository[] drishtireposotoryarray = drishtiRepositories.toArray(new DrishtiRepository[drishtiRepositories.size()]);
        return drishtireposotoryarray;
    }

    public AllEligibleCouples allEligibleCouples() {
        initRepository();
        if (allEligibleCouples == null) {
            allEligibleCouples = new AllEligibleCouples(eligibleCoupleRepository(), alertRepository(), timelineEventRepository());
        }
        return allEligibleCouples;
    }

    public AllAlerts allAlerts() {
        initRepository();
        if (allAlerts == null) {
            allAlerts = new AllAlerts(alertRepository());
        }
        return allAlerts;
    }

    public AllSettings allSettings() {
        initRepository();
        if (allSettings == null) {
            allSettings = new AllSettings(allSharedPreferences(), settingsRepository());
        }
        return allSettings;
    }

    public AllSharedPreferences allSharedPreferences() {
        if (allSharedPreferences == null) {
            setDefaultValues(this.applicationContext, R.xml.preferences, false);
            allSharedPreferences = new AllSharedPreferences(getDefaultSharedPreferences(this.applicationContext));
        }
        return allSharedPreferences;
    }

    public AllBeneficiaries allBeneficiaries() {
        initRepository();
        if (allBeneficiaries == null) {
            allBeneficiaries = new AllBeneficiaries(motherRepository(), childRepository(), alertRepository(), timelineEventRepository());
        }
        return allBeneficiaries;
    }

    public AllTimelineEvents allTimelineEvents() {
        initRepository();
        if (allTimelineEvents == null) {
            allTimelineEvents = new AllTimelineEvents(timelineEventRepository());
        }
        return allTimelineEvents;
    }

    public AllReports allReports() {
        initRepository();
        if (allReports == null) {
            allReports = new AllReports(reportRepository());
        }
        return allReports;
    }

    public AllServicesProvided allServicesProvided() {
        initRepository();
        if (allServicesProvided == null) {
            allServicesProvided = new AllServicesProvided(serviceProvidedRepository());
        }
        return allServicesProvided;
    }

    private EligibleCoupleRepository eligibleCoupleRepository() {
        if (eligibleCoupleRepository == null) {
            eligibleCoupleRepository = new EligibleCoupleRepository();
        }
        return eligibleCoupleRepository;
    }

    protected AlertRepository alertRepository() {
        if (alertRepository == null) {
            alertRepository = new AlertRepository();
        }
        return alertRepository;
    }

    protected SettingsRepository settingsRepository() {
        if (settingsRepository == null) {
            settingsRepository = new SettingsRepository();
        }
        return settingsRepository;
    }

    private ChildRepository childRepository() {
        if (childRepository == null) {
            childRepository = new ChildRepository();
        }
        return childRepository;
    }

    public DetailsRepository detailsRepository(){
        if (detailsRepository == null){
            detailsRepository = new DetailsRepository();
        }
        return detailsRepository;
    }

    private MotherRepository motherRepository() {
        if (motherRepository == null) {
            motherRepository = new MotherRepository();
        }
        return motherRepository;
    }

    protected TimelineEventRepository timelineEventRepository() {
        if (timelineEventRepository == null) {
            timelineEventRepository = new TimelineEventRepository();
        }
        return timelineEventRepository;
    }

    private ReportRepository reportRepository() {
        if (reportRepository == null) {
            reportRepository = new ReportRepository();
        }
        return reportRepository;
    }

    public FormDataRepository formDataRepository() {
        if (formDataRepository == null) {
            formDataRepository = new FormDataRepository();
        }
        return formDataRepository;
    }

    protected ServiceProvidedRepository serviceProvidedRepository() {
        if (serviceProvidedRepository == null) {
            serviceProvidedRepository = new ServiceProvidedRepository();
        }
        return serviceProvidedRepository;
    }

    protected FormsVersionRepository formsVersionRepository() {
        if (formsVersionRepository == null) {
            formsVersionRepository = new FormsVersionRepository();
        }
        return formsVersionRepository;
    }
    public ImageRepository imageRepository() {
        if (imageRepository == null) {
            imageRepository = new ImageRepository();
        }
        return imageRepository;
    }

    public UserService userService() {
        if (userService == null) {
            repository = initRepository();
            userService = new UserService(repository, allSettings(), allSharedPreferences(), httpAgent(), session(), configuration(), saveANMLocationTask(),saveUserInfoTask());
        }
        return userService;
    }

    private SaveANMLocationTask saveANMLocationTask() {
        if (saveANMLocationTask == null) {
            saveANMLocationTask = new SaveANMLocationTask(allSettings());
        }
        return saveANMLocationTask;
    }

    private SaveUserInfoTask saveUserInfoTask() {
        if(saveUserInfoTask == null) {
            saveUserInfoTask = new SaveUserInfoTask(allSettings());
        }
        return saveUserInfoTask;
    }

    public AlertService alertService() {
        if (alertService == null) {
            if(commonFtsObject() != null) {
                alertService = new AlertService(alertRepository(), commonFtsObject(), allCommonsRepositoryMap());
            }else {
                alertService = new AlertService(alertRepository());
            }
        }
        return alertService;
    }

    public ServiceProvidedService serviceProvidedService() {
        if (serviceProvidedService == null) {
            serviceProvidedService = new ServiceProvidedService(allServicesProvided());
        }
        return serviceProvidedService;
    }

    public EligibleCoupleService eligibleCoupleService() {
        if (eligibleCoupleService == null) {
            eligibleCoupleService = new EligibleCoupleService(allEligibleCouples(), allTimelineEvents(), allBeneficiaries());
        }
        return eligibleCoupleService;
    }

    public MotherService motherService() {
        if (motherService == null) {
            motherService = new MotherService(allBeneficiaries(), allEligibleCouples(), allTimelineEvents(), serviceProvidedService());
        }
        return motherService;
    }

    public ChildService childService() {
        if (childService == null) {
            childService = new ChildService(allBeneficiaries(), motherRepository(), childRepository(), allTimelineEvents(), serviceProvidedService(), allAlerts());
        }
        return childService;
    }

    public Session session() {
        if (session == null) {
            session = new Session();
        }
        return session;
    }

    public ANMService anmService() {
        if (anmService == null) {
            anmService = new ANMService(allSharedPreferences(), allBeneficiaries(), allEligibleCouples());
        }
        return anmService;
    }

    public Cache<String> listCache() {
        if (listCache == null) {
            listCache = new Cache<String>();
        }
        return listCache;
    }

    public Cache<SmartRegisterClients> smartRegisterClientsCache() {
        if (smartRegisterClientsCache == null) {
            smartRegisterClientsCache = new Cache<SmartRegisterClients>();
        }
        return smartRegisterClientsCache;
    }

    public Cache<HomeContext> homeContextCache() {
        if (homeContextCache == null) {
            homeContextCache = new Cache<HomeContext>();
        }
        return homeContextCache;
    }

    public Boolean IsUserLoggedOut() {
        return userService().hasSessionExpired();
    }

    public DristhiConfiguration configuration() {
        if (configuration == null) {
            configuration = new DristhiConfiguration(getInstance().applicationContext().getAssets());
        }
        return configuration;
    }

    public PendingFormSubmissionService pendingFormSubmissionService() {
        if (pendingFormSubmissionService == null) {
            pendingFormSubmissionService = new PendingFormSubmissionService(formDataRepository());
        }
        return pendingFormSubmissionService;
    }

    public ANMController anmController() {
        if (anmController == null) {
            anmController = new ANMController(anmService(), listCache(), homeContextCache());
        }
        return anmController;
    }

    public ANMLocationController anmLocationController() {
        if (anmLocationController == null) {
            anmLocationController = new ANMLocationController(allSettings(), listCache());
        }
        return anmLocationController;
    }

    //#TODO: Refactor to use one cache object
    public Cache<ECClients> ecClientsCache() {
        if (ecClientsCache == null) {
            ecClientsCache = new Cache<ECClients>();
        }
        return ecClientsCache;

    }

    //#TODO: Refactor to use one cache object
    public Cache<FPClients> fpClientsCache() {
        if (fpClientsCache == null) {
            fpClientsCache = new Cache<FPClients>();
        }
        return fpClientsCache;

    }

    //#TODO: Refactor to use one cache object

    public Cache<ANCClients> ancClientsCache() {
        if (ancClientsCache == null) {
            ancClientsCache = new Cache<ANCClients>();
        }
        return ancClientsCache;
    }

    public Cache<PNCClients> pncClientsCache() {
        if (pncClientsCache == null) {
            pncClientsCache = new Cache<PNCClients>();
        }
        return pncClientsCache;
    }

    public Cache<Villages> villagesCache() {
        if (villagesCache == null) {
            villagesCache = new Cache<Villages>();
        }
        return villagesCache;
    }

    public Cache<Typeface> typefaceCache() {
        if (typefaceCache == null) {
            typefaceCache = new Cache<Typeface>();
        }
        return typefaceCache;
    }

    public String getStringResource(int id) {
        return applicationContext().getResources().getString(id);
    }

    public int getColorResource(int id) {
        return applicationContext().getResources().getColor(id);
    }

    public Drawable getDrawable(int id) {
        return applicationContext().getResources().getDrawable(id);
    }

    public Drawable getDrawableResource(int id) {
        return applicationContext().getResources().getDrawable(id);
    }


    ///////////////////////////////// common methods ///////////////////////////////
    public Cache<CommonPersonObjectClients> personObjectClientsCache(){
        this.personObjectClientsCache = null;
        personObjectClientsCache = new Cache<CommonPersonObjectClients>();
        return personObjectClientsCache;
    }
    public AllCommonsRepository allCommonsRepositoryobjects(String tablename){
        initRepository();
        allCommonPersonObjectsRepository = new AllCommonsRepository(commonrepository(tablename),alertRepository(),timelineEventRepository());
        return allCommonPersonObjectsRepository;
    }

    private HashMap <String ,CommonRepository> MapOfCommonRepository;

    public long countofcommonrepositroy(String tablename){
        return commonrepository(tablename).count();
    }

    public CommonRepository commonrepository(String tablename){
        if(MapOfCommonRepository == null){
            MapOfCommonRepository = new HashMap<String, CommonRepository>();
        }
        if(MapOfCommonRepository.get(tablename) == null){
            for(CommonRepositoryInformationHolder bindType: bindtypes){
                if(bindType.getBindtypename().equalsIgnoreCase(tablename)){
                    if(commonFtsObject != null && commonFtsObject.containsTable(tablename)){
                        MapOfCommonRepository.put(bindType.getBindtypename(), new CommonRepository(commonFtsObject, bindType.getBindtypename(), bindType.getColumnNames()));
                        break;
                    } else {
                        MapOfCommonRepository.put(bindType.getBindtypename(), new CommonRepository(bindType.getBindtypename(), bindType.getColumnNames()));
                        break;
                    }
                }
            }

        }
        return  MapOfCommonRepository.get(tablename);
    }

    public void assignbindtypes(){
        bindtypes = new ArrayList<CommonRepositoryInformationHolder>();
        AssetManager assetManager = getInstance().applicationContext().getAssets();
        // create common repository definition for the ec models
        getEcBindtypes();
        try {
            String str = ReadFromfile("bindtypes.json",getInstance().applicationContext);
            JSONObject jsonObject = new JSONObject(str);
            JSONArray bindtypeObjects = jsonObject.getJSONArray("bindobjects");

            for(int i = 0 ;i<bindtypeObjects.length();i++){
                String bindname = bindtypeObjects.getJSONObject(i).getString("name");
                String [] columNames = new String[ bindtypeObjects.getJSONObject(i).getJSONArray("columns").length()];
                for(int j = 0 ; j < columNames.length;j++){
                  columNames[j] =  bindtypeObjects.getJSONObject(i).getJSONArray("columns").getJSONObject(j).getString("name");
                }
                bindtypes.add(new CommonRepositoryInformationHolder(bindname,columNames));
                Log.v("bind type logs",bindtypeObjects.getJSONObject(i).getString("name"));
            }
        } catch (Exception e) {
             Log.e(TAG, e.toString(), e);
        }
    }

    public void getEcBindtypes(){
        try {
            AssetManager assetManager = getInstance().applicationContext().getAssets();
            String str = ReadFromfile("ec_client_fields.json", getInstance().applicationContext);
            JSONObject jsonObject = new JSONObject(str);
            JSONArray bindtypeObjects = jsonObject.getJSONArray("bindobjects");

            for(int i = 0 ; i < bindtypeObjects.length(); i++){
                JSONObject columnDefinitionObject = bindtypeObjects.getJSONObject(i);
                String bindname = columnDefinitionObject.getString("name");
                JSONArray columnsJsonArray = columnDefinitionObject.getJSONArray("columns");
                String [] columnNames = new String[columnsJsonArray.length()];
                for(int j = 0 ; j < columnNames.length; j++){
                    JSONObject columnObject = columnsJsonArray.getJSONObject(j);
                    columnNames[j] =  columnObject.getString("column_name");
                }
                bindtypes.add(new CommonRepositoryInformationHolder(bindname, columnNames));
                Log.v("bind type logs", bindname);
            }
        }catch (Exception e){
            Log.e(TAG, e.toString(), e);
        }

    }

    public String ReadFromfile(String fileName, android.content.Context context) {
        StringBuilder returnString = new StringBuilder();
        InputStream fIn = null;
        InputStreamReader isr = null;
        BufferedReader input = null;
        try {
            fIn = context.getResources().getAssets()
                    .open(fileName, android.content.Context.MODE_WORLD_READABLE);
            isr = new InputStreamReader(fIn);
            input = new BufferedReader(isr);
            String line = "";
            while ((line = input.readLine()) != null) {
                returnString.append(line);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            try {
                if (isr != null)
                    isr.close();
                if (fIn != null)
                    fIn.close();
                if (input != null)
                    input.close();
            } catch (Exception e2) {
                e2.getMessage();
            }
        }
        return returnString.toString();
    }

    protected void setApplicationContext(android.content.Context applicationContext) {
        this.applicationContext = applicationContext;
    }

    protected Repository getRepository() {
        return repository;
    }

    protected void setRepository(Repository repository) {
        this.repository = repository;
    }

    public HTTPAgent getHttpAgent() {
        return httpAgent;
    }

    public Context updateCommonFtsObject(CommonFtsObject commonFtsObject){
        this.commonFtsObject = commonFtsObject;
        return this;
    }

    public Context updateRepository(Repository repository){
        this.repository = repository;
        return this;
    }

    public CommonFtsObject commonFtsObject() {
        return commonFtsObject;
    }

    /**
     * Linking generated concept with human readable values
     * @param customHumanReadableConceptResponse
     * @return
     */
    public Context updateCustomHumanReadableConceptResponse(Map<String, String> customHumanReadableConceptResponse) {
        this.customHumanReadableConceptResponse = customHumanReadableConceptResponse;
        return this;
    }

    public Map<String, String> customHumanReadableConceptResponse() {
        if (customHumanReadableConceptResponse == null) {
            return new HashMap<>();
        }
        return customHumanReadableConceptResponse;
    }

    public Map<String, AllCommonsRepository> allCommonsRepositoryMap() {
        Map<String, AllCommonsRepository> allCommonsRepositoryMap = new HashMap<String, AllCommonsRepository>();
        for (String ftsTable : commonFtsObject.getTables()) {
            AllCommonsRepository allCommonsRepository = allCommonsRepositoryobjects(ftsTable);
            allCommonsRepositoryMap.put(ftsTable, allCommonsRepository);
        }
        return allCommonsRepositoryMap;
    }

    public void setDetailsRepository(DetailsRepository _detailsRepository){
        detailsRepository = _detailsRepository;
    }
    ///////////////////////////////////////////////////////////////////////////////
}
