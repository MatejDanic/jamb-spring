package matej.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import matej.models.GameDice;

public interface DiceRepository extends JpaRepository<GameDice, Integer> {
	
}
