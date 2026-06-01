import java.util.ArrayList;
import java.util.List;

// Resultado do motor de regras: a lista de itens ja ordenada por prioridade
// (CRITICA primeiro). Sabe se imprimir de forma legivel no console.
public class RelatorioPrioridade {

    private final List<ItemRelatorio> itens = new ArrayList<>();

    public void adicionar(ItemRelatorio item) {
        itens.add(item);
    }

    public List<ItemRelatorio> getItens() {
        return itens;
    }

    // Quantos trechos realmente precisam de alguma intervencao.
    public long totalComIntervencao() {
        return itens.stream()
                .filter(i -> i.getIntervencaoRecomendada() != null)
                .count();
    }

    public void imprimir() {
        System.out.println("==========================================================================");
        System.out.println("                   RELATORIO DE PRIORIDADE DE ROCADA");
        System.out.println("==========================================================================");
        System.out.printf("%-8s %-9s %-7s %-9s %-6s %-20s%n",
                "PRIORID.", "TRECHO", "KM", "ALTURA", "FONTE", "INTERVENCAO");
        System.out.println("--------------------------------------------------------------------------");

        for (ItemRelatorio item : itens) {
            TrechoRodovia t = item.getTrecho();
            System.out.printf("%-8s %-9s %-7.1f %-9s %-6s %-20s%n",
                    item.getPrioridade(),
                    t.getId(),
                    t.getKm(),
                    String.format("%.1fcm", t.getAlturaVegetacao()),
                    item.isAtualizadoViaIoT() ? "IoT" : "Visual",
                    item.descricaoIntervencao());
        }

        System.out.println("--------------------------------------------------------------------------");
        System.out.printf("Total de trechos: %d | Precisam de intervencao: %d%n",
                itens.size(), totalComIntervencao());
        System.out.println("==========================================================================");
    }
}
