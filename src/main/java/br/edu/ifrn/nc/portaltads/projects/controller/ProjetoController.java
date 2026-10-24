package br.edu.ifrn.nc.portaltads.projects.controller;

import br.edu.ifrn.nc.portaltads.projects.business.ProjetoService;
import br.edu.ifrn.nc.portaltads.projects.business.dto.request.ProjetoRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.request.StatusProjetoRequestDTO;
import br.edu.ifrn.nc.portaltads.projects.business.dto.response.ProjetoResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
@Tag(name = "Projetos", description = "Gerenciamento de Projetos")
public class ProjetoController {

    private final ProjetoService projetoService;

    @PostMapping
    @Operation(summary = "Cadastrar Projeto", description = "Cadastra um novo projeto")
    @ApiResponse(responseCode = "201", description = "Projeto cadastrado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "409", description = "Já existe um projeto com esse nome")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<ProjetoResponseDTO> cadastrar (@Valid @RequestBody ProjetoRequestDTO dto){

        ProjetoResponseDTO responseDTO = projetoService.cadastrar(dto);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(responseDTO.getId()).
                toUri();

        return ResponseEntity.created(location).body(responseDTO);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Projeto por ID", description = "Busca um projeto pelo seu identificador")
    @ApiResponse(responseCode = "200", description = "Projeto encontrado com sucesso")
    @ApiResponse(responseCode = "400", description = "ID inválido")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<ProjetoResponseDTO> buscarPorId(@PathVariable Long id){
        return ResponseEntity.ok(projetoService.listarPorId(id));
    }

    @GetMapping
    @Operation(summary = "Listar Projetos", description = "Lista todos os projetos cadastrados")
    @ApiResponse(responseCode = "200", description = "Busca realizada com sucesso")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<List<ProjetoResponseDTO>> listar(){
        return ResponseEntity.ok(projetoService.listar());
    }

    @PutMapping("/{id}")
    @Operation(summary = "Atualizar Projeto", description = "Atualiza os dados de um projeto existente")
    @ApiResponse(responseCode = "200", description = "Projeto atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Dados inválidos")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    @ApiResponse(responseCode = "409", description = "Já existe outro projeto com esse nome")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<ProjetoResponseDTO> atualizar(@PathVariable Long id, @Valid @RequestBody ProjetoRequestDTO dto){
        return ResponseEntity.ok(projetoService.atualizar(id, dto));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Atualizar Status do Projeto", description = "Atualiza o status do projeto conforme as etapas")
    @ApiResponse(responseCode = "200", description = "Status do projeto atualizado com sucesso")
    @ApiResponse(responseCode = "400", description = "Transição de status inválida")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<ProjetoResponseDTO> atualizarStatus(@PathVariable Long id, @Valid @RequestBody StatusProjetoRequestDTO dto){
        return ResponseEntity.ok(projetoService.atualizarStatus(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Excluir Projeto", description = "Exclui um projeto existente")
    @ApiResponse(responseCode = "204", description = "Projeto excluído com sucesso")
    @ApiResponse(responseCode = "404", description = "Projeto não encontrado")
    @ApiResponse(responseCode = "500", description = "Erro interno no servidor")
    public ResponseEntity<Void> excluir(@PathVariable Long id){
        projetoService.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

