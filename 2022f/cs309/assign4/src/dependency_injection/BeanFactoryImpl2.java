package dependency_injection;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * TODO you should complete the class
 */
public class BeanFactoryImpl2 implements BeanFactory {

//  private Map<String, String> injectMap = new HashMap<>();
//  private Map<String, String> valueMap = new HashMap<>();

    private Properties injectProp;
    private Properties valueProp;

    @Override
    public void loadInjectProperties(File file) {
        injectProp = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            injectProp.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void loadValueProperties(File file) {
        valueProp = new Properties();
        try {
            InputStream in = new BufferedInputStream(new FileInputStream(file));
            valueProp.load(in);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public <T> T createInstance(Class<T> clazz) {
        Object res = null;
        try {
            String clazzName = clazz.getName();
            Class<?> implClazz = null;
            String implClazzName = null;
            if (injectProp.containsKey(clazzName)) {
                implClazzName = injectProp.getProperty(clazzName);
                implClazz = Class.forName(implClazzName);
            } else {
                implClazz = clazz;
            }

            Constructor<?> constructor = null;
            for (Constructor<?> c : implClazz.getDeclaredConstructors()) {
                if (c.getAnnotation(Inject.class) != null) {
                    constructor = c;
                    break;
                }
            }

            if (constructor == null) {
                res = implClazz.getDeclaredConstructor().newInstance();
            } else {
                Parameter[] parameters = constructor.getParameters();
                List<Object> objects = new ArrayList<>();
                for (Parameter p : parameters) {
                    if (p.getAnnotation(Value.class) != null) {
                        Value valueAnnotation = p.getAnnotation(Value.class);
                        String value = valueAnnotation.value();
                        if (valueProp.containsKey(valueAnnotation.value())) {
                            value = valueProp.getProperty(valueAnnotation.value());
                        }

                        if (p.getType() == Boolean.class) {
                            String[] strings = value.split(valueAnnotation.delimiter());
                            Boolean b = null;
                            for (String string : strings) {
                                String s = string.toLowerCase();
                                if (s.equals("true") || s.equals("false")) {
                                    b = Boolean.parseBoolean(s);
                                }
                            }
                            objects.add(b);
                        } else if (p.getType() == boolean.class) {
                            String[] strings = value.split(valueAnnotation.delimiter());
                            boolean b = false;
                            for (String string : strings) {
                                String s = string.toLowerCase();
                                if (s.equals("true") || s.equals("false")) {
                                    b = Boolean.parseBoolean(s);
                                    break;
                                }
                            }
                            objects.add(b);
                        } else if (p.getType() == Integer.class) {
                            String[] strings = value.split(valueAnnotation.delimiter());
                            Integer num = null;
                            for (String s : strings) {
                                if (isIntPattern(s)) {
                                    num = Integer.parseInt(s);
                                    break;
                                }
                            }
                            objects.add(num);
                        } else if (p.getType() == int.class) {
                            String[] strings = value.split(valueAnnotation.delimiter());
                            int num = 0;
                            for (String s : strings) {
                                if (isIntPattern(s)) {
                                    num = Integer.parseInt(s);
                                    break;
                                }
                            }
                            objects.add(num);
                        } else if (p.getType() == String.class) {
                            String[] strings = value.split(valueAnnotation.delimiter());
                            String string = null;
                            for (String s : strings) {
                                if (s != null) {
                                    string = s;
                                    break;
                                }
                            }
                            objects.add(string);
                        } else if (p.getType() == boolean[].class) {
                            value = deleteEdge(value, '[', ']');
                            String[] strings = value.split(valueAnnotation.delimiter());
                            ArrayList<Boolean> booleans = new ArrayList<>();
                            if (!value.equals("[]") && !value.equals("")) {
                                for (String string : strings) {
                                    String b = string.toLowerCase();
                                    if (b.equals("true") || b.equals("false")) {
                                        booleans.add(Boolean.parseBoolean(b));
                                    }
                                }
                            }
                            boolean[] boolean_list = new boolean[booleans.size()];
                            for (int i = 0; i < booleans.size(); i++) {
                                boolean_list[i] = booleans.get(i);
                            }
                            objects.add(boolean_list);
                        } else if (p.getType() == int[].class) {
                            value = deleteEdge(value, '[', ']');
                            String[] strings = value.split(valueAnnotation.delimiter());
                            ArrayList<Integer> ints = new ArrayList<>();
                            if (!value.equals("[]") && !value.equals("")) {
                                for (String s : strings) {
                                    if (isIntPattern(s)) {
                                        int num = Integer.parseInt(s);
                                        ints.add(num);
                                    }
                                }
                            }
                            int[] int_list = new int[ints.size()];
                            for (int i = 0; i < ints.size(); i++) {
                                int_list[i] = ints.get(i);
                            }
                            objects.add(int_list);
                        } else if (p.getType() == String[].class) {
                            value = deleteEdge(value, '[', ']');
                            String[] strings = value.split(valueAnnotation.delimiter());
                            ArrayList<String> string_list = new ArrayList<>();
                            if (!value.equals("[]") && !value.equals("")) {
                                for (String s : strings) {
                                    if (s != null) {
                                        string_list.add(s);
                                    }
                                }
                            }
                            String[] res_string = new String[string_list.size()];
                            for (int i = 0; i < string_list.size(); i++) {
                                res_string[i] = string_list.get(i);
                            }
                            objects.add(res_string);
                        } else if (p.getType() == List.class) {
                            Class<?> elementType = Class.forName(((ParameterizedType) p.getParameterizedType())
                                    .getActualTypeArguments()[0].getTypeName());
                            value = deleteEdge(value, '[', ']');
                            String[] elements = value.split(valueAnnotation.delimiter());
                            List<Object> list = new ArrayList<>();
                            if (!value.equals("[]") && !value.equals("")) {
                                for (String element : elements) {
                                    if (isLegal(elementType, element)) {
                                        list.add(convert(elementType, element));
                                    }
                                }
                            }
                            objects.add(list);
                        } else if (p.getType() == Set.class) {
                            Class<?> elementType = Class.forName(((ParameterizedType) p.getParameterizedType())
                                    .getActualTypeArguments()[0].getTypeName());
                            value = deleteEdge(value, '[', ']');
                            String[] elements = value.split(valueAnnotation.delimiter());
                            Set<Object> set = new HashSet<>();
                            if (!value.equals("[]") && !value.equals("")) {
                                for (String element : elements) {
                                    if (isLegal(elementType, element)) {
                                        set.add(convert(elementType, element));
                                    }
                                }
                            }
                            objects.add(set);
                        } else if (p.getType() == Map.class) {
                            //find parameter type of Map
                            Class<?> keyType = Class.forName(((ParameterizedType) p.getParameterizedType())
                                    .getActualTypeArguments()[0].getTypeName());
                            Class<?> valueType = Class.forName(((ParameterizedType) p.getParameterizedType())
                                    .getActualTypeArguments()[1].getTypeName());
//              System.out.println(keyType + ":" + valueType);
                            value = deleteEdge(value, '{', '}');
                            String[] elements = value.split(valueAnnotation.delimiter());
                            Map<Object, Object> mapObj = new HashMap<>();
                            if (!value.equals("{}") && !value.equals("")) {
                                for (String element : elements) {
                                    String key0 = element.split(":")[0];
                                    String value0 = element.split(":")[1];
                                    if (isLegal(keyType, key0) && isLegal(valueType, value0)) { //好好写一下
                                        mapObj.put(convert(keyType, key0), convert(valueType, value0));
                                    }
//                System.out.printf("%s:%s%n", key0, mapObj.get(key0));
                                }
                            }
                            objects.add(mapObj);
                        }

                    } else {
                        objects.add(createInstance(p.getType()));
                    }
                }
                Object[] ob = new Object[objects.size()];
                for (int i = 0; i < objects.size(); i++) {
                    ob[i] = objects.get(i);
                }
                res = constructor.newInstance(ob);
            }

            Field[] fields = res.getClass().getDeclaredFields();
            for (Field f : fields) {
                if (f.getAnnotation(Value.class) != null) {
                    Value valueAnnotation = f.getAnnotation(Value.class);
                    String value = valueAnnotation.value();
                    if (valueProp.containsKey(valueAnnotation.value())) {
                        value = valueProp.getProperty(valueAnnotation.value());
                    }

                    if (f.getType() == Boolean.class) {
                        String[] strings = value.split(valueAnnotation.delimiter());
                        Boolean b = null;
                        for (String string : strings) {
                            String s = string.toLowerCase();
                            if (s.equals("true") || s.equals("false")) {
                                b = Boolean.parseBoolean(s);
                                break;
                            }
                        }
                        f.setAccessible(true);
                        f.set(res, b);
                        f.setAccessible(false);
                    } else if (f.getType() == boolean.class) {
                        String[] strings = value.split(valueAnnotation.delimiter());
                        boolean b = false;
                        for (String string : strings) {
                            String s = string.toLowerCase();
                            if (s.equals("true") || s.equals("false")) {
                                b = Boolean.parseBoolean(s);
                                break;
                            }
                        }
                        f.setAccessible(true);
                        f.set(res, b);
                        f.setAccessible(false);
                    } else if (f.getType() == Integer.class) {
                        String[] strings = value.split(valueAnnotation.delimiter());
                        Integer num = null;
                        for (String s : strings) {
                            if (isIntPattern(s)) {
                                num = Integer.parseInt(s);
                                break;
                            }
                        }
                        f.setAccessible(true);
                        f.set(res, num);
                        f.setAccessible(false);
                    } else if (f.getType() == int.class) {
                        String[] strings = value.split(valueAnnotation.delimiter());
                        int num = 0;
                        for (String s : strings) {
                            if (isIntPattern(s)) {
                                num = Integer.parseInt(s);
                                break;
                            }
                        }
                        f.setAccessible(true);
                        f.set(res, num);
                        f.setAccessible(false);
                    } else if (f.getType() == String.class) {
                        String[] strings = value.split(valueAnnotation.delimiter());
                        String string = null;
                        for (String s : strings) {
                            if (s != null) {
                                string = s;
                                break;
                            }
                        }
                        f.setAccessible(true);
                        f.set(res, string);
                        f.setAccessible(false);
                    } else if (f.getType() == boolean[].class) {
                        value = deleteEdge(value, '[', ']');
                        String[] strings = value.split(valueAnnotation.delimiter());
                        ArrayList<Boolean> booleans = new ArrayList<>();
                        if(!value.equals("[]") && !value.equals("")) {
                            for (String string : strings) {
                                String b = string.toLowerCase();
                                if (b.equals("true") || b.equals("false")) {
                                    booleans.add(Boolean.parseBoolean(b));
                                }
                            }
                        }
                        boolean[] boolean_list = new boolean[booleans.size()];
                        for (int i = 0; i < booleans.size(); i++) {
                            boolean_list[i] = booleans.get(i);
                        }
                        f.setAccessible(true);
                        f.set(res, boolean_list);
                        f.setAccessible(false);
                    } else if (f.getType() == int[].class) {
                        value = deleteEdge(value, '[', ']');
                        String[] strings = value.split(valueAnnotation.delimiter());
                        ArrayList<Integer> ints = new ArrayList<>();
                        if(!value.equals("[]") && !value.equals("")) {
                            for (String s : strings) {
                                if (isIntPattern(s)) {
                                    int num = Integer.parseInt(s);
                                    ints.add(num);
                                }
                            }
                        }
                        int[] int_list = new int[ints.size()];
                        for (int i = 0; i < ints.size(); i++) {
                            int_list[i] = ints.get(i);
                        }
                        f.setAccessible(true);
                        f.set(res, int_list);
                        f.setAccessible(false);
                    } else if (f.getType() == String[].class) {
                        value = deleteEdge(value, '[', ']');
                        String[] strings = value.split(valueAnnotation.delimiter());
                        ArrayList<String> string_list = new ArrayList<>();
                        if(!value.equals("[]") && !value.equals("")) {
                            for (String s : strings) {
                                if (s != null) {
                                    string_list.add(s);
                                }
                            }
                        }
                        String[] res_list = new String[string_list.size()];
                        for (int i = 0; i < string_list.size(); i++) {
                            res_list[i] = string_list.get(i);
                        }
                        f.setAccessible(true);
                        f.set(res, res_list);
                        f.setAccessible(false);
                    } else if (f.getType() == List.class) {
                        Class<?> elementType = Class.forName(((ParameterizedType) f.getGenericType())
                                .getActualTypeArguments()[0].getTypeName());
                        value = deleteEdge(value, '[', ']');
                        List<Object> list = new ArrayList<>();
                        if(!value.equals("[]") && !value.equals("")) {
                            String[] elements = value.split(valueAnnotation.delimiter());
                            for (String element : elements) {
                                if (isLegal(elementType, element)) {
                                    list.add(convert(elementType, element));
                                }
                            }
                        }
                        f.setAccessible(true);
                        f.set(res, list);
                        f.setAccessible(false);

                    } else if (f.getType() == Set.class) {
                        Class<?> elementType = Class.forName(((ParameterizedType) f.getGenericType())
                                .getActualTypeArguments()[0].getTypeName());
                        value = deleteEdge(value, '[', ']');
                        String[] elements = value.split(valueAnnotation.delimiter());
                        Set<Object> set = new HashSet<>();
                        if(!value.equals("[]") && !value.equals("")) {
                            for (String element : elements) {
                                if (isLegal(elementType, element)) {
                                    set.add(convert(elementType, element));
                                }
                            }
                        }
                        f.setAccessible(true);
                        f.set(res, set);
                        f.setAccessible(false);
                    } else if (f.getType() == Map.class) {
                        //find parameter type of Map
                        Class<?> keyType = Class.forName(((ParameterizedType) f.getGenericType())
                                .getActualTypeArguments()[0].getTypeName());
                        Class<?> valueType = Class.forName(((ParameterizedType) f.getGenericType())
                                .getActualTypeArguments()[1].getTypeName());
//              System.out.println(keyType + ":" + valueType);
                        value = deleteEdge(value, '{', '}');
                        String[] elements = value.split(valueAnnotation.delimiter());
                        Map<Object, Object> mapObj = new HashMap<>();
                        if(!value.equals("[]") && !value.equals("")) {
                            for (String element : elements) {
                                String key0 = element.split(":")[0];
                                String value0 = element.split(":")[1];
                                if (isLegal(keyType, key0) && isLegal(valueType, value0)) { //好好写一下
                                    mapObj.put(convert(keyType, key0), convert(valueType, value0));
                                }
//                System.out.printf("%s:%s%n", key0, mapObj.get(key0));
                            }
                        }
                        f.setAccessible(true);
                        f.set(res, mapObj);
                        f.setAccessible(false);
                    }
                } else if (f.getAnnotation(Inject.class) != null) {
                    Object fieldObj = createInstance(f.getType());
                    f.setAccessible(true);
                    f.set(res, fieldObj);
                    f.setAccessible(false);
                }
            }

        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException | ClassNotFoundException e) {
            e.printStackTrace();
        }


        /*
        找实现类：
        1. 找到implClazzName
        2. Class.forName(implClazzName) 获取Class类型 implClazz
        确定构造方法：
        1. 通过implClazz找所有的构造方法, 带有@Inject的构造方法
        或default构造方法（递归的终止条件） getDeclaredConstructor().newInstance();
        构建对象：
        1. 找构造方法里所有的parameter
            如果没有@value，肯定是用户自定义类 递归 调用newInstance(classType)
            如果有@value, 即肯定是文档规定的几种类型之一，确定数据类型，通过配置文件创建实例（local-value.properties）
        2. 对每个parameter 分别注入值，放在一个Object[]中
        3. 调用构造方法构建实例：
            e.g. Object[] objects2 = {bObject, cObject, parameterObject1, parameterObject2};
                 AA aObject2 = (AA) constructor.newInstance(objects2);
        在已有对象中注入属性：
        1. 找当前类里对所有属性 getDeclaredFields()
        2. 在属性中找所有带有@value注解的属性 （还是上面说的文档提到的几种类型）
               在aObject中 listField属性 fieldObj值
               e.g. listField.setAccessible(true);
                    listField.set(aObject, fieldObj);
                    listField.setAccessible(false);
        3. 在属性中找所有带有@inject注解的属性 （还是上面说的文档提到的几种类型）
           递归newInstance()

         */
        return (T) res;
    }

    public boolean isLegal(Class<?> type, String s) {
        if (type == boolean.class || type == Boolean.class) {
            s = s.toLowerCase();
            return s.equals("true") || s.equals("false");
        } else if (type == int.class || type == Integer.class) {
            return isIntPattern(s);
        } else if (type == String.class) {
            return s != null;
        }
        return false;
    }

    public Object convert(Class<?> type, String s) {
        Object res = null;
        if (type == boolean.class || type == Boolean.class) {
            res = Boolean.parseBoolean(s);
        } else if (type == int.class || type == Integer.class) {
            res = Integer.parseInt(s);
        } else if (type == String.class) {
            res = s;
        }
        return res;
    }

    public boolean isIntPattern(String s) {
        boolean re = false;
        String pattern = "[0-9]+";
        boolean con1 = Pattern.matches(pattern, s);
        if (con1) {
            if (s.length() <= 10) {
                long tmp = Long.parseLong(s);
                if (tmp >= -2147483648L && tmp <= 2147483647L) {
                    re = true;
                }
            }
        }
        return re;
    }

    public String deleteEdge(String value, char c1, char c2) {
        if (value.length() > 2) {
            if (value.charAt(0) == c1) {
                value = value.substring(1);
            }
            if (value.charAt(value.length() - 1) == c2) {
                value = value.substring(0, value.length() - 1);
            }
        }
        return value;
    }
}
