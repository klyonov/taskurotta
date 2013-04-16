package ru.taskurotta.backend.config;

import ru.taskurotta.backend.config.model.ActorPreferences;
import ru.taskurotta.backend.config.model.ExpirationPolicyConfig;

/**
 * User: romario
 * Date: 4/1/13
 * Time: 12:08 PM
 */
public interface ConfigBackend {

    public boolean isActorBlocked(String actorId);

    public ActorPreferences[] getActorPreferences();

    public ExpirationPolicyConfig[] getExpirationPolicies();

}
