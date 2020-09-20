package br.com.curso.spring.minhasfinancas.api.contoller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.curso.spring.minhasfinancas.api.controller.UsuarioController;
import br.com.curso.spring.minhasfinancas.api.dto.UsuarioDTO;
import br.com.curso.spring.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.curso.spring.minhasfinancas.exception.RegraNegocioException;
import br.com.curso.spring.minhasfinancas.model.entity.Usuario;
import br.com.curso.spring.minhasfinancas.service.LancamentoService;
import br.com.curso.spring.minhasfinancas.service.UsuarioService;

@ExtendWith(SpringExtension.class) //Spring 2.2.x
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioController.class)
@AutoConfigureMockMvc
public class UsuarioControllerTest {
	
	static final String API = "/minhasfinancas/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;
	
	@Autowired
	MockMvc mvc; //irá mockar todas as chamadas aos métodos.
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService serviceLancamento;
	
	@Test
	public void deveAutenticarUmUsuario() throws Exception {
		
		//cenário
		String email = "usuario@usuario.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().
				email(email).
				senha(senha).
				build();
		
		Usuario usuario = Usuario.builder().
				email(email).
				senha(senha).
				id(1l).
				build();
		
		Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//Execução e verificação
		MockHttpServletRequestBuilder request =  MockMvcRequestBuilders   //Monta uma requisição mockada para a realização do teste
												.post(API.concat("/autenticar"))   
												.accept(JSON)
												.contentType(JSON)
												.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isOk())
		.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
		.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
		.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));

	}
	
	
	@Test
	public void deveRetornarUmBadRequestAoObterErroDeAutenticacao() throws Exception {
		
		//cenário
		String email = "usuario@usuario.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().
				email(email).
				senha(senha).
				build();
		
		Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacaoException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//Execução e verificação
		MockHttpServletRequestBuilder request =  MockMvcRequestBuilders   //Monta uma requisição mockada para a realização do teste
												.post(API.concat("/autenticar"))   
												.accept(JSON)
												.contentType(JSON)
												.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
	
	
	@Test
	public void deveCriarUmNovoUmUsuario() throws Exception {
		
		//cenário
		String email = "usuario@usuario.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().
				email(email).
				senha(senha).
				build();
		
		Usuario usuario = Usuario.builder().
				email(email).
				senha(senha).
				id(1l).
				build();
		
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//Execução e verificação
		MockHttpServletRequestBuilder request =  MockMvcRequestBuilders   //Monta uma requisição mockada para a realização do teste
												.post(API)   
												.accept(JSON)
												.contentType(JSON)
												.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isCreated())
		.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
		.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
		.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));

	}
	
	
	@Test
	public void deveRetornarUmBadRequestAoSalvarUmUsuario() throws Exception {
		
		//cenário
		String email = "usuario@usuario.com";
		String senha = "123";
		
		UsuarioDTO dto = UsuarioDTO.builder().
				email(email).
				senha(senha).
				build();
		
		
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		//Execução e verificação
		MockHttpServletRequestBuilder request =  MockMvcRequestBuilders   //Monta uma requisição mockada para a realização do teste
												.post(API)   
												.accept(JSON)
												.contentType(JSON)
												.content(json);
		
		mvc.perform(request)
		.andExpect(MockMvcResultMatchers.status().isBadRequest());

	}
	
	
	
	

}
