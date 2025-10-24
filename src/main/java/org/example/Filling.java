package org.example;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.*;

public class Filling {
    private final List<List<String>> tubes = new ArrayList<>();
    private int n;
    private int v;
    public List<List<String>> getTubes() {
        return tubes;
    }

    /**
     * Метод проверки входных аргументов - N и V
     * @param args Входной массив строк
     */
    public void readSizeFlasks(String[] args) {
        if (args.length != 2) {
            System.out.println("При запуске программы должны быть два параметра - N и V");
            System.exit(1);
        }
        try {
            n = Integer.parseInt(args[0]);
            v = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            System.out.println("Параметры N и V должны быть целыми числами");
            System.exit(1);
        }
        if ((v > 2) && (n < 3)) {
            System.out.println("Если ячеек в колбе больше двух, для решении задачи надо минимум 3 колбы");
            System.exit(1);
        }
        if ((v > 1) && (n < 2)) {
            System.out.println("Если ячеек в колбе больше одной, для решении задачи надо минимум 2 колбы");
            System.exit(1);
        }
        if (v < 2) {
            System.out.println("Для запуска программы нужно ввести хотя бы две ячейки");
            System.exit(1);
        }
        if (n < v) {
            System.out.println("Количество колб не может быть меньше количества ячеек в колбе");
            System.exit(1);
        }
    }

    /**
     * Метод для выбора режима игры: 1 - заполнение колб вручную, 2 - случайное заполнение
     * @param br Буфер для считывания
     * @return Число, означающее выбор режима
     * @throws IOException Ошибка ввода/вывода
     */
    private int modeSelection(BufferedReader br) throws IOException {
        System.out.println("Выберите режим заполнения колб:");
        System.out.println("1 - Ввести вручную");
        System.out.println("2 - Случайное заполнение");
        System.out.print("Ваш выбор: ");
        try
        {
            int mode = Integer.parseInt(br.readLine());

            if (mode == 1 || mode == 2)
            {
                return mode;
            }
            else
            {
                System.out.println("Введите целое число: 1 или 2");
                System.exit(1);
                return 0;
            }
        }
        catch (NumberFormatException e)
        {
            System.out.println("Для выбора режима введите целое число");
            System.exit(1);
            return 0;
        }
    }

    /**
     * Метод проверки заполнения колб, чтобы 2 колбы были пустые
     * @return Флаг соответствия
     */
    private boolean checkTwoEmptyFlasks(){
        long emptyCount = tubes.stream()
                .filter(tube -> tube.stream().allMatch(Objects::isNull))
                .count();
        return ((emptyCount < 2) && (n > 2));
    }

    /**
     * Метод проверки заполненных колб на то, что каждый цвет суммарно заполняет одну колбу
     * @return Флаг соответствия
     */
    private boolean divisibleByV(){
        Map<String, Integer> colorCounts = new HashMap<>();
        for (List<String> tube : tubes)
        {
            for (String cell : tube)
            {
                if (cell != null)
                {
                    colorCounts.put(cell, colorCounts.getOrDefault(cell, 0) + 1);
                }
            }
        }
        long filledTubes = tubes.stream().filter(t -> t.stream().anyMatch(Objects::nonNull)).count();
        if (filledTubes <= 1)
        {
            return true;
        }
        boolean divisibleByV = true;
        for (Map.Entry<String, Integer> entry : colorCounts.entrySet()) {
            if (entry.getValue() % v != 0) {
                divisibleByV = false;
                break;
            }
        }
        return divisibleByV;
    }

    /**
     * Метод проверки пустых ячеек между слоями
     * @param tube Одна колба
     * @return Флаг проверки
     */
    private boolean checkEmptyBetweenLayers(String[] tube) {
        boolean foundLiquidBelowEmpty = false;
        boolean seenLiquid = false;
        for (int j = tube.length - 1; j >= 0; j--) {
            if (tube[j] == null || tube[j].equals(".")) {
                if (seenLiquid) {
                    foundLiquidBelowEmpty = true;
                    break;
                }
            } else {
                seenLiquid = true;
            }
        }
        return foundLiquidBelowEmpty;
    }

