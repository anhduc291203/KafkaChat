package com.shubh.kafkachat;

import com.shubh.kafkachat.constants.KafkaConstants;
import com.shubh.kafkachat.model.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class KafkaJavaApp {
    public static void main(String[] args) {
        SpringApplication.run(KafkaJavaApp.class, args);
    }
    @Bean
    CommandLineRunner commandLineRunner(KafkaTemplate<String, Message> kafkaTemplate){
        return args -> {
            for (int i = 0; i < 1000; i++) {
                try {
                    //Sending the message to kafka topic queu   e
                    Message message = new Message();
                    message.setSender(0 + i);
                    message.setContent("10");
                    message.setTimestamp(LocalDateTime.now().toString());
                    message.setSendtime(System.currentTimeMillis());
                    kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC, message).get();
                } catch (InterruptedException | ExecutionException e) {
                    throw new RuntimeException(e);
                }
            }
        };
    }
}
