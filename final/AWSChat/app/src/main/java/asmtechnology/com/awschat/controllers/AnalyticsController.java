package asmtechnology.com.awschat.controllers;

import android.content.Context;
import android.util.Log;

import com.amazonaws.mobileconnectors.amazonmobileanalytics.AnalyticsEvent;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.InitializationException;
import com.amazonaws.mobileconnectors.amazonmobileanalytics.MobileAnalyticsManager;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;

public class AnalyticsController {

    private String identityPoolD = "us-east-1:74034201-e8f6-454e-a7e0-52b428bff06b";
    private String appID = "994e34638d084c04860b5976da50b5c4";

    private MobileAnalyticsManager analytics = null;

    private Context mContext;

    private static AnalyticsController instance = null;
    private AnalyticsController() {}

    public static AnalyticsController getInstance(Context context) {
        if(instance == null) {
            instance = new AnalyticsController();
        }

        instance.mContext = context;

        try {
            instance.analytics = MobileAnalyticsManager.getOrCreateInstance(context,  instance.appID, instance.identityPoolD);
        } catch(InitializationException ex) {
            Log.d("AWSChat", "Failed to initialize Amazon Mobile Analytics");
        }

        return instance;
    }


    public void onPause() {
        if(analytics != null  && analytics.getSessionClient() != null) {
            analytics.getSessionClient().pauseSession();
            analytics.getEventClient().submitEvents();
        }
    }

    public void onResume() {
        if(analytics != null && analytics.getSessionClient() != null) {
            analytics.getSessionClient().resumeSession();
        }
    }

    public void postCustomEvent(String eventType, Map<String, String> eventAttributes) {

        if (analytics == null) {
            return;
        }

        AnalyticsEvent event =  analytics.getEventClient().createEvent(eventType);

        if (eventAttributes != null) {

            final Enumeration<String> keys = Collections.enumeration(eventAttributes.keySet());
            while (keys.hasMoreElements()) {
                String key = keys.nextElement();
                String value = eventAttributes.get(key);
                event.addAttribute(key, value);
            }
        }

        analytics.getEventClient().recordEvent(event);
    }

}
