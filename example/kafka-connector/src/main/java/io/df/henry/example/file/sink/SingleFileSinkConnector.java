package io.df.henry.example.file.sink;

import io.df.henry.example.file.source.SingleFileSourceConnectorConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.kafka.common.config.ConfigDef;
import org.apache.kafka.common.config.ConfigException;
import org.apache.kafka.connect.connector.Task;
import org.apache.kafka.connect.sink.SinkConnector;

public class SingleFileSinkConnector extends SinkConnector {
  private Map<String, String> configProperties;
  @Override
  public String version() {
    return "1.0";
  }

  @Override
  public void start(Map<String, String> props) {
    this.configProperties = props;

    try{
      new SingleFileSinkConnectorConfig(props);
    } catch (ConfigException e) {
      throw new ConfigException(e.getMessage(), e);
    }
  }

  @Override
  public Class<? extends Task> taskClass() {
    return SingleFileSinkTask.class;
  }

  /**
   * 태스크가 2개 이상인 경우라도 동일한 설정값으로 설정 함.
   * 태스크 마다 다르게 설정할 수도 있음.
   * @param maxTasks maximum number of configurations to generate
   * @return
   */
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
  public ConfigDef config() {
    return SingleFileSourceConnectorConfig.CONFIG;
  }

  @Override
  public void stop() {

  }


}
