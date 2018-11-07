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

import java.util.Map;
import java.util.Objects;

import org.osgi.framework.ServiceReference;

/**
 * {@link AggregateStateInfo} is used to store the information regarding the
 * aggregate states that the service identified by {@link #serviceReference}
 * provides. This also provides the associated properties of the aggregate
 * states.
 *
 * Example, consider a service providing the following aggregate states in its
 * service properties.
 *
 * <pre>
 * Service A:
 *      - aggregate.state = stateA, stateB
 *      - stateA = MyState1
 *      - stateB = MyState2
 *
 * The {@link #stateProperties} will then comprise:
 *     - [KEY]  = [VALUE]
 *     - stateA = MyState1
 *     - stateB = MyState2
 * </pre>
 */
public final class AggregateStateInfo {

	ServiceReference<?> serviceReference;
	Map<String, String> stateProperties;

	@Override
	public boolean equals(final Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj instanceof AggregateStateInfo) {
			final AggregateStateInfo other = (AggregateStateInfo) obj;
			return Objects.equals(serviceReference, other.serviceReference);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash(serviceReference);
	}

	@Override
	public String toString() {
		return "[serviceReference=" + serviceReference + ", stateProperties=" + stateProperties + "]";
	}

}
