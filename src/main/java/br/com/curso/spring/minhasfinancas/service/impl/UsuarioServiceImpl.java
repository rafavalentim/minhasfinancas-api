package br.com.curso.spring.minhasfinancas.service.impl;

import java.util.Optional;

import org.aspectj.apache.bcel.Repository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.curso.spring.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.curso.spring.minhasfinancas.exception.RegraNegocioException;
import br.com.curso.spring.minhasfinancas.model.entity.Usuario;
import br.com.curso.spring.minhasfinancas.model.repository.UsuarioRepository;
import br.com.curso.spring.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {
	
	private UsuarioRepository repository;  //Realiza a interface com os modelos do banco.
	
	//Source > Generate Constructor using fields.
	public UsuarioServiceImpl(UsuarioRepository respository) {
		super();
		this.repository = respository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		
		Optional<Usuario> usuario =  repository.findByEmail(email);
		
		//Caso o usuário não exista
		if(!usuario.isPresent()) {
			throw new ErroAutenticacaoException("Usuário não encontrado com o e-mail informado");
		}
		
		//Caso a senha esteja incorreta
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacaoException("Senha inválida");
		}
		
		//Se tudo estiver ok, retorna o usuário.
		return usuario.get();
	}

	
	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		
		validarEmail(usuario.getEmail());
		
		return repository.save(usuario);

	}

	@Override
	public void validarEmail(String email) {

		boolean existe = repository.existsByEmail(email);
		
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com esse e-mail.");
		}
	}

}
