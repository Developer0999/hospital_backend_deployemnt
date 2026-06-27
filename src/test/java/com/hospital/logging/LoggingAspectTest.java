package com.hospital.logging;

import org.junit.jupiter.api.Test;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LoggingAspectTest {

    @Test
    void aspectShouldProxyServiceMethodWithoutBreakingInvocation() {
        AspectJProxyFactory factory = new AspectJProxyFactory(new SampleService());
        factory.addAspect(new LoggingAspect());

        SampleService proxy = factory.getProxy();
        String result = proxy.greet("Alice");

        assertEquals("Hello Alice", result);
    }

    static class SampleService {
        public String greet(String name) {
            return "Hello " + name;
        }
    }
}
