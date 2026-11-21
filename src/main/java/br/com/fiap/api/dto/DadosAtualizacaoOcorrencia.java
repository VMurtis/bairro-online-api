
package br.com.fiap.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DadosAtualizacaoOcorrencia(
        @NotBlank
        @Size(max = 255)
        String titulo,

        @NotBlank
        String categoria,

        @NotBlank
        @Size(max = 1000)
        String descricao,

        @Size(max = 200)
        String endereco
) {
}
