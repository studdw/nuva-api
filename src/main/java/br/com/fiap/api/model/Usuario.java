package br.com.fiap.api.model;

import java.time.LocalDate;

/** tb_nuva_usuario - guardiao com saldo de SoulCoins */
public class Usuario {

    private int id;
    private String nome;
    private String email;
    private Integer ligaId;
    private int saldo;
    private int pontosSemana;
    private LocalDate dataCadastro;

    public Usuario() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getLigaId() {
        return ligaId;
    }

    public void setLigaId(Integer ligaId) {
        this.ligaId = ligaId;
    }

    public int getSaldo() {
        return saldo;
    }

    public void setSaldo(int saldo) {
        this.saldo = saldo;
    }

    public int getPontosSemana() {
        return pontosSemana;
    }

    public void setPontosSemana(int pontosSemana) {
        this.pontosSemana = pontosSemana;
    }

    public LocalDate getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDate dataCadastro) {
        this.dataCadastro = dataCadastro;
    }
}
