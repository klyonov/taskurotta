start java -Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=5005 -jar target/wf-dropwizard-oracle-0.2.0-SNAPSHOT.jar server src/main/resources/conf.yaml