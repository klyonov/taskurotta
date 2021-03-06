package ru.taskurotta.bootstrap.profiler.impl;

import ru.taskurotta.bootstrap.config.ProfilerConfig;
import ru.taskurotta.bootstrap.profiler.Profiler;

public class RollingLoggingProfilerConfig implements ProfilerConfig {

	private RollingLoggingProfiler singleton;
	private long logPeriod = -1l;
	private boolean isSingleton = true;

	public boolean isSingleton() {
		return isSingleton;
	}

	public void setSingleton(boolean isSingleton) {
		this.isSingleton = isSingleton;
	}

	@Override
	public Profiler getProfiler(Class actorInterface) {
		RollingLoggingProfiler result = null;
		if (isSingleton) {
		    synchronized (this) {
	            if (singleton == null) {
	                singleton = new RollingLoggingProfiler(getLogPeriod());
	            }
	            result = singleton;
            }
		} else {
			result = new RollingLoggingProfiler(getLogPeriod());
			result.setName(actorInterface.getSimpleName());
		}
		return result;
	}

	public void setLogPeriod(long logPeriod) {
		this.logPeriod = logPeriod;
	}

	public long getLogPeriod() {
		return logPeriod;
	}

}
