import java.util.Comparator;

// O MOTOR DE REGRAS (inteligencia da Sprint 2).
// Varre um array de trechos e gera um Relatorio de Prioridade automatico,
// decidindo quem precisa de Rocada Mecanizada, Manual, Pulverizacao ou nada.
public class MotorDeRegras {

    // Limiares de altura da vegetacao (cm) que disparam cada prioridade.
    private static final double LIMIAR_CRITICO = 40.0;
    private static final double LIMIAR_ALTO    = 25.0;
    private static final double LIMIAR_MEDIO   = 15.0;

    // Acima desta taxa de crescimento (cm/dia) o trecho e considerado
    // "de rebrote rapido" e a pulverizacao passa a fazer mais sentido.
    private static final double TAXA_REBROTE_RAPIDO = 2.0;

    // Algoritmo principal: recebe o array, atualiza o que for IoT,
    // classifica cada trecho e devolve o relatorio ordenado.
    public RelatorioPrioridade gerarRelatorio(TrechoRodovia[] trechos) {
        RelatorioPrioridade relatorio = new RelatorioPrioridade();

        for (TrechoRodovia trecho : trechos) {
            if (trecho == null) {
                continue;
            }

            // Trechos com IoT se atualizam sozinhos, sem inspecao visual.
            boolean viaIoT = false;
            if (trecho instanceof MonitoravelViaIoT) {
                ((MonitoravelViaIoT) trecho).transmitirDadosSensor();
                viaIoT = true;
            }

            Prioridade prioridade = classificar(trecho);
            IntervencaoOperacional intervencao = recomendarIntervencao(trecho, prioridade);
            relatorio.adicionar(new ItemRelatorio(trecho, prioridade, intervencao, viaIoT));
        }

        // Ordena pela urgencia (CRITICA tem o menor ordinal).
        relatorio.getItens().sort(Comparator.comparingInt(i -> i.getPrioridade().ordinal()));
        return relatorio;
    }

    // REGRA 1: a prioridade depende da altura da vegetacao.
    public Prioridade classificar(TrechoRodovia trecho) {
        double altura = trecho.getAlturaVegetacao();
        if (altura >= LIMIAR_CRITICO) {
            return Prioridade.CRITICA;
        } else if (altura >= LIMIAR_ALTO) {
            return Prioridade.ALTA;
        } else if (altura >= LIMIAR_MEDIO) {
            return Prioridade.MEDIA;
        } else {
            return Prioridade.BAIXA;
        }
    }

    // REGRA 2: o tipo de intervencao depende da prioridade e do rebrote.
    public IntervencaoOperacional recomendarIntervencao(TrechoRodovia trecho, Prioridade prioridade) {
        boolean rebroteRapido = trecho.taxaCrescimentoDiaria() >= TAXA_REBROTE_RAPIDO;

        switch (prioridade) {
            case CRITICA:
                // Vegetacao alta e densa: so o trator da conta.
                return new RocadaMecanizada();
            case ALTA:
                // Rebrote rapido -> pulveriza para nao voltar logo;
                // senao, mecaniza.
                return rebroteRapido ? new Pulverizacao() : new RocadaMecanizada();
            case MEDIA:
                // Volume menor: equipe a pe resolve.
                return new RocadaManual();
            case BAIXA:
            default:
                // Sem necessidade de acao agora, apenas monitorar.
                return null;
        }
    }
}
