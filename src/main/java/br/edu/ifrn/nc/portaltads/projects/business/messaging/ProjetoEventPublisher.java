package br.edu.ifrn.nc.portaltads.projects.business.messaging;

import br.edu.ifrn.nc.portaltads.projects.business.event.ProjetoSubmetidoEvent;
import br.edu.ifrn.nc.portaltads.projects.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProjetoEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    public void publicarProjetoSubmetido(ProjetoSubmetidoEvent evento) {

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY_PROJETO_SUBMETIDO,
                evento);
    }
}
