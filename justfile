base:
    foreman start -f Procfile_base
srv:
    foreman start -f Procfile_services
tool:
    foreman start -f Procfile_tool
clickhouse:
    cd ~/workspace/olap/clickhouse && sudo ./clickhouse server
clickhouse-cli:
    cd ~/workspace/olap/clickhouse && ./clickhouse client -m
maxwell:
	maxwell --user='maxwell' --password='XXXXXX' --host='127.0.0.1' --producer=stdout
maxwell-kafka:
    maxwell --user='maxwell' --password='XXXXXX' --host='127.0.0.1' --producer=kafka --kafka.bootstrap.servers=localhost:9092 --kafka_topic=maxwell
cassandra:
    cassandra -f
kafka:
    kafka-server-start /usr/local/etc/kafka/server.properties
flink:
    ~/workspace/flink/flink-1.13.2/bin/start-cluster.sh
stop-flink:
    ~/workspace/flink/flink-1.13.2/bin/stop-cluster.sh
sql-client:
    ~/workspace/flink/flink-1.13.2/bin/sql-client.sh
restart-flink:
    ~/workspace/flink/flink-1.13.2/bin/stop-cluster.sh
    ~/workspace/flink/flink-1.13.2/bin/start-cluster.sh
    ~/workspace/flink/flink-1.13.2/bin/sql-client.sh

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

socket:
    nc -lk 7777

fraud:
	mvn compile exec:java -Dexec.mainClass="com.bluecc.refs.FraudDetectionJob"
run program +FLAGS='':
    mvn compile exec:java -Dexec.mainClass="com.bluecc.refs.{{program}}" -Dexec.args="{{FLAGS}}"
ft program +FLAGS='':
    mvn compile exec:java -Dexec.mainClass="com.bluecc.fixtures.{{program}}" -Dexec.args="{{FLAGS}}"

mysqlsh:
	mysqlsh mysql://root:root@localhost:3306

