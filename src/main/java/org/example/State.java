package org.example;

import java.util.*;

public class State {
    private final List<List<String>> tubes;  // текущее состояние пробирок
    private final List<Move> moves;          // последовательность ходов, приведших к этому состоянию
    private final int depth;                 // глубина (кол-во ходов от начала)

    public State(List<List<String>> tubes) {
        this.tubes = deepCopy(tubes);
        this.moves = new ArrayList<>();
        this.depth = 0;
    }

    public State(List<List<String>> tubes, List<Move> moves, int depth) {
        this.tubes = deepCopy(tubes);
        this.moves = new ArrayList<>(moves);
        this.depth = depth;
    }

    public List<List<String>> getTubes() {
        return tubes;
    }

    public List<Move> getMoves() {
        return moves;
    }

    public int getDepth() {
        return depth;
    }

    // --- Проверка, решено ли состояние ---
    public boolean isSolved() {
        for (List<String> tube : tubes) {
            String color = null;
            for (String cell : tube) {
                if (cell == null || cell.equals(".")) continue;
                if (color == null) color = cell;
                else if (!color.equals(cell)) return false; // разный цвет в одной колбе
            }
        }
        return true;
    }

    // --- Создаёт новое состояние после совершения хода ---
    public State makeMove(int from, int to) {
        List<List<String>> newTubes = deepCopy(tubes);
        String colorToPour = null;

        // находим верхний цвет из колбы "from"
        for (int i = 0; i < newTubes.get(from).size(); i++) {
            if (newTubes.get(from).get(i) != null && !newTubes.get(from).get(i).equals(".")) {
                colorToPour = newTubes.get(from).get(i);
                newTubes.get(from).set(i, "."); // удаляем из источника
                break;
            }
        }

        if (colorToPour == null) return null; // нечего переливать

        // наливаем в первую пустую ячейку сверху в колбе "to"
        for (int i = newTubes.get(to).size() - 1; i >= 0; i--) {
            if (newTubes.get(to).get(i) == null || newTubes.get(to).get(i).equals(".")) {
                newTubes.get(to).set(i, colorToPour);
                break;
            }
        }

        List<Move> newMoves = new ArrayList<>(moves);
        newMoves.add(new Move(from, to));

        return new State(newTubes, newMoves, depth + 1);
    }

    // --- Глубокое копирование состояния ---
    private static List<List<String>> deepCopy(List<List<String>> src) {
        List<List<String>> copy = new ArrayList<>();
        for (List<String> tube : src) {
            copy.add(new ArrayList<>(tube));
        }
        return copy;
    }

    // --- Хэш и equals для хранения в HashSet (по состоянию пробирок) ---
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof State)) return false;
        State state = (State) o;
        return Objects.equals(tubes, state.tubes);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tubes);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Depth: " + depth + "\n");
        for (List<String> tube : tubes) {
            sb.append(tube).append("\n");
        }
        return sb.toString();
    }
}

