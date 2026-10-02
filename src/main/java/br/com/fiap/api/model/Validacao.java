package br.com.fiap.api.model;

import java.time.LocalDateTime;

/** tb_nuva_validacao - envio de video para validacao da IA (rostoVisivel nao e persistido) */
public class Validacao {

    private int id;
    private int usuarioId;
    private int missaoId;
    private String nomeArquivo;
    private boolean rostoVisivel;
    private StatusValidacao status;
    private String mensagem;
    private LocalDateTime dataEnvio;

    public Validacao() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public int getMissaoId() {
        return missaoId;
    }

    public void setMissaoId(int missaoId) {
        this.missaoId = missaoId;
    }

    public String getNomeArquivo() {
        return nomeArquivo;
    }

    public void setNomeArquivo(String nomeArquivo) {
        this.nomeArquivo = nomeArquivo;
    }

    public boolean isRostoVisivel() {
        return rostoVisivel;
    }

    public void setRostoVisivel(boolean rostoVisivel) {
        this.rostoVisivel = rostoVisivel;
    }

    public StatusValidacao getStatus() {
        return status;
    }

    public void setStatus(StatusValidacao status) {
        this.status = status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public LocalDateTime getDataEnvio() {
        return dataEnvio;
    }

    public void setDataEnvio(LocalDateTime dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
}
