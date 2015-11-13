package com.segment.analytics.android.integrations.bugsnag;

import android.app.Activity;
import android.os.Bundle;
import com.bugsnag.android.Bugsnag;
import com.bugsnag.android.Client;
import com.segment.analytics.Analytics;
import com.segment.analytics.Traits;
import com.segment.analytics.ValueMap;
import com.segment.analytics.integrations.IdentifyPayload;
import com.segment.analytics.integrations.Integration;
import com.segment.analytics.integrations.ScreenPayload;
import com.segment.analytics.integrations.TrackPayload;
import java.util.Map;

/**
 * Bugsnag is an error tracking service for websites and mobile apps. It automatically captures any
 * errors in your code so that you can find them and resolve them as quickly as possible.
 *
 * @see <a href="https://bugsnag.com/">Bugsnag</a>
 * @see <a href="https://segment.com/docs/integrations/bugsnag/">Bugsnag Integration</a>
 * @see <a href="https://github.com/bugsnag/bugsnag-android">Bugsnag Android SDK</a>
 */
public class BugsnagIntegration extends Integration<Client> {
  public static final Factory FACTORY = new Factory() {
    @Override public Integration<?> create(ValueMap settings, Analytics analytics) {
      return new BugsnagIntegration(analytics, settings);
    }

    @Override public String key() {
      return BUGSNAG_KEY;
    }
  };
  private static final String BUGSNAG_KEY = "Bugsnag";

  BugsnagIntegration(Analytics analytics, ValueMap settings) {
    Bugsnag.init(analytics.getApplication(), settings.getString("apiKey"));
  }

  @Override public Client getUnderlyingInstance() {
    return Bugsnag.getClient();
  }

  @Override public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
    super.onActivityCreated(activity, savedInstanceState);
    Bugsnag.setContext(activity.getLocalClassName());
  }

  @Override public void identify(IdentifyPayload identify) {
    super.identify(identify);
    Traits traits = identify.traits();
    Bugsnag.setUser(traits.userId(), traits.email(), traits.name());
    final String userKey = "User";
    for (Map.Entry<String, Object> entry : traits.entrySet()) {
      Bugsnag.addToTab(userKey, entry.getKey(), entry.getValue());
    }
  }

  @Override public void screen(ScreenPayload screen) {
    super.screen(screen);
    Bugsnag.leaveBreadcrumb(String.format("Viewed %s Screen", screen.event()));
  }

  @Override public void track(TrackPayload track) {
    super.track(track);
    Bugsnag.leaveBreadcrumb(track.event());
  }
}
