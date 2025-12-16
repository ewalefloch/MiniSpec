package metaModel;

import metaModel.type.Type;
import metaModel.visiteur.MinispecElement;
import metaModel.visiteur.Visitor;

public class Attribute implements MinispecElement {
    private String name;
    private Type type;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Type getType() { return type; }
    public void setType(Type type) { this.type = type; }

    @Override
    public void accept(Visitor v) {
        v.visitAttribute(this);
    }
}