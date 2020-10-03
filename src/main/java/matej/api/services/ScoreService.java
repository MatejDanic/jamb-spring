package matej.api.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import matej.api.repositories.ScoreRepository;
import matej.api.repositories.UserRepository;
import matej.models.GameScore;
import matej.utils.DateUtil;

/**
 * Service Class for managing {@link GameScore} repostiory
 *
 * @author MatejDanic
 * @version 1.0
 * @since 2020-08-20
 */
@Service
public class ScoreService {

	@Autowired
	ScoreRepository scoreRepo;

	@Autowired
	UserRepository userRepo;

	public List<GameScore> getScores() {
		return scoreRepo.findAll();
	}

	public GameScore getScoreById(int id) {
		return scoreRepo.findById(id).get();
	}

	public void deleteScoreById(int id) {
		scoreRepo.deleteById(id);
	}

	public List<Map<String, String>> getScoreboard(int max) {
		List<Map<String, String>> scoreboard = new ArrayList<>();
		List<GameScore> scores = getCurrentWeekScores();
		for (GameScore score : scores) {
			Map<String, String> scoreMap = new HashMap<>();
			scoreMap.put("username", score.getUser().getUsername());
			scoreMap.put("value", String.valueOf(score.getValue()));
			scoreboard.add(scoreMap);
			max--;
			if (max == 0) break;
		}
		return scoreboard;
	}

	public String getCurrentWeekLeader() {
		String leader = "";
		List<GameScore> scores = getCurrentWeekScores();
		if (scores.size() > 0) leader = scores.get(0).getUser().getUsername();
		return leader;
	}

	public String getLastWeekLeader() {
		String leader = "";
		List<GameScore> scores = getLastWeekScores();
		if (scores.size() > 0) leader = scores.get(0).getUser().getUsername();
		return leader;
	}


	private List<GameScore> getCurrentWeekScores() {
		List<GameScore> currentWeekScores = scoreRepo.findAll();
		Queue<GameScore> queue = new LinkedList<>();
		LocalDateTime today = LocalDateTime.now();
		currentWeekScores.forEach(e -> {
			if (!(DateUtil.isSameWeek(e.getDate(), today))) {
				queue.add(e);
			}
		});
		currentWeekScores.removeAll(queue);
		Collections.sort(currentWeekScores, new Comparator<GameScore>() {
			@Override
			public int compare(GameScore s1, GameScore s2) {
				return (s2.getValue() - s1.getValue());
			}
		});
		return currentWeekScores;
	}

	private List<GameScore> getLastWeekScores() {
		List<GameScore> currentWeekScores = scoreRepo.findAll();
		Queue<GameScore> queue = new LinkedList<>();
		LocalDateTime today = LocalDateTime.now();
		currentWeekScores.forEach(e -> {
			if (!(DateUtil.isSameWeek(e.getDate(), today))) {
				queue.add(e);
			}
		});
		currentWeekScores.removeAll(queue);
		Collections.sort(currentWeekScores, new Comparator<GameScore>() {
			@Override
			public int compare(GameScore s1, GameScore s2) {
				return (s2.getValue() - s1.getValue());
			}
		});
		return currentWeekScores;
	}
}
