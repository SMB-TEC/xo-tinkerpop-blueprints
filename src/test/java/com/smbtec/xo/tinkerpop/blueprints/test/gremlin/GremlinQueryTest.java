package com.smbtec.xo.tinkerpop.blueprints.test.gremlin;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import java.net.URISyntaxException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.collection.IsIterableContainingInAnyOrder;
import org.hamcrest.collection.IsIterableWithSize;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.buschmais.xo.api.Example;
import com.buschmais.xo.api.Query.Result;
import com.buschmais.xo.api.XOException;
import com.buschmais.xo.api.XOManager;
import com.buschmais.xo.api.bootstrap.XOUnit;
import com.smbtec.xo.tinkerpop.blueprints.api.TinkerPopDatastoreSession;
import com.smbtec.xo.tinkerpop.blueprints.test.AbstractTinkerPopXOManagerTest;
import com.smbtec.xo.tinkerpop.blueprints.test.gremlin.composite.Person;
import com.smbtec.xo.tinkerpop.blueprints.test.gremlin.composite.SimpleNamed;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.KeyIndexableGraph;

@RunWith(Parameterized.class)
public class GremlinQueryTest extends AbstractTinkerPopXOManagerTest {

    private Person john;
    private Person mary;
    private Person jDow;

    public GremlinQueryTest(XOUnit xoUnit) {
        super(xoUnit);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> getXOUnits() throws URISyntaxException {
        return xoUnits(Person.class);
    }

    @Before
    public void populate() {
        XOManager xoManager = getXoManager();
        Graph graph = xoManager.getDatastoreSession(TinkerPopDatastoreSession.class).getGraph();
        ((KeyIndexableGraph) graph).createKeyIndex("firstname", com.tinkerpop.blueprints.Vertex.class);
        ((KeyIndexableGraph) graph).createKeyIndex("lastname", com.tinkerpop.blueprints.Vertex.class);
        john = xoManager.create(new Example<Person>() {

            @Override
            public void prepare(Person example) {
                example.setFirstname("John");
                example.setLastname("Doe");
                example.setAge(25);
            }

        }, Person.class);
        mary = xoManager.create(new Example<Person>() {

            @Override
            public void prepare(Person example) {
                example.setFirstname("Mary");
                example.setLastname("Doe");
                example.setAge(20);
            }

        }, Person.class);

        jDow = xoManager.create(new Example<Person>() {

            @Override
            public void prepare(Person example) {
                example.setFirstname("Jon");
                example.setLastname("Dow");
                example.setAge(31);
            }

        }, Person.class);
        john.getFriends().add(mary);
        john.getFriends().add(jDow);
        xoManager.flush();
    }

    @Test
    public void selectVertex() {
        Person person = getXoManager().createQuery("g.v(0)", Person.class).execute().getSingleResult();
        assertThat(person, equalTo(john));
    }

    @Test
    public void selectVertices() {
        Result<Person> result = getXoManager().createQuery("g.V[0..10]", Person.class).execute();
        assertThat(result, IsIterableContainingInAnyOrder.<Person> containsInAnyOrder(john, mary, jDow));
    }

    @Test
    public void selectAttributeOfVertex() {
        String firstName = getXoManager().createQuery("g.v(0).firstname", String.class).execute().getSingleResult();
        assertThat(firstName, equalTo("John"));
        Result<String> result = getXoManager().createQuery("g.v(0).outE('friends').inV().firstname", String.class)
                .execute();
        assertThat(result, IsIterableWithSize.<String> iterableWithSize(2));
    }

    @Test
    public void selectAttributeOfVertices() {
        Result<String> result = getXoManager().createQuery("g.V[0..10].firstname", String.class).execute();
        assertThat(result, IsIterableContainingInAnyOrder.<String> containsInAnyOrder("John", "Mary", "Jon"));
    }

    @Test
    public void selectVertexByAttribute() {
        Person person = getXoManager().createQuery("g.V('firstname','John')", Person.class).execute().getSingleResult();
        assertThat(person, equalTo(john));
    }

    @Test
    public void countVertices() {
        XOManager xoManager = getXoManager();
        Long count = xoManager.createQuery("g.V('firstname','John').count()", Long.class).execute().getSingleResult();
        assertThat(count, equalTo(1L));
    }

    @Test
    public void countEdges() {
        XOManager xoManager = getXoManager();
        Long count = xoManager.createQuery("g.v(0).outE().count()", Long.class).execute().getSingleResult();
        assertThat(count, equalTo(2L));
    }

    @Test(expected = XOException.class)
    public void inOutEdges() {
        // this query must fail, since we do not have a typed relationship
        getXoManager().createQuery("g.v(0).outE('friends')").execute().getSingleResult();
    }

    @Test
    public void ages() {
        Result<Long> result = getXoManager().createQuery("g.V('lastname','Doe').outE('friends').inV().age", Long.class)
                .execute();
        assertThat(result, IsIterableWithSize.<Long> iterableWithSize(2));
    }

    @Test
    public void parameterizedQuery() {
        XOManager xoManager = getXoManager();
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("me", 0);
        String result = xoManager.createQuery("g.v(me).firstname", String.class).withParameters(parameters).execute()
                .getSingleResult();
        assertThat(result, equalTo("John"));
        result = xoManager.createQuery("g.V.filter(){it.firstname==name}.lastname", String.class)
                .withParameter("name", "John").execute().getSingleResult();
        assertThat(result, equalTo("Doe"));
        result = xoManager.createQuery("g.V('firstname',name).lastname", String.class).withParameter("name", "John")
                .execute().getSingleResult();
        assertThat(result, equalTo("Doe"));
    }

    @Test
    public void mapQuery() {
        XOManager xoManager = getXoManager();
        Result<SimpleNamed> result = xoManager.createQuery(SimpleNamed.class).execute();
        assertThat(result, IsIterableWithSize.<SimpleNamed> iterableWithSize(2));
    }

    @Test
    public void primitiveReturnTypes() {
        XOManager xoManager = getXoManager();
        Long result = xoManager.createQuery("2 + 1", Long.class).execute().getSingleResult();
        assertThat(result, is(3L));
    }
}
