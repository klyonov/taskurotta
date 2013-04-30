package ru.taskurotta.backend.ora.checkpoint;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.taskurotta.backend.checkpoint.CheckpointService;
import ru.taskurotta.backend.checkpoint.TimeoutType;
import ru.taskurotta.backend.checkpoint.model.Checkpoint;
import ru.taskurotta.backend.checkpoint.model.CheckpointQuery;
import ru.taskurotta.backend.ora.tools.SqlParam;
import ru.taskurotta.exception.BackendCriticalException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import static ru.taskurotta.backend.ora.tools.SqlParamHelper.createPreparedStatementWithSqlParams;


/**
 * User: greg
 */
public class OracleCheckpointService implements CheckpointService {

    private static final Logger logger = LoggerFactory.getLogger(OracleCheckpointService.class);

    private DataSource dataSource;

    public OracleCheckpointService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void addCheckpoint(Checkpoint checkpoint) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("insert into TR_CHECKPOINTS(ENTITY_ID, ENTITY_TYPE, TYPE_TIMEOUT,CHECKPOINT_TIME) values (?,?,?,?)")
        ) {
            ps.setString(1, checkpoint.getEntityGuid().toString());
            ps.setString(2, checkpoint.getEntityType());
            ps.setString(3, checkpoint.getTimeoutType().toString());
            ps.setLong(4, checkpoint.getTime());
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.error("Database error", ex);
            throw new BackendCriticalException("Database error", ex);
        }
    }

    @Override
    public void addCheckpoints(TimeoutType timeoutType, List<Checkpoint> checkpoints) {
        for (Checkpoint checkpoint : checkpoints) {
            checkpoint.setTimeoutType(timeoutType);
            addCheckpoint(checkpoint);
        }

    }

    @Override
    public void removeCheckpoint(Checkpoint checkpoint) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement ps = connection.prepareStatement("delete from TR_CHECKPOINTS where ENTITY_ID = ? and TYPE_TIMEOUT = ?")
        ) {
            ps.setString(1, checkpoint.getEntityGuid().toString());
            ps.setString(2, checkpoint.getTimeoutType().toString());
            ps.executeUpdate();
        } catch (SQLException ex) {
            logger.error("Database error", ex);
            throw new BackendCriticalException("Database error", ex);
        }
    }

    @Override
    public void removeCheckpoints(TimeoutType timeoutType, List<Checkpoint> checkpoint) {
        for (Checkpoint check : checkpoint) {
            removeCheckpoint(check);
        }
    }

    @Override
    public List<Checkpoint> listCheckpoints(CheckpointQuery command) {
        if (command != null && command.getTimeoutType() != null) {
            final List<SqlParam> sqlParams = Lists.newArrayList();
            final List<Checkpoint> checkpoints = Lists.newArrayList();
            final String query = "select * from TR_CHECKPOINTS where TYPE_TIMEOUT=?";
            final StringBuilder stringBuilder = new StringBuilder(query);
            sqlParams.add(new SqlParam(1, command.getTimeoutType().toString()));
            int idx = 2;
            if (command.getMinTime() > 0) {
                sqlParams.add(new SqlParam(idx, command.getMinTime()));
                stringBuilder.append(" and CHECKPOINT_TIME > ?");
                idx++;
            }
            if (command.getMaxTime() > 0) {
                sqlParams.add(new SqlParam(idx, command.getMaxTime()));
                stringBuilder.append(" and CHECKPOINT_TIME < ?");
                idx++;
            }
            if (command.getEntityType() != null) {
                sqlParams.add(new SqlParam(idx, command.getEntityType()));
                stringBuilder.append(" and ENTITY_TYPE = ?");
            }
            try(Connection connection = dataSource.getConnection();
                PreparedStatement ps = createPreparedStatementWithSqlParams(connection, sqlParams, stringBuilder.toString())
            ) {
                final ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    final Checkpoint checkpoint = new Checkpoint();
                    checkpoint.setEntityGuid(UUID.fromString(rs.getString("ENTITY_ID")));
                    checkpoint.setEntityType(rs.getString("ENTITY_TYPE"));
                    checkpoint.setTimeoutType(TimeoutType.forValue(rs.getString("TYPE_TIMEOUT")));
                    checkpoint.setTime(rs.getLong("CHECKPOINT_TIME"));
                    checkpoints.add(checkpoint);
                }
                return checkpoints;
            } catch (SQLException ex) {
                logger.error("Database error", ex);
                throw new BackendCriticalException("Database error", ex);
            }
        }
        return null;
    }

    @Override
    public int removeEntityCheckpoints(UUID uuid, TimeoutType timeoutType) {
        try(Connection connection = dataSource.getConnection();
            PreparedStatement ps = connection.prepareStatement("delete from TR_CHECKPOINTS where ENTITY_ID = ? and TYPE_TIMEOUT=?")) {
            ps.setString(1, uuid.toString());
            ps.setString(2, timeoutType.toString());
            return ps.executeUpdate();
        } catch (SQLException ex) {
            logger.error("Database error", ex);
            throw new BackendCriticalException("Database error", ex);
        }
    }

}
