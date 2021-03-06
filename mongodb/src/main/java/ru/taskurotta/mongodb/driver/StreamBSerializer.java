package ru.taskurotta.mongodb.driver;

/**
 */
public interface StreamBSerializer<T> {

    public static final CString _ID = new CString("_id");

    Class<T> getObjectClass();

    void write(BDataOutput out, T object);

    T read(BDataInput in);
}
