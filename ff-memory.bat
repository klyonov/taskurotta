java -Xmx64m -Xms64m -Dcom.sun.management.jmxremote.port=9999 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=dump -server -cp assemble/target/assemble-0.7.0-SNAPSHOT.jar;assemble/src/main/resources/default.properties ru.taskurotta.bootstrap.Main -f assemble/src/main/resources/tests/stress/mem/mem-ff.yml