package agent.rlagent;

import environnement.Action;
import environnement.Environnement;
import environnement.Etat;

import java.util.*;

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

    /**
     * @param alpha
     * @param gamma
     * @param _env
     */
    public QLearningAgent(double alpha, double gamma,
                          Environnement _env) {
        super(alpha, gamma, _env);
        qvaleurs = new HashMap<>();
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

        double valeur = this.getValeur(e);
        for(Action action : this.getActionsLegales(e)) {
            if (getQValeur(e, action) == valeur) returnactions.add(action);
        }


        return returnactions;
    }

    @Override
    public double getValeur(Etat e) {
        double value = Integer.MIN_VALUE;
        if(!qvaleurs.containsKey(e)) {
            qvaleurs.put(e, new HashMap<>());
            for(Action a : this.getActionsLegales(e)) {
                setQValeur(e, a, 0.0);
            }
            return 0.0;
        } else {
            for(Action a : this.qvaleurs.get(e).keySet()) {
                if(this.getQValeur(e, a) > value) {
                    value = this.getQValeur(e, a);
                }
            }
        }
        return value;
    }

    @Override
    public double getQValeur(Etat e, Action a) {
        double value = 0.0;
        if (qvaleurs.containsKey(e)) {
            if (qvaleurs.get(e).containsKey(a)) {
                value = qvaleurs.get(e).get(a);
            } else {
                qvaleurs.get(e).put(a, value);
            }
        } else {
            qvaleurs.put(e, new HashMap<>());
            qvaleurs.get(e).put(a, value);
        }
        return value;
    }


    @Override
    public void setQValeur(Etat e, Action a, double d) {
        if (qvaleurs.containsKey(e)) {
            qvaleurs.get(e).put(a, d);
        } else {
            qvaleurs.put(e, new HashMap<>());
            qvaleurs.get(e).put(a, d);
        }

        // mise a jour vmax et vmin pour affichage du gradient de couleur:
        //vmax est la valeur de max pour tout s de V
        //vmin est la valeur de min pour tout s de V
        // ...
        if (d > vmax) vmax = d;
        if (d < vmin) vmin = d;

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

        double old_estim = this.getQValeur(e, a);
        double new_echantillon = reward + this.gamma * this.getValeur(esuivant);
        double value = (1 - this.alpha) * old_estim + this.alpha * new_echantillon;
        this.setQValeur(e, a, value);


    }

    @Override
    public Action getAction(Etat e) {
        this.actionChoisie = this.stratExplorationCourante.getAction(e);
        return this.actionChoisie;
    }

    @Override
    public void reset() {
        super.reset();

        for (Map.Entry<Etat, HashMap<Action, Double>> etats : qvaleurs.entrySet()) {
            for (Map.Entry<Action, Double> actions : etats.getValue().entrySet()) {
                qvaleurs.get(etats.getKey()).put(actions.getKey(), 0.0);
            }
        }

        this.episodeNb = 0;
        this.notifyObs();
    }


}
