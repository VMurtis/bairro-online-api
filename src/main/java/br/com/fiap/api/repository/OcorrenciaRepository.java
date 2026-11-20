package br.com.fiap.api.repository;

import br.com.fiap.api.model.Ocorrencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OcorrenciaRepository extends JpaRepository<Ocorrencia, Long> {

    List<Ocorrencia> findByUsuarioId(Long usuarioId);

    Optional<Ocorrencia> findByIdAndUsuarioId(Long id, Long usuarioId);
}