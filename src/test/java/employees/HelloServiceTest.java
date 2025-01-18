package employees;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class HelloServiceTest {

    @Test
    void sayHello() {
        HelloProperties helloProperties = new HelloProperties();
        helloProperties.setMessage("Hello World");

        HelloService helloService = new HelloService(helloProperties);
        String message = helloService.sayHello();

        assertThat(message).startsWith("Hello");
    }
}