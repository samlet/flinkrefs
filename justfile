base:
    foreman start -f Procfile_base
srv:
    foreman start -f Procfile_services
tool:
    foreman start -f Procfile_tool
clickhouse:
    cd ~/workspace/olap/clickhouse && sudo ./clickhouse server
clickhouse-cli:
    ~/workspace/olap/clickhouse/clickhouse client -m
maxwell:
	maxwell --user='maxwell' --password='XXXXXX' --host='127.0.0.1' --producer=stdout
maxwell-kafka:
    maxwell --user='maxwell' --password='XXXXXX' --host='127.0.0.1' --producer=kafka --kafka.bootstrap.servers=localhost:9092 --kafka_topic=maxwell
maxwell-gmall:
    maxwell --user='maxwell' --password='XXXXXX' --host='127.0.0.1' --producer=kafka --kafka.bootstrap.servers=localhost:9092 --kafka_topic=ods_base_db_m

# $ just maxwell-boot base_province
maxwell-boot table:
    maxwell-bootstrap --user='maxwell' --password='XXXXXX' --host='127.0.0.1' --database=gmall2020 --table={{table}}
maxwell-ofbiz table:
    maxwell-bootstrap --user='maxwell' --password='XXXXXX' --host='127.0.0.1' --database=ofbiz --table={{table}}

# https://serverfault.com/questions/62411/how-can-i-sort-du-h-output-by-size
# sort allows a -h parameter, which allows numeric suffixes of the kind produced by du -h
kafka-data:
    du -hs /usr/local/var/lib/kafka-logs/* | sort -h

cassandra:
    cassandra -f
kafka:
    kafka-server-start /usr/local/etc/kafka/server.properties
es:
    elasticsearch
kibana:
    kibana

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

# pref delete.topic.enable=true
delete topic:
    kafka-topics --zookeeper localhost:2181 \
                    --topic {{topic}} \
                    --delete

socket:
    nc -lk 7777

fraud:
	mvn compile exec:java -Dexec.mainClass="com.bluecc.refs.fraud.FraudDetectionJob"
run program +FLAGS='':
    mvn compile exec:java -Dexec.mainClass="com.bluecc.refs.{{program}}" -Dexec.args="{{FLAGS}}"
ft program +FLAGS='':
    mvn compile exec:java -Dexec.mainClass="com.bluecc.fixtures.{{program}}" -Dexec.args="{{FLAGS}}"

mysqlsh:
	mysqlsh mysql://root:root@localhost:3306

