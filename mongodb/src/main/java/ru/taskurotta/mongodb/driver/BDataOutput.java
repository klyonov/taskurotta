package ru.taskurotta.mongodb.driver;

import java.util.Date;
import java.util.UUID;

/**
 */
public interface BDataOutput {

    public void writeString(CString name, String value);

    public void writeUUID(CString name, UUID value);

    public void writeLong(CString name, long value);

    public void writeDate(CString name, Date value);

    public int writeObject(CString name);

    public void writeObjectStop(int label);

}