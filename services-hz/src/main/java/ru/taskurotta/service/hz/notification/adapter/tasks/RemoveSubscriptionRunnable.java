package ru.taskurotta.service.hz.notification.adapter.tasks;

import ru.taskurotta.service.hz.notification.adapter.HzNotificationDaoAdapter;

import java.io.Serializable;

/**
 * Created on 15.06.2015.
 */
public class RemoveSubscriptionRunnable implements Runnable, Serializable {

    private long id;

    public RemoveSubscriptionRunnable(long id) {
        this.id = id;
    }

    @Override
    public void run() {
        HzNotificationDaoAdapter.getRealNotificationsDao().removeSubscription(id);
    }
}
