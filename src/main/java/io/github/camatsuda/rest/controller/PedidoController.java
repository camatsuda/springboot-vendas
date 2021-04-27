package io.github.camatsuda.rest.controller;

import io.github.camatsuda.domain.entity.ItemPedido;
import io.github.camatsuda.domain.entity.Pedido;
import io.github.camatsuda.domain.enums.StatusPedido;
import io.github.camatsuda.rest.dto.AtualizacaoStatusPedidoDTO;
import io.github.camatsuda.rest.dto.InformacoesItemPedidoDTO;
import io.github.camatsuda.rest.dto.InformacoesPedidoDTO;
import io.github.camatsuda.rest.dto.PedidoDTO;
import io.github.camatsuda.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import javax.validation.Valid;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Integer save (@RequestBody @Valid PedidoDTO dto){
        Pedido pedido = pedidoService.salvar(dto);
        return pedido.getId();
    }

    @GetMapping("/{id}")
    public InformacoesPedidoDTO getById(@PathVariable Integer id){
        return pedidoService.obterPedidoCompleto(id)
                .map(p-> converter(p))
                .orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Pedido nao encontrado."));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void udpdateStatus(@PathVariable Integer id, @RequestBody @Valid AtualizacaoStatusPedidoDTO dto){
        String status = dto.getNovoStatus();
        pedidoService.atualizaStatus(id, StatusPedido.valueOf(status));
    }

    private InformacoesPedidoDTO converter(Pedido pedido){
        return InformacoesPedidoDTO.builder()
                .codigo(pedido.getId())
                .dataPedido(pedido.getDataPedido().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                .nomeCliente(pedido.getCliente().getNome())
                .cpf(pedido.getCliente().getCpf())
                .total(pedido.getTotal())
                .itens(converter(pedido.getItens()))
                .status(pedido.getStatus().name())
                .build();
    }

    private List<InformacoesItemPedidoDTO> converter(List<ItemPedido> itens){
        if (CollectionUtils.isEmpty(itens)){
            return Collections.emptyList();
        }

        return itens.stream().map(
                item -> InformacoesItemPedidoDTO.builder()
                        .descricaoProduto(item.getProduto().getDescricao())
                        .precoUnitario(item.getProduto().getPreco())
                        .quantidade(item.getQuantidade())
                .build()
        ).collect(Collectors.toList());
    }
}
