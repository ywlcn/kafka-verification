/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.rgs.kafka.streams;

public class App {

    // Todo 実際のサーバへ修正
    public static String KAFKA_SERVER = "dell-master:9091";

    public String getGreeting() {
        return "Hello world.";
    }

    public static void main(String[] args) throws Exception {

        LineSplit.excute();

    }
}