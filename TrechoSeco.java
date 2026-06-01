// Trecho de terreno SECO: a vegetacao cresce devagar.
// NAO possui tecnologia instalada, entao NAO implementa MonitoravelViaIoT:
// a atualizacao depende de inspecao visual em campo.
public class TrechoSeco extends TrechoRodovia {

    private static final double TAXA_BASE = 0.8; // cm por dia (cresce devagar)

    public TrechoSeco(String id, double km, double alturaInicial) {
        super(id, km, alturaInicial);
    }

    @Override
    public double taxaCrescimentoDiaria() {
        return TAXA_BASE;
    }

    @Override
    public String getTipoTerreno() {
        return "Seco";
    }
}
