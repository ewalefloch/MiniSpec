public class Satellite {

    public String nom;
    public Flotte parent;

    public Satellite() { }

    public String getNom() {
        return this.nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public Flotte getParent() {
        return this.parent;
    }

    public void setParent(Flotte parent) {
        this.parent = parent;
    }
}

