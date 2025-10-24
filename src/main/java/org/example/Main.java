package org.example;
import java.io.*;
import java.util.*;


public class Main {
    private static final Filling fillingFlasks = new Filling();
    /**
     * Метод - стартовая точка программы
     * @param args - аргументы командной строки
     * @throws IOException - ошибка ввода/вывода
     */
    public static void main(String[] args) throws IOException
    {
        int[] nXv = fillingFlasks.readSizeFlasks(args);
        int n = nXv[0];
        int v = nXv[1];

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        List<List<String>> tubes = new ArrayList<>(); //колбы

        fillingFlasks.fillingFlasks(br, n, v, tubes);

        tubes.forEach(System.out::println);

        Solver solver = new Solver(tubes);
        List<Move> solution = solver.solve();

        if (solution == null) {
            System.out.println("Нет решений");
        } else {
            System.out.println("Найдено решение в " + solution.size() + " ходов(а):");
            for (Move m : solution) {
                System.out.println(m);
            }
        }
    }
}

