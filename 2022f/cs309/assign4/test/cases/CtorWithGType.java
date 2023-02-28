package cases;

import dependency_injection.Inject;
import dependency_injection.Value;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Data
@SuperBuilder
@NoArgsConstructor
public class CtorWithGType {

    Asshole asshole;

    List<Boolean> bs;

    @Inject
    EmptyStr es;

    @Value("42")
    private int i;

    @Inject
    public CtorWithGType(Asshole asshole,
                  @Value("[1,truE,false]") List<Boolean> bs) {
        this.asshole = asshole;
        this.bs = bs;
    }
}
