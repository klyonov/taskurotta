start java -Xmx128m -Xms128m -Dcom.sun.management.jmxremote.port=9995 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -jar ../../../../../target/assemble-0.8.0-SNAPSHOT.jar server hz-mongo-node1.yml