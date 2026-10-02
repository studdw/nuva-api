package br.com.fiap.api.dao;

import br.com.fiap.api.config.ConnectionFactory;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.model.Contato;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class ContatoDao {

    private final ConnectionFactory connectionFactory;
    private final String INSERT_SQL = "insert into tb_nuva_contato (cd_contato, nm_contato, ds_email, ds_assunto, ds_mensagem) values (sq_nuva_contato.nextval, ?, ?, ?, ?)";
    private final String SELECT_SQL = "select * from tb_nuva_contato order by dt_envio desc";
    private final String SELECT_BY_ID_SQL = "select * from tb_nuva_contato where cd_contato = ?";
    private final String UPDATE_SQL = "update tb_nuva_contato set nm_contato = ?, ds_email = ?, ds_assunto = ?, ds_mensagem = ? where cd_contato = ?";
    private final String DELETE_SQL = "delete from tb_nuva_contato where cd_contato = ?";

    public ContatoDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void cadastrar(Contato c) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[]{"cd_contato"})) {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getEmail());
            stmt.setString(3, c.getAssunto());
            stmt.setString(4, c.getMensagem());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) c.setId(rs.getInt(1));
            }
        }
    }

    public List<Contato> listar() throws SQLException {
        List<Contato> lista = new ArrayList<>();
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(getContato(rs));
        }
        return lista;
    }

    public Contato buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Contato não encontrado");
                return getContato(rs);
            }
        }
    }

    public void atualizar(Contato c) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, c.getNome());
            stmt.setString(2, c.getEmail());
            stmt.setString(3, c.getAssunto());
            stmt.setString(4, c.getMensagem());
            stmt.setInt(5, c.getId());
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Contato não encontrado");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Contato não encontrado");
        }
    }

    private Contato getContato(ResultSet rs) throws SQLException {
        Contato c = new Contato();
        c.setId(rs.getInt("cd_contato"));
        c.setNome(rs.getString("nm_contato"));
        c.setEmail(rs.getString("ds_email"));
        c.setAssunto(rs.getString("ds_assunto"));
        c.setMensagem(rs.getString("ds_mensagem"));
        c.setDataEnvio(rs.getTimestamp("dt_envio").toLocalDateTime());
        return c;
    }
}
