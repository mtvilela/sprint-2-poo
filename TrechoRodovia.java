// Classe ABSTRATA: molde base de um trecho de rodovia monitorado.
// Nao pode ser instanciada diretamente; cada terreno (umido, seco, ...)
// define seu PROPRIO comportamento de crescimento da vegetacao.
public abstract class TrechoRodovia {

    protected String id;
    protected double km;                 // marco quilometrico do trecho
    protected double alturaVegetacao;    // altura atual da vegetacao em cm
    protected int diasSemRocada;         // dias desde a ultima intervencao

    public TrechoRodovia(String id, double km, double alturaInicial) {
        this.id = id;
        this.km = km;
        this.alturaVegetacao = Math.max(0, alturaInicial);
        this.diasSemRocada = 0;
    }

    // Metodo ABSTRATO: o coracao do motor de regras.
    // Cada tipo de terreno cresce a uma taxa diferente (cm por dia).
    public abstract double taxaCrescimentoDiaria();

    // Descreve o terreno (ex.: "Umido", "Seco"). Usado nos relatorios.
    public abstract String getTipoTerreno();

    // POLIMORFISMO: a simulacao e a mesma, mas o resultado depende
    // da taxa de cada subclasse.
    public void simularCrescimento(int dias) {
        if (dias <= 0) {
            return;
        }
        this.alturaVegetacao += taxaCrescimentoDiaria() * dias;
        this.diasSemRocada += dias;
    }

    // Chamado por uma IntervencaoOperacional apos o servico ser executado.
    public void registrarRocada(double alturaResidual) {
        this.alturaVegetacao = Math.max(0, alturaResidual);
        this.diasSemRocada = 0;
    }

    public String getId() {
        return id;
    }

    public double getKm() {
        return km;
    }

    public double getAlturaVegetacao() {
        return alturaVegetacao;
    }

    // Atualiza a altura medida (usado tanto por inspecao visual quanto por IoT).
    public void setAlturaVegetacao(double altura) {
        if (altura >= 0) {
            this.alturaVegetacao = altura;
        }
    }

    public int getDiasSemRocada() {
        return diasSemRocada;
    }

    @Override
    public String toString() {
        return String.format("Trecho %s (KM %.1f) | %s | %.1f cm | %d dias sem rocada",
                id, km, getTipoTerreno(), alturaVegetacao, diasSemRocada);
    }
}
