# 目次

- 環境設定
- 検証







# １．環境設定

### 1.1 事前準備

　ThirdPartyのKafkaConnectを入れているため、自作のDockerImageを事前に用意が必要。

```bash
cd .\make-enviroment\docker-build

docker build -t rgs/kafka-connect:1.0.0  -f dockerfile-kafka-connect-2.txt .
```

### 1.2 DockerComposeの起動

```
cd .\make-enviroment\docker-compose

 docker-compose ps
        Name                     Command                State                          Ports
---------------------------------------------------------------------------------------------------------------
broker1                 /etc/confluent/docker/run   Up               0.0.0.0:9091->9092/tcp
broker2                 /etc/confluent/docker/run   Up               0.0.0.0:9092->9092/tcp
broker3                 /etc/confluent/docker/run   Up               0.0.0.0:9093->9092/tcp
kafka-connect           /etc/confluent/docker/run   Up               80/tcp, 0.0.0.0:8083->8083/tcp, 9092/tcp
kafka-connect-ui        /run.sh                     Up               0.0.0.0:9001->8000/tcp
kafka-manager           ./start-kafka-manager.sh    Up               0.0.0.0:9000->9000/tcp
kafka-schema-registry   /etc/confluent/docker/run   Up               0.0.0.0:8081->8081/tcp
zookeeper               /etc/confluent/docker/run   Up               0.0.0.0:2181->2181/tcp, 2888/tcp, 3888/tcp

```

### 1.3 環境確認

- Kafka-connect確認

![kafka-connect](.\image\kafka-connect.png)

- 色々なConnectが追加されていること

![kafka-connect-new](.\image\kafka-connect-new.png)

- Topic情報の確認

![](.\image\kafka-manage.png)

- 作成したClusterを追加する

![kafka-manage-add-cluster](.\image\kafka-manage-add-cluster.png)

- 追加したClusterの情を確認できること

![kafka-manage-cluster-info](.\image\kafka-manage-cluster-info.png)





# ２．検証

### 2.1 基礎

- Topict追加

  ```bash
  [root@dell-master cp-all-in-one]# docker exec -it broker1 kafka-topics --list --zookeeper zookeeper
  __consumer_offsets
  _confluent-license
  _confluent-metrics
  _confluent-telemetry-metrics
  _confluent_balancer_api_state
  _confluent_balancer_broker_samples
  _confluent_balancer_partition_samples
  _schemas
  docker-connect-configs
  docker-connect-offsets
  docker-connect-status
  
  # docker exec -it broker1 kafka-topics --zookeeper zookeeper --create --replication-factor 1 --partitions 1 --topic sample1-1
  # docker exec -it broker1 kafka-topics --zookeeper zookeeper --create --replication-factor 2 --partitions 1 --topic sample2-1
  # docker exec -it broker1 kafka-topics --zookeeper zookeeper --create --replication-factor 1 --partitions 2 --topic sample1-2
  # docker exec -it broker1 kafka-topics --zookeeper zookeeper --create --replication-factor 2 --partitions 2 --topic sample2-2
  # docker exec -it broker1 kafka-topics --zookeeper zookeeper --create --replication-factor 3 --partitions 5 --topic sample3-5
  
  # docker exec -it broker1 kafka-topics --zookeeper zookeeper --create --replication-factor 4 --partitions 5 --topic sample4-5
  Error while executing topic command : Replication factor: 4 larger than available brokers: 3.
  ERROR org.apache.kafka.common.errors.InvalidReplicationFactorException: Replication factor: 4 larger than available brokers: 3.
  
  ## 正常作成時の状態
  # docker exec -it broker1 kafka-topics --list --zookeeper zookeeper
  __consumer_offsets
  _confluent-license
  _confluent-metrics
  _confluent-telemetry-metrics
  _confluent_balancer_api_state
  _confluent_balancer_broker_samples
  _confluent_balancer_partition_samples
  _schemas
  docker-connect-configs
  docker-connect-offsets
  docker-connect-status
  sample1-1
  sample1-2
  sample2-1
  sample2-2
  sample3-5
  
  ## 各Topicの詳細情報（Partitionがどこにいる、どれはLeader、コピーはどこいる）
  # docker exec -it broker1 kafka-topics --describe --zookeeper zookeeper
  Topic: sample1-1        PartitionCount: 1       ReplicationFactor: 1    Configs:
          Topic: sample1-1        Partition: 0    Leader: 2       Replicas: 2     Isr: 2  Offline:
  Topic: sample1-2        PartitionCount: 2       ReplicationFactor: 1    Configs:
          Topic: sample1-2        Partition: 0    Leader: 2       Replicas: 2     Isr: 2  Offline:
          Topic: sample1-2        Partition: 1    Leader: 3       Replicas: 3     Isr: 3  Offline:
  Topic: sample2-1        PartitionCount: 1       ReplicationFactor: 2    Configs:
          Topic: sample2-1        Partition: 0    Leader: 2       Replicas: 2,3   Isr: 2,3        Offline:
  Topic: sample2-2        PartitionCount: 2       ReplicationFactor: 2    Configs:
          Topic: sample2-2        Partition: 0    Leader: 3       Replicas: 3,2   Isr: 3,2        Offline:
          Topic: sample2-2        Partition: 1    Leader: 1       Replicas: 1,3   Isr: 1,3        Offline:
  Topic: sample3-5        PartitionCount: 5       ReplicationFactor: 3    Configs:
          Topic: sample3-5        Partition: 0    Leader: 1       Replicas: 1,3,2 Isr: 1,3,2      Offline:
          Topic: sample3-5        Partition: 1    Leader: 2       Replicas: 2,1,3 Isr: 2,1,3      Offline:
          Topic: sample3-5        Partition: 2    Leader: 3       Replicas: 3,2,1 Isr: 3,2,1      Offline:
          Topic: sample3-5        Partition: 3    Leader: 1       Replicas: 1,2,3 Isr: 1,2,3      Offline:
          Topic: sample3-5        Partition: 4    Leader: 2       Replicas: 2,3,1 Isr: 2,3,1      Offline:
  ```

