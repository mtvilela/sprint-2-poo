// Intervencao com roçadeira costal/equipe a pe: indicada para vegetacao
// media, locais de dificil acesso ou perto de defensas/placas.
// Rende menos KM por dia e deixa altura residual um pouco maior.
public class RocadaManual extends IntervencaoOperacional {

    private static final double ALTURA_RESIDUAL = 8.0; // cm

    public RocadaManual() {
        super("Rocada Manual", 3.0);
    }

    @Override
    public String executarServico(TrechoRodovia trecho) {
        double antes = trecho.getAlturaVegetacao();
        trecho.registrarRocada(ALTURA_RESIDUAL);
        return String.format(
                "[%s] KM %.1f: equipe a pe cortou de %.1f cm para %.1f cm.",
                nome, trecho.getKm(), antes, ALTURA_RESIDUAL);
    }
}
