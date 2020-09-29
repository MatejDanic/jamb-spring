package matej.api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import matej.models.GameScore;

public interface ScoreRepository extends JpaRepository<GameScore, Integer> {
	
}
