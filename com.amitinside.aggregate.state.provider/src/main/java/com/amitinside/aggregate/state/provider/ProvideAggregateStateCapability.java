/*******************************************************************************
 * Copyright (c) 2018 Amit Kumar Mondal
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 *******************************************************************************/
package com.amitinside.aggregate.state.provider;

import static com.amitinside.aggregate.state.AggregateState.CAPABILITY_NAME;
import static java.lang.annotation.RetentionPolicy.CLASS;
import static org.osgi.namespace.extender.ExtenderNamespace.EXTENDER_NAMESPACE;

import java.lang.annotation.Retention;

import org.osgi.annotation.bundle.Capability;

@Capability(namespace = EXTENDER_NAMESPACE, name = CAPABILITY_NAME, version = "1.0")
@Retention(CLASS)
public @interface ProvideAggregateStateCapability {
}