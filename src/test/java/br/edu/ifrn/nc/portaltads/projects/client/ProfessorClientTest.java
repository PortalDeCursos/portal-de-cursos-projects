package br.edu.ifrn.nc.portaltads.projects.client;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.*;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.net.ConnectException;
import java.net.URI;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ProfessorClientTest {

    @Test
    void deveEnviarTokenBearerNaRequisicao() {
        String token = "Bearer token-teste";

        AtomicReference<String> tokenEnviado = new AtomicReference<>();

        ExchangeFunction exchangeFunction = request -> {
            tokenEnviado.set(
                    request.headers().getFirst(HttpHeaders.AUTHORIZATION)
            );

            return reactor.core.publisher.Mono.just(
                    ClientResponse.create(HttpStatus.OK)
                            .header(HttpHeaders.CONTENT_TYPE, "application/json")
                            .body("""
                                {
                                    "id": 10,
                                    "usuario": {
                                        "id": 20,
                                        "nome": "Professor Teste",
                                        "email": "professor@teste.com"
                                    },
                                    "cursoId": 1
                                }
                                """)
                            .build()
            );
        };

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .exchangeFunction(exchangeFunction)
                .build();

        ProfessorClient professorClient = new ProfessorClient(webClient);

        professorClient.buscarPorId(10L, token);

        assertEquals(token, tokenEnviado.get());
    }

    @Test
    void deveLancarExcecaoQuandoTokenForInvalido() {
        ExchangeFunction exchangeFunction = request ->
                reactor.core.publisher.Mono.just(
                        ClientResponse.create(HttpStatus.UNAUTHORIZED)
                                .build()
                );

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .exchangeFunction(exchangeFunction)
                .build();

        ProfessorClient professorClient = new ProfessorClient(webClient);

        assertThrows(
                TokenInvalidoException.class,
                () -> professorClient.buscarPorId(10L, "Bearer token-invalido")
        );
    }

    @Test
    void deveLancarExcecaoQuandoAcessoForNegado() {
        ExchangeFunction exchangeFunction = request ->
                reactor.core.publisher.Mono.just(
                        ClientResponse.create(HttpStatus.FORBIDDEN)
                                .build()
                );

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .exchangeFunction(exchangeFunction)
                .build();

        ProfessorClient professorClient = new ProfessorClient(webClient);

        assertThrows(
                AcessoNegadoException.class,
                () -> professorClient.buscarPorId(10L, "Bearer token-teste")
        );
    }

    @Test
    void deveLancarExcecaoQuandoServicoDeUsuariosEstiverIndisponivel() {
        ExchangeFunction exchangeFunction = request ->
                reactor.core.publisher.Mono.just(
                        ClientResponse.create(HttpStatus.SERVICE_UNAVAILABLE)
                                .build()
                );

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .exchangeFunction(exchangeFunction)
                .build();

        ProfessorClient professorClient = new ProfessorClient(webClient);

        assertThrows(
                ServicoUsuariosIndisponivelException.class,
                () -> professorClient.buscarPorId(10L, "Bearer token-teste")
        );
    }

    @Test
    void deveLancarExcecaoQuandoNaoConseguirConectarAoServicoDeUsuarios() {
        ExchangeFunction exchangeFunction = request ->
                reactor.core.publisher.Mono.error(
                        new WebClientRequestException(
                                new ConnectException("Conexão recusada"),
                                request.method(),
                                URI.create(request.url().toString()),
                                request.headers()
                        )
                );

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .exchangeFunction(exchangeFunction)
                .build();

        ProfessorClient professorClient = new ProfessorClient(webClient);

        assertThrows(
                ServicoUsuariosIndisponivelException.class,
                () -> professorClient.buscarPorId(10L, "Bearer token-teste")
        );
    }

    @Test
    void deveLancarExcecaoQuandoProfessorNaoForEncontrado() {
        ExchangeFunction exchangeFunction = request ->
                reactor.core.publisher.Mono.just(
                        ClientResponse.create(HttpStatus.NOT_FOUND)
                                .build()
                );

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .exchangeFunction(exchangeFunction)
                .build();

        ProfessorClient professorClient = new ProfessorClient(webClient);

        assertThrows(
                ResourceNotFoundException.class,
                () -> professorClient.buscarPorId(10L, "Bearer token-teste")
        );
    }

    @Test
    void deveLancarExcecaoQuandoOcorrerErroInternoNoServicoDeUsuarios() {
        ExchangeFunction exchangeFunction = request ->
                reactor.core.publisher.Mono.just(
                        ClientResponse.create(HttpStatus.INTERNAL_SERVER_ERROR)
                                .build()
                );

        WebClient webClient = WebClient.builder()
                .baseUrl("http://localhost:8080")
                .exchangeFunction(exchangeFunction)
                .build();

        ProfessorClient professorClient = new ProfessorClient(webClient);

        assertThrows(
                ErroIntegracaoException.class,
                () -> professorClient.buscarPorId(10L, "Bearer token-teste")
        );
    }
}