package br.edu.ifrn.nc.portaltads.projects.infrastructure.repository;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.Projetos;
import br.edu.ifrn.nc.portaltads.projects.infrastructure.enums.StatusProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoRepository extends JpaRepository<Projetos, Long> {

    boolean existsByNome(String nome);
    boolean existsByNomeAndIdNot(String nome, Long id);
    List<Projetos> findByStatusProjeto(StatusProjeto statusProjeto);
}
