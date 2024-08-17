package io.df.henry.example.api;

import com.google.gson.Gson;
import io.df.henry.example.dto.UserEventVo;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin(origins = "*", allowedHeaders = "*")
@Slf4j
public class ProducerApi {
  private final KafkaTemplate<String, String> kafkaTemplate;

  public ProducerApi(KafkaTemplate<String, String> kafkaTemplate) {
    this.kafkaTemplate = kafkaTemplate;
  }

  @GetMapping("/api/select")
  public void selectColor(
      @RequestHeader("user-agent") String userAgentName,
      @RequestParam(value = "color") String colorName,
      @RequestParam(value = "user") String userName
  ) {
    SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZ");
    Date now = new Date();
    Gson gson = new Gson();
    UserEventVo userEventVo = new UserEventVo(
        sdfDate.format(now),
        userAgentName, colorName, userName);
    String jsonolorLog = gson.toJson(userEventVo);
    kafkaTemplate.send("select-color", jsonolorLog)
        .thenAccept(
            r -> {
              log.info(r.toString());
            }
        ).exceptionally(
            ex -> {
              log.error(ex.getMessage(), ex);
              return null;
            }
        );

  }

}