- Producerの作成

  ```
  docker exec -it broker1 kafka-console-producer --topic=sample3-5 --bootstrap-server=broker1:29091
  ```

  

- Consumerの作成

  ```bash
  docker exec -it broker1 kafka-console-consumer --bootstrap-server=broker1:29091 --topic=sample3-5 --partition=0
  docker exec -it broker1 kafka-console-consumer --bootstrap-server=broker1:29091 --topic=sample3-5 --partition=1
  docker exec -it broker1 kafka-console-consumer --bootstrap-server=broker2:29092 --topic=sample3-5 --partition=2
  docker exec -it broker1 kafka-console-consumer --bootstrap-server=broker3:29093 --topic=sample3-5 --partition=3
  docker exec -it broker1 kafka-console-consumer --bootstrap-server=broker3:29093 --topic=sample3-5 --partition=4
  
  ## --partitionを指定しないと、すべてのpartitionのメッセージを受け取り
  docker exec -it broker1 kafka-console-consumer --bootstrap-server=broker3:29093 --topic=sample3-5
  
  ```



- クリアアップ（Topicの削除）

  ```bash
  #docker exec -it broker1 kafka-topics --zookeeper zookeeper --delete --topic sample2-2
  ```

  

### 2.2 kafka-connect 検証

<u>Connectの設定はKafkaのCofingフォルダ()に設定することができるが、便利上では、kafka-connect-uiを通じて設定する。画面で設定値を修正して、curlコマンドを生成する（Rest-APIで）</u>

![File-To-File](.\image\File-To-File.png)

#### 2.2.1 File-To-File

- Source Connectの設定

```bash
curl -X POST \
  /api/kafka-connect-1/connectors \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
  "name": "FileStreamSourceConnector",
  "config": {
    "connector.class": "org.apache.kafka.connect.file.FileStreamSourceConnector",
    "tasks.max": "1",
    "topic": "file-topic",
    "file": "/tmp/file-topic-test"
  }
}'
```

- sink Connectの設定

```bash
curl -X POST \
  /api/kafka-connect-1/connectors \
  -H 'Content-Type: application/json' \
  -H 'Accept: application/json' \
  -d '{
  "name": "FileStreamSinkConnector",
  "config": {
    "connector.class": "org.apache.kafka.connect.file.FileStreamSinkConnector",
    "topics": "file-topic",
    "tasks.max": "1",
    "file": "/tmp/get-file"
  }
}'
```

- Consumerの起動

```bash
docker exec -it broker1 kafka-console-consumer --bootstrap-server=broker1:29091 --topic=file-topic
```

- 結果確認（kafka-connectのコンテナー内）

  get-fileは自動生成しているが、file-topic-testは自動生成していない

  誤操作で、/tmp/get-file、/tmp/file-topic-testを手動で削除すると、おかしくなる。最初からやり直すことになった

```bash
### ファイルに何か書き込む
[root@kafka-connect tmp]# echo 1111 >> file-topic-test
[root@kafka-connect tmp]# echo 2222 >> file-topic-test
[root@kafka-connect tmp]# echo 3333 >> file-topic-test
### Consumer側の内容を確認する　(バグかも、file-topic-testが生成していない状態で書き込むと一行目の内容がなくなってしまった)
[root@kafka-connect tmp]# cat get-file

2222
3333
[root@kafka-connect tmp]# cat file-topic-test
1111
2222
3333


###　Consumer側のログ内容
[root@dell-master ~]# docker exec -it broker1 kafka-console-consumer --bootstrap-server=broker3:29093 --topic=file-topic

2222
3333
```

