package io.df.henry.example.file.source;

import java.util.Map;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

public class SingleFileSourceConnectorConfig extends AbstractConfig {
  public static final String DIR_FILE_NAME = "file ";
  private static final String DIR_FILE_NAME_DEFAULT_VALUE = "/tmp/kafka.txt ";
  // 읽을 파일 경로와 이름
  private static final String DIR_FILE_NAME_DOC = "this is sample file test.";
  public static final String TOPIC_NAME = "topic";
  private static final String TOPIC_DEFAULT_VALUE = "test";
  private static final String TOPIC_DOC = "";
  public static ConfigDef CONFIG =
      new ConfigDef()
          .define(
              DIR_FILE_NAME,
              Type.STRING,
              DIR_FILE_NAME_DEFAULT_VALUE,
              Importance.HIGH,
              DIR_FILE_NAME_DOC)
          .define(TOPIC_NAME, Type.STRING, TOPIC_DEFAULT_VALUE, Importance.HIGH, TOPIC_DOC);

  public SingleFileSourceConnectorConfig(Map<String, String> originals) {
    super(CONFIG, originals);
  }
}
