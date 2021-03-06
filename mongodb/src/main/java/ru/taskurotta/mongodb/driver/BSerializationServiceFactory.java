package ru.taskurotta.mongodb.driver;

import ru.taskurotta.mongodb.driver.impl.BSerializationServiceImpl;

/**
 */
public class BSerializationServiceFactory {

    public static final BSerializationService newInstance(StreamBSerializer... streamBSerializers) {
        return new BSerializationServiceImpl(streamBSerializers, null);
    }

    public static final BSerializationService newInstance(BSerializationService childService, StreamBSerializer...
            streamBSerializers) {
        return new BSerializationServiceImpl(streamBSerializers, childService);
    }
}
