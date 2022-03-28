package ru.skillbench.tasks.basics.math.lab1.part4;

import org.json.simple.parser.ParseException;
import ru.skillbench.tasks.basics.math.SLAUsolvers.Solver;
import ru.skillbench.tasks.basics.math.algebra.Matrix;
import ru.skillbench.tasks.basics.math.algebra.Vector;
import ru.skillbench.tasks.basics.math.util.ObjectParser;

import java.io.IOException;
import java.util.List;

/**
 * Лабораторная 1.4 метод вращения
 */
public class Lab1_4 {
    public static void main(String[] args) throws IOException, ParseException {
        ObjectParser parser = new ObjectParser("slau4.json");
        Matrix matrix = parser.getMatrix();
        System.out.println("Матрица:");
        System.out.println(matrix);
        double epsilon = parser.getEpsilon();

        List<Object> result = Solver.rotationMethod(matrix, epsilon);
        System.out.println("Собственные значения : " + result.get(0));
        System.out.println("Собвтенные векторы: " + result.get(1));
    }
}
