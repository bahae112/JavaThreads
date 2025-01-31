import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;



interface Livre {
    String afficherDetails();
}


class LivrePapier implements Livre {
    private String titre;
    private String auteur;
    private String datePublication;

    public LivrePapier(String titre, String auteur, String datePublication) {
        this.titre = titre;
        this.auteur = auteur;
        this.datePublication = datePublication;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getDatePublication() {
        return datePublication;
    }

    @Override
    public String afficherDetails() {
        return "Livre Papier - Titre: " + titre + ", Auteur: " + auteur + ", Date de Publication: " + datePublication;
    }

    @Override
    public String toString() {
        return titre + " (Livre Papier)";
    }
}


class LivreNumerique implements Livre {
    private String titre;
    private String auteur;
    private String datePublication;
    private double tailleFichier;

    public LivreNumerique(String titre, String auteur, String datePublication, double tailleFichier) {
        this.titre = titre;
        this.auteur = auteur;
        this.datePublication = datePublication;
        this.tailleFichier = tailleFichier;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getDatePublication() {
        return datePublication;
    }

    public double getTailleFichier() {
        return tailleFichier;
    }

    @Override
    public String afficherDetails() {
        return "livre Numerique  Titre: " + titre + ", Auteur: " + auteur +
                ", Date de publication: " + datePublication + ", Taille du fichier: " + tailleFichier + " Mo";
    }

    @Override
    public String toString() {
        return titre + " (livre numerique)";
    }
}

// classe principal pour l'interface graphique
class GestionBibliotheque extends JFrame {

    private ArrayList<Livre> livres = new ArrayList<>();

    private JTextField titreField = new JTextField(20);
    private JTextField auteurField = new JTextField(20);
    private JTextField dateField = new JTextField(20);
    private JTextField tailleField = new JTextField(20);
    private JComboBox<String> typeBox = new JComboBox<>(new String[]{"Livre Papier", "Livre Numérique"});

    private JTextArea booksDisplayArea = new JTextArea(20, 40);
    private JTextArea messageArea = new JTextArea(5, 40);
    private JTextField selectionField = new JTextField(5);

    public GestionBibliotheque() {
        this.setTitle("Gestion de Bibliothèque");
        this.setSize(800, 600);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setLayout(new BorderLayout());

        booksDisplayArea.setEditable(false);
        JScrollPane bookScrollPane = new JScrollPane(booksDisplayArea);
        bookScrollPane.setPreferredSize(new Dimension(400, 400));
        this.add(bookScrollPane, BorderLayout.CENTER);

        messageArea.setEditable(false);
        JScrollPane messageScrollPane = new JScrollPane(messageArea);
        this.add(messageScrollPane, BorderLayout.EAST);

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setPreferredSize(new Dimension(400, 200));
        formPanel.add(new JLabel("Type:"));
        formPanel.add(typeBox);
        formPanel.add(new JLabel("Titre:"));
        formPanel.add(titreField);
        formPanel.add(new JLabel("Auteur:"));
        formPanel.add(auteurField);
        formPanel.add(new JLabel("Date de Publication:"));
        formPanel.add(dateField);
        formPanel.add(new JLabel("Taille Fichier (Mo):"));
        formPanel.add(tailleField);

        this.add(formPanel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Ajouter");
        JButton detailsButton = new JButton("Afficher Détails");
        JButton saveButton = new JButton("Sauvegarder");
        JButton loadButton = new JButton("Charger");
        buttonPanel.add(addButton);
        buttonPanel.add(new JLabel("Sélection (numéro):"));
        buttonPanel.add(selectionField);
        buttonPanel.add(detailsButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(loadButton);
        this.add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> ajouterLivre());
        detailsButton.addActionListener(e -> afficherDetails());
        saveButton.addActionListener(e -> sauvegarderLivres());
        loadButton.addActionListener(e -> chargerLivres());

        typeBox.addActionListener(e -> {
            boolean isNumerique = typeBox.getSelectedItem().equals("Livre Numérique");
            tailleField.setEnabled(isNumerique);
        });
    }

    private void ajouterLivre() {
        try {
            String titre = titreField.getText();
            String auteur = auteurField.getText();
            String date = dateField.getText();
            String type = (String) typeBox.getSelectedItem();

            if (titre.isEmpty() || auteur.isEmpty() || date.isEmpty()) {
                messageArea.setText("Veuillez remplir tous les champs obligatoires.");
                return;
            }

            Livre livre;
            if (type.equals("Livre Papier")) {
                livre = new LivrePapier(titre, auteur, date);
            } else {
                double taille = Double.parseDouble(tailleField.getText());
                livre = new LivreNumerique(titre, auteur, date, taille);
            }

            livres.add(livre);
            actualiserListeLivres();

            titreField.setText("");
            auteurField.setText("");
            dateField.setText("");
            tailleField.setText("");
            messageArea.setText("Livre ajouté avec succès");
        } catch (NumberFormatException ex) {
            messageArea.setText("Veuillez entrer une taille de fichier valide.");
        }
    }

    private void afficherDetails() {
        try {
            int index = Integer.parseInt(selectionField.getText()) - 1;
            if (index >= 0 && index < livres.size()) {
                Livre livre = livres.get(index);
                messageArea.setText(livre.afficherDetails());
            } else {
                messageArea.setText("Numéro invalide. Veuillez entrer un numéro entre 1 et " + livres.size());
            }
        } catch (NumberFormatException ex) {
            messageArea.setText("Veuillez entrer un numéro valide.");
        }
    }

    private void sauvegarderLivres() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("bibliotheque.txt"))) {
            for (Livre livre : livres) {
                if (livre instanceof LivrePapier) {
                    LivrePapier lp = (LivrePapier) livre;
                    writer.write("Papier;" + lp.getTitre() + ";" + lp.getAuteur() + ";" + lp.getDatePublication());
                } else if (livre instanceof LivreNumerique) {
                    LivreNumerique ln = (LivreNumerique) livre;
                    writer.write("Numérique;" + ln.getTitre() + ";" + ln.getAuteur() + ";" + ln.getDatePublication() + ";" + ln.getTailleFichier());
                }
                writer.newLine();
            }
            messageArea.setText("Livres sauvegardés avec succès.");
        } catch (IOException e) {
            messageArea.setText("Erreur lors de la sauvegarde : " + e.getMessage());
        }
    }

    private void chargerLivres() {
        try (BufferedReader reader = new BufferedReader(new FileReader("bibliotheque.txt"))) {
            String line;
            livres.clear();
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts[0].equals("Papier")) {
                    LivrePapier lp = new LivrePapier(parts[1], parts[2], parts[3]);
                    livres.add(lp);
                } else if (parts[0].equals("Numérique")) {
                    LivreNumerique ln = new LivreNumerique(parts[1], parts[2], parts[3], Double.parseDouble(parts[4]));
                    livres.add(ln);
                }
            }
            actualiserListeLivres();
            messageArea.setText("Livres chargés avec succès.");
        } catch (IOException e) {
            messageArea.setText("Erreur lors du chargement : " + e.getMessage());
        }
    }

    private void actualiserListeLivres() {
        booksDisplayArea.setText("");
        int i = 1;
        for (Livre livre : livres) {
            booksDisplayArea.append((i++) + ". " + livre.toString() + "\n");
        }
    }
}


public class Main {
    public static void main(String[] args) {
            GestionBibliotheque app = new GestionBibliotheque();
            app.setVisible(true);

    }
}
