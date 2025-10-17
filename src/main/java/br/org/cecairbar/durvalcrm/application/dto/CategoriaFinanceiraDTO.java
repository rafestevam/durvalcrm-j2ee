package br.org.cecairbar.durvalcrm.application.dto;

import br.org.cecairbar.durvalcrm.domain.model.TipoCategoriaFinanceira;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoriaFinanceiraDTO {

    private UUID id;

    @NotEmpty(message = "Nome é obrigatório")
    @Size(max = 100, message = "Nome deve ter no máximo 100 caracteres")
    private String nome;

    @Size(max = 500, message = "Descrição deve ter no máximo 500 caracteres")
    private String descricao;

    @NotNull(message = "Tipo é obrigatório")
    private TipoCategoriaFinanceira tipo;

    @Builder.Default
    private Boolean ativa = true;

    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
             message = "Cor deve ser um código hexadecimal válido (ex: #FF5733)")
    private String cor;

    private Instant criadoEm;
    private Instant atualizadoEm;

    // Factory method para criar nova categoria
    public static CategoriaFinanceiraDTO criar(String nome, String descricao,
                                                TipoCategoriaFinanceira tipo) {
        return CategoriaFinanceiraDTO.builder()
                .nome(nome)
                .descricao(descricao)
                .tipo(tipo)
                .ativa(true)
                .build();
    }
}
