package matej.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import matej.models.GameBox;

public interface BoxRepository extends JpaRepository<GameBox, Integer> {
	
}
