package br.com.fiap.api.dao;

import br.com.fiap.api.config.ConnectionFactory;
import br.com.fiap.api.exception.EntidadeNaoEncontradaException;
import br.com.fiap.api.model.Liga;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
public class LigaDao {

    private final ConnectionFactory connectionFactory;
    private final String INSERT_SQL = "insert into tb_nuva_liga (cd_liga, nm_liga, ds_regiao, nr_max_participantes) values (sq_nuva_liga.nextval, ?, ?, ?)";
    private final String SELECT_SQL = "select l.*, (select count(*) from tb_nuva_usuario u where u.cd_liga = l.cd_liga) as qt_participantes from tb_nuva_liga l order by l.cd_liga";
    private final String SELECT_BY_ID_SQL = "select l.*, (select count(*) from tb_nuva_usuario u where u.cd_liga = l.cd_liga) as qt_participantes from tb_nuva_liga l where l.cd_liga = ?";
    private final String UPDATE_SQL = "update tb_nuva_liga set nm_liga = ?, ds_regiao = ?, nr_max_participantes = ? where cd_liga = ?";
    private final String DELETE_SQL = "delete from tb_nuva_liga where cd_liga = ?";

    public LigaDao(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void cadastrar(Liga liga) throws SQLException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(INSERT_SQL, new String[]{"cd_liga"})) {
            stmt.setString(1, liga.getNome());
            stmt.setString(2, liga.getRegiao());
            stmt.setInt(3, liga.getMaxParticipantes());
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) liga.setId(rs.getInt(1));
            }
        }
    }

    public List<Liga> listar() throws SQLException {
        List<Liga> lista = new ArrayList<>();
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_SQL);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) lista.add(getLiga(rs));
        }
        return lista;
    }

    public Liga buscar(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(SELECT_BY_ID_SQL)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) throw new EntidadeNaoEncontradaException("Liga não encontrada");
                return getLiga(rs);
            }
        }
    }

    public void atualizar(Liga liga) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(UPDATE_SQL)) {
            stmt.setString(1, liga.getNome());
            stmt.setString(2, liga.getRegiao());
            stmt.setInt(3, liga.getMaxParticipantes());
            stmt.setInt(4, liga.getId());
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Liga não encontrada");
        }
    }

    public void remover(int id) throws SQLException, EntidadeNaoEncontradaException {
        try (Connection conexao = connectionFactory.getConnection();
             PreparedStatement stmt = conexao.prepareStatement(DELETE_SQL)) {
            stmt.setInt(1, id);
            if (stmt.executeUpdate() == 0) throw new EntidadeNaoEncontradaException("Liga não encontrada");
        }
    }

    private Liga getLiga(ResultSet rs) throws SQLException {
        Liga liga = new Liga();
        liga.setId(rs.getInt("cd_liga"));
        liga.setNome(rs.getString("nm_liga"));
        liga.setRegiao(rs.getString("ds_regiao"));
        liga.setMaxParticipantes(rs.getInt("nr_max_participantes"));
        liga.setParticipantes(rs.getInt("qt_participantes"));
        return liga;
    }
}
