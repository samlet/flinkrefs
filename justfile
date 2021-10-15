fraud:
	mvn compile exec:java -Dexec.mainClass="com.bluecc.refs.FraudDetectionJob"
run program:
    mvn compile exec:java -Dexec.mainClass="com.bluecc.refs.{{program}}"
