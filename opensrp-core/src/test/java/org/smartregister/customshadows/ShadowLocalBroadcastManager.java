package org.smartregister.customshadows;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.robolectric.Robolectric;
import androidx.test.core.app.ApplicationProvider;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.Provider;
import org.robolectric.shadows.ShadowApplication;
import org.robolectric.util.ReflectionHelpers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Implements(LocalBroadcastManager.class)
public class ShadowLocalBroadcastManager {
    private final List<Intent> sentBroadcastIntents = new ArrayList<>();
    private final List<ShadowLocalBroadcastManager.Wrapper> registeredReceivers = new ArrayList<>();

    @Implementation
    public static LocalBroadcastManager getInstance(final Context context) {
        return ShadowApplication.getInstance().getSingleton(LocalBroadcastManager.class, new Provider<LocalBroadcastManager>() {
            @Override
            public LocalBroadcastManager get() {
                return ReflectionHelpers.callConstructor(LocalBroadcastManager.class, ReflectionHelpers.ClassParameter.from(Context.class, context));
            }
        });
    }

    @Implementation
    public void registerReceiver(BroadcastReceiver receiver, IntentFilter filter) {
        registeredReceivers.add(new ShadowLocalBroadcastManager.Wrapper(receiver, filter));
    }

    @Implementation
    public void unregisterReceiver(BroadcastReceiver receiver) {
        Iterator<ShadowLocalBroadcastManager.Wrapper> iterator = registeredReceivers.iterator();
        while (iterator.hasNext()) {
            ShadowLocalBroadcastManager.Wrapper wrapper = iterator.next();
            if (wrapper.broadcastReceiver == receiver) {
                iterator.remove();
            }
        }
    }

    @Implementation
    public boolean sendBroadcast(Intent intent) {
        boolean sent = false;
        sentBroadcastIntents.add(intent);
        List<ShadowLocalBroadcastManager.Wrapper> copy = new ArrayList<>();
        copy.addAll(registeredReceivers);
        for (ShadowLocalBroadcastManager.Wrapper wrapper : copy) {
            if (wrapper.intentFilter.matchAction(intent.getAction())) {
                final int match = wrapper.intentFilter.matchData(intent.getType(), intent.getScheme(), intent.getData());
                if (match != IntentFilter.NO_MATCH_DATA && match != IntentFilter.NO_MATCH_TYPE) {
                    sent = true;
                    final BroadcastReceiver receiver = wrapper.broadcastReceiver;
                    final Intent broadcastIntent = intent;
                    Robolectric.getForegroundThreadScheduler().post(new Runnable() {
                        @Override
                        public void run() {
                            receiver.onReceive(ApplicationProvider.getApplicationContext(), broadcastIntent);
                        }
                    });
                }
            }
        }
        return sent;
    }

    public List<Intent> getSentBroadcastIntents() {
        return sentBroadcastIntents;
    }

    public List<ShadowLocalBroadcastManager.Wrapper> getRegisteredBroadcastReceivers() {
        return registeredReceivers;
    }

    public static class Wrapper {
        public final BroadcastReceiver broadcastReceiver;
        public final IntentFilter intentFilter;

        public Wrapper(BroadcastReceiver broadcastReceiver, IntentFilter intentFilter) {
            this.broadcastReceiver = broadcastReceiver;
            this.intentFilter = intentFilter;
        }
    }
}
