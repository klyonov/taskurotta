package ru.taskurotta.bootstrap.config;

import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

import ru.taskurotta.bootstrap.profiler.Profiler;

/**
 * User: stukushin
 * Date: 26.03.13
 * Time: 12:49
 */
public class DefaultProfilerConfig implements ProfilerConfig {

	private String className;
	private Properties properties;

	@Override
	public Profiler getProfiler(Class actorInterface) {
		Profiler profiler = null;

		try {
			profiler = (Profiler) Class.forName(className).getConstructor(Class.class, Properties.class).newInstance(actorInterface, properties);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return profiler;
	}

	public void setClass(String className) {
		this.className = className;
	}

	public void setProperties(Properties properties) {
		this.properties = properties;
	}
}