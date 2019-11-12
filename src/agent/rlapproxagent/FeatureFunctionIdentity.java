package agent.rlapproxagent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import environnement.Action;
import environnement.Action2D;
import environnement.Etat;
import javafx.util.Pair;

/**
 * Vecteur de fonctions caracteristiques phi_i(s,a): autant de fonctions caracteristiques que de paire (s,a),
 * <li> pour chaque paire (s,a), un seul phi_i qui vaut 1  (vecteur avec un seul 1 et des 0 sinon).
 * <li> pas de biais ici
 *
 * @author laetitiamatignon
 */
public class FeatureFunctionIdentity implements FeatureFunction {

    private double[] matrix;

    public FeatureFunctionIdentity(int _nbEtat, int _nbAction) {
        for(int i = 0; i < _nbEtat * _nbEtat; i++) {
            if(i == 4) matrix[i] = 1;
            else matrix[i] = 0;
        }
    }

    @Override
    public int getFeatureNb() {
        return matrix.length;
    }

    @Override
    public double[] getFeatures(Etat e, Action a) {
        return matrix;
    }


}
