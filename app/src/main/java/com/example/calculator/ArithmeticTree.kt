package com.example.calculator

import java.util.*
import kotlin.math.max
import kotlin.math.min

class ArithmeticTree(expression: String) {  // static props and methods, readonly parameters in fun, readonly indexes in for loops
    private var top: Node

    private var pointer: Int = 0

    public var result: Double

    companion object {
        private val operations: List<Operation> = listOf(
            Operation('+', { x, y -> x + y }, Priority.LOW),
            Operation('-', { x, y -> x - y }, Priority.LOW),
            Operation('×', { x, y -> x * y }, Priority.HIGH),
            Operation('÷', { x, y -> x / y }, Priority.HIGH)
        )

        private val priorityOperations: List<Char> = operations.filter { it.priority == Priority.HIGH }.map { it.sign }

        private val nonPriorityOperations: List<Char> = operations.filter { it.priority == Priority.LOW }.map { it.sign }

        public val operationsRegex: Regex = Regex("[${operations.map { it.sign }.joinToString("\\")}]")

        private val numbersRegex: Regex = Regex("""[\d,.]""")

        private fun isNumberOrOpenBracket(expression: List<Char>, index: Int): Boolean {
            if (numbersRegex.containsMatchIn(expression[index].toString()) || expression[index] == '-' && index == 0)
                return true

            if (expression[index] != '-')
                return false

            return operationsRegex.containsMatchIn(expression[index - 1].toString())
                    || Regex("[(]").containsMatchIn(expression[index - 1].toString())
        }
    }

    init {
        val expressionList = prioritize(closeBrackets(expression.replace(" ", "")).toMutableList())
        expressionList.add(0, '(')
        expressionList.add(expressionList.size, ')')
        top = makeArithmeticTree(expressionList)
        result = getResultNode(top).value!!.toDouble()
    }

    private fun closeBrackets(_expression: String): String {
        var expression = _expression
        if (Regex("[(]").findAll(expression).count() == Regex("[)]").findAll(expression).count())
            return expression

        val brackets = Stack<Char>()
        for (symbol in expression)
        {
            if (symbol == '(')
                brackets.push('(')
            else if (symbol == ')')
                brackets.pop()
        }

        while (brackets.any())
        {
            brackets.pop()
            expression += ')'
        }

        return expression
    }

    private fun prioritize(expression: MutableList<Char>):  MutableList<Char> {
        var (leftIndex, rightIndex) = getBracketsIndexes(expression)

        if (leftIndex == rightIndex)  // одночлены
            return if (operationsRegex.findAll(expression.joinToString("")).count() <= 1)expression
                else insertBrackets(insertBrackets(expression, priorityOperations), nonPriorityOperations)

        do
        {
            val slice = prioritize(expression.slice(leftIndex + 1 until rightIndex) as MutableList<Char>)
            if (slice.size != rightIndex - leftIndex - 1)
            {
                expression.subList(leftIndex + 1, rightIndex).clear()
                rightIndex += slice.size - (rightIndex - leftIndex - 1)
                expression.addAll(leftIndex + 1, slice)
            }

            val pair = getBracketsIndexes(expression, rightIndex + 1)
            leftIndex = pair.first
            rightIndex = pair.second
        } while (leftIndex != rightIndex)

        // одночлены со скобками (уничтожает все символы внутри скобок в т ч вложенные, по крайней мере должно было)
        return if(operationsRegex.findAll(
                        Regex("""\((?>[^()]|\(|(?<o>)\))*\)(?>(o)(?!))""").replace(expression.joinToString(""), "")
                ).count() <= 1) expression
            else insertBrackets(insertBrackets(expression, priorityOperations), nonPriorityOperations)
    }

    private fun getBracketsIndexes(expression: List<Char>, startIndex: Int=0): Pair<Int, Int> {
        var leftIndex = startIndex

        while (leftIndex < expression.size && expression[leftIndex] != '(')
            leftIndex++

        if (leftIndex + 1 >= expression.size - 1) return Pair(-1, -1)

        val brackets = Stack<Char>()
        brackets.push('(')

        var rightIndex = leftIndex + 1
        while (rightIndex < expression.size && brackets.any()) {
            if (expression[rightIndex] == '(')
                brackets.push('(')
            else if (expression[rightIndex] == ')')
                brackets.pop()
            rightIndex++
        }

        return if(brackets.any()) Pair(-1, -1)
            else Pair(leftIndex, rightIndex - 1)
    }

