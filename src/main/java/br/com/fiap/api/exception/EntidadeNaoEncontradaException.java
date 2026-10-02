package br.com.fiap.api.exception;

/** Lancada quando um registro buscado pelo id nao existe no banco. Gera HTTP 404. */
public class EntidadeNaoEncontradaException extends Exception {

    public EntidadeNaoEncontradaException(String mensagem) {
        super(mensagem);
    }
}
