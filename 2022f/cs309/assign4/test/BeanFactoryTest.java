import cases.Asshole;
import cases.CtorWithGType;
import cases.EmptyStr;
import dependency_injection.BeanFactory;
import dependency_injection.BeanFactoryImpl;
import dependency_injection.BeanFactoryImpl2;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;


class BeanFactoryTest {

    BeanFactory beanFactory;

    @BeforeEach
    void setup() {
//        beanFactory = new BeanFactoryImpl();
        beanFactory = new BeanFactoryImpl2();
        beanFactory.loadInjectProperties(new File("local-inject.properties"));
        beanFactory.loadValueProperties(new File("local-value.properties"));
    }

    @Test
    void testFieldInjection() {
        assertThat(beanFactory.createInstance(EmptyStr.class)).isEqualTo(
                EmptyStr.builder()
                        .a("[]")
                        .b("")
                        .as(new String[0])
                        .ass(new String[0])
                        .aSet1(Set.of())
                        .aSet2(Set.of())
                        .map2(Map.of())
                        .map3(Map.of("", ""))
                        .aList(List.of())
                        .bList(List.of("", " "))
                        .map(Map.of(1, ""))
                        .build()
        );
    }

    @Test
    void testAsshole() {
        assertThat(beanFactory.createInstance(Asshole.class)).isEqualTo(
                Asshole.builder()
                        .a(0)
                        .b("[a")
                        .build()
        );
    }

    @Test
    void testCtorWithGType() {
        assertThat(beanFactory.createInstance(CtorWithGType.class)).isEqualTo(
                CtorWithGType.builder()
                        .asshole(beanFactory.createInstance(Asshole.class))
                        .bs(List.of(true, false))
                        .es(beanFactory.createInstance(EmptyStr.class))
                        .i(42)
                        .build()
        );
    }
}
