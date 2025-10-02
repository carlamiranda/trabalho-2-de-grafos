import java.util.Stack;

public class EulerianCycleFleury {
    private Stack<Integer> cycle = new Stack<>();  // Eulerian cycle; null if no cycle

    private static class Edge {
        private final int v, w;
        private boolean isUsed;

        public Edge(int v, int w) { this.v = v; this.w = w; isUsed = false; }
        public int other(int vertex) {
            if (vertex == v) return w;
            if (vertex == w) return v;
            throw new IllegalArgumentException("Illegal endpoint");
        }
    }

    public EulerianCycleFleury(Graph G) {
        if (G.E() == 0) return;

        for (int v = 0; v < G.V(); v++)
            if (G.degree(v) % 2 != 0) return;

        // copiar listas de adjacência
        @SuppressWarnings("unchecked")
        Queue<Edge>[] adj = (Queue<Edge>[]) new Queue[G.V()];
        for (int v = 0; v < G.V(); v++) adj[v] = new Queue<>();

        for (int v = 0; v < G.V(); v++) {
            int selfLoops = 0;
            for (int w : G.adj(v)) {
                if (v == w) {
                    if (selfLoops % 2 == 0) {
                        Edge e = new Edge(v, w);
                        adj[v].enqueue(e);
                        adj[w].enqueue(e);
                    }
                    selfLoops++;
                } else if (v < w) {
                    Edge e = new Edge(v, w);
                    adj[v].enqueue(e);
                    adj[w].enqueue(e);
                }
            }
        }

        int s = nonIsolatedVertex(G);
        cycle = new Stack<>();
        fleury(s, adj);

        if (cycle.size() != G.E() + 1) cycle = null;
    }

    private void fleury(int v, Queue<Edge>[] adj) {
        for (Edge e : adj[v]) {
            if (!e.isUsed) {
                e.isUsed = true;
                fleury(e.other(v), adj);
            }
        }
        cycle.push(v);
    }

    public Iterable<Integer> cycle() { return cycle; }
    public boolean hasEulerianCycle() { return cycle != null; }

    private static int nonIsolatedVertex(Graph G) {
        for (int v = 0; v < G.V(); v++) if (G.degree(v) > 0) return v;
        return -1;
    }
}
