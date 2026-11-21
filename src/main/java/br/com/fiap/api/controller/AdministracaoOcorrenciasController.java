package br.com.fiap.api.controller;

import br.com.fiap.api.dto.DadosListagemOcorrencia;
import br.com.fiap.api.model.Ocorrencia;
import br.com.fiap.api.model.StatusOcorrencia;
import br.com.fiap.api.repository.OcorrenciaRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/ocorrencias")
public class AdministracaoOcorrenciasController {

    private final OcorrenciaRepository repository;

    public AdministracaoOcorrenciasController(
            OcorrenciaRepository repository
    ) {
        this.repository = repository;
    }

    @GetMapping
    public List<DadosListagemOcorrencia> listarTodas() {
        return repository.findAll()
                .stream()
                .map(DadosListagemOcorrencia::new)
                .toList();
    }

    @PatchMapping("/{id}/status")
    @Transactional
    public ResponseEntity<DadosListagemOcorrencia> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusOcorrencia status
    ) {
        var resultado = repository.findById(id);

        if (resultado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ocorrencia ocorrencia = resultado.get();
        ocorrencia.atualizarStatus(status);

        return ResponseEntity.ok(
                new DadosListagemOcorrencia(ocorrencia)
        );
    }

    @PatchMapping("/{id}/mensagem")
    @Transactional
    public ResponseEntity<DadosListagemOcorrencia> atualizarMensagem(
            @PathVariable Long id,
            @RequestParam String mensagem
    ) {
        if (mensagem == null || mensagem.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        var resultado = repository.findById(id);

        if (resultado.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Ocorrencia ocorrencia = resultado.get();
        ocorrencia.atualizarMensagemAdministrador(mensagem.trim());

        return ResponseEntity.ok(
                new DadosListagemOcorrencia(ocorrencia)
        );
    }
}