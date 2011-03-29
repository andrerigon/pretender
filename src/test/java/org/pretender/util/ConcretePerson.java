package org.pretender.util;

import java.util.Date;
import java.util.List;
import java.util.Map;

public class ConcretePerson implements Person {

    public ConcretePerson() {
    }

    public String name;
    public Integer age;
    public List<Integer> list;
    public Map<String, ConcretePerson> buddies;
    public Date birth;

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