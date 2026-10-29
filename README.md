# Semestrálna práca: Útek z basementu

## 👥 Autori
- Lucia Horváthová
- Anna Klubalová
- Tibor Kováč
- Peter Šonka

## 🎮 Názov hry
**Útek z basementu**

---

## 📋 O projekte
Preberáš sa na studenej betónovej podlahe v temnom suteréne bez spomienok na to, ako si sa sem dostal. Dvere sú zamknuté a na stene svieti mrazivý odkaz: „Nemal si to zistiť.“

Tvojou hlavnou úlohou je prežiť, preskúmať tajomné priestory budovy, prekonávať zamknuté prekážky, riešiť technické hádanky (ako oprava elektriny či kotolne) a nájsť cestu na slobodu.

Hra však ponúka viacero koncov – ak chceš dosiahnuť úplné víťazstvo a spravodlivosť, nestačí len ujsť. Musíš pozbierať dôkazy, prečítať skryté dokumenty a odhaliť identitu a motív únoscu skôr, než opustíš budovu.

### Prostredie
Basement tvorí sklad, hlavná chodba, kancelária, dielňa, kotolňa, technická miestnosť a zamknutý výstup na prízemie.

### Konce hry

Základná verzia má tri konce:

* **Úplné odhalenie** – únik s kódom k východu a všetkými dôkazmi (úspešný koniec)
* **Útek bez dôkazov** – únik bez možnosti usvedčiť vinníka (úspešný koniec)
* **Neúspešný koniec** – hráč sa vzdá a zostane uväznený

### Príkazy

| Príkaz | Popis |
|---|---|
| `pomoc` | zobrazí zoznam príkazov |
| `pozri` | vypíše popis miestnosti |
| `prohledaj` | vypíše predmety v miestnosti |
| `jdi [miestnost]` | presun do inej miestnosti |
| `vezmi [predmet]` | zoberie predmet do inventára |
| `inventar` | vypíše obsah inventára |
| `pouzi [predmet]` | použije predmet |
| `procti [predmet]` | prečíta dokument |
| `mluv martin` | rozhovor s Martinom |
| `dokazy` | zoznam získaných dôkazov |
| `uloz` / `nacitaj` | uloží / načíta stav hry |
| `konec` | ukončí hru |

## ⚙️ Inštalácia a spustenie
1. Klonujte repozitár:
   ```bash
   git clone [https://github.com/tibi-kov07/semestralkaJAVA.git](https://github.com/tibi-kov07/semestralkaJAVA.git)

2. Otvorte projekt v IDE (napr. IntelliJ IDEA) a spustite triedu `Main`.
