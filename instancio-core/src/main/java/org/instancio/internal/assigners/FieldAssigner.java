/*
 * Copyright 2022-2023 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.instancio.internal.assigners;

import org.instancio.assignment.AssignmentType;
import org.instancio.assignment.OnSetFieldError;
import org.instancio.exception.InstancioApiException;
import org.instancio.internal.nodes.Node;
import org.instancio.internal.util.Format;
import org.instancio.internal.util.Sonar;
import org.instancio.settings.Keys;
import org.instancio.settings.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static org.instancio.internal.util.ExceptionHandler.logException;

public class FieldAssigner implements Assigner {
    private static final Logger LOG = LoggerFactory.getLogger(FieldAssigner.class);

    private final OnSetFieldError onSetFieldError;

    public FieldAssigner(final Settings settings) {
        this.onSetFieldError = settings.get(Keys.ON_SET_FIELD_ERROR);

        LOG.trace("{}, {}", AssignmentType.FIELD, onSetFieldError);
    }

    @Override
    public void assign(final Node node, final Object target, final Object value) {
        final Field field = node.getField();

        if (value != null) {
            setField(target, field, value);
        } else if (!field.getType().isPrimitive()) { // can't assign null to a primitive
            setField(target, field, null);
        }
    }

    @SuppressWarnings(Sonar.ACCESSIBILITY_UPDATE_SHOULD_BE_REMOVED)
    private void setField(final Object target, final Field field, final Object value) {
        try {
            field.setAccessible(true);
            field.set(target, value);
        } catch (IllegalArgumentException ex) {
            // Wrong type is being assigned to a field.
            // Always propagate type mismatch errors as it's either a bug or user error.
            final String msg = AssignerErrorUtil.getFieldAssignmentErrorMessage(
                    value, Format.formatField(field), ex);

            throw new InstancioApiException(msg, ex);
        } catch (Exception ex) {
            handleError(field, value, ex);
        }
    }

    private void handleError(final Field field, final Object value, final Exception ex) {
        if (onSetFieldError == OnSetFieldError.FAIL) {
            final String msg = AssignerErrorUtil.getFieldAssignmentErrorMessage(
                    value, Format.formatField(field), ex);

            throw new InstancioApiException(msg, ex);
        }

        if (onSetFieldError == OnSetFieldError.IGNORE) {
            logException("{}: error assigning value to field: {}", ex, OnSetFieldError.IGNORE, field);
        }
    }
}
