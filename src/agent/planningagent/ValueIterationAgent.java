package agent.planningagent;

import java.util.*;

import environnement.*;
import util.HashMapUtil;


/**
 * Cet agent met a jour sa fonction de valeur avec value iteration
 * et choisit ses actions selon la politique calculee.
 *
 * @author laetitiamatignon
 */
public class ValueIterationAgent extends PlanningValueAgent {
    /**
     * discount facteur
     */
    protected double gamma;

    /**
     * fonction de valeur des etats
     */
    protected HashMap<Etat, Double> V;

    /**
     * @param gamma
     * @param mdp
     */
    public ValueIterationAgent(double gamma, MDP mdp) {
        super(mdp);
        this.gamma = gamma;

        this.V = new HashMap<Etat, Double>();
        for (Etat etat : this.mdp.getEtatsAccessibles()) {
            V.put(etat, 0.0);
        }
    }

    public ValueIterationAgent(MDP mdp) {
        this(0.9, mdp);
    }

    /**
     * Mise a jour de V: effectue UNE iteration de value iteration (calcule V_k(s) en fonction de V_{k-1}(s'))
     * et notifie ses observateurs.
     * Ce n'est pas la version inplace (qui utilise la nouvelle valeur de V pour mettre a jour ...)
     */
    @Override
    public void updateV() {
        //delta est utilise pour detecter la convergence de l'algorithme
        //Dans la classe mere, lorsque l'on planifie jusqu'a convergence, on arrete les iterations
        //lorsque delta < epsilon
        //Dans cette classe, il  faut juste mettre a jour delta
        this.delta = 0.0;
        Etat etat, etatSuivant;
        Double value, valueTmp, proba, oldValue;

        for (Map.Entry<Etat, Double> pairV : this.V.entrySet()) {
            etat = pairV.getKey();
            value = pairV.getValue();

            for (Action action : this.mdp.getActionsPossibles(etat)) {
                valueTmp = 0.0;
                try {
                    for (Map.Entry<Etat, Double> pair : this.mdp.getEtatTransitionProba(etat, action).entrySet()) {
                        etatSuivant = pair.getKey();
                        proba = pair.getValue();
                        valueTmp += proba * (this.mdp.getRecompense(etat, action, etatSuivant) + gamma * this.V.get(etatSuivant));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (valueTmp > value) {
                    oldValue = value;
                    value = valueTmp;
                    this.V.put(etat, value);
                    if (value - oldValue > delta) delta = value - oldValue;
                }
            }
        }

        //mise a jour de vmax et vmin utilise pour affichage du gradient de couleur:
        //vmax est la valeur max de V pour tout s
        //vmin est la valeur min de V pour tout s
        // ...
        this.vmax = this.V.entrySet().stream().max((entry1, entry2) ->
                entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue();
        this.vmin = this.V.entrySet().stream().min((entry1, entry2) ->
                entry1.getValue() > entry2.getValue() ? 1 : -1).get().getValue();

        //******************* laisser cette notification a la fin de la methode
        this.notifyObs();
    }


    /**
     * renvoi l'action executee par l'agent dans l'etat e
     * Si aucune actions possibles, renvoi Action2D.NONE
     */
    @Override
    public Action getAction(Etat e) {
        List<Action> actions = getPolitique(e);
        if(actions.isEmpty()) return Action2D.NONE;
        // Si plus d'une action est possible, alors on en choisit une aléatoirement
        else if (actions.size() > 1) {
            Random r = new Random();
            return actions.get(r.nextInt(actions.size()));
        } else {
            return actions.get(0);
        }
    }

    @Override
    public double getValeur(Etat _e) {
        //Renvoie la valeur de l'Etat _e, c'est juste un getter, ne calcule pas la valeur ici
        //(la valeur est calculee dans updateV)
        return this.V.get(_e);
    }

    /**
     * renvoi action(s) de plus forte(s) valeur(s) dans etat
     * (plusieurs actions sont renvoyees si valeurs identiques, liste vide si aucune action n'est possible)
     */
    @Override
    public List<Action> getPolitique(Etat _e) {
        // retourne action de meilleure valeur dans _e selon V,
        // retourne liste vide si aucune action legale (etat absorbant)
        List<Action> returnActions = new ArrayList<Action>();
        if(this.mdp.estAbsorbant(_e)) return returnActions;

        Etat etatSuivant = null;
        Double value, valueMax = 0.0;
        // On parcourt toutes les actions possibles à partir de l'état _e
        for (Action action : this.mdp.getActionsPossibles(_e)) {
            try {
                // On récupère l'etatSuivant qui est l'état donné par l'action
                etatSuivant = this.mdp.getEtatTransitionProba(_e, action).entrySet().stream().max((entry1, entry2) ->
                        entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
            } catch (Exception e) {
                e.printStackTrace();
            }
            // Si l'action mène à la récompense max, on renvoie cette action
            if (this.mdp.getRecompense(_e, action, etatSuivant) == this.mdp.getRecompenseMax()) {
                returnActions.clear();
                returnActions.add(action);
                return returnActions;
            }
            // On récupère la valeur de cette état
            value = this.V.get(etatSuivant);
            // On compare cette valeur à la valueMax, pour ajouter ou non l'action
            // dans la liste d'actions possibles.
            if (value > valueMax) {
                returnActions.clear();
                returnActions.add(action);
                valueMax = value;
            } else if (value.equals(valueMax)) {
                returnActions.add(action);
            }
        }
        return returnActions;
    }

    @Override
    public void reset() {
        super.reset();
        //reinitialise les valeurs de V
        this.V.forEach((k,v) -> this.V.put(k, 0.0));
        this.notifyObs();
    }

    public HashMap<Etat, Double> getV() {
        return V;
    }

    public double getGamma() {
        return gamma;
    }

    @Override
    public void setGamma(double _g) {
        System.out.println("gamma= " + gamma);
        this.gamma = _g;
    }

}