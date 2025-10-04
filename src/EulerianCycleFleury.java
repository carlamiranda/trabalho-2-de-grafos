public class EulerianCycleFleury {

    private Stack<Integer> cycle = new Stack<>();
    private final int totalEdges; 
    private Queue<Edge>[] adj;

    // Classe auxiliar para rastrear arestas não direcionadas e o uso
    private static class Edge {
        private final int v;
        private final int w;
        private boolean isUsed;

        public Edge(int v, int w) {
            this.v = v;
            this.w = w;
            isUsed = false;
        }

        public int other(int vertex) {
            if (vertex == v) return w;
            if (vertex == w) return v;
            throw new IllegalArgumentException("Illegal endpoint");
        }
    }

    public EulerianCycleFleury(Graph G) {
        this.totalEdges = G.E();
        if (totalEdges == 0) return;

        // Mantém a checagem defensiva de grau par (a Main faz a checagem principal)
        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) % 2 != 0) return;
        }

        // 1. Inicializa a lista de adjacência de arestas
        @SuppressWarnings("unchecked")
        Queue<Edge>[] adj = (Queue<Edge>[]) new Queue[G.V()];
        for (int v = 0; v < G.V(); v++) adj[v] = new Queue<>();

        for (int v = 0; v < G.V(); v++) {
            for (int w : G.adj(v)) {
                if (v < w) {
                    Edge e = new Edge(v, w);
                    adj[v].enqueue(e);
                    adj[w].enqueue(e);
                }
                else if (v == w) {
                    Edge e = new Edge(v, w);
                    adj[v].enqueue(e);
                    adj[w].enqueue(e);
                }
            }
        }
        this.adj = adj;

        int s = nonIsolatedVertex(G);
        if (s == -1) return;

        Stack<Integer> stack = new Stack<>();
        stack.push(s);

        // 2. Loop principal de Fleury
        while (!stack.isEmpty()) {
            int v = stack.peek();
            
            Edge nextEdge = null;
            int w = -1;

            // Encontra a próxima aresta (Regra de Fleury: Prioriza Não-Ponte)
            for (Edge e : adj[v]) {
                if (!e.isUsed) {
                    int u = e.other(v);

                    if (isBridge(v, u, e)) {
                        // É ponte: só a guarda se ainda não tivermos uma opção
                        if (nextEdge == null) {
                            nextEdge = e; 
                            w = u;
                        }
                    } else {
                        // NÃO é ponte: é a ESCOLHA preferida. Pega e sai.
                        nextEdge = e;
                        w = u;
                        break; 
                    }
                }
            }

            if (nextEdge != null) {
                nextEdge.isUsed = true;
                stack.push(w);
            } else {
                // Não há mais arestas. Adiciona ao ciclo e volta.
                cycle.push(stack.pop());
            }
        }

        // 3. Checagem final
        if (cycle.size() != totalEdges + 1) cycle = null;
    }

    // --- MÉTODOS AUXILIARES CORRIGIDOS PARA O FLEURY ---

    /**
     * Verifica se a aresta (v, w) é uma ponte no subgrafo de arestas não usadas.
     */
    private boolean isBridge(int v, int w, Edge edge) {
        
        // 1. Caso obrigatório: Se o grau restante for 1, deve ser usada.
        int remainingDegree = 0;
        for (Edge e : adj[v]) {
            if (!e.isUsed) remainingDegree++;
        }
        if (remainingDegree == 1) return false; 

        // 2. Testa a conectividade se houver alternativas (remainingDegree > 1).
        
        // A. Simula a remoção de (v, w)
        edge.isUsed = true;
        
        // B. Conta o número TOTAL de vértices ativos (com arestas não usadas)
        int totalActiveCount = countActiveVertices();

        // C. Conta quantos vértices ativos são alcançáveis a partir de 'w'
        // Passa o 'v' para ter um ponto de partida consistente.
        boolean[] visited = new boolean[adj.length];
        int reachableActiveCount = dfsCount(v, visited); 

        // D. Desfaz a simulação
        edge.isUsed = false;
        
        // E. Se a contagem alcançável for menor que a total, a aresta é uma ponte.
        return (reachableActiveCount < totalActiveCount);
    }

    /**
     * Conta quantos vértices no grafo ainda possuem pelo menos uma aresta não usada.
     */
    private int countActiveVertices() {
        int activeCount = 0;
        for (int v = 0; v < adj.length; v++) {
            for (Edge e : adj[v]) {
                if (!e.isUsed) {
                    activeCount++;
                    break; 
                }
            }
        }
        return activeCount;
    }

    /**
     * DFS que visita apenas arestas não usadas, contando apenas os vértices ATIVOS alcançados.
     */
    private int dfsCount(int v, boolean[] visited) {
        Stack<Integer> stack = new Stack<>();
        stack.push(v);
        int count = 0;
        
        while (!stack.isEmpty()) {
            int x = stack.pop();
            if (!visited[x]) {
                visited[x] = true;
                
                // CRUCIAL para o Fleury: Só conta o vértice se ele é ATIVO
                boolean isActive = false;
                for (Edge e : adj[x]) {
                    if (!e.isUsed) { isActive = true; break; }
                }
                if (isActive) count++;
                
                for (Edge e : adj[x]) {
                    if (!e.isUsed) {
                        int y = e.other(x);
                        if (!visited[y]) stack.push(y);
                    }
                }
            }
        }
        return count;
    }

    // Método auxiliar (não alterado)
    private static int nonIsolatedVertex(Graph G) {
        for (int v = 0; v < G.V(); v++)
            if (G.degree(v) > 0) return v;
        return -1;
    }

    public Iterable<Integer> cycle() { return cycle; }
    public boolean hasEulerianCycle() { return cycle != null; }
}