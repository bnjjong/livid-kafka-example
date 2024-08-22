package io.df.henry.example.config;

import java.util.Map;
import org.apache.kafka.common.config.AbstractConfig;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigDef.Importance;
import org.apache.kafka.common.config.ConfigDef.Type;

public class ElasticSearchSinkConnectorConfig extends AbstractConfig {
  public static final String ES_CLUSTER_HOST = "es.host";
  public static final String ES_CLUSTER_HOST_DEFAULT_VALUE = "localhost";
  public static final String ES_CLUSTER_HOST_DOC = "localhost";

  public static final String ES_CLUSTER_PORT = "es.port";
  public static final String ES_CLUSTER_PORT_DEFAULT_VALUE = "9200";
  public static final String ES_CLUSTER_PORT_DOC = "9200";

  public static final String ES_INDEX = "es.index";
  public static final String ES_INDEX_DEFAULT_VALUE = "kafka-connector-index";
  public static final String ES_INDEX_DOC = "kafka-connector-index";

  public static ConfigDef CONFIG =
      new ConfigDef()
          .define(
              ES_CLUSTER_HOST,
              Type.STRING,
              ES_CLUSTER_HOST_DEFAULT_VALUE,
              Importance.HIGH,
              ES_CLUSTER_PORT_DOC)
          .define(
              ES_CLUSTER_PORT,
              Type.INT,
              ES_CLUSTER_PORT_DEFAULT_VALUE,
              Importance.HIGH,
              ES_CLUSTER_PORT_DOC)
          .define(ES_INDEX, Type.STRING, ES_INDEX_DEFAULT_VALUE, Importance.HIGH, ES_INDEX_DOC);

  public ElasticSearchSinkConnectorConfig(Map<String, String> props) {
    super(CONFIG, props);
  }


}
