package io.github.camatsuda.domain.repository;

import io.github.camatsuda.domain.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Produtos extends JpaRepository<Produto, Integer> {

}
