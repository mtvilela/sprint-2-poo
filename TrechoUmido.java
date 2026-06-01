import java.util.Random;

// Trecho de terreno UMIDO: a vegetacao cresce rapido.
// Alem de ser um TrechoRodovia (HERANCA), ele POSSUI tecnologia instalada,
// entao implementa a interface MonitoravelViaIoT (CONTRATO de comportamento).
public class TrechoUmido extends TrechoRodovia implements MonitoravelViaIoT {

    private static final double TAXA_BASE = 2.5; // cm por dia (cresce rapido)

    private final String idSensor;
    private final Random random = new Random();

    public TrechoUmido(String id, double km, double alturaInicial, String idSensor) {
        super(id, km, alturaInicial);
        this.idSensor = idSensor;
    }

    @Override
    public double taxaCrescimentoDiaria() {
        return TAXA_BASE;
    }

    @Override
    public String getTipoTerreno() {
        return "Umido";
    }

    @Override
    public double transmitirDadosSensor() {
        // Simula a telemetria: o sensor mede uma pequena variacao em torno
        // da altura atual e ja atualiza o trecho automaticamente.
        double medicao = alturaVegetacao + (random.nextDouble() * 2.0 - 1.0);
        medicao = Math.max(0, medicao);
        setAlturaVegetacao(medicao);
        return medicao;
    }

    @Override
    public String getIdSensor() {
        return idSensor;
    }
}
