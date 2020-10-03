package matej.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import matej.models.GameForm;

public interface FormRepository extends JpaRepository<GameForm, Integer> {
	
}
