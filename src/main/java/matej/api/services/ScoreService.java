package matej.api.services;

import java.time.LocalDate;
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
import matej.models.Score;
import matej.utils.DateUtil;

/**
 * Service Class for managing {@link Score} repostiory
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

	public List<Score> getScores() {
		return scoreRepo.findAll();
	}

	public Score getScoreById(int id) {
		return scoreRepo.findById(id).get();
	}

	public void deleteScoreById(int id) {
		scoreRepo.deleteById(id);
	}

	public List<Map<String, String>> getScoreboard(int max) {
		List<Map<String, String>> scoreboard = new ArrayList<>();
		List<Score> scores = getCurrentWeekScores();
		for (Score score : scores) {
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
		List<Score> scores = getCurrentWeekScores();
		if (scores.size() > 0) leader = scores.get(0).getUser().getUsername();
		return leader;
	}

	public String getLastWeekLeader() {
		String leader = "";
		List<Score> scores = getLastWeekScores();
		if (scores.size() > 0) leader = scores.get(0).getUser().getUsername();
		return leader;
	}


	private List<Score> getCurrentWeekScores() {
		List<Score> currentWeekScores = scoreRepo.findAll();
		Queue<Score> queue = new LinkedList<>();
		LocalDate today = LocalDate.now();
		currentWeekScores.forEach(e -> {
			if (!(DateUtil.isSameWeek(e.getDate(), today))) {
				queue.add(e);
			}
		});
		currentWeekScores.removeAll(queue);
		Collections.sort(currentWeekScores, new Comparator<Score>() {
			@Override
			public int compare(Score s1, Score s2) {
				return (s2.getValue() - s1.getValue());
			}
		});
		return currentWeekScores;
	}

	private List<Score> getLastWeekScores() {
		List<Score> currentWeekScores = scoreRepo.findAll();
		Queue<Score> queue = new LinkedList<>();
		LocalDate today = LocalDate.now();
		currentWeekScores.forEach(e -> {
			if (!(DateUtil.isSameWeek(e.getDate(), today))) {
				queue.add(e);
			}
		});
		currentWeekScores.removeAll(queue);
		Collections.sort(currentWeekScores, new Comparator<Score>() {
			@Override
			public int compare(Score s1, Score s2) {
				return (s2.getValue() - s1.getValue());
			}
		});
		return currentWeekScores;
	}
}
