
package com.rgs.kafka.streams;

import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.kstream.KStream;

import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

public class LineSplit {

    public static void excute() throws Exception {

        String producerTopic = "streams-plaintext-input";
        String consumerTopic = "streams-linesplit-output";

        Properties props = new Properties();
        props.put(StreamsConfig.APPLICATION_ID_CONFIG, "streams-linesplit");
        props.put(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, App.KAFKA_SERVER);
        props.put(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass());
        props.put(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.String().getClass());

        final StreamsBuilder builder = new StreamsBuilder();

        KStream<String, String> source = builder.stream(producerTopic);
        source.flatMapValues(value -> Arrays.asList(value.split("\\W+"))).to(consumerTopic);

        final Topology topology = builder.build();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        final CountDownLatch latch = new CountDownLatch(1);

        List<String> messages = Arrays.asList("hello this word","goode after word","night before word","back after world","goode noon word");
        MyProducer.excute(producerTopic, messages);

        Thread threadConsumer =  new Thread("threadConsumer") {
            @Override
            public void run() {
                MyConsumer.excute(consumerTopic);
            }
        };



        // attach shutdown handler to catch control-c
        Runtime.getRuntime().addShutdownHook(new Thread("streams-shutdown-hook") {
            @Override
            public void run() {
                // threadConsumer.stop();
                // streams.close();
                // latch.countDown();
            }
        });

        try {

            // Producer & Con
            threadConsumer.start();
            streams.start();            
            latch.await();
        } catch (Throwable e) {
            System.exit(1);
        }
        System.exit(0);
    }




}