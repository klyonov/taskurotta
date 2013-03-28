package ru.taskurotta.internal.proxy;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import ru.taskurotta.TaskHandler;
import ru.taskurotta.annotation.NoWait;
import ru.taskurotta.annotation.Wait;
import ru.taskurotta.core.ArgType;

/**
 * User: romario
 * Date: 2/5/13
 * Time: 3:37 PM
 */
abstract public class CachedProxyFactory implements ProxyFactory {


	private Map<Class, Object> clientToProxy = new HashMap<Class, Object>();

	abstract public <TargetInterface> Object createProxy(Class<TargetInterface> proxyType, TaskHandler taskHandler);

	@Override
	public <TargetInterface> TargetInterface create(Class<TargetInterface> targetInterface, TaskHandler taskHandler) {

		// should be not cached
		if (taskHandler != null) {
			return (TargetInterface) createProxy(targetInterface, taskHandler);
		}

		Object proxyClient = clientToProxy.get(targetInterface);

		if (proxyClient == null) {
			proxyClient = createProxy(targetInterface, null);

			clientToProxy.put(targetInterface, proxyClient);
		}

		return (TargetInterface) proxyClient;
	}

	protected ArgType[] getArgTypes(Method method) {
		Annotation[][] parametersAnnotations = method.getParameterAnnotations();
		ArgType[] result = new ArgType[parametersAnnotations.length];
		boolean annotationFound = false;

		for (int i = 0; i < parametersAnnotations.length; i++) {
			Annotation[] parameterAnnotations = parametersAnnotations[i];
			for (Annotation annotation : parameterAnnotations) {
				if (annotation instanceof NoWait) {
					result[i] = ArgType.NO_WAIT;
					annotationFound = true;
				} else if (annotation instanceof Wait) {
					result[i] = ArgType.WAIT;
					annotationFound = true;
				}
			}
		}

		return annotationFound ? result : null;
	}

}
