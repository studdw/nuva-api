package br.com.fiap.api.exception;

/** Lancada pela camada BO quando uma validacao ou regra de negocio e violada. Gera HTTP 400. */
public class RegraNegocioException extends Exception {

    public RegraNegocioException(String mensagem) {
        super(mensagem);
    }
}
