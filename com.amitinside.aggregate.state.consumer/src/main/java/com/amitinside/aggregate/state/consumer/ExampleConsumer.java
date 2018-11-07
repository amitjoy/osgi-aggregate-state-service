package com.amitinside.aggregate.state.consumer;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import com.amitinside.aggregate.state.AggregateState;

@Component
public class ExampleConsumer {

	@Reference(target = "(#sample=1)")
	AggregateState aggregateState;

}
