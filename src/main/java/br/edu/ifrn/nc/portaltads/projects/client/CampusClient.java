
package br.edu.ifrn.nc.portaltads.projects.client;

import br.edu.ifrn.nc.portaltads.projects.business.dto.CampusDTO;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ErroIntegracaoException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.ServicoCampusIndisponivelException;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.CampusNaoEncontradoException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class CampusClient {

    private final WebClient webClient;

    public CampusClient(@Qualifier("cursosWebClient") WebClient webClient) {
        this.webClient = webClient;
    }

    public CampusDTO buscarPorID(Long id) {

        try {

            return webClient.get()
                    .uri("/campus/{id}", id)
                    .retrieve()
                    .bodyToMono(CampusDTO.class)
                    .block();

        } catch (WebClientResponseException.NotFound e) {

            throw new CampusNaoEncontradoException(
                    "Campus não encontrado com id: " + id);

        } catch (WebClientResponseException.ServiceUnavailable e) {

            throw new ServicoCampusIndisponivelException(
                    "O serviço de campus está indisponível.");

        } catch (WebClientRequestException e) {

            throw new ServicoCampusIndisponivelException(
                    "Não foi possível conectar ao serviço de campus.");

        } catch (WebClientResponseException e) {

            throw new ErroIntegracaoException(
                    "Erro na comunicação com o serviço de campus: "
                            + e.getStatusCode());
        }
    }
}
