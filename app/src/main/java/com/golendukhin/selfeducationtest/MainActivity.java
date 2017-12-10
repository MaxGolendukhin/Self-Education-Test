package com.golendukhin.selfeducationtest;

import android.content.DialogInterface;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    private final int firstQuestion = 0;
    private final int lastQuestion = 17;
    private int[][] answersWeights;
    private String options[][] = new String[18][3];
    private String questions[];

    private int questionNumber = 0;
    private boolean answers[][] = new boolean[18][3];

    private TextView questionTextView;
    private RadioButton firstOptionRadioButton, secondOptionRadioButton, thirdOptionRadioButton;
    private Button previousButton, nextButton, finishButton;
    private RadioGroup radioGroup;

    /**
     * Creates main activity. Initializes all views.
     * @param savedInstanceState of activity. If savedInstanceState is not null, rotation of device
     *                           happened. Data then fetched and applyed to activity.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            questionNumber = savedInstanceState.getInt("questionNumber");
            answers = (boolean[][]) savedInstanceState.getSerializable("answers");
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        questionTextView = findViewById(R.id.question_text_view);
        radioGroup = findViewById(R.id.radio_group);
        firstOptionRadioButton = findViewById(R.id.option_1_radio_button);
        secondOptionRadioButton = findViewById(R.id.option_2_radio_button);
        thirdOptionRadioButton = findViewById(R.id.option_3_radio_button);

        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);
        finishButton = findViewById(R.id.finish_button);

        setTestParameters();
        updateViews();

        if (isTestCompleted() && savedInstanceState != null)
            finishButton.setVisibility(View.VISIBLE);
    }

    /**
     * Triggered if screen has been rotated
     * @param outState used to save data, that will be used later after activity is recreated
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("questionNumber", questionNumber);
        outState.putSerializable("answers", answers);
        super.onSaveInstanceState(outState);
    }

    /**
     * Helper method to initialize variables, that match test passing
     */
    private void setTestParameters() {
        Resources resources = getResources();
        questions = resources.getStringArray(R.array.questions);

        TypedArray typedArray = resources.obtainTypedArray(R.array.options);
        int length = typedArray.length();
        for (int i = 0; i < length; ++i) {
            int id = typedArray.getResourceId(i, 0);
            options[i] = resources.getStringArray(id);
        }
        typedArray.recycle();

        answersWeights = new int[][]
                {{2, 1, 3},
                        {3, 2, 1},
                        {1, 2, 3},
                        {3, 2, 1},
                        {2, 3, 1},
                        {3, 2, 2},
                        {2, 3, 1},
                        {3, 2, 1},
                        {2, 3, 1},
                        {2, 3, 1},
                        {1, 2, 3},
                        {1, 3, 2},
                        {3, 2, 1},
                        {1, 3, 2},
                        {1, 3, 2},
                        {3, 2, 1},
                        {2, 1, 3},
                        {2, 3, 1},};
    }

    /**
     * Previous button pressed method.
     * Updates activity with previous question.
     */
    public void previous(View view) {
        questionNumber--;

        Animation animationOut = AnimationUtils.loadAnimation(this, R.anim.translate_right_off);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation animationIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_from_left);
                radioGroup.startAnimation(animationIn);
                updateViews();
            }
        });
        radioGroup.startAnimation(animationOut);
    }

    /**
     * Next button pressed method.
     * Updates activity with next question.
     */
    public void next(View view) {
        questionNumber++;

        Animation animationOut = AnimationUtils.loadAnimation(this, R.anim.translate_left_off);
        animationOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {}

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                Animation animationIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.translate_from_right);
                radioGroup.startAnimation(animationIn);
                updateViews();
            }
        });
        radioGroup.startAnimation(animationOut);
    }

    /**
     * Finish button pressed method.
     * Calculates results of passed test and shows dialog window with results
     */
    public void finish(View view) {
        String result = getString(R.string.dialog_message) + getResult() + "." + "\n\n" +
                getString(R.string.take_test_again);

        new AlertDialog.Builder(this, R.style.AlertDialogCustom)
                .setTitle(R.string.dialog_title)
                .setMessage(result)
                .setPositiveButton(getString(R.string.ok_label), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                })
                .setNegativeButton(getString(R.string.cancel_label), null)
                .create()
                .show();
    }

    /**
     * Launched when one of radio buttons is checked.
     * Checks if all questions are answered and shows button to finish test.
     * In case if user have answered last question, but have skipped one ore more questions before,
     * toast with notification appears.
     * Method is marked as unused, but don't believe to IntelliJ,onClick - tag is defined in styles.xml.
     */
    public void onRadioButtonClicked(View view) {
        updateAnswers();
        if (isTestCompleted()) {
            finishButton.setVisibility(View.VISIBLE);
        } else {
            if (lastQuestion == questionNumber) {
                Toast.makeText(this,
                        getString(R.string.toast), Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Helper method to update question text view and radio buttons to visualize test progress
     */
    private void updateViews() {
        previousButton.setVisibility(View.VISIBLE);
        nextButton.setVisibility(View.VISIBLE);
        if (questionNumber == firstQuestion) previousButton.setVisibility(View.GONE);
        else if (questionNumber == lastQuestion) nextButton.setVisibility(View.GONE);

        Animation animationFadeIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation animationFadeOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        questionTextView.setText(questions[questionNumber]);
        questionTextView.startAnimation(animationFadeOut);
        questionTextView.startAnimation(animationFadeIn);

        firstOptionRadioButton.setText(options[questionNumber][0]);
        secondOptionRadioButton.setText(options[questionNumber][1]);
        thirdOptionRadioButton.setText(options[questionNumber][2]);

        radioGroup.clearCheck();
        firstOptionRadioButton.setChecked(answers[questionNumber][0]);
        secondOptionRadioButton.setChecked(answers[questionNumber][1]);
        thirdOptionRadioButton.setChecked(answers[questionNumber][2]);
    }

    /**
     * Helper method.
     * Updates "answers" variable with options, user choose
     */
    private void updateAnswers() {
        answers[questionNumber][0] = firstOptionRadioButton.isChecked();
        answers[questionNumber][1] = secondOptionRadioButton.isChecked();
        answers[questionNumber][2] = thirdOptionRadioButton.isChecked();
    }

    /**
     * Checks if all questions have been answered
     * @return true if yes, false otherwise
     */
    private boolean isTestCompleted() {
        int testCompletion = -1;
        for (boolean[] answer : answers) {
            if (answer[0] || answer[1] || answer[2]) testCompletion++;
        }
        return testCompletion == lastQuestion;
    }

    /**
     * @return string with test results depending on test score
     */
    private String getResult() {
        int points = calculatePoints();
        if (points < 26) return getString(R.string.very_low);
        else if (points >= 26 && points < 29) return getString(R.string.low);
        else if (points >= 26 && points < 29) return getString(R.string.low);
        else if (points >= 29 && points < 32) return getString(R.string.below_average);
        else if (points >= 32 && points < 35) return getString(R.string.slightly_below_average);
        else if (points >= 35 && points < 38) return getString(R.string.average);
        else if (points >= 38 && points < 41) return getString(R.string.slightly_above_average);
        else if (points >= 41 && points < 44) return getString(R.string.above_average);
        else if (points >= 44 && points < 47) return getString(R.string.high);
        else return getString(R.string.very_high);
    }

    /**
     * @return score gained after test completely passed
     */
    private int calculatePoints() {
        int points = 0;
        for (int i = 0; i < answers.length; i++) {
            if (answers[i][0]) points += answersWeights[i][0];
            else if (answers[i][1]) points += answersWeights[i][1];
            else if (answers[i][2]) points += answersWeights[i][2];
        }
        return points;
    }
}