    /**
     * Метод для проверки решённых колб
     * @param tube Колба
     * @return Содержит ли колба один цвет?
     */
    private boolean isSolvedTube(List<String> tube)
    {
        String color = null;
        for (String cell : tube) {
            if (cell == null)
            {
                continue;
            }
            if (color == null)
            {
                color = cell;
            }
            else if (!cell.equals(color))
            {
                return false;
            }
        }
        return color != null;
    }
    /**
     * Метод для ручного заполнения колб
     * @param br Буфер для считывания
     * @throws IOException Ошибка ввода/вывода
     */
    private void manualFillingFlasks(BufferedReader br) throws IOException {
        int tubesToInput = Math.max(1, n - 2);
        Map<String, Integer> colorCounts = new HashMap<>();
        System.out.printf("""
                Введите содержимое каждой колбы через пробел, начиная с верхнего уровня.
                Пример строки: A B B A - это заполнение одной колбы.
                При вводе важно соблюдать два условия:
                1. Количество ячеек в колбе не может быть больше v.
                2. Общее количество ячеек заполненных одинаковой жидкостью должны быть кратны v
                (Введите %d строк, максимум %d элементов в каждой строке):
                %n""", tubesToInput, v);
        for (int i = 0; i < tubesToInput; i++) {
            while (true)
            {
                System.out.printf("Колба %d: ", i + 1);
                String line = br.readLine();
                if (line == null)
                {
                    line = "";
                }
                String[] input = line.trim().isEmpty() ? new String[0] : line.trim().split("\\s+");

                if (checkEmptyBetweenLayers(input))
                {
                    System.out.println("Ошибка: точка (.) не может находиться между жидкостями. Попробуйте снова.");
                    continue;
                }

                if (input.length > v)
                {
                    System.out.println("Ошибка: слишком много элементов. Максимум " + v + ". Попробуйте снова.");
                    continue;
                }

                List<String> tube = new ArrayList<>();
                for (String s : input) {
                    if (!s.equals(".")) {
                        tube.add(s);
                        colorCounts.put(s, colorCounts.getOrDefault(s, 0) + 1);
                    }
                }
                while (tube.size() < v) {
                    boolean added = false;
                    for (Map.Entry<String, Integer> entry : colorCounts.entrySet()) {
                        if (entry.getValue() % v != 0) {
                            tube.add(entry.getKey());
                            colorCounts.put(entry.getKey(), entry.getValue() + 1);
                            added = true;
                            break;
                        }
                    }
                    if (!added) {
                        if (!tube.isEmpty()) {
                            String any = tube.getFirst();
                            tube.add(any);
                            colorCounts.put(any, colorCounts.getOrDefault(any, 0) + 1);
                        } else {
                            tube.add("A");
                            colorCounts.put("A", colorCounts.getOrDefault("A", 0) + 1);
                        }
                    }
                }

                if (isSolvedTube(tube))
                {
                    System.out.println("Ошибка: колба не должна быть полностью одного цвета. Попробуйте снова.");
                    continue;
                }
                tubes.add(tube);
                break;
            }
        }
        for (int i = 0; i < n - tubesToInput; i++)
        {
            List<String> emptyTube = new ArrayList<>(Collections.nCopies(v, null));
            tubes.add(emptyTube);
        }
        if (n > 2 && checkTwoEmptyFlasks())
        {
            System.out.println("Ошибка: должно быть две полностью пустые колбы. Повторите ввод заново.");
            tubes.clear();
            manualFillingFlasks(br);
        }
        if (!divisibleByV())
        {
            System.out.println("Ошибка: общее количество капель каждого цвета должно быть кратно " + v + ". Повторите ввод заново.");
            tubes.clear();
            manualFillingFlasks(br);
        }
    }

    /**
     * Метод устранения пустых ячеек между слоями
     * @param tube Колба
     */
    private void compactTube(List<String> tube) {
        List<String> nonEmpty = tube.stream()
                .filter(s -> s != null && !s.equals("."))
                .toList();

        int v = tube.size();
        int emptyCount = v - nonEmpty.size();

        for (int j = 0; j < v; j++) {
            if (j < emptyCount)
            {
                tube.set(j, ".");
            }
            else
            {
                tube.set(j, nonEmpty.get(j - emptyCount));
            }
        }
    }
    /**
     * Метод для автоматического заполнения колб
     */
    private void autoFillingFlasks() {
        if (n < 4) {
            List<String> tube = new ArrayList<>();
            if (v == 2) {
                tube.add("A");
                tube.add("B");
            }
            if (v == 3) {
                tube.add("A");
                tube.add("B");
                tube.add("C");
            }
            tubes.add(tube);
            for(int i = 0; i < n - 1; i++){
                tubes.add(new ArrayList<>(Collections.nCopies(v, null)));
            }
            return;
        }
        Random random = new Random();

        int numEmptyTubes = 2;
        int numFilledTubes = n - numEmptyTubes;

        int numColors = Math.min(v, numFilledTubes);
        int totalCells = numFilledTubes * v;

        List<String> colors = new ArrayList<>();
        for (int i = 0; i < numColors; i++) {
            colors.add(Character.toString((char) ('A' + i)));
        }

        List<String> pool = new ArrayList<>();
        int colorBatch = numColors * v;

        while (pool.size() + colorBatch <= totalCells) {
            for (String color : colors) {
                for (int j = 0; j < v; j++) {
                    pool.add(color);
                }
            }
        }

        int remaining = totalCells - pool.size();
        if (remaining > 0) {
            List<String> extraColors = new ArrayList<>(colors);
            Collections.shuffle(extraColors, random);
            for (String color : extraColors) {
                if (remaining <= 0) break;
                for (int j = 0; j < v && remaining > 0; j++) {
                    pool.add(color);
                    remaining--;
                }
            }
        }

        Collections.shuffle(pool, random);

        for (int i = 0; i < numFilledTubes; i++) {
            List<String> tube = new ArrayList<>();
            for (int j = 0; j < v; j++) {
                tube.add(pool.removeFirst());
            }

            if (isSolvedTube(tube)) {
                for (int j = 0; j < v; j++) {
                    for (String color : colors) {
                        if (!color.equals(tube.get(j))) {
                            tube.set(j, color);
                            break;
                        }
                    }
                    if (!isSolvedTube(tube)) break;
                }
            }

            Collections.shuffle(tube, random);
            tubes.add(tube);
        }
        for (int i = 0; i < numEmptyTubes; i++) {
            tubes.add(new ArrayList<>(Collections.nCopies(v, null)));
        }
        for (List<String> tubeList : tubes) {
            String[] tubeArray = tubeList.toArray(new String[0]);

            if (checkEmptyBetweenLayers(tubeArray)) {
                compactTube(tubeList);
            }
        }
    }
    /**
     * Метод для заполнения колб
     * @param br Буфер для считывания
     * @throws IOException Ошибка ввода/вывода
     */
    public void fillingFlasks(BufferedReader br) throws IOException {
        int mode = modeSelection(br);
        if (mode == 1)
        {
            manualFillingFlasks(br);
        }
        else
        {
            autoFillingFlasks();
        }
    }
}