    private fun insertBrackets(expression: MutableList<Char>, operations: Collection<Char>): MutableList<Char> {
        var i = -1
        while (++i < expression.size) {

            if (!operations.contains(expression[i])) continue

            var (leftIndex, rightIndex) = getIndexesForBrackets(expression, i)

            if (leftIndex == rightIndex
                || ((expression[leftIndex] == '(' || expression[rightIndex] == ')')
                        && !Regex("[()]")
                    .containsMatchIn(expression.subList(leftIndex + 1, rightIndex).joinToString("")))) continue

            expression.add(if(leftIndex == 0) 0 else leftIndex + 1, '(')
            rightIndex++
            expression.add(if(rightIndex == expression.size - 1) expression.size else rightIndex, ')')
            i++
        }

        return expression
    }

    private fun getIndexesForBrackets(expression: List<Char>, index: Int=0): Pair<Int, Int> {
        var leftIndex = max(0, index - 1)
        var rightIndex = min(expression.size - 1, index + 1)
        while (leftIndex > 0 && isNumberOrOpenBracket(expression, leftIndex))
            leftIndex--
        while (rightIndex < expression.size - 1 && isNumberOrOpenBracket(expression, rightIndex))
            rightIndex++

        val brackets = Stack<Char>()
        if (expression[leftIndex] == ')')
        {
            brackets.push(')')
            leftIndex--
            while (leftIndex >= 0 && brackets.any()) {
                if (expression[leftIndex] == ')')
                    brackets.push(')')
                else if (expression[leftIndex] == '(')
                    brackets.pop()
                leftIndex--
            }

            if (leftIndex < 0)
                leftIndex++
        }

        if (brackets.any()) return Pair(-1, -1)

        if (expression[rightIndex] == '(')
        {
            brackets.push('(')
            rightIndex++
            while(rightIndex < expression.size && brackets.any()) {
                if (expression[rightIndex] == '(')
                    brackets.push('(')
                else if (expression[rightIndex] == ')')
                    brackets.pop()
                rightIndex++
            }

            if (rightIndex == expression.size)
                rightIndex--
        }

        return if(brackets.any() || expression[index] == '-' && operationsRegex.containsMatchIn(expression[leftIndex].toString()))
            Pair(-1, -1)
            else Pair(leftIndex, rightIndex)
    }

    private fun makeArithmeticTree(expression: List<Char>): Node {
        val node = Node()
        if (expression.size == 3) // (5)
        {
            node.value = expression[1].toString()
            return node
        }

        if (pointer >= expression.size) return node

        var symbol = expression[pointer].toString()
        if (isNumberOrOpenBracket(expression, pointer))
        {
            while (isNumberOrOpenBracket(expression, pointer + 1))
            {
                pointer++
                symbol += if (expression[pointer] == ',') '.' else expression[pointer]
            }

            node.value = symbol
        }
        else
        {
            pointer++
            node.left = makeArithmeticTree(expression)

            if (pointer + 1 >= expression.size - 1) return node

            pointer++
            symbol = expression[pointer].toString()
            while (symbol == ")" && pointer < expression.size - 1)
            {
                pointer++
                symbol = expression[pointer].toString()
            }

            if (!operationsRegex.containsMatchIn(symbol)) return node

            node.value = symbol
            pointer++
            node.right = makeArithmeticTree(expression)
        }

        return node
    }

    private fun getResultNode(_node: Node): Node {
        var node = _node
        if (node.left != null && node.right == null && node.value == null)
            node = getResultNode(node.left!!)

        val cloneNode = Node(
            left = if (node.left != null) getResultNode(node.left!!) else null,
            right = if (node.right != null) getResultNode(node.right!!) else null,
            value = node.value
        )

        if (cloneNode.value?.length != 1 || !operationsRegex.containsMatchIn(cloneNode.value!!))
            return cloneNode

        return Node(
            value = operations.first { it.sign.toString() == cloneNode.value }.func.invoke
                (cloneNode.left?.value!!.toDouble(), cloneNode.right?.value!!.toDouble()).toString()
        )
    }
}