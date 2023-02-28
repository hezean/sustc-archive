## I. Classes

### 2. Inject

#### 2.1 On Fields: `ElementType.FIELD`

#### 2.2 On Constructor: `ElementType.CONSTRUCTOR`



### 3. Value


ii. For the array type, you should **ONLY** select those values fit the type requirements into the array; 

Example 2 for ii:
```java
public class Example2 {
    @Value(value = "[10086,10010,10000,baadfood]")
    private int[] mobiles; // mobiles wil be an array of length 3, whose values are 10086, 10010 and 10000.
    // baadfood is not a decimal number, so we ignore it.

    @Value(value = "[FaLSe-0-1-Yes-No-tRuE]", delimiter = "-")
    private boolean[] yesOrNo; // yesOrNo wil be an array of length 2, whose values are false and true.
    // 0, 1, Yes, No are not boolean Strings, so they are omitted.
    // FaLSe and tRuE are equal to boolean values false and true regardless of cases.

    @Value(value = "[never gonna give you up]", delimiter = " ")
    private String[] swindle; // swindle will be an array of length 5
    // Its values are "never", "gonna", "give", "you", "up"
}
```

iii. For the generic type, you should **ONLY** select those values fit the type requirements into the container and consider the characteristic of the container type(i.e. Set should **NOT** contain duplicate values, and Map should **NOT** contain duplicate keys). 

Example 3 for iii: 
```java
public class Example3 {
    @Value(value = "[2345,360,kingsoft,]")
    private List<String> rogue3Software; // rogue3Software will be a list of size 3
    // Its values are "2345", "360", "kingsoft".

    @Value(value = "{hatsune-miku-39-39-39c5bb}", delimiter = "-")
    private Set<Integer> mikuSet; // mikuSet will be a set of size 1, which only contains 39.
    // hatsune, miku, 39c5bb are not decimal numbers, so they are not allowed to be injected into the set.

    @Value(value = "{never-gonna:give-you-up,never-gonna:let-you-down}", delimiter = ",")
    private Map<String, String> swindle; //swindle will be a map with 1 entry
    // The entry's key is "never-gonna" and value is "let-you-down".
    // The value "give-you-up" is replaced by "let-you-down"

    @Value(value = "{tRUe:955,fALsE:icu,Yes:955,No:996}", delimiter = ",")
    private Map<Boolean, Integer> work; //work will be a map with 1 entry
    // The entry's key is true and value is 955.
    // icu is not an integer number, so its entry is ignored
    // Yes and No are not boolean String, so their entries are ignored as well
}
```

iv. For the array types(i.e. `boolean[]`, `int[]` and `String[]`) and the generic container types(i.e. `List<?>`, `Set<?>` and `Map<?, ?>`), the `values()` will be the String with brackets after being replaced; And for the entries of `Map<?, ?>`, the key and values are connected via `:` so the `delimiter()` of Map type will **NEVER** be `":"`, and the key or value of entries will **NEVER** contain `":"` as well. You need to use `delimiter()` to split the entry elements. 

To simplify the problem, we ensure that:

1. We will not contain the `delimiter()` String for the entry-key element in the testcases for `Map<?,?>` type injection.


#### 3.1 On Fields: ElementType.FIELD 

:point_right: If `@Value` is marked on field, in `<T> T createInstance(Class<T> clazz)` method, we not only needs create an instance for current class, but also given those fields an specified value that identified by `@Value`. If the `value()` String appears in the `value.properties`, then first replace it as defined in the properties file; Otherwise, use itself as `value()`.

For example, consider the following code:


#### 3.2 On Parameters: ElementType.PARAMETER 

If `@Value` is marked on parameters in constructor, when execute the constructor, an specific value should be given to corresponding parameters.
We ensure that, in our test cases, all parameters in the constructor that annotated by `@Inject` are either injected or annotated by `@Value`. If the `value()` String appears in the `value.properties`, then first replace it as defined in the properties file; Otherwise, use itself as `value()`.
