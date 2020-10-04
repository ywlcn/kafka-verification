package com.rgs.kafka.streams;

import java.util.List;
import java.util.Properties;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;

public class MyProducer {

    public static void excute(String topic, List<String> messages) {
        // 接続時の設定値を Properties インスタンスとして構築する
        Properties properties = new Properties();
        // 接続先 Kafka ノード
        properties.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, App.KAFKA_SERVER);
        properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, "org.apache.kafka.common.serialization.StringSerializer");
        
        // Producer を構築する
        KafkaProducer<String, String> producer = new KafkaProducer<>(properties, new StringSerializer(),
                new StringSerializer());

        try {
            // トピックを指定してメッセージを送信する
            for (int i = 0; i < messages.size(); i++) {
                producer.send(new ProducerRecord<String, String>(topic, messages.get(i)));
            }
        } finally {
            producer.close();
        }
    }
}
