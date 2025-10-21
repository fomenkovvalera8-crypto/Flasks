package org.example;
import java.io.*;
import java.util.*;


public class Main {
    /**
     * Метод проверки входных аргументов - N и V
     * @param args Входной массив строк
     * @return Размерность матрицы NxV в виде int[], где int[0]=N, int[1]=V
     */
    public static int[] readSizeFlasks(String[] args){
        if (args.length != 2)
        {
            System.out.println("При запуске программы должны быть два параметра - N и V");
            System.exit(1);
        }
        try
        {
            int n = Integer.parseInt(args[0]);
            int v = Integer.parseInt(args[1]);

            if ((v > 2) && (n < 3))
            {
                System.out.println("Если ячеек в колбе больше двух, для решении задачи надо минимум 3 колбы");
                System.exit(1);
            }
            if ((v > 1) && (n < 2))
            {
                System.out.println("Если ячеек в колбе больше одной, для решении задачи надо минимум 2 колбы");
                System.exit(1);
            }
            if ((v < 1) || (n < 1))
            {
                System.out.println("Для запуска программы нужно ввести хотя бы 1 колбу и одну ячейку");
                System.exit(1);
            }
            return new int[]{n, v};
        }
        catch (NumberFormatException e)
        {
            System.out.println("Параметры N и V должны быть целыми числами");
            System.exit(1);
            return null;
        }
    }

