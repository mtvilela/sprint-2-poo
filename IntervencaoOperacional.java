// Classe ABSTRATA: representa o conceito GENERICO de uma intervencao de
// manutencao da vegetacao. Nao existe "executar uma intervencao qualquer":
// e sempre preciso saber QUAL servico sera prestado (rocada mecanizada,
// manual, pulverizacao, ...). Por isso a classe e abstrata e executarServico()
// e um metodo abstrato, obrigando cada filha a definir seu comportamento.
public abstract class IntervencaoOperacional {

    protected String nome;
    protected double rendimentoKmPorDia; // produtividade da equipe

    public IntervencaoOperacional(String nome, double rendimentoKmPorDia) {
        this.nome = nome;
        this.rendimentoKmPorDia = rendimentoKmPorDia;
    }

    // Metodo ABSTRATO: cada tipo de intervencao executa o servico de um jeito
    // e deixa a vegetacao em uma altura residual diferente.
    public abstract String executarServico(TrechoRodovia trecho);

    public String getNome() {
        return nome;
    }

    public double getRendimentoKmPorDia() {
        return rendimentoKmPorDia;
    }
}
