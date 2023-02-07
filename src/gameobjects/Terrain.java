package gameobjects;

// Import de packages java
import java.util.ArrayList;
import java.util.Random;

// Import d'autres dossiers
import gui.Vue;

// Gère tous les éléments du terrain
public class Terrain {

    private ArrayList<Plateforme> plateformesListe; // Liste des plateformes sur le terrain
    private Joueur[] ListeJoueurs; // Liste des joueurs
    private final double height, width; // Dimensions du terrain
    private double difficulty = 1.0;
    // La difficulté baisse plus le score monte. Affecte la densite des plateformes.
    // Affecte la proba qu'un item bonus ou malus (sûrement 1/diff) apparaisse.

    public Terrain(Joueur[] ljoueur, double height, double width) {
        // Initialisation des champs
        this.plateformesListe = new ArrayList<Plateforme>();
        this.ListeJoueurs = ljoueur;
        this.height = height;
        this.width = width;

        // Création des plateformes
        generateObstacles(20);
    }

    /// Méthodes de la classe

    // Crée la liste des plateformes (avec un nbPlateformes en entrée)
    private void generateObstacles(int nbPlateformes) {
        // Limites min/max de plateformes sur le terrain
        nbPlateformes = (nbPlateformes > 50) ? 50 : (nbPlateformes < 7) ? 8 : nbPlateformes;

        // Génère des plateformes à coord aléatoires pour la liste des plateformes
        for (int i = 0; i < (nbPlateformes * difficulty); ++i) {
            // On définit la largeur/hauteur des plateformes de base
            int w = 60, h = 20;
            int x = new Random().nextInt((int) this.width - w);
            int y = new Random().nextInt((int) this.height - h);
            plateformesListe.add(new PlateformeBase(x, y, w, h, -10));
        }

        // On s'assure d'aboird toujours une solution au début
        nbPlateformes = (nbPlateformes > 10) ? 10 : nbPlateformes;
        for (int i = 1; i < nbPlateformes; i++) {
            plateformesListe.get(i).setY(this.height - i * 90);
        }
    }

    // Renvoie la plateforme la plus haute sur le terrain
    private Plateforme highestPlateforme() {
        Plateforme plateformeLaPlusHaute = plateformesListe.get(0);
        for (Plateforme p : plateformesListe) {
            if (p.getY() <= plateformeLaPlusHaute.getY()) {
                plateformeLaPlusHaute = p;
            }
        }
        return plateformeLaPlusHaute;
    }

    // Gère, pour le perso, le débordement de l'écran
    private void limite(Personnage p) {
        // 0.43 est la valeur exacte de la moitié du perso
        // Si + de la moitié du perso est sortie du côté gauche de l'écran
        // => on place la moitié du perso au côté droit de l'écran
        if (p.getX() + p.getWidth() * 0.43 <= 0)
            p.setX(this.width - (p.getWidth() * 0.43));
        else if (p.getX() + p.getWidth() * 0.43 >= width) // Et inversement
            p.setX(-(p.getWidth() * 0.43));
    }

    // Mise à jour du jeu.
    public void update() {
        // On effectue une mise à jour pour tous les joueurs
        for (int i = 0; i < ListeJoueurs.length; ++i) {
            Joueur j = ListeJoueurs[i];
            Personnage p = j.getPerso();
            double next = (highestPlateforme().getY() - 85);

            // Ralentissement progressif après un saut
            p.setDy(p.getDy() + 0.2);
            p.setY(p.getY() + p.getDy());

            // Si les piedds du perso touchent le bas de la fenêtre, on a perdu
            if (p.getY() + 0.87 * p.getHeight() >= this.height) {
                Vue.isRunning = false;
            }

            // Si la tête du personnage dépasse la moitié de l'écran
            if (p.getY() < this.height / 2) {
                p.setY(this.height / 2);
                j.setScore(j.getScore() + 1); // On incrémente le score de 1
                // On descend toutes les plateforme
                for (Plateforme pf : plateformesListe) {
                    pf.setY(pf.getY() - (int) p.getDy());
                    if (pf.getY() - pf.getHeight() >= this.height * 0.95) {
                        if (next < 300) {
                            pf.setY(0);
                        } else {
                            pf.setY(next);
                        }
                        int r = new Random().nextInt(530);
                        pf.setX(r);
                    }
                }
            }
            // On gère les collisions & les débordements du personnage
            for (Plateforme pf : plateformesListe) {
                p.collides_plateforme(pf);
            }
            limite(p);
        }
    }

    // Getter & Setter

    public ArrayList<Plateforme> getPlateformesListe() {
        return plateformesListe;
    }

    public void setPlateformesListe(ArrayList<Plateforme> plateformesListe) {
        this.plateformesListe = plateformesListe;
    }

    public double getHeight() {
        return height;
    }

    public double getWidth() {
        return width;
    }

    public double getDifficulty() {
        return difficulty;
    }

    public void setDifficulty(double difficulty) {
        this.difficulty = difficulty;
    }

    public Joueur[] getListeJoueurs() {
        return ListeJoueurs;
    }

    public void setListeJoueurs(Joueur[] listeJoueurs) {
        ListeJoueurs = listeJoueurs;
    }

}