package br.com.curso.spring.minhasfinancas.api.controller;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.curso.spring.minhasfinancas.api.dto.UsuarioDTO;
import br.com.curso.spring.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.curso.spring.minhasfinancas.exception.RegraNegocioException;
import br.com.curso.spring.minhasfinancas.model.entity.Usuario;
import br.com.curso.spring.minhasfinancas.service.LancamentoService;
import br.com.curso.spring.minhasfinancas.service.UsuarioService;
import lombok.RequiredArgsConstructor;

@RestController   //Possui o ResponseBody por default
@RequestMapping("/minhasfinancas/api/usuarios")  //Define a raiz da apis.
@RequiredArgsConstructor //cria o construtor automaticamente com os parametros final

public class UsuarioController {
	
	
	private final UsuarioService service;
	private final LancamentoService lancamentoService;
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar(@RequestBody UsuarioDTO usuarioDTO) {
		
		try {
			
			Usuario usuarioAutenticado = service.autenticar(usuarioDTO.getEmail(), usuarioDTO.getSenha());
			
			return ResponseEntity.ok(usuarioAutenticado);
			
		}catch(ErroAutenticacaoException e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
			
		}
		
		
	}
	
	@PostMapping
	public ResponseEntity salvar(@RequestBody UsuarioDTO dto) { //RequestBody faz o parse do JSON
		
		Usuario usuario = dto.convertDtoToUsuario(dto);
		
		try {
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED); 
			
		}catch(RegraNegocioException e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
			
		}
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo(@PathVariable("id") Long id) {
		
		Optional<Usuario> usuario = service.obterPorId(id);
		
		if(!usuario.isPresent()) {
			return new ResponseEntity(HttpStatus.NOT_FOUND);
		}
		
		BigDecimal saldo = lancamentoService.obterSaldoPorUsuario(id);
		
		return ResponseEntity.ok(saldo);
	}
	
	
	

}
