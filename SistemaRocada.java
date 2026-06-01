import java.util.Scanner;

// Aplicacao da Sprint 2: monta um array de trechos da rodovia, simula o
// crescimento da vegetacao e usa o MotorDeRegras para gerar e executar o
// Relatorio de Prioridade de roçada.
public class SistemaRocada {

    private final TrechoRodovia[] trechos;
    private final MotorDeRegras motor;
    private final Scanner scanner;

    public SistemaRocada() {
        this.scanner = new Scanner(System.in);
        this.motor = new MotorDeRegras();

        // Array de trechos: alguns umidos (com IoT) e alguns secos (visuais).
        this.trechos = new TrechoRodovia[] {
            new TrechoUmido("BR101-A", 12.0, 18.0, "IOT-001"),
            new TrechoSeco ("BR101-B", 27.5, 22.0),
            new TrechoUmido("BR101-C", 40.0, 35.0, "IOT-002"),
            new TrechoSeco ("BR101-D", 58.0,  9.0),
            new TrechoUmido("BR101-E", 73.0, 12.0, "IOT-003")
        };
    }

    public void iniciar() {
        int opcao;
        do {
            exibirMenu();
            opcao = lerInteiro("Escolha uma opcao: ");
            System.out.println();
            switch (opcao) {
                case 1 -> listarTrechos();
                case 2 -> simularPassagemDeTempo();
                case 3 -> gerarRelatorio();
                case 4 -> executarIntervencoes();
                case 0 -> System.out.println("Encerrando o sistema de roçada. Ate logo!");
                default -> System.out.println("Opcao invalida. Tente novamente.");
            }
            System.out.println();
        } while (opcao != 0);
    }

    private void exibirMenu() {
        System.out.println("==================================================");
        System.out.println("   SISTEMA DE PRIORIZACAO DE ROCADA - MOTIVA");
        System.out.println("==================================================");
        System.out.println(" 1 - Listar trechos");
        System.out.println(" 2 - Simular passagem de tempo (crescimento)");
        System.out.println(" 3 - Gerar relatorio de prioridade");
        System.out.println(" 4 - Executar intervencoes recomendadas");
        System.out.println(" 0 - Sair");
        System.out.println("--------------------------------------------------");
    }

    private void listarTrechos() {
        System.out.println("----- TRECHOS MONITORADOS -----");
        for (TrechoRodovia t : trechos) {
            String fonte = (t instanceof MonitoravelViaIoT)
                    ? " [IoT: " + ((MonitoravelViaIoT) t).getIdSensor() + "]"
                    : " [inspecao visual]";
            System.out.println("  - " + t + fonte);
        }
    }

    private void simularPassagemDeTempo() {
        int dias = lerInteiro("Quantos dias deseja simular? ");
        for (TrechoRodovia t : trechos) {
            t.simularCrescimento(dias);
        }
        System.out.println("Crescimento simulado para " + dias + " dia(s).");
        System.out.println("(Trechos umidos crescem mais rapido que os secos.)");
    }

    private void gerarRelatorio() {
        RelatorioPrioridade relatorio = motor.gerarRelatorio(trechos);
        relatorio.imprimir();
    }

    private void executarIntervencoes() {
        RelatorioPrioridade relatorio = motor.gerarRelatorio(trechos);
        System.out.println("----- EXECUCAO DAS INTERVENCOES RECOMENDADAS -----");

        boolean houveServico = false;
        for (ItemRelatorio item : relatorio.getItens()) {
            IntervencaoOperacional intervencao = item.getIntervencaoRecomendada();
            if (intervencao == null) {
                continue;
            }
            // POLIMORFISMO: a mesma chamada gera comportamentos diferentes.
            System.out.println("  " + intervencao.executarServico(item.getTrecho()));
            houveServico = true;
        }

        if (!houveServico) {
            System.out.println("Nenhum trecho precisa de intervencao no momento.");
        } else {
            System.out.println("\nIntervencoes concluidas. Vegetacao dos trechos atualizada.");
        }
    }

    private int lerInteiro(String mensagem) {
        System.out.print(mensagem);
        while (true) {
            String linha = scanner.nextLine().trim();
            try {
                return Integer.parseInt(linha);
            } catch (NumberFormatException e) {
                System.out.print("Digite um numero inteiro valido: ");
            }
        }
    }

    public static void main(String[] args) {
        SistemaRocada sistema = new SistemaRocada();
        sistema.iniciar();
    }
}
