package br.edu.ifrn.nc.portaltads.projects.infrastructure.repository;

import br.edu.ifrn.nc.portaltads.projects.infrastructure.entity.ProjetoProfessor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProjetoProfessorRepository extends JpaRepository<ProjetoProfessor, Long> {
    List<ProjetoProfessor> findByProjeto_id(Long projetoId);
}
