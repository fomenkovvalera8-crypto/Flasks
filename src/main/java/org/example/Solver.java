package org.example;

import java.util.*;

public class Solver {
    private final List<List<String>> tubes;

    public List<List<String>> getTubes() {
        return tubes;
    }

    public Solver(List<List<String>> tubes) {
        this.tubes = tubes;
    }

    public List<Move> solve() {
        List<Move> moves = new ArrayList<>();
        int step = 0;

        while (true) {
            // если все колбы однородные или пустые — задача решена
            if (allTubesUniformOrEmpty()) break;

            boolean movedInThisIteration = false;

            for (int i = 0; i < tubes.size(); i++) {
                List<String> from = tubes.get(i);
                if (isUniformOrEmpty(from)) continue;

                boolean movedFromThisTube;
                do {
                    movedFromThisTube = false;
                    String top = topColor(from);
                    if (top == null) break;

                    for (int j = 0; j < tubes.size(); j++) {
                        if (i == j) continue;
                        List<String> to = tubes.get(j);

                        if (canPour(from, to)) {
                            int moved = pour(from, to);
                            if (moved > 0) {
                                step++;
                                moves.add(new Move(i + 1, j + 1));
                                movedFromThisTube = true;
                                movedInThisIteration = true;

                                System.out.println("Ход " + step + ": " + new Move(i + 1, j + 1));
                                printTubes();

                                // после переливания проверяем с этой же колбы дальше
                                break;
                            }
                        }
                    }
                } while (movedFromThisTube);
            }

            if (!movedInThisIteration) break; // если с ни одной колбы больше перелить нельзя
        }

        return moves;
    }

    private boolean allTubesUniformOrEmpty() {
        for (List<String> tube : tubes) {
            if (!isUniformOrEmpty(tube)) return false;
        }
        return true;
    }

    private boolean isUniformOrEmpty(List<String> tube) {
        String color = null;
        for (String cell : tube) {
            if (cell != null && !cell.equals(".")) {
                if (color == null) color = cell;
                else if (!color.equals(cell)) return false;
            }
        }
        return true;
    }

    private String topColor(List<String> tube) {
        for (int i = tube.size() - 1; i >= 0; i--) {
            String cell = tube.get(i);
            if (cell != null && !cell.equals(".")) return cell;
        }
        return null;
    }

    private boolean canPour(List<String> from, List<String> to) {
        String topFrom = topColor(from);
        if (topFrom == null) return false;

        String topTo = topColor(to);
        boolean hasSpace = to.stream().anyMatch(s -> s == null || s.equals("."));
        return hasSpace && (topTo == null || topTo.equals(topFrom));
    }

    private int pour(List<String> from, List<String> to) {
        String topFrom = topColor(from);
        if (topFrom == null) return 0;

        // Считаем, сколько элементов сверху одинакового цвета
        int countToMove = 0;
        for (int i = from.size() - 1; i >= 0; i--) {
            String cell = from.get(i);
            if (cell != null && cell.equals(topFrom)) countToMove++;
            else if (cell != null) break;
        }

        if (countToMove == 0) return 0;

        // Переливаем элементы в первую пустую позицию слева (индексы 0..v-1)
        int moved = 0;
        for (int i = from.size() - 1; i >= 0 && moved < countToMove; i--) {
            String cell = from.get(i);
            if (cell != null && cell.equals(topFrom)) {
                for (int j = 0; j < to.size(); j++) {
                    if (to.get(j) == null || to.get(j).equals(".")) {
                        to.set(j, topFrom);
                        from.set(i, null);
                        moved++;
                        break; // нашли пустое место для этой капли
                    }
                }
            } else if (cell != null) {
                break; // встретили другой цвет
            }
        }

        return moved;
    }


    private void printTubes() {
        for (int i = 0; i < tubes.size(); i++) {
            List<String> tube = tubes.get(i);
            System.out.print("Колба " + (i + 1) + ": ");
            for (String cell : tube) {
                System.out.print((cell == null ? "." : cell) + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
}
