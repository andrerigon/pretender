package org.pretender.util;

import org.pretender.annotation.BindToName;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface Person {

        @BindToName("name")
        String nomeMaisLegal();

        Integer age();

        List<Integer> list();

        Map<String, ConcretePerson> buddies();

        Date birth();
    }