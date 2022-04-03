package ru.skillbench.tasks.basics.math.algebra;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Матрицы
 */
public class Matrix implements Cloneable{
    private List<Vector> matrix;

    public List<Vector> getMatrix() {
        return matrix;
    }

    public void setMatrix(List<Vector> matrix) {
        this.matrix = matrix;
    }

    public Matrix(List<Vector> matrix) {
        this.matrix = matrix;
    }

    public Matrix(int i, int j) {
        this.matrix = new ArrayList<>();
        for (int k = 0; k < i; k++) {
            this.matrix.add(new Vector(j));
        }
    }

    public Matrix() {
        this.matrix = new ArrayList<>();
    }

    public Vector getAt(int i) {
        return matrix.get(i);
    }

    public Double getElement(int i, int j) {
        return getAt(i).getAt(j);
    }

    public void setElement(int i, int j, Double value) {
        getAt(i).setAt(j, value);
    }

    public int sizeRows() {
        return matrix.size();
    }

    public int sizeColumns() {
        return getAt(0).size();
    }

    public static Matrix identityMatrix(int n) {
        Matrix mat = new Matrix(n, n);
        for (int i = 0; i < n; i++) {
            mat.setElement(i, i, 1D);
        }
        return mat;
    }

    public Matrix sum(Matrix argument) {
        return new Matrix(IntStream.range(0, sizeRows())
            .mapToObj(i -> getAt(i).sum(argument.getAt(i)))
            .collect(Collectors.toList()));
    }

    public void swapRows(int i, int j) {
        Collections.swap(getMatrix(), i, j);
    }

    public List<Object> luDecomposition(Matrix matrix) {
        Matrix LU = matrix.clone();
        Vector pivot = Vector.arange(0, sizeRows());

        for (int k = 0; k < LU.sizeRows() - 1; k++) {
            int maxRowIndex = LU.findMaxArgAtColumn(k, k);
            pivot.swap(k, maxRowIndex);
            LU.swapRows(k, maxRowIndex);
            for (int i = k+1; i < LU.sizeRows(); i++) {
                LU.setElement(i, k, LU.getElement(i, k)/LU.getElement(k, k));
                for (int j = k+1; j < LU.sizeRows(); j++) {
                    Double old = LU.getElement(i, j);
                    LU.setElement(i, j, old - LU.getElement(i, k)*LU.getElement(k, j));
                }
            }
        }

        return List.of(LU, pivot);
    }

    public double luDeterminant() {
        Matrix lu = (Matrix) luDecomposition(this).get(0);
        return IntStream.range(0, lu.sizeRows())
            .mapToDouble(value -> lu.getElement(value, value))
            .reduce(1, (a, b) -> a*b);
    }

    public Vector luDecompositionSolve(Vector rightSide) {
        List<Object> decompose = luDecomposition(this);
        Vector pivot = (Vector) decompose.get(1);
        Matrix LU = (Matrix) decompose.get(0);

        Vector y = rightSide.permutation(pivot);
        for (int i = 0; i < LU.sizeRows(); i++) {
            for (int j = 0; j < i; j++) {
                y.setAt(i, y.getAt(i)-LU.getElement(i, j)*y.getAt(j));
            }
        }

        for (int i = LU.sizeRows() - 1; i > -1; i--) {
            for (int j = i + 1; j < LU.sizeColumns(); j++) {
                y.setAt(i, y.getAt(i) - LU.getElement(i, j)*y.getAt(j));
            }
            y.setAt(i, y.getAt(i)/LU.getElement(i, i));
        }
        return y;
    }

    private int findMaxArgAtColumn(int k, int columnIndex) {
        int maxArg = 0;
        double max = Math.abs(getElement(k, 0));
        for (int i = k; i < sizeColumns(); i++) {
            if (Math.abs(getElement(i, columnIndex)) > max) {
                max = Math.abs(getElement(i, columnIndex));
                maxArg = i;
            }
        }
        return maxArg;
    }

    public Matrix multiply(Matrix left) {
        Matrix result = new Matrix(sizeRows(), left.sizeColumns());
        for (int i = 0; i < sizeRows(); i++) { // aRow
            for (int j = 0; j < left.sizeColumns(); j++) { // bColumn
                for (int k = 0; k < sizeColumns(); k++) { // aColumn
                    result.setElement(i, j, result.getElement(i, j) + getElement(i, k) * left.getElement(k, j));
                }
            }
        }
        return result;
    }

    public Matrix luInverse() {
        Matrix E = identityMatrix(sizeRows());
        List<Vector> vectors = new ArrayList<>();
        for (int i = 0; i < sizeRows(); i++) {
            vectors.add(luDecompositionSolve(E.getAt(i)));
        }
        return new Matrix(vectors).transpose();
    }

    public Matrix transpose() {
        Matrix copy = new Matrix(sizeColumns(), sizeRows());
        for (int i = 0; i < sizeRows(); i++) {
            for (int j = 0; j < sizeColumns(); j++) {
                copy.setElement(j, i, getElement(i, j));
            }
        }
        return copy;
    }

    public Vector getDiagonal() {
        Vector vector = new Vector();
        for (int i = 0; i < sizeRows(); i++) {
            vector.add(getElement(i, i));
        }
        return vector;
    }

    public Matrix multiplyByNumber(Double number) {
        Matrix result = clone();
        for (int i = 0; i < sizeRows(); i++) {
            for (int j = 0; j < sizeColumns(); j++) {
                result.setElement(i, j, getElement(i, j) * number);
            }
        }
        return result;
    }

    @Override
    public Matrix clone() {
        List<Vector> vectors = new ArrayList<>(matrix.size());
        matrix.forEach(vector -> vectors.add(vector.clone()));
        return new Matrix(vectors);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        matrix.forEach(vector -> stringBuilder.append(vector.toString()));
        return stringBuilder.toString();
    }
}
