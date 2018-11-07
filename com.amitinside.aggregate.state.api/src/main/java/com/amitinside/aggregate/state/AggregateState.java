/*******************************************************************************
 * Copyright (c) 2018 Amit Kumar Mondal
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 *******************************************************************************/
package com.amitinside.aggregate.state;

import org.osgi.annotation.versioning.ProviderType;

/**
 * In OSGi there is no start ordering. This means, any requirement like (start)
 * ordering must be translated to (service) dependencies. Once something is a
 * service dependency, a (DS) component can defer its activation until the
 * dependency is there. Since this is a proper dependency, an deregistration
 * will automatically deactivate any components that depend on this service.
 * Once something is mapped to a service it leverages the DS runtime to handle
 * the highly complex ordering issues between different components.
 *
 * The {@link AggregateState} now actively tracks any service that has the
 * {@link #PROPERTY} service property. It uses the learned information to modify
 * its own service properties.
 *
 * The {@link AggregateState} also registers the cardinality that it detected
 * for each state. The cardinality is the number of values that were registered
 * for a state. It will prefix the state id with a hash ({@code #}) and register
 * the total number of values that it found in other services. The {@code %}
 * prefix registers the total number of unique values.
 *
 * <b>Note that</b>, the implementation can also throw
 * {@link AggregateStateException} if the service specified state(s) cannot be
 * mapped to any existing property.
 *
 * <p>
 * Access to this service requires the
 * {@code ServicePermission[AggregateStateService, GET]} permission. It is
 * intended that consumer bundles should be granted this permission.
 *
 *
 * @noimplement This interface is not intended to be implemented by consumers.
 * @noextend This interface is not intended to be extended by consumers.
 *
 * @ThreadSafe
 *
 * @see http://aqute.biz/2017/04/24/aggregate-state.html
 */
@ProviderType
public interface AggregateState {

	/**
	 * The name of the service property key that identifies a service taking part in
	 * aggregate service mechanism
	 */
	String PROPERTY = "aggregate.state";

	/**
	 * Capability name for aggregate state
	 *
	 * <p>
	 * Used in {@code Provide-Capability} and {@code Require-Capability} manifest
	 * headers with the {@code osgi.extender} namespace. For example:
	 * </p>
	 *
	 * <pre>
	 * Require-Capability: osgi.extender;
	 *  filter:="(&amp;(osgi.extender=osgi.aggregate.state)(version&gt;=1.0)(!(version&gt;=2.0)))"
	 * </pre>
	 */
	String AGGREGATE_STATE_CAPABILITY_NAME = "osgi.aggregate.state";

}
