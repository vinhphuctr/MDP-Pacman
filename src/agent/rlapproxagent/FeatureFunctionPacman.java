package agent.rlapproxagent;

import pacman.elements.ActionPacman;
import pacman.elements.StateAgentPacman;
import pacman.elements.StateGamePacman;
import pacman.environnementRL.EnvironnementPacmanMDPClassic;
import environnement.Action;
import environnement.Etat;

/**
 * Vecteur de fonctions caracteristiques pour jeu de pacman: 4 fonctions phi_i(s,a)
 *
 * @author laetitiamatignon
 */
public class FeatureFunctionPacman implements FeatureFunction {
    private double[] vfeatures;

    private static int NBACTIONS = 4;//5 avec NONE possible pour pacman, 4 sinon
    //--> doit etre coherent avec EnvironnementPacmanRL::getActionsPossibles


    public FeatureFunctionPacman() { }

    @Override
    public int getFeatureNb() {
        return 4;
    }

    @Override
    public double[] getFeatures(Etat e, Action a) {
        vfeatures = new double[4];
        StateGamePacman stategamepacman;
        //EnvironnementPacmanMDPClassic envipacmanmdp = (EnvironnementPacmanMDPClassic) e;

        //calcule pacman resulting position a partir de Etat e
        if (e instanceof StateGamePacman) {
            stategamepacman = (StateGamePacman) e;
        } else {
            System.out.println("erreur dans FeatureFunctionPacman::getFeatures n'est pas un StateGamePacman");
            return vfeatures;
        }

        StateAgentPacman pacmanstate_next = stategamepacman.movePacmanSimu(0, new ActionPacman(a.ordinal()));

        vfeatures[0] = 1;
        // ---------------
        vfeatures[1] = 0;
        int[][] positions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};
        int x, y;
        StateAgentPacman agent;
        ActionPacman action = new ActionPacman(a.ordinal());
        StateAgentPacman pacman = stategamepacman.movePacmanSimu(0, action);
        for(int i = 0; i < stategamepacman.getNumberOfGhosts(); i++) {
            agent = stategamepacman.getGhostState(i);
            for (int[] pos : positions) {
                x = agent.getX() + pos[0];
                y = agent.getY() + pos[1];
                if(pacman.getX() == x && pacman.getY() == y) vfeatures[1]++;
            }
        }
        // ---------------
        if(stategamepacman.getMaze().isFood(pacman.getX(), pacman.getY()))
            vfeatures[2] = 1;
        else
            vfeatures[2] = 0;
        // ---------------
        int size = stategamepacman.getMaze().getSizeX() * stategamepacman.getMaze().getSizeY();
        vfeatures[3] = stategamepacman.getClosestDot(pacman) / size;

        return vfeatures;
    }

    public void reset() {
        vfeatures = new double[4];
    }

}
