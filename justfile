base:
    foreman start -f Procfile_base
srv:
    foreman start -f Procfile_services

topics:
    kafka-topics --list --bootstrap-server localhost:9092
desc topic:
    kafka-topics --zookeeper localhost:2181 --topic {{topic}} --describe
create topic:
    kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic {{topic}}
list topic:
    kafka-console-consumer --bootstrap-server localhost:9092 --topic {{topic}} --from-beginning
consume topic:
    kafka-console-consumer --bootstrap-server localhost:9092 --topic {{topic}}
produce topic:
    kafka-console-producer --broker-list localhost:9092 --topic {{topic}}

fraud:
	mvn compile exec:java -Dexec.mainClass="com.bluecc.refs.FraudDetectionJob"
run program:
    mvn compile exec:java -Dexec.mainClass="com.bluecc.refs.{{program}}"
