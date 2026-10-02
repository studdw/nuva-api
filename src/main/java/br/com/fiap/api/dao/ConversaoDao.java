package br.com.fiap.api.dao;

import br.com.fiap.api.config.ConnectionFactory;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.model.Conversao;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ConversaoDao {

    private final ConnectionFactory connectionFactory;
    private final String INSERT_SQL = "insert into tb_nuva_conversao (cd_conversao, cd_usuario, nr_coins, vl_desconto) values (sq_nuva_conversao.nextval, ?, ?, ?)";
    private final String SELECT_SQL = "select * from tb_nuva_conversao order by dt_conversao desc";
    private final String SELECT_BY_ID_SQL = "select * from tb_nuva_conversao where cd_conversao = ?";
    private final String SELECT_BY_USUARIO_SQL = "select * from tb_nuva_conversao where cd_usuario = ? order by dt_conversao desc, cd_conversao desc";
    private final String DELETE_SQL = "delete from tb_nuva_conversao where cd_conversao = ?";

    public ConversaoDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void cadastrar(Conversao c) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[]{"cd_conversao"})) {
            stmt.setInt(1, c.getUsuarioId());
            stmt.setInt(2, c.getCoins());
            stmt.setBigDecimal(3, c.getValorDesconto());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
        }
    }

    public List<Conversao> listar() throws SQLException {
        return consultarLista(SELECT_SQL, null);
    }

    public List<Conversao> listarPorUsuario(int usuarioId) throws SQLException {
        return consultarLista(SELECT_BY_USUARIO_SQL, usuarioId);
    }

    public Conversao buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Conversão não encontrada");
                return getConversao(rs);
            }
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Conversão não encontrada");
        }
    }

    private List<Conversao> consultarLista(String sql, Integer parametro) throws SQLException {
        List<Conversao> lista = new ArrayList<>();
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (parametro != null) stmt.setInt(1, parametro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(getConversao(rs));
            }
        }
        return lista;
    }

    private Conversao getConversao(ResultSet rs) throws SQLException {
        Conversao c = new Conversao();
        c.setId(rs.getInt("cd_conversao"));
        c.setUsuarioId(rs.getInt("cd_usuario"));
        c.setCoins(rs.getInt("nr_coins"));
        c.setValorDesconto(rs.getBigDecimal("vl_desconto"));
        c.setData(rs.getDate("dt_conversao").toLocalDate());
        return c;
    }
}
