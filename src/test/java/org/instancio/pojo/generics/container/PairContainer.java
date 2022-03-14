package org.instancio.pojo.generics.container;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.instancio.pojo.generics.basic.Pair;

@Getter
@Setter
public class PairContainer<X, Y> {

    private Pair<X, Y> pairValue;

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.MULTI_LINE_STYLE);
    }
}
