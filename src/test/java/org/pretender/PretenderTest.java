package org.pretender;

import com.google.caliper.Param;
import com.google.caliper.SimpleBenchmark;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.junit.Test;
import org.pretender.annotation.BindToName;

import java.util.*;

import static org.junit.Assert.*;

public class PretenderTest extends SimpleBenchmark {

    @Param({"10", "100", "1000"})
    private int length;

    final Date NOW = new Date();

    String json = new Gson().toJson(getPerson());

    private Person getPerson() {
        ConcretePerson person = new ConcretePerson();
        person.name = "andre";
        person.age = 5;
        person.birth = NOW;
        person.list = Arrays.asList(3, 4);

        ConcretePerson p = new ConcretePerson();
        p.age = (12);
        p.name = ("maria");
        Map<String, ConcretePerson> map = new HashMap<String, PretenderTest.ConcretePerson>();
        map.put("maria", p);
        person.buddies = map;

        return person;
    }

    public void timeAbstractLazyDeserialization(int max) {
        Gson gson = Pretender.gsonLazyDeserializerFor(Person.class);

        for (int i = 0; i < max; i++) {
            Person p = gson.fromJson(json, Person.class);
            assertPersonOK(p);
        }
    }

    @Test
    public void shouldInvokeHashCode() {
        Gson gson = Pretender.gsonEagerDeserializerFor(Person.class);
        Person p = gson.fromJson(json, Person.class);
        assertNotNull(p.hashCode());
    }

    public void timeAbstractEagerDeserialization(int max) {
        Gson gson = Pretender.gsonEagerDeserializerFor(Person.class);

        for (int i = 0; i < max; i++) {
            Person p = gson.fromJson(json, Person.class);
            assertPersonOK(p);
        }
    }


    public void timeConcreteDeserialization(int max) {
        for (int i = 0; i < max; i++) {
            Person p = new Gson().fromJson(json, ConcretePerson.class);
            assertPersonOK(p);
        }
    }

    @Test
    public void should_deserialize_a_list_of_objects() {

        @SuppressWarnings("serial")
        String listjson = new Gson().toJson(new ArrayList<Person>() {
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
        }, new TypeToken<List<ConcretePerson>>() {
        }.getType());

        Gson gson = Pretender.gsonLazyDeserializerFor(Person.class);
        final List<Person> list = gson.<List<Person>>fromJson(listjson, new TypeToken<List<Person>>() {
        }.getType());
        Person p = list.get(0);
        assertEquals("maria", p.nomeMaisLegal());
        assertEquals(Integer.valueOf(12), p.age());

        p = list.get(1);
        assertEquals("jose", p.nomeMaisLegal());
        assertEquals(Integer.valueOf(125), p.age());
    }

    @Test
    public void should_deserialize_map() {
        @SuppressWarnings("serial")
        String listjson = new Gson().toJson(new HashMap<String, Person>() {
            {
                ConcretePerson p = new ConcretePerson();
                p.age = (12);
                p.name = ("maria");
                put("maria", p);
            }
        }, new TypeToken<Map<String, ConcretePerson>>() {
        }.getType());

        Gson gson = Pretender.gsonLazyDeserializerFor(Person.class);
        Person p = gson.<Map<String, Person>>fromJson(listjson, new TypeToken<Map<String, Person>>() {
        }.getType()).get("maria");
        assertEquals("maria", p.nomeMaisLegal());
    }

    @Test
    public void should_deserialize_embbebed_objects() {


        Map<String, Object> clientMap = new HashMap<String, Object>();
        clientMap.put("person", getPerson());
        clientMap.put("clientSince", NOW);

        String clientJson = new Gson().toJson(clientMap);

        Client client = Pretender.gsonLazyDeserializerFor(Client.class, Person.class)
                .fromJson(clientJson, Client.class);
        assertPersonOK(client.person());
        assertEquals(NOW.toString(), client.clientSince().toString());
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

    public static class ConcretePerson implements Person {

        public ConcretePerson() {
        }

        private String name;
        private Integer age;
        private List<Integer> list;
        private Map<String, ConcretePerson> buddies;
        private Date birth;

        @Override
        public String nomeMaisLegal() {
            return name;
        }

        @Override
        public Integer age() {
            return age;
        }

        @Override
        public List<Integer> list() {
            return list;
        }

        @Override
        public Map<String, ConcretePerson> buddies() {
            return buddies;
        }

        public Date birth() {
            return birth;
        }
    }

    public static interface Person {

        @BindToName("name")
        String nomeMaisLegal();

        Integer age();

        List<Integer> list();

        Map<String, ConcretePerson> buddies();

        Date birth();
    }

    public static interface Client {
        Person person();

        Date clientSince();
    }
}