#### 2.2.1 DB-To-DB

##### ア テーブル用意

```sql
CREATE DATABASE inputdatabase;
CREATE DATABASE outputdatabase;


##Sourceテーブル
CREATE TABLE person(
   pid SERIAL  PRIMARY KEY    NOT NULL,
   name           CHAR(20)    NOT NULL,
   age            INT         NOT NULL,
   address        CHAR(50),
   PRIMARY KEY (pid)
);

##Sinkテーブル
CREATE TABLE kafkaperson(
   pid SERIAL PRIMARY KEY     NOT NULL,
   name           CHAR(20)    NOT NULL,
   age            INT         NOT NULL,
   address        CHAR(50)
);

```



##### イ Source Connector確認

- Source Connector定義

  ```json
  {
    "name": "jdbc-source-Connector",
    "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
    "tasks.max": "1",
    "connection.url": "jdbc:postgresql://postgres-server:5432/inputdatabase",
    "mode": "incrementing",
    "incrementing.column.name": "pid",
    "topic.prefix": "test-postgresql-jdbc-",
    "connection.user": "postgres",
    "connection.password": "postgres",
    "table.whitelist": "person"
  }
  ```

- 疎通確認

  ```bash
  ## consumerを起動する前に、２レコードを追加する
  INSERT into person(name , age , address) values ('name111' , 11 , 'address 123-23') ;
  INSERT into person(name , age , address) values ('name222' , 22 , 'address 123-23') ;
  
  ## consumer起動(データがちょっとおかしい　データタイプの関係かも)　ToDo　
  docker exec -it broker1 kafka-console-consumer --bootstrap-server=broker1:29091 --topic=test-postgresql-jdbc-person  --from-beginning
  (name111             daddress 123-23
  (name222             ,daddress 123-23
  
  ## ２レコードを追加する
  INSERT into person(name , age , address) values ('name333' , 33 , 'address 123-23') ;
  INSERT into person(name , age , address) values ('name444' , 44 , 'address 123-23') ;
  
  ## しばらくすると、データ表示する
  docker exec -it broker1 kafka-console-consumer --bootstrap-server=broker1:29091 --topic=test-postgresql-jdbc-person  --from-beginning
  (name111             daddress 123-23
  (name222             ,daddress 123-23
  (name333             Bdaddress 123-23
  (name444             Xdaddress 123-23
  ```

  

```
psql -h kafka-1 -p 5432 -d testdb -U connectuser -c "INSERT INTO test_table(seq, item) VALUES (3, 'banana');"
```

##### ウ Sink Connector確認

- Sink Connector定義

  Sink Connector定義を追加した後に、outputdatabaseにテーブルにデータが自動的に同期化とする　

  ```json
  {
      "name": "Jdbc-Sink-Connector",
      "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
      "topics": "test-postgresql-jdbc-person",
      "tasks.max": 1,
      "connection.url": "jdbc:postgresql://postgres-server:5432/outputdatabase",
      "connection.user": "postgres",
      "connection.password": "postgres",
      "table.name.format": "kafkaperson",
      "auto.create": "false",
      "insert.mode": "upsert",
      "pk.mode": "record_value",
      "pk.fields":"pid"
  }
  
  ## テーブル自動作成の場合(作成したテーブルは：test-postgresql-jdbc-person)　ToDo　自動作成したテーブル
  {
      "name": "Jdbc-Sink-Connector",
      "connector.class": "io.confluent.connect.jdbc.JdbcSinkConnector",
      "topics": "test-postgresql-jdbc-person",
      "tasks.max": 1,
      "connection.url": "jdbc:postgresql://postgres-server:5432/outputdatabase",
      "connection.user": "postgres",
      "connection.password": "postgres",
      "auto.create": "auto",
      "insert.mode": "upsert",
      "auto.create": "true",
      "pk.mode": "record_value",
      "pk.fields":"pid"
  }
  ```

  

### 2.3 kafka-stream 検証

https://kafka.apache.org/26/documentation/streams/quickstart



![kafka-all](.\image\kafka-all.png)



# ９９． 今後確認

- メッセージを生成する際に、異なるPartitionに格納することについて、クライアント側で実装している？？？　どのPartitionに追加するかだけ決めるか？
- ConnectSourceはどのような技術を利用して実現しているか？　　File　DB
  - File:  FileStreamSourceConnector.java  COREソースはFileStreamSourceTask.javaであり、そこに、ファイルを読んでいる、読んでいるデータをRecorderとして作成して、Brokerへ送信する。
  - DB：　　おそらく、pkやTimeStampで更新・増加の分を抽出して、レコード作成するか　　Deleteはどうなるか？
- ConnectでInsertは行けた、Update、Deleteは？









