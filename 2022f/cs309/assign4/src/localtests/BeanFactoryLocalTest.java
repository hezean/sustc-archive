package localtests;

import dependency_injection.BeanFactory;
import dependency_injection.BeanFactoryImpl;
import testclass.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;

import static org.junit.jupiter.api.Assertions.*;

public class BeanFactoryLocalTest {

    private BeanFactory beanFactory;

    @BeforeEach
    public void setup() {
        this.beanFactory = new BeanFactoryImpl();
        beanFactory.loadInjectProperties(new File("local-inject.properties"));
        beanFactory.loadValueProperties(new File("local-value.properties"));
    }

    @Test
    public void testResolveWithNoDependency() {
        A instance = beanFactory.createInstance(A.class);
        assertNotNull(instance);
    }

    @Test
    public void testFieldWithValue() {
        D instance = beanFactory.createInstance(D.class);
        assertEquals(10, instance.getVal());
    }

    @Test
    public void testWithConstructorDependency() {
        B instance = beanFactory.createInstance(B.class);
        assertNotNull(instance);
        assertNotNull(instance.getCDep());
        assertNotNull(instance.getDDep());
        assertEquals(instance.getDDep().getVal(), 10);
    }

    @Test
    public void testWithAnnotationDependency() {
        G instance = beanFactory.createInstance(G.class);
        assertNotNull(instance);
        assertNotNull(instance.getCDep());
        assertNotNull(instance.getDDep());
        assertEquals(instance.getDDep().getVal(), 10);
    }

    @Test
    public void testPrimitiveValueInjection() {
        H instance = beanFactory.createInstance(H.class);
        assertNotNull(instance);
        assertEquals(instance.getHomo(), 114514);
        assertFalse(instance.isMagic());
        assertEquals(instance.getLyric(), "never gonna give you up");
    }

    @Test
    public void testListInjection() {
        I instance = beanFactory.createInstance(I.class);
        assertNotNull(instance);
        assertEquals(3, instance.getRogue3Software().size());
        assertEquals(1, instance.getMikuSet().size());
        assertEquals(1, instance.getSwindle().size());
        assertEquals(1, instance.getWork().size());
    }

    @Test
    public void testImplTypeForInterface() {
        E instance = beanFactory.createInstance(E.class);
        assertNotNull(instance);
        assertTrue(instance instanceof EImpl);
    }

    @Test
    public void testImplTypeForAbstractClass() {
        F instance = beanFactory.createInstance(F.class);
        assertNotNull(instance);
        assertTrue(instance instanceof FEnhanced);
    }

    @Test
    public void testDependencyImpl() {
        K instance = beanFactory.createInstance(K.class);
        assertNotNull(instance);
        assertNotNull(instance.getEDep());
        assertNotNull(instance.getFDep());
        assertTrue(instance.getEDep() instanceof EImpl);
        assertTrue(instance.getFDep() instanceof FEnhanced);
    }

    @Test
    public void testConstructorInject() {
        L instance = beanFactory.createInstance(L.class);
        assertNotNull(instance);
        assertNotNull(instance.getBDep());
        assertTrue(instance.isBool());
    }


    @Test
    public void testPrimitiveArrayValues() {
        J instance = beanFactory.createInstance(J.class);
        assertNotNull(instance);
        assertTrue(instance instanceof JImpl);
    }

    @Test
    public void testWrappedPrimitiveProperties() {
        J instance = beanFactory.createInstance(J.class);
        assertNotNull(instance);
        assertTrue(instance instanceof JImpl);
        assertEquals(34, instance.getInt());
    }

    @Test
    public void testPropertiesList() {
        J instance = beanFactory.createInstance(J.class);
        assertNotNull(instance);
        assertTrue(instance instanceof JImpl);
        assertEquals(((JImpl) instance).getList().toString(), "[114514, 1919810, 33445566]");
        assertEquals(((JImpl) instance).getMap().get(44), "55");
        assertEquals(((JImpl) instance).getBoolArray().length, 3);
        assertEquals(((JImpl) instance).getMap().size(), 2);
    }

    @Test
    public void testParameterInjection() {
        M instance = beanFactory.createInstance(M.class);
        assertTrue(instance.getJ() instanceof JImpl);
        assertEquals(3, ((JImpl) instance.getJ()).getList().size());
        assertEquals(2, instance.getList().size());
        assertEquals("notest", instance.getS());
    }
}
