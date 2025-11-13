package org.example.utils;

import java.util.*;

public class GraphUtils {

    public static List<Set<String>> getConnectedComponents(Map<String, Set<String>> adjacencyMap) {
        Set<String> visited = new HashSet<>();
        List<Set<String>> comps = new ArrayList<>();

        for(String node : adjacencyMap.keySet()) {
            if(!visited.contains(node)) {
                Set<String> comp = new HashSet<>();
                Queue<String> queue = new LinkedList<>();
                queue.add(node);
                visited.add(node);

                while(!queue.isEmpty()) {
                    String cur = queue.poll();
                    comp.add(cur);
                    for (String neighbour : adjacencyMap.getOrDefault(cur, Collections.emptySet())) {
                        if(!visited.contains(neighbour)) {
                            visited.add(neighbour);
                            queue.add(neighbour);
                        }
                    }
                }
                comps.add(comp);
            }
        }
        return comps;
    }
    private static int bfsMaxDistance(Map<String, Set<String>> adjacencyMap, String start, Set<String> component) {
        Queue<String> q = new LinkedList<>();
        Map<String, Integer> dist = new HashMap<>();
        q.add(start);
        dist.put(start, 0);
        int maxDist = 0;

        while (!q.isEmpty()) {
            String curr = q.poll();
            int dCurr = dist.get(curr);
            maxDist = Math.max(maxDist, dCurr);

            for (String neighbour : adjacencyMap.getOrDefault(curr, Collections.emptySet())) {
                if (!component.contains(neighbour)) continue;
                if (!dist.containsKey(neighbour)) {
                    dist.put(neighbour, dCurr + 1);
                    q.add(neighbour);
                }
            }
        }
        return maxDist;
    }

    public static int getComponentDiameter(Map<String, Set<String>> adjacencyMap, Set<String> component) {
        int diameter = 0;
        for (String node : component){
            int d = bfsMaxDistance(adjacencyMap, node, component);
            diameter = Math.max(diameter, d);
        }
        return diameter;
    }

    public static class Pair<A, B> {
        public final A first;
        public final B second;

        public Pair(A first, B second) {
            this.first = first;
            this.second = second;
        }
    }

    public static Pair<Set<String>, Integer> getMostSociableCommunity(Map<String, Set<String>> adjacencyMap) {
        List<Set<String>> comps = getConnectedComponents(adjacencyMap);
        Set<String> bestComp = Collections.emptySet();
        int bestDiameter = -1;
        for(Set<String> comp : comps) {
            int d = getComponentDiameter(adjacencyMap, comp);
            if (d > bestDiameter) {
                bestDiameter = d;
                bestComp = comp;
            }
        }
        if (bestDiameter == -1)
            return null;
        return new Pair<>(bestComp, bestDiameter);
    }
}
