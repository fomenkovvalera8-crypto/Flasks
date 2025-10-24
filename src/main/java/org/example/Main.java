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
        fillingFlasks.readSizeFlasks(args);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        fillingFlasks.fillingFlasks(br);

        fillingFlasks.getTubes().forEach(System.out::println);

        Solver solver = new Solver(fillingFlasks.getTubes());
        List<Move> solution = solver.solve();

        if (solution == null) {
            System.out.println("Нет решений");
        } else {
            System.out.println("Найдено решение в " + solution.size() + " ходов(а):");
            solver.getTubes().forEach(System.out::println);
        }
    }
}

