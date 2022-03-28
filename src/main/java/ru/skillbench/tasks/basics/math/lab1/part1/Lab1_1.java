package ru.skillbench.tasks.basics.math.lab1.part1;

import org.json.simple.parser.ParseException;
import ru.skillbench.tasks.basics.math.algebra.Matrix;
import ru.skillbench.tasks.basics.math.algebra.Vector;
import ru.skillbench.tasks.basics.math.util.ObjectParser;

import java.io.IOException;

public class Lab1_1 {

    public static void main(String[] args) throws IOException, ParseException {
        ObjectParser parser = new ObjectParser("slau1.json");
        Matrix matrix = parser.getMatrix();
        System.out.println("Матрица:");
        System.out.println(matrix);
        Vector vector = parser.getColumn();
        System.out.println("Вектор столбец: " + vector);

        Vector result = matrix.luDecompositionSolve(vector);
        System.out.println("Ответ: " + result);

        System.out.println("Определитель: " + matrix.luDeterminant());

        System.out.println("Обратная матрица: ");
        System.out.println(matrix.luInverse());
    }
}
