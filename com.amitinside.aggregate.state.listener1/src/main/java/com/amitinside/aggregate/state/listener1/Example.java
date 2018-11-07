package com.amitinside.aggregate.state.listener1;

import org.osgi.service.component.annotations.Component;

@Component(service = Example.class, property = "aggregate.state=sample")
public class Example {

}
