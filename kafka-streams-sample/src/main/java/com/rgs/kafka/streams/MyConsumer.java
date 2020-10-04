package com.rgs.kafka.streams;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

public class MyConsumer {

    public static void excute(String topic) {
        // 接続時の設定値を Properties インスタンスとして構築する
        Properties properties = new Properties();
        // 接続先 Kafka ノード
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, App.KAFKA_SERVER);
        // Consumer を識別するための group id
        properties.put(ConsumerConfig.GROUP_ID_CONFIG, "java-consumer-group");
        // 読み取り位置である offset が未指定だった場合に、Kafka 上に最も早く置かれたメッセージを読み取る
        properties.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        // 読み取り後に、読み取り位置である offset を自動で更新する
        properties.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        // Consumer を構築する
        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(properties, new StringDeserializer(),
                new StringDeserializer());

        // Consumer をトピックに割り当てる
        consumer.subscribe(Arrays.asList(topic));

        try {
            while (true) {
                // メッセージをとりだす
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

                // とりだしたメッセージを表示する
                for (ConsumerRecord<String, String> record : records) {
                    System.out.println(String.format("%s:%s", record.offset(), record.value()));
                }

                // メッセージの読み取り位置である offset を、最後に poll() した位置で(同期処理で)更新する
                consumer.commitSync();
            }
        } finally {
            consumer.close();
        }
    }
}
