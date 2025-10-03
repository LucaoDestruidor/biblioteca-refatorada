package cadastro.projeto_1.service;

import cadastro.projeto_1.dao.UsuarioDAO;
import cadastro.projeto_1.model.Aluno;
import cadastro.projeto_1.model.Usuario;
import java.sql.SQLException;
import java.util.List;

public class UsuarioService {
    private final UsuarioDAO usuarioDAO;

    public UsuarioService() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public Usuario autenticar(String login, String senha) throws SQLException {
        if (login == null || login.trim().isEmpty() || senha == null || senha.trim().isEmpty()) {
            // Em um sistema real, lançaríamos uma exceção aqui, mas para manter a compatibilidade com a LoginView original:
            return null;
        }
        return usuarioDAO.autenticar(login, senha);
    }
    
    public void cadastrarAluno(String nome, String login, String senha, String matricula) throws SQLException, IllegalArgumentException {
        if (nome.trim().isEmpty() || login.trim().isEmpty() || senha.trim().isEmpty() || matricula.trim().isEmpty()) {
            throw new IllegalArgumentException("Todos os campos são obrigatórios para o cadastro de Aluno.");
        }
        
        // Em um sistema robusto, adicionaríamos aqui:
        // 1. Verificação se o login já existe.
        // 2. Verificação se a matrícula já existe.
        
        Aluno novoAluno = new Aluno(0, nome, login, senha, matricula);
        usuarioDAO.cadastrar(novoAluno);
    }
    
    public List<Usuario> listarTodos() throws SQLException {
        return usuarioDAO.listarTodos();
    }
}