package matej.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import matej.models.types.BoxType;

public interface BoxTypeRepository extends JpaRepository<BoxType, Integer> {
	
}
