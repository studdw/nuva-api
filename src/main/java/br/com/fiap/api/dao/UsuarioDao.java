package br.com.fiap.api.dao;

import br.com.fiap.api.config.ConnectionFactory;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.model.Usuario;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

@Repository
public class UsuarioDao {

    private final ConnectionFactory connectionFactory;
    private final String INSERT_SQL = "insert into tb_nuva_usuario (cd_usuario, nm_usuario, ds_email, cd_liga, nr_saldo, nr_pontos_semana) values (sq_nuva_usuario.nextval, ?, ?, ?, ?, ?)";
    private final String SELECT_SQL = "select * from tb_nuva_usuario order by cd_usuario";
    private final String SELECT_BY_ID_SQL = "select * from tb_nuva_usuario where cd_usuario = ?";
    private final String SELECT_BY_LIGA_SQL = "select * from tb_nuva_usuario where cd_liga = ? order by nr_pontos_semana desc, nm_usuario";
    private final String UPDATE_SQL = "update tb_nuva_usuario set nm_usuario = ?, ds_email = ?, cd_liga = ? where cd_usuario = ?";
    private final String DELETE_SQL = "delete from tb_nuva_usuario where cd_usuario = ?";
    private final String CREDITAR_SQL = "update tb_nuva_usuario set nr_saldo = nr_saldo + ?, nr_pontos_semana = nr_pontos_semana + ? where cd_usuario = ?";
    // debito atomico: so atualiza se houver saldo suficiente
    private final String ESTORNAR_SQL = "update tb_nuva_usuario set nr_saldo = nr_saldo + ? where cd_usuario = ?";
    private final String DEBITAR_SQL = "update tb_nuva_usuario set nr_saldo = nr_saldo - ? where cd_usuario = ? and nr_saldo >= ?";

    public UsuarioDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void cadastrar(Usuario usuario) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[]{"cd_usuario"})) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            setLiga(stmt, 3, usuario.getLigaId());
            stmt.setInt(4, usuario.getSaldo());
            stmt.setInt(5, usuario.getPontosSemana());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) usuario.setId(rs.getInt(1));
            }
        }
    }

    public List<Usuario> listar() throws SQLException {
        return consultarLista(SELECT_SQL, null);
    }

    public List<Usuario> listarPorLiga(int ligaId) throws SQLException {
        return consultarLista(SELECT_BY_LIGA_SQL, ligaId);
    }

    public Usuario buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Usuário não encontrado");
                return getUsuario(rs);
            }
        }
    }

    public void atualizar(Usuario usuario) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, usuario.getNome());
            stmt.setString(2, usuario.getEmail());
            setLiga(stmt, 3, usuario.getLigaId());
            stmt.setInt(4, usuario.getId());
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Usuário não encontrado");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Usuário não encontrado");
        }
    }

    public void creditar(int id, int coins) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(CREDITAR_SQL)) {
            stmt.setInt(1, coins);
            stmt.setInt(2, coins);
            stmt.setInt(3, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Usuário não encontrado");
        }
    }

    /** @return true se debitou; false se o saldo era insuficiente */
    public boolean debitar(int id, int coins) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DEBITAR_SQL)) {
            stmt.setInt(1, coins);
            stmt.setInt(2, id);
            stmt.setInt(3, coins);
            return stmt.executeUpdate() > 0;
        }
    }

    /** Devolve coins ao saldo sem contar pontos da semana (estorno de conversao). */
    public void estornar(int id, int coins) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(ESTORNAR_SQL)) {
            stmt.setInt(1, coins);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }

    private List<Usuario> consultarLista(String sql, Integer parametro) throws SQLException {
        List<Usuario> lista = new ArrayList<>();
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(sql)) {
            if (parametro != null) stmt.setInt(1, parametro);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) lista.add(getUsuario(rs));
            }
        }
        return lista;
    }

    private void setLiga(PreparedStatement stmt, int indice, Integer ligaId) throws SQLException {
        if (ligaId == null) stmt.setNull(indice, Types.INTEGER);
        else stmt.setInt(indice, ligaId);
    }

    private Usuario getUsuario(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getInt("cd_usuario"));
        usuario.setNome(rs.getString("nm_usuario"));
        usuario.setEmail(rs.getString("ds_email"));
        int liga = rs.getInt("cd_liga");
        usuario.setLigaId(rs.wasNull() ? null : liga);
        usuario.setSaldo(rs.getInt("nr_saldo"));
        usuario.setPontosSemana(rs.getInt("nr_pontos_semana"));
        usuario.setDataCadastro(rs.getDate("dt_cadastro").toLocalDate());
        return usuario;
    }
}
