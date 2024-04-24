package dk.sdu.mmmi.cbse.scoringSystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class ScoringSystem {

	private int totalScore = 0;

	public static void main(String[] args) {
		SpringApplication.run(ScoringSystem.class, args);
	}
	@GetMapping("/update")
	public int updateScore(@RequestParam(value = "amount") int amount) {
		totalScore += amount;
		return totalScore ;
	}

	@GetMapping("/get")
	public int getScore() {
		return totalScore ;
	}

	@GetMapping("/reset")
	public int resetScore() {
		totalScore = 0;
		return totalScore;
	}
}
