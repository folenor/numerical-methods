package ru.skillbench.tasks.basics.math.lab1.part5;

import org.json.simple.parser.ParseException;
import ru.skillbench.tasks.basics.math.solvers.Solver;
import ru.skillbench.tasks.basics.math.algebra.Matrix;
import ru.skillbench.tasks.basics.math.util.ObjectParser;

import java.io.IOException;
import java.util.List;

/**
 * QR разложение
 */
public class Lab1_5 {
    public static void main(String[] args) throws IOException, ParseException {
        ObjectParser parser = new ObjectParser("slau5.json");
        Matrix matrix = parser.getMatrix();
        System.out.println("Матрица:");
        System.out.println(matrix);
        double epsilon = parser.getEpsilon();

        List<Object> result = Solver.eigenValuesQR(matrix, epsilon);
        System.out.println("Число итераций: " + result.get(1));
        System.out.println("Собственные значения: " + result.get(0));
    }
}
