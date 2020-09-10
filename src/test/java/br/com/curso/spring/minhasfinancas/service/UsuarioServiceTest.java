package br.com.curso.spring.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.curso.spring.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.curso.spring.minhasfinancas.exception.RegraNegocioException;
import br.com.curso.spring.minhasfinancas.model.entity.Usuario;
import br.com.curso.spring.minhasfinancas.model.repository.UsuarioRepository;
import br.com.curso.spring.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
	
	
	
	@Test
	public void validaEmail() {
		
		//cenário		
		//repository.deleteAll();
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false); //Simula se retorna um email
		
		
		//ação
		
		
		//verificação
		assertDoesNotThrow(() -> service.validarEmail("usuario@usuario.com"), "");	
		
	}
	
	
	@Test
	public void deveLancarErroDeValidacaoDeEmailCadastrado() {
		
		//cenário
		//Usuario usuario = Usuario.builder().nome("usuario").email("usuario@usuario.com").build();
		//repository.save(usuario);
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true); //Simula se retorna um email

		
		//ação
		    Exception exception = assertThrows(
					RegraNegocioException.class,
					() -> service.validarEmail("usuario@usuario.com"));
		    
		    
		    	System.out.println("Exception lançada:"+ exception.getMessage());
		        assertTrue(exception.getMessage().contains("Já existe um usuário cadastrado com esse e-mail"));
	}
	
	
	@Test
	public void deveAutenticarUmUsuarioComSucesso() {
		
		//cenário
		String email = "usuario@usuario.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().nome("Usuário").email(email).senha(senha).id(1l).build();
		
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//ação
		Usuario result = service.autenticar(email, senha);
		
		
		//verificação
		Assertions.assertThat(result).isNotNull();
		
		assertDoesNotThrow(() -> service.autenticar(email, senha), "");	
		
	}
	
	@Test
	public void deveLancarErroQuandoNaoAcharUsuarioCadastradoComOEmailInformado() {
		
		//cenário
		//Simulando o retorno de um objeto vazio (Não achou o objeto no DB).
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//ação
		
		//verificação
		Exception exception = assertThrows(

			ErroAutenticacaoException.class,

				() -> service.autenticar("email@usuario.com", "senha"));

				assertTrue(exception.getMessage().contains("Usuário não encontrado com o e-mail informado"));
	}
	
	
	@Test
	public void deveLancarErroQuandoSenhaNaoBater() {
		
		//cenário
		String senha = "senha";
		Usuario usuario = Usuario.builder().nome("Usuario").senha(senha).email("email@usuario.com").id(1l).build();
		
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		
		//ação
		
		
		
		//verificação
		Exception exception = assertThrows(

				ErroAutenticacaoException.class,

					() -> service.autenticar("email@usuario.com", "senha123"));

					assertTrue(exception.getMessage().contains("Senha inválida"));
		
	}
	
	@Test
	public void deveSalvarUmUsuario() {
		
		//cenário
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().
				nome("Usuario").
				senha("senha").
				email("email@usuario.com").
				id(1l).
				build();
		
		
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//ação
		
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//verificacao
		
		Assertions.assertThat(usuarioSalvo).isNotNull();
		
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1l);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("Usuario");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@usuario.com");
		
		
		assertDoesNotThrow(() -> service.salvarUsuario(usuario), ""); 

	}
	
	@Test
	public void naoDeveSalvarUmUsuarioComEmailCadastrado() {
		
		String email = "email@usuario.com";
		
		//cenario
		Usuario usuario = Usuario.builder().
				nome("Usuario").
				senha("senha").
				email(email).
				id(1l).
				build();
		
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		
		//ação
		
		service.salvarUsuario(usuario);
		
		//verificação
		Mockito.verify(repository, Mockito.never()).save(usuario);
		
		
		Exception exception = assertThrows(

				RegraNegocioException.class,

					() -> service.salvarUsuario(usuario));

					assertTrue(exception.getMessage().contains("Senha inválida"));
	}	
		
}
	
