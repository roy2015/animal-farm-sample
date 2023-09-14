package com.animal.farm.infrastructure.foundation.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.beanutils.BeanUtilsBean;
import org.apache.commons.beanutils.ConversionException;
import org.apache.commons.beanutils.ConvertUtilsBean;
import org.apache.commons.lang3.reflect.FieldUtils;

/**
 * @author : zhengyangyong
 */
public class ObjectUtil {
  private static final String EMPTY = "";

  private static final String NULL = "null";

  private static ConvertUtilsBean convertUtilsBean = null;

  private static final String SERIAL_VERSION = "serialVersionUID";

  static {
    BeanUtilsBean.getInstance().getConvertUtils().register(true, true, 0);
    ObjectUtil.convertUtilsBean = BeanUtilsBean.getInstance().getConvertUtils();
  }

  private static final Map<Class, Map<String, Field>> CLASS_FIELDS = new ConcurrentHashMap<>();

  public static boolean isFieldsNotAllNull(Object object, String... ignoreFields) throws IllegalAccessException {
    return !isFieldsAllNull(object, ignoreFields);
  }

  public static boolean isFieldsAllNull(Object object, String... ignoreFields) throws IllegalAccessException {
    if (object == null) {
      return true;
    }
    Map<String, Field> fields = getObjectFields(object);
    Set<String> ignores =
        CollectionUtil.isNullOrEmpty(ignoreFields) ? new HashSet<>() : new HashSet<>(Arrays.asList(ignoreFields));
    for (Entry<String, Field> field : fields.entrySet()) {
      if (!ignores.contains(field.getKey())) {
        Object value = FieldUtils.readField(object, field.getKey(), true);
        if (value != null) {
          return false;
        }
      }
    }
    return true;
  }

  public static <T> T clone(Object object, Class<T> type) {
    try {
      T target = type.newInstance();
      merge(object, target);
      return target;
    } catch (Exception ex) {
      throw new RuntimeException(ex);
    }
  }

  public static boolean merge(Object from, Object target) {
    if (from == null || target == null) {
      throw new IllegalArgumentException("from and target can not be null");
    }
    Map<String, Field> sourceFields = getObjectFields(from);
    Map<String, Field> targetFields = getObjectFields(target);
    boolean merged = false;
    if (sourceFields.size() != 0 && targetFields.size() != 0) {
      try {
        for (Entry<String, Field> sourceField : sourceFields.entrySet()) {
          if (targetFields.containsKey(sourceField.getKey()) &&
              sourceField.getValue().getAnnotatedType().getType()
                  .equals(targetFields.get(sourceField.getKey()).getAnnotatedType().getType())) {
            Object value = FieldUtils.readField(from, sourceField.getKey(), true);
            if (value != null) {
              if (Map.class.isAssignableFrom(value.getClass())) {
                if (((Map) value).size() == 0) {
                  continue;
                }
              } else if (Collection.class.isAssignableFrom(value.getClass())) {
                if (((Collection) value).size() == 0) {
                  continue;
                }
              } else if (value.getClass().isArray()) {
                if (Array.getLength(value) == 0) {
                  continue;
                }
              }
              FieldUtils.writeField(target, sourceField.getKey(), value, true);
              merged = true;
            }
          }
        }
      } catch (IllegalAccessException ex) {
        throw new RuntimeException(ex);
      }
    }
    return merged;
  }

  private static Map<String, Field> getObjectFields(Object object) {
    return CLASS_FIELDS.computeIfAbsent(object.getClass(),
        type -> Arrays.stream(FieldUtils.getAllFields(type))
            .filter(p -> !SERIAL_VERSION.equals(p.getName()) &&
                !Modifier.isStatic(p.getModifiers()) &&
                !p.getName().startsWith("$"))
            .collect(Collectors.toMap(Field::getName, field -> field)));
  }

  public static <T> T convert(Object object, Class<T> type) throws ConversionException {
    if (object == null) {
      return null;
    }
    if (object.getClass().equals(type)) {
      return (T) object;
    }
    return (T) convertUtilsBean.convert(object, type);
  }

  public static <T> T convert(Object object, Class<T> type, T defaultValue) {
    if (object == null) {
      return null;
    }
    if (object.getClass().equals(type)) {
      return (T) object;
    }
    try {
      return (T) convertUtilsBean.convert(object, type);
    } catch (ConversionException ex) {
      return defaultValue;
    }
  }

  public static boolean isEmpty(Object obj) {
    if (obj == null) {
      return true;
    }
    if (obj instanceof String) {
      String s = (String) obj;
      return EMPTY.equals(s.trim()) || NULL.equals(s);
    } else if (obj instanceof Map) {
      Map m = (Map) obj;
      return m.size() == 0;
    } else if (obj instanceof List) {
      List l = (List) obj;
      return l.size() == 0;
    } else if (obj instanceof Set) {
      Set l = (Set) obj;
      return l.size() == 0;
    } else if (obj instanceof String[]) {
      String[] arr = (String[]) obj;
      return arr.length == 0;
    }
    return false;
  }

  public static boolean isNotEmpty(Object obj) {
    return !isEmpty(obj);
  }

  public static boolean isAnyNotEmpty(Object obj, Object... objs) {
    if (isNotEmpty(obj)) {
      return true;
    }
    if (objs != null) {
      for (Object o : objs) {
        if (isNotEmpty(o)) {
          return true;
        }
      }
    }
    return false;
  }

  public static boolean isAnyEmpty(Object obj, Object... objs) {
    if (isEmpty(obj)) {
      return true;
    }
    if (objs != null) {
      for (Object o : objs) {
        if (isEmpty(o)) {
          return true;
        }
      }
    }

    return false;
  }

  public static boolean isAllEmpty(Object date, Object... dates) {
    return !isAnyNotEmpty(date, dates);
  }

  public static boolean isAllNotEmpty(Object date, Object... dates) {
    return !isAnyEmpty(date, dates);
  }

  public static <T> T[] concatAll(T[] first, T[]... rest) {
    int totalLength = first.length;
    for (T[] array : rest) {
      totalLength += array.length;
    }
    T[] result = Arrays.copyOf(first, totalLength);
    int offset = first.length;
    for (T[] array : rest) {
      System.arraycopy(array, 0, result, offset, array.length);
      offset += array.length;
    }
    return result;
  }
}
