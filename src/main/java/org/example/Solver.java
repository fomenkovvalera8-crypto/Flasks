package org.example;

import java.util.*;

public class Solver {
    private final State initial;

    public Solver(List<List<String>> tubes) {
        this.initial = new State(tubes);
    }

    public List<Move> solve() {
        if (initial.isSolved()) {
            return Collections.emptyList();
        }

        long startTime = System.currentTimeMillis();
        final long TIME_LIMIT_MS = 10000; // максимум 5 секунд на поиск

        Queue<State> queue = new ArrayDeque<>();
        Map<State, Parent> parent = new HashMap<>();
        queue.add(initial);
        parent.put(initial, new Parent(null, null));

        int nodes = 0;

        while (!queue.isEmpty()) {
            // проверка на превышение лимита времени
            if (System.currentTimeMillis() - startTime > TIME_LIMIT_MS) {
                System.out.println("Время решения истекло (" + TIME_LIMIT_MS + " мс)");
                return null;
            }

            State current = queue.poll();
            nodes++;
            int maxNodes = 5_000_000;
            if (nodes > maxNodes) {
                System.out.println("Превышен лимит узлов (" + maxNodes + ")");
                return null;
            }

            if (current.isSolved()) {
                return reconstruct(parent, current);
            }

            for (Move move : getPossibleMoves(current)) {
                State next = current.makeMove(move.getFrom(), move.getTo());
                if (next == null) continue;

                if (!parent.containsKey(next)) {
                    parent.put(next, new Parent(current, move));
                    queue.add(next);
                }
            }
        }

        return null;
    }


    private List<Move> reconstruct(Map<State, Parent> parent, State goal) {
        LinkedList<Move> path = new LinkedList<>();
        State current = goal;

        while (true)
        {
            Parent p = parent.get(current);
            if (p == null || p.prev == null)
            {
                break;
            }
            path.addFirst(p.move);
            current = p.prev;
        }

        return path;
    }

    private record Parent(State prev, Move move) {
    }

    /**
     * Находит все возможные переливания (from → to),
     * где есть хотя бы один цвет в "from" и свободное место в "to"
     */
    private List<Move> getPossibleMoves(State state) {
        List<Move> moves = new ArrayList<>();
        List<List<String>> tubes = state.getTubes();

        for (int i = 0; i < tubes.size(); i++)
        {
            List<String> from = tubes.get(i);
            String colorToPour = topColor(from);
            if (colorToPour == null)
            {
                continue;
            }

            for (int j = 0; j < tubes.size(); j++) {
                if (i == j)
                {
                    continue;
                }
                List<String> to = tubes.get(j);

                if (canPour(from, to))
                {
                    moves.add(new Move(i, j));
                }
            }
        }
        return moves;
    }

    private String topColor(List<String> tube)
    {
        for (String cell : tube)
        {
            if (cell != null && !cell.equals(".")) return cell;
        }
        return null;
    }

    private boolean canPour(List<String> from, List<String> to)
    {
        String topFrom = topColor(from);
        if (topFrom == null)
        {
            return false;
        }

        boolean hasEmpty = to.stream().anyMatch(s -> s == null || s.equals("."));
        if (!hasEmpty)
        {
            return false;
        }

        String topTo = topColor(to);
        return topTo == null || topTo.equals(topFrom);
    }
}
