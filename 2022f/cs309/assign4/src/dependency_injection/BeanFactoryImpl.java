package dependency_injection;

import java.io.*;
import java.lang.reflect.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@SuppressWarnings({"unchecked", "java:S112"})
public class BeanFactoryImpl implements BeanFactory {

    private Properties injections;

    private Properties values;

    @FunctionalInterface
    @SuppressWarnings("AlibabaAbstractMethodOrInterfaceMethodMustUseJavadoc")
    private interface Decoder<T> {
        Optional<T> valueOf(String val, String delim, Type type);
    }

    private static final Map<Type, Decoder<?>> DECODERS = new HashMap<>(9);

    private static final Pattern LIST_REPR = Pattern.compile("\\[(?<ctx>.*)]");

    private static final Pattern SET_MAP_REPR = Pattern.compile("\\{(?<ctx>.*)}");

    private static final Pattern ENTRY_REPR = Pattern.compile("(?<k>.*):(?<v>.*)");

    private static final Decoder<?> ARRAY_CVT = (val, delim, type) -> {
        Matcher matcher = LIST_REPR.matcher(val);
        if (!matcher.find()) {
            return Optional.of(Array.newInstance(((Class<?>) type).getComponentType(), 0));
        }
        Object[] value = Arrays.stream(matcher.group("ctx").split(delim))
                .map(e -> DECODERS.get(((Class<?>) type).getComponentType()).valueOf(e, null, null))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toArray();
        // FIXME: bad practice
        if (int.class.equals(((Class<?>) type).getComponentType())) {
            int[] res = new int[value.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = (Integer) value[i];
            }
            return Optional.of(res);
        }
        if (boolean.class.equals(((Class<?>) type).getComponentType())) {
            boolean[] res = new boolean[value.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = (Boolean) value[i];
            }
            return Optional.of(res);
        }
        if (String.class.equals(((Class<?>) type).getComponentType())) {
            String[] res = new String[value.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = (String) value[i];
            }
            if (Arrays.equals(new String[]{""}, res)) {
                return Optional.of(new String[0]);
            }
            return Optional.of(res);
        }
        return Optional.of(value);
    };

    private static final Decoder<List<?>> LIST_CVT = (val, delim, type) -> {
        Matcher matcher = LIST_REPR.matcher(val);
        if (!matcher.find()) {
            return Optional.of(List.of());
        }
        Type eleType = ((ParameterizedType) type).getActualTypeArguments()[0];
        String[] tokens = matcher.group("ctx").split(delim);
        if (String.class.equals(eleType) && Arrays.equals(new String[]{""}, tokens)) {
            return Optional.of(List.of());
        }
        return Optional.of(Arrays.stream(tokens)
                .map(i -> DECODERS.get(eleType).valueOf(i, null, null))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList());
    };

