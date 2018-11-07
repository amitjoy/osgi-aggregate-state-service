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

public final class AggregateStateException extends RuntimeException {

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
	 * @param s the detail message.
	 */
	public AggregateStateException(String s) {
		super(s);
	}
}