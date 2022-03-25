package org.instancio.internal.model;

import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class ArrayNode extends Node {

    private final Node elementNode;

    public ArrayNode(final NodeContext nodeContext,
                     final Class<?> klass,
                     final Node elementNode,
                     @Nullable final Field field,
                     @Nullable final Type genericType,
                     @Nullable final Node parent) {

        super(nodeContext, klass, field, genericType, parent);

        Verify.isTrue(klass.isArray(), "Not an array type: %s", klass.getName());

        elementNode.setParent(this);

        this.elementNode = elementNode;
    }

    /**
     * Returns an empty list; children come from the {@link #getElementNode()}.
     */
    @Override
    protected List<Node> collectChildren() {
        return Collections.emptyList();
    }

    public Node getElementNode() {
        return elementNode;
    }

    @Override
    public boolean equals(@Nullable Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (this.getClass() != o.getClass()) return false;
        ArrayNode other = (ArrayNode) o;
        return Objects.equals(this.getKlass(), other.getKlass())
                && Objects.equals(this.getElementNode(), other.getElementNode());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getKlass(), getElementNode());
    }
}