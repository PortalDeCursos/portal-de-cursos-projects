package br.edu.ifrn.nc.portaltads.projects.client;

import br.edu.ifrn.nc.portaltads.projects.business.dto.CursoDTO;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.CursoNaoEncontradoException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ErroIntegracaoException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ServicoCursoIndisponivelException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class CursoClient {

    private final WebClient webClient;

    public CursoClient(@Qualifier("cursosWebClient") WebClient webClient) {
        this.webClient = webClient;
    }
    public CursoDTO buscarPorID(Long id) {

        try {

            return webClient.get()
                    .uri("/cursos/{id}", id)
                    .retrieve()
                    .bodyToMono(CursoDTO.class)
                    .block();

        } catch (WebClientResponseException.NotFound |
                 WebClientResponseException.UnprocessableEntity e) {

            throw new CursoNaoEncontradoException(
                    "Curso não encontrado com id: " + id);

        } catch (WebClientResponseException.ServiceUnavailable e) {

            throw new ServicoCursoIndisponivelException(
                    "O serviço de cursos está indisponível.");

        } catch (WebClientRequestException e) {

            throw new ServicoCursoIndisponivelException(
                    "Não foi possível conectar ao serviço de cursos.");

        } catch (WebClientResponseException e) {

            throw new ErroIntegracaoException(
                    "Erro na comunicação com o serviço de cursos: "
                            + e.getStatusCode());
        }
    }
}