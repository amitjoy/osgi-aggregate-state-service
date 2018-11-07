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
 * Thrown when aggregate state cannot be processed if the service specified
 * state(s) cannot be mapped to any existing property
 */
@ProviderType
public class AggregateStateException extends RuntimeException {

	private static final long serialVersionUID = -3487103127847862920L;

	/**
	 * Constructs a {@code AggregateStateException} with no detailed message.
	 */
	public AggregateStateException() {
		super();
	}

	/**
	 * Constructs a {@code AggregateStateException} with the specified detailed
	 * message.
	 *
	 * @param message the detailed message.
	 */
	public AggregateStateException(final String message) {
		super(message);
	}
}