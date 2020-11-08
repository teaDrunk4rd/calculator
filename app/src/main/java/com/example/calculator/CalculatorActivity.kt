package com.example.calculator

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_calculator.*
import java.lang.Exception
import java.lang.NumberFormatException
import java.util.*

class CalculatorActivity : AppCompatActivity() {
    private val insertRules: List<Rule> = listOf(
        Rule({ b -> // +9 -> 9
            user_input.selectionStart == 0 && ArithmeticTree.operationsRegex.containsMatchIn(b.text)
        }, null),
        Rule({ b -> // 9) -> 9
            b.text == ")" && user_input.selectionStart != 0
        }, { b ->
            val brackets = Stack<Char>()
            brackets.push(')')
            var index = user_input.selectionStart - 1
            while (index >= 0 && brackets.any()) {
                if (user_input.text[index] == ')')
                    brackets.push(')')
                else if (user_input.text[index] == '(')
                    brackets.pop()
                index--
            }

            if (!brackets.any())
                setTextAndSelection(b, user_input.selectionStart + 1)
        }),
        Rule({ b -> // 9,, -> 9,
            b.text == "," && user_input.selectionStart != 0
                    && !ArithmeticTree.operationsRegex
                        .containsMatchIn(user_input.text[user_input.selectionStart - 1].toString())
        }, { b ->
            for (i in user_input.selectionStart - 1 downTo 0) {
                if (ArithmeticTree.operationsRegex.containsMatchIn(user_input.text[i].toString())
                    || Regex("""[()]""").containsMatchIn(user_input.text[i].toString())
                )
                    break
                if (user_input.text[i] == ',')
                    return@Rule
            }
            setTextAndSelection(b, user_input.selectionStart + 1)
        }),
        Rule({ b -> // 9+, -> 9+0,
            b.text == "," && (user_input.selectionStart == 0
                    || !Regex("""[\d]""").containsMatchIn(user_input.text[user_input.selectionStart - 1].toString()))
        }, { b ->
            setTextAndSelection(
                "${user_input.text.toString().substring(0, user_input.selectionStart)}0${b.text}",
                user_input.selectionStart + 2
            )
        }),
        Rule({ b -> // 9( -> 9*(
            b.text == "(" && user_input.selectionStart != 0
                    && !ArithmeticTree.operationsRegex.containsMatchIn(user_input.text[user_input.selectionStart - 1].toString())
        }, { b ->
            setTextAndSelection(
                "${user_input.text.toString().substring(0, user_input.selectionStart)}×${b.text}",
                user_input.selectionStart + 2
            )
        }),
        Rule({ b -> // 9+* -> 9*
            ArithmeticTree.operationsRegex.containsMatchIn(b.text)
                    && user_input.selectionStart != 0
                    && ArithmeticTree.operationsRegex.containsMatchIn(user_input.text[user_input.selectionStart - 1].toString())
        }, { b ->
            setTextAndSelection(
                user_input.text.toString().substring(0, user_input.selectionStart - 1) + b.text +
                        user_input.text.toString().substring(user_input.selectionStart, user_input.text.length),
                user_input.selectionStart
            )
        }),
        Rule({ b -> // 9+* -> 9*
            ArithmeticTree.operationsRegex.containsMatchIn(b.text)
                    && user_input.selectionStart != user_input.text.length
                    && ArithmeticTree.operationsRegex.containsMatchIn(user_input.text[user_input.selectionStart].toString())
        }, { b ->
            setTextAndSelection(
                user_input.text.toString().substring(0, user_input.selectionStart) + b.text +
                        user_input.text.toString().substring(user_input.selectionStart + 1, user_input.text.length),
                user_input.selectionStart
            )
        })
    )

    private val deleteRules: List<Rule> = listOf(
        Rule({ b -> // +9 -> 9
            user_input.selectionStart - 1 == 0
                    && ArithmeticTree.operationsRegex.containsMatchIn(user_input.text[user_input.selectionStart].toString())
        }, { b ->
            setTextAndSelection(
                user_input.text.toString().substring(0, user_input.selectionStart - 1) +
                        user_input.text.substring(user_input.selectionStart + 1, user_input.text.length),
                user_input.selectionStart - 1
            )
        })
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calculator)

