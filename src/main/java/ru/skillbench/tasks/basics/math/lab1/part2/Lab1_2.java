package ru.skillbench.tasks.basics.math.lab1.part2;

import org.json.simple.parser.ParseException;
import ru.skillbench.tasks.basics.math.SLAUsolvers.Solver;
import ru.skillbench.tasks.basics.math.algebra.Matrix;
import ru.skillbench.tasks.basics.math.algebra.Vector;
import ru.skillbench.tasks.basics.math.util.ObjectParser;

import java.io.IOException;

/**
 * Лабораторная 1.2, метод прогонки
 */
public class Lab1_2 {
    public static void main(String[] args) throws IOException, ParseException {
        ObjectParser parser = new ObjectParser("slau2.json");
        Matrix matrix = parser.getMatrix();
        System.out.println("Матрица:");
        System.out.println(matrix);
        Vector vector = parser.getColumn();
        System.out.println("Вектор столбец: " + vector);
        Vector result = Solver.tridiagonalAlgorithm(matrix, vector);
        System.out.println("Ответ: " + result);
    }
}
