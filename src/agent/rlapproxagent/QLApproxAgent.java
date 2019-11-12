package agent.rlapproxagent;


import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import agent.rlagent.QLearningAgent;
import agent.rlagent.RLAgent;
import environnement.Action;
import environnement.Environnement;
import environnement.Etat;

/**
 * Agent qui apprend avec QLearning en utilisant approximation de la Q-valeur :
 * approximation lineaire de fonctions caracteristiques
 *
 * @author laetitiamatignon
 */
public class QLApproxAgent extends QLearningAgent {

    private FeatureFunction featureFunction;
    private double[] poids;

    public QLApproxAgent(double alpha, double gamma, Environnement _env, FeatureFunction _featurefunction) {
        super(alpha, gamma, _env);
        this.featureFunction = _featurefunction;
        this.poids = new double[_featurefunction.getFeatureNb()];

        for(int i = 0; i < poids.length; i++) {
            poids[i] = 0.0;
        }
    }


    @Override
    public double getQValeur(Etat e, Action a) {
        double[] features = featureFunction.getFeatures(e, a);
        double result = 0.0;
        for(int i = 0; i < features.length; i++) result += poids[i] * features[i];

        return result;

    }


    @Override
    public void endStep(Etat e, Action a, Etat esuivant, double reward) {
        if (RLAgent.DISPRL) {
            System.out.println("QL: mise a jour poids pour etat \n" + e + " action " + a + " etat' \n" + esuivant + " r " + reward);
        }
        //inutile de verifier si e etat absorbant car dans runEpisode et threadepisode
        //arrete episode lq etat courant absorbant

        Double max = 0.0;

        for(Action action : this.env.getActionsPossibles(esuivant))
            if (this.getQValeur(esuivant, action) > max) max = this.getQValeur(esuivant, action);

        for(int i = 0; i < poids.length; i++)
            poids[i] = poids[i] + alpha * (reward + (gamma * max) - this.getQValeur(e, a)) * featureFunction.getFeatures(e, a)[i];


    }

    @Override
    public void reset() {
        super.reset();
        this.qvaleurs.clear();

        for(int i = 0; i < poids.length; i++) {
            poids[i] = 0.0;
        }

        this.episodeNb = 0;
        this.notifyObs();
    }


}
