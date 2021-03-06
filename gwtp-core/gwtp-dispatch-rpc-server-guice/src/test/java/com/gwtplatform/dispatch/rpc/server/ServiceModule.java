/*
 * Copyright 2013 ArcBees Inc.
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

package com.gwtplatform.dispatch.rpc.server;

import com.google.inject.AbstractModule;
import com.google.inject.Singleton;
import com.gwtplatform.dispatch.rpc.server.actionhandler.ActionHandler;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorClass;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorMap;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorMapImpl;
import com.gwtplatform.dispatch.rpc.server.actionhandlervalidator.ActionHandlerValidatorRegistry;
import com.gwtplatform.dispatch.rpc.server.actionvalidator.ActionValidator;
import com.gwtplatform.dispatch.rpc.server.guice.DispatchImpl;
import com.gwtplatform.dispatch.rpc.server.guice.actionhandlervalidator.ActionHandlerValidatorLinker;
import com.gwtplatform.dispatch.rpc.server.guice.actionhandlervalidator.LazyActionHandlerValidatorRegistryImpl;
import com.gwtplatform.dispatch.rpc.shared.Action;
import com.gwtplatform.dispatch.rpc.shared.Result;

public class ServiceModule extends AbstractModule {
    private final Class<? extends ActionValidator> actionValidator;

    public ServiceModule(Class<? extends ActionValidator> actionValidator) {
        this.actionValidator = actionValidator;
    }

    @Override
    protected void configure() {
        bind(Dispatch.class).to(DispatchImpl.class);
        bind(ActionHandlerValidatorRegistry.class).to(
                LazyActionHandlerValidatorRegistryImpl.class).in(Singleton.class);

        bindHandler(SomeAction.class, HandlerThatThrowsActionException.class, actionValidator);
        requestStaticInjection(ActionHandlerValidatorLinker.class);
    }

    protected <A extends Action<R>, R extends Result> void bindHandler(
            Class<A> actionClass, Class<? extends ActionHandler<A, R>> handlerClass,
            Class<? extends ActionValidator> actionValidator) {

        bind(ActionHandlerValidatorMap.class).toInstance(
                new ActionHandlerValidatorMapImpl<>(actionClass,
                        new ActionHandlerValidatorClass<>(handlerClass, actionValidator)));
    }
}
