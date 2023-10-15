# Connect X
## Progetto di algoritmi e strutture dati 2023-2024

### Gruppo

- **Petru Marcel Marincas**
- **Davide Donati**
- **Giuseppe Forciniti**

### Descrizione

Il progetto consiste nella realizzazione di un gioco chiamato Connect X, che è una variante del gioco Connect 4. Il gioco si svolge su una griglia di dimensione variabile, dove ogni cella può essere vuota o contenere una pedina di uno dei due giocatori. Lo scopo del gioco è quello di allineare un numero di pedine (uguale per entrambi i giocatori) in orizzontale, verticale o diagonale. Il primo giocatore che riesce a fare ciò vince la partita.

---
### Paper di riferimento
- Confronto tra Minmax & AlphaBeta Pruning con MCTS 
  - MM&ABP è migliore nel caso si ha poco tempo a disposizione (< 0.1 secondi). MCTS è meglio quando si ha più tempo.
  - [https://www.diva-portal.org/smash/get/diva2:1597267/FULLTEXT01.pdf]
- Confronto di diversi algoritmi per ConnectX
  - Oltre alle cose relative all'AI, che non implementeremo, MCTS sembra essere migliore quando la quantità di mosse è maggiore, mentre MM&ABP è migliore quando la quantità di mosse è minore. 
  - [https://arxiv.org/pdf/2210.08263.pdf]

Conclusioni nostre: MCTS sarebbe una soluzione ideale quando non si riesce ad arrivare a tutte le foglie. Genericamente, per Connect 4 giochi più piccoli si può considerare quindi MM&ABP come una soluzione migliore, mentre per tutti gli altri casi MCTS è migliore.

Altri metodi, ovvero l'ibrido tra MCTS e MM, oppure modi puramente euristici, oppure ancora metodi che aggiungono "complicità" di calcolo tendenzialmente riducono la performance. Sembra piuttosto che il numero di mosse che si possono guardare in avanti sia il fattore più importante per la performance.

Inoltre, troviamo che la ricerca di euristiche complesse sia una difficoltà che difficilmente sarà appagata, in quanto probabilmente non riuscirà a battere MCTS. Se lo riesce a battere, probabilmente sarà in grado di farlo solo per alcuni dei valori di M, N o X. Quindi questo non sarà un focus del nostro progetto.



---

Da farsi (in questo ordine):
- Implementare l'iterative deepening
- Implementare l'eval in modo che contenga anche:
  - Valutazione alta nel caso ci siano 2 possibili mosse che porta alla vittoria nella prossima mossa
    - Quindi per esempio il caso dove si ha "--ooo--" e bisogna metterne 4 in fila, in questo caso sia a sinitra che a destra si vince
    - Pertanto dobbiamo valutarla in modo estremamente più alta rispetto al valore di ora di "3" sottoinsiemi, per esempio "1000000" - come valore simbolico che indica una vittoria piuttosto assicurata.
    - Sempre da mettere in considerazione e controllare  la vittoria dell'avversario in primis
  - Valutare la stessa situazione per anche per il nemico, in modo da ritornare un valore molto basso dall'eval, indicando una sconfitta imminente
  - Se entrambi hanno questa possiblità, capire chi potrebbe vincere
  - Valutare l'interrupt della vittoria dell'avversario
    - Questo valore è molto più alto del valore del doppio caso di vittoria
- Implementare un albero che salva le mosse esplorate in precedenza nella classe player
- Evitare di esplorare mosse che killano immediatamente
