package org.instancio.settings;

import org.instancio.util.ReflectionUtils;
import org.instancio.util.Verify;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.TreeMap;
import java.util.function.Function;

import static org.instancio.internal.model.InstancioValidator.validateSettingKey;
import static org.instancio.internal.model.InstancioValidator.validateSubtypeMapping;

/**
 * Instancio settings API.
 */
public class Settings {
    private static final String TYPE_MAPPING_PREFIX = "type.mapping.";

    private boolean isLockedForModifications;
    private Map<Object, Object> settingsMap;
    private Map<Class<?>, Class<?>> subtypeMap;

    private Settings() {
        this.settingsMap = new HashMap<>();
        this.subtypeMap = new HashMap<>();
    }

    /**
     * Create a new instance of empty settings.
     *
     * @return empty settings
     */
    public static Settings create() {
        return new Settings();
    }

    /**
     * Create default settings.
     *
     * @return settings containing defaults
     */
    public static Settings defaults() {
        Settings settings = new Settings();
        for (Setting setting : Setting.values()) {
            settings.set(setting, setting.defaultValue());
        }
        return settings;
    }

    /**
     * Create settings from the given map.
     *
     * @param map to create settings from
     * @return settings
     */
    public static Settings from(final Map<?, ?> map) {
        final Settings settings = new Settings();

        map.forEach((k, v) -> {
            final String key = k.toString();
            if (key.startsWith(TYPE_MAPPING_PREFIX)) {
                final String fromClass = key.replace(TYPE_MAPPING_PREFIX, "");
                settings.mapType(ReflectionUtils.getClass(fromClass), ReflectionUtils.getClass(v.toString()));
            } else {
                final SettingKey settingKey = Setting.getByKey(key);
                final Function<String, Object> fn = ValueOfFunctions.getFunction(settingKey.type());
                final Object val = fn.apply(v.toString());
                settings.set(settingKey, val);
            }
        });

        return settings;
    }

    /**
     * Merge given settings into these settings.
     *
     * @param overrides settings containing overrides
     * @return updated settings
     */
    public Settings merge(@Nullable final Settings overrides) {
        checkLockedForModifications();
        if (overrides != null) {
            settingsMap.putAll(overrides.settingsMap);
            subtypeMap.putAll(overrides.subtypeMap);
        }
        return this;
    }

    /**
     * Get setting value for given key.
     *
     * @param key setting key
     * @param <T> setting value type
     * @return value for given key, or {@code null} if none.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(final SettingKey key) {
        return (T) settingsMap.get(Verify.notNull(key, "Key must not be null"));
    }

    /**
     * Set setting with given key to the specified value.
     *
     * @param key   to set
     * @param value to set
     * @return updated settings
     */
    public Settings set(final SettingKey key, final Object value) {
        checkLockedForModifications();
        validateSettingKey(key, value);
        settingsMap.put(key, value);
        return this;
    }

    /**
     * Map 'from' supertype to 'to' subtype.
     *
     * @param from supertype class
     * @param to   subtype class
     * @return updated settings
     */
    public Settings mapType(final Class<?> from, Class<?> to) {
        checkLockedForModifications();
        validateSubtypeMapping(from, to);
        subtypeMap.put(from, to);
        return this;
    }

    /**
     * Returns a read-only view of the subtype map.
     *
     * @return subtype map
     */
    public Map<Class<?>, Class<?>> getSubtypeMap() {
        return Collections.unmodifiableMap(subtypeMap);
    }

    /**
     * Locks these settings for further modifications,
     * making this instance immutable.
     *
     * @return read-only settings
     */
    public Settings lock() {
        settingsMap = Collections.unmodifiableMap(settingsMap);
        subtypeMap = Collections.unmodifiableMap(subtypeMap);
        isLockedForModifications = true;
        return this;
    }

    private void checkLockedForModifications() {
        if (isLockedForModifications) {
            throw new UnsupportedOperationException("Settings are read-only");
        }
    }

    @Override
    public String toString() {
        return new StringJoiner(";", Settings.class.getSimpleName() + "[", "]")
                .add("\n  isLockedForModifications=" + isLockedForModifications)
                .add("\n  settingsMap=" + new TreeMap<>(settingsMap))
                .add("\n  subtypeMap=" + subtypeMap)
                .toString();
    }
}