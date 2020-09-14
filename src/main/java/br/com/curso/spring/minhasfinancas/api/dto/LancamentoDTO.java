package br.com.curso.spring.minhasfinancas.api.dto;

import java.math.BigDecimal;
import br.com.curso.spring.minhasfinancas.service.UsuarioService;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LancamentoDTO {
	
	private  UsuarioService service;
	
	private Long id;
	
	private String descricao;
	
	private Integer mes;
	
	private Integer ano;
	
	private BigDecimal valor;
		
	private Long idUsuario;
	
	
	private String tipo;
	
	private String status;
	
}
