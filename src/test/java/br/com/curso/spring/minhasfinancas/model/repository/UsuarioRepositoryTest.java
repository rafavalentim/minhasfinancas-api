package br.com.curso.spring.minhasfinancas.model.repository;


import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
//import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import br.com.curso.spring.minhasfinancas.model.entity.Usuario;

//@RunWith(SpringRunner.class)   Spring 2.1.X
@ExtendWith(SpringExtension.class) //Spring 2.2.x
@ActiveProfiles("test") //Declaração explícita do profile de testes.
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE) //Não muda as configurações do H2 definida no propertie de test
public class UsuarioRepositoryTest {
	
	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager; //Configurado apenas para testes
	
	
	@Test
	public void verificaExistenciaDeEmail() {
		
		//cenário
		Usuario usuario = criarUsuarioTeste();
		//repository.save(usuario);
		entityManager.persist(usuario);
		
		//ação / execucao
		boolean resultado = repository.existsByEmail("usuario@usuario.com");
		
		
		//verificacao
		Assertions.assertThat(resultado).isTrue();
		
	}
	
	
	@Test
	public void retornaFalseSeNaoHouverEmailCadastrado() {
		
		//cenário
		//Usuario usuario = Usuario.builder().nome("Usuario").email("usuario@email.com").build();
		//repository.deleteAll();
		
		//ação / execucao
		boolean resultado = repository.existsByEmail("usuario@usuario.com");
		
		
		//verificacao
		Assertions.assertThat(resultado).isFalse();
		
	}
	
	@Test
	public void devePersistirUmUsuarioNaBase() {
		
		//cennário
		Usuario usuario = criarUsuarioTeste();
		
		//ação
		Usuario usuarioSalvo = entityManager.persist(usuario);
		
		//verificação
		Assertions.assertThat(usuarioSalvo.getId()).isNotNull();
		
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		
		//cenário
		Usuario usuario = criarUsuarioTeste();
		
		
		//ação
		Usuario usuarioSalvo = entityManager.persist(usuario);
		
		Optional<Usuario> usuarioRecuperado = repository.findByEmail("usuario@usuario.com");
		
		
		//verificação
		Assertions.assertThat(usuarioRecuperado.isPresent()).isTrue();
		
		
	}
	
	
	@Test
	public void deveRetornarVazioEmCasoDeUsuarioInexistente() {
		
		//cenário
		
		
		//ação
		
		
		Optional<Usuario> usuarioRecuperado = repository.findByEmail("usuario@usuario.com");
		
		
		//verificação
		Assertions.assertThat(usuarioRecuperado.isPresent()).isFalse();
		
		
	}
	
	
	
	
	
	public static Usuario criarUsuarioTeste() {
		
		Usuario usuario = Usuario.builder().
				nome("Usuario").
				email("usuario@usuario.com").
				senha("").
				build();
		
		return usuario;
		
	}
	
	

}
