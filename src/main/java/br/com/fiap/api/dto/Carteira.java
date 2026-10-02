package br.com.fiap.api.dto;

import br.com.fiap.api.model.Conversao;

import java.math.BigDecimal;
import java.util.List;

/** Resumo da carteira de SoulCoins exibido na pagina /carteira do front-end. */
public class Carteira {

    private final int usuarioId;
    private final int saldo;
    private final BigDecimal saldoEmReais;
    private final List<Conversao> conversoes;

    public Carteira(int usuarioId, int saldo, BigDecimal saldoEmReais, List<Conversao> conversoes) {
        this.usuarioId = usuarioId;
        this.saldo = saldo;
        this.saldoEmReais = saldoEmReais;
        this.conversoes = conversoes;
    }

    public int getUsuarioId() { return usuarioId; }
    public int getSaldo() { return saldo; }
    public BigDecimal getSaldoEmReais() { return saldoEmReais; }
    public List<Conversao> getConversoes() { return conversoes; }
}
