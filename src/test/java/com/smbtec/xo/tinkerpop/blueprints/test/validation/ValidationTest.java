package com.smbtec.xo.tinkerpop.blueprints.test.validation;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.annotation.PreUpdate;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.smbtec.xo.tinkerpop.blueprints.test.AbstractTinkerPopXOManagerTest;
import com.smbtec.xo.tinkerpop.blueprints.test.validation.composite.A;
import com.smbtec.xo.tinkerpop.blueprints.test.validation.composite.B;

@RunWith(Parameterized.class)
public class ValidationTest extends AbstractTinkerPopXOManagerTest {

    public ValidationTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(A.class, B.class);
    }

    @Test
    public void validationOnCommitAfterInsert() {
        XOManager xoManager = getXoManager();
        A a = xoManager.create(A.class);
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            xoManager.flush();
            Assert.fail("Validation must fail.");
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(2));
        B b = xoManager.create(B.class);
        a.setB(b);
        a.setName("Indiana Jones");
    }

    @Test
    public void validationOnCommitAfterQuery() {
        XOManager xoManager = getXoManager();
        B b = xoManager.create(B.class);
        for (int i = 0; i < 2; i++) {
            A a = xoManager.create(A.class);
            a.setName("Miller");
            a.setB(b);
        }
        xoManager.flush();
        closeXOmanager();
        xoManager = getXoManager();
        for (A miller : xoManager.find(A.class, "Miller")) {
            miller.setName(null);
        }
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            xoManager.flush();
            Assert.fail("Validation must fail.");
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(1));
    }

    @Test
    public void validationAfterPreUpdate() {
        XOManager xoManager = getXoManager();
        B b = xoManager.create(B.class);
        A a = xoManager.create(A.class);
        a.setB(b);
        Set<ConstraintViolation<?>> constraintViolations = null;
        try {
            xoManager.flush();
        } catch (ConstraintViolationException e) {
            constraintViolations = e.getConstraintViolations();
        }
        assertThat(constraintViolations.size(), equalTo(1));
        xoManager.registerInstanceListener(new InstanceListener());
    }

    public static final class InstanceListener {
        @PreUpdate
        public void setName(A instance) {
            instance.setName("Miller");
        }
    }
}
