package ru.skillbench.tasks.basics.math.lab1.part3;

import org.json.simple.parser.ParseException;
import ru.skillbench.tasks.basics.math.SLAUsolvers.Solver;
import ru.skillbench.tasks.basics.math.algebra.Matrix;
import ru.skillbench.tasks.basics.math.algebra.Vector;
import ru.skillbench.tasks.basics.math.util.ObjectParser;

import java.io.IOException;

/**
 * Лабораторная 1.3. Метод простых итераций и Зейделя
 */
public class Lab1_3 {
    public static void main(String[] args) throws IOException, ParseException {
        ObjectParser parser = new ObjectParser("slau3.json");
        Matrix matrix = parser.getMatrix();
        System.out.println("Матрица:");
        System.out.println(matrix);
        Vector vector = parser.getColumn();
        System.out.println("Вектор столбец: " + vector);

        double epsilon = parser.getEpsilon();
        Vector result = Solver.simpleIterationMethod(matrix, vector, epsilon);
        System.out.println("Ответ: " + result);

        result = Solver.SeidelMethod(matrix, vector, epsilon);
        System.out.println("Методом Зейделя: " + result);
    }
}
