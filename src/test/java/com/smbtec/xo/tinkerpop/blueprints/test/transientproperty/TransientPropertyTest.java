package com.smbtec.xo.tinkerpop.blueprints.test.transientproperty;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.smbtec.xo.tinkerpop.blueprints.test.AbstractTinkerPopXOManagerTest;
import com.smbtec.xo.tinkerpop.blueprints.test.transientproperty.composite.A;

@RunWith(Parameterized.class)
public class TransientPropertyTest extends AbstractTinkerPopXOManagerTest {

    public TransientPropertyTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class);
    }

    @Test
    public void transientProperty() {
        XOManager xoManager = getXoManager();
        A a = xoManager.create(A.class);
        a.setValue("persistent value");
        a.setTransientValue("transient value");
        xoManager.flush();
        assertThat(a.getValue(), equalTo("persistent value"));
        assertThat(a.getTransientValue(), equalTo("transient value"));
        xoManager.flush();
        closeXOmanager();
        xoManager = getXoManager();
        A result = xoManager.find(A.class, "persistent value").getSingleResult();
        assertThat(result.getValue(), equalTo("persistent value"));
        assertThat(result.getTransientValue(), nullValue());
    }

}
