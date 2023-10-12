package com.animal.farm.familytree.infrastructure;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.YamlMapFactoryBean;
import org.springframework.beans.factory.config.YamlProcessor.ResolutionMethod;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Component;

import com.animal.farm.familytree.infrastructure.util.ObjectUtil;
import com.animal.farm.familytree.infrastructure.util.StringUtil;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.dataformat.javaprop.JavaPropsFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator;

/**
 * @author : zhengyangyong
 */
@Component
public class ServiceConfiguration {
  private static final Logger LOGGER = LoggerFactory.getLogger(ServiceConfiguration.class);

  private volatile Map<String, Object> configurations = new HashMap<>(0);

  public ServiceConfiguration() throws IOException {
    StringBuilder builder = new StringBuilder();
    System.getProperties().forEach((key, value) -> {
      if (!key.toString().startsWith("sun") && !key.toString().startsWith("java") &&
          !key.toString().startsWith("file") && !key.toString().startsWith("catalina") &&
          !key.toString().startsWith("jboss") && !key.toString().startsWith("intellij")) {
        builder.append(String.format("%s=%s\n", key, value == null ? "" : value.toString()));
      }
    });

    //读取application-${spring.profiles.active}.yml
    String propertiesToYaml = convertPropertiesToYaml(builder.toString());
    String active = System.getProperties().getProperty("spring.profiles.active");
    if (StringUtil.hasText(active)) {
      active = "-" + active;
    } else {
      active = "";
    }
    YamlMapFactoryBean factory = new YamlMapFactoryBean();
    factory.setResolutionMethod(ResolutionMethod.OVERRIDE_AND_IGNORE);
    factory.setResources(new ClassPathResource("/application" + active + ".yml"),
        new InputStreamResource(new ByteArrayInputStream(propertiesToYaml.getBytes(StandardCharsets.UTF_8))));
    this.configurations = factory.getObject();
  }

  public <T> T get(String path, T defaultValue, Class<T> type) {
    String[] keys = path.split("\\.");
    Map<String, Object> values = this.configurations;
    for (int i = 0; i < keys.length - 1; i++) {
      if (values.containsKey(keys[i])) {
        values = (Map<String, Object>) values.get(keys[i]);
      } else {
        return defaultValue;
      }
    }

    String key = keys[keys.length - 1];
    Object returnValue = values.getOrDefault(key, defaultValue);
    if (returnValue == null) {
      return null;
    } else {
      if (String.class.equals(type)) {
        return (T) returnValue.toString();
      } else if (type.equals(returnValue.getClass()) || type.isAssignableFrom(returnValue.getClass())) {
        return (T) returnValue;
      } else if (List.class.equals(type)) {
        if (Map.class.isAssignableFrom(returnValue.getClass())) {
          return (T) new ArrayList<>(Collections.singletonList(returnValue));
        } else {
          return (T) returnValue;
        }
      } else {
        return ObjectUtil.convert(returnValue, type);
      }
    }
  }

  public <T> T getInterceptor(String name, String path, T defaultValue, Class<T> type) {
    return get("interceptor." + name + "." + path, defaultValue, type);
  }

  public <T> T getService(String name, String path, T defaultValue, Class<T> type) {
    return get("service." + name + "." + path, defaultValue, type);
  }

  public Map<String, String> get(String path) throws Exception {
    Map<String, String> all = new HashMap<>();
    get(all, path, get(path, null, Object.class));
    return all;
  }

  private void get(Map<String, String> all, String path, Object input) throws Exception {
    if (input != null) {
      if (List.class.isAssignableFrom(input.getClass())) {
        throw new Exception("unsupported list type");
      } else if (Map.class.isAssignableFrom(input.getClass())) {
        Map<String, Object> map = (Map<String, Object>) input;
        if (map.size() != 0) {
          for (Entry<String, Object> entry : map.entrySet()) {
            String key = String.format("%s.%s", path, entry.getKey());
            get(all, key, entry.getValue());
          }
        }
      } else {
        all.put(path, input.toString());
      }
    }
  }

  private String convertPropertiesToYaml(String properties) throws IOException {
    if (properties.contains("\\")) {
      properties = properties.replace("\\", "/");
    }
    try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JsonParser parser = new JavaPropsFactory().createParser(
            new InputStreamReader(new ByteArrayInputStream(properties.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8));
        YAMLGenerator generator = new YAMLFactory()
            .createGenerator(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8))) {
      JsonToken token = parser.nextToken();
      while (token != null) {
        if (JsonToken.START_OBJECT.equals(token)) {
          generator.writeStartObject();
        } else if (JsonToken.FIELD_NAME.equals(token)) {
          generator.writeFieldName(parser.getCurrentName());
        } else if (JsonToken.VALUE_STRING.equals(token)) {
          generator.writeString(parser.getText());
        } else if (JsonToken.END_OBJECT.equals(token)) {
          generator.writeEndObject();
        }
        token = parser.nextToken();
      }
      generator.flush();
      return new String(outputStream.toByteArray(), StandardCharsets.UTF_8);
    }
  }
}
