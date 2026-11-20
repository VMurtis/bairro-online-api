package br.com.fiap.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosCadastroOcorrencia(
        @NotBlank
        @Size(min = 5, max = 100)
        String titulo,

        @NotBlank
        String categoria,

        @NotBlank
        @Size(min = 10, max = 1000)
        String descricao,

        @Size(max = 200)
        String endereco
) {
}