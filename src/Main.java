
public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Lembrete para rodar: java Main <caminho para arquivo do grafo> <H para Hierholzer ou F para Fleury> ex: java Main ..\\data\\c4.txt H ou java -cp src Main data\\c4.txt H");
            return;
        }

        Graph G;
        try {
            In in = new In(args[0]);
            G = new Graph(in);
        } catch (Exception e) {
            System.out.println("ERRO: falha ao ler o grafo");
            return;
        }

        // Verifica se todos os vértices têm grau par
        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) % 2 != 0) {
                System.out.println("Nao Euleriano: Vertices de grau impar");
                return;
            }
        }

        // Encontra vértice inicial e conta vértices não isolados
        int startVertex = -1, nonIsolated = 0;
        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) > 0) {
                if (startVertex == -1) startVertex = v;
                nonIsolated++;
            }
        }

        // Grafo sem arestas
        if (nonIsolated == 0) {
            System.out.println("");
            return;
        }

        // Verifica conectividade ignorando isolados
        BreadthFirstPaths bfs = new BreadthFirstPaths(G, startVertex);
        int reached = 0;
        for (int v = 0; v < G.V(); v++) {
            if (G.degree(v) > 0 && bfs.hasPathTo(v)) reached++;
        }
        if (reached != nonIsolated) {
            System.out.println("Nao Euleriano: Grafo desconexo");
            return;
        }

        // Escolhe algoritmo
        String algoritmo = args.length > 1 ? args[1] : "H";

        if (algoritmo.equalsIgnoreCase("H")) {
            EulerianCycle e = new EulerianCycle(G);
            if (e.hasEulerianCycle()) {
                for (int v : e.cycle()) System.out.print(v + " ");
                System.out.println();
            } else {
                System.out.println("Nao Euleriano");
            }
        } else if (algoritmo.equalsIgnoreCase("F")) {
            EulerianCycleFleury f = new EulerianCycleFleury(G);
            if (f.hasEulerianCycle()) {
                for (int v : f.cycle()) System.out.print(v + " ");
                System.out.println();
            } else {
                System.out.println("Nao Euleriano");
            }
        } else {
            System.out.println("Algoritmo desconhecido. Use 'H' para Hierholzer ou 'F' para Fleury.");
        }
    }
}
