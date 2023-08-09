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
                groupId = KafkaConstants.GROUP_ID
        )
        public void listen(Message message) {

            Integer newValue = message.getSender();
            String value = message.getContent();
            long sendTime = message.getSendtime();
            long receiveTime = System.currentTimeMillis();
            kafkaService.updateValue(newValue, value);
            kafkaService.printValues();
            long elapsedTime = receiveTime - sendTime;
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("elapsedTimes.txt", true))) {
                writer.append("ID: " + newValue + ", Elapsed time: " + elapsedTime + " ms\n");
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.out.println("Elapsed time: " + elapsedTime + " ms");
            System.out.println("sending via kafka listener..");
            template.convertAndSend("/topic/group", message);
            template.convertAndSend("/topic/group/values", kafkaService.printValues());
        }
    }
