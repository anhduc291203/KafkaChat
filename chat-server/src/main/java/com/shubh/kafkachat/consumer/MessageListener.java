package com.shubh.kafkachat.consumer;

import com.shubh.kafkachat.Service.KafkaService;
import com.shubh.kafkachat.constants.KafkaConstants;
import com.shubh.kafkachat.model.Message;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@Component
public class MessageListener {
    private KafkaService kafkaService;
    private SimpMessagingTemplate template;

    public MessageListener(KafkaService kafkaService, SimpMessagingTemplate template){
        this.kafkaService = kafkaService;
        this.template = template;
    }

    @KafkaListener(
            topics = KafkaConstants.KAFKA_TOPIC,
            groupId = KafkaConstants.GROUP_ID,
            concurrency = "10"
    )
    public void listen(Message message) {

        long sendTimestamp = message.getSendtime();
        long receiveTimestamp = System.currentTimeMillis();

        Integer newValue = message.getSender();
        String value = message.getContent();
        kafkaService.updateValue(newValue, value);
        kafkaService.printValues();

        long endProcessingTimestamp = System.currentTimeMillis();
        long brokerLatency = receiveTimestamp - sendTimestamp;
        long processingLatency = endProcessingTimestamp - receiveTimestamp;

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("elapsedTimes10000.txt", true))) {
            writer.append("ID: " + newValue +
                    ", Broker Latency: " + brokerLatency + " ms" +
                    ", Processing Latency: " + processingLatency + " ms" +
                    ", Send time: " + sendTimestamp + " ms" +
                    ", Receive Time: " + receiveTimestamp + " ms\n");
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Broker Latency: " + brokerLatency + " ms");
        System.out.println("Processing Latency: " + processingLatency + " ms");
        System.out.println("sending via kafka listener...");
        template.convertAndSend("/topic/group", message);
        template.convertAndSend("/topic/group/values", kafkaService.printValues());
    }
}
