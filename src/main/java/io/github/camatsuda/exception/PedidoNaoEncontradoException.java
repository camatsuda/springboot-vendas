package io.github.camatsuda.exception;

public class PedidoNaoEncontradoException extends RuntimeException{
    public PedidoNaoEncontradoException(){
        super("Pedido não encontrado.");
    }
}
