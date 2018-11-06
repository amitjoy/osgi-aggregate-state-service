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

public final class AggregateStateTrackingInfo {

	ServiceReference<?> serviceReference;
	Map<String, String> stateProperties;

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof AggregateStateTrackingInfo)) {
			return false;
		}
		if (obj == this) {
			return true;
		}
		final AggregateStateTrackingInfo object = (AggregateStateTrackingInfo) obj;
		return Objects.equals(serviceReference, object.serviceReference);
	}

	@Override
	public int hashCode() {
		return Objects.hash(serviceReference);
	}

}
