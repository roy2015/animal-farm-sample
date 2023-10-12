使用需知：
1. http://localhost:8083/api/sendMsg?topic=topic2023&partition=0&key=1&message=roy222
   传入参数 topic， partition， key， message
2. 如果连不上kafka，记得把container Id加入host


bin/kafka-topics.sh --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 3 --topic topic2023

bin/kafka-console-consumer.sh --bootstrap-server localhost:9092   --topic topic2023 --from-beginning

bin/kafka-topics.sh --describe --bootstrap-server localhost:9092 --topic topic2023