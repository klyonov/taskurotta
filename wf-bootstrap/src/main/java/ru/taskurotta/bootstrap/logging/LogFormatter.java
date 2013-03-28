package ru.taskurotta.bootstrap.logging;

// TODO: 10/12/11 <coda> -- test LogFormatter
// TODO: 10/12/11 <coda> -- document LogFormatter

import java.util.TimeZone;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;

public class LogFormatter extends PatternLayout {
	public LogFormatter(LoggerContext context, TimeZone timeZone) {
		super();
		setOutputPatternAsHeader(false);
		getDefaultConverterMap().put("ex", PrefixedThrowableProxyConverter.class.getName());
		getDefaultConverterMap().put("xEx", PrefixedExtendedThrowableProxyConverter.class.getName());
		setPattern("%-5p [%d{ISO8601," + timeZone.getID() + "}] %c: %m%n%xEx");
		setContext(context);
	}
}
