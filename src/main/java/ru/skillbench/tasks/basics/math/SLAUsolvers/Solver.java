package ru.skillbench.tasks.basics.math.SLAUsolvers;

import ru.skillbench.tasks.basics.math.algebra.Matrix;
import ru.skillbench.tasks.basics.math.algebra.Vector;

import java.util.List;
import java.util.Objects;
import java.util.stream.IntStream;

/**
 * todo Document type Solver
 */
public class Solver {
    public static Vector tridiagonalAlgorithm(Matrix matrix, Vector rightSide) {
        Vector x = new Vector();
        Vector y = new Vector();
        Vector a = new Vector();
        Vector b = new Vector();
        int n = matrix.sizeRows() - 1;
        y.add(matrix.getElement(0, 0));
        a.add(-matrix.getElement(0, 1)/y.getAt(0));
        b.add(rightSide.getAt(0)/y.getAt(0));
        for (int i = 1; i < n; i++) {
            y.add(matrix.getElement(i, i) + matrix.getElement(i, i-1)*a.getAt(i-1));
            a.add(-matrix.getElement(i, i+1)/y.getAt(i));
            b.add((rightSide.getAt(i) - matrix.getElement(i, i-1) * b.getAt(i-1))/y.getAt(i));
        }
        y.add(matrix.getElement(n, n) + matrix.getElement(n, n-1)*a.getAt(n-1));
        b.add((rightSide.getAt(n) - matrix.getElement(n, n-1)*b.getAt(n-1))/y.getAt(n));
        x.add(b.getAt(n));
        for (int i = 0; i < a.size(); i++) {
            x.add(x.getAt(x.size() - 1)*a.getAt(a.size() - i - 1) + b.getAt(b.size() - i - 2));
        }
        return x.inverse();
    }

    public static Vector simpleIterationMethod(Matrix matrix, Vector rightSide, Double epsilon) {
        List<Object> formed = getIterationForm(matrix, rightSide);
        Matrix copy = ((Matrix) formed.get(0)).clone();
        Matrix xOld = new Matrix(List.of((Vector) formed.get(1))).transpose();
        Matrix helper = new Matrix(List.of((Vector) formed.get(1))).transpose();
        int iteration = 0;
        double eps = 1;
        while (eps > epsilon) {
            Matrix xSave = xOld.clone();
            xOld = helper.sum(copy.multiply(xOld));
            Vector xOldVector = new Vector().inLineMatrix(xOld);
            Vector xSaveVector = new Vector().inLineMatrix(xSave);
            eps = xOldVector.sum(xSaveVector.multiplyByNumber(-1D)).norm();
            iteration++;
        }
        System.out.println("Число итераций: " + iteration);
        return new Vector().inLineMatrix(xOld);
    }

    public static Vector SeidelMethod(Matrix matrix, Vector rightSide, Double epsilon) {
        List<Object> formed = getIterationForm(matrix, rightSide);
        Matrix alpha = ((Matrix) formed.get(0)).clone();
        Vector b = ((Vector) formed.get(1)).clone();
        Vector xOld = b.clone();
        Vector xNew;
        int iteration = 0;
        double eps = 1;
        while (eps > epsilon) {
            xNew = new Vector(b.size());
            for (int i = 0; i < alpha.sizeRows(); i++) {
                for (int j = 0; j < alpha.sizeColumns(); j++) {
                    if (j < i) {
                        xNew.setAt(i, xNew.getAt(i) + alpha.getElement(i, j) * xNew.getAt(j));
                    } else {
                        xNew.setAt(i, xNew.getAt(i) + alpha.getElement(i, j) * xOld.getAt(j));
                    }
                }
                xNew.setAt(i, xNew.getAt(i) + b.getAt(i));
            }
            eps = xOld.sum(xNew.multiplyByNumber(-1D)).norm();
            iteration++;
            xOld = xNew.clone();
        }
        System.out.println("Число итераций: " + iteration);
        return xOld;
    }

    public static List<Object> rotationMethod(Matrix matrix, Double epsilon) {
        int size = matrix.sizeRows();
        Matrix clone = matrix.clone();
        Matrix eigenvectors = Matrix.identityMatrix(size);
        int a = 0;
        while (true) {
            a++;
            Matrix U = Matrix.identityMatrix(size);
            List<Integer> indexes = indexesOfMax(clone);
            Integer iMax = indexes.get(0);
            Integer jMax = indexes.get(1);
            double phi;
            if (Objects.equals(clone.getElement(iMax, iMax), clone.getElement(jMax, jMax))) {
                phi = Math.PI/4;
            } else {
                phi = 0.5 * Math.atan((2 * clone.getElement(iMax, jMax)) / (clone.getElement(iMax, iMax) - clone.getElement(jMax, jMax)));
            }
            U.setElement(iMax, iMax, Math.cos(phi));
            U.setElement(jMax, jMax, Math.cos(phi));
            U.setElement(iMax, jMax, -Math.sin(phi));
            U.setElement(jMax, iMax, Math.sin(phi));

            eigenvectors = eigenvectors.multiply(U);
            Matrix UT = U.transpose();
            clone = UT.multiply(clone).multiply(U);
            double epsilonCalculated = 0D;
            for (int i = 0; i < size; i++) {
                for (int j = 0; j < i; j++) {
                    epsilonCalculated += clone.getElement(i, j) * clone.getElement(i, j);
                }
            }
            epsilonCalculated = Math.sqrt(epsilonCalculated);
            if (epsilonCalculated < epsilon) {
                System.out.println("Число итераций: " + a);
                break;
            }
        }
        Vector eigenvalues = clone.getDiagonal();
        return List.of(eigenvalues, eigenvectors);
    }

    private static List<Integer> indexesOfMax(Matrix matrix) {
        int iMax = 0;
        int jMax = 0;
        Double aMax = 0D;
        for (int i = 0; i < matrix.sizeRows(); i++) {
            for (int j = i + 1; j < matrix.sizeRows(); j++) {
                if (Math.abs(matrix.getElement(i, j)) > aMax) {
                    aMax = Math.abs(matrix.getElement(i, j));
                    iMax = i;
                    jMax = j;
                }
            }
        }
        return List.of(iMax, jMax);
    }

    private static List<Object> getIterationForm(Matrix matrix, Vector rightSide) {
        Matrix formed = new Matrix(matrix.sizeRows(), matrix.sizeColumns());
        Vector formedVector = new Vector(rightSide.size());
        int counter = -1;
        for (int i = 0; i < matrix.sizeRows(); i++) {
            if (matrix.getElement(i, i) == 0) {
                counter = i;
                break;
            }
        }
        if (counter != -1) {
            matrix.swapRows(counter, counter-1);
            rightSide.swap(counter, counter-1);
        }
        for (int i = 0; i < matrix.sizeRows(); i++) {
            formedVector.setAt(i, rightSide.getAt(i)/matrix.getElement(i, i));
            for (int j = 0; j < matrix.sizeColumns(); j++) {
                formed.setElement(i, j, -matrix.getElement(i,j)/ matrix.getElement(i, i));
            }
            formed.setElement(i, i, 0D);
        }
        return List.of(formed, formedVector);
    }
}
