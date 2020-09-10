package br.com.curso.spring.minhasfinancas.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.curso.spring.minhasfinancas.api.dto.UsuarioDTO;
import br.com.curso.spring.minhasfinancas.exception.ErroAutenticacaoException;
import br.com.curso.spring.minhasfinancas.exception.RegraNegocioException;
import br.com.curso.spring.minhasfinancas.model.entity.Usuario;
import br.com.curso.spring.minhasfinancas.service.UsuarioService;

@RestController   //Possui o ResponseBody por default
@RequestMapping("/minhasfinancas/api/usuarios")  //Define a raiz da apis.
public class UsuarioController {
	
	
	private UsuarioService service;
	
	public UsuarioController(UsuarioService service){
		
		this.service = service;	
		
	}
	
	
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
	
	
	

}
