package employees;

import lombok.AllArgsConstructor;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EventStoreGateway {
    private final JmsTemplate jmsTemplate;

    public void sendMesssage(String name) {
        jmsTemplate.convertAndSend("eventsQueue", new CreateEventCommand("Employee has beene created: " + name));
    }
}
