package br.edu.ifrn.nc.portaltads.projects.infrastructure.repository;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.Projetos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProjetoRepository extends JpaRepository<Projetos, Long> {

    boolean existsByNome(String nome);
    boolean existsByNomeAndIdNot(String nome, Long id);
}
