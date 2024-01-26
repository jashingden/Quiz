package com.eddy.quiz;

public class QuestionAnswer {
	
	public String question;
	public String[] answer = new String[4];
	public int right_answer;
	
	public QuestionAnswer(String quest, String[] ans) {
		question = quest;
		right_answer = Integer.parseInt(ans[0])-1;
		for (int i = 1; i < 5; i++) {
			String[] tmp = ans[i].split(",");
			answer[Integer.parseInt(tmp[0])-1] = "(" + tmp[0] + ") " + tmp[1];
		}
	}
}
