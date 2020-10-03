package matej.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import matej.models.types.ColumnType;

public interface ColumnTypeRepository extends JpaRepository<ColumnType, Integer> {
	
}
