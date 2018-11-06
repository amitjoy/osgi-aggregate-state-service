/*******************************************************************************
 * Copyright (c) 2018 Amit Kumar Mondal
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 *******************************************************************************/
package com.amitinside.aggregate.state.api;

import java.util.stream.Stream;

import org.osgi.annotation.versioning.ProviderType;

/**
 * In OSGi there is no start ordering. This means that any requirement like
 * (start) ordering must be translated to (service) dependencies. Once something
 * is a service dependency, a (DS) component can defer its activation until the
 * dependency is there. Since this is a proper dependency, an deregistration
 * will automatically deactivate any components that depend on this service.
 * Once something is mapped to a service it leverages the awesome DS runtime to
 * handle the highly complex ordering issues between different components. And
 * since DS is so easy to use with the annotations it does not cost much source
 * code real estate. Really, spend the effort to properly handle your
 * dependencies.
 *
 * The {@link AggregateState} now actively tracks any service that has
 * the {@code aggregate.state} service property. It uses the learned information
 * to modify its own service properties.
 *
 * The {@link AggregateState} also registers the cardinality that it
 * detected for each state. The cardinality is the number of values that were
 * registered for a state. It will prefix the state id with a hash ({@code #})
 * and register the total number of values that it found on other services. The
 * {@code %} prefix register the total number of unique values. This makes it
 * possible to wait until there are a given number of services available.
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
	 * Returns all (known) {@code aggregate.state}s tracked by this service
	 *
	 * @return The known {@code aggregate.state}s instances
	 */
	Stream<String> getTrackedStates();

}
