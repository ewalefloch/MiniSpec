package metaModel.type;

import metaModel.visiteur.MinispecElement;
import metaModel.visiteur.Visitor;

public abstract class Type implements MinispecElement {
    protected String name;

    public Type(String name) {
        this.name = name;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public abstract void accept(Visitor visitor);
}