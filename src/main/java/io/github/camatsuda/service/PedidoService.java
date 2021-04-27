package io.github.camatsuda.service;

import io.github.camatsuda.domain.entity.Pedido;
import io.github.camatsuda.domain.enums.StatusPedido;
import io.github.camatsuda.rest.dto.PedidoDTO;

import java.util.Optional;

public interface PedidoService {
    Pedido salvar(PedidoDTO dto);
    Optional<Pedido> obterPedidoCompleto(Integer id);
    void atualizaStatus (Integer id, StatusPedido status);

}
