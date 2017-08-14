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

    public static String getAppDir() {
        File appDir = DrishtiApplication.getInstance().getApplicationContext()
                .getDir("opensrp", android.content.Context.MODE_PRIVATE); //Creating an internal
        // dir;
        return appDir.getAbsolutePath();
    }

    public static OpenSRPImageLoader getCachedImageLoaderInstance() {
        if (cachedImageLoader == null) {
            cachedImageLoader = new OpenSRPImageLoader(
                    DrishtiApplication.getInstance().getApplicationContext(),
                    R.drawable.woman_placeholder).setFadeInImage((Build.VERSION.SDK_INT >= 12));
        }

        return cachedImageLoader;
    }

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

    public abstract void logoutCurrentUser();

    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

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