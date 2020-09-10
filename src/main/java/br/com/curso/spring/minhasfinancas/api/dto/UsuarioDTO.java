package br.com.curso.spring.minhasfinancas.api.dto;

import br.com.curso.spring.minhasfinancas.model.entity.Usuario;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class UsuarioDTO {
	
	private String nome;
	private String email;
	private String senha;
	
	
	
	public Usuario convertDtoToUsuario(UsuarioDTO dto) {
		
		return Usuario.builder().
				nome(dto.getNome()).
				email(dto.getEmail()).
				senha(dto.getSenha()).
				build();
	}
	
	

}
