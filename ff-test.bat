start java -Xmx256m -Xms256m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=target -server -cp assemble/target/assemble-0.10.3-SNAPSHOT.jar;assemble/src/main/resources/default.properties -Dhazelcast.health.monitoring.level=OFF ru.taskurotta.bootstrap.Main -f assemble/src/main/resources/tests/ff-hz-mongo-quantity.yml