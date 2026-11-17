package br.edu.ifrn.nc.portaltads.projects.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    public static final String EXCHANGE = "portaltads.projetos.exchange";
    public static final String QUEUE_PROJETO_SUBMETIDO = "portaltads.projeto-submetido.queue";
    public static final String ROUTING_KEY_PROJETO_SUBMETIDO = "projeto.submetido";

    @Bean
    public TopicExchange projetosExchange() {

        return new TopicExchange(EXCHANGE);
    }

    @Bean
    public Queue projetoSubmetidoQueue() {

        return new Queue(QUEUE_PROJETO_SUBMETIDO, true);
    }

    @Bean
    public Binding projetoSubmetidoBinding(Queue projetoSubmetidoQueue, TopicExchange projetosExchange) {

        return BindingBuilder.bind(projetoSubmetidoQueue)
                .to(projetosExchange)
                .with(ROUTING_KEY_PROJETO_SUBMETIDO);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {

        return new JacksonJsonMessageConverter();
    }
}