        user_input.showSoftInputOnFocus = false
        user_input.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (ArithmeticTree.operationsRegex.containsMatchIn(s.toString())) {
                    try {
                        val expressionResult = ArithmeticTree(s.toString()).result
                        result.text = if (expressionResult % 1 == 0.0) expressionResult.toInt().toStringWithSpaces()
                            else expressionResult.toStringWithSpaces().replace('.', ',')
                    } catch (e: NumberFormatException) {
                        val toast = Toast.makeText(this@CalculatorActivity, R.string.division_by_zero_exception, Toast.LENGTH_SHORT)
                        toast.setGravity(Gravity.BOTTOM, 10, 10)
                        toast.show()
                        result.text = ""
                    } catch (e: Exception) {
                        result.text = ""
                    }
                } else {
                    result.text = ""
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    fun onClick(b: View?) {
        if (b !is Button || user_input.selectionStart != user_input.selectionEnd)
            return

        val rule = insertRules.firstOrNull { rule -> rule.condition(b) }
        if (rule != null) {
            rule.action?.invoke(b)
            return
        }

        setTextAndSelection(b, user_input.selectionStart + 1)
    }

    fun deleteSymbol(b: View?) {
        if (b !is Button || user_input.selectionStart != user_input.selectionEnd)
            return

        if (user_input.selectionStart - 1 < 0) return

        val rule = deleteRules.firstOrNull { rule -> rule.condition(b) }
        if (rule != null) {
            rule.action?.invoke(b)
            return
        }

        setTextAndSelection(
            user_input.text.toString().substring(0, user_input.selectionStart - 1) +
                    user_input.text.toString().substring(user_input.selectionStart, user_input.text.length),
            user_input.selectionStart - 1
        )
    }

    fun onChangeSign(b: View?) {
        if (b !is Button || user_input.selectionStart != user_input.selectionEnd)
            return

        val brackets = Stack<Char>()

        var replaceString = ""
        var border = user_input.selectionStart - 1
        var rightPadding = 1
        var selection = user_input.selectionStart

        loop@ for (i in user_input.selectionStart - 1 downTo 0) {
            when (user_input.text[i]) {
                ')' -> brackets.push(')')
                '(' -> brackets.pop()
            }
            if (!brackets.any()) {
                when (user_input.text[i]) {
                    '+' -> {
                        replaceString = "-"
                        break@loop
                    }
                    '-' -> {
                        replaceString = if (border - 1 > 0 && user_input.text[border - 1] == '(') {
                            border -= 1
                            rightPadding += 1
                            selection -= 2
                            ""
                        } else if (border == 0) {
                            selection -= 1
                            ""
                        } else {
                            "+"
                        }
                        break@loop
                    }
                    '×' -> {
                        replaceString = "×(-"
                        selection += 2
                        break@loop
                    }
                    '÷' -> {
                        replaceString = "÷(-"
                        selection += 2
                        break@loop
                    }
                }
            }
            border--
        }

        if (border < 0) {
            border = 0
            rightPadding = 0
            replaceString = "-"
            selection += 1
        }

        setTextAndSelection(
            user_input.text.toString().substring(0, border) + replaceString +
                    user_input.text.toString().substring(border + rightPadding),
            selection
        )
    }

    fun getResult(v: View?) {
        if (result.text == "") return

        user_input.setText(result.text)
        user_input.setSelection(result.text.length)
        result.text = ""
    }

    fun clearInput(v: View?) = user_input.text.clear()

    private fun setTextAndSelection(button: Button, selection: Int) {
        setTextAndSelection(
            user_input.text.toString().substring(0, user_input.selectionStart) + button.text +
                    user_input.text.toString().substring(user_input.selectionStart, user_input.text.length),
            selection
        )
    }

    private fun setTextAndSelection(text: String, selection: Int) {
        user_input.setText(text)
        user_input.setSelection(selection)
    }
}