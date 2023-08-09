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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@SpringBootApplication
public class KafkaJavaApp {
    public static void main(String[] args) {
        SpringApplication.run(KafkaJavaApp.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(KafkaTemplate<String, Message> kafkaTemplate) {
        return args -> {
            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (int i = 0; i < 10000; i++) {
                final int index = i;
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    Message message = new Message();
                    message.setSender(index);
                    message.setContent("10");
                    message.setTimestamp(LocalDateTime.now().toString());
                    message.setSendtime(System.currentTimeMillis());
                    kafkaTemplate.send(KafkaConstants.KAFKA_TOPIC, message);
                });

                futures.add(future);
            }

            CompletableFuture<Void> allOf = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
            allOf.join();
        };
    }
}