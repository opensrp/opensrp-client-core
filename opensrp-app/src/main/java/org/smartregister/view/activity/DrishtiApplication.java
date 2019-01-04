package org.smartregister.view.activity;

import android.app.Application;
import android.os.Build;
import android.support.multidex.MultiDex;

import net.sqlcipher.database.SQLiteDatabase;

import org.smartregister.AllConstants;
import org.smartregister.Context;
import org.smartregister.CoreLibrary;
import org.smartregister.R;
import org.smartregister.repository.DrishtiRepository;
import org.smartregister.repository.Repository;
import org.smartregister.util.BitmapImageCache;
import org.smartregister.util.OpenSRPImageLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import static org.smartregister.util.Log.logError;

/**
 * <p>This class initializes all the global parameters for the Application</p>
 * <p>It provides the following :
 *  <ul>
 *      <li><b>A Repository Object :</b> For local access to the application central database <i>{@link #getRepository()}</i></li>
 *      <li><b>An Image Cache :</b> A volley based image cache to save network resources <i>{@link #getMemoryCacheInstance()}</i></li>
 *      <li><b>An Image Loader :</b> For image retrieval  <i>{@link #getCachedImageLoaderInstance()}</i></li>
 *      <li>Access to OpenSRP custom context</li>
 *      <li>Access the application object in a thread safe way</li>
 *  </ul>
 * </p>
 *
 * @author OpenSRPLegends
 * @version 0.1
 * @since 2018-01-01
 */
public abstract class DrishtiApplication extends Application {
    private static final String TAG = "DrishtiApplication";
    protected static DrishtiApplication mInstance;
    private static BitmapImageCache memoryImageCache;
    private static OpenSRPImageLoader cachedImageLoader;
    protected Locale locale = null;
    protected Context context;
    protected Repository repository;
    private String password;

    public static synchronized DrishtiApplication getInstance() {
        return mInstance;
    }

    public static BitmapImageCache getMemoryCacheInstance() {
        if (memoryImageCache == null) {
            memoryImageCache = new BitmapImageCache(BitmapImageCache
                    .calculateMemCacheSize(AllConstants.ImageCache.MEM_CACHE_PERCENT));
        }

        return memoryImageCache;
    }

    /**
     * @return default app directory
     */
    public static String getAppDir() {
        File appDir = DrishtiApplication.getInstance().getApplicationContext()
                .getDir("opensrp", android.content.Context.MODE_PRIVATE); //Creating an internal
        // dir;
        return appDir.getAbsolutePath();
    }

    /**
     * Gives users a singleton OpenSRPImageLoader
     * @return OpenSRPImageLoader
     */
    public static OpenSRPImageLoader getCachedImageLoaderInstance() {
        if (cachedImageLoader == null) {
            cachedImageLoader = new OpenSRPImageLoader(
                    DrishtiApplication.getInstance().getApplicationContext(),
                    R.drawable.woman_placeholder).setFadeInImage((Build.VERSION.SDK_INT >= 12));
        }

        return cachedImageLoader;
    }

    /**
     * Initializes the database
     */
    @Override
    public void onCreate() {
        try {
            super.onCreate();
            mInstance = this;
            SQLiteDatabase.loadLibs(this);
        } catch (UnsatisfiedLinkError e) {
            logError("Error on onCreate: " + e);
        }
    }

    /**
     * Implementation of this method to logout the current and end session
     */
    public abstract void logoutCurrentUser();

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    /**
     *
     * @return a @see org.smartregister.repository.Repository
     */
    public Repository getRepository() {
        ArrayList<DrishtiRepository> drishtireposotorylist = CoreLibrary.getInstance().context()
                .sharedRepositories();
        DrishtiRepository[] drishtireposotoryarray = drishtireposotorylist
                .toArray(new DrishtiRepository[drishtireposotorylist.size()]);
        if (repository == null) {
            repository = new Repository(getInstance().getApplicationContext(), null,
                    drishtireposotoryarray);
        }
        return repository;
    }

    public String getPassword() {
        if (password == null) {
            String username = context.userService().getAllSharedPreferences().fetchRegisteredANM();
            password = context.userService().getGroupId(username);
        }
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}