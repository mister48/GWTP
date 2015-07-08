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

package com.gwtplatform.dispatch.rest.processors.endpoint;

import java.util.List;

import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.util.Elements;

import com.google.auto.service.AutoService;
import com.google.common.base.Optional;
import com.gwtplatform.dispatch.rest.processors.AbstractContextProcessor;
import com.gwtplatform.dispatch.rest.processors.ContextProcessors;
import com.gwtplatform.dispatch.rest.processors.definitions.EndPointDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.HttpVariableDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.TypeDefinition;
import com.gwtplatform.dispatch.rest.processors.definitions.VariableDefinition;
import com.gwtplatform.dispatch.rest.processors.resource.ResourceMethodContext;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationContext;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationContext.IO;
import com.gwtplatform.dispatch.rest.processors.serialization.SerializationProcessor;

import static com.google.common.collect.Iterables.isEmpty;
import static com.gwtplatform.dispatch.rest.processors.NameFactory.endPointName;

@AutoService(EndPointImplProcessor.class)
public class DefaultEndPointImplProcessor extends AbstractContextProcessor<EndPointImplContext, EndPointImplDefinition>
        implements EndPointImplProcessor {
    private static final String TEMPLATE = "/com/gwtplatform/dispatch/rest/processors/endpoint/EndPoint.vm";

    private ContextProcessors contextProcessors;
    private Elements elements;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);

        contextProcessors = new ContextProcessors(processingEnv, logger);
        elements = processingEnv.getElementUtils();
    }

    @Override
    public boolean canProcess(EndPointImplContext context) {
        return true;
    }

    @Override
    public EndPointImplDefinition process(EndPointImplContext context) {
        logger.debug("Generating end-point implementation for `%s`.", context);

        EndPointImplDefinition definition = processImplDefinition(context);

        generateSerializers(definition);

        outputter.withTemplateFile(TEMPLATE)
                .withParam("endPoint", definition.getEndPoint())
                .withParam("fields", definition.getFields())
                .writeTo(definition.getImpl());

        return definition;
    }

    private EndPointImplDefinition processImplDefinition(EndPointImplContext context) {
        ResourceMethodContext methodContext = context.getResourceMethodContext();
        TypeDefinition impl = endPointName(elements, methodContext.getParent(), methodContext.getElement());
        EndPointDefinition endPoint = context.getEndPointDefinition();
        List<VariableDefinition> fields = context.getMethodDefinition().getParameters();

        return new EndPointImplDefinition(impl, fields, endPoint);
    }

    private void generateSerializers(EndPointImplDefinition definition) {
        EndPointDefinition endPoint = definition.getEndPoint();
        Optional<HttpVariableDefinition> body = endPoint.getBody();

        if (body.isPresent()) {
            processSerialization(new SerializationContext(body.get().getType(), endPoint.getConsumes(), IO.READ));
        }

        processSerialization(new SerializationContext(endPoint.getResult(), endPoint.getProduces(), IO.WRITE));
    }

    private void processSerialization(SerializationContext context) {
        Iterable<SerializationProcessor> processors =
                contextProcessors.getProcessors(SerializationProcessor.class, context);

        for (SerializationProcessor processor : processors) {
            processor.process(context);
        }

        if (isEmpty(processors)) {
            logger.mandatoryWarning("No serialization policy found for context `%s`.", context);
        }
    }
}
