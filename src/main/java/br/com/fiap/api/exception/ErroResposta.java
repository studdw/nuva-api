package br.com.fiap.api.exception;

import java.time.LocalDateTime;

/** Corpo JSON padronizado devolvido em qualquer erro da API. */
public class ErroResposta {

    private final int status;
    private final String erro;
    private final String mensagem;
    private final String caminho;
    private final LocalDateTime dataHora = LocalDateTime.now();

    public ErroResposta(int status, String erro, String mensagem, String caminho) {
        this.status = status;
        this.erro = erro;
        this.mensagem = mensagem;
        this.caminho = caminho;
    }

    public int getStatus() { return status; }
    public String getErro() { return erro; }
    public String getMensagem() { return mensagem; }
    public String getCaminho() { return caminho; }
    public LocalDateTime getDataHora() { return dataHora; }
}