    private static final Decoder<Set<?>> SET_CVT = (val, delim, type) -> {
        Matcher matcher = SET_MAP_REPR.matcher(val);
        if (!matcher.find()) {
            return Optional.of(Set.of());
        }
        Type eleType = ((ParameterizedType) type).getActualTypeArguments()[0];
        String[] tokens = matcher.group("ctx").split(delim);
        if (String.class.equals(eleType) && Arrays.equals(new String[]{""}, tokens)) {
            return Optional.of(Set.of());
        }
        return Optional.of(Arrays.stream(matcher.group("ctx").split(delim))
                .map(i -> DECODERS.get(eleType).valueOf(i, null, null))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet()));
    };

    private static final Decoder<Map<?, ?>> MAP_CVT = (val, delim, type) -> {
        Matcher matcher = SET_MAP_REPR.matcher(val);
        if (!matcher.find()) {
            return Optional.of(Map.of());
        }
        Type keyType = ((ParameterizedType) type).getActualTypeArguments()[0];
        Type valType = ((ParameterizedType) type).getActualTypeArguments()[1];

        return Optional.of(Arrays.stream(matcher.group("ctx").split(delim))
                .map(entry -> {
                    Matcher kvMatch = ENTRY_REPR.matcher(entry);
                    if (!kvMatch.find()) {
                        return Optional.empty();
                    }
                    Optional<?> key = DECODERS.get(keyType).valueOf(kvMatch.group("k"), null, null);
                    Optional<?> value = DECODERS.get(valType).valueOf(kvMatch.group("v"), null, null);
                    if (key.isEmpty() || value.isEmpty()) {
                        return Optional.empty();
                    }
                    return Optional.of(List.of(key.get(), value.get()));
                })
                .filter(Optional::isPresent)
                .collect(Collectors.toMap(
                        e -> ((List<?>) e.get()).get(0),
                        e -> ((List<?>) e.get()).get(1),
                        (e0, e1) -> e1
                )));
    };

    static {

        DECODERS.put(int.class, (Decoder<Integer>) (val, delim, type) -> {
            String[] tokens = tokensFrom(val, delim);
            for (String tok : tokens) {
                try {
                    return Optional.of(Integer.parseInt(tok));
                } catch (Exception ignored) {
                    // skip invalid value
                }
            }
            return Optional.empty();
        });

        DECODERS.put(Integer.class, DECODERS.get(int.class));

        DECODERS.put(boolean.class, (Decoder<Boolean>) (val, delim, type) -> {
            String[] tokens = tokensFrom(val, delim);
            for (String tok : tokens) {
                if ("true".equalsIgnoreCase(tok)) {
                    return Optional.of(true);
                }
                if ("false".equalsIgnoreCase(tok)) {
                    return Optional.of(false);
                }
            }
            return Optional.empty();
        });

        DECODERS.put(Boolean.class, DECODERS.get(boolean.class));

        DECODERS.put(String.class, (Decoder<String>) (val, delim, type) -> {
            String[] tokens = tokensFrom(val, delim);
            if (tokens.length > 0) {
                return Optional.of(tokens[0]);
            }
            return Optional.empty();
        });

        DECODERS.put(int[].class, ARRAY_CVT);

        DECODERS.put(boolean[].class, ARRAY_CVT);

        DECODERS.put(String[].class, ARRAY_CVT);

        DECODERS.put(List.class, LIST_CVT);

        DECODERS.put(Set.class, SET_CVT);

        DECODERS.put(Map.class, MAP_CVT);
    }

    private static String[] tokensFrom(String val, String delim) {
        if (Objects.isNull(delim)) {
            return new String[]{val};
        }
        return Objects.requireNonNullElse(val, "").split(delim);
    }

    @Override
    public void loadInjectProperties(File file) {
        injections = new Properties();
        try (InputStream is = new FileInputStream(file)) {
            injections.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void loadValueProperties(File file) {
        values = new Properties();
        try (InputStream is = new FileInputStream(file)) {
            values.load(is);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    @SuppressWarnings("java:S3011")
    public <T> T createInstance(Class<T> clazz) {
        if (clazz.isPrimitive()) {
            return (T) Array.get(Array.newInstance(clazz, 1), 0);
        }
        clazz = (Class<T>) implOf(clazz);
        Constructor<T> ctor = ctorOf(clazz);
        Parameter[] params = ctor.getParameters();
        Object[] paramValues = new Object[ctor.getParameterCount()];
        for (int i = 0; i < params.length; i++) {
            paramValues[i] = valueFor(params[i]);
        }
        T instance;
        try {
            ctor.trySetAccessible();
            instance = ctor.newInstance(paramValues);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        } finally {
            ctor.setAccessible(false);
        }

        // fields in super class should be injected in the ctor  // FIXME?
        for (Field field : clazz.getDeclaredFields()) {
            Object injectValue = null;
            if (field.isAnnotationPresent(Value.class)) {
                injectValue = valueFor(field);
            } else if (field.isAnnotationPresent(Inject.class)) {
                injectValue = createInstance(field.getType());
            }
            if (Objects.nonNull(injectValue)) {
                try {
                    field.trySetAccessible();
                    field.set(instance, valueFor(field));
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    throw new RuntimeException(e);
                } finally {
                    field.setAccessible(false);
                }
            }
        }
        return instance;
    }

    private <T> Class<? extends T> implOf(Class<T> clazz) {
        try {
            return (Class<? extends T>) Class.forName(
                    injections.getProperty(clazz.getName(), clazz.getName()));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private static <T> Constructor<T> ctorOf(Class<T> clazz) {
        return (Constructor<T>) Arrays.stream(clazz.getDeclaredConstructors())
                .filter(ctor -> ctor.isAnnotationPresent(Inject.class))
                .findFirst()
                .orElseGet(() -> {
                    try {
                        return clazz.getDeclaredConstructor();
                    } catch (NoSuchMethodException ex) {
                        throw new RuntimeException(ex);
                    }
                });
    }

    @SuppressWarnings("java:S3655")
    private <T> T valueFor(AnnotatedElement element) {
        Type rType;
        Type gType;
        if (element instanceof Parameter param) {
            gType = param.getParameterizedType();
            rType = param.getType();
        } else if (element instanceof Field field) {
            gType = field.getGenericType();
            rType = field.getType();
        } else {
            throw new IllegalArgumentException("element is not a field or param");
        }

        Optional<Value> anno = Arrays.stream(element.getAnnotations())
                .filter(a -> Value.class.equals(a.annotationType()))
                .map(Value.class::cast)
                .findFirst();
        if (anno.isPresent()) {
            String valueToken = values.getProperty(anno.get().value(), anno.get().value());
            Optional<T> res = ((Decoder<T>) DECODERS.get(rType)).valueOf(valueToken, anno.get().delimiter(), gType);
            if (res.isPresent()) {
                return res.get();
            }
        }
        return createInstance((Class<? extends T>) gType);
    }
}
