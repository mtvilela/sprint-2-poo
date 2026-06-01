import java.lang.reflect.Modifier;

// Testes unitarios em Java puro (sem dependencia de JUnit, roda com `java Testes`).
// Um mini-harness imprime PASS/FAIL para cada cenario pedido na Sprint 2.
public class Testes {

    private static int passou = 0;
    private static int falhou = 0;

    public static void main(String[] args) {
        System.out.println("===== EXECUTANDO TESTES UNITARIOS =====\n");

        testeClasseAbstrataNaoInstanciavel();
        testeMockMonitoravelViaIoTCapturaCrescimento();
        testeCrescimentoUmidoMaiorQueSeco();
        testeMotorClassificaPrioridade();
        testeMotorRecomendaIntervencao();
        testeExecutarServicoReduzVegetacao();

        System.out.println("\n===== RESULTADO: " + passou + " passou, " + falhou + " falhou =====");
        if (falhou > 0) {
            System.exit(1);
        }
    }

    // SUGESTAO DA SPRINT: garantir a impossibilidade de instanciar a base com new.
    // O compilador ja impede `new IntervencaoOperacional(...)`; aqui validamos via
    // reflexao que as classes-base estao realmente marcadas como abstratas.
    private static void testeClasseAbstrataNaoInstanciavel() {
        boolean intervencaoAbstrata = Modifier.isAbstract(IntervencaoOperacional.class.getModifiers());
        verificar("IntervencaoOperacional e abstrata (nao instanciavel com new)", intervencaoAbstrata);

        boolean trechoAbstrato = Modifier.isAbstract(TrechoRodovia.class.getModifiers());
        verificar("TrechoRodovia e abstrata (nao instanciavel com new)", trechoAbstrato);

        // Tentar instanciar via reflexao deve estourar InstantiationException.
        boolean lancouErro = false;
        try {
            IntervencaoOperacional.class.getDeclaredConstructor(String.class, double.class)
                    .newInstance("X", 1.0);
        } catch (InstantiationException e) {
            lancouErro = true;
        } catch (Exception outras) {
            lancouErro = true; // qualquer falha de instanciacao tambem confirma o ponto
        }
        verificar("Instanciar IntervencaoOperacional via reflexao falha", lancouErro);
    }

    // SUGESTAO DA SPRINT: Mock que implementa MonitoravelViaIoT e testa a
    // captura de dados de crescimento.
    private static void testeMockMonitoravelViaIoTCapturaCrescimento() {
        SensorMock mock = new SensorMock("MOCK-01", new double[] {10.0, 23.5, 41.0});

        double leitura1 = mock.transmitirDadosSensor();
        double leitura2 = mock.transmitirDadosSensor();
        double leitura3 = mock.transmitirDadosSensor();

        verificar("Mock captura 1a leitura (10.0)", leitura1 == 10.0);
        verificar("Mock captura crescimento (23.5)", leitura2 == 23.5);
        verificar("Mock captura crescimento (41.0)", leitura3 == 41.0);
        verificar("Mock expoe id do sensor", "MOCK-01".equals(mock.getIdSensor()));

        // O motor deve conseguir tratar o mock como qualquer monitoravel.
        verificar("Mock e um MonitoravelViaIoT", mock instanceof MonitoravelViaIoT);
    }

    private static void testeCrescimentoUmidoMaiorQueSeco() {
        TrechoRodovia umido = new TrechoUmido("U", 1.0, 0.0, "S-U");
        TrechoRodovia seco = new TrechoSeco("S", 2.0, 0.0);

        umido.simularCrescimento(10);
        seco.simularCrescimento(10);

        verificar("Trecho umido cresce mais que o seco",
                umido.getAlturaVegetacao() > seco.getAlturaVegetacao());
    }

    private static void testeMotorClassificaPrioridade() {
        MotorDeRegras motor = new MotorDeRegras();

        verificar("Altura 45cm -> CRITICA",
                motor.classificar(new TrechoSeco("a", 0, 45)) == Prioridade.CRITICA);
        verificar("Altura 30cm -> ALTA",
                motor.classificar(new TrechoSeco("b", 0, 30)) == Prioridade.ALTA);
        verificar("Altura 18cm -> MEDIA",
                motor.classificar(new TrechoSeco("c", 0, 18)) == Prioridade.MEDIA);
        verificar("Altura 5cm -> BAIXA",
                motor.classificar(new TrechoSeco("d", 0, 5)) == Prioridade.BAIXA);
    }

    private static void testeMotorRecomendaIntervencao() {
        MotorDeRegras motor = new MotorDeRegras();

        TrechoRodovia critico = new TrechoSeco("crit", 0, 50);
        IntervencaoOperacional iCritico =
                motor.recomendarIntervencao(critico, motor.classificar(critico));
        verificar("CRITICA -> Rocada Mecanizada", iCritico instanceof RocadaMecanizada);

        // ALTA + rebrote rapido (umido) -> Pulverizacao.
        TrechoRodovia altoUmido = new TrechoUmido("au", 0, 30, "S");
        IntervencaoOperacional iAltoUmido =
                motor.recomendarIntervencao(altoUmido, motor.classificar(altoUmido));
        verificar("ALTA + umido -> Pulverizacao", iAltoUmido instanceof Pulverizacao);

        TrechoRodovia medio = new TrechoSeco("med", 0, 18);
        IntervencaoOperacional iMedio =
                motor.recomendarIntervencao(medio, motor.classificar(medio));
        verificar("MEDIA -> Rocada Manual", iMedio instanceof RocadaManual);

        TrechoRodovia baixo = new TrechoSeco("bx", 0, 5);
        IntervencaoOperacional iBaixo =
                motor.recomendarIntervencao(baixo, motor.classificar(baixo));
        verificar("BAIXA -> sem intervencao (null)", iBaixo == null);
    }

    private static void testeExecutarServicoReduzVegetacao() {
        TrechoRodovia trecho = new TrechoSeco("t", 0, 60);
        new RocadaMecanizada().executarServico(trecho);
        verificar("Apos rocada mecanizada a vegetacao cai para 5cm",
                trecho.getAlturaVegetacao() == 5.0);
        verificar("Dias sem rocada zeram apos o servico",
                trecho.getDiasSemRocada() == 0);
    }

    // ----- infra do mini-harness -----

    private static void verificar(String descricao, boolean condicao) {
        if (condicao) {
            passou++;
            System.out.println("[PASS] " + descricao);
        } else {
            falhou++;
            System.out.println("[FAIL] " + descricao);
        }
    }

    // MOCK: implementa o contrato MonitoravelViaIoT devolvendo leituras
    // pre-programadas, simulando um sensor de campo.
    static class SensorMock implements MonitoravelViaIoT {
        private final String idSensor;
        private final double[] leituras;
        private int indice = 0;

        SensorMock(String idSensor, double[] leituras) {
            this.idSensor = idSensor;
            this.leituras = leituras;
        }

        @Override
        public double transmitirDadosSensor() {
            double valor = leituras[indice];
            if (indice < leituras.length - 1) {
                indice++;
            }
            return valor;
        }

        @Override
        public String getIdSensor() {
            return idSensor;
        }
    }
}
