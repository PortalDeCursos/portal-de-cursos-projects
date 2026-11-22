package br.edu.ifrn.nc.portaltads.projects.business.messaging;

import br.edu.ifrn.nc.portaltads.projects.business.event.ProjetoStatusAlteradoEvent;
import br.edu.ifrn.nc.portaltads.projects.business.event.ProjetoSubmetidoEvent;
import br.edu.ifrn.nc.portaltads.projects.config.RabbitConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
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

    public void publicarStatusAlterado(ProjetoStatusAlteradoEvent evento) {

        rabbitTemplate.convertAndSend(
                RabbitConfig.EXCHANGE,
                RabbitConfig.ROUTING_KEY_PROJETO_STATUS,
                evento);

        log.info("[RabbitMQ] Evento de status publicado: projetoId={}, status={}, destinatarios={}",
                evento.projetoId(), evento.statusProjeto(), evento.destinatarios());
    }
}
