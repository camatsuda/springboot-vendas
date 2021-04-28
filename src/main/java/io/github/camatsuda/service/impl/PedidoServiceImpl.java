package io.github.camatsuda.service.impl;

import io.github.camatsuda.domain.entity.Cliente;
import io.github.camatsuda.domain.entity.ItemPedido;
import io.github.camatsuda.domain.entity.Pedido;
import io.github.camatsuda.domain.entity.Produto;
import io.github.camatsuda.domain.enums.StatusPedido;
import io.github.camatsuda.domain.repository.Clientes;
import io.github.camatsuda.domain.repository.ItemPedidos;
import io.github.camatsuda.domain.repository.Pedidos;
import io.github.camatsuda.domain.repository.Produtos;
import io.github.camatsuda.exception.PedidoNaoEncontradoException;
import io.github.camatsuda.exception.RegraNegocioException;
import io.github.camatsuda.rest.dto.ItemPedidoDTO;
import io.github.camatsuda.rest.dto.PedidoDTO;
import io.github.camatsuda.service.PedidoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PedidoServiceImpl implements PedidoService {

    @Autowired
    private Pedidos pedidos;

    @Autowired
    private Clientes clientes;

    @Autowired
    private Produtos produtos;

    @Autowired
    private ItemPedidos itemPedidos;

    @Override
    @Transactional
    public Pedido salvar(PedidoDTO dto) {
        Integer idCliente = dto.getCliente();
        Cliente cliente = clientes.findById(idCliente).orElseThrow(() -> new RegraNegocioException("Codigo de cliente inválido."));

        Pedido pedido = new Pedido();
        pedido.setTotal(dto.getTotal());
        pedido.setDataPedido(LocalDate.now());
        pedido.setCliente(cliente);
        pedido.setStatus(StatusPedido.REALIZADO);

        List<ItemPedido> itens = converterItens(pedido, dto.getItems());
        pedidos.save(pedido);
        itemPedidos.saveAll(itens);

        return pedido;
    }

    @Override
    public Optional<Pedido> obterPedidoCompleto(Integer id) {
        return pedidos.findByIdFetchItens(id);
    }

    @Override
    @Transactional
    public void atualizaStatus(Integer id, StatusPedido status) {
        pedidos
                .findById(id)
                .map(pedido -> {
                        pedido.setStatus(status);
                        return pedidos.save(pedido);
                    })
                .orElseThrow(() -> new PedidoNaoEncontradoException());
    }

    private List<ItemPedido> converterItens(Pedido pedido, List<ItemPedidoDTO> itens){
        if(itens.isEmpty()){
            throw  new RegraNegocioException("Não é possivel realizar pedido sem itens.");
        }
        return itens.stream().map(
                dto -> {
                    Integer idProduto = dto.getProduto();
                    Produto produto = produtos.findById(idProduto).orElseThrow(() -> new RegraNegocioException("Codigo de produto invalido"));
                    ItemPedido itemPedido = new ItemPedido();
                    itemPedido.setQuantidade(dto.getQuantidade());
                    itemPedido.setPedido(pedido);
                    itemPedido.setProduto(produto);
                    return itemPedido;
                }).collect(Collectors.toList());
    }
}
