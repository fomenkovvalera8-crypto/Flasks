package org.example;

import java.util.*;

public class Solver {
    private final List<List<String>> tubes;
    private boolean allowOneMismatch = true;
    private boolean allowPartialPour = true;

    public List<List<String>> getTubes() {
        return tubes;
    }

    public Solver(List<List<String>> tubes) {
        this.tubes = tubes;
    }

    public List<Move> solve() {
        List<Move> moves = new ArrayList<>();
        Map<String, Set<String>> triedMoves = new HashMap<>();
        int step = 0;

        while (true) {
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

                    // собираем все подходящие колбы
                    List<Integer> candidateIndices = new ArrayList<>();
                    for (int j = 0; j < tubes.size(); j++) {
                        if (i == j) continue;
                        if (canPour(from, tubes.get(j))) candidateIndices.add(j);
                    }

                    // сортируем кандидатов по качеству совпадения
                    candidateIndices.sort((a, b) -> {
                        List<String> ta = tubes.get(a), tb = tubes.get(b);
                        String toA = topColor(ta), toB = topColor(tb);
                        String topFrom = topColor(from);
                        int scoreA = (toA == null || toA.equals(topFrom)) ? 0 : 2;
                        int scoreB = (toB == null || toB.equals(topFrom)) ? 0 : 2;
                        return Integer.compare(scoreA, scoreB);
                    });

                    // ключ текущего состояния
                    String stateKey = getStateKey();
                    Set<String> tried = triedMoves.computeIfAbsent(stateKey, k -> new HashSet<>());

                    // пробуем кандидатов по приоритету
                    for (int j : candidateIndices) {
                        String moveKey = (i + 1) + "->" + (j + 1);
                        if (tried.contains(moveKey)) continue;
                        tried.add(moveKey);

                        List<String> to = tubes.get(j);
                        int moved = pour(from, to);
                        if (moved > 0) {
                            step++;
                            moves.add(new Move(i + 1, j + 1));
                            movedFromThisTube = true;
                            movedInThisIteration = true;

                            System.out.println("Ход " + step + ": " + new Move(i + 1, j + 1));
                            printTubes();
                            break;
                        }
                    }

                } while (movedFromThisTube);
            }

            if (!movedInThisIteration) {
                if (!allowOneMismatch) {
                    allowOneMismatch = true;
                } else if (!allowPartialPour) {
                    allowPartialPour = true;
                } else {
                    break; // больше нет ходов
                }
            }
        }

        // --- Финальный этап: аккуратное сгруппирование остатков ---
        boolean moved;
        do {
            moved = false;
            for (int i = 0; i < tubes.size(); i++) {
                List<String> from = tubes.get(i);
                if (isEmpty(from)) continue;
                String topFrom = topColor(from);

                for (int j = 0; j < tubes.size(); j++) {
                    if (i == j) continue;
                    List<String> to = tubes.get(j);
                    if (isFull(to)) continue;
                    String topTo = topColor(to);

                    // переливание только если to пустая или совпадает цвет сверху
                    if (topTo != null && !topTo.equals(topFrom)) continue;

                    // приоритет: переливаем только в колбу с большим или равным количеством цвета сверху
                    if (countTopColor(to, topFrom) < countTopColor(from, topFrom)) continue;

                    int poured = pour(from, to);
                    if (poured > 0) {
                        moved = true;
                        step++;
                        moves.add(new Move(i + 1, j + 1));
                    }
                }
            }
        } while (moved);

        return moves;
    }
    private int countTopColor(List<String> tube, String color) {
        int count = 0;
        for (int i = tube.size() - 1; i >= 0; i--) {
            String cell = tube.get(i);
            if (cell == null || cell.equals(".")) continue;
            if (cell.equals(color)) count++;
            else break;
        }
        return count;
    }
    private boolean isEmpty(List<String> tube) {
        for (String s : tube) if (s != null && !s.equals(".")) return false;
        return true;
    }

    private boolean isFull(List<String> tube) {
        for (String s : tube) if (s == null || s.equals(".")) return false;
        return true;
    }
    private String getStateKey() {
        StringBuilder sb = new StringBuilder();
        for (List<String> tube : tubes) {
            for (String cell : tube) {
                sb.append(cell == null ? "." : cell);
            }
            sb.append("|");
        }
        return sb.toString();
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
        int space = (int) to.stream().filter(s -> s == null || s.equals(".")).count();
        if (space == 0) return false;

        // Считаем, сколько одинаковых сверху в колбе from
        int countTopFrom = 0;
        for (int i = from.size() - 1; i >= 0; i--) {
            String cell = from.get(i);
            if (cell == null || cell.equals(".")) continue;
            if (cell.equals(topFrom)) countTopFrom++;
            else break;
        }
// --- Новое правило ---
        if (topTo == null) {
            // количество верхнего цвета в from
            int topCount = countTopColor(from, topFrom);
            // сколько всего элементов в from
            int totalNonEmpty = (int) from.stream().filter(s -> s != null && !s.equals(".")).count();

            // запретить переливание, если from полностью опустеет
            if (topCount == totalNonEmpty) return false;

            boolean hasSameTopElsewhere = false;
            for (List<String> tube : tubes) {
                if (tube == from || tube == to) continue;

                String t = topColor(tube);
                // колба имеет тот же верхний цвет, и в ней есть место для переливания
                if (t != null && t.equals(topFrom) && canPour(from, tube)) {
                    hasSameTopElsewhere = true;
                    break;
                }
            }

// если есть подходящая альтернатива — запрещаем переливать в пустую
            if (hasSameTopElsewhere) return false;

            // иначе разрешаем переливание в пустую колбу
            return true;
        } else if (!topTo.equals(topFrom)) {
            return false; // нельзя переливать в колбу с другим цветом сверху
        }
        // если помещается полностью — разрешаем
        if (countTopFrom <= space) return true;

// если не помещается полностью — разрешаем один неполный перелив
        return allowPartialPour;
    }


    private int pour(List<String> from, List<String> to) {
        String topFrom = topColor(from);
        if (topFrom == null) return 0;

        String topTo = topColor(to);
        boolean mismatch = (topTo != null && !topTo.equals(topFrom));

        int countToMove = 0;
        for (int i = from.size() - 1; i >= 0; i--) {
            String cell = from.get(i);
            if (cell != null && cell.equals(topFrom)) countToMove++;
            else if (cell != null) break;
        }

        if (countToMove == 0) return 0;

        int moved = 0;
        for (int i = from.size() - 1; i >= 0 && moved < countToMove; i--) {
            String cell = from.get(i);
            if (cell != null && cell.equals(topFrom)) {
                for (int j = 0; j < to.size(); j++) {
                    if (to.get(j) == null || to.get(j).equals(".")) {
                        to.set(j, topFrom);
                        from.set(i, null);
                        moved++;
                        break;
                    }
                }
            } else if (cell != null) break;
        }

        // Если перелили в "неподходящую" колбу — сбрасываем флаг
        if (mismatch) {
            allowOneMismatch = false;
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
