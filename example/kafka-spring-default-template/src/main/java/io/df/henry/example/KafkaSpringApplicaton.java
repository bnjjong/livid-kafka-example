package io.df.henry.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class KafkaSpringApplicaton implements CommandLineRunner {
  private static String TOPIC_NAME = "test";

  @Autowired
  private KafkaTemplate<Integer, String> template;

  public static void main(String[] args){
    SpringApplication.run(KafkaSpringApplicaton.class, args);
  }

  /**
   * 컨슈머로 확인 하는 방법
   * <pre>
   * bin/kafka-console-consumer.sh --bootstrap-server localhost:9092 \
   * --topic test --from-beginning
   * Test0
   * Test1
   * Test2
   * Test3
   * Test4
   * Test5
   * Test6
   * Test7
   * Test8
   * Test9
   * </pre>
   * @param args
   * @throws Exception
   */
  @Override
  public void run(String... args) throws Exception {
    for (int i=0; i<10; i++) {
      template.send(TOPIC_NAME, "Test" + i);
    }
    System.exit(0);
  }
}
