package com.eddy.quiz;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

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

    private View bingo_layout;
    private View quest_ans_layout;
    private TextView[][] bingo_text = new TextView[5][5];
    private TextView selected_bingo_text;

    private ArrayList<Category> mCategoryList = new ArrayList<Category>();
    private Category mCategory;
    private QuestionAnswer mQuestionAnswer;
    private Player mMan = new Player();
    private Player mWoman = new Player();
    private int mTurn = Player.TURN_MAN;

    private static final String KEY_BINGO_CAT_INDICES = "bingo_cat_indices";
    private static final String KEY_BINGO_STATUS = "bingo_status";
    private static final String KEY_TURN = "turn";
    private static final String KEY_MAN_SCORE = "man_score";
    private static final String KEY_WOMAN_SCORE = "woman_score";
    private static final String KEY_MAN_FUNC = "man_func";
    private static final String KEY_WOMAN_FUNC = "woman_func";

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 將畫面由全螢幕呈現
        //this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 修正Target35版面延展關係，改統一調整為顯示安全邊界
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        ViewCompat.setOnApplyWindowInsetsListener(getWindow().getDecorView(), new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets mInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
                v.setPadding(mInsets.left, mInsets.top, mInsets.right, mInsets.bottom);
                return WindowInsetsCompat.CONSUMED;
            }
        });

        this.setContentView(R.layout.activity_main);

        getView();
        loadFiles(savedInstanceState);
        
        if (savedInstanceState != null) {
            mTurn = savedInstanceState.getInt(KEY_TURN);
            mMan.score = savedInstanceState.getInt(KEY_MAN_SCORE);
            mWoman.score = savedInstanceState.getInt(KEY_WOMAN_SCORE);
            mMan.function_used = savedInstanceState.getBooleanArray(KEY_MAN_FUNC);
            mWoman.function_used = savedInstanceState.getBooleanArray(KEY_WOMAN_FUNC);
            
            man_score.setText(String.valueOf(mMan.score));
            woman_score.setText(String.valueOf(mWoman.score));
            updateFunctions();
            
            if (mTurn == Player.TURN_MAN) {
                player_man.setBackgroundResource(R.drawable.shp_frame_blue);
                player_woman.setBackgroundColor(Color.TRANSPARENT);
            } else {
                player_woman.setBackgroundResource(R.drawable.shp_frame_blue);
                player_man.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        int[] catIndices = new int[25];
        int[] status = new int[25];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int pos = i * 5 + j;
                Category cat = (Category) bingo_text[i][j].getTag();
                catIndices[pos] = mCategoryList.indexOf(cat);
                Object s = bingo_text[i][j].getTag(R.id.bingo_status);
                status[pos] = (s instanceof Integer) ? (Integer) s : -1;
            }
        }
        outState.putIntArray(KEY_BINGO_CAT_INDICES, catIndices);
        outState.putIntArray(KEY_BINGO_STATUS, status);
        outState.putInt(KEY_TURN, mTurn);
        outState.putInt(KEY_MAN_SCORE, mMan.score);
        outState.putInt(KEY_WOMAN_SCORE, mWoman.score);
        outState.putBooleanArray(KEY_MAN_FUNC, mMan.function_used);
        outState.putBooleanArray(KEY_WOMAN_FUNC, mWoman.function_used);
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
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCategory = (Category)v.getTag();
                    mQuestionAnswer = mCategory.getQuestion();
                    showQuestion(mQuestionAnswer);
                    select_dialog.dismiss();
                }
            });
            select_layout.addView(btn, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
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
            if (selected_bingo_text != null) {
                int color = (mTurn == Player.TURN_MAN) ? Color.BLUE : Color.MAGENTA;
                selected_bingo_text.setBackgroundColor(color);
                selected_bingo_text.setTag(R.id.bingo_status, mTurn);
                selected_bingo_text.setEnabled(false);
            }
            checkVictory();
        } else {
            showToast(R.string.toast_wrong_answer, false);
            if (selected_bingo_text != null) {
                selected_bingo_text.setBackgroundResource(R.drawable.shp_frame_blue);
                selected_bingo_text.setEnabled(true);
            }
        }
        mQuestionAnswer = null;
        selected_bingo_text = null;
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

        bingo_layout.setVisibility(View.VISIBLE);
        quest_ans_layout.setVisibility(View.GONE);
    }

    private void getView() {
        bingo_layout = this.findViewById(R.id.bingo_layout);
        quest_ans_layout = this.findViewById(R.id.quest_ans_layout);

        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int resId = getResources().getIdentifier("bingo_" + i + "_" + j, "id", getPackageName());
                bingo_text[i][j] = (TextView) this.findViewById(resId);
                bingo_text[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCategory = (Category) v.getTag();
                        if (mCategory.list.size() > 0) {
                            selected_bingo_text = (TextView) v;
                            mQuestionAnswer = mCategory.getQuestion();
                            clearQuestion();
                            showQuestion(mQuestionAnswer);
                            bingo_layout.setVisibility(View.GONE);
                            quest_ans_layout.setVisibility(View.VISIBLE);
                            v.setBackgroundColor(Color.YELLOW);
                            v.setEnabled(false);
                        } else {
                            showMessageDialog(R.string.msg_no_questions);
                        }
                    }
                });
            }
        }

        btn_func_delete = (Button)this.findViewById(R.id.btn_func_delete);
        btn_func_delete.setOnClickListener(new View.OnClickListener() {
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
        btn_func_delete2.setOnClickListener(new View.OnClickListener() {
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
        btn_func_assign.setVisibility(View.GONE);
        btn_func_assign.setOnClickListener(new View.OnClickListener() {
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
        btn_func_select.setVisibility(View.GONE);
        btn_func_select.setOnClickListener(new View.OnClickListener() {
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
        btn_func_answer.setOnClickListener(new View.OnClickListener() {
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
        group_answer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

    private void loadFiles(Bundle savedInstanceState) {
        String[] cat_file = Utility.readStringArrayFromAssetFile(this, "category.txt");

        for (String file : cat_file) {
            String[] tmp = file.split("=");
            mCategoryList.add(new Category(this, mCategoryList, tmp[0], tmp[1]));
        }

        if (savedInstanceState == null) {
            initBingo();
        } else {
            restoreBingo(savedInstanceState);
        }
    }

    private void initBingo() {
        ArrayList<Category> temp = new ArrayList<>(mCategoryList);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int index = (int) (Math.random() * temp.size());
                Category cat = temp.get(index);
                bingo_text[i][j].setText(cat.name);
                bingo_text[i][j].setTag(cat);
            }
        }
    }

    private void restoreBingo(Bundle savedInstanceState) {
        int[] catIndices = savedInstanceState.getIntArray(KEY_BINGO_CAT_INDICES);
        int[] status = savedInstanceState.getIntArray(KEY_BINGO_STATUS);
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                int pos = i * 5 + j;
                Category cat = mCategoryList.get(catIndices[pos]);
                bingo_text[i][j].setText(cat.name);
                bingo_text[i][j].setTag(cat);
                bingo_text[i][j].setTag(R.id.bingo_status, status[pos]);
                if (status[pos] == Player.TURN_MAN) {
                    bingo_text[i][j].setBackgroundColor(Color.BLUE);
                    bingo_text[i][j].setEnabled(false);
                } else if (status[pos] == Player.TURN_WOMAN) {
                    bingo_text[i][j].setBackgroundColor(Color.MAGENTA);
                    bingo_text[i][j].setEnabled(false);
                } else {
                    bingo_text[i][j].setBackgroundResource(R.drawable.shp_frame_blue);
                    bingo_text[i][j].setEnabled(true);
                }
            }
        }
    }

    private int getBingoStatus(int r, int c) {
        Object s = bingo_text[r][c].getTag(R.id.bingo_status);
        return (s instanceof Integer) ? (Integer) s : -1;
    }

    private void checkVictory() {
        int winner = -1;

        // 1. 檢查橫線與直線
        for (int i = 0; i < 5; i++) {
            // 橫線
            int rowS = getBingoStatus(i, 0);
            if (rowS != -1) {
                boolean rowWin = true;
                for (int j = 1; j < 5; j++) {
                    if (getBingoStatus(i, j) != rowS) {
                        rowWin = false;
                        break;
                    }
                }
                if (rowWin) winner = rowS;
            }

            // 直線
            int colS = getBingoStatus(0, i);
            if (colS != -1) {
                boolean colWin = true;
                for (int j = 1; j < 5; j++) {
                    if (getBingoStatus(j, i) != colS) {
                        colWin = false;
                        break;
                    }
                }
                if (colWin) winner = colS;
            }
            if (winner != -1) break;
        }

        // 2. 檢查斜線
        if (winner == -1) {
            int d1S = getBingoStatus(0, 0);
            if (d1S != -1) {
                boolean d1Win = true;
                for (int i = 1; i < 5; i++) {
                    if (getBingoStatus(i, i) != d1S) {
                        d1Win = false;
                        break;
                    }
                }
                if (d1Win) winner = d1S;
            }
        }

        if (winner == -1) {
            int d2S = getBingoStatus(0, 4);
            if (d2S != -1) {
                boolean d2Win = true;
                for (int i = 1; i < 5; i++) {
                    if (getBingoStatus(i, 4 - i) != d2S) {
                        d2Win = false;
                        break;
                    }
                }
                if (d2Win) winner = d2S;
            }
        }

        if (winner != -1) {
            showVictoryDialog(winner);
            return;
        }

        // 3. 檢查是否全盤填滿 (平手或比較分數)
        boolean allFilled = true;
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                if (getBingoStatus(i, j) == -1) {
                    allFilled = false;
                    break;
                }
            }
        }

        if (allFilled) {
            if (mMan.score > mWoman.score) winner = Player.TURN_MAN;
            else if (mWoman.score > mMan.score) winner = Player.TURN_WOMAN;
            else winner = 2; // 代表平手

            showVictoryDialog(winner);
        }
    }

    private void showVictoryDialog(int winner) {
        String message;
        if (winner == Player.TURN_MAN) message = "恭喜男生獲得勝利！";
        else if (winner == Player.TURN_WOMAN) message = "恭喜女生獲得勝利！";
        else message = "雙方平手！";

        new AlertDialog.Builder(this)
                .setTitle("遊戲結束")
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("重新開始", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(MainActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                        MainActivity.this.startActivity(intent);
                        MainActivity.this.finish();
                    }
                })
                .setNegativeButton("退出遊戲", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MainActivity.this.finish();
                    }
                })
                .show();
    }

}
