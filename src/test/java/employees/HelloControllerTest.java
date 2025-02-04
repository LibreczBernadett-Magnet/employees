package employees;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HelloControllerTest {
    @Mock
    HelloService helloService;

    @InjectMocks
    HelloController helloController;


    @Test
    void sayHello() {
        when(helloService.sayHello()).thenReturn("Mock hello");

        String messge = helloController.sayHello();

        assertThat(messge).isEqualTo("Mock hello");
    }
}