package io.df.henry.example;

import io.df.henry.example.config.ElasticSearchSinkConnectorConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.errors.ConnectException;
import org.apache.kafka.connect.sink.SinkConnector;

@Slf4j
public class ElasticSearchSinkConnector extends SinkConnector {
  private Map<String, String> configProperties;

  @Override
  public void start(Map<String, String> props) {
    this.configProperties = props;
    try {
      new ElasticSearchSinkConnectorConfig(props);
    } catch (ConfigException e) {
      throw new ConnectException(e.getMessage(), e);
    }

  }

  @Override
  public Class<? extends Task> taskClass() {
    return ElasticSearchSinkTask.class;
  }

  @Override
  public List<Map<String, String>> taskConfigs(int maxTasks) {
    List<Map<String, String>> taskConfigs = new ArrayList<>();
    Map<String, String> taskProps = new HashMap<>();
    taskProps.putAll(configProperties);

    for (int i=0; i<maxTasks; i++) {
      taskConfigs.add(taskProps);
    }
    return taskConfigs;
  }

  @Override
  public void stop() {
    log.info("Stop elasticsearch connector");
  }

  @Override
  public ConfigDef config() {
    return ElasticSearchSinkConnectorConfig.CONFIG;
  }

  @Override
  public String version() {
    return "1.0";
  }
}
