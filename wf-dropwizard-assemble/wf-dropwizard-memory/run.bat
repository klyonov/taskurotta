start java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar target/wf-dropwizard-memory-0.2.0-SNAPSHOT.jar server target/classes/TaskQueueConfig.yml