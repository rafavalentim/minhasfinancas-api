package br.com.curso.spring.minhasfinancas.model.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import br.com.curso.spring.minhasfinancas.model.entity.Usuario;


public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
	
	//Query Method
	
	Optional<Usuario> findByEmail(String email);
	
	boolean existsByEmail(String email);
	

}
