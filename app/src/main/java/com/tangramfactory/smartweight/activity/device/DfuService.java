package com.tangramfactory.smartweight.activity.device;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import no.nordicsemi.android.dfu.DfuBaseService;

public class DfuService extends DfuBaseService {
    @Override
    protected Class<? extends Activity> getNotificationTarget() {
        return NotificationActivity.class;
    }
}
