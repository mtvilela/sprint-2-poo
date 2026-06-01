# Global Solution 2026 - POO | Sprint 2: O Motor de Regras (Inteligência)

Sistema de **monitoramento e priorização de roçada de vegetação nas rodovias** da Motiva,
feito em **Java**. Esta Sprint adiciona a *inteligência*: diferentes comportamentos de
crescimento da vegetação, diferentes tipos de intervenção e um **algoritmo que varre um
array de trechos e gera um Relatório de Prioridade automático**.

## Integrantes

- Daniel Castro Sanches — RM 563333
- Maria Eduarda de Áquila Amaral — RM 563783
- Matheus Vilela Silveira — RM 564989

## Como executar

```bash
# compilar tudo
javac *.java

# rodar a aplicação interativa (menu)
java SistemaRocada

# rodar os testes unitários
java Testes
```

> Não há dependências externas — Java puro (`javac`/`java`).

## Estrutura do projeto

| Arquivo | Papel |
|---|---|
| `TrechoRodovia.java` | **Classe abstrata** base de um trecho; método abstrato `taxaCrescimentoDiaria()`. |
| `TrechoUmido.java` | Trecho que cresce rápido **e** implementa `MonitoravelViaIoT`. |
| `TrechoSeco.java` | Trecho que cresce devagar, atualizado por inspeção visual. |
| `MonitoravelViaIoT.java` | **Interface** enxuta com `transmitirDadosSensor()`. |
| `IntervencaoOperacional.java` | **Classe abstrata** base; método abstrato `executarServico()`. |
| `RocadaMecanizada.java` / `RocadaManual.java` / `Pulverizacao.java` | Tipos de intervenção (filhas). |
| `MotorDeRegras.java` | O **motor de regras**: varre os trechos e gera o relatório. |
| `Prioridade.java` / `ItemRelatorio.java` / `RelatorioPrioridade.java` | Modelo e impressão do **Relatório de Prioridade**. |
| `SistemaRocada.java` | Aplicação com menu (`main`). |
| `Testes.java` | Testes unitários em Java puro. |

## Regras do motor (resumo)

A prioridade vem da **altura da vegetação**; a intervenção, da prioridade + velocidade de rebrote:

| Altura | Prioridade | Intervenção recomendada |
|---|---|---|
| ≥ 40 cm | CRÍTICA | Roçada Mecanizada (trator) |
| 25–40 cm | ALTA | Pulverização se rebrote rápido (terreno úmido); senão Mecanizada |
| 15–25 cm | MÉDIA | Roçada Manual (equipe a pé) |
| < 15 cm | BAIXA | Nenhuma (apenas monitorar) |

Trechos com IoT (`MonitoravelViaIoT`) se atualizam sozinhos antes da varredura,
sem inspeção visual.

## Conceitos de POO da Sprint

### Classes Abstratas
`IntervencaoOperacional` e `TrechoRodovia` são abstrações puras: representam o **conceito
base genérico** (uma intervenção qualquer / um trecho qualquer) e obrigam as filhas a
definir o comportamento concreto (`executarServico()`, `taxaCrescimentoDiaria()`).

**Reflexão — por que não faz sentido executar uma "Intervenção Operacional" genérica?**
Porque "intervenção operacional" é só o conceito. Em campo, a equipe nunca executa "uma
intervenção qualquer": ela leva um trator (mecanizada), uma equipe a pé (manual) ou um
pulverizador (químico) — cada uma com custo, rendimento e resultado diferentes. Despachar
uma intervenção sem dizer **qual** seria impossível de planejar e orçar. Por isso a classe
é abstrata: ela existe para ser especializada, não instanciada.

### Interfaces
`MonitoravelViaIoT` é um **contrato de comportamento desacoplado da hierarquia**: define
apenas `transmitirDadosSensor()` e `getIdSensor()` (mantida enxuta — *Interface Segregation
Principle*). Só os trechos que possuem tecnologia instalada a implementam.

**Reflexão — herdar de classe abstrata vs. implementar uma interface?**
- **Herdar de `TrechoRodovia`** responde *"o que o objeto É"*: um `TrechoUmido` **é um**
  trecho de rodovia e herda estado (altura, KM) e comportamento comum.
- **Implementar `MonitoravelViaIoT`** responde *"o que o objeto CONSEGUE FAZER"*: a
  capacidade de transmitir dados via IoT é ortogonal ao tipo de terreno. Nem todo trecho
  tem sensor, e essa capacidade poderia existir em objetos de outra hierarquia. A interface
  evita acoplamento à árvore de herança e permite que o `MotorDeRegras` trate qualquer
  objeto monitorável de forma uniforme (inclusive um *mock* nos testes).

## Testes unitários (`Testes.java`)

- **Impossibilidade de instanciar a base com `new`**: além da garantia do compilador,
  validamos via reflexão (`Modifier.isAbstract`) e mostramos que instanciar
  `IntervencaoOperacional` por reflexão lança erro.
- **Mock de `MonitoravelViaIoT`**: `SensorMock` implementa a interface com leituras
  pré-programadas, comprovando a captura de dados de crescimento.
- Cobertura adicional: crescimento úmido > seco, classificação de prioridade, recomendação
  de intervenção e efeito de `executarServico()` na vegetação.
