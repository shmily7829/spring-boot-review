package com.mily.springbootreview.entities;

import com.mily.springbootreview.exceptions.DuplicateNumberException;
import com.mily.springbootreview.exceptions.NotFoundException;
import com.mily.springbootreview.exceptions.NumberFormatException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "player")
@NoArgsConstructor
public class Player {

    @Id
    @Column(name = "player_id")
    private String playerId;

    @Column(name = "answer")
    private String answer;

    @Column(name = "game_Id")
    private String gameId;

    public Player(String gameId) {
        this.playerId = UUID.randomUUID().toString();
        this.gameId = gameId;
    }

    public String guessNumber(String guessNumber, Player opponent) {

        checkNumber(guessNumber);

        String answer = opponent.getAnswer();

        if (guessNumber.equals(answer)) {
            return "4A";
        }

        int a = 0;
        int b = 0;

        for (int i = 0; i < guessNumber.length(); i++) {
            char guessNum = guessNumber.charAt(i);
            char answerNum = answer.charAt(i);
            if (guessNum == answerNum) {
                a++;
            } else if (guessNumber.indexOf(answerNum) > 0) {
                b++;
            }
        }
        return String.format("%sA%sB", a, b);
    }

    public void setAnswer(String answer) {
        //答案已設置過
        if (hasAnswer()) {
            throw new NumberFormatException("The player has set up his answer. He can’t change his answer.");
        }
        checkNumber(answer);
        this.answer = answer;
    }

    public boolean hasAnswer() {
        return this.answer != null && !this.answer.isBlank();
    }

    private void checkNumber(String number) {

        if (number == null || number.isBlank()) {
            throw new NotFoundException("The player must set number before they guess.");
        }

        Set<Character> set = new HashSet<>();
        for (int i = 0; i < number.length(); i++) {
            char c = number.charAt(i);
            if (!set.add(c)) {
                throw new DuplicateNumberException("The number must be 4 non-repeating digits.");
            }
        }
    }

}
