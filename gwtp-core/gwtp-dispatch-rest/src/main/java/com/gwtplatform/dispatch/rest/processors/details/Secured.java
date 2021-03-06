/*
 * Copyright 2015 ArcBees Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.gwtplatform.dispatch.rest.processors.details;

import java.util.Objects;

import javax.lang.model.element.Element;

import com.gwtplatform.dispatch.rest.shared.NoXsrfHeader;

import static com.google.auto.common.MoreElements.isAnnotationPresent;

public class Secured {
    private final boolean secured;

    public Secured(Element element) {
        secured = isElementSecured(element);
    }

    public Secured(Element element, Secured parent) {
        secured = parent.isSecured() && isElementSecured(element);
    }

    private boolean isElementSecured(Element element) {
        return !isAnnotationPresent(element, NoXsrfHeader.class);
    }

    public boolean isSecured() {
        return secured;
    }

    @Override
    public String toString() {
        return String.valueOf(secured);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Secured secured1 = (Secured) o;
        return Objects.equals(secured, secured1.secured);
    }

    @Override
    public int hashCode() {
        return Objects.hash(secured);
    }
}
