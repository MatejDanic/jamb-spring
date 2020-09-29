package matej.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import matej.models.GameColumn;

public interface ColumnRepository extends JpaRepository<GameColumn, Integer> {
	
}
