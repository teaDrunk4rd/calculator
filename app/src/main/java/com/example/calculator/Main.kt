package com.example.calculator
//
//class Main {
//    fun main() {
//        if (BuildConfig.DEBUG) {
//            runTests()
//        }
//
//        while (true) {
//            try {
//                val tree = ArithmeticTree(readLine()!!)
//                println(tree.result)
//            } catch (e: Exception) {
//                println("sooqa\n$e")
//            }
//        }
//    }
//
//    fun runTests() {
//        assert(ArithmeticTree("-5").result == -5.0)
//        assert(ArithmeticTree("-5*6").result == -30.0)
//        assert(ArithmeticTree("-5-33").result == -38.0)
//        assert(ArithmeticTree("-5*5*5*5*-5").result == 3125.0)
//        assert(ArithmeticTree("-5*5*5*5*5").result == ArithmeticTree("-5*5*-5*5*-5").result)
//
//        assert(ArithmeticTree("5").result == 5.0)
//        assert(ArithmeticTree("5+33").result == 38.0)
//        assert(ArithmeticTree("5*5*5*5*5").result == 3125.0)
//        assert(ArithmeticTree("4+45+89+46").result == 184.0)
//        assert(ArithmeticTree("7-(2*3)").result == 1.0)
//        assert(ArithmeticTree("(5+3)*5").result == 40.0)
//        assert(ArithmeticTree("((7+3)*5)+3").result == 53.0)
//        assert(ArithmeticTree("(7*2)+(2*3)").result == 20.0)
//        assert(ArithmeticTree("(((5+3)*5)/10)-(4*4)").result == -12.0)
//        assert(ArithmeticTree("(70*10)+((2*3)/6)").result == 701.0)
//        assert(ArithmeticTree("((85/2)+150)+((350-75)/2)").result == 330.0)
//        assert(ArithmeticTree("820-5*(81+4)+100-8+2*(3+4-1)").result == 499.0)
//        assert(ArithmeticTree("(934 - 500) * 23 + (2004 - 999) * 17 - 58 * 15").result == 26197.0)
//        assert(ArithmeticTree("(((5.87+3.99)*5.95)/10)-(4.89*4.45)").result == -15.8938)
//        assert(ArithmeticTree("(7*2.5)+(2*3)").result == 23.5)
//
//        assert(ArithmeticTree("(7*2.5)+(2*3)").result == ArithmeticTree("(7*2,5)+(2*3)").result)
//        assert(ArithmeticTree("(((5+3)))*5").result == ArithmeticTree("((((5+3)*5)))").result)
//        assert(ArithmeticTree("5+5*3").result == ArithmeticTree("5+(5*3)").result)
//        assert(ArithmeticTree("5+5*3").result == ArithmeticTree("5*3+5").result)
//        assert(ArithmeticTree("7*50+7*35").result == ArithmeticTree("7*(50+35)").result)
//        assert(ArithmeticTree("70*10+2*3/6").result == ArithmeticTree("(70*10)+((2*3)/6)").result)
//        assert(ArithmeticTree("(7*2)+(2/3)").result == ArithmeticTree("7*2+2/3").result)
//        assert(ArithmeticTree("(7*2)+(6/3)+(45+39)").result == ArithmeticTree("7*2+6/3+45+39").result)
//        assert(ArithmeticTree("(7*2)+(6/3*5)+(45+39)").result == ArithmeticTree("(7*2)+((6/3)*5)+(45+39)").result)
//        assert(ArithmeticTree("85/2+150+(350-75)/2").result == ArithmeticTree("((85/2)+150)+((350-75)/2)").result)
//
//        println("Tests succeed")
//    }
//}