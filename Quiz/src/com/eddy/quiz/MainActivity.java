package com.eddy.quiz;

import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class MainActivity extends Activity {
	
	private final static int TURN_MAN = 0;
	private final static int TURN_WOMAN = 1;
	
	private Button btn_func_delete;
	private Button btn_func_delete2;
	private Button btn_func_assign;
	private Button btn_func_select;
	private Button btn_func_answer;
	
	private View player_man;
	private TextView man_score;
	private View player_woman;
	private TextView woman_score;
	
	private TextView text_question;
	private RadioGroup group_answer;
	private RadioButton[] btn_answer = new RadioButton[4];
	private int[] btn_answer_id = new int[]{R.id.text_ans_1, R.id.text_ans_2, R.id.text_ans_3, R.id.text_ans_4};
	
	private Dialog select_dialog;
	
	private ArrayList<Category> mCategoryList = new ArrayList<Category>();
	private Category mCategory;
	private QuestionAnswer mQuestionAnswer;
	private Player mMan = new Player();
	private Player mWoman = new Player();
	private int mTurn = TURN_MAN;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 將畫面由全螢幕呈現
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.activity_main);
		
		getView();
		loadFiles();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			new AlertDialog.Builder(this)
			.setTitle("訊息通知")
			.setMessage("請確定是否離開程式?")
			.setPositiveButton("確定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int whichButton) {
					MainActivity.this.finish();
					System.exit(0);
				}
			}).show();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	private void showSelectDialog() {
		LinearLayout select_layout = new LinearLayout(this);
		select_layout.setOrientation(LinearLayout.VERTICAL);
		select_layout.setPadding(20, 20, 20, 20);
		
		for (Category cat : mCategoryList) {
			Button btn = (Button)this.getLayoutInflater().inflate(R.layout.select_button, null);
			btn.setText(cat.name);
			btn.setTextColor(Color.BLACK);
			btn.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 40);
			btn.setTag(cat);
			btn.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mCategory = (Category)v.getTag();
					mQuestionAnswer = mCategory.getQuestion();
					showQuestion(mQuestionAnswer);
					select_dialog.dismiss();
				}
			});
			select_layout.addView(btn, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		}
		
		select_dialog = new Dialog(this);
		select_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		select_dialog.setContentView(select_layout);
		select_dialog.show();
	}
	
	private void clearQuestion() {
		text_question.setText("");
		for (RadioButton btn : btn_answer) {
			btn.setVisibility(View.VISIBLE);
			btn.setBackgroundColor(Color.TRANSPARENT);
			btn.setText("");
		}
		group_answer.clearCheck();
	}
	
	private void showQuestion(QuestionAnswer qa) {
		text_question.setText(qa.question);
		btn_answer[0].setText(qa.answer[0]);
		btn_answer[1].setText(qa.answer[1]);
		btn_answer[2].setText(qa.answer[2]);
		btn_answer[3].setText(qa.answer[3]);
	}
	
	private void showAnswer() {
		int answerId = btn_answer_id[mQuestionAnswer.right_answer];
		if (answerId == R.id.text_ans_1) {
			btn_answer[0].setBackgroundResource(R.drawable.shp_frame_red);
		} else if (answerId == R.id.text_ans_2) {
			btn_answer[1].setBackgroundResource(R.drawable.shp_frame_red);
		} else if (answerId == R.id.text_ans_3) {
			btn_answer[2].setBackgroundResource(R.drawable.shp_frame_red);
		} else if (answerId == R.id.text_ans_4) {
			btn_answer[3].setBackgroundResource(R.drawable.shp_frame_red);
		}
		
		int checkedId = group_answer.getCheckedRadioButtonId();
		if (answerId == checkedId) {
			Player player = getPlayer();
			player.score ++;
		}
	}
	
	private void showDeleteAnswer(int delete_items) {
		int count = 0;
		for (int i = 0; i < 4; i++) {
			if (i != mQuestionAnswer.right_answer) {
				btn_answer[i].setVisibility(View.INVISIBLE);
				count++;
			}
			if (count >= delete_items) {
				break;
			}
		}
	}
	
	private void updateFunctions() {
		Player player = getPlayer();
		btn_func_delete.setVisibility(player.function_used[0] ? View.INVISIBLE : View.VISIBLE);
		btn_func_delete2.setVisibility(player.function_used[1] ? View.INVISIBLE : View.VISIBLE);
		btn_func_assign.setVisibility(player.function_used[2] ? View.INVISIBLE : View.VISIBLE);
	}
	
	private Player getPlayer() {
		if (mTurn == TURN_MAN) {
			return mMan;
		} else {
			return mWoman;
		}
	}
	
	private void turnPlayer() {
		if (mTurn == TURN_MAN) {
			mTurn = TURN_WOMAN;
			player_woman.setBackgroundResource(R.drawable.shp_frame_blue);
			player_man.setBackgroundColor(Color.TRANSPARENT);
		} else {
			mTurn = TURN_MAN;
			player_man.setBackgroundResource(R.drawable.shp_frame_blue);
			player_woman.setBackgroundColor(Color.TRANSPARENT);
		}
		man_score.setText(String.valueOf(mMan.score));
		woman_score.setText(String.valueOf(mWoman.score));
	}
	
	private void getView() {
		btn_func_delete = (Button)this.findViewById(R.id.btn_func_delete);
		btn_func_delete.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getPlayer().function_used[0] = true;
				showDeleteAnswer(1);
				updateFunctions();
			}
		});
		btn_func_delete2 = (Button)this.findViewById(R.id.btn_func_delete2);
		btn_func_delete2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getPlayer().function_used[1] = true;
				showDeleteAnswer(2);
				updateFunctions();
			}
		});
		btn_func_assign = (Button)this.findViewById(R.id.btn_func_assign);
		btn_func_assign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getPlayer().function_used[2] = true;
				turnPlayer();
				updateFunctions();
			}
		});
		btn_func_select = (Button)this.findViewById(R.id.btn_func_select);
		btn_func_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearQuestion();
				showSelectDialog();
			}
		});
		btn_func_answer = (Button)this.findViewById(R.id.btn_func_answer);
		btn_func_answer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showAnswer();
				turnPlayer();
				updateFunctions();
			}
		});
		
		player_man = this.findViewById(R.id.player_man);
		man_score = (TextView)this.findViewById(R.id.man_score);
		player_woman = this.findViewById(R.id.player_woman);
		woman_score = (TextView)this.findViewById(R.id.woman_score);
		
		text_question = (TextView)this.findViewById(R.id.text_question);
		group_answer = (RadioGroup)this.findViewById(R.id.group_answer);
		btn_answer[0] = (RadioButton)this.findViewById(R.id.text_ans_1);
		btn_answer[1] = (RadioButton)this.findViewById(R.id.text_ans_2);
		btn_answer[2] = (RadioButton)this.findViewById(R.id.text_ans_3);
		btn_answer[3] = (RadioButton)this.findViewById(R.id.text_ans_4);
		group_answer.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				for (RadioButton btn : btn_answer) {
					btn.setBackgroundColor(Color.TRANSPARENT);
				}
				if (checkedId == R.id.text_ans_1) {
					btn_answer[0].setBackgroundResource(R.drawable.shp_frame_blue);
				} else if (checkedId == R.id.text_ans_2) {
					btn_answer[1].setBackgroundResource(R.drawable.shp_frame_blue);
				} else if (checkedId == R.id.text_ans_3) {
					btn_answer[2].setBackgroundResource(R.drawable.shp_frame_blue);
				} else if (checkedId == R.id.text_ans_4) {
					btn_answer[3].setBackgroundResource(R.drawable.shp_frame_blue);
				}
			}
		});
	}
	
	private void loadFiles() {
		String[] cat_file = readStringArrayFromAssetFile("category.txt");
		
	    for (String file : cat_file) {
	    	String[] tmp = file.split("=");
	    	mCategoryList.add(new Category(tmp[0], tmp[1]));
	    }
	}
	
	private String[] readStringArrayFromAssetFile(String filename) {
		AssetManager asset = this.getAssets();
	    String txt = "";
		try {
			InputStream in = asset.open(filename);
	        byte[] b = new byte[in.available()];
	        in.read(b);
	        txt = Utility.readString(b);
		} catch (Exception e) {
			return new String[0];
		}
	    return txt.split("\r\n");
	}
	
	private class Player {
		public boolean[] function_used = new boolean[3];
		public int score;
	}
	
	private class Category {
		public String name;
		public ArrayList<QuestionAnswer> list = new ArrayList<QuestionAnswer>();
		
		public Category(String title, String filename) {
			name = title;
			String[] strList = readStringArrayFromAssetFile(filename);
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
				mCategoryList.remove(this);
			}
			return qa;
		}
	}
	
	private class QuestionAnswer {
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
}
