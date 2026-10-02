package br.com.fiap.api.model;

import java.math.BigDecimal;
import java.time.LocalDate;

/** tb_nuva_conversao - conversao de SoulCoins em desconto (100 SC = R$ 1,00) */
public class Conversao {

    private int id;
    private int usuarioId;
    private int coins;
    private BigDecimal valorDesconto;
    private LocalDate data;

    public Conversao() {
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

    public int getCoins() {
        return coins;
    }

    public void setCoins(int coins) {
        this.coins = coins;
    }

    public BigDecimal getValorDesconto() {
        return valorDesconto;
    }

    public void setValorDesconto(BigDecimal valorDesconto) {
        this.valorDesconto = valorDesconto;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }
}
