package com.mily.springbootreview.data;

import com.mily.springbootreview.exceptions.DuplicateNumberException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "player")
public class Player {

    public Player() {
    }

    public Player(String playerId) {
        this.playerId = playerId;
    }

    @Id
    private String playerId;
    private String answer;

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        checkNumber(answer);
        this.answer = answer;
    }

    public String guessNumber(String guessNumber, Player opponent) {

        checkNumber(guessNumber);

        String answer = opponent.getAnswer();

        //完全符合
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

    private void checkNumber(String answer) {
        Set<Character> set = new HashSet<>();
        for (int i = 0; i < answer.length(); i++) {
            char c = answer.charAt(i);
            if (!set.add(c)) {
                throw new DuplicateNumberException("The answer must be 4 non-repeating digits.");
            }
        }
    }

    public boolean hasAnswer() {
        return this.answer != null && !this.answer.isBlank();
    }
    //result = player1.guessNumber(guessNumber,player2)
    // guessNumber == player2.answer 完全符合 -> 4A
    // 當猜謎者猜 1052 時，而答案為 8123，會得到結果 0A2B。

    // 把猜數的第一個數字 1 和答案的第一個數字 8 比對，
    // 如果猜數的第一個數字 == 答案的第一個數字 -> 1A
    // -> 若數字符合，猜數位置往下+1，重新和答案的每一位數字比對
    // -> 若數字不符合，將猜數的第一個數字和答案下一個數字比對

    // 把猜數的第一個數字 1 和答案的第二個數字 1 比對，
    // 如果猜數的第一個數字 == 答案的第二個數字 -> 1B
    // -> 若數字符合，猜數位置往下+1，重新和答案的每一位數字比對
    // -> 若數字不符合，將猜數的第二個數字和答案下一個數字比對

    // 把猜數的第二個數字 0 和答案的第一個數字 8 比對，
    // 如果猜數的第二個數字 == 答案的第一個數字 -> 1B
    // -> 若數字符合，猜數位置往下+1，重新和答案的每一位數字比對
    // -> 若數字不符合，將猜數的第二個數字和答案下一個數字比對
    //直到所有猜數的數字都和答案的數字比較完 -> 取得比對結果 xAyB
}
