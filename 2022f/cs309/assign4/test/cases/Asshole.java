package cases;

import dependency_injection.Value;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class Asshole {

    @Value(value = "[1,2]")
    int a;

    @Value(value = "[a,1]")
    String b;
}
