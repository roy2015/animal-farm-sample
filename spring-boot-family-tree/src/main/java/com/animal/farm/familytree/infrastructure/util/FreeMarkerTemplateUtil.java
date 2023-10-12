package com.animal.farm.familytree.infrastructure.util;

import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import com.animal.farm.familytree.infrastructure.exception.DataPlatformException;
import com.animal.farm.familytree.infrastructure.web.MessageCode;

import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * @author wei
 */
public class FreeMarkerTemplateUtil {
  public static String freemarkerProcess(String templateStr, Map<String, String> input) throws DataPlatformException {
    StringTemplateLoader stringLoader = new StringTemplateLoader();
    String template = "sql_content";
    stringLoader.putTemplate(template, templateStr);
    Configuration cfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
    cfg.setTemplateLoader(stringLoader);
    try {
      Template templateCon = cfg.getTemplate(template);
      StringWriter writer = new StringWriter();
      templateCon.process(input, writer);
      return writer.toString();
    } catch (IOException | TemplateException e) {
      throw new DataPlatformException(MessageCode.ERROR_1107, "解析参数", templateStr, e.getMessage());
    }
  }

  public static List<String> getVariableNames(String templateStr) {
    String pattern = "\\$\\{(.*?)\\}";
    Pattern r = Pattern.compile(pattern);
    Matcher m = r.matcher(templateStr);
    Set<String> result = new LinkedHashSet<>();
    while (m.find()) {
      result.add(m.group(1));
    }
    return new ArrayList<>(result);
  }
}
