start java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar target/wf-dropwizard-memory-0.1.0.jar server target/classes/TaskQueueConfig.yml