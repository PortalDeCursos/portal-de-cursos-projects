package br.edu.ifrn.nc.portaltads.projects.client;

import br.edu.ifrn.nc.portaltads.projects.business.dto.CoordenadorDTO;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.exception.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Component
public class CoordenadorClient {

    private final WebClient usuariosWebClient;

    public CoordenadorClient(@Qualifier("usuariosWebClient") WebClient usuariosWebClient) {
        this.usuariosWebClient = usuariosWebClient;
    }

    public CoordenadorDTO buscarPorId(Long id, String token) {

        try {

            return usuariosWebClient.get()
                    .uri("/coordenadores/{id}", id)
                    .header("Authorization", token)
                    .retrieve()
                    .bodyToMono(CoordenadorDTO.class)
                    .block();

        } catch (WebClientResponseException.NotFound e) {

            throw new ResourceNotFoundException("Coordenador não encontrado com id: " + id);

        } catch (WebClientResponseException.Unauthorized e) {

            throw new TokenInvalidoException("Token inválido ou ausente.");

        } catch (WebClientResponseException.Forbidden e) {

            throw new AcessoNegadoException("Você não tem permissão para consultar este coordenador.");

        } catch (WebClientRequestException e) {

            throw new ServicoUsuariosIndisponivelException("Não foi possível conectar ao serviço de usuários.");

        } catch (WebClientResponseException e) {

            throw new ErroIntegracaoException("Erro na comunicação com o serviço de usuários: " + e.getStatusCode());
        }
    }
}
