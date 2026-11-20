package br.com.fiap.api.dto;

import br.com.fiap.api.model.Ocorrencia;
import br.com.fiap.api.model.StatusOcorrencia;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;

public record DadosListagemOcorrencia(
        Long id,
        String titulo,
        String categoria,
        String descricao,
        String endereco,
        StatusOcorrencia status,
        String mensagemAdministrador,
        Long usuarioId,
        String nomeUsuario,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm")
        LocalDateTime dataRegistro,
        String fotoUrl
) {
    public DadosListagemOcorrencia(Ocorrencia ocorrencia) {
        this(
                ocorrencia.getId(),
                ocorrencia.getTitulo(),
                ocorrencia.getCategoria(),
                ocorrencia.getDescricao(),
                ocorrencia.getEndereco(),
                ocorrencia.getStatus(),
                ocorrencia.getMensagemAdministrador(),
                ocorrencia.getUsuario().getId(),
                ocorrencia.getUsuario().getNome(),
                ocorrencia.getDataRegistro(),
                ocorrencia.getFotoUrl()
        );
    }
}