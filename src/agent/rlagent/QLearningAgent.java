package agent.rlagent;

import java.util.*;

import javafx.util.Pair;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;

import static java.util.stream.Collectors.toMap;

/**
 * Renvoi 0 pour valeurs initiales de Q
 *
 * @author laetitiamatignon
 */
public class QLearningAgent extends RLAgent {
    /**
     * format de memorisation des Q valeurs: utiliser partout setQValeur car cette methode notifie la vue
     */
    protected HashMap<Etat, HashMap<Action, Double>> qvaleurs;
    //AU CHOIX: vous pouvez utiliser une Map avec des Pair pour clés si vous préférez
    //protected HashMap<Pair<Etat,Action>,Double> qvaleurs;


    /**
     * @param alpha
     * @param gamma
     * @param Environnement
     */
    public QLearningAgent(double alpha, double gamma,
                          Environnement _env) {
        super(alpha, gamma, _env);
        qvaleurs = new HashMap<Etat, HashMap<Action, Double>>();
    }

    /**
     * renvoi action(s) de plus forte(s) valeur(s) dans l'etat e
     * (plusieurs actions sont renvoyees si valeurs identiques)
     * renvoi liste vide si aucunes actions possibles dans l'etat (par ex. etat absorbant)
     */
    @Override
    public List<Action> getPolitique(Etat e) {
        // retourne action de meilleures valeurs dans e selon Q : utiliser getQValeur()
        // retourne liste vide si aucune action legale (etat terminal)
        List<Action> returnactions = new ArrayList<Action>();
        if (this.getActionsLegales(e).size() == 0) {//etat  absorbant; impossible de le verifier via environnement
            System.out.println("aucune action legale");
            return new ArrayList<Action>();
        }

        Map<Action, Double> mapQValue = new HashMap<>();
        for (Action a : this.getActionsLegales(e)) {
            mapQValue.put(a, getQValeur(e, a));
        }

        Map<Action, Double> sorted = mapQValue
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));


        double maxValue;
        for (Map.Entry<Action, Double> entry : sorted.entrySet()) {
            maxValue = entry.getValue();
            if (maxValue == entry.getValue()) returnactions.add(entry.getKey());
        }

        return returnactions;


    }

    @Override
    public double getValeur(Etat e) {
        //TODO
        return 0.0;

    }

    @Override
    public double getQValeur(Etat e, Action a) {
        return qvaleurs.get(e).get(a);
    }


    @Override
    public void setQValeur(Etat e, Action a, double d) {
        //double qvalue =  (1 - alpha)*(getQValeur(e, a)) + alpha * (d + gamma);
        qvaleurs.get(e).replace(a, d);


        // mise a jour vmax et vmin pour affichage du gradient de couleur:
        //vmax est la valeur de max pour tout s de V
        //vmin est la valeur de min pour tout s de V
        // ...

        this.notifyObs();

    }


    /**
     * mise a jour du couple etat-valeur (e,a) apres chaque interaction <etat e,action a, etatsuivant esuivant, recompense reward>
     * la mise a jour s'effectue lorsque l'agent est notifie par l'environnement apres avoir realise une action.
     *
     * @param e
     * @param a
     * @param esuivant
     * @param reward
     */
    @Override
    public void endStep(Etat e, Action a, Etat esuivant, double reward) {
        if (RLAgent.DISPRL)
            System.out.println("QL mise a jour etat " + e + " action " + a + " etat' " + esuivant + " r " + reward);

        double qvalue = (1 - alpha) * (getQValeur(e, a)) + alpha * (reward + gamma * getQValeur(esuivant, getPolitique(esuivant).get(0)));
        setQValeur(e, a, qvalue);
    }

    @Override
    public Action getAction(Etat e) {
        this.actionChoisie = this.stratExplorationCourante.getAction(e);
        return this.actionChoisie;
    }

    @Override
    public void reset() {
        super.reset();
        // TODO

        this.episodeNb = 0;
        this.notifyObs();
    }


}
