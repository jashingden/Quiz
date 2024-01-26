package com.eddy.quiz;

import java.util.ArrayList;

import android.content.Context;

import com.eddy.quiz.QuestionAnswer;

public class Category {
	
	public ArrayList<Category> categoryList;
	
	public String name;
	public ArrayList<QuestionAnswer> list = new ArrayList<QuestionAnswer>();
	
	public Category(Context context, ArrayList<Category> catList, String title, String filename) {
		categoryList = catList;
		name = title;
		String[] strList = Utility.readStringArrayFromAssetFile(context, filename);
		String question = null;
		String[] answer;
		for (String str : strList) {
			if (str.startsWith("Q:")) {
				question = str.substring(2);
				answer = null;
			} else if (str.startsWith("A:")) {
				answer = str.substring(2).split(";");
				try {
					list.add(new QuestionAnswer(question, answer));
				} catch (Exception e) {
				}
			}
		}
	}
	
	public QuestionAnswer getQuestion() {
		int count = list.size();
		int index = (int)(Math.random()* count);
		QuestionAnswer qa = list.remove(index);
		if (list.size() <= 0) {
			categoryList.remove(this);
		}
		return qa;
	}
}
