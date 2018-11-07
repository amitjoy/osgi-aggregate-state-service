package com.amitinside.aggregate.state.listener;

import org.osgi.service.component.annotations.Component;

@Component(service = Example.class, property = { "aggregate.state=sample", "sample=A" })
public class Example {

}
