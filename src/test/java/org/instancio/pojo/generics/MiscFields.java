package org.instancio.pojo.generics;

import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.instancio.pojo.generics.basic.Pair;
import org.instancio.pojo.generics.foobarbaz.Bar;
import org.instancio.pojo.generics.foobarbaz.Baz;
import org.instancio.pojo.generics.foobarbaz.Foo;

import java.util.List;

@Getter
public class MiscFields<A, B, C> {

    private A fieldA;
    private C[] arrayOfCs;
    private Foo<Bar<Baz<String>>> fooBarBazString;
    private Foo<Bar<Pair<B, C>>> fooBarPairBC;
    private List<Baz<String>> listOfBazStrings;
    private List<C> listOfCs;
    private Pair<A, B> pairAB;
    private Pair<B, A> pairBA;
    private Pair<Foo<Bar<String>>, B> pairFooBarStringB;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
