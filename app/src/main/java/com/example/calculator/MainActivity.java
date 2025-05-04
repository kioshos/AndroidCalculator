package com.example.calculator;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    TextView textViewResult, textViewMemory;
    private StringBuilder stringBuilderInput = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textViewResult = findViewById(R.id.result_TextBox);
        textViewMemory = findViewById(R.id.memory_TextBox);
        setButtonListener();
    }
    public void setButtonListener(){
        int[] numberButtons = {
                R.id.button_Zero, R.id.button_One,R.id.button_Two,R.id.button_Three,R.id.button_Four,
                R.id.button_Five,R.id.button_Six,R.id.button_Seven,R.id.button_Eight, R.id.button_Nine, R.id.button_Dot
        };

        int[] operationsButtons = {
                R.id.button_Plus, R.id.button_Minus,R.id.button_Divide,
                R.id.button_Multiply, R.id.button_openBracket, R.id.button_closeBracket
        };

        View.OnClickListener numberListener = v -> {
            String value = ((TextView) v).getText().toString();
            stringBuilderInput.append(value);
            textViewResult.setText(stringBuilderInput.toString());
        };

        for (int id : numberButtons)
            findViewById(id).setOnClickListener(numberListener);

        View.OnClickListener operatorListener = v -> {
            String value = ((TextView) v).getText().toString();
            if (value.equals("X")) value = "*";
            stringBuilderInput.append(value);
            textViewResult.setText(stringBuilderInput.toString());
        };

        for (int id : operationsButtons)
            findViewById(id).setOnClickListener(operatorListener);

        findViewById(R.id.button_ClearButton).setOnClickListener(v -> {
            stringBuilderInput.setLength(0);
            textViewResult.setText("0");
            textViewMemory.setText("");
        });

        findViewById(R.id.button_Backspace).setOnClickListener(v -> {
            if (stringBuilderInput.length() > 0)
                stringBuilderInput.deleteCharAt(stringBuilderInput.length() - 1);
            textViewResult.setText(stringBuilderInput.length() > 0 ? stringBuilderInput.toString() : "0");
        });

        findViewById(R.id.button_Equals).setOnClickListener(v -> evaluate());
    }
    public double handling(final String str) {
        return new Object() {
            int pos = -1, ch;

            void nextChar() {
                ch = (++pos < str.length()) ? str.charAt(pos) : -1;
            }

            boolean erase(int charToEat) {
                while (ch == ' ') nextChar();
                if (ch == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char) ch);
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                while (true) {
                    if (erase('+')) x += parseTerm();
                    else if (erase('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                while (true) {
                    if (erase('*')) x *= parseFactor();
                    else if (erase('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (erase('+')) return parseFactor();
                if (erase('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (erase('(')) {
                    x = parseExpression();
                    erase(')');
                } else if ((ch >= '0' && ch <= '9') || ch == '.') {
                    while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
                    x = Double.parseDouble(str.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                return x;
            }
        }.parse();
    }

    private void evaluate() {
        try {
            String expression = stringBuilderInput.toString()
                    .replaceAll("×", "*")
                    .replaceAll("÷", "/");

            double result = handling(expression);
            textViewMemory.setText(expression);
            textViewResult.setText(String.valueOf(result));

            stringBuilderInput.setLength(0);
            stringBuilderInput.append(result);

        } catch (Exception e) {
            textViewResult.setText("Error");
            stringBuilderInput.setLength(0);
        }
    }
}