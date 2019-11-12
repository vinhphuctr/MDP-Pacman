package pacman.environnementRL;

import environnement.Etat;
import pacman.elements.ActionPacman;
import pacman.elements.StateGamePacman;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Classe pour définir un etat du MDP pour l'environnement pacman avec QLearning tabulaire
 */
public class EtatPacmanMDPClassic implements Etat, Cloneable {

    private int[][] matrix;
    private List<Point> casesAdjacentes;
    private int directionClosestDot;


    private int[][] positions = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}};

    public EtatPacmanMDPClassic(StateGamePacman _stategamepacman) {
        casesAdjacentes = new ArrayList<>();
        for (int[] point: positions) casesAdjacentes.add(new Point(point[0], point[1]));

        matrix = new int[5][5];

        int posPacmanX = _stategamepacman.getPacmanState(0).getX();
        int posPacmanY = _stategamepacman.getPacmanState(0).getY();

        matrix[2][2] = 1;

        int x, y;

        for(int i = -2 ; i <= 2 ; i++) {
            for(int j = -2 ; j <= 2 ; j++) {
                if((posPacmanX+i < _stategamepacman.getMaze().getSizeX()
                        && posPacmanY+j < _stategamepacman.getMaze().getSizeY())
                        && (posPacmanX+i >= 0 && posPacmanY+j >= 0)) {
                    if(_stategamepacman.getMaze().isFood(posPacmanX+i, posPacmanY+j)) {
                        matrix[2+i][2+j] = 3;
                    } else if(_stategamepacman.isGhost(posPacmanX+i, posPacmanY+j)) {
                        matrix[2+i][2+j] = 2;
                    } else if(_stategamepacman.getMaze().isWall(posPacmanX+i, posPacmanY+j)) {
                        matrix[2+i][2+j] = 4;
                    }
                }
            }
        }




        //System.out.println(_stategamepacman);

        int count = 0;
        int closestDot = 100;
        int newClosestDot;
        StateGamePacman simuGame;
       // System.out.println(closestDot);
        for (Point p : casesAdjacentes) {
            x = 1 + p.x;
            y = 1 + p.y;
            if(matrix[x][y] == 3) {
                directionClosestDot = count;
                break;
            } else if (matrix[x][y] == 0) {
                simuGame = _stategamepacman.nextStatePacman(new ActionPacman(count));
                newClosestDot = simuGame.getClosestDot(simuGame.getPacmanState(0));
                if (closestDot > newClosestDot) {
                    directionClosestDot = count;
                    closestDot = newClosestDot;
                }
                //System.out.println(count + ": " + newClosestDot);
            }
            count++;
        }



        System.out.println("Dot: " + directionClosestDot);
        print();
        System.out.println("_______");




    }

    public void print() {
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix.length; j++) {
                System.out.print(matrix[i][j]);
            }
            System.out.println();
        }
    }

    @Override
    public String toString() {
        String result = "";
        for(int i = 0; i < matrix.length; i++) {
            for(int j = 0; j < matrix.length; j++) {
                result += Integer.toString(matrix[i][j]) + " ";
            }
            result += "\n";
        }
        result += Integer.toString(directionClosestDot);
        return result;
    }


    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.deepHashCode(matrix);
        result = prime * result + this.directionClosestDot;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        EtatPacmanMDPClassic other = (EtatPacmanMDPClassic) obj;
        if (this.directionClosestDot != this.directionClosestDot) return false;
        if (!Arrays.deepEquals(this.matrix, other.matrix)) return false;

        return true;
    }

    public Object clone() {
        EtatPacmanMDPClassic clone = null;
        try {
            // On recupere l'instance a renvoyer par l'appel de la
            // methode super.clone()
            clone = (EtatPacmanMDPClassic) super.clone();
        } catch (CloneNotSupportedException cnse) {
            // Ne devrait jamais arriver car nous implementons
            // l'interface Cloneable
            cnse.printStackTrace(System.err);
        }

        // on renvoie le clone
        return clone;
    }

    public int getDimensions() {
        return 24*4*4;
    }

}
