package com.eddy.quiz;

import java.util.ArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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
import android.widget.Toast;

public class MainActivity extends Activity {
	
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
	private int mTurn = Player.TURN_MAN;

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
			.setTitle(R.string.alert_title)
			.setMessage(R.string.alert_confirm_exit)
			.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
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
	
	private void showMessageDialog(int resId) {
		String message = getString(resId);
		new AlertDialog.Builder(this)
		.setTitle(R.string.alert_title)
		.setMessage(message)
		.setPositiveButton(R.string.alert_ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int whichButton) {
				dialog.dismiss();
			}
		}).show();
	}
	
	private void showSelectDialog() {
		LayoutInflater inflater = this.getLayoutInflater();
		View layout = inflater.inflate(R.layout.select_layout, null);
		LinearLayout select_layout = (LinearLayout)layout.findViewById(R.id.select_list);
		
		for (Category cat : mCategoryList) {
			Button btn = (Button)inflater.inflate(R.layout.select_button, null);
			btn.setText(cat.name);
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
		select_dialog.setContentView(layout);
		select_dialog.show();
	}
	
	private void showToast(int resId, boolean showLong) {
		Toast.makeText(this, resId, showLong ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
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
			showToast(R.string.toast_right_answer, false);
		} else {
			showToast(R.string.toast_wrong_answer, false);
		}
		mQuestionAnswer = null;
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
		if (mTurn == Player.TURN_MAN) {
			return mMan;
		} else {
			return mWoman;
		}
	}
	
	private void turnPlayer() {
		if (mTurn == Player.TURN_MAN) {
			mTurn = Player.TURN_WOMAN;
			player_woman.setBackgroundResource(R.drawable.shp_frame_blue);
			player_man.setBackgroundColor(Color.TRANSPARENT);
		} else {
			mTurn = Player.TURN_MAN;
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
				if (mQuestionAnswer != null) {
					getPlayer().function_used[0] = true;
					showDeleteAnswer(1);
					updateFunctions();
					showToast(R.string.toast_func_delete, true);
				}
			}
		});
		btn_func_delete2 = (Button)this.findViewById(R.id.btn_func_delete2);
		btn_func_delete2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mQuestionAnswer != null) {
					getPlayer().function_used[1] = true;
					showDeleteAnswer(2);
					updateFunctions();
					showToast(R.string.toast_func_delete2, true);
				}
			}
		});
		btn_func_assign = (Button)this.findViewById(R.id.btn_func_assign);
		btn_func_assign.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mQuestionAnswer != null) {
					getPlayer().function_used[2] = true;
					turnPlayer();
					updateFunctions();
					showToast(R.string.toast_func_assign, true);
				}
			}
		});
		btn_func_select = (Button)this.findViewById(R.id.btn_func_select);
		btn_func_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mQuestionAnswer == null) {
					if (mCategoryList.size() > 0) {
						clearQuestion();
						showSelectDialog();
					} else {
						showMessageDialog(R.string.msg_no_questions);
					}
				}
			}
		});
		btn_func_answer = (Button)this.findViewById(R.id.btn_func_answer);
		btn_func_answer.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mQuestionAnswer != null) {
					if (-1 != group_answer.getCheckedRadioButtonId()) {
						showAnswer();
						turnPlayer();
						updateFunctions();
					} else {
						showMessageDialog(R.string.msg_select_answer);
					}
				}
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
				if (mQuestionAnswer != null) {
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
			}
		});
	}
	
	private void loadFiles() {
		String[] cat_file = Utility.readStringArrayFromAssetFile(this, "category.txt");
		
	    for (String file : cat_file) {
	    	String[] tmp = file.split("=");
	    	mCategoryList.add(new Category(this, mCategoryList, tmp[0], tmp[1]));
	    }
	}
	
}
