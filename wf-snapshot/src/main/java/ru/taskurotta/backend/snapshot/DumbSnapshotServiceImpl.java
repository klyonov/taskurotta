package ru.taskurotta.backend.snapshot;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: greg
 * Dumb snapshot service for solution without snapshot service
 */
public class DumbSnapshotServiceImpl implements SnapshotService {
    private final static Logger logger = LoggerFactory.getLogger(DumbSnapshotServiceImpl.class);

    @Override
    public void createSnapshot(UUID processID) {
        logger.debug("Dumb snapshot created");
    }

    @Override
    public Snapshot getSnapshot(UUID snapshotId) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public List<Snapshot> getSnapshotByProcessId(UUID snapshotId) {
        throw new java.lang.UnsupportedOperationException();
    }

    @Override
    public void saveSnapshot(Snapshot snapshot) {
        throw new java.lang.UnsupportedOperationException();
    }
}
