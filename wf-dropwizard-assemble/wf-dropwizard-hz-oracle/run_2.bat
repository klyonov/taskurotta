start java -Dcom.sun.management.jmxremote.port=9998 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false -DassetsMode=dev -Dts.node.custom.name="node2" -Ddw.http.port=8821 -Ddw.http.adminPort=8822 -Ddw.logging.file.currentLogFilename="./target/logs/service2.log" -jar target/wf-dropwizard-hz-oracle-0.2.0-SNAPSHOT.jar server src/main/resources/conf.yml