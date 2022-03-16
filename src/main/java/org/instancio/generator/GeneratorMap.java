package org.instancio.generator;

import javax.xml.datatype.XMLGregorianCalendar;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.NavigableSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.UUID;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentNavigableMap;

public class GeneratorMap {

    private static final int DEFAULT_ARRAY_LENGTH = 2;
    private final Map<Class<?>, ValueGenerator<?>> generatorMap = new HashMap<>();
    private final Map<Class<?>, ValueGenerator<?>> arrayGeneratorMap = new HashMap<>();

    public GeneratorMap() {
        // Core types
        generatorMap.put(byte.class, new ByteGenerator());
        generatorMap.put(short.class, new ShortGenerator());
        generatorMap.put(int.class, new IntegerGenerator());
        generatorMap.put(long.class, new LongGenerator());
        generatorMap.put(float.class, new FloatGenerator());
        generatorMap.put(double.class, new DoubleGenerator());
        generatorMap.put(boolean.class, new BooleanGenerator());
        generatorMap.put(char.class, new CharacterGenerator());
        generatorMap.put(Byte.class, new ByteGenerator());
        generatorMap.put(Short.class, new ShortGenerator());
        generatorMap.put(Integer.class, new IntegerGenerator());
        generatorMap.put(Long.class, new LongGenerator());
        generatorMap.put(Float.class, new FloatGenerator());
        generatorMap.put(Double.class, new DoubleGenerator());
        generatorMap.put(Boolean.class, new BooleanGenerator());
        generatorMap.put(Character.class, new CharacterGenerator());
        generatorMap.put(String.class, new StringGenerator());

        generatorMap.put(UUID.class, new UUIDGenerator());
        generatorMap.put(BigDecimal.class, new BigDecimalGenerator());
        generatorMap.put(LocalDateTime.class, new LocalDateTimeGenerator());
        generatorMap.put(XMLGregorianCalendar.class, new XMLGregorianCalendarGenerator());

        // Collections
        generatorMap.put(List.class, new ArrayListGenerator<>());
        generatorMap.put(Map.class, new HashMapGenerator<>());
        generatorMap.put(ConcurrentMap.class, new ConcurrentHashMapGenerator<>());
        generatorMap.put(ConcurrentNavigableMap.class, new ConcurrentSkipListMapGenerator<>());
        generatorMap.put(SortedMap.class, new TreeMapGenerator<>());
        generatorMap.put(NavigableMap.class, new TreeMapGenerator<>());
        generatorMap.put(Set.class, new HashSetGenerator<>());
        generatorMap.put(SortedSet.class, new TreeSetGenerator<>());
        generatorMap.put(NavigableSet.class, new TreeSetGenerator<>());

    }

    public ValueGenerator<?> get(Class<?> klass) {
        ValueGenerator<?> generator = generatorMap.get(klass);

        if (generator == null) {
            if (klass.isEnum()) {
                generator = new EnumGenerator(klass);
                generatorMap.put(klass, generator);
            } else if (klass.isArray()) {
                throw new IllegalArgumentException("Should be calling getArrayGenerator(Class)!");
            }
        }

        return generator;
    }

    public ValueGenerator<?> getArrayGenerator(Class<?> klass) {
        ValueGenerator<?> generator = arrayGeneratorMap.get(klass);

        if (generator == null) {
            generator = new ArrayGenerator(klass, DEFAULT_ARRAY_LENGTH);
            arrayGeneratorMap.put(klass, generator);
        }

        return generator;
    }
}