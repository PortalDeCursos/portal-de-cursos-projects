
package br.edu.ifrn.nc.portaltads.projects.client;

import br.edu.ifrn.nc.portaltads.projects.business.dto.DisciplinaDTO;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ErroIntegracaoException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ServicoCursoIndisponivelException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Component
public class DisciplinaClient {

    private final WebClient webClient;

    public DisciplinaClient(@Qualifier("cursosWebClient") WebClient webClient) {
        this.webClient = webClient;
    }
    public List<DisciplinaDTO> buscarPorCurso(Long cursoId) {

        try {

            return webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/disciplinas")
                            .queryParam("cursoId", cursoId)
                            .build())
                    .retrieve()
                    .bodyToFlux(DisciplinaDTO.class)
                    .collectList()
                    .block();

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
