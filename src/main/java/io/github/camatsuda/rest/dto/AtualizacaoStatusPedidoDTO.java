package io.github.camatsuda.rest.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class AtualizacaoStatusPedidoDTO {
    @NotEmpty(message = "Campo status é obrigatorio.")
    private String novoStatus;
}
