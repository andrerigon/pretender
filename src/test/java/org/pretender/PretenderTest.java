package org.pretender;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.pretender.util.Benckmarked;
import org.pretender.util.Client;
import org.pretender.util.ConcretePerson;
import org.pretender.util.Person;

import java.util.*;

import static org.junit.Assert.*;

public class PretenderTest extends SimpleBenchmark {

    @Param({"10", "100", "1000"})
    private int length;

    final Date NOW = new Date();

    String json = new Gson().toJson(getPerson());


    @Benckmarked
    public void time_abstract_lazy_deserialization(int count) {
        runLoop(count, lazyDeserializerFor(Person.class), Person.class);
    }

    @Benckmarked
    public void time_abstract_eager_deserialization(int count) {
        runLoop(count, eagerDeserializerFor(Person.class), Person.class);
    }

    @Benckmarked
    public void time_concrete_deserialization(int count) {
        runLoop(count, new Gson(), ConcretePerson.class);
    }

    @Test
    public void should_deserialize_a_list_of_objects() {

        final List<Person> list = listFromJson(personListAsJson());

        Person p = list.get(0);
        assertEquals("maria", p.nomeMaisLegal());
        assertEquals(Integer.valueOf(12), p.age());

        p = list.get(1);
        assertEquals("jose", p.nomeMaisLegal());
        assertEquals(Integer.valueOf(125), p.age());
    }

    @Test
    public void should_deserialize_map() {
        Map<String, Person> map = mapFromJson(personMapAsJson());
        Person p = map.get("maria");
        assertEquals("maria", p.nomeMaisLegal());
    }

    @Test
    public void should_deserialize_embbebed_objects() {
        Client client = clientFromJson(clientAsJson());
        assertPersonOK(client.person());
        assertEquals(NOW.toString(), client.clientSince().toString());
    }

    private Client clientFromJson(String clientJson) {
        return lazyDeserializerFor(Person.class)
                    .fromJson(clientJson, Client.class);
    }

    private String clientAsJson() {
        Map<String, Object> clientMap = new HashMap<String, Object>();
        clientMap.put("person", getPerson());
        clientMap.put("clientSince", NOW);

        return new Gson().toJson(clientMap);
    }

    @Test
    public void shouldInvokeHashCode() {
        Gson gson = lazyDeserializerFor(Person.class);
        Person p = gson.fromJson(json, Person.class);
        assertNotNull(p.hashCode());
    }

    @Test
    public void runBenchmark() {
        com.google.caliper.Runner.main(this.getClass());
    }

    private void assertPersonOK(Person p) {
        Person b = p.buddies().get("maria");
        assertEquals("andre", p.nomeMaisLegal());
        assertEquals(Integer.valueOf(5), p.age());
        assertEquals("maria", b.nomeMaisLegal());
        assertEquals(Integer.valueOf(12), Integer.valueOf(b.age()));
        assertArrayEquals(new Integer[]{3, 4}, p.list().toArray());
        assertEquals(NOW.toString(), p.birth().toString());
    }

    private Person getPerson() {
        ConcretePerson person = new ConcretePerson();
        person.name = "andre";
        person.age = 5;
        person.birth = NOW;
        person.list = Arrays.asList(3, 4);

        ConcretePerson p = new ConcretePerson();
        p.age = (12);
        p.name = ("maria");
        Map<String, ConcretePerson> map = new HashMap<String, ConcretePerson>();
        map.put("maria", p);
        person.buddies = map;

        return person;
    }

    private void runLoop(int count, Gson gson, Class<? extends Person> clazz) {
        for (int i = 0; i < count; i++) {
            Person p = gson.fromJson(json, clazz);
            assertPersonOK(p);
        }
    }

    private Gson eagerDeserializerFor(Class<?>... classes) {
        return Pretender.gsonEagerDeserializerFor(classes);
    }

    private Gson lazyDeserializerFor(Class<?>... classes) {
        return Pretender.gsonLazyDeserializerFor(classes);
    }

    protected String personListAsJson() {
        return new Gson().toJson(personList(), new TypeToken<List<ConcretePerson>>() {
        }.getType());
    }

    private List<Person> listFromJson(String listjson) {
        Gson gson = lazyDeserializerFor(Person.class);
        return gson.<List<Person>>fromJson(listjson, new TypeToken<List<Person>>() {
        }.getType());
    }

    private ArrayList<Person> personList() {
        return new ArrayList<Person>() {
            {
                ConcretePerson p = new ConcretePerson();
                p.age = (12);
                p.name = ("maria");
                add(p);

                ConcretePerson p2 = new ConcretePerson();
                p2.age = (125);
                p2.name = ("jose");
                add(p2);
            }
        };
    }


    private Map<String, Person> mapFromJson(String mapJson) {
        Gson gson = lazyDeserializerFor(Person.class);
        return gson.<Map<String, Person>>fromJson(mapJson, new TypeToken<Map<String, Person>>() {
        }.getType());
    }

    private String personMapAsJson() {
        return new Gson().toJson(new HashMap<String, Person>() {
            {
                ConcretePerson p = new ConcretePerson();
                p.age = (12);
                p.name = ("maria");
                put("maria", p);
            }
        }, new TypeToken<Map<String, ConcretePerson>>() {
        }.getType());
    }

}