    /**
     * Метод для выбора режима игры: 1 - заполнение колб вручную, 2 - случайное заполнение
     * @param br Буфер для считывания
     * @return Число, означающее выбор режима
     * @throws IOException Ошибка ввода/вывода
     */
    public static int modeSelection(BufferedReader br) throws IOException {
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
     * Метод для ручного заполнения колб
     * @param br Буфер для считывания
     * @param n Количество колб
     * @param v Количество ячеек в колбе
     * @param tubes Лист с колбами
     * @throws IOException Ошибка ввода/вывода
     */
    public static void manualFillingFlasks(BufferedReader br, int n, int v, List<List<String>> tubes) throws IOException {
        System.out.printf("""
                Введите содержимое каждой колбы через пробел, начиная с верхнего уровня.
                Используйте точки (.) для пустых ячеек.
                Пример строки: A B B A  или  . . A B - это заполнение одной колбы.
                При вводе важно соблюдать шесть условий:
                1. Пустые ячейки (точки) не могут стоять между заполненными слоями
                2. Пустые ячейки (точки) могут стоять только, если выше них тоже пустые ячейки
                3. Количество ячеек в колбе не может быть больше v. Если количество меньше - то колба автоматически заполнится пустотой сверху
                4. Для решения обязательно должны быть две пустые колбы.
                5. Общее количество ячеек заполненных одинаковой жидкостью должны быть кратны v
                6. Количество пустых ячеек должно быть 2*v
                (Введите %d строк, максимум %d элементов в каждой строке):
                %n""", n, v);
        for (int i = 0; i < n; i++) {
            while (true) // повторяем, пока пользователь не введёт содержимое колбы корректно
            {
                System.out.printf("Колба %d: ", i + 1);
                String line = br.readLine();
                if (line == null)
                {
                    line = "";
                }
                String[] tube = line.trim().isEmpty() ? new String[0] : line.trim().split("\\s+");

                boolean foundLiquidBelowEmpty = false;
                boolean seenLiquid = false;
                for (int j = tube.length - 1; j >= 0; j--) {
                    if (tube[j].equals(".")) {
                        if (seenLiquid) {
                            foundLiquidBelowEmpty = true;
                            break;
                        }
                    }
                    else
                    {
                        seenLiquid = true;
                    }
                }

                if (foundLiquidBelowEmpty) {
                    System.out.println("Ошибка: точка (.) не может находиться между жидкостями. Попробуйте снова.");
                    continue;
                }

                if (tube.length > v) {
                    System.out.println("Ошибка: слишком много элементов. Максимум " + v + ". Попробуйте снова.");
                    continue;
                }

                if (tube.length < v)
                {
                    String[] tmp = new String[v];
                    int empty = v - tube.length;
                    for (int j = 0; j < empty; j++)
                    {
                        tmp[j] = ".";
                    }
                    for (int j = 0; j < tube.length; j++)
                    {
                        tmp[empty + j] = tube[j];
                    }
                    tube = tmp;
                }

                List<String> tubeList = new ArrayList<>();
                for (int j = 0; j < v; j++) tubeList.add(tube[j].equals(".") ? null : tube[j]);
                tubes.add(tubeList);
                break;
            }
        }
        long emptyCount = tubes.stream()
                .filter(tube -> tube.stream().allMatch(Objects::isNull))
                .count();

        if ((emptyCount != 2) && (n > 2)) {
            System.out.println("Ошибка: должно быть две полностью пустые колбы. Повторите ввод заново.");
            tubes.clear();
            manualFillingFlasks(br, n, v, tubes);
        }
        Map<String, Integer> colorCounts = new HashMap<>();
        for (List<String> tube : tubes) {
            for (String cell : tube) {
                if (cell != null) {
                    colorCounts.put(cell, colorCounts.getOrDefault(cell, 0) + 1);
                }
            }
        }

        boolean divisibleByV = true;
        for (Map.Entry<String, Integer> entry : colorCounts.entrySet()) {
            if (entry.getValue() % v != 0) {
                System.out.println("Ошибка: общее количество капель цвета '" + entry.getKey() + "' должно быть кратно " + v + ". Повторите ввод заново.");
                divisibleByV = false;
                break;
            }
        }

        if (!divisibleByV) {
            tubes.clear();
            manualFillingFlasks(br, n, v, tubes);
        }
        int emptyCells = 0;
        for (List<String> tube : tubes) {
            for (String cell : tube) {
                if (cell == null) emptyCells++;
            }
        }

        if (emptyCells != 2*v) {
            System.out.println("Ошибка: общее количество пустых ячеек должно быть 2*" + v + ". Повторите ввод заново.");
            tubes.clear();
            manualFillingFlasks(br, n, v, tubes);
        }
    }

    /**
     * Метод для автоматического заполнения колб
     * @param n Количество колб
     * @param v Количество ячеек в колбе
     * @param tubes Лист с колбами
     */
    public static void autoFillingFlasks(int n, int v, List<List<String>> tubes) {
        Random random = new Random();

        int numEmptyTubes = 2; // условие 4
        int numFilledTubes = n - numEmptyTubes;

        // --- Выбираем количество разных жидкостей ---
        int maxColors = Math.min(3, v); // 1..3 цвета на уровне
        int numColors = 1 + random.nextInt(maxColors); // 1..maxColors

        List<String> colors = new ArrayList<>();
        for (int i = 0; i < numColors; i++) {
            colors.add(Character.toString((char) ('A' + i)));
        }

        // --- Определяем общее количество капель каждой жидкости ---
        Map<String, Integer> colorCounts = new HashMap<>();
        for (String color : colors) {
            // кратность v
            int multiples = 1 + random.nextInt(3); // количество полных колб с этим цветом
            colorCounts.put(color, multiples * v);
        }

        // --- Общее количество пустых ячеек = 2*v ---
        int emptyCells = 2 * v;

        // --- Формируем плоский список всех капель ---
        List<String> pool = new ArrayList<>();
        for (Map.Entry<String, Integer> entry : colorCounts.entrySet()) {
            for (int i = 0; i < entry.getValue(); i++) {
                pool.add(entry.getKey());
            }
        }
        for (int i = 0; i < emptyCells; i++) pool.add(null);

        // --- Перемешиваем список ---
        Collections.shuffle(pool, random);

        // --- Заполняем колбы ---
        for (int i = 0; i < n; i++) {
            List<String> tube = new ArrayList<>(Collections.nCopies(v, null));

            // пустые колбы
            if (i >= numFilledTubes) {
                tubes.add(tube);
                continue;
            }

            // берем случайные v элементов из pool
            int filled = 0;
            for (int j = 0; j < pool.size() && filled < v; j++) {
                String val = pool.get(j);
                if (val != null) {
                    tube.set(v - 1 - filled, val); // заполняем снизу вверх
                    pool.set(j, null); // убираем из пула
                    filled++;
                }
            }

            tubes.add(tube);
        }

        // --- Проверка на пустые ячейки между жидкостями ---
        for (List<String> tube : tubes) {
            boolean seenLiquid = false;
            for (int j = 0; j < v; j++) {
                if (tube.get(j) != null) {
                    seenLiquid = true;
                } else if (seenLiquid) {
                    // пустая ячейка после жидкости, сдвигаем вниз все жидкости
                    int k = j;
                    while (k < v && tube.get(k) == null) k++;
                    if (k < v) {
                        // переносим жидкость вниз
                        tube.set(j, tube.get(k));
                        tube.set(k, null);
                    }
                }
            }
        }
    }
    /**
     * Метод для заполнения колб
     * @param mode Выбор режима игры
     * @param br Буфер для считывания
     * @param n Количество колб
     * @param v Количество единиц жидкости в колбе
     * @param tubes Колбы, которые тут заполнятся
     * @throws IOException Ошибка ввода/вывода
     */
    public static void fillingFlasks(int mode, BufferedReader br, int n, int v, List<List<String>> tubes) throws IOException {
        if (mode == 1)
        {
            manualFillingFlasks(br, n, v, tubes);
        }
        else
        {
            autoFillingFlasks(n, v, tubes);
        }
    }
    /**
     * Метод - стартовая точка программы
     * @param args - аргументы командной строки
     * @throws IOException - ошибка ввода/вывода
     */
    public static void main(String[] args) throws IOException
    {
        int[] nXv = readSizeFlasks(args);
        int n = nXv[0];
        int v = nXv[1];

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        int mode = modeSelection(br);

        List<List<String>> tubes = new ArrayList<>(); //колбы

        fillingFlasks(mode, br, n, v, tubes);

        //@TODO:решение головоломки
    }
}