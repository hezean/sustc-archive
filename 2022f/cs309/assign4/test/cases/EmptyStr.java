package cases;

import dependency_injection.Value;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;
import java.util.Set;


@Data
@SuperBuilder
@NoArgsConstructor
public class EmptyStr {

    @Value("[]")
    private String a;

    @Value("")
    private String b;

    @Value("")
    private String[] as;

    @Value("[]")
    private String[] ass;

    @Value("[]")
    private List<String> aList;

    @Value("[]")
    private Set<String> aSet1;

    @Value("{}")
    private Set<String> aSet2;

    @Value("[, ,]")
    private List<String> bList;

    @Value(value = "{1:}")
    private Map<Integer, String> map;

    @Value(value = "{:}")
    private Map<Integer, String> map2;

    @Value(value = "{:}")
    private Map<String, String> map3;
}
