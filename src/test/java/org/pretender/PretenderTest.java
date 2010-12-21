package org.pretender;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.MethodRule;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;
import org.pretender.annotation.BindToName;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class PretenderTest {

	@Rule
	public MethodRule timeRule = new MethodRule() {

		@Override
		public Statement apply(final Statement base, final FrameworkMethod method, Object target) {
			return new Statement() {

				@Override
				public void evaluate() throws Throwable {
					long time = System.currentTimeMillis();
					base.evaluate();
					System.out.println(String.format("%s run in: %d millis", method.getName(),
							System.currentTimeMillis() - time));
				}
			};
		}
	};

	String json = new Gson().toJson(new HashMap<String, Object>() {

		private static final long serialVersionUID = 1L;

		{
			put("name", "andre");
			put("age", "" + 5);
			put("list", Arrays.asList(3, 4));
			ConcretePerson p = new ConcretePerson();
			p.age = (12);
			p.name = ("maria");
			Map<String, ConcretePerson> map = new HashMap<String, PretenderTest.ConcretePerson>();
			map.put("maria", p);
			put("buddies", map);
		}
	});

	int max = 200000;

	@Test
	public void abstractLazyPerson() {
		Gson gson = Pretender.gsonLazyDeserializerFor(Person.class);

		for (int i = 0; i < max; i++) {
			Person p = gson.fromJson(json, Person.class);
			assertPersonOK(p);
		}
	}

	@Test
	public void abstractEagerPerson() {
		Gson gson = Pretender.gsonEagerDeserializerFor(Person.class);

		for (int i = 0; i < max; i++) {
			Person p = gson.fromJson(json, Person.class);
			assertPersonOK(p);
		}
	}

	@Test
	public void concrete() {
		for (int i = 0; i < max; i++) {
			Person p = new Gson().fromJson(json, ConcretePerson.class);
			assertPersonOK(p);
		}
	}

	@Test
	public void list() {

		@SuppressWarnings("serial")
		String listjson = new Gson().toJson(new ArrayList<Person>() {
			{
				ConcretePerson p = new ConcretePerson();
				p.age = (12);
				p.name = ("maria");
				add(p);
			}
		});

		Gson gson = Pretender.gsonLazyDeserializerFor(Person.class);
		Person p = gson.<List<Person>> fromJson(listjson, new TypeToken<List<Person>>() {
		}.getType()).get(0);
		assertEquals("maria", p.nomeMaisLegal());
	}

	@Test
	public void map() {
		@SuppressWarnings("serial")
		String listjson = new Gson().toJson(new HashMap<String, Person>() {
			{
				ConcretePerson p = new ConcretePerson();
				p.age = (12);
				p.name = ("maria");
				put("maria", p);
			}
		});

		Gson gson = Pretender.gsonLazyDeserializerFor(Person.class);
		Person p = gson.<Map<String, Person>> fromJson(listjson, new TypeToken<Map<String, Person>>() {
		}.getType()).get("maria");
		assertEquals("maria", p.nomeMaisLegal());
	}

	private void assertPersonOK(Person p) {
		Person b = p.buddies().get("maria");
		assertEquals("andre", p.nomeMaisLegal());
		assertEquals(Integer.valueOf(5), p.age());
		assertEquals("maria", b.nomeMaisLegal());
		assertEquals(Integer.valueOf(12), Integer.valueOf(b.age()));
		assertArrayEquals(new Integer[] { 3, 4 }, p.list().toArray());
	}

	public static class ConcretePerson implements Person {

		public ConcretePerson() {
		}

		private String name;
		private Integer age;
		private List<Integer> list;
		private Map<String, ConcretePerson> buddies;

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

	}

	public static interface Person {

		@BindToName("name")
		String nomeMaisLegal();

		Integer age();

		List<Integer> list();

		Map<String, ConcretePerson> buddies();
	}
}
