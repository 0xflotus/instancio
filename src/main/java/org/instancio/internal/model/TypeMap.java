package org.instancio.internal.model;

import org.instancio.util.ObjectUtils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.lang.reflect.WildcardType;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Helper class for mapping type variables to actual type arguments.
 */
public class TypeMap {

    private final Map<TypeVariable<?>, Class<?>> rootTypeMap;
    private final Map<Type, Type> typeMap;

    public TypeMap(final Type genericType, final Map<TypeVariable<?>, Class<?>> rootTypeMap) {
        this.rootTypeMap = Collections.unmodifiableMap(rootTypeMap);
        this.typeMap = Collections.unmodifiableMap(buildTypeMap(genericType));
    }

    public Type get(final Type type) {
        return typeMap.get(type);
    }

    public Type getOrDefault(final Type type, final Type defaultValue) {
        return typeMap.getOrDefault(type, defaultValue);
    }

    public Type getActualType(final Type type) {
        return typeMap.get(type);
    }

    public int size() {
        return typeMap.size();
    }

    /**
     * Some possible field declarations and their generic types:
     *
     * <pre>{@code
     *   Type                | Generic type class
     *   --------------------+-------------------
     *   int                 | Class
     *   Integer             | Class
     *   Item<Integer>       | ParameterizedType
     *   Item<T>             | ParameterizedType
     *   T                   | TypeVariable
     *   Item<?>             | WildcardType
     *   Item<? extends Foo> | WildcardType
     * }</pre>
     *
     * @param genericType to build the type map for
     * @return type map
     */
    private Map<Type, Type> buildTypeMap(final Type genericType) {
        final Map<Type, Type> map = new HashMap<>();

        if (genericType instanceof Class) {
            return map;
        }

        if (genericType instanceof TypeVariable && rootTypeMap.containsKey(genericType)) {
            final Class<?> mappedType = rootTypeMap.get(genericType);
            map.put(genericType, mappedType);
            return map;
        }

        if (genericType instanceof ParameterizedType) {
            final ParameterizedType pType = (ParameterizedType) genericType;
            final Class<?> rawType = (Class<?>) pType.getRawType();
            final TypeVariable<?>[] typeVars = rawType.getTypeParameters();
            final Type[] typeArgs = pType.getActualTypeArguments();

            for (int i = 0; i < typeArgs.length; i++) {
                final Type mappedType = resolveTypeMapping(typeArgs[i]);
                // Mapped type can be null when a type variable isn't in the root type map.
                // In this case with map type variable to type variable
                map.put(typeVars[i], ObjectUtils.defaultIfNull(mappedType, typeArgs[i]));
            }
        }
        return map;
    }

    private Type resolveTypeMapping(final Type typeArg) {
        if (typeArg instanceof TypeVariable) {
            return rootTypeMap.get(typeArg);
        } else if (typeArg instanceof ParameterizedType) {
            return typeArg;
        } else if (typeArg instanceof Class) {
            return typeArg;
        } else if (typeArg instanceof WildcardType) {
            WildcardType wType = (WildcardType) typeArg;
            return resolveTypeMapping(wType.getUpperBounds()[0]); // TODO multiple bounds
        }
        throw new UnsupportedOperationException("Unsupported type: " + typeArg.getClass());
    }

    @Override
    public String toString() {
        return new StringJoiner("\n - ", TypeMap.class.getSimpleName() + "[", "]")
                .add("typeMap=" + typeMap)
                .add("rootTypeMap=" + rootTypeMap)
                .toString();
    }
}