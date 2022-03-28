package ru.skillbench.tasks.basics.math.util;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import ru.skillbench.tasks.basics.math.algebra.Matrix;
import ru.skillbench.tasks.basics.math.algebra.Vector;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Парсер JSON объектов для лабораторных
 */
public class ObjectParser {
    private final JSONParser parser;
    private String file;

    public ObjectParser(String path) {
        this.parser = new JSONParser();
        this.file = path;
    }

    public Object parse(String pathToFile) throws IOException, ParseException {
        this.file = pathToFile;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream(this.file);
        InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        BufferedReader bufferedReader = new BufferedReader(reader);
        return parser.parse(bufferedReader);
    }

    /**
     * Возвращает матрицу из JSON файла (должен быть ключ "matrix" в slau4.json)
     * @return матрица из JSON файла
     */
    public Matrix getMatrix() throws IOException, ParseException {
        Object obj = parse(file);
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray matrixString = (JSONArray) jsonObject.get("matrix");
        Iterator<JSONArray> iterator = matrixString.iterator();
        List<Vector> vectors = new ArrayList<>();
        while (iterator.hasNext()) {
            List<Double> doubles = Arrays.stream(iterator.next().toArray())
                .map(value -> Double.parseDouble(value.toString()))
                .collect(Collectors.toList());
            vectors.add(new Vector(doubles));
        }
        return new Matrix(vectors);
    }

    /**
     * Возвращает вектор из JSON файл (должен быть ключ "vector" в slau4.json)
     * @return вектор из JSON
     */
    public Vector getColumn() throws IOException, ParseException {
        Object obj = parse(file);
        JSONObject jsonObject = (JSONObject) obj;
        JSONArray vector = (JSONArray) jsonObject.get("vector");
        List<Double> doubles = Arrays.stream(vector.toArray())
            .map(value -> Double.parseDouble(value.toString()))
            .collect(Collectors.toList());
        return new Vector(doubles);
    }

    public double getEpsilon() throws IOException, ParseException {
        Object obj = parse(file);
        JSONObject jsonObject = (JSONObject) obj;
        return (Double) jsonObject.get("epsilon");
    }

